package com.mascotasperdidas.repositories;

import com.mascotasperdidas.model.NoticeToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface NoticeTokenRepository extends JpaRepository<NoticeToken, UUID> {
    List<NoticeToken> findByNoticeIdAndRevokedAtIsNull(UUID noticeId);

    @Modifying
    @Query("UPDATE NoticeToken t SET t.revokedAt = :revokedAt WHERE t.noticeId = :noticeId AND t.revokedAt IS NULL")
    void revokeByNoticeId(@Param("noticeId") UUID noticeId, @Param("revokedAt") Instant revokedAt);
}
