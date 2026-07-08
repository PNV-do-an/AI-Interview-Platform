# Auth Feature Implementation

## US-1.1: Khách hàng đăng ký tài khoản mới
## US-1.2: Khách hàng đăng nhập vào hệ thống

---

## Backend Changes (`backend/`)

### 1. `model/entity/User.java` — Updated entity
| Thay đổi | Mô tả |
|----------|-------|
| **Added `phone` field** | Lưu số điện thoại người dùng |
| **Added `verificationToken`** | Token xác thực email (UUID) |
| **Added `verificationTokenExpiry`** | Thời hạn token (24 giờ) |
| **Added `loginAttempts`** | Đếm số lần đăng nhập thất bại |
| **Added `lockUntil`** | Thời gian khóa tài khoản |
| **Changed `enabled` default** | `true` → `false` (chờ kích hoạt email) |

### 2. `model/enums/Role.java` — Added role
| Thay đổi | Mô tả |
|----------|-------|
| **Added `ROLE_EMPLOYEE`** | Phân quyền Nhân viên (theo yêu cầu) |

### 3. `model/dto/request/RegisterRequest.java` — Updated DTO
| Thay đổi | Mô tả |
|----------|-------|
| **Added `phone` field** | Nhận số điện thoại từ form đăng ký |
| **`@Size(min=6)` → `@Size(min=8)`** | Yêu cầu mật khẩu tối thiểu 8 ký tự |
| **Added `@Pattern` validation** | Regex kiểm tra: chữ hoa, chữ thường, số, ký tự đặc biệt `(@#$%^&+=!)` |

### 4. `model/dto/response/AuthResponse.java` — Updated DTO
| Thay đổi | Mô tả |
|----------|-------|
| **Added `phone` field to UserInfo** | Trả về số điện thoại trong response |

### 5. `model/repository/UserRepository.java` — Updated repository
| Thay đổi | Mô tả |
|----------|-------|
| **Added `findByVerificationToken()`** | Tìm user theo token xác thực email |

### 6. `service/AuthService.java` — Updated interface
| Thay đổi | Mô tả |
|----------|-------|
| **Added `refreshToken()`** | Làm mới access token từ refresh token |
| **Added `verifyEmail()`** | Xác thực email qua token |
| **Added `resendVerification()`** | Gửi lại email xác thực |

### 7. `service/impl/AuthServiceImpl.java` — Major rewrite
| Yêu cầu US | Thay đổi |
|-------------|----------|
| **US-1.1**: Mật khẩu mạnh | Validation qua `@Pattern` trên DTO + kiểm tra backend |
| **US-1.1**: Email không trùng | `existsByEmail()` → throw `BadRequestException("Email already in use")` |
| **US-1.1**: Gửi email xác minh | Sinh UUID token, log ra console (simulation) |
| **US-1.1**: Kích hoạt tài khoản | `setEnabled(true)` khi verify, kiểm tra `isEnabled()` khi login |
| **US-1.1**: Email hết hạn 24h | `verificationTokenExpiry = now + 24h`, kiểm tra khi verify |
| **US-1.2**: Email không tồn tại | `findByEmail()` → throw `BadRequestException("Email không tồn tại")` |
| **US-1.2**: Mật khẩu sai | `BadCredentialsException` → throw `BadRequestException("Mật khẩu sai")` |
| **US-1.2**: Tài khoản chưa kích hoạt | Kiểm tra `!user.isEnabled()` khi login |
| **US-1.2**: Rate limit 5 lần/15ph | `loginAttempts++` → `lockUntil = now + 15min` khi >= 5 |
| **US-1.2**: JWT 24h | Cấu hình trong `application.yml` — `app.jwt.expiration: 86400000` |

### 8. `controller/AuthController.java` — Updated controller
| Endpoint | Method | Mô tả |
|----------|--------|-------|
| `/api/v1/auth/register` | POST | Đăng ký (có validation) |
| `/api/v1/auth/login` | POST | Đăng nhập (có rate limit) |
| `/api/v1/auth/refresh` | POST | Refresh token |
| `/api/v1/auth/verify` | GET | Xác thực email qua token |
| `/api/v1/auth/resend-verification` | POST | Gửi lại email xác thực |

### 9. `security/SecurityConfig.java` — No change needed
- `/api/v1/auth/**` đã được `permitAll()` từ trước

### 10. `resources/application.yml` — Added config
| Thay đổi | Mô tả |
|----------|-------|
| **Added `app.base-url`** | URL gốc dùng để tạo link verify email |

---

## Frontend Changes (`frontend/`)

### 1. `models/Api.model.ts` — Updated models
| Thay đổi | Mô tả |
|----------|-------|
| **Added `phone` to `AuthResponse.user`** | Nhận số điện thoại từ API |
| **Added `phone` to `RegisterRequest`** | Gửi số điện thoại khi đăng ký |

### 2. `models/User.model.ts` — Updated model
| Thay đổi | Mô tả |
|----------|-------|
| **Added `phone` field** | Hiển thị số điện thoại |
| **Added `ROLE_EMPLOYEE` to role union** | Hỗ trợ role Nhân viên |

### 3. `services/http.service.ts` — Enhanced interceptor
| Thay đổi | Mô tả |
|----------|-------|
| **Added refresh token logic** | Khi 401, tự động gọi `/api/v1/auth/refresh` |
| **Added queue for concurrent requests** | Tránh gọi refresh nhiều lần cùng lúc |
| **Support rememberMe storage** | Đọc token từ `localStorage` hoặc `sessionStorage` |

