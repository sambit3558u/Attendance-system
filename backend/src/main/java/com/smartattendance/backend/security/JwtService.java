package com.smartattendance.backend.security;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.smartattendance.backend.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * =========================================================
 * JWT Service
 * ---------------------------------------------------------
 * This service is responsible for:
 *
 * 1. Generating JWT tokens after successful login.
 * 2. Reading user information from JWT tokens.
 * 3. Checking whether a token is valid.
 * 4. Checking whether a token has expired.
 *
 * The token remains valid for 30 days.
 * =========================================================
 */

@Service
public class JwtService {

    /**
     * JWT secret key loaded from application.properties.
     *
     * This key signs the token and protects it from
     * unauthorized modification.
     */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * JWT validity duration in milliseconds.
     *
     * Current value:
     * 2,592,000,000 ms = 30 days
     */
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * =====================================================
     * Generate JWT Token
     * -----------------------------------------------------
     * Called after a user successfully logs in.
     *
     * Token contains:
     * - User email
     * - User ID
     * - User role
     * - Creation time
     * - Expiration time
     * =====================================================
     */
    public String generateToken(User user) {

        Date currentTime = new Date();

        Date expirationTime = new Date(
                currentTime.getTime() + jwtExpiration
        );

        return Jwts.builder()

                /*
                 * Email is stored as the token subject.
                 */
                .subject(user.getEmail())

                /*
                 * Additional user information stored
                 * inside the token.
                 */
                .claim("userId", user.getId())
                .claim("role", user.getRole().name())
                .claim("name", user.getName())

                /*
                 * Token creation time.
                 */
                .issuedAt(currentTime)

                /*
                 * Token expiry time.
                 */
                .expiration(expirationTime)

                /*
                 * Digitally sign the token using
                 * the secret key.
                 */
                .signWith(getSigningKey())

                /*
                 * Convert the token into its final
                 * compact String format.
                 */
                .compact();
    }

    /**
     * =====================================================
     * Extract Email
     * -----------------------------------------------------
     * Reads the email stored as the JWT subject.
     * =====================================================
     */
    public String extractEmail(String token) {

        return extractClaim(
                token,
                Claims::getSubject
        );
    }

    /**
     * =====================================================
     * Extract User ID
     * =====================================================
     */
    public Long extractUserId(String token) {

        Claims claims = extractAllClaims(token);

        return claims.get(
                "userId",
                Long.class
        );
    }

    /**
     * =====================================================
     * Extract Role
     * =====================================================
     */
    public String extractRole(String token) {

        Claims claims = extractAllClaims(token);

        return claims.get(
                "role",
                String.class
        );
    }

    /**
     * =====================================================
     * Extract Expiration Date
     * =====================================================
     */
    public Date extractExpiration(String token) {

        return extractClaim(
                token,
                Claims::getExpiration
        );
    }

    /**
     * =====================================================
     * Extract Any Claim
     * -----------------------------------------------------
     * Generic method used to read a selected value
     * from the JWT claims.
     * =====================================================
     */
    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimResolver
    ) {

        Claims claims = extractAllClaims(token);

        return claimResolver.apply(claims);
    }

    /**
     * =====================================================
     * Validate JWT Token
     * -----------------------------------------------------
     * Token is valid when:
     *
     * 1. Token email matches the database user's email.
     * 2. Token has not expired.
     * 3. Token signature is valid.
     * =====================================================
     */
    public boolean isTokenValid(
            String token,
            User user
    ) {

        try {
            String tokenEmail = extractEmail(token);

            return tokenEmail.equalsIgnoreCase(user.getEmail())
                    && !isTokenExpired(token);

        } catch (Exception exception) {

            /*
             * Invalid, modified, malformed or expired
             * token will return false.
             */
            return false;
        }
    }

    /**
     * =====================================================
     * Check Token Expiration
     * =====================================================
     */
    public boolean isTokenExpired(String token) {

        return extractExpiration(token)
                .before(new Date());
    }

    /**
     * =====================================================
     * Read and Verify All Claims
     * -----------------------------------------------------
     * The JWT signature is verified before claims
     * are returned.
     * =====================================================
     */
    private Claims extractAllClaims(String token) {

        return Jwts.parser()

                /*
                 * Verify token signature using the
                 * same signing key.
                 */
                .verifyWith(getSigningKey())

                /*
                 * Create the JWT parser.
                 */
                .build()

                /*
                 * Parse signed JWT token.
                 */
                .parseSignedClaims(token)

                /*
                 * Return token payload.
                 */
                .getPayload();
    }

    /**
     * =====================================================
     * Create Signing Key
     * -----------------------------------------------------
     * Converts Base64 secret from application.properties
     * into a SecretKey used to sign and verify tokens.
     * =====================================================
     */
    private SecretKey getSigningKey() {

        byte[] decodedKey =
                Decoders.BASE64.decode(jwtSecret);

        return Keys.hmacShaKeyFor(decodedKey);
    }

    /**
     * Returns configured token validity.
     *
     * Frontend response me expiry information bhejne
     * ke liye use hoga.
     */
    public long getExpirationTime() {
        return jwtExpiration;
    }
}