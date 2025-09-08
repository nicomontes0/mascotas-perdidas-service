package com.mascotasperdidas.controller;

import com.mascotasperdidas.controller.model.ReportRequestBody;
import com.mascotasperdidas.model.Notice;
import com.mascotasperdidas.model.UserReport;
import com.mascotasperdidas.service.NoticeService;
import com.mascotasperdidas.service.UserReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/api/reports")
public class ReportController {

    private final UserReportService userReportService;
    private final NoticeService noticeService;

    public ReportController(UserReportService userReportService, NoticeService noticeService) {
        this.userReportService = userReportService;
        this.noticeService = noticeService;
    }

    @GetMapping
    public List<UserReport> getReports(
            @RequestParam(value = "noticeId", required = false) UUID noticeId
    ) {
        log.info("Se llama a /api/reports con noticeId: {}", noticeId);
        if (noticeId == null) {
            return userReportService.get();
        } else {
            return userReportService.getByNotice(noticeId);
        }
    }

    @GetMapping("/{id}")
    public UserReport getReport(
            @PathVariable UUID id
    ) {
        log.info("Se llama a /api/reports/{}", id);
        UserReport userReport = userReportService.get(id);
        log.info("Se obtiene el report: {}", userReport);
        return userReport;
    }

    @PostMapping
    public ResponseEntity<UUID> create(@RequestBody ReportRequestBody reportRequestBody) {
        log.info("Se llama a crear /api/reports con body {}", reportRequestBody);
        Notice noticeFromReport = noticeService.get(reportRequestBody.getNoticeId());
        UserReport report = userReportService.create(reportRequestBody.toDomain(noticeFromReport));
        return ResponseEntity.status(HttpStatus.CREATED).body(report.getId());
    }

}
