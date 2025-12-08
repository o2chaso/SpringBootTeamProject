package com.example.SpringGroupBB.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "board_reply")
@Getter
@Setter
//@ToString(onlyExplicitlyIncluded = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
public class BoardReply {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "board_reply_id")
  private Long id;

  // 원본글의 PK를 FK로 설정
  @ManyToOne(fetch = FetchType.LAZY)
  @ToString.Exclude
  @JoinColumn(name = "board_id", referencedColumnName = "board_id")
  private Board board;

  // 부모댓글 관련: self join
  @ManyToOne(fetch = FetchType.LAZY)
  @ToString.Exclude
  @JoinColumn(name = "parent_id")
  private BoardReply parent; //부모 댓글

  @Column(nullable = false)
  private int ref;  // 원본글 번호(그룹번호)

  @Column(nullable = false)
  private int reStep; // 댓글 계층(레벨,들여쓰기)

  @Column(nullable = false)
  private int reOrder;

  @Column(length = 20, nullable = false)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @ToString.Exclude
  @JoinColumn(name = "email", referencedColumnName = "email")
  private Member member;

  @Lob
  @Column(nullable = false)
  private String content;

  @Column(length = 50, nullable = false)
  private String hostIp;

  @CreatedDate
  private LocalDateTime wDate;

  @ColumnDefault("'NO'")
  private String complaint;

  @Column(name="reply_sw", nullable = false)
  private int replySw; // 원본 댓글 여부(상태값): 1 = 원본댓글, 0=대댓글

}
