package com.mascotasperdidas.repositories;

import com.mascotasperdidas.model.NoticeImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NoticeImageRepository extends JpaRepository<NoticeImage, UUID> {
}
