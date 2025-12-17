package com.example.SpringGroupBB.entity;


import com.example.SpringGroupBB.constant.Role;
import com.example.SpringGroupBB.constant.UserDel;
import com.example.SpringGroupBB.dto.MemberDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

  // cascade = 부모 객체가 삭제될 때 자식 객체도 삭제하겠다.
  // orphanRemoval 부모 객체에서 자식 객체를 삭제할 수 있도록 하겠다(부모 살아있는 상태에서 부모 객체를 통해 자식 객체 삭제).
  // 로그인 이력.
  @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<LoginHistory> loginHistories = new ArrayList<>();
  // QnA 보내는 사람 이메일.
  @OneToMany(mappedBy = "fromEmail", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<QnA> fromEmail = new ArrayList<>();
  // QnA 받는 사람 이메일.
  @OneToMany(mappedBy = "dearEmail", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<QnA> dearEmail = new ArrayList<>();
  // 게시판 글쓴이.
  @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Board> board = new ArrayList<>();
  // 댓글 댓글쓴이.
  @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<BoardReply> boardReply = new ArrayList<>();

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
