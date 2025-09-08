package com.mascotasperdidas.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class NoticeDTO {
    private UUID noticeId;
    private String token;
}
