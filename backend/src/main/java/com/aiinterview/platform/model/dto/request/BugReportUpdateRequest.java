package com.aiinterview.platform.model.dto.request;

import lombok.Data;

@Data
public class BugReportUpdateRequest {

    private String status;

    private String reply;
}
