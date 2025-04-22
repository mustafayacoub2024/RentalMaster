package com.example.rentalmaster.controllers;

import com.example.rentalmaster.model.db.entity.RentalOrder;
import com.example.rentalmaster.model.db.repository.EmployeesRepository;
import com.example.rentalmaster.model.db.repository.RentalOrderRepository;
import com.example.rentalmaster.service.impl.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test-report")
public class TestReportController {

    private final ReportService reportService;
    private final EmployeesRepository employeesRepository;
    private final RentalOrderRepository rentalOrderRepository;

    @GetMapping("/generate")
    @PreAuthorize("hasRole('DIRECTOR')")
    public void testReportGeneration() throws IOException {
        List<RentalOrder> orders = rentalOrderRepository.findAllCompletedOrdersWithTechniques();
        log.info("Найдено завершённых заявок: {}", orders.size());

        byte[] stream = reportService.generateCompletedOrdersReport();
        Path path = Paths.get("test_report.xlsx");
        Files.copy(new ByteArrayInputStream(stream), path, StandardCopyOption.REPLACE_EXISTING);

        log.info("Файл сохранён по пути: {}", path.toAbsolutePath());
    }
}