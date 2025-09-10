package com.mascotasperdidas.service;

import com.mascotasperdidas.configuration.JwtConfig;
import com.mascotasperdidas.model.NoticeToken;
import com.mascotasperdidas.repositories.NoticeTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class TokenService {

    private final JwtConfig jwtConfig;
    private final NoticeTokenRepository tokenRepo;
    private final long defaultTtl;
    private final Argon2PasswordEncoder passwordEncoder;
    private final String pepper;

    public TokenService(JwtConfig jwtConfig,
                        NoticeTokenRepository tokenRepo,
                        @Value("${security.jwt.ttl-seconds}") long defaultTtl,
                        @Value("${security.token.pepper}") String pepper) {
        this.jwtConfig = jwtConfig;
        this.tokenRepo = tokenRepo;
        this.defaultTtl = defaultTtl;
        this.passwordEncoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
        this.pepper = pepper;
    }

    @Transactional
    public String generateTokenForNotice(UUID noticeId) {
        String jti = UUID.randomUUID().toString();
        String token = jwtConfig.createTokenForNotice(noticeId.toString(), defaultTtl, jti);

        String toHash = jti + pepper;
        String jtiHash = passwordEncoder.encode(toHash);
        String jtiLast8 = jti.length() > 8 ? jti.substring(jti.length() - 8) : jti;

        NoticeToken nt = new NoticeToken();
        nt.setNoticeId(noticeId);
        nt.setJtiHash(jtiHash);
        nt.setJtiLast8(jtiLast8);
        nt.setExpiresAt(Instant.now().plusSeconds(defaultTtl));
        tokenRepo.save(nt);

        return token;
    }

    public boolean validateTokenAndOwnership(String jwtToken, UUID id) {
        try {
            Jws<Claims> parsed = jwtConfig.parseToken(jwtToken);
            Claims c = parsed.getBody();
            String jti = c.getId();
            UUID noticeId = UUID.fromString(c.get("noticeId", String.class));

            if (!noticeId.equals(id)) {
                return false;
            }

            List<NoticeToken> candidates = tokenRepo.findByNoticeIdAndRevokedAtIsNull(noticeId);
            String toMatch = jti + pepper;
            for (NoticeToken nt : candidates) {
                if (passwordEncoder.matches(toMatch, nt.getJtiHash())) {
                    // comprobar expires_at en DB align con JWT
                    return nt.getExpiresAt() == null || !nt.getExpiresAt().isBefore(Instant.now());
                }
            }
            return false;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    @Transactional
    public void revokeToken(UUID tokenId) {
        NoticeToken nt = tokenRepo.findById(tokenId).orElseThrow();
        nt.setRevokedAt(Instant.now());
        tokenRepo.save(nt);
    }

    @Transactional
    public String rotateTokenForNotice(UUID noticeId) {
        // revoca todos los tokens actuales y crea uno nuevo (o solo revoca el actual)
        tokenRepo.revokeByNoticeId(noticeId, Instant.now());
        return generateTokenForNotice(noticeId);
    }
}