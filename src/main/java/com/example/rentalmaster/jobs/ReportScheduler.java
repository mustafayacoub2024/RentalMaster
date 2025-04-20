package com.example.rentalmaster.jobs;

import com.example.rentalmaster.model.db.entity.Employees;
import com.example.rentalmaster.service.impl.EmailService;
import com.example.rentalmaster.service.impl.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportScheduler {

    private final ReportService reportService;
    private final EmailService emailService;

    @Scheduled(cron = "0 0 9 * * MON-FRI") // Каждый будний день в 09:00
//    @Scheduled(initialDelay = 10_000, fixedDelay = Long.MAX_VALUE) // нужно для проверки шедулера, отправляет отчёт через 10 сек после запуска программы
    public void sendDailyCompletedOrdersReport() {
        try {
            Employees director = reportService.getDirector();
            byte[] reportData = reportService.generateCompletedOrdersReport();

            if (reportData.length == 0) {
                log.warn("Отчёт не содержит данных");
                return;
            }

            emailService.sendEmailWithAttachment(
                    director.getEmail(),
                    "Ежедневный отчёт по завершённым заявкам",
                    "Во вложении отчёт по всем завершённым заявкам",
                    reportData,
                    "completed_orders_" + LocalDate.now() + ".xlsx"
            );

        } catch (Exception e) {
            log.error("Ошибка генерации отчёта: {}", e.getMessage(), e);
        }
    }
}
