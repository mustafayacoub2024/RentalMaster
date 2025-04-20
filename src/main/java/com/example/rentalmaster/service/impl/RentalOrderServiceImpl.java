package com.example.rentalmaster.service.impl;

import com.example.rentalmaster.exception.CommonBackendException;
import com.example.rentalmaster.model.db.entity.Branches;
import com.example.rentalmaster.model.db.entity.Clients;
import com.example.rentalmaster.model.db.entity.Drivers;
import com.example.rentalmaster.model.db.entity.Employees;
import com.example.rentalmaster.model.db.entity.RentalOrder;
import com.example.rentalmaster.model.db.entity.Technique;
import com.example.rentalmaster.model.db.repository.ClientsRepository;
import com.example.rentalmaster.model.db.repository.DriversRepository;
import com.example.rentalmaster.model.db.repository.EmployeesRepository;
import com.example.rentalmaster.model.db.repository.RentalOrderRepository;
import com.example.rentalmaster.model.db.repository.TechniqueRepository;
import com.example.rentalmaster.model.dto.request.RentalOrderRequest;
import com.example.rentalmaster.model.dto.request.RentalOrderUpdateRequest;
import com.example.rentalmaster.model.dto.response.ClientsResponse;
import com.example.rentalmaster.model.dto.response.RentalOrderGetAllResponse;
import com.example.rentalmaster.model.dto.response.RentalOrderResponse;
import com.example.rentalmaster.model.dto.response.RentalShortResponse.ClientShortResponse;
import com.example.rentalmaster.model.dto.response.RentalShortResponse.DriverShortResponse;
import com.example.rentalmaster.model.dto.response.RentalShortResponse.EmployeeShortResponse;
import com.example.rentalmaster.model.dto.response.RentalShortResponse.TechniqueShortResponse;
import com.example.rentalmaster.model.enums.Availability;
import com.example.rentalmaster.model.enums.Status;
import com.example.rentalmaster.service.BranchesService;
import com.example.rentalmaster.service.ClientsService;
import com.example.rentalmaster.service.DriversService;
import com.example.rentalmaster.service.EmployeesService;
import com.example.rentalmaster.service.RentalOrderService;
import com.example.rentalmaster.service.TechniqueService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;


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
    private final BranchesService branchesService;
    private final EmployeesService employeesService;
    private final TechniqueService techniqueService;
    private final ClientsService clientsService;


    @Override
    public RentalOrderResponse addRentalOrder(RentalOrderRequest rentalOrderRequest) {

        checkTechniqueAvailability(rentalOrderRequest);

        List<String> driverIds = rentalOrderRequest.getDrivers().stream()
                .map(Drivers::getPersonalNumber)
                .toList();
        List<Drivers> drivers = driversRepository.findAllByPersonalNumberIn(driverIds);

        List<String> techniqueIds = rentalOrderRequest.getTechniques().stream()
                .map(Technique::getStateNumber)
                .toList();
        List<Technique> techniques = techniqueRepository.findAllById(techniqueIds);

        techniques.forEach(tech -> {
            if (tech.getAvailability() != Availability.AVAILABLE) {
                throw new CommonBackendException(
                        "Техника " + tech.getStateNumber() + " недоступна",
                        HttpStatus.CONFLICT);
            }
        });

        String personalNumber = rentalOrderRequest.getEmployees().getPersonalNumber();
        Employees employee = employeesService.getEmployee(personalNumber);

        String inn = rentalOrderRequest.getClients().getInn();
        Clients client = clientsService.getClient(inn);

        String branchName = rentalOrderRequest.getBranch().getBranchName();
        Branches branch = branchesService.getBranchByBranchName(branchName);

        techniques.forEach(tech -> {
            if (tech.getAvailability() != Availability.AVAILABLE) {
                throw new CommonBackendException(
                        "Техника " + tech.getStateNumber() + " недоступна",
                        HttpStatus.CONFLICT);
            }
        });

        Double calculatedTotalCost = calculateTotalCost(rentalOrderRequest);
        Double calculateRentalCost = calculateRentalCost(rentalOrderRequest);

        RentalOrder rentalOrder = RentalOrder.builder()
                .rentalCost(calculateRentalCost)
                .address(rentalOrderRequest.getAddress())
                .createdAt(rentalOrderRequest.getCreatedAt())
                .branch(branch)
                .endDate(rentalOrderRequest.getEndDate())
                .drivers(drivers)
                .techniques(techniques)
                .employees(employee)
                .status(Status.NEW)
                .startDate(rentalOrderRequest.getStartDate())
                .totalCost(calculatedTotalCost)
                .updatedAt(rentalOrderRequest.getUpdatedAt())
                .clients(client)
                .build();

        RentalOrder order = objectMapper.convertValue(rentalOrder, RentalOrder.class);
        RentalOrder savedRentalOrder = rentalOrderRepository.save(order);

//        sendNotificationEmails(savedRentalOrder); временно отключил рассылку

        RentalOrderResponse rentalOrderResponse = new RentalOrderResponse();
        rentalOrderResponse.setMessage("Заявка номер " + savedRentalOrder.getRentalOrderId() + " успешно создана");
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


        if (savedRentalOrder.getDrivers() != null) {
            rentalOrderResponse.setDrivers(savedRentalOrder.getDrivers().stream()
                    .map(d -> DriverShortResponse.builder()
                            .personalNumber(d.getPersonalNumber())
                            .build())
                    .collect(Collectors.toList()));
        }

        if (savedRentalOrder.getTechniques() != null) {
            rentalOrderResponse.setTechniques(savedRentalOrder.getTechniques().stream()
                    .map(t -> TechniqueShortResponse.builder()
                            .stateNumber(t.getStateNumber())
                            .build())
                    .collect(Collectors.toList()));
        }

        if (savedRentalOrder.getClients() != null) {
            rentalOrderResponse.setClients(ClientShortResponse.builder()
                    .inn(savedRentalOrder.getClients().getInn())
                    .build());
        }

        if (savedRentalOrder.getEmployees() != null) {
            rentalOrderResponse.setEmployees(EmployeeShortResponse.builder()
                    .personalNumber(savedRentalOrder.getEmployees().getPersonalNumber())
                    .build());
        }
        return rentalOrderResponse;
    }

    @Override
    public RentalOrderResponse updateStatusByInProgress(String rentalOrderId) {
        RentalOrder rentalOrder = rentalOrderRepository.findByRentalOrderId(rentalOrderId)
                .orElseThrow(() -> new CommonBackendException(
                        "Заявка " + rentalOrderId + " не найдена",
                        HttpStatus.NOT_FOUND
                ));

        if (rentalOrder.getStatus() == Status.IN_PROGRESS) {
            throw new CommonBackendException(
                    "Заявка уже находится в статусе 'В работе'",
                    HttpStatus.BAD_REQUEST
            );
        }

        rentalOrder.getTechniques().forEach(technique -> {
            technique.setAvailability(Availability.BUSY);
            techniqueRepository.save(technique);
        });

        if (rentalOrder.getStatus() == Status.NEW) {
            rentalOrder.setStatus(Status.IN_PROGRESS);
            rentalOrderRepository.save(rentalOrder);
        }

        RentalOrderResponse response = objectMapper.convertValue(rentalOrder, RentalOrderResponse.class);
        response.setRentalDays((int) ChronoUnit.DAYS.between(
                rentalOrder.getStartDate().toLocalDate(),
                rentalOrder.getEndDate().toLocalDate()
        ));
        response.setMessage("Статус заявки " + rentalOrderId + " успешно изменён на статус 'в работе'");
        return response;
    }


    @Override
    public RentalOrderResponse updateStatusByCombleted(String rentalOrderId) {
        RentalOrder rentalOrder = rentalOrderRepository.findByRentalOrderId(rentalOrderId)
                .orElseThrow(() -> new CommonBackendException(
                        "Заявка " + rentalOrderId + " не найдена",
                        HttpStatus.NOT_FOUND
                ));

        if (rentalOrder.getStatus() == Status.COMPLETED) {
            throw new CommonBackendException(
                    "Заявка уже находится в статусе 'Завершено'",
                    HttpStatus.BAD_REQUEST
            );
        }

        rentalOrder.getTechniques().forEach(technique -> {
            technique.setAvailability(Availability.AVAILABLE);
            techniqueRepository.save(technique);
        });

        if (rentalOrder.getStatus() == Status.IN_PROGRESS) {
            rentalOrder.setStatus(Status.COMPLETED);
            rentalOrderRepository.save(rentalOrder);
        }

        rentalOrder.setActualEndDate(LocalDateTime.now());
        List<Technique> techniques = techniqueRepository.findAllById(
                rentalOrder.getTechniques().stream()
                        .map(Technique::getStateNumber)
                        .toList()
        );

        List<Drivers> drivers = driversRepository.findAllByPersonalNumberIn(
                rentalOrder.getDrivers().stream()
                        .map(Drivers::getPersonalNumber)
                        .toList()
        );


        double actualDailyCost = techniques.stream().mapToDouble(Technique::getBaseCost).sum()
                + drivers.stream().mapToDouble(d -> d.getSalary() * 2).sum();

        int actualDays = (int) ChronoUnit.DAYS.between(
                rentalOrder.getStartDate().toLocalDate(),
                rentalOrder.getActualEndDate().toLocalDate()
        );

        Double newTotalCost = actualDailyCost * actualDays;
        rentalOrder.setTotalCost(newTotalCost);
        rentalOrder.setStatus(Status.COMPLETED);
        rentalOrder.setActualEndDate(LocalDateTime.now());

        RentalOrder updatedOrder = rentalOrderRepository.save(rentalOrder);

        RentalOrderResponse response = objectMapper.convertValue(updatedOrder, RentalOrderResponse.class);
        response.setRentalDays(actualDays);
        response.setMessage("Статус заявки " + rentalOrderId + " успешно изменён на статус 'Завершено'");
        return response;
    }

    @Override
    public RentalOrderResponse updateRentalOrder(String rentalOrderId, RentalOrderUpdateRequest rentalOrderRequest) {
        RentalOrder rentalOrder = rentalOrderRepository.findByRentalOrderId(rentalOrderId).orElseThrow(() ->
                new CommonBackendException("Заявка с номером " + rentalOrderId + " не найдено", HttpStatus.NOT_FOUND));

        List<String> driverIds = rentalOrderRequest.getDrivers().stream()
                .map(Drivers::getPersonalNumber)
                .toList();
        List<Drivers> drivers = driversRepository.findAllByPersonalNumberIn(driverIds);

        List<String> techniqueIds = rentalOrderRequest.getTechniques().stream()
                .map(Technique::getStateNumber)
                .toList();
        List<Technique> techniques = techniqueRepository.findAllById(techniqueIds);

        techniques.forEach(tech -> {
            if (tech.getAvailability() != Availability.AVAILABLE) {
                throw new CommonBackendException(
                        "Техника " + tech.getStateNumber() + " недоступна",
                        HttpStatus.CONFLICT);
            }
        });

        String personalNumber = rentalOrderRequest.getEmployees().getPersonalNumber();
        Employees employee = employeesService.getEmployee(personalNumber);

        rentalOrder.setAddress(rentalOrderRequest.getAddress());
        rentalOrder.setStartDate(rentalOrderRequest.getStartDate());
        rentalOrder.setEndDate(rentalOrderRequest.getEndDate());
        rentalOrder.setEmployees(employee);
        rentalOrder.setDrivers(drivers);
        rentalOrder.setTechniques(techniques);
        rentalOrder.setTotalCost(rentalOrderRequest.getTotalCost());

        double dailyCost = calculateTechCost(techniques) + calculateDriverCost(drivers);
        long days = ChronoUnit.DAYS.between(
                rentalOrder.getStartDate().toLocalDate(),
                rentalOrder.getEndDate().toLocalDate()
        );
        rentalOrder.setTotalCost(dailyCost * days);

        int actualDays = (int) ChronoUnit.DAYS.between(
                rentalOrder.getStartDate().toLocalDate(),
                rentalOrder.getEndDate().toLocalDate()
        );

        RentalOrder updatedOrder = rentalOrderRepository.save(rentalOrder);

        RentalOrderResponse response = objectMapper.convertValue(updatedOrder, RentalOrderResponse.class);
        response.setRentalDays(actualDays);
        response.setMessage("Данные заявки " + rentalOrderId + " успешно изменены");
        return response;
    }

    @Override
    public List<RentalOrderGetAllResponse> getAllRentalOrders() {
        List<RentalOrder> rentalOrders = rentalOrderRepository.findAll();

        if (rentalOrders.isEmpty()) {
            throw new CommonBackendException("Списко заявок пуст", HttpStatus.NOT_FOUND);
        }

        return rentalOrders.stream()
                .map(rentalOrder -> {
                    RentalOrderGetAllResponse response = objectMapper.convertValue(rentalOrder, RentalOrderGetAllResponse.class);
                    response.setMessage("Заявка номер " + rentalOrder.getRentalOrderId() + " общая сумма "
                            + rentalOrder.getTotalCost() + " рублей");
                    return response;
                })
                .toList();
    }

    @Override
    public RentalOrderResponse getInfoToOrderById(String rentalOrderId) {
        RentalOrder rentalOrder = rentalOrderRepository.findByRentalOrderId(rentalOrderId)
                .orElseThrow(() -> new CommonBackendException("Заявка с номером " + rentalOrderId + " не найдена"
                        , HttpStatus.NOT_FOUND));

        EmployeeShortResponse employeeShort = EmployeeShortResponse.builder()
                .personalNumber(rentalOrder.getEmployees().getPersonalNumber())
                .build();

        ClientShortResponse clientShort = ClientShortResponse.builder()
                .inn(rentalOrder.getClients().getInn())
                .build();

        List<DriverShortResponse> driverShorts = rentalOrder.getDrivers().stream()
                .map(d -> DriverShortResponse.builder()
                        .personalNumber(d.getPersonalNumber())
                        .build())
                .collect(Collectors.toList());

        List<TechniqueShortResponse> techniqueShorts = rentalOrder.getTechniques().stream()
                .map(t -> TechniqueShortResponse.builder()
                        .stateNumber(t.getStateNumber())
                        .build())
                .collect(Collectors.toList());

        return RentalOrderResponse.builder()
                .message("Заявка с номером " + rentalOrderId)
                .rentalOrderId(rentalOrder.getRentalOrderId())
                .rentalDays((int) ChronoUnit.DAYS.between(
                        rentalOrder.getStartDate().toLocalDate(),
                        rentalOrder.getEndDate().toLocalDate()))
                .status(rentalOrder.getStatus())
                .createdAt(rentalOrder.getCreatedAt())
                .updatedAt(rentalOrder.getUpdatedAt())
                .address(rentalOrder.getAddress())
                .startDate(rentalOrder.getStartDate())
                .endDate(rentalOrder.getEndDate())
                .totalCost(rentalOrder.getTotalCost())
                .employees(employeeShort)
                .clients(clientShort)
                .drivers(driverShorts)
                .techniques(techniqueShorts)
                .build();

    }

    @Override
    public RentalOrderResponse updateStatusByRejected(String rentalOrderId) {
        RentalOrder rentalOrder = rentalOrderRepository.findByRentalOrderId(rentalOrderId)
                .orElseThrow(() -> new CommonBackendException(
                        "Заявка " + rentalOrderId + " не найдена",
                        HttpStatus.NOT_FOUND
                ));

        if (rentalOrder.getStatus() == Status.REJECTED) {
            throw new CommonBackendException(
                    "Заявка уже находится в статусе 'Завершено'",
                    HttpStatus.BAD_REQUEST
            );
        }

        rentalOrder.getTechniques().forEach(technique -> {
            technique.setAvailability(Availability.AVAILABLE);
            techniqueRepository.save(technique);
        });

        rentalOrder.setActualEndDate(LocalDateTime.now());
        List<Technique> techniques = techniqueRepository.findAllById(
                rentalOrder.getTechniques().stream()
                        .map(Technique::getStateNumber)
                        .toList()
        );

        List<Drivers> drivers = driversRepository.findAllByPersonalNumberIn(
                rentalOrder.getDrivers().stream()
                        .map(Drivers::getPersonalNumber)
                        .toList()
        );


        double actualDailyCost = techniques.stream().mapToDouble(Technique::getBaseCost).sum()
                + drivers.stream().mapToDouble(d -> d.getSalary() * 2).sum();

        int actualDays = (int) ChronoUnit.DAYS.between(
                rentalOrder.getStartDate().toLocalDate(),
                rentalOrder.getActualEndDate().toLocalDate()
        );

        Double newTotalCost = actualDailyCost * actualDays;
        rentalOrder.setTotalCost(newTotalCost);
        rentalOrder.setStatus(Status.REJECTED);
        rentalOrder.setActualEndDate(LocalDateTime.now());

        RentalOrder updatedOrder = rentalOrderRepository.save(rentalOrder);

        RentalOrderResponse response = objectMapper.convertValue(updatedOrder, RentalOrderResponse.class);
        response.setRentalDays(actualDays);
        response.setMessage("Статус заявки " + rentalOrderId + " успешно изменён на статус 'Завершено'");
        return response;
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
        checkTechniqueAvailability(request);
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
            if ((technique.getAvailability() == Availability.BUSY ||
                    technique.getAvailability() == Availability.MAINTENANCE)) {
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

    private void checkTechniqueAvailability(RentalOrderRequest request) {
        List<String> techniqueNumbers = request.getTechniques().stream()
                .map(Technique::getStateNumber)
                .toList();

        List<Technique> techniques = techniqueRepository.findAllById(techniqueNumbers);

        techniques.forEach(tech -> {
            boolean isBusy = rentalOrderRepository.existsByTechniquesAndDateRange(
                    tech.getStateNumber(),
                    request.getStartDate(),
                    request.getEndDate()
            );

            if (isBusy) {
                throw new CommonBackendException(
                        "Техника " + tech.getStateNumber() +
                                " занята в указанный период",
                        HttpStatus.CONFLICT
                );
            }
        });

    }
}
