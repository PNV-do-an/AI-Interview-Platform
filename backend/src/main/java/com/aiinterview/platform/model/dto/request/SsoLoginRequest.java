package com.aiinterview.platform.model.dto.request;

import lombok.Getter;
import lombok.Setter;

public class SsoLoginRequest {
    @Getter @Setter private String code;
    @Getter @Setter private String redirectUri;
    /** nonce đã gửi trong authorize request — id_token phải mang lại đúng giá trị này */
    @Getter @Setter private String nonce;
}
