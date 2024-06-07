package com.house.hunter.util;

import com.house.hunter.constant.UserRole;
import com.house.hunter.model.entity.RefreshToken;
import com.house.hunter.model.entity.User;
import com.house.hunter.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@AllArgsConstructor
@Data
public class JWTUtil {
    private final String secretKey;
    @Value("${jwt.access.expiration}")
    private long accessTokenExpirationTime;
    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpirationTime;
    @Autowired
    private UserRepository userRepository;
    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    public JWTUtil() {
        this.secretKey = SecretKeyUtil.readEncryptedSecretFromEnv();
    }

    public RefreshToken generateRefreshToken(User user) {
        final String token = UUID.randomUUID().toString();
        return RefreshToken.builder()
                .user(user)
                .token(token)
                .expiryDate(new Date(System.currentTimeMillis() + refreshTokenExpirationTime).toInstant())
                .build();
    }

    public String buildAccessToken(String email, UserRole role) {
        return buildToken(email, role, accessTokenExpirationTime);
    }

    private String buildToken(String email, UserRole role, long expirationTime) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("role", role);
        claims.put("status", userRepository.findByEmail(email).get().getVerificationStatus().name());
        return TOKEN_PREFIX + Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusMillis(expirationTime)))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public boolean validateTokenWithoutPrefix(String token) {
        try {
            return validateTokenClaims(parseClaims(token));
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    public String getEmailFromToken(String token) {
        return parseClaims(token).get("email", String.class);
    }


    private boolean validateTokenClaims(Claims claims) {
        try {
            Date exp = claims.get("exp", Date.class);
            Instant expirationTime = exp.toInstant();

            // Check if the token is expired, email is invalid, or role is invalid
            return expirationTime.isAfter(Instant.now()) &&
                    getStatus(claims) != null && !getStatus(claims).isEmpty() &&
                    getEmail(claims) != null && !getEmail(claims).isEmpty() &&
                    getRole(claims) != null && isValidRole(getRole(claims));
        } catch (Exception e) {
            return false;
        }
    }

    public Instant getExpirationDateFromToken(String token) {
        return parseClaims(token).getExpiration().toInstant();
    }

    private boolean isValidRole(String role) {
        // Check if the role is one of the allowed roles
        return role.equals("ADMIN") || role.equals("LANDLORD") || role.equals("TENANT") || role.equals("GUEST");
    }

    private String getEmail(Claims claims) {
        var deg = claims.get("email", String.class);
        return deg;
    }

    private String getRole(Claims claims) {
        var deg = claims.get("role", String.class);
        return deg;
    }

    private String getStatus(Claims claims) {
        var deg = claims.get("status", String.class);
        return deg;
    }

}
