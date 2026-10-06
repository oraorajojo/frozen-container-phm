package com.singsing.frozenapi.security.filter;

import com.singsing.frozenapi.util.CustomJWTException;
import com.singsing.frozenapi.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

// 요청마다 한 번씩 실행되며 Authorization 헤더의 JWT를 검사하는 필터
// (CustomSecurityConfig에서 UsernamePasswordAuthenticationFilter 앞에 등록해서 사용)
//
// 지금은 CustomSecurityConfig의 authorizeHttpRequests가 전부 permitAll이라,
// 이 필터가 있어도 토큰이 없거나 잘못됐다고 해서 요청 자체를 막지는 않는다.
// 토큰이 유효하면 SecurityContext에 인증 정보를 세팅해두기만 하고,
// 나중에 특정 API를 "로그인 필요"로 잠글 때(authorizeHttpRequests에서 authenticated()로 변경) 그대로 동작하게 된다.
@Slf4j
@RequiredArgsConstructor // JWTUtil을 생성자로 주입받음 (CustomSecurityConfig에서 new JWTCheckFilter(jwtUtil) 형태로 직접 생성)
public class JWTCheckFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    // 로그인 전에 호출돼야 하는 API들은 이 필터 자체를 건너뛴다 (토큰이 없는 게 당연한 요청이므로)
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/api/users/signup")
                || path.equals("/api/users/login")
                || path.equals("/api/users/refresh");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // "Bearer <토큰>" 형식일 때만 검증 시도. 없으면 그냥 다음 필터로 진행 (permitAll이라 막을 필요 없음)
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Map<String, Object> claims = jwtUtil.validateToken(token);
                String email = (String) claims.get("email");
                String role = (String) claims.get("role");

                // Spring Security의 hasRole("STAFF") 같은 표현식이 내부적으로 "ROLE_" 접두사를 기대하기 때문에 붙여준다
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        email, null, List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (CustomJWTException e) {
                // 지금은 permitAll이라 여기서 요청을 막지 않고, 인증 정보 없이 그냥 통과시킨다.
                // (나중에 특정 API에 인증을 강제하면, SecurityContext에 인증 정보가 없어서 자동으로 401/403 처리됨)
                log.info("JWT 검증 실패 (인증 없이 통과): {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

}
