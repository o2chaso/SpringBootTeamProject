package com.example.SpringGroupBB.service;

import com.example.SpringGroupBB.constant.UserDel;
import com.example.SpringGroupBB.custom.CustomOAuth2User;
import com.example.SpringGroupBB.entity.Member;
import com.example.SpringGroupBB.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
    private final MemberRepository memberRepository;

    @Value("${app.oauth.kakao.require-email:true}")
    private boolean requireEmail;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

      OAuth2User oAuth2User = delegate.loadUser(userRequest);
      Map<String, Object> attributes = oAuth2User.getAttributes();

      String email = null;
      String nickname = null;

      try {
        Object kakaoAccountObj = attributes.get("kakao_account");
        if (kakaoAccountObj instanceof Map) {
          Map<?, ?> kakaoAccount = (Map<?, ?>) kakaoAccountObj;
          Object emailObj = kakaoAccount.get("email");
          if (emailObj instanceof String) email = (String) emailObj;

          Object profileObj = kakaoAccount.get("profile");
          if (profileObj instanceof Map) {
            Object nickObj = ((Map<?,?>)profileObj).get("nickname");
            if (nickObj instanceof String) nickname = (String) nickObj;
          }
        }
      } catch (Exception e) {
        log.warn("Failed to parse kakao attributes", e);
      }

      if (requireEmail && (email == null || email.isBlank())) {
        OAuth2Error error = new OAuth2Error("email_required",
                "Kakao did not return an email. Please allow email scope.", null);
        throw new OAuth2AuthenticationException(error);
      }

      Optional<Member> opMember = memberRepository.findByEmail(email);

      if(opMember.isPresent()) {
        Member member = opMember.get();

        if(member.getUserDel() == UserDel.OK) {
          throw new OAuth2AuthenticationException(new OAuth2Error("disabled_user", null, null));
        }
        return new CustomOAuth2User(oAuth2User, email, nickname);
      }
      else {
        ServletRequestAttributes attr =
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession();

        session.setAttribute("sKakaoEmail", email);
        session.setAttribute("sKakaoNickname", nickname);

        throw new OAuth2AuthenticationException(new OAuth2Error("new_user", null, null));
      }
    }
}