package com.aiinterview.platform.model.dto.request;

import lombok.Getter;
import lombok.Setter;

public class GithubLoginRequest {
    @Getter @Setter private String code;
    @Getter @Setter private String redirectUri;
}
