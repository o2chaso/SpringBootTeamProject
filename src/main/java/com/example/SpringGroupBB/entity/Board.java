package com.example.SpringGroupBB.entity;

import com.example.SpringGroupBB.dto.BoardDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "board")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
public class Board {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "board_id")
  private Long id;

  @Column(length = 20, nullable = false)
  private String name;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "email", referencedColumnName = "email")
  private Member member;

  @Column(length = 100, nullable = false)
  private String title;

  @Lob
  @NotNull
  private String content;

  @Column(length = 40, nullable = false)
  private String hostIp;

  @ColumnDefault("'OK'")
  private String openSw;

  @ColumnDefault("'OK'")
  private String noticeSw; // 공지사항 여부

  @ColumnDefault("0")
  private int readNum;

  @CreatedDate
  private LocalDateTime wDate;

  @ColumnDefault("0")
  private int good;

  @ColumnDefault("'NO'")
  private String complaint;

  // 댓글과의 연관관계설정
  @ToString.Exclude
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<BoardReply> boardReplies = new ArrayList<>();

  // JPA 영속적이지 않는 필드로 계산시에만 사용하기위한 필드 선언 : @Transient
  @Transient
  private long hourDiff;

  @Transient
  private long dateDiff;

  @Transient
  private long replyCnt;

  // dto to entity
  public static Board dtoToEntity(BoardDTO dto, Member member) {
    return Board.builder()
            .id(dto.getId())
            .name(dto.getName())
            .member(member)
            .title(dto.getTitle())
            .content(dto.getContent())
            .hostIp(dto.getHostIp())
            .openSw(dto.getOpenSw())
            .noticeSw(dto.getNoticeSw())
            .readNum(dto.getReadNum())
            .wDate(dto.getWDate())
            .good(dto.getGood())
            .complaint(dto.getComplaint())
            .build();
  }

}
