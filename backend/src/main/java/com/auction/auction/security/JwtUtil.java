package com.auction.auction.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "supersecretkeyforauctionappjwtgeneration123";    // 32byte 이상 필요
    private final long EXPIRATION_TIME = 1000*60*60*24; // 24시간


    /**
     * JWT 토큰을 생성하는 메서드
     * @param email 사용자의 이메일 (토큰에 저장될 정보)
     * @return 생성된 JWT 토큰 문자열
     */
    public String generateToken(String email){
        return Jwts.builder()
                .setSubject(email)  // 토큰의 사용자 식별 값을 이메일로 설정
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))  // 만료시간(현재시간 + 24시간)
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)    // 서명 적용
                .compact();
    }

    /**
     * JWT 토큰에서 이메일(사용자 식별 값) 추출
     * @param token 클라이언트가 보낸 JWT 토큰
     * @return 토큰에 저장된 사용자 이메일
     */
    public String getEmailFromToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8)) // 서명 키 설정
                .build()
                .parseClaimsJws(token)  // 토큰을 해석하여 정보 가져오기
                .getBody()
                .getSubject();
    }

    /**
     * JWT 토큰이 유효한지 검증하는 메서드
     * @param token 클라이언트가 보낸 JWT 토큰
     * @return 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(token); // 토큰 검증 시도

            return true;    // 검증 성공
        }   catch (JwtException | IllegalArgumentException e){
            return false;   // 검증 실패
        }
    }
}