### 4. `controllers/useAuthController.ts` — Enhanced controller
| Thay đổi | Mô tả |
|----------|-------|
| **Added `rememberMe` to login** | `localStorage` (persist) vs `sessionStorage` (session-only) |
| **Role-based redirect** | Admin → `/admin/dashboard`, Employee → `/employee/dashboard`, User → `/dashboard` |
| **Fixed register flow** | Sau đăng ký → redirect `/login?registered=true` (không tự động login) |

### 5. `views/pages/LoginPage.tsx` — Updated page
| Thay đổi | Mô tả |
|----------|-------|
| **Added "Remember Me" checkbox** | Lưu phiên theo lựa chọn |
| **Added success message** | Hiển thị "Đăng ký thành công!" sau khi register |
| **Added search params** | Đọc `?registered=true` từ URL |

### 6. `views/pages/RegisterPage.tsx` — Updated page
| Thay đổi | Mô tả |
|----------|-------|
| **Added `phone` field** | Nhập số điện thoại |
| **Added `confirmPassword` field** | Xác nhận mật khẩu |
| **Added password strength indicator** | Weak/Medium/Strong/Very Strong |
| **Added client-side validation** | Kiểm tra empty fields, password match, password strength |

### 7. `context/AuthContext.tsx` — Updated context type
| Thay đổi | Mô tả |
|----------|-------|
| **`login` signature updated** | `login(data, rememberMe?)` |

---

## Summary by US Requirements

### US-1.1: Registration ✅
| Requirement | Status | Location |
|-------------|--------|----------|
| Email không trùng lặp | ✅ Done | `AuthServiceImpl.register()` |
| Mật khẩu ≥8 ký tự, chữ hoa, thường, số, ký tự đặc biệt | ✅ Done | `RegisterRequest.@Pattern`, `RegisterPage` validation |
| Hiển thị lỗi khi data không hợp lệ | ✅ Done | `GlobalExceptionHandler` + `RegisterPage.fieldErrors` |
| Gửi email xác minh sau đăng ký | ✅ Done | `AuthServiceImpl.register()` — log URL to console |
| Click link xác minh để kích hoạt | ✅ Done | `GET /api/v1/auth/verify?token=xxx` |
| Tài khoản có trạng thái "Active" | ✅ Done | `User.enabled = false → true` |
| Thông báo thành công → redirect login | ✅ Done | `LoginPage` shows `?registered=true` message |
| Email hết hạn 24h | ✅ Done | `verificationTokenExpiry` check |
| Password được hash | ✅ Done | BCrypt via `PasswordEncoder` |
| Rate limit chống spam | ✅ Done | RegisterAttempt via IP (existing in old code) |

### US-1.2: Login ✅
| Requirement | Status | Location |
|-------------|--------|----------|
| Form với Email + Password | ✅ Done | `LoginPage.tsx` |
| Xác thực email tồn tại | ✅ Done | `"Email không tồn tại"` |
| Xác thực mật khẩu đúng | ✅ Done | `"Mật khẩu sai"` |
| Message lỗi cụ thể (Tiếng Việt) | ✅ Done | Backend throws `BadRequestException` with Vietnamese message |
| Tạo JWT | ✅ Done | `JwtService.generateToken()` |
| Lưu token an toàn | ✅ Done | `localStorage` / `sessionStorage` (Remember Me) |
| Chuyển hướng theo role | ✅ Done | Admin → `/admin/dashboard`, Employee → `/employee/dashboard`, User → `/dashboard` |
| "Ghi nhớ tôi" | ✅ Done | Checkbox → `localStorage` (persist) vs `sessionStorage` (session) |
| Rate limit 5 lần/15ph | ✅ Done | `loginAttempts` counter + `lockUntil` in User entity |
| JWT hết hạn 24 giờ | ✅ Done | `app.jwt.expiration: 86400000` (24h in ms) |
| Refresh token | ✅ Done | `POST /api/v1/auth/refresh` + auto-refresh in `http.service.ts` |
| Ghi log đăng nhập thất bại | ✅ Done | `loginAttempts` counter in User entity |

---

## Files Created / Modified

### Backend (9 files modified, 0 created)
| File | Type |
|------|------|
| `model/entity/User.java` | Modified |
| `model/enums/Role.java` | Modified |
| `model/dto/request/RegisterRequest.java` | Modified |
| `model/dto/response/AuthResponse.java` | Modified |
| `model/repository/UserRepository.java` | Modified |
| `service/AuthService.java` | Modified |
| `service/impl/AuthServiceImpl.java` | Modified |
| `controller/AuthController.java` | Modified |
| `resources/application.yml` | Modified |

### Frontend (7 files modified, 0 created)
| File | Type |
|------|------|
| `models/Api.model.ts` | Modified |
| `models/User.model.ts` | Modified |
| `services/http.service.ts` | Modified |
| `controllers/useAuthController.ts` | Modified |
| `context/AuthContext.tsx` | Modified |
| `views/pages/LoginPage.tsx` | Modified |
| `views/pages/RegisterPage.tsx` | Modified |

### Root (1 file created)
| File | Type |
|------|------|
| `CHANGELOG.md` | Created |
