package com.example.SpringGroupBB.entity;

import com.example.SpringGroupBB.dto.LoginHistoryDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
public class LoginHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "loginRecode_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id", referencedColumnName = "member_id")
  private Member member;

  @Column(length = 45)
  private String ip;

  @Column(length = 10)
  private String loginMethod;

  @CreatedDate
  @Column(nullable = false)
  private LocalDateTime createDate;

  public static LoginHistory dtoToEntity(LoginHistoryDTO dto, Member member) {
    return LoginHistory.builder()
            .member(member)
            .ip(dto.getIp())
            .loginMethod(dto.getLoginMethod())
            .createDate(dto.getCreateDate())
            .build();
  }
}
