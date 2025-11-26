package org.makrowave.agartha_plinko_backend.authentication.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService implements IJwtService {

    private final byte[] key = "replace-with-min-32-char-secret-key".getBytes();

    public String generateToken(String username) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + 1000 * 60 * 60);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(Keys.hmacShaKeyFor(key), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(key))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validate(String token) {
        try {
            extractUsername(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
