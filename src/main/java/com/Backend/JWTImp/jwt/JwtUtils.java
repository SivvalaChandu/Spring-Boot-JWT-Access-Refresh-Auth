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

    private static final String SECURE_KEY = "af60addca9ea3e3c099551e1b6576c9966dce0a33de879dd7e160f86dbd872ca236d6e9ee66fb6e30039fe7c345324a10f3d0741b0600fa7a45df4c6691eff4f4209767ed39f51e37717d8feecd5dd14fc34ebe619e6a29ae91d9ffe134cb5718bec0b3680d6ae7fc09e67763fe7c05d05d3ba69f47211163852633755b7f861132b0c98f8d7c1af9152d547408e676867a0a32fb525a4354180f5fb6b2dc23b5faa4155b8db63385f96259a90b6ee0e74a5b90a4f0f4fa96fafc296c64588b5c009b3829ae2e1d69a1cf7569b50a65fa553350495d18816f785f961c970c0a9cb9c8da25cc5e9fa4a3e9527a132d616b232d1ee21c3bf6dc8d9e3376e2e82c0";

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