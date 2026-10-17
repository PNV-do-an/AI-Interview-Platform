package com.aiinterview.platform.model.dto.response;

import lombok.Builder;
import lombok.Getter;

/** Thông tin SSO công khai cho frontend dựng authorize URL (không chứa secret). */
@Getter
@Builder
public class SsoConfigResponse {
    private boolean enabled;
    private String authorizationEndpoint;
    private String clientId;
    private String scopes;
}
