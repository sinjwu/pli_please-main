package com.team8.pli.please.security;

import com.team8.pli.please.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // Base64 버전
    @Value("${jwt.secret-b64:}")
    private String secretKeyB64;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMs;

    private Key signKey;

    @PostConstruct
    void initKey() {
        if (secretKeyB64 == null || secretKeyB64.isBlank()) {
            throw new IllegalStateException("JWT secret is missing: set jwt.secret-b64 / JWT_SECRET_B64");
        }
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyB64);
        if (keyBytes.length < 32) { // 256-bit 미만 금지
            throw new IllegalStateException("JWT secret must be >= 32 bytes after Base64 decoding.");
        }
        this.signKey = Keys.hmacShaKeyFor(keyBytes);
    }

    private Key getSignInKey() { return signKey; }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> extra = new HashMap<>();
        if (userDetails instanceof User u) {
            extra.put("id", u.getId());
            extra.put("email", u.getEmail());
            extra.put("username", u.getUsername());
            extra.put("profileImageUrl", u.getProfileImageUrl());
            extra.put("bio", u.getBio());
        }
        return generateToken(extra, userDetails);
    }

    public String generateToken(Map<String, Object> extra, UserDetails userDetails) {
        return buildToken(extra, userDetails, jwtExpirationMs);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshExpirationMs);
    }

    private String buildToken(Map<String, Object> claims, UserDetails userDetails, long expMs) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expMs))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String sub = extractUsername(token);
        final boolean notExpired = extractExpiration(token).after(new Date());
        String expected = (userDetails instanceof User u) ? u.getUsername() : userDetails.getUsername();
        return notExpired && sub.equals(expected);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
