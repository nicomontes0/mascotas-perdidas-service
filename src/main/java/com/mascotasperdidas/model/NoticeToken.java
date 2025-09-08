package com.mascotasperdidas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "report_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeToken {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "report_id", nullable = false)
    private UUID noticeId;

    @Column(name = "jti_hash", nullable = false)
    private String jtiHash;

    @Column(name = "jti_last8", nullable = false)
    private String jtiLast8;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

}