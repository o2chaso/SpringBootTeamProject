package com.example.SpringGroupBB.dto;

import com.example.SpringGroupBB.entity.LoginHistory;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginHistoryDTO {

  private Long id;
  private Long memberId;
  private String ip;
  private String loginMethod;
  private LocalDateTime createDate;

  private String memberName;
  private String memberEmail;

  public static LoginHistoryDTO entityToDto(LoginHistory loginHistory) {
    return LoginHistoryDTO.builder()
            .id(loginHistory.getId())
            .memberId(loginHistory.getMember().getId())
            .memberName(loginHistory.getMember().getName())
            .memberEmail(loginHistory.getMember().getEmail())
            .ip(loginHistory.getIp())
            .loginMethod(loginHistory.getLoginMethod())
            .createDate(loginHistory.getCreateDate())
            .build();
  }
}
