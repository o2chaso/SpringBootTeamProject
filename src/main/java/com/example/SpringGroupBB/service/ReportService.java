package com.example.SpringGroupBB.service;

import com.example.SpringGroupBB.dto.ReportSaveDTO;
import com.example.SpringGroupBB.dto.SensorDTO;
import com.example.SpringGroupBB.entity.ReportSave;
import com.example.SpringGroupBB.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
  private final ReportRepository reportRepository;

  public List<ReportSaveDTO> selectAllReportList() {
    return ReportSaveDTO.entityListToDTOList(reportRepository.findAll());
  }

  public ReportSaveDTO selectReportID(Long id) {
    return ReportSaveDTO.entityToDTO(reportRepository.findById(id).orElseThrow());
  }

  public ReportSave selectReportDate(String measureDatetime, String restDay, String deviceCode) {
    return reportRepository.findBySaveReportDateAndSaveReportRestDateAndDeviceCode(LocalDateTime.parse(measureDatetime+"T00:00:00"), LocalDateTime.parse(restDay+"T00:00:00"), deviceCode);
  }

  public void insertReportSave(List<SensorDTO> sensorList, LocalDateTime measureDatetime, LocalDateTime measureDatetimePast, String deviceCode, int flag) {
    String values = "", events = "", measureDatetimes = "", alarms = "", warnings = "";
    for(int i=0; i<sensorList.size(); i++) {
      values += sensorList.get(i).getEventValue() + "/";
      events += sensorList.get(i).getEvent() + "/";
      measureDatetimes += sensorList.get(i).getEventMeasureDatetime() + "/";
      alarms += sensorList.get(i).getAlarm() + ",";
      warnings += sensorList.get(i).getWarning() + ",";
    }
    values = values.substring(0, values.length()-1);
    events = events.substring(0, events.length()-1);
    measureDatetimes = measureDatetimes.substring(0, measureDatetimes.length()-1);
    alarms = alarms.substring(0, alarms.length()-1);
    warnings = warnings.substring(0, warnings.length()-1);

    ReportSave entity = ReportSave.builder()
            .saveReportDate(measureDatetime)
            .saveReportRestDate(measureDatetimePast)
            .deviceCode(deviceCode)
            .report(flag==0?"일일":flag==1?"주간":"월간")
            .value1MinData(sensorList.get(0).getMinData())
            .value1AvgData(sensorList.get(0).getAvgData())
            .value1MaxData(sensorList.get(0).getMaxData())
            .value1EventData(sensorList.get(0).getEventData())
            .value1MinRate(sensorList.get(0).getMinRate())
            .value1AvgRate(sensorList.get(0).getAvgRate())
            .value1MaxRate(sensorList.get(0).getMaxRate())
            .value1EventRate(sensorList.get(0).getEventRate())
            .value2MinData(sensorList.get(1).getMinData())
            .value2AvgData(sensorList.get(1).getAvgData())
            .value2MaxData(sensorList.get(1).getMaxData())
            .value2EventData(sensorList.get(1).getEventData())
            .value2MinRate(sensorList.get(1).getMinRate())
            .value2AvgRate(sensorList.get(1).getAvgRate())
            .value2MaxRate(sensorList.get(1).getMaxRate())
            .value2EventRate(sensorList.get(1).getEventRate())
            .value3MinData(sensorList.get(2).getMinData())
            .value3AvgData(sensorList.get(2).getAvgData())
            .value3MaxData(sensorList.get(2).getMaxData())
            .value3EventData(sensorList.get(2).getEventData())
            .value3MinRate(sensorList.get(2).getMinRate())
            .value3AvgRate(sensorList.get(2).getAvgRate())
            .value3MaxRate(sensorList.get(2).getMaxRate())
            .value3EventRate(sensorList.get(2).getEventRate())
            .value4MinData(sensorList.get(3).getMinData())
            .value4AvgData(sensorList.get(3).getAvgData())
            .value4MaxData(sensorList.get(3).getMaxData())
            .value4EventData(sensorList.get(3).getEventData())
            .value4MinRate(sensorList.get(3).getMinRate())
            .value4AvgRate(sensorList.get(3).getAvgRate())
            .value4MaxRate(sensorList.get(3).getMaxRate())
            .value4EventRate(sensorList.get(3).getEventRate())
            .value5MinData(sensorList.get(4).getMinData())
            .value5AvgData(sensorList.get(4).getAvgData())
            .value5MaxData(sensorList.get(4).getMaxData())
            .value5EventData(sensorList.get(4).getEventData())
            .value5MinRate(sensorList.get(4).getMinRate())
            .value5AvgRate(sensorList.get(4).getAvgRate())
            .value5MaxRate(sensorList.get(4).getMaxRate())
            .value5EventRate(sensorList.get(4).getEventRate())
            .value6MinData(sensorList.get(5).getMinData())
            .value6AvgData(sensorList.get(5).getAvgData())
            .value6MaxData(sensorList.get(5).getMaxData())
            .value6EventData(sensorList.get(5).getEventData())
            .value6MinRate(sensorList.get(5).getMinRate())
            .value6AvgRate(sensorList.get(5).getAvgRate())
            .value6MaxRate(sensorList.get(5).getMaxRate())
            .value6EventRate(sensorList.get(5).getEventRate())
            .value7MinData(sensorList.get(6).getMinData())
            .value7AvgData(sensorList.get(6).getAvgData())
            .value7MaxData(sensorList.get(6).getMaxData())
            .value7EventData(sensorList.get(6).getEventData())
            .value7MinRate(sensorList.get(6).getMinRate())
            .value7AvgRate(sensorList.get(6).getAvgRate())
            .value7MaxRate(sensorList.get(6).getMaxRate())
            .value7EventRate(sensorList.get(6).getEventRate())
            .value8MinData(sensorList.get(7).getMinData())
            .value8AvgData(sensorList.get(7).getAvgData())
            .value8MaxData(sensorList.get(7).getMaxData())
            .value8EventData(sensorList.get(7).getEventData())
            .value8MinRate(sensorList.get(7).getMinRate())
            .value8AvgRate(sensorList.get(7).getAvgRate())
            .value8MaxRate(sensorList.get(7).getMaxRate())
            .value8EventRate(sensorList.get(7).getEventRate())
            .value9MinData(sensorList.get(8).getMinData())
            .value9AvgData(sensorList.get(8).getAvgData())
            .value9MaxData(sensorList.get(8).getMaxData())
            .value9EventData(sensorList.get(8).getEventData())
            .value9MinRate(sensorList.get(8).getMinRate())
            .value9AvgRate(sensorList.get(8).getAvgRate())
            .value9MaxRate(sensorList.get(8).getMaxRate())
            .value9EventRate(sensorList.get(8).getEventRate())
            .value10MinData(sensorList.get(9).getMinData())
            .value10AvgData(sensorList.get(9).getAvgData())
            .value10MaxData(sensorList.get(9).getMaxData())
            .value10EventData(sensorList.get(9).getEventData())
            .value10MinRate(sensorList.get(9).getMinRate())
            .value10AvgRate(sensorList.get(9).getAvgRate())
            .value10MaxRate(sensorList.get(9).getMaxRate())
            .value10EventRate(sensorList.get(9).getEventRate())
            .eventValue(values)
            .event(events)
            .eventMeasureDatetime(measureDatetimes)
            .alarm(alarms)
            .warning(warnings)
            .build();
    reportRepository.save(entity);
  }

  public ReportSaveDTO selectReportSaveDateDeviceCodeFlag(String measureDatetime, String deviceCode, int flag) {
    String report = flag==0?"일일":flag==1?"주간":"월간";
    try {
      return ReportSaveDTO.entityToDTO(reportRepository.findBySaveReportDateAndDeviceCodeAndReport(LocalDateTime.parse(measureDatetime+"T00:00:00"), deviceCode, report));
    } catch (Exception e) {return null;}
  }

  public void deleteReportID(Long id) {
    reportRepository.deleteById(id);
  }
}
