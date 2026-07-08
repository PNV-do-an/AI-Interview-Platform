# CHANGELOG - Auth Feature Implementation

## Summary
Implemented US-1.1 (Registration) and US-1.2 (Login) requirements into the standard project structure under `backend/` and `frontend/` folders, branch `refactor/auth-api21`.

## Backend Changes (9 files)

| File | Changes |
|------|---------|
| `User.java` | Added phone, verificationToken, verificationTokenExpiry, loginAttempts, lockUntil; enabled default = false |
| `Role.java` | Added ROLE_EMPLOYEE |
| `RegisterRequest.java` | Added phone, @Size(min=8), @Pattern for password strength |
| `AuthResponse.java` | Added phone to UserInfo |
| `UserRepository.java` | Added findByVerificationToken() |
| `AuthService.java` | Added refreshToken(), verifyEmail(), resendVerification() |
| `AuthServiceImpl.java` | Full auth logic: BCrypt, email verification (log URL), rate limit (5 attempts / 15min lock), Vietnamese error messages, refresh token |
| `AuthController.java` | Added POST /refresh, GET /verify, POST /resend-verification |
| `application.yml` | Added app.base-url config |

## Frontend Changes (7 files)

| File | Changes |
|------|---------|
| `Api.model.ts` | Added phone to AuthResponse and RegisterRequest |
| `User.model.ts` | Added phone, ROLE_EMPLOYEE |
| `http.service.ts` | Added refresh token queue, rememberMe storage support |
| `useAuthController.ts` | Added rememberMe, role-based redirect (Admin/Employee/User) |
| `AuthContext.tsx` | Updated login signature with rememberMe param |
| `LoginPage.tsx` | Added Remember Me checkbox, registered success message |
| `RegisterPage.tsx` | Added phone, confirmPassword, password strength indicator |

## Root (1 file created)
- `CHANGELOG.md`
