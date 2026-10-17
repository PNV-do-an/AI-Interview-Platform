package com.aiinterview.platform.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Tài khoản ứng với link đặt lại mật khẩu còn hiệu lực — client hiển thị danh tính + thời gian còn lại. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetTokenInfoResponse {

    private String fullName;
    private String email;
    private String role;
    private long remainingSeconds;
}
