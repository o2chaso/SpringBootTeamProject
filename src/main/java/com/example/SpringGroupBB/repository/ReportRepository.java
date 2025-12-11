package com.example.SpringGroupBB.repository;

import com.example.SpringGroupBB.entity.ReportSave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ReportRepository extends JpaRepository<ReportSave, Long> {
  ReportSave findBySaveReportDateAndSaveReportRestDateAndDeviceCode(LocalDateTime measureDatetime, LocalDateTime restDay, String deviceCode);

  ReportSave findBySaveReportDateAndDeviceCodeAndReport(LocalDateTime measureDatetime, String deviceCode, String report);
}
