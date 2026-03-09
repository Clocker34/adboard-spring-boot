package ru.rkjrth.adboard.service;

import ru.rkjrth.adboard.dto.ReportDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ReportService {
    private final Map<Long, ReportDto> reports = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public List<ReportDto> getAll() { return new ArrayList<>(reports.values()); }

    public ReportDto getById(Long id) { return reports.get(id); }

    public ReportDto create(ReportDto report) {
        Long id = idGenerator.getAndIncrement();
        ReportDto newReport = new ReportDto(id, report.reason(), report.reporterId(), report.targetListingId());
        reports.put(id, newReport);
        return newReport;
    }

    public ReportDto update(Long id, ReportDto report) {
        if (!reports.containsKey(id)) return null;
        ReportDto updated = new ReportDto(id, report.reason(), report.reporterId(), report.targetListingId());
        reports.put(id, updated);
        return updated;
    }

    public boolean delete(Long id) { return reports.remove(id) != null; }
}
