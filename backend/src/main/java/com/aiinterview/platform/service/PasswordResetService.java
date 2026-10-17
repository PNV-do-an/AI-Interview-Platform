package com.aiinterview.platform.service;

import com.aiinterview.platform.model.dto.response.PasswordResetResponse;
import com.aiinterview.platform.model.dto.response.ResetTokenInfoResponse;

public interface PasswordResetService {

    String forgotPassword(String email);

    ResetTokenInfoResponse getResetTokenInfo(String token);

    PasswordResetResponse resetPassword(String token, String newPassword, String confirmPassword,
                                       boolean signOutEverywhere);
}
