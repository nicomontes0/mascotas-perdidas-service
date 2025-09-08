package com.mascotasperdidas.controller.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mascotasperdidas.model.Notice;
import com.mascotasperdidas.model.UserReport;
import com.mascotasperdidas.model.enums.ReportReason;
import lombok.Data;

import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportRequestBody {
    private UUID noticeId;
    private String reason;
    private String details;

    public UserReport toDomain(Notice notice) {
        return UserReport.builder()
                .reportedNotice(notice)
                .reason(ReportReason.valueOf(reason))
                .details(details)
                .build();
    }
}
