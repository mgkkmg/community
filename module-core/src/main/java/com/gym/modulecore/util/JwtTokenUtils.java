package com.gym.modulecore.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JwtTokenUtils {

    public static String getUserName(String token, String key) {
        return extractClaims(token, key).get("userName", String.class);
    }

    public static boolean isExpired(String token, String key) {
        Date expiredDate = extractClaims(token, key).getExpiration();
        return expiredDate.before(new Date());
    }

    private static Claims extractClaims(String token, String key) {
        // JWT를 검증하기 위한 Jwts.parser() 메서드 제공
        // 검증 시에는 JWT 문자열과 비밀키를 전달하여 JWT를 검증
        // JWS(JSON Web Signature)는 JWT의 서명 부분을 담당하는 표준 기술로 JWT가 위조되지 않았음을 검증하기 위해 사용
        // parseClaimsJws()는  JWT의 서명 부분을 검증하여 JWT가 위조되었는지 여부를 확인하고, 검증에 성공하면 JWT의 내용인 Claim(페이로드)을 파싱하여 반환한다.
        return Jwts.parserBuilder().setSigningKey(getKey(key))
                .build().parseClaimsJws(token).getBody();
    }

    public static String generateToken(String userName, String key, long expiredTimeMs) {
        /* JWT의 페이로드 구조
        {
            "sub": "1234567890",
            "name": "John Doe",
            "admin": true
        }
        */
        // 페이로드 구성에 담을 key와 value의 한쌍으로 이루어 진 형태를 클레임이라고 한다.
        // 위에 예시로는 "sub": "1234567890"가 하나의 클레임이다.
        Claims claims = Jwts.claims();
        claims.put("userName", userName);

        // JWT를 생성하기 위한 Jwts.builder() 메서드 제공
        // JWT 생성 시 헤더와 페이로드 정보를 설정하고, 서명에 필요한 알고리즘과 비밀키를 설정
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis())) // 토큰 발행 시간
                .setExpiration(new Date(System.currentTimeMillis() + expiredTimeMs)) // 토큰 만료 시간
                .signWith(getKey(key), SignatureAlgorithm.HS256) // 해시 알고리즘(256bits)
                .compact();
    }

    private static Key getKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
