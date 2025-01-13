package com.Backend.JWTImp.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtUtils {

    private static final String SECURE_KEY = "replace your secret key here";

    // Access Token Expiration Time
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 2 * 60; // 15 minutes
    // Refresh Token Expiration Time
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 8 * 60; // 7 days

    public String issueAccessToken(String subject, String... roles) {
        return issueToken(subject, ACCESS_TOKEN_EXPIRATION_TIME, roles);
    }

    // Issue Refresh Token
    public String issueRefreshToken(String subject) {
        return issueToken(subject, REFRESH_TOKEN_EXPIRATION_TIME);
    }

    private String issueToken(String subject, long expirationTimeInSeconds, String... roles) {
        Map<String, Object> claims = new HashMap<>();
        if (roles.length > 0) {
            claims.put("roles", roles);
        }

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTimeInSeconds * 1000)) // Set expiration time
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECURE_KEY));
    }

    // Validate Token
    public boolean isValidToken(String token, String username) {
        String subject = getSubject(token);
        return subject.equals(username) && !isTokenExpired(token);
    }

    // Check if the token is expired
    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    // Get Claims from Token
    public Claims getClaims(String token) {
        Claims claims = Jwts
                .parser()
                .verifyWith((SecretKey) getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims;
    }

    // Get Subject (Username) from Token
    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }
}
