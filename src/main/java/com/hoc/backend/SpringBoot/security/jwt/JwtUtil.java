package com.hoc.backend.SpringBoot.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {

    private static final String ACCESS_SECRET = "access-secret-access-secret-access-secret";
    private static final String REFRESH_SECRET = "refresh-secret-refresh-secret-refresh-secret";
    private static final Key accessKey = Keys.hmacShaKeyFor(ACCESS_SECRET.getBytes());
    private static final Key refreshKey = Keys.hmacShaKeyFor(REFRESH_SECRET.getBytes());
    private static final long EXPIRATION_TIME_ACCESS_TOKEN = 1000 * 60 * 15;
    private static final long EXPIRATION_TIME_REFRESH_TOKEN =  1000L * 60 * 60 * 24 * 7;

    public static String generateAccessToken(String account, String role) {

        System.out.println(">>> GENERATE ACCESS TOKEN WITH: " + account);

        String accessToken = Jwts.builder()
                .setSubject(account)
                .claim("role", role)
//                .claim("age",age)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_ACCESS_TOKEN))
                .signWith(accessKey)
                .compact();

        System.out.println(">>> ACCESS TOKEN: " + accessToken);

        return accessToken;
    }

    public static String genarateRefreshToken(String account, String role) {

        String refreshToken = Jwts.builder()
                .setSubject(account)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_REFRESH_TOKEN))
                .signWith(refreshKey)
                .compact();

        System.out.println(">>> REFRESH TOKEN " + refreshToken );

        return refreshToken;
    }

    public static Claims validateAccessToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(accessKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static Claims validateRefreshToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(refreshKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static String getUsername(String token) {
        return validateAccessToken(token).getSubject();
    }

    public static String getRole(String token) {
        return validateAccessToken(token).get("role", String.class);
    }
}

//
//--> Tiếp tục endpoint gọi để refresh token -> ( đặt logic kểm tra ở đâu ) -> kiểm tra
//        mỗi lần client gửi request