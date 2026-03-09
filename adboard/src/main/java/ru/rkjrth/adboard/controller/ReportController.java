package ru.rkjrth.adboard.controller;

import ru.rkjrth.adboard.dto.ReportDto;
import ru.rkjrth.adboard.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public List<ReportDto> getAll() { return reportService.getAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<ReportDto> getById(@PathVariable Long id) {
        ReportDto report = reportService.getById(id);
        return report != null ? ResponseEntity.ok(report) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ReportDto create(@RequestBody ReportDto report) { return reportService.create(report); }

    @PutMapping("/{id}")
    public ResponseEntity<ReportDto> update(@PathVariable Long id, @RequestBody ReportDto report) {
        ReportDto updated = reportService.update(id, report);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = reportService.delete(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
