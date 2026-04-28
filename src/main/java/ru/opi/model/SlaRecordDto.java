package ru.opi.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SlaRecordDto {
    private Integer id;
    private Integer engineerId;
    private String engineerName;
    private Integer requestId;
    private String requestSubject;
    private LocalDateTime plannedStart;
    private LocalDateTime plannedEnd;
    private LocalDateTime actualStart;
    private LocalDateTime actualEnd;
    private Boolean slaForecast;

    public static SlaRecordDto fromEntity(SlaRecord sla) {
        if (sla == null) return null;
        SlaRecordDto dto = new SlaRecordDto();
        dto.setId(sla.getId());
        dto.setPlannedStart(sla.getPlannedStart());
        dto.setPlannedEnd(sla.getPlannedEnd());
        dto.setActualStart(sla.getActualStart());
        dto.setActualEnd(sla.getActualEnd());
        dto.setSlaForecast(sla.getSlaForecast());

        if (sla.getEngineer() != null) {
            dto.setEngineerId(sla.getEngineer().getId());
            dto.setEngineerName(sla.getEngineer().getFio());
        }

        if (sla.getRequest() != null) {
            dto.setRequestId(sla.getRequest().getId());
            dto.setRequestSubject(sla.getRequest().getSubject());
        }

        return dto;
    }
}