package com.aiinterview.platform.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Kết quả đặt lại mật khẩu — client dùng eventId và trạng thái đăng xuất mọi phiên cho màn xác nhận. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetResponse {
    private String eventId;
    private boolean signedOutEverywhere;
}
