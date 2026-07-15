package com.singsing.frozenapi.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

// JWT(JSON Web Token) 생성/검증을 담당하는 유틸리티
//
// @Component로 등록해서 Spring이 관리하는 Bean으로 만든 이유:
// application.yaml의 custom.jwt.key 값을 @Value로 주입받으려면 static 메서드가 아니라
// Spring이 생성/관리하는 객체(Bean)여야 하기 때문 (static은 DI를 받을 수 없음)
@Component
@Slf4j
public class JWTUtil {

    @Value("${custom.jwt.key}") // application.yaml의 custom.jwt.key 값이 주입됨
    private String key;

    // 토큰 생성 : claims(토큰에 담을 정보)와 유효시간(분)을 받아 JWT 문자열을 만든다
    public String generateToken(Map<String, Object> claims, int minutes) {
        SecretKey secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));

        String token = Jwts.builder()
                .setHeader(Map.of("typ", "JWT"))
                .setClaims(claims)
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(minutes).toInstant()))
                .signWith(secretKey) // 이 비밀 키로 서명 -> 키를 모르면 토큰을 위조할 수 없음
                .compact();

        log.info("generated jwt token: {}", token);
        return token;
    }

    // 토큰 검증 : 서명/만료 여부를 확인하고, 문제 없으면 토큰에 담겨있던 claims를 꺼내서 리턴
    // 문제가 있으면(만료/변조 등) CustomJWTException을 던진다
    public Map<String, Object> validateToken(String token) {
        try {
            SecretKey secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token) // 서명 검증 + 파싱, 실패 시 예외 발생
                    .getBody();
        } catch (ExpiredJwtException e) { // 유효기간이 지난 토큰
            throw new CustomJWTException("EXPIRED");
        } catch (JwtException e) { // 서명 불일치, 형식 오류 등 그 외 모든 JWT 관련 예외
            throw new CustomJWTException("INVALID");
        } catch (Exception e) { // 예상 못한 나머지 예외
            throw new CustomJWTException("ERROR");
        }
    }

}
