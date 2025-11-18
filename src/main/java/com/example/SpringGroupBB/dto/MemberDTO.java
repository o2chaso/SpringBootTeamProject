package com.example.SpringGroupBB.dto;

import com.example.SpringGroupBB.constant.Role;
import com.example.SpringGroupBB.entity.Member;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.Optional;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDTO {
  private Long id;
  @NotEmpty(message = "아이디를 입력해주세요.")
  private String mid;
  @NotEmpty(message = "비밀번호를 입력해주세요.")
  @Length(min = 4, max = 255, message = "비밀번호는 최소 4글자 이상 입력해주세요.")
  private String password;
  @NotEmpty(message = "이름을 입력해주세요.")
  @Length(min = 2, max = 20, message = "이름은 2~20글자 사이로 입력해주세요.")
  private String name;
  private Role role;

  public static MemberDTO entityToDTO(Optional<Member> member) {
    return MemberDTO.builder()
            .id(member.get().getId())
            .mid(member.get().getMid())
            .password(member.get().getPassword())
            .name(member.get().getName())
            .role(member.get().getRole())
            .build();
  }
}
