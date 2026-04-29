package com.Api_Gateway;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.Api_Gateway.security.JwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil();

    // Helper - generate a valid test token
    private String generateTestToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(SignatureAlgorithm.HS256, "secretarjituttt123455667ksfbbf")
                .compact();
    }

    // ✅ Test 1 - Valid token passes validation
    @Test
    void testValidateToken_Valid() {
        String token = generateTestToken("arjit", "ROLE_USER");
        assertTrue(jwtUtil.validateToken(token));
    }

    // ✅ Test 2 - Invalid token fails validation
    @Test
    void testValidateToken_Invalid() {
        assertFalse(jwtUtil.validateToken("invalid.token.here"));
    }

    // ✅ Test 3 - Extract claims username
    @Test
    void testExtractClaims_Username() {
        String token = generateTestToken("arjit", "ROLE_USER");
        Claims claims = jwtUtil.extractClaims(token);
        assertEquals("arjit", claims.getSubject());
    }

    // ✅ Test 4 - Extract claims role
    @Test
    void testExtractClaims_Role() {
        String token = generateTestToken("arjit", "ADMIN");
        Claims claims = jwtUtil.extractClaims(token);
        assertEquals("ADMIN", claims.get("role").toString());
    }

    // ✅ Test 5 - Tampered token fails
    @Test
    void testValidateToken_Tampered() {
        String token = generateTestToken("arjit", "ROLE_USER");
        String tampered = token + "tampered";
        assertFalse(jwtUtil.validateToken(tampered));
    }
}
