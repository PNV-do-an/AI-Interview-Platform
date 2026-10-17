package com.aiinterview.platform.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotBlank(message = "Reset token is required")
    private String token;

    @NotBlank(message = "New password is required")
    private String password;

    @NotBlank(message = "Confirmation password is required")
    private String confirmPassword;

    /** null hoặc true = vô hiệu toàn bộ phiên khác; chỉ false khi user bỏ chọn "Sign out of all other sessions" */
    private Boolean signOutEverywhere;
}
