package com.mascotasperdidas.repositories;

import com.mascotasperdidas.model.UserReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserReportRepository extends JpaRepository<UserReport, UUID> {
    List<UserReport> findByReportedNoticeId(UUID reportedNoticeId);
}
