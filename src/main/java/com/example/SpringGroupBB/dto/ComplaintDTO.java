package com.example.SpringGroupBB.dto;

import com.example.SpringGroupBB.entity.Complaint;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplaintDTO {
  private Long id;            // 인덱스 번호
  private String part;        // 신고된 곳
  private Long partId;         // 신고된 곳의 인덱스 번호
  private String cpName;       // 신고자
  private String cpContent;   // 신고내용
  private String cpDate;      // 신고된 날짜
  private String progress;    // 신고처리 상태 : 접수,처리완료 등

  public Complaint entityToDto() {
    return Complaint.builder()
            .id(id)
            .part(part)
            .partId(partId)
            .cpName(cpName)
            .cpContent(cpContent)
            .cpDate(cpDate)
            .progress(progress != null ? progress:"신고접수")
            .build();
  }
}
