package com.example.SpringGroupBB.config;


import com.example.SpringGroupBB.handler.CustomFailureHandler;
import com.example.SpringGroupBB.service.KakaoOAuth2UserService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class SecurityConfig {
  private final KakaoOAuth2UserService kakaoOAuth2UserService;
  private final CustomFailureHandler customFailureHandler;

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
            /*
              CookieCsrfTokenRepository: X-XSRF-TOKEN
              CSRF 토큰을 'XSRF-TOKEN' 이름의 쿠키로 클라이언트에 전달.
              클라이언트는 해당 값을 'XSRF-TOKEN' 헤더에 담아 서버로 전송해야 CSRF 검증 통과
              withHttpOnlyFalse() 설정 시 JavaScript에서도 쿠키 접근 가능(e.g., AJAX 요청 시 사용)
             */
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
            .formLogin(form -> form
              .loginPage("/member/memberLogin") // 커스텀 로그인 페이지
              .defaultSuccessUrl("/member/memberLoginOk", true)  // 로그인 성공 시 이동
              .failureHandler(customFailureHandler)
              .usernameParameter("email") // 로그인 form의 name="email"
              .permitAll())

            .oauth2Login(oauth -> oauth
              .userInfoEndpoint(userInfo -> userInfo
                      .userService(kakaoOAuth2UserService)
              )
              .defaultSuccessUrl("/member/memberLoginOk", true)
              .failureHandler(customFailureHandler)
            );

    // 페이지 접근 권한설정
    security.authorizeHttpRequests(request -> request
            // 비회원도 열람 가능(메인페이지, 회사소개, 기술소개, 상품 리스트).
            .requestMatchers("/", "/company/**", "/technology/**", "/product/**").permitAll()
            // 회원가입(memberLogin, memberLoginOk는 사용자 지정 로그인 폼에서 이미 허용처리 되어있음).
            .requestMatchers("/member/login/error", "/member/memberJoin", "/member/memberEmailCheck", "/member/memberEmailCheckOk", "/member/memberEmailCheckNo").permitAll()
            // 카카오로그인.
            .requestMatchers("/member/kakaoJoin", "/member/kakaoLogout", "/member/memberMidFind", "/member/memberPwdFind", "/member/memberPwdChange").permitAll()
            // 이미지 처리.
            .requestMatchers("/images/**", "/admin/product/**", "/js/**").permitAll()
            // 관리자 처리.
            .requestMatchers("/qna/qnaDelete", "/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated());

    // 권한 없는 user의 접근시 예외처리
    security.exceptionHandling(exception -> exception
            .accessDeniedPage("/error/accessDenied"));

    // 기본 로그아웃 처리
    security.logout(Customizer.withDefaults());

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
