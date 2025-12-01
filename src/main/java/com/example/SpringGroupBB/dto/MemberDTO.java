package com.example.SpringGroupBB.dto;

import com.example.SpringGroupBB.constant.Role;
import com.example.SpringGroupBB.constant.UserDel;
import com.example.SpringGroupBB.entity.Member;
import com.example.SpringGroupBB.validation.CreateGroup;
import com.example.SpringGroupBB.validation.UpdateGroup;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDTO {

  private Long id;

  @NotEmpty(message = "메일은 필수 입력입니다.", groups = CreateGroup.class)
  @Email(message = "이메일 형식으로 입력해주세요", groups = CreateGroup.class)
  private String email;

  @Pattern(regexp = "^[a-zA-Z0-9]{4,16}$", message = "비밀번호는 4~16자의 영문 대소문자와 숫자를 사용할 수 있습니다.", groups = CreateGroup.class)
  @NotEmpty(message = "비밀번호는 필수 입력입니다.",
          groups = CreateGroup.class)
  private String password;

  @Length(min = 2, max = 15, message = "이름은 2~15자 이하로 입력해 주세요",
          groups = {UpdateGroup.class, CreateGroup.class})
  @NotEmpty(message = "이름은 필수 입력입니다.",
          groups = {UpdateGroup.class, CreateGroup.class})
  private String name;

  @NotEmpty(message = "전화번호는 필수 입력입니다.", groups = {UpdateGroup.class, CreateGroup.class})
  @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "전화번호 형식에 맞지 않습니다.",
          groups = {UpdateGroup.class, CreateGroup.class})
  private String tel;

  private String address;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate birthday;
  private String profileImage;
  private LocalDateTime wDate;
  private Role role;
  private UserDel userDel;
  private LocalDateTime delDate;

  private String tel2;
  private String tel3;

  private String postcode;
  private String roadAddress;
  private String detailAddress;
  private String extraAddress;

  public static MemberDTO entityToDto(Member member) {
    return MemberDTO.builder()
            .id(member.getId())
            .email(member.getEmail())
            .password(member.getPassword())
            .name(member.getName())
            .tel(member.getTel())
            .address(member.getAddress())
            .birthday(member.getBirthday())
            .profileImage(member.getProfileImage())
            .wDate(member.getWDate())
            .role(member.getRole())
            .userDel(member.getUserDel())
            .delDate(member.getDelDate())
            .build();
  }
}
