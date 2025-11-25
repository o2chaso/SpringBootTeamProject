package com.example.SpringGroupBB.dto;

import com.example.SpringGroupBB.validation.CreateGroup;
import com.example.SpringGroupBB.validation.UpdateGroup;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoDTO {

  private Long id;
  private String email;
  private String name;

  @Pattern(regexp = "^[a-zA-Z0-9]{4,16}$", message = "비밀번호는 4~16자의 영문 대소문자와 숫자를 사용할 수 있습니다.", groups = CreateGroup.class)
  @NotEmpty(message = "비밀번호는 필수 입력입니다.",
          groups = CreateGroup.class)
  private String password;

  @NotEmpty(message = "전화번호는 필수 입력입니다.", groups = {UpdateGroup.class, CreateGroup.class})
  @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식에 맞지 않습니다.",
          groups = {UpdateGroup.class, CreateGroup.class})
  private String tel;

  private String address;
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate birthday;
}
