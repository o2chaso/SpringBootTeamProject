package com.example.SpringGroupBB.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "complaint")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
public class Complaint {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id; // 신고 pk

  @Column(nullable = false, length = 20)
  private String part; // 신고된 곳(ex.게시판에서 신고했으면 board, 게시판댓글에서 신고했으면 boardReply)

  @Column(nullable = false)
  private Long partId; // 신고된 글 번호(id)

  @Column(nullable = false, length = 50)
  private String cpName; // 신고자 이름

  @Column(nullable = false, length = 1000)
  private String cpContent; // 신고내용

  @CreatedDate
  private String cpDate;    // 신고한 날짜

  @Column(nullable = false, length = 20)
  @Builder.Default
  private String progress = "신고접수"; // 신고진행상황(ex.신고접수 - 처리중 - 처리완료)

  @Transient
  private long hourDiff;

  @Transient
  private long dateDiff;

  @Transient
  private long replyCnt;



}
