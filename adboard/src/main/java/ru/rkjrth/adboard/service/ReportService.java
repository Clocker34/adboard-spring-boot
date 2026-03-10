package ru.rkjrth.adboard.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rkjrth.adboard.entity.Listing;
import ru.rkjrth.adboard.entity.Report;
import ru.rkjrth.adboard.entity.Report.Status;
import ru.rkjrth.adboard.entity.User;
import ru.rkjrth.adboard.repository.ListingRepository;
import ru.rkjrth.adboard.repository.ReportRepository;
import ru.rkjrth.adboard.repository.UserRepository;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ListingRepository listingRepository;

    public ReportService(ReportRepository reportRepository,
                         UserRepository userRepository,
                         ListingRepository listingRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.listingRepository = listingRepository;
    }

    /**
     * Бизнес-операция 3:
     * Подать жалобу на объявление.
     */
    @Transactional
    public Report createReport(Long listingId,
                               Long reporterId,
                               String reason) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found: " + listingId));

        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new IllegalArgumentException("Reporter not found: " + reporterId));

        if (listing.getOwner().getId().equals(reporter.getId())) {
            throw new IllegalArgumentException("Owner cannot report his own listing");
        }

        Report report = new Report(reason, reporter, listing);
        return reportRepository.save(report);
    }

    /**
     * Бизнес-операция 4:
     * Взять жалобу в работу: статус IN_REVIEW, объявление скрыть (HIDDEN).
     */
    @Transactional
    public Report startReview(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found: " + reportId));

        Listing listing = report.getListing();

        if (report.getStatus() != Status.OPEN) {
            throw new IllegalStateException("Report must be OPEN to start review");
        }

        report.setStatus(Status.IN_REVIEW);
        listing.setStatus(Listing.Status.HIDDEN);

        reportRepository.save(report);
        listingRepository.save(listing);

        return report;
    }

    /**
     * Бизнес-операция 5:
     * Завершить рассмотрение жалобы: RESOLVED или REJECTED.
     * При RESOLVED объявление остаётся HIDDEN,
     * при REJECTED возвращаем объявление в ACTIVE.
     */
    @Transactional
    public Report completeReview(Long reportId, Status newStatus) {
        if (newStatus != Status.RESOLVED && newStatus != Status.REJECTED) {
            throw new IllegalArgumentException("newStatus must be RESOLVED or REJECTED");
        }

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found: " + reportId));

        Listing listing = report.getListing();

        if (report.getStatus() != Status.IN_REVIEW) {
            throw new IllegalStateException("Only IN_REVIEW reports can be completed");
        }

        report.setStatus(newStatus);

        if (newStatus == Status.REJECTED) {
            listing.setStatus(Listing.Status.ACTIVE);
        }

        reportRepository.save(report);
        listingRepository.save(listing);

        return report;
    }
}
