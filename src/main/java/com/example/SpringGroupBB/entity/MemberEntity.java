package com.example.SpringGroupBB.entity;

import com.example.SpringGroupBB.dto.MemberDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "member_test")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberEntity {

  @Id
  @Column(name = "member_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 20)
  private String name;

  @Column(unique = true, nullable = false, length = 50)
  private String email;

  @NotNull
  private String password;

  private String address;

  public static MemberEntity dtoToEntity(MemberDTO dto, PasswordEncoder passwordEncoder) {
    return MemberEntity.builder()
            .name(dto.getName())
            .email(dto.getEmail())
            .password(passwordEncoder.encode(dto.getPassword()))
            .address(dto.getAddress())
            .build();
  }

}
