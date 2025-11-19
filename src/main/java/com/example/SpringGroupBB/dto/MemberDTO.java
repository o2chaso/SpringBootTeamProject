package com.example.SpringGroupBB.dto;

import com.example.SpringGroupBB.entity.MemberEntity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDTO {

  private Long id;

  @NotEmpty(message = "이름은 필수 입력입니다.")
  @Length(min = 1, max = 20, message = "이름은 1~20글자 이하로 입력해주세요.")
  private String name;

  @NotEmpty(message = "메일은 필수 입력입니다.")
  @Email(message = "이메일 형식을 확인해주세요.")
  private String email;

  @NotEmpty(message = "비밀번호는 필수 입력입니다.")
  @Length(min = 4, max = 20, message = "비밀번호는 4~20글자 이하로 입력해주세요.")
  private String password;

  private String address;
  

  public static MemberDTO entityToDto(MemberEntity entity) {
    return MemberDTO.builder()
            .id(entity.getId())
            .name(entity.getName())
            .email(entity.getEmail())
            .address(entity.getAddress())
            .password(entity.getPassword())
            .build();

  }

}
