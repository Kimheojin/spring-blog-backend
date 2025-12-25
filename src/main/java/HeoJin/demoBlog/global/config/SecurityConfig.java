package HeoJin.demoBlog.global.config;


import HeoJin.demoBlog.global.filter.JwtAuthenticationFilter;
import HeoJin.demoBlog.global.util.CustomUtil;
import HeoJin.demoBlog.member.service.CustomUserDetailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;
import java.util.Map;
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailService customUserDetailService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
                                                   ObjectMapper objectMapper,
                                                   AuthenticationManager authenticationManager) throws Exception {
        httpSecurity
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login", "/api/auth/logout",
                                "/api/categories", "/api/categories/stats",
                                "/api/posts/*/comments", "/api/posts/comments", "/api/comments",
                                "/api/posts", "/api/posts/single", "/api/posts/category",
                                "/api/tag/list", "/api/tag/postlist", "/api/health", "/api/seo/unified-search"
                                ,"/api/prometheus")
                        .permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 이거 filter 단위라 controllerAdvice에 안잡힘
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                handleSecurityException(response, objectMapper, "인증이 필요합니다.",
                                        HttpServletResponse.SC_UNAUTHORIZED, "AUTHENTICATION_REQUIRED"))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                handleSecurityException(response, objectMapper, "접근 권한이 없습니다.",
                                        HttpServletResponse.SC_FORBIDDEN, "ACCESS_DENIED")));


        return httpSecurity.build();
    }

    // 공통 예외 처리 메소드
    private void handleSecurityException(HttpServletResponse response, ObjectMapper objectMapper,
                                         String message, int statusCode, String code) {
        try {
            response.setStatus(statusCode);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", message);
            errorResponse.put("statusCode", statusCode);
            errorResponse.put("code", code);

            CustomUtil.setUTF(response).getWriter().write(objectMapper.writeValueAsString(errorResponse));
        } catch (Exception e) {
            throw new RuntimeException("Security exception handling failed", e);
        }
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}