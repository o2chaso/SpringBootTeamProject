package com.example.SpringGroupBB.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthFailureHandler implements AuthenticationFailureHandler {
  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                      AuthenticationException exception) throws IOException, ServletException {
    String errorMsg = "";

    if(exception instanceof UsernameNotFoundException) {
      errorMsg = exception.getMessage();
    }
    else if(exception instanceof BadCredentialsException) {
      errorMsg = "비밀번호가 일치하지 않습니다.";
    }
    String encodedMsg = java.net.URLEncoder.encode(errorMsg, java.nio.charset.StandardCharsets.UTF_8);

    response.sendRedirect("/member/login/error?errorMsg=" + encodedMsg);
  }
}
