package com.example.SpringGroupBB.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomFailureHandler implements AuthenticationFailureHandler {

  @Override
  public void onAuthenticationFailure(HttpServletRequest request,
                                      HttpServletResponse response,
                                      AuthenticationException exception)
          throws IOException, ServletException {
    String errorMsg = null;
    String redirectUrl = "/SpringGroupBB/member/login/error";

    if (exception instanceof OAuth2AuthenticationException oauthEx) {
      String errorCode = oauthEx.getError().getErrorCode();
      if ("new_user".equals(errorCode)) {
        request.getSession().setAttribute("sMessage", "회원가입 후 사용 가능합니다.");
        redirectUrl = "/SpringGroupBB/member/kakaoJoin";
      }
      else if ("disabled_user".equals(errorCode)) {
        errorMsg = "탈퇴된 계정입니다.";
      }
    }
    else if (exception instanceof DisabledException) {
      errorMsg = "탈퇴된 계정입니다.";
    }

    if(errorMsg != null) {
      request.getSession().setAttribute("sErrorMsg", errorMsg);
    }
    response.sendRedirect(redirectUrl);
  }
}