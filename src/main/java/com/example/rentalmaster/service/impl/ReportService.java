package com.example.rentalmaster.service.impl;

import com.example.rentalmaster.exception.CommonBackendException;
import com.example.rentalmaster.model.db.entity.Clients;
import com.example.rentalmaster.model.db.entity.Employees;
import com.example.rentalmaster.model.db.entity.RentalOrder;
import com.example.rentalmaster.model.db.entity.Technique;
import com.example.rentalmaster.model.db.repository.EmployeesRepository;
import com.example.rentalmaster.model.db.repository.RentalOrderRepository;
import com.example.rentalmaster.model.enums.Roles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private static final String[] COLUMNS = {
            "ID заявки",
            "Клиент (ИНН)",
            "Техника",
            "Дата начала",
            "Дата завершения",
            "Фактическая дата закрытия",
            "Общая стоимость"
    };

    private final RentalOrderRepository rentalOrderRepository;
    private final EmployeesRepository employeesRepository;

    public byte[] generateCompletedOrdersReport() throws IOException {
        List<RentalOrder> orders = rentalOrderRepository.findAllCompletedOrdersWithTechniques();


        if (orders.isEmpty()) {
            log.warn("Отчёт не сгенерирован: нет завершённых заказов");
            throw new CommonBackendException("Нет данных для отчёта", HttpStatus.NO_CONTENT);
        }


        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = prepareSheet(workbook);
            fillDataRows(sheet, orders);
            workbook.write(out);

            log.info("Успешно сгенерирован отчёт с {} записями", orders.size());
            return out.toByteArray();

        } catch (Exception e) {
            log.error("Критическая ошибка генерации отчёта: {}", e.getMessage());
            throw new CommonBackendException("Ошибка генерации файла", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Sheet prepareSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Завершённые заявки");

        // Заголовки
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < COLUMNS.length; i++) {
            headerRow.createCell(i).setCellValue(COLUMNS[i]);
        }
        return sheet;
    }

    private void fillDataRows(Sheet sheet, List<RentalOrder> orders) {
        int rowNum = 1;

        for (RentalOrder order : orders) {
            try {
                Row row = sheet.createRow(rowNum++);
                fillOrderData(row, order);
            } catch (Exception e) {
                log.error("Ошибка обработки заказа ID {}: {}",
                        order.getRentalOrderId(), e.getMessage());
                rowNum--;
            }
        }
    }

    private void fillOrderData(Row row, RentalOrder order) {
        int cellNum = 0;
        try {

            safeCellCreate(row, cellNum++, order.getRentalOrderId(), "ID заявки");

            String inn = Optional.ofNullable(order.getClients())
                    .map(Clients::getInn)
                    .orElse("Не указан");
            safeCellCreate(row, cellNum++, inn, "ИНН клиента");

            String techniqueList = Optional.ofNullable(order.getTechniques())
                    .orElseGet(Collections::emptyList)
                    .stream()
                    .map(Technique::getStateNumber)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(", "));
            safeCellCreate(row, cellNum++, techniqueList.isEmpty() ? "Нет данных" : techniqueList, "Техника");

            safeCellCreate(row, cellNum++,
                    order.getStartDate() != null ? formatDateTime(order.getStartDate()) : "Нет данных",
                    "Дата начала");

            safeCellCreate(row, cellNum++,
                    order.getEndDate() != null ? formatDateTime(order.getEndDate()) : "Нет данных",
                    "Дата завершения");

            safeCellCreate(row, cellNum++,
                    order.getActualEndDate() != null ? formatDateTime(order.getActualEndDate()) : "Нет данных",
                    "Фактическая дата");

            Double totalCost = Optional.ofNullable(order.getTotalCost()).orElse(0.0);
            safeCellCreate(row, cellNum, totalCost, "Общая стоимость");


        } catch (Exception e) {
            log.error("Критическая ошибка обработки заказа {}: {}",
                    order.getRentalOrderId(), e.getMessage());
            throw new CommonBackendException(
                    "Ошибка заполнения строки отчёта",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void safeCellCreate(Row row, int cellNum, Object value, String fieldName) {
        try {
            Cell cell = row.createCell(cellNum);

            if (value == null) {
                cell.setCellValue("N/A");
                log.warn("Пустое значение для поля: {}", fieldName);
                return;
            }

            if (value instanceof Number) {
                cell.setCellValue(((Number) value).doubleValue());
            } else if (value instanceof TemporalAccessor) {
                cell.setCellValue(formatDateTime((TemporalAccessor) value));
            } else {
                cell.setCellValue(value.toString());
            }
        } catch (Exception e) {
            log.error("Ошибка создания ячейки {}: {}", fieldName, e.getMessage());
            throw e;
        }
    }

    private String formatDateTime(TemporalAccessor dateTime) {
        if (dateTime == null) {
            log.warn("Попытка форматирования null-даты");
            return "N/A";
        }
        try {
            return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(dateTime);
        } catch (DateTimeException e) {
            log.error("Ошибка формата даты: {}", e.getMessage());
            return "Некорректная дата";
        }
    }

    public Employees getDirector() {
        return employeesRepository.findByRole(Roles.DIRECTOR)
                .orElseThrow(() -> new CommonBackendException(
                        "Директор не найден в системе",
                        HttpStatus.NOT_FOUND
                ));
    }
}
