package com.example.SpringGroupBB.entity;

import com.example.SpringGroupBB.constant.OpenSW;
import com.example.SpringGroupBB.constant.Progress;
import com.example.SpringGroupBB.dto.QnADTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
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
public class QnA {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "qna_id")
  private Long id;
  @ColumnDefault("0")
  private Long parentId;
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  @JoinColumn(name = "from_email", referencedColumnName = "email", nullable = false)
  private MemberEntity fromEmail;
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  @JoinColumn(name = "dear_email", referencedColumnName = "email", nullable = false)
  private MemberEntity dearEmail;
  @Column(length = 20, nullable = false)
  private String title;
  @Lob
  @NotEmpty
  private String content;
  @Column(length = 2, nullable = false)
  @Enumerated(EnumType.STRING)
  @ColumnDefault("'NO'")
  private OpenSW openSW;
  @Column(length = 4, nullable = false)
  @Enumerated(EnumType.STRING)
  @ColumnDefault("'RESOLVING'")
  private Progress progress;
  @CreatedDate
  private LocalDateTime startDate;
  @CreatedDate
  private LocalDateTime lastDate;

  public static QnA dtoToEntity(MemberEntity fromEmail, MemberEntity dearEmail, QnADTO dto) {
    return QnA.builder()
            .parentId(dto.getParentId())
            .fromEmail(fromEmail)
            .dearEmail(dearEmail)
            .title(dto.getTitle())
            .content(dto.getContent())
            .openSW(OpenSW.NO)
            .progress(dto.getProgress()==null?Progress.RESOLVING:dto.getProgress())
            .startDate(dto.getStartDate())
            .lastDate(dto.getLastDate())
            .build();
  }
}
