package com.example.SpringGroupBB.entity;

import com.example.SpringGroupBB.dto.CompanyDTO;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "company")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "company_id")
  private Long Id;

  @Column(name = "company_name", nullable = false)
  private String companyName;

  @Column(name = "company_code", nullable = false)
  private String companyCode;

  @Column(name = "company_address", nullable = false)
  private String companyAddress;

  @Column(name = "company_email", nullable = false, length = 50)
  private String companyEmail;

  @Column(name = "company_time")
  @CreatedDate
  private LocalDateTime companyTime;

  // Sensor와 양방향 연관관계 설정
  @OneToMany(mappedBy = "company", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @ToString.Exclude
  @Builder.Default
  private List<SensorEntity> sensors = new ArrayList<>();

  // DTO To Entity
  public static CompanyEntity dtoToEntity(CompanyDTO dto) {
    return CompanyEntity.builder()
            .companyName(dto.getCompanyName())
            .companyCode(dto.getCompanyCode())
            .companyAddress(dto.getCompanyAddress())
            .companyEmail(dto.getCompanyEmail())
            .companyTime(dto.getCompanyTime())
            .build();
  }
}
