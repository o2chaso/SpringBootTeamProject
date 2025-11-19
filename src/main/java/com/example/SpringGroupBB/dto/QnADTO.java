package com.example.SpringGroupBB.dto;

import com.example.SpringGroupBB.constant.OpenSW;
import com.example.SpringGroupBB.constant.Progress;
import com.example.SpringGroupBB.entity.QnA;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QnADTO {
  private Long id;
  private Long parentId;
  @NotEmpty(message = "보내는 사람의 아이디는 필수입니다.")
  private String fromEmail;
  @NotEmpty(message = "받는 사람의 아이디는 필수입니다.")
  private String dearEmail;
  @NotEmpty(message = "문의 제목을 입력해주세요.")
  @Length(min = 2, max = 20, message = "문의 제목은 2~20글자 사이로 입력해주세요.")
  private String title;
  @NotEmpty(message = "문의 내용을 입력해주세요.")
  private String content;
  private OpenSW openSW;
  private Progress progress;
  private LocalDateTime startDate;
  private LocalDateTime lastDate;

  public static QnADTO entityToDTO(QnA qna) {
    return QnADTO.builder()
            .id(qna.getId())
            .parentId(qna.getParentId())
            .fromEmail(qna.getFromEmail().getEmail())
            .dearEmail(qna.getDearEmail().getEmail())
            .title(qna.getTitle())
            .content(qna.getContent())
            .openSW(qna.getOpenSW())
            .progress(qna.getProgress())
            .startDate(qna.getStartDate())
            .lastDate(qna.getLastDate())
            .build();
  }
}
