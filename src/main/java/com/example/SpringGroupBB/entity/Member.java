package com.example.SpringGroupBB.entity;


import com.example.SpringGroupBB.constant.Role;
import com.example.SpringGroupBB.constant.UserDel;
import com.example.SpringGroupBB.dto.MemberDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "member_id")
  private Long id;

  @Column(unique = true, length = 50)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(length = 15, nullable = false)
  private String name;

  @Column(length = 15, nullable = false)
  private String tel;

  private String address;

  private LocalDate birthday;

  private String profileImage;

  @CreatedDate
  private LocalDateTime wDate;



  @Enumerated(EnumType.STRING)
  private Role role;

  @Enumerated(EnumType.STRING)
  private UserDel userDel;

  private LocalDateTime delDate;

  public static Member dtoToEntity(MemberDTO dto, PasswordEncoder passwordEncoder) {
    return Member.builder()
            .email(dto.getEmail())
            .password(passwordEncoder.encode(dto.getPassword()))
            .name(dto.getName())
            .tel(dto.getTel())
            .address(dto.getAddress())
            .birthday(dto.getBirthday())
            .profileImage("noImage.jpg")
            .wDate(dto.getWDate())
            .role(Role.USER)
            .userDel(UserDel.NO)
            .build();
  }
}
