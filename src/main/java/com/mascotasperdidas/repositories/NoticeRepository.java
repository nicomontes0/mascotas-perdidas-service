package com.mascotasperdidas.repositories;

import com.mascotasperdidas.model.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface NoticeRepository extends JpaRepository<Notice, UUID>, JpaSpecificationExecutor<Notice> {
}
