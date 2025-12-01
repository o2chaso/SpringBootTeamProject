package com.example.SpringGroupBB.custom;

import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
@ToString
public class CustomOAuth2User implements OAuth2User {

  private final OAuth2User oAuth2User;
  private final String email;
  private final String nickname;

  public CustomOAuth2User(OAuth2User user, String email, String nickname) {
    this.oAuth2User = user;
    this.email = email;
    this.nickname = nickname;
  }
  @Override
  public Map<String, Object> getAttributes() {
    return oAuth2User.getAttributes();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return oAuth2User != null ? oAuth2User.getAuthorities()
            : List.of(new SimpleGrantedAuthority("ROLE_USER"));
  }

  @Override
  public String getName() {
    return email;
  }
}
