package com.mascotasperdidas.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtConfig {

    private final Key signingKey;
    private final String issuer;

    public JwtConfig(@Value("${security.jwt.secret}") String secret,
                   @Value("${security.jwt.issuer}") String issuer) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes()); // HS256 example
        this.issuer = issuer;
    }

    public String createTokenForNotice(String noticeId, long ttlSeconds, String jti) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject("anon-owner")
                .setIssuer(issuer)
                .setId(jti)
                .claim("noticeId", noticeId)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(ttlSeconds)))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token);
    }
}