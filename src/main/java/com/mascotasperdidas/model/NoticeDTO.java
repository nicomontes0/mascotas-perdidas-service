package com.mascotasperdidas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class NoticeDTO {
    private UUID noticeId;
    private String token;
    @JsonIgnore
    private String title;
    @JsonIgnore
    private Map<String,String> contactInfo;
}
