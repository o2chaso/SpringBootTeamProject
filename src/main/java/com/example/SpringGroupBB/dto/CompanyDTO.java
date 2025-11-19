package com.example.SpringGroupBB.dto;

import com.example.SpringGroupBB.entity.CompanyEntity;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyDTO {
  private Long Id;

  @NotEmpty(message = "회사 명은 필수 입력입니다.")
  private String companyName;

  @NotEmpty(message = "회사 구분은 필수 입력입니다.")
  private String companyCode;

  @NotEmpty(message = "사업장 위치는 필수 입력입니다.")
  private String companyAddress;

  @NotEmpty(message = "회사 이메일은 필수 입력입니다.")
  @Email(message = "이메일 형식을 확인해주세요.")
  private String companyEmail;

  private LocalDateTime companyTime;

  // Entity To DTO
  public static CompanyDTO entityToDTO(CompanyEntity entity) {
    return CompanyDTO.builder()
            .Id(entity.getId())
            .companyName(entity.getCompanyName())
            .companyCode(entity.getCompanyCode())
            .companyAddress(entity.getCompanyAddress())
            .companyEmail(entity.getCompanyEmail())
            .companyTime(entity.getCompanyTime())
            .build();
  }
}
