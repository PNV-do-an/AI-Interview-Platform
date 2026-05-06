package com.hoc.backend.SpringBoot.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {

    private static final String SECRET = "my-secret-key-my-secret-key-my-secret-key";
    private static final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());
    private static final long EXPIRATION_TIME = 1000 * 60 * 30;

    public static String generateToken(String account, String role) {

//        System.out.println(">>> GENERATE TOKEN WITH: " + account);

        String token = Jwts.builder()
                .setSubject(account)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();

        System.out.println(">>> TOKEN: " + token);

        return token;
    }

    public static Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static String getUsername(String token) {
        return validateToken(token).getSubject();
    }

    public static String getRole(String token) {
        return validateToken(token).get("role", String.class);
    }
}