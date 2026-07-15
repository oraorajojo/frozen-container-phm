package com.singsing.frozenapi.config;

import com.singsing.frozenapi.security.filter.JWTCheckFilter;
import com.singsing.frozenapi.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

// Spring Security 관련 전역 설정
//
// build.gradle에 spring-boot-starter-security를 추가하는 순간,
// Spring Security가 자동으로 활성화되면서 "기본적으로 모든 요청에 로그인을 요구"하게 된다.
// (요청 시 자동 생성된 임시 비밀번호로 로그인해야 하는 기본 로그인 페이지가 뜸)
// 그래서 이 설정 클래스를 직접 만들어 우리 프로젝트에 맞게 규칙을 재정의
@Configuration    // 이 클래스가 Spring 설정 클래스임을 표시 (내부의 @Bean들이 자동 등록됨)
@Slf4j
@EnableWebSecurity // Spring Security의 웹 보안 기능을 활성화
@RequiredArgsConstructor // JWTUtil을 생성자로 주입받기 위해 추가 (JWTCheckFilter를 만들 때 필요)
public class CustomSecurityConfig {

    private final JWTUtil jwtUtil;

    // HTTP 요청에 대한 보안 규칙 체인을 정의하는 핵심 Bean
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("********************** security config!");

        // CORS(Cross-Origin Resource Sharing) 설정 적용
        // -> 리액트(다른 포트/도메인)에서 이 서버로 요청을 보낼 수 있도록 허용하기 위함 (아래 corsConfigurationSource() 참고)
        http.cors(corsConfig -> corsConfig.configurationSource(corsConfigurationSource()));

        // 세션을 사용하지 않는 무상태(STATELESS) 방식으로 설정
        // -> REST API 서버는 보통 세션 대신 매 요청마다 토큰(JWT 등)으로 인증하는 방식을 사용하기 때문
        http.sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // CSRF(Cross-Site Request Forgery) 보호 비활성화
        // -> CSRF 보호는 브라우저의 세션/쿠키 기반 인증을 전제로 하는데, 우리는 세션을 안 쓰므로(STATELESS) 불필요
        http.csrf(csrf -> csrf.disable());

        // 스프링 시큐리티 기본 로그인 폼/HTTP Basic 인증 화면 비활성화
        // -> 로그인은 폼이 아니라 UserController.login()에서 JSON으로 직접 처리하기 때문에, 기본 로그인 화면은 필요 없음
        http.formLogin(login -> login.disable());
        http.httpBasic(basic -> basic.disable());

        // JWT 검증 필터를 UsernamePasswordAuthenticationFilter보다 앞에 등록
        // -> 매 요청마다 이 필터가 먼저 실행되어 Authorization 헤더의 토큰을 검사한다 (JWTCheckFilter 참고)
        http.addFilterBefore(new JWTCheckFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        // 요청 경로별 인가(authorization) 규칙
        // 지금은 로그인/회원가입 API만 만든 단계라 특정 API를 "로그인 필요"로 강제하지는 않고 모두 permitAll로 열어둠
        // -> JWTCheckFilter는 이미 붙어있으니, 나중에 특정 API를 잠글 땐
        //    예) auth.requestMatchers("/api/containers/**").authenticated().anyRequest().permitAll() 처럼 세분화하면 된다
        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }

    // CORS 세부 정책을 정의하는 Bean
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));               // 모든 출처(도메인) 허용 (개발 단계용, 운영에서는 프론트 주소로 제한 권장)
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용할 HTTP 메서드
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));  // 허용할 요청 헤더
        configuration.setAllowCredentials(true); // 쿠키/인증정보 포함 요청 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로("/**")에 위 설정 적용
        return source;
    }

    // 비밀번호 암호화기 Bean
    // -> UserServiceImpl에서 passwordEncoder.encode(원본비밀번호) 형태로 주입받아 사용
    // BCrypt는 같은 입력이라도 매번 다른 암호화 결과(해시)를 만들어내는 단방향 해시 알고리즘이라
    // 원본 비밀번호를 복호화할 수 없고, 안전하게 저장/비교(matches())가 가능하다.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
