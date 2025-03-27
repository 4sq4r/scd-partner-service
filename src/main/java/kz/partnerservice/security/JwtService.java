package kz.partnerservice.security;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

import static com.auth0.jwt.JWT.create;
import static com.auth0.jwt.JWT.require;
import static com.auth0.jwt.algorithms.Algorithm.HMAC256;
import static java.time.temporal.ChronoUnit.HOURS;

@Slf4j
@Service
public class JwtService {

    @Value("${spring.security.jwt.issuer}")
    private String issuer;

    @Value("${spring.security.jwt.secret}")
    private String jwtSecret;

    @Value("${spring.security.jwt.expirationHours}")
    private int jwtExpirationHours;

    private Algorithm algorithm;
    private JWTVerifier verifier;

    @PostConstruct
    public void init() {
        try {
            this.algorithm = HMAC256(jwtSecret);
            this.verifier = require(algorithm)
                    .withIssuer(issuer)
                    .build();
        } catch (IllegalArgumentException e) {
            log.error("Error initializing JWT algorithm: {}", e.getMessage());
        }
    }

    public String generateToken(Authentication authentication) {
        return create()
                .withSubject(authentication.getName())
                .withIssuer(issuer)
                .withExpiresAt(Date.from(Instant.now()
                        .plus(jwtExpirationHours, HOURS)))
                .sign(algorithm);
    }

    public boolean validateToken(String token) {
        return verifier.verify(token).getExpiresAt().after(new Date());
    }

    public String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        }

        return "Username not found.";
    }
}