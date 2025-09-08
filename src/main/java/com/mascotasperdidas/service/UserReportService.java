package com.mascotasperdidas.service;

import com.mascotasperdidas.model.UserReport;
import com.mascotasperdidas.repositories.UserReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UserReportService {

    private final UserReportRepository userReportRepository;

    public UserReportService(UserReportRepository userReportRepository) {
        this.userReportRepository = userReportRepository;
    }

    public UserReport create(UserReport userReport) {
        return userReportRepository.save(userReport);
    }

    public List<UserReport> getByNotice(UUID noticeId) {
        return userReportRepository.findByReportedNoticeId(noticeId);
    }

    public UserReport get(UUID id) {
        return userReportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report no encontrado."));
    }

    public List<UserReport> get() {
        return userReportRepository.findAll();
    }
}
