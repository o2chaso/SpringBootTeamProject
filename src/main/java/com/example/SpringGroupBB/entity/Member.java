package com.example.SpringGroupBB.entity;

import com.example.SpringGroupBB.constant.Role;
import com.example.SpringGroupBB.dto.MemberDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@DynamicInsert
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "member_id")
  private Long id;
  @Column(unique = true, nullable = false, length = 50)
  private String mid;
  @NotNull
  @Column(nullable = false)
  private String password;
  @Column(nullable = false, length = 20)
  private String name;
  @Enumerated(EnumType.STRING)
  private Role role;

  public static Member dtoToEntity(PasswordEncoder passwordEncoder, MemberDTO dto) {
    return Member.builder()
            .mid(dto.getMid())
            .password(passwordEncoder.encode(dto.getPassword()))
            .name(dto.getName())
            .role(Role.USER)
            .build();
  }
}
