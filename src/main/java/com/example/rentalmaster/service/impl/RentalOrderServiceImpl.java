package com.example.rentalmaster.service.impl;

import com.example.rentalmaster.exception.CommonBackendException;
import com.example.rentalmaster.model.db.entity.Clients;
import com.example.rentalmaster.model.db.entity.Drivers;
import com.example.rentalmaster.model.db.entity.RentalOrder;
import com.example.rentalmaster.model.db.entity.Technique;
import com.example.rentalmaster.model.db.repository.ClientsRepository;
import com.example.rentalmaster.model.db.repository.DriversRepository;
import com.example.rentalmaster.model.db.repository.EmployeesRepository;
import com.example.rentalmaster.model.db.repository.RentalOrderRepository;
import com.example.rentalmaster.model.db.repository.TechniqueRepository;
import com.example.rentalmaster.model.dto.request.RentalOrderRequest;
import com.example.rentalmaster.model.dto.response.RentalOrderResponse;
import com.example.rentalmaster.model.dto.response.RentalShortResponse.ClientShortResponse;
import com.example.rentalmaster.model.dto.response.RentalShortResponse.DriverShortResponse;
import com.example.rentalmaster.model.dto.response.RentalShortResponse.EmployeeShortResponse;
import com.example.rentalmaster.model.dto.response.RentalShortResponse.TechniqueShortResponse;
import com.example.rentalmaster.model.enums.Availability;
import com.example.rentalmaster.model.enums.Status;
import com.example.rentalmaster.service.DriversService;
import com.example.rentalmaster.service.RentalOrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class RentalOrderServiceImpl implements RentalOrderService {

    private final RentalOrderRepository rentalOrderRepository;
    private final ClientsRepository clientsRepository;
    private final TechniqueRepository techniqueRepository;
    private final DriversRepository driversRepository;
    private final EmployeesRepository employeesRepository;
    private final ObjectMapper objectMapper;
    private final EmailService emailService;
    private final DriversService driversService;


    @Override
    public RentalOrderResponse addRentalOrder(RentalOrderRequest rentalOrderRequest) {
        Clients clients = rentalOrderRequest.getClients();

        clientsRepository.findByInnAndStatus(clients.getInn(), Status.Новая)
                .ifPresent(rentalOrders -> {
                    throw new CommonBackendException("Заявка для компании " + clients.getNameOfOrganization()
                            + " (ИНН: " + clients.getInn() + ") уже создано",
                            HttpStatus.CONFLICT
                    );
                });

        clientsRepository.findByInnAndStatus(clients.getInn(), Status.Обработывется)
                .ifPresent(rentalOrders -> {
                    throw new CommonBackendException("Заявка для компании " + clients.getNameOfOrganization()
                            + " (ИНН: " + clients.getInn() + ") уже находится в обработке",
                            HttpStatus.CONFLICT
                    );
                });



        Double calculatedTotalCost = calculateTotalCost(rentalOrderRequest);
        Double calculateRentalCost = calculateRentalCost(rentalOrderRequest);

        RentalOrder rentalOrder = RentalOrder.builder()
                .rentalCost(calculateRentalCost)
                .address(rentalOrderRequest.getAddress())
                .createdAt(rentalOrderRequest.getCreatedAt())
                .branch(rentalOrderRequest.getBranch())
                .endDate(rentalOrderRequest.getEndDate())
                .drivers(driversService.getAll())
                .techniques(rentalOrderRequest.getTechniques())
                .employees(rentalOrderRequest.getEmployees())
                .status(Status.Новая)
                .startDate(rentalOrderRequest.getStartDate())
                .totalCost(calculatedTotalCost)
                .updatedAt(rentalOrderRequest.getUpdatedAt())
                .clients(rentalOrderRequest.getClients())
                .build();

        RentalOrder savedRentalOrder = rentalOrderRepository.save(rentalOrder);

        sendNotificationEmails(savedRentalOrder);

        RentalOrderResponse rentalOrderResponse = new RentalOrderResponse();
        rentalOrderResponse.setMessage("Заявка номер" + savedRentalOrder.getRentalOrderId() + " успешно создана");
        rentalOrderResponse.setRentalOrderId(savedRentalOrder.getRentalOrderId());
        rentalOrderResponse.setRentalDays((int) ChronoUnit.DAYS.between(
                savedRentalOrder.getStartDate().toLocalDate(),
                savedRentalOrder.getEndDate().toLocalDate()
        ));
        rentalOrderResponse.setStatus(savedRentalOrder.getStatus());
        rentalOrderResponse.setCreatedAt(savedRentalOrder.getCreatedAt());
        rentalOrderResponse.setUpdatedAt(savedRentalOrder.getUpdatedAt());
        rentalOrderResponse.setAddress(savedRentalOrder.getAddress());
        rentalOrderResponse.setStartDate(savedRentalOrder.getStartDate());
        rentalOrderResponse.setEndDate(savedRentalOrder.getEndDate());
        rentalOrderResponse.setTotalCost(savedRentalOrder.getTotalCost());


        if (savedRentalOrder.getEmployees() != null) {
            rentalOrderResponse.setEmployees(EmployeeShortResponse.builder()
                    .personalNumber(savedRentalOrder.getEmployees().getPersonalNumber())
                    .build());
        }

        if (savedRentalOrder.getDrivers() != null) {
            rentalOrderResponse.setDrivers((List<DriverShortResponse>) savedRentalOrder.getDrivers().stream()
                    .map(driver -> DriverShortResponse.builder()
                            .personalNumber(driver.getPersonalNumber())
                            .build())
                    .toList());
        }

        if (savedRentalOrder.getTechniques() != null) {
            rentalOrderResponse.setTechniques((List<TechniqueShortResponse>) savedRentalOrder.getTechniques().stream()
                    .map(tech -> TechniqueShortResponse.builder()
                            .stateNumber(tech.getStateNumber())
                            .build())
                    .toList());
        }

        if (savedRentalOrder.getClients() != null) {
            rentalOrderResponse.setClients(ClientShortResponse.builder()
                    .inn(savedRentalOrder.getClients().getInn())
                    .build());
        }

        return rentalOrderResponse;
    }


    private Double calculateTotalCost(RentalOrderRequest request) {
        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new CommonBackendException(
                    "Даты начала и окончания аренды обязательны для расчета стоимости",
                    HttpStatus.BAD_REQUEST
            );
        }

        long days = ChronoUnit.DAYS.between(
                request.getStartDate().toLocalDate(),
                request.getEndDate().toLocalDate()
        );

        if (days < 1) {
            throw new CommonBackendException(
                    "Минимальный срок аренды - 1 полный день",
                    HttpStatus.BAD_REQUEST
            );
        }
        Double calculateRentalCost = calculateRentalCost(request);
        if (calculateRentalCost == null) {
            throw new CommonBackendException(
                    "Стоимость аренды не может быть пустой",
                    HttpStatus.BAD_REQUEST
            );
        }

        return calculateRentalCost * days;
    }

    private Double calculateRentalCost(RentalOrderRequest request) {

        List<String> techniqueIds = request.getTechniques().stream()
                .map(Technique::getStateNumber)
                .toList();
        List<String> driverIds = request.getDrivers().stream()
                .map(Drivers::getPersonalNumber)
                .toList();

        List<Technique> dbTechnique = techniqueRepository.findAllById(techniqueIds);
        List<Drivers> dbDriver = driversRepository.findAllByPersonalNumberIn(driverIds);

        if (dbTechnique == null || dbTechnique.isEmpty()) throw new CommonBackendException("Техника не найдена",
                HttpStatus.NOT_FOUND);

        for (Technique technique : dbTechnique) {
            if ((technique.getAvailability() == Availability.Занято ||
                    technique.getAvailability() == Availability.ТО)) {
                throw new CommonBackendException("Техника с госномером " + technique.getStateNumber() + " недоступно",
                        HttpStatus.CONFLICT);
            }
        }

        if (dbDriver == null || dbDriver.isEmpty()) throw new CommonBackendException("Водитель не найден",
                HttpStatus.NOT_FOUND);

        return calculateDriverCost(dbDriver) + calculateTechCost(dbTechnique);

    }

    private double calculateTechCost(List<Technique> techniques) {
        return techniques.stream().mapToDouble(tech -> {
            if (tech.getBaseCost() == null || tech.getBaseCost() <= 0)
                throw new CommonBackendException("Некорректная стоимость техники: " + tech.getStateNumber(),
                        HttpStatus.BAD_REQUEST);
            return tech.getBaseCost();
        }).sum();
    }

    private double calculateDriverCost(List<Drivers> drivers) {
        return drivers.stream().mapToDouble(driver -> {
            if (driver.getSalary() == null || driver.getSalary() <= 0)
                throw new CommonBackendException("Некорректная зарплата водителя: " + driver.getPersonalNumber(),
                        HttpStatus.BAD_REQUEST);
            return driver.getSalary() * 2;
        }).sum();
    }

    private void sendNotificationEmails(RentalOrder rentalOrder) {
        try {

            if (rentalOrder.getClients() != null && rentalOrder.getClients().getEmail() != null) {
                String clientSubject = "Ваша заявка на аренду создана #" + rentalOrder.getRentalOrderId();
                String clientText = String.format(
                        "Уважаемый клиент!\n\n" +
                                "Ваша заявка на аренду №%s успешно создана.\n" +
                                "Статус: %s\n" +
                                "Дата начала: %s\n" +
                                "Дата окончания: %s\n" +
                                "Общая стоимость: %.2f руб.\n\n" +
                                "Спасибо за выбор нашей компании!",
                        rentalOrder.getRentalOrderId(),
                        rentalOrder.getStatus(),
                        rentalOrder.getStartDate(),
                        rentalOrder.getEndDate(),
                        rentalOrder.getTotalCost()
                );
                emailService.sendEmail(rentalOrder.getClients().getEmail(), clientSubject, clientText);
            }


            if (rentalOrder.getDrivers() != null) {
                String driverSubject = "Новое задание по заявке #" + rentalOrder.getRentalOrderId();
                String driverText = String.format(
                        "Уважаемый водитель!\n\n" +
                                "Вам назначено новое задание по заявке №%s.\n" +
                                "Дата начала: %s\n" +
                                "Дата окончания: %s\n" +
                                "Адрес: %s\n\n" +
                                "Пожалуйста, проверьте детали в системе.",
                        rentalOrder.getRentalOrderId(),
                        rentalOrder.getStartDate(),
                        rentalOrder.getEndDate(),
                        rentalOrder.getAddress()
                );

                rentalOrder.getDrivers().forEach(driver -> {
                    if (driver.getEmail() != null) {
                        emailService.sendEmail(driver.getEmail(), driverSubject, driverText);
                    }
                });
            }
        } catch (Exception e) {
            log.error("Ошибка при отправке уведомлений о заявке: {}", e.getMessage());

        }
    }
}
