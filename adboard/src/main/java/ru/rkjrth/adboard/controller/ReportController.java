package ru.rkjrth.adboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.rkjrth.adboard.entity.Report;
import ru.rkjrth.adboard.entity.Report.Status;
import ru.rkjrth.adboard.service.ReportService;
import ru.rkjrth.adboard.repository.ReportRepository;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;
    private final ReportRepository reportRepository;

    public ReportController(ReportService reportService,
                            ReportRepository reportRepository) {
        this.reportService = reportService;
        this.reportRepository = reportRepository;
    }

    // ==== НОВОЕ: получить все жалобы ====

    @GetMapping
    public List<Report> getAll() {
        return reportRepository.findAll();
    }

    // ==== НОВОЕ: получить одну жалобу по id ====

    @GetMapping("/{id}")
    public ResponseEntity<Report> getById(@PathVariable Long id) {
        return reportRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ====== DTO для запросов ======

    public static class CreateReportRequest {
        private String reason;

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    public static class CompleteReviewRequest {
        private Status status;

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }
    }

    // Бизнес-операция 3: подать жалобу на объявление
    @PostMapping("/listing/{listingId}/from/{reporterId}")
    public ResponseEntity<Report> createReport(@PathVariable Long listingId,
                                               @PathVariable Long reporterId,
                                               @RequestBody CreateReportRequest body) {
        Report report = reportService.createReport(listingId, reporterId, body.getReason());
        return ResponseEntity.ok(report);
    }

    // Бизнес-операция 4: взять жалобу в работу
    @PostMapping("/{reportId}/start-review")
    public ResponseEntity<Report> startReview(@PathVariable Long reportId) {
        Report report = reportService.startReview(reportId);
        return ResponseEntity.ok(report);
    }

    // Бизнес-операция 5: завершить рассмотрение жалобы
    @PostMapping("/{reportId}/complete-review")
    public ResponseEntity<Report> completeReview(@PathVariable Long reportId,
                                                 @RequestBody CompleteReviewRequest body) {
        Report report = reportService.completeReview(reportId, body.getStatus());
        return ResponseEntity.ok(report);
    }
}
