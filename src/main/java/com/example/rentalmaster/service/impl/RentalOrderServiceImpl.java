package com.example.rentalmaster.service.impl;

import com.example.rentalmaster.exception.CommonBackendException;
import com.example.rentalmaster.model.db.entity.Branches;
import com.example.rentalmaster.model.db.entity.Clients;
import com.example.rentalmaster.model.db.entity.Drivers;
import com.example.rentalmaster.model.db.entity.Employees;
import com.example.rentalmaster.model.db.entity.RentalOrder;
import com.example.rentalmaster.model.db.entity.Technique;
import com.example.rentalmaster.model.db.repository.BranchesRepository;
import com.example.rentalmaster.model.db.repository.ClientsRepository;
import com.example.rentalmaster.model.db.repository.DriversRepository;
import com.example.rentalmaster.model.db.repository.EmployeesRepository;
import com.example.rentalmaster.model.db.repository.RentalOrderRepository;
import com.example.rentalmaster.model.db.repository.TechniqueRepository;
import com.example.rentalmaster.model.dto.request.RentalOrderRequest;
import com.example.rentalmaster.model.dto.request.RentalOrderUpdateRequest;
import com.example.rentalmaster.model.dto.response.CostComponents;
import com.example.rentalmaster.model.dto.response.RentalOrderEntities;
import com.example.rentalmaster.model.dto.response.RentalOrderGetAllResponse;
import com.example.rentalmaster.model.dto.response.RentalOrderResponse;
import com.example.rentalmaster.model.dto.response.RentalShortResponse.ClientShortResponse;
import com.example.rentalmaster.model.dto.response.RentalShortResponse.DriverShortResponse;
import com.example.rentalmaster.model.dto.response.RentalShortResponse.EmployeeShortResponse;
import com.example.rentalmaster.model.dto.response.RentalShortResponse.TechniqueShortResponse;
import com.example.rentalmaster.model.enums.Availability;
import com.example.rentalmaster.model.enums.Status;
import com.example.rentalmaster.service.EmployeesService;
import com.example.rentalmaster.service.RentalOrderService;
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
    private final EmployeesService employeesService;
    private final BranchesRepository branchesRepository;

    @Override
    public RentalOrderResponse addRentalOrder(RentalOrderRequest rentalOrderRequest) {
        log.info("Создание новой заявки");
        checkTechniqueAvailability(rentalOrderRequest);
        RentalOrderEntities entities = retrieveEntities(rentalOrderRequest);
        RentalOrder newOrder = buildRentalOrder(rentalOrderRequest, entities);
        RentalOrder savedOrder = saveAndNotify(newOrder);
        //        sendNotificationEmails(savedRentalOrder); временно отключил рассылку
        log.info("Заявка с номером:{} успешно создана", savedOrder.getRentalOrderId());
        return mapToResponse(savedOrder);
    }

    @Override
    public RentalOrderResponse updateStatusByInProgress(String rentalOrderId) {
        log.info("Обновление статуса заявке номер:{} на статус:{}", rentalOrderId, Status.IN_PROGRESS);
        RentalOrder rentalOrder = validateRentalOrderNoFound(rentalOrderId);
        log.info("Заявка с номер:{} найдена", rentalOrderId);
        validRentalOrderStatus(rentalOrder.getStatus());
        log.info("Изменение статуса технике");
        updateTechniqueAvailability(rentalOrder.getTechniques());
        log.info("Статус заявки:{}", rentalOrder.getRentalOrderId());
        updateOrderStatusIfNew(rentalOrder);

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
        log.info("Инициировано завершение заявки {}", rentalOrderId);
        RentalOrder order = validateForCompletion(rentalOrderId);
        updateAssociatedEntities(order);
        calculateAndApplyFinalCost(order);
        log.info("Заявка:{} успешно завершена", rentalOrderId);
        return prepareCompletionResponse(order);
    }

    @Override
    public RentalOrderResponse updateRentalOrder(String rentalOrderId, RentalOrderUpdateRequest rentalOrderRequest) {
       log.info("Обнавление данных заявки:{}", rentalOrderId);
        RentalOrder order = validateRentalOrderNoFound(rentalOrderId);
        List<Drivers> drivers = fetchRequestDrivers(rentalOrderRequest);
        List<Technique> techniques = fetchAndValidateTechniques(rentalOrderRequest);

        updateRentalOrderDetails(order, rentalOrderRequest, drivers, techniques);
        RentalOrder updatedOrder = rentalOrderRepository.save(order);

        return prepareUpdateResponse(updatedOrder, rentalOrderId);
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
        RentalOrder rentalOrder = validateRentalOrderNoFound(rentalOrderId);
        RentalOrderResponse response = buildRentalOrderResponse(rentalOrder);
        enrichResponseWithAssociatedEntities(response, rentalOrder);
        return response;
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

    private RentalOrderEntities retrieveEntities(RentalOrderRequest request) {
        List<Drivers> drivers = driversRepository.findAllByPersonalNumberIn(
                request.getDrivers().stream()
                        .map(Drivers::getPersonalNumber)
                        .toList()
        );

        List<Technique> techniques = techniqueRepository.findAllById(
                request.getTechniques().stream()
                        .map(Technique::getStateNumber)
                        .toList()
        );
        Employees employee = employeesRepository.findByPersonalNumber(
                        request.getEmployees().getPersonalNumber())
                .orElseThrow(() -> new CommonBackendException(
                        "Сотрудник не найден", HttpStatus.NOT_FOUND));

        Clients client = clientsRepository.findByInn(
                        request.getClients().getInn())
                .orElseThrow(() -> new CommonBackendException(
                        "Клиент не найден", HttpStatus.NOT_FOUND));

        Branches branch = branchesRepository.findByBranchName(
                        request.getBranch().getBranchName())
                .orElseThrow(() -> new CommonBackendException(
                        "Филиал не найден", HttpStatus.NOT_FOUND));

        return new RentalOrderEntities(drivers, techniques, employee, client, branch);
    }

    private RentalOrder buildRentalOrder(RentalOrderRequest request, RentalOrderEntities entities) {
        Double totalCost = calculateTotalCost(request);
        Double rentalCost = calculateRentalCost(request);

        return RentalOrder.builder()
                .rentalCost(rentalCost)
                .address(request.getAddress())
                .createdAt(request.getCreatedAt())
                .branch(entities.branch())
                .endDate(request.getEndDate())
                .drivers(entities.drivers())
                .techniques(entities.techniques())
                .employees(entities.employee())
                .status(Status.NEW)
                .startDate(request.getStartDate())
                .totalCost(totalCost)
                .updatedAt(request.getUpdatedAt())
                .clients(entities.client())
                .build();
    }

    private RentalOrder saveAndNotify(RentalOrder order) {
        RentalOrder entity = objectMapper.convertValue(order, RentalOrder.class);
        RentalOrder saved = rentalOrderRepository.save(entity);
//        sendNotificationEmails(savedRentalOrder); временно отключил рассылку
        log.info("Заявка {} успешно сохранена", saved.getRentalOrderId());
        return saved;
    }

    private RentalOrderResponse mapToResponse(RentalOrder order) {
        RentalOrderResponse response = new RentalOrderResponse();
        response.setMessage("Заявка номер " + order.getRentalOrderId() + " успешно создана");
        response.setRentalOrderId(order.getRentalOrderId());
        response.setRentalDays(calculateRentalDays(order));
        response.setStatus(order.getStatus());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        response.setAddress(order.getAddress());
        response.setStartDate(order.getStartDate());
        response.setEndDate(order.getEndDate());
        response.setTotalCost(order.getTotalCost());

        mapAssociatedEntities(response, order);
        return response;
    }

    private void mapAssociatedEntities(RentalOrderResponse response, RentalOrder order) {
        if (order.getDrivers() != null) {
            response.setDrivers(mapDrivers(order.getDrivers()));
        }

        if (order.getTechniques() != null) {
            response.setTechniques(mapTechniques(order.getTechniques()));
        }

        if (order.getClients() != null) {
            response.setClients(mapClient(order.getClients()));
        }

        if (order.getEmployees() != null) {
            response.setEmployees(mapEmployee(order.getEmployees()));
        }
    }

    private List<DriverShortResponse> mapDrivers(List<Drivers> drivers) {
        return drivers.stream()
                .map(d -> DriverShortResponse.builder()
                        .personalNumber(d.getPersonalNumber())
                        .build())
                .collect(Collectors.toList());
    }

    private List<TechniqueShortResponse> mapTechniques(List<Technique> techniques) {
        return techniques.stream()
                .map(d -> TechniqueShortResponse.builder()
                        .stateNumber(d.getStateNumber())
                        .build())
                .collect(Collectors.toList());
    }

    private EmployeeShortResponse mapEmployee(Employees employee) {
        return EmployeeShortResponse.builder()
                .personalNumber(employee.getPersonalNumber())
                .build();
    }

    private ClientShortResponse mapClient(Clients client) {
        return ClientShortResponse.builder()
                .inn(client.getInn())
                .build();
    }

    private int calculateRentalDays(RentalOrder order) {
        if (order.getStartDate() == null || order.getEndDate() == null) {
            throw new CommonBackendException(
                    "Даты аренды не могут быть пустыми",
                    HttpStatus.BAD_REQUEST
            );
        }

        return (int) ChronoUnit.DAYS.between(
                order.getStartDate().toLocalDate(),
                order.getEndDate().toLocalDate()
        );
    }

    private RentalOrder validateRentalOrderNoFound(String rentalOrderId) {
        return rentalOrderRepository.findByRentalOrderId(rentalOrderId)
                .orElseThrow(() -> new CommonBackendException(
                        "Заявка " + rentalOrderId + " не найдена",
                        HttpStatus.NOT_FOUND
                ));
    }

    private void validRentalOrderStatus(Status status) {
        if (status == Status.IN_PROGRESS) {
            throw new CommonBackendException(
                    "Заявка уже находится в статусе 'В работе'",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private void updateTechniqueAvailability(List<Technique> techniques) {
        techniques.forEach(tech -> {
            log.debug("Обновление техники {}: {} → {}",
                    tech.getStateNumber(),
                    tech.getAvailability(),
                    Availability.BUSY);

            tech.setAvailability(Availability.BUSY);
            techniqueRepository.save(tech);
        });
    }

    private void updateOrderStatusIfNew(RentalOrder order) {
        if (order.getStatus() == Status.NEW) {
            log.info("Смена статуса заявки {}: {} → {}",
                    order.getRentalOrderId(),
                    order.getStatus(),
                    Status.IN_PROGRESS);

            order.setStatus(Status.IN_PROGRESS);
            rentalOrderRepository.save(order);
        }
    }

    private void validStatusIfCompleted(Status status) {
        if (status == Status.COMPLETED) {
            throw new CommonBackendException(
                    "Заявка уже находится в статусе 'Завершено'",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private RentalOrder validateForCompletion(String rentalOrderId) {
        RentalOrder order = validateRentalOrderNoFound(rentalOrderId);
        log.debug("Получена заявка {} (статус: {})", rentalOrderId, order.getStatus());

        validStatusIfCompleted(order.getStatus());
        return order;
    }

    private void updateAssociatedEntities(RentalOrder order) {
        updateTechniqueStatus(order.getTechniques());
        updateOrderStatus(order);
        setCompletionTimestamp(order);
    }

    private void updateTechniqueStatus(List<Technique> techniques) {
        techniques.forEach(tech -> {
            tech.setAvailability(Availability.AVAILABLE);
            techniqueRepository.save(tech);
            log.info("Техника {} возвращена в доступ", tech.getStateNumber());
        });
    }

    private void updateOrderStatus(RentalOrder order) {
        if (order.getStatus() == Status.IN_PROGRESS) {
            order.setStatus(Status.COMPLETED);
            rentalOrderRepository.save(order);
            log.info("Статус заявки {} обновлен на COMPLETED", order.getRentalOrderId());
        }
    }

    private void setCompletionTimestamp(RentalOrder order) {
        order.setActualEndDate(LocalDateTime.now());
        log.debug("Установлена фактическая дата завершения: {}", order.getActualEndDate());
    }

    private void calculateAndApplyFinalCost(RentalOrder order) {
        CostComponents components = calculateCostComponents(order);
        applyFinalPricing(order, components);
    }

    private CostComponents calculateCostComponents(RentalOrder order) {
        List<Technique> techniques = fetchTechniques(order);
        List<Drivers> drivers = fetchDrivers(order);

        double dailyTechCost = techniques.stream()
                .mapToDouble(Technique::getBaseCost)
                .sum();

        double dailyDriverCost = drivers.stream()
                .mapToDouble(d -> d.getSalary() * 2)
                .sum();

        int rentalDays = calculateRentalDuration(order);

        return new CostComponents(dailyTechCost, dailyDriverCost, rentalDays);
    }

    private void applyFinalPricing(RentalOrder order, CostComponents components) {
        double totalCost = (components.dailyTechCost() + components.dailyDriverCost()) * components.days();
        order.setTotalCost(totalCost);
        log.info("Рассчитана итоговая стоимость: {}", totalCost);
    }

    private RentalOrderResponse prepareCompletionResponse(RentalOrder order) {
        RentalOrderResponse response = objectMapper.convertValue(order, RentalOrderResponse.class);
        enrichResponseData(response, order);
        return response;
    }

    private void enrichResponseData(RentalOrderResponse response, RentalOrder order) {
        response.setRentalDays(calculateRentalDuration(order));
        response.setMessage(generateCompletionMessage(order.getRentalOrderId()));
    }

    private String generateCompletionMessage(String orderId) {
        return String.format("Заявка %s успешно завершена. Доступен финальный отчет.", orderId);
    }

    private int calculateRentalDuration(RentalOrder order) {
        return (int) ChronoUnit.DAYS.between(
                order.getStartDate().toLocalDate(),
                order.getActualEndDate().toLocalDate()
        );
    }

    private List<Technique> fetchTechniques(RentalOrder order) {
        return techniqueRepository.findAllById(
                order.getTechniques().stream()
                        .map(Technique::getStateNumber)
                        .toList()
        );
    }

    private List<Drivers> fetchDrivers(RentalOrder order) {
        return driversRepository.findAllByPersonalNumberIn(
                order.getDrivers().stream()
                        .map(Drivers::getPersonalNumber)
                        .toList()
        );
    }

    private List<Drivers> fetchRequestDrivers(RentalOrderUpdateRequest request) {
        List<String> driverIds = request.getDrivers().stream()
                .map(Drivers::getPersonalNumber)
                .toList();
        return driversRepository.findAllByPersonalNumberIn(driverIds);
    }

    private List<Technique> fetchAndValidateTechniques(RentalOrderUpdateRequest request) {
        List<String> techniqueIds = request.getTechniques().stream()
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
        return techniques;
    }

    private void updateRentalOrderDetails(RentalOrder order,
                                          RentalOrderUpdateRequest request,
                                          List<Drivers> drivers,
                                          List<Technique> techniques) {

        order.setAddress(request.getAddress());
        order.setStartDate(request.getStartDate());
        order.setEndDate(request.getEndDate());
        order.setEmployees(employeesService.getEmployee(
                request.getEmployees().getPersonalNumber()));
        order.setDrivers(drivers);
        order.setTechniques(techniques);
        recalculateTotalCost(order);
    }

    private void recalculateTotalCost(RentalOrder order) {
        double techCost = calculateTechCost(order.getTechniques());
        double driverCost = calculateDriverCost(order.getDrivers());
        int days = calculateRentalDays(order);
        order.setTotalCost((techCost + driverCost) * days);
    }

    private RentalOrderResponse prepareUpdateResponse(RentalOrder order, String rentalOrderId) {
        RentalOrderResponse response = objectMapper.convertValue(order, RentalOrderResponse.class);
        response.setRentalDays(calculateRentalDays(order));
        response.setMessage(generateUpdateMessage(rentalOrderId));
        return response;
    }

    private String generateUpdateMessage(String orderId) {
        return "Данные заявки " + orderId + " успешно изменены";
    }

    private RentalOrderResponse buildRentalOrderResponse(RentalOrder rentalOrder) {
        return RentalOrderResponse.builder()
                .message("Заявка с номером " + rentalOrder.getRentalOrderId())
                .rentalOrderId(rentalOrder.getRentalOrderId())
                .rentalDays(calculateRentalDays(rentalOrder)) // Используем существующий метод
                .status(rentalOrder.getStatus())
                .createdAt(rentalOrder.getCreatedAt())
                .updatedAt(rentalOrder.getUpdatedAt())
                .address(rentalOrder.getAddress())
                .startDate(rentalOrder.getStartDate())
                .endDate(rentalOrder.getEndDate())
                .totalCost(rentalOrder.getTotalCost())
                .build();
    }

    private void enrichResponseWithAssociatedEntities(RentalOrderResponse response, RentalOrder rentalOrder) {
        response.setEmployees(mapEmployee(rentalOrder.getEmployees()));
        response.setClients(mapClient(rentalOrder.getClients()));
        response.setDrivers(mapDriverShortResponses(rentalOrder.getDrivers()));
        response.setTechniques(mapTechniqueShortResponses(rentalOrder.getTechniques()));
    }

    private List<DriverShortResponse> mapDriverShortResponses(List<Drivers> drivers) {
        return drivers.stream()
                .map(this::mapDriverToShortResponse)
                .collect(Collectors.toList());
    }

    private DriverShortResponse mapDriverToShortResponse(Drivers driver) {
        return DriverShortResponse.builder()
                .personalNumber(driver.getPersonalNumber())
                .build();
    }

    private List<TechniqueShortResponse> mapTechniqueShortResponses(List<Technique> techniques) {
        return techniques.stream()
                .map(this::mapTechniqueToShortResponse)
                .collect(Collectors.toList());
    }


    private TechniqueShortResponse mapTechniqueToShortResponse(Technique technique) {
        return TechniqueShortResponse.builder()
                .stateNumber(technique.getStateNumber())
                .build();
    }
}