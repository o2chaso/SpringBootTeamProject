package com.example.SpringGroupBB.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {
    // CsrfTokenRequestAttributeHandler: 클라이언트 요청(request)에서 CSRF 토큰을 읽고 검증하는 역할을 담당하는 객체
    CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
    // 요청(request)에 _csrf라는 이름으로 토큰 값을 저장하고 처리
    requestHandler.setCsrfRequestAttributeName("_csrf");


    // 사용자가 만든 로그인폼에 대해서만 허용처리
    security
            .csrf(csrf -> csrf
            .csrfTokenRequestHandler(requestHandler)
            .ignoringRequestMatchers("/ckeditor/imageUpload", "/member/memberDelete", "/sensor/sensorList/sse")
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
            /*
              CookieCsrfTokenRepository: X-XSRF-TOKEN
              CSRF 토큰을 'XSRF-TOKEN' 이름의 쿠키로 클라이언트에 전달.
              클라이언트는 해당 값을 'XSRF-TOKEN' 헤더에 담아 서버로 전송해야 CSRF 검증 통과
              withHttpOnlyFalse() 설정 시 JavaScript에서도 쿠키 접근 가능(e.g., AJAX 요청 시 사용)
             */

            .formLogin(form -> form
            .loginPage("/member/memberLogin") // 커스텀 로그인 페이지
            .defaultSuccessUrl("/member/memberLoginOk", true)  // 로그인 성공 시 이동
            .failureUrl("/member/login/error")  // 실패 시 이동
            .usernameParameter("email") // 로그인 form의 name="email"
            .permitAll());

    // 페이지 접근 권한설정
    security.authorizeHttpRequests(request -> request
            .requestMatchers("/", "/images/**", "/css/**", "/ckeditor/**", "/ckeditorUpload/**").permitAll()
            .requestMatchers("/board/replyInput", "/board/replyDelete", "/member/memberDelete", "/member/memberPasswordChange").hasAnyAuthority("USER", "ADMIN")
            .requestMatchers("/member/memberProfileUpdate").hasAnyAuthority("USER", "ADMIN")
            .requestMatchers("/sensor/**", "/sensor/sensorList/sse", "/board/List", "/board/boardContent").permitAll()
            .requestMatchers("/member/memberJoin", "/member/memberLogin", "/member/login/error").permitAll()
            .requestMatchers("/member/memberMain").authenticated()
            .anyRequest().authenticated());

    // 기본 로그아웃 처리
    security.logout(Customizer.withDefaults());
    
    // 권한 없는 user의 접근시 예외처리
    security.exceptionHandling(exception -> exception
            .accessDeniedPage("/error/accessDenied"));


    // iframe 보안 정책(x-frame-options)
    security.headers(headers -> headers
            .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)); // 같은 도메인 내에서는 iframe 허용

    return security.build();
  }

  @Bean
  public PasswordEncoder encoder() {
    return new BCryptPasswordEncoder();
  }
}
