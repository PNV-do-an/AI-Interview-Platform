# 📘 AI Interview Platform — Technical Deep-Tutorial Guide (Song ngữ / Bilingual)

> Tài liệu tham khảo kỹ thuật toàn diện cho backend (Spring Boot) + frontend (React TS).
> Nêu rõ **các từ khóa kỹ thuật cần nắm**, **logic từng chức năng** và **cấu hình quan trọng**.
> Language: **VI (chính)** + **EN (phụ)** — để vừa học vừa đối chiếu thuật ngữ.

---

## Mục lục (Table of Contents)

1. Tổng quan & cấu trúc dự án
2. Stack công nghệ / Glossary từ khóa kỹ thuật
3. Kiến trúc 3 tầng & vòng đời request
4. **Authentication** — Register (đăng ký) — deep logic
5. **Authentication** — Verify email & Resend (xác thực email)
6. **Authentication** — Login & JWT (đăng nhập) — deep logic
7. **Password Reset** — Forgot / Reset mật khẩu — deep logic
8. **Admin** — Quản lý người dùng & Audit Log — deep logic
9. Bảng dữ liệu (Database schema)
10. Bảo mật — Security config, secret, CORS
11. Các endpoint API đầy đủ
12. Cách chạy: Local (IntelliJ) vs Docker Compose
13. Kế hoạch cải tiến tiếp theo (token_attempt, Flyway)

---

## 1. Tổng quan & cấu trúc dự án

**VI:** AI Interview Platform là ứng dụng web tuyển dụng/phỏng vấn: đăng ký, đăng nhập, xác thực email, quản lý người dùng theo vai trò. Backend **Spring Boot 3.2.5 (Java 17)**, Frontend **React 18 + TypeScript (CRA)**, DB **MySQL 8**. Chạy được cả local (IntelliJ) lẫn Docker Compose.

**EN:** AI Interview Platform is a hiring/interview web application with registration, login, email verification and role-based user management.

```
SpringBoot/
├── backend/                     # Spring Boot (Java) — REST API, port 8080
│   └── src/main/
│       ├── java/com/aiinterview/platform/
│       │   ├── common/          # ApiResponse, ErrorModel, GlobalExceptionHandler
│       │   ├── config/          # DataInitializer (seed admin)
│       │   ├── controller/      # auth/*, AdminUserController, user/*
│       │   ├── model/           # entity/, dto/request, dto/response, enums/, repository/
│       │   ├── security/        # jwt/ (JwtUtil, JwtFilter, JwtSecurityConfig), utils/
│       │   └── service/         # interfaces + impl/
│       └── resources/
│           ├── application.yml
│           └── db/migration/    # V1..V5 Flyway SQL (hiện tắt)
├── frontend/                    # React TS — UI, port 3000
│   └── src/
│       ├── App.tsx              # Router v6
│       ├── context/             # AuthContext
│       ├── controllers/         # useAuthController, useAdminUserController
│       ├── models/              # kiểu TS (User, ApiResponse,...)
│       ├── services/            # http.service, auth.service, admin.service
│       └── views/               # pages/ + components/
├── docker-compose.yml           # db + backend + frontend
├── .env / .env.example          # secret (mail, JWT) — .env gitignored
└── docs/TECHNICAL_GUIDE.md      # ← tài liệu này
```

---

## 2. Stack công nghệ — Glossary từ khóa kỹ thuật

| Công nghệ | VI | EN | Nơi dùng |
|---|---|---|---|
| **Spring Boot 3.2.5** | Framework Java tự cấu hình REST API | Java auto-configured framework | `backend/pom.xml` |
| **Java 17** | Ngôn ngữ, bản LTS (`java.version=17`) | Language, LTS version | `pom.xml` |
| **Spring MVC** | Ánh xạ URL → method Java | URL → method mapping | `@RestController`, `@GetMapping` |
| **Spring Data JPA** | Truy xuất DB qua entity | DB access via entities | `JpaUserRepository`, `User` |
| **Lombok 1.18.38** | Bỏ boilerplate code (`@Data`, `@Builder`) | Remove boilerplate | mọi entity/DTO |
| **Spring Security** | Xác thực + phân quyền | AuthN + AuthZ framework | `JwtSecurityConfig`, `JwtFilter` |
| **JWT (JJWT 0.11.5)** | Token JSON có chữ ký, stateless | Signed JSON token | `JwtUtil.java` |
| **BCrypt** | Băm mật khẩu một chiều + salt | One-way password hash | `BCryptPasswordEncoder` |
| **Flyway** | Version hóa schema qua file `V{n}.sql` | DB schema versioning | `db/migration/V1..V5` |
| **Bean Validation** | Ràng buộc field (`@NotBlank`, `@Email`) | Field constraints | `RegisterRequest`, `@Valid` |
| **Spring Mail** | Gửi email qua SMTP Gmail (STARTTLS) | SMTP email sending | `EmailServiceImpl` |
| **CORS** | Cho origin 3000 gọi API 8080 | Cross-origin policy | `JwtSecurityConfig` |
| **TypeScript** | JS + kiểu tĩnh | JS with static types | `frontend/src/*.ts(x)` |
| **React Router v6** | Điều hướng SPA | SPA routing | `App.tsx` |
| **Axios** | HTTP client + interceptor | HTTP client w/ interceptors | `http.service.ts` |
| **React Context** | Chia sẻ state toàn app (auth) | Global state | `AuthContext.tsx` |
| **MySQL 8** | Cơ sở dữ liệu quan hệ | Relational DB | docker `db` |
| **Docker Compose** | Chạy nhiều container cùng lúc | Multi-container orchestration | `docker-compose.yml` |

---

## 3. Kiến trúc 3 tầng & vòng đời request

**VI:** Kiến trúc **3 tầng phổ biến của Spring Boot**:

```
React FE (:3000)
   │ HTTP/JSON (Axios, qua CORS)
   ▼
Controller Layer  (@RestController)  ← nhận request, gọi service, trả ApiResponse
   │
   ▼
Service Layer     (@Service)         ← logic nghiệp vụ (register, login, admin...)
   │
   ▼
Repository Layer  (@Repository)      ← JPA truy vấn DB
   │
   ▼
MySQL  (bảng users, admin_audit_log, ...)
```

**Vòng đời 1 request:**
1. **Controller** nhận `@RequestBody`/`@RequestParam`, đánh dấu `@Valid` để validate.
2. Gọi method của **Service** (chứa logic + ném exception tùy nghiệp vụ).
3. **Service** gọi **Repository** (`save`, `findByEmail`, `findAllWithFilters`...).
4. Kết quả gói trong `ApiResponse.success(...)` trả về JSON.
5. Nếu có lỗi → **GlobalExceptionHandler** (`@RestControllerAdvice`) bắt và trả `ErrorModel` với HTTP status chuẩn.

**EN:** Typical 3-layer architecture; request flows Controller → Service → Repository → DB, wrapped in `ApiResponse`, errors caught by `GlobalExceptionHandler`.

---

## 4. Authentication — Register (Đăng ký) — Deep logic

**Endpoint:** `POST /api/v1/auth/register`
**File:** `controller/auth/AuthController.java` → `service/impl/RegisterInterfaceImpl.java`

### Luồng từng bước (VI):
1. **Nhận request** từ `RegisterRequest` (`email`, `password`, `fullName`, `phone`) — có `@Valid` (validate format email, password...). Lấy thêm `HttpServletRequest` để lấy **IP client** (`SecurityUtils.getClientIP`).
2. **Chống thư rác / brute-force theo IP:** `registerAttemptRepository.countFailedByIp(ip, 5 phút trước)`. Nếu **≥ 10 lần fail** trong 5 phút → ném lỗi "hoạt động bất thường".
3. **Rate-limit theo email:** tìm/lập `RegisterAttempt` cho email. Nếu đang `locked` và chưa hết `lockUntil` → chặn. (5 fail email → lock 5 phút.)
4. **Rate-limit theo IP (giữ từ code cũ):** tương tự bước 3 nhưng theo IP.
5. **Kiểm tra email trùng:** `userRepository.existsByEmail(email)` → nếu có → lỗi "Email đã tồn tại".
6. **Validate password** bằng regex `PASSWORD_PATTERN`:
   ```
   ^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@#$%^&+=!]).{8,}$
   ```
   = ít nhất 8 ký tự, có chữ thường, chữ hoa, số, ký tự đặc biệt. Sai → tăng counter email+IP, có thể lock, ném lỗi.
7. **Tạo verification token:** `UUID.randomUUID().toString()` — chuỗi ngẫu nhiên (122 bit), KHÔNG liên quan đến dữ liệu user. Token + `verificationTokenExpiry = now + 24h` được lưu vào entity.
8. **Tạo User mới** bằng Lombok `@Builder`:
   ```java
   User.builder()
      .email(email)
      .password(passwordEncoder.encode(password))   // BCrypt băm mật khẩu
      .fullName(fullName)
      .phone(phone)
      .role(Role.ROLE_USER)                          // mặc định USER
      .enabled(false)                                 // ⚠️ QUAN TRỌNG: chưa active!
      .verificationToken(verificationToken)
      .verificationTokenExpiry(now.plusHours(24))
      .build();
   ```
   > **Lưu ý lịch sử bug:** trước đây `.enabled(true)` khiến user active ngay lập tức dù chưa xác thực email. **Đã sửa thành `false`** để khớp đúng luồng: chỉ active sau khi verify.
9. **`userRepository.save(newUser)`** — lưu DB.
10. **Reset counters** email & IP sau khi thành công.
11. **Gửi email xác thực** qua `emailService.sendVerificationEmail(...)` — lỗi gửi email chỉ `warn` log, không chặn đăng ký.
12. **Trả về** message "Đăng ký thành công! Vui lòng kiểm tra email để kích hoạt tài khoản."

### Từ khóa cần nắm (Glossary):
- **`@Valid` + `@RestControllerAdvice`**: validate request từ client.
- **Rate limiting**: giới hạn số request để chống lạm dụng.
- **UUID**: mã định danh ngẫu nhiên.
- **BCrypt `encode()`**: băm mật khẩu, không lưu plaintext.
- **`@Builder` default**: tránh quên field (như `enabled`).
- **`@PrePersist`**: set `createdAt`/`updatedAt` trước khi insert (trong `User.java`).

---

## 5. Authentication — Verify email & Resend

### 5.1 Verify email
**Endpoint:** `GET /api/v1/auth/verify?token=...`
**File:** `RegisterInterfaceImpl.verifyEmail(String token)`

```
findByVerificationToken(token)
   └─ không tìm thấy → "Token xác thực không hợp lệ"
   └─ hết hạn (now > verificationTokenExpiry) → "Link đã hết hạn (24h)"
   └─ hợp lệ →
        user.setEnabled(true)              ← KÍCH HOẠT tài khoản
        user.setVerificationToken(null)    ← dùng 1 lần: xóa token
        user.setVerificationTokenExpiry(null)
        save
        → "Tài khoản đã được kích hoạt thành công!"
```

### 5.2 Resend verification
**Endpoint:** `POST /api/v1/auth/resend-verification` (body: `{email}`)

- Nếu `user.isEnabled()` đã active → lỗi "Tài khoản đã được kích hoạt".
- Ngược lại: tạo **token mới**, set lại expiry 24h, gửi lại email.

### Từ khóa cần nắm:
- **One-time token** (dùng 1 lần): sau khi verify, token bị xóa → bấm link lần 2 sẽ lỗi.
- **Expiry** (hết hạn): token có thời gian sống (24h).
- **`@Enumerated(EnumType.STRING)`**: lưu enum dưới dạng chuỗi trong DB.

---

## 6. Authentication — Login & JWT — Deep logic

**Endpoint:** `POST /api/v1/auth/login`
**File:** `service/impl/LoginInterfaceImpl.java`

### Luồng từng bước:
1. Lấy IP client.
2. **IP rate-limit:** `countFailedByIp(ip, 15 phút) >= 20` → chặn "bất thường".
3. **`findByEmail(email)`** — nếu không tồn tại → ghi 1 attempt fail + lỗi "Email không tồn tại". *(Chống lộ tài khoản — user enumeration.)*
4. **Kiểm tra `deletedAt != null`** → tài khoản bị xóa (soft delete).
5. **Kiểm tra `!isEnabled()`** → chưa kích hoạt → "Vui lòng kiểm tra email".
6. **Kiểm tra `isLocked()`** → bị admin khóa → `InvalidAccountLockedException` (HTTP 423).
7. **Kiểm tra `lockUntil`** → đang khóa do nhập sai (15 phút).
8. **Đếm fail gần đây:** `getFailedAttempts(email, 15 phút).size() >= 5` → khóa 15 phút.
9. **So mật khẩu:** `passwordEncoder.matches(password, user.getPassword())`:
   - **Sai** → tăng `loginAttempts`, ghi attempt fail; nếu `loginAttempts >= 5` → lock 15 phút; ném lỗi "Mật khẩu sai".
   - **Đúng** → ghi attempt success, reset `loginAttempts = 0`, `lockUntil = null`, save.
10. **Tạo JWT:**
    - `JwtUtil.generateAccessToken(email, role)` — hết hạn **1 ngày**.
    - `JwtUtil.generateRefreshToken(email, role)` — hết hạn **7 ngày**.
11. **Trả về** `LoginResponse` + `AuthResponse` đầy đủ (token + thông tin user) cho frontend.

### JWT chi tiết (`security/jwt/JwtUtil.java`)
- Dùng `Keys.hmacShaKeyFor(secret.getBytes())` (HMAC-SHA).
- **Access token**: `setSubject(email)`, claim `role`, `expiration = 1 ngày`.
- **Refresh token**: `expiration = 7 ngày`.
- `validateAccessToken` / `validateRefreshToken`: parse + xác minh chữ ký.
- ⚠️ **Ghi chú bảo mật:** secret đang hardcode (`access-secret-...`). Nên chuyển vào env/biến config trong production.

### `JwtFilter` (OncePerRequestFilter) — bảo vệ mọi request
```
doFilterInternal:
  đọc header "Authorization: Bearer <token>"
  nếu có → validate access token → lấy email (subject)
          → loadUserByUsername(email) → ép kiểu (User)
          → nếu !enabled || locked || deletedAt!=null → KHÔNG set auth (bỏ qua)
          → ngược lại tạo UsernamePasswordAuthenticationToken
            set vào SecurityContextHolder
  → chain.doFilter(request, response)
```

**Từ khóa cần nắm:**
- **Stateless session**: `SessionCreationPolicy.STATELESS` — server không lưu session.
- **`SecurityContextHolder`**: lưu Authentication của request hiện tại.
- **`OncePerRequestFilter`**: filter chạy 1 lần mỗi request.
- **Principal**: `@AuthenticationPrincipal User` (JWT filter load User thật).
- **Refresh token**: token dài hạn dùng để cấp access token mới khi hết hạn.

---

## 7. Password Reset — Forgot / Reset mật khẩu — Deep logic

**File:** `service/impl/PasswordResetServiceImpl.java`

### 7.1 `forgotPassword(String email)`
**Endpoint:** `POST /api/v1/auth/forgot-password`

```
userRepository.findByEmail(email)
  └─ không tồn tại → trả về GENERIC_MESSAGE (không lộ email)   ← chống user enumeration
  └─ tồn tại →
       resetToken = UUID.randomUUID()
       setResetToken + setResetTokenExpiry(now + 1 giờ)
       save
       gửi email chứa link reset
       trả về GENERIC_MESSAGE "Nếu email tồn tại, bạn sẽ nhận được link..."
```

> **Chống user enumeration:** dù email có hay không đều trả cùng thông báo chung chung → hacker không biết email nào tồn tại.

### 7.2 `resetPassword(String token, String newPassword)`
**Endpoint:** `POST /api/v1/auth/reset-password`

```
findByResetToken(token)
  └─ không tìm thấy → "Link đặt lại mật khẩu không hợp lệ"
  └─ hết hạn (now > resetTokenExpiry) → "Link đã hết hạn"
  └─ password không đạt regex PASSWORD_PATTERN → lỗi mật khẩu
  └─ hợp lệ →
       password = BCrypt.encode(newPassword)
       tokenVersion++            ← vô hiệu các JWT cũ
       resetToken = null (dùng 1 lần)
       resetTokenExpiry = null
       loginAttempts = 0 ; lockUntil = null
       save
       gửi email xác nhận
       → "Mật khẩu đã được đặt lại thành công."
```

**Từ khóa cần nắm:**
- **TokenVersion / token invalidation**: check JWT version (nếu có) để ép đăng xuất sau đổi mật khẩu.
- **Account lockout reset**: sau đổi mật khẩu thành công, bỏ luôn trạng thái khóa.

---

## 8. Admin — Quản lý người dùng & Audit Log — Deep logic

**Controller:** `AdminUserController` — base `/api/v1/admin`, `@PreAuthorize("hasRole('ADMIN')")`.
**Service:** `AdminUserServiceImpl`

### Các action admin (deep logic)

| Method | Endpoint | Logic |
|---|---|---|
| GET | `/admin/users?page=&size=&search=&status=&role=` | Lọc + phân trang (JPQL `findAllWithFilters`); `parseRole` chuyển String→Role, invalid → `BadRequestException` |
| GET | `/admin/users/{id}` | Xem chi tiết; ghi audit `VIEW_USER` |
| PATCH | `/admin/users/{id}/lock` | Khóa (set `locked=true`) + email + audit `LOCK_USER`; không khóa chính mình |
| PATCH | `/admin/users/{id}/unlock` | Mở khóa + email + audit `UNLOCK_USER` |
| DELETE | `/admin/users/{id}` | **Soft delete** (set `deletedAt=now`) + email + audit `DELETE_USER`; không xóa mình |
| POST | `/admin/users/{id}/reset-password` | Gửi email reset + audit `RESET_PASSWORD` |
| PATCH | `/admin/users/{id}/role` | Đổi role + audit `CHANGE_ROLE`; **bảo vệ admin cuối cùng** |
| GET | `/admin/audit-logs?page=&size=` | Xem nhật ký audit |

### Điểm đáng chú ý:
- **`parseRole`** (`String` → `Role`): nếu role param không hợp lệ → ném `BadRequestException` (HTTP 400 JSON sạch, không phải 500). Đây là fix đã làm.
- **`resolveAdmin()`**: lấy admin hiện tại từ `SecurityContextHolder`, trả `null` nếu không xác định (vd test `@WithMockUser`) → `adminId = 0`, `adminEmail="system"`.
- **Bảo vệ admin cuối cùng:** khi bỏ role ADMIN của 1 admin, đếm số admin còn lại; nếu `<= 1` → cấm (luôn có ≥1 admin).
- **Soft delete vs hard delete:** chỉ set `deletedAt`, không xóa bản ghi — giữ dữ liệu lịch sử.

**Từ khóa cần nắm:**
- **`@PreAuthorize` / `@EnableMethodSecurity`**: phân quyền theo role ở mức method.
- **Audit log**: ghi lại hành động admin (ai, làm gì, khi nào).
- **Soft delete**: đánh dấu xóa thay vì xóa vĩnh viễn.
- **Pagination**: `PageRequest.of(page, size, Sort.by(...))`.
- **`@Transactional`**: gói nhiều thao tác DB trong 1 transaction (atomic).

---

## 9. Bảng dữ liệu (Database schema)

> ⚠️ **Lưu ý quan trọng:** Hiện tại `application.yml` để **`flyway.enabled: false`** và **`ddl-auto: update`** → Hibernate tự tạo/cập nhật bảng từ entity. Các file `V1..V5.sql` **chưa được chạy** (chỉ để tham khảo/version tương lai). Kế hoạch sắp tới: bật Flyway làm nguồn chân lý.

### Bảng `users`
| Cột | Kiểu | Ghi chú |
|---|---|---|
| `id` | BIGINT PK AUTO | |
| `email` | VARCHAR UNIQUE NOT NULL | Login bằng email |
| `password` | VARCHAR NOT NULL | BCrypt hash |
| `full_name` | VARCHAR NOT NULL | |
| `phone` | VARCHAR | |
| `role` | VARCHAR(50) NOT NULL `DEFAULT 'ROLE_USER'` | `ROLE_USER/STAFF/ADMIN/INTERVIEWER` |
| `enabled` | TINYINT(1) NOT NULL `DEFAULT 0` | Khớp fix: mặc định chưa active |
| `locked` | TINYINT(1) NOT NULL `DEFAULT 0` | Khóa bởi admin |
| `deleted_at` | DATETIME NULL | Soft delete |
| `created_at` / `updated_at` | DATETIME | |
| `verification_token` / `verification_token_expiry` | VARCHAR / DATETIME | Verify email (24h) |
| `reset_token` / `reset_token_expiry` | VARCHAR / DATETIME | Reset mật khẩu (1h) |
| `token_version` | INT NOT NULL DEFAULT 0 | Vô hiệu JWT cũ |
| `login_attempts` | INT NOT NULL DEFAULT 0 | Đếm fail login |
| `lock_until` | DATETIME | Hết khóa tạm |

> (Lưu ý: migration `V1` đang để `DEFAULT 1` cho `enabled` — **sẽ sửa** trong kế hoạch Flyway.)

### Bảng `admin_audit_log`
| Cột | Kiểu | Ghi chú |
|---|---|---|
| `id` | BIGINT PK AUTO | |
| `admin_id` / `admin_email` | BIGINT / VARCHAR NOT NULL | Tai ai làm |
| `target_user_id` | BIGINT NULL | Bị tác động |
| `action` | VARCHAR(100) NOT NULL | `LOCK_USER`, `CHANGE_ROLE`... |
| `detail` | TEXT | Mô tả |
| `created_at` | DATETIME NOT NULL | |

### Bảng attempt (in-memory cho login/register — CHƯA làm JPA yet)
- `LoginAttempt` / `RegisterAttempt` hiện là **Java class trong ArrayList** (`FakeLoginAttemptRepository`, `FakeRegisterAttempt`) — **chỉ tồn tại trong bộ nhớ, mất khi restart**. Không phải bảng DB thật.
- ➡️ Kế hoạch: tạo **bảng `token_attempt` (JPA)** cho verify/reset (xem mục 13).

---

## 10. Bảo mật — Security config, secret, CORS

### `JwtSecurityConfig.java`
- `.csrf().disable()` — API stateless, tắt CSRF.
- `.cors(...)` — cho phép frontend gọi.
- `.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())` — **⚠️ hiện mở tất cả**. `@PreAuthorize` vẫn chặn admin ở method level. *(Kế hoạch: siết chặt lại.)*
- `.sessionManagement().sessionCreationPolicy(STATELESS)`.
- `.addFilterBefore(new JwtFilter(userService), UsernamePasswordAuthenticationFilter.class)`.
- `PasswordEncoder` → `BCryptPasswordEncoder`.

### CORS (`corsConfigurationSource`)
- `allowedOriginPatterns("*")`, methods GET/POST/PUT/DELETE/OPTIONS/PATCH, headers `*`, `allowCredentials(true)`.
- ⚠️ `*` origin + credentials = rộng. Nên giới hạn theo `cors.allowed-origins` ở production.

### Secret management
**`application.yml`** dùng placeholder với default:
```yaml
mail:
  host: ${MAIL_HOST:smtp.gmail.com}
  port: ${MAIL_PORT:587}
  username: ${MAIL_USERNAME:}     # default rỗng → không lộ secret
  password: ${MAIL_PASSWORD:}

app:
  jwt:
    secret: ${JWT_SECRET:404E6352...}
```
- Secret thật (mail app password) nằm trong **`.env` (gitignored)** → không bao giờ commit.
- Docker Compose đọc `.env` qua `${MAIL_USERNAME:-}`.
- Mỗi developer có `.env` riêng.

---

## 11. Các endpoint API đầy đủ

### Auth (`/api/v1/auth`)
| Method | Path | Mô tả | File |
|---|---|---|---|
| POST | `/register` | Đăng ký | `AuthController` |
| POST | `/login` | Đăng nhập | `AuthController` |
| POST | `/refresh` | Refresh token | `AuthController` |
| GET | `/verify?token=` | Xác thực email | `AuthController` |
| POST | `/resend-verification` | Gửi lại email xác thực | `AuthController` |
| POST | `/forgot-password` | Quên mật khẩu | `AuthController` |
| POST | `/reset-password` | Đặt lại mật khẩu | `AuthController` |
| GET | `/me` | Thông tin user hiện tại (JWT) | `AuthController` |

### Admin (`/api/v1/admin` — cần `ROLE_ADMIN`)
| Method | Path | Mô tả |
|---|---|---|
| GET | `/users` | Danh sách + lọc + phân trang |
| GET | `/users/{id}` | Chi tiết |
| PATCH | `/users/{id}/lock` / `/unlock` | Khóa / mở khóa |
| DELETE | `/users/{id}` | Xóa (soft) |
| POST | `/users/{id}/reset-password` | Reset mật khẩu |
| PATCH | `/users/{id}/role` | Đổi vai trò |
| GET | `/audit-logs` | Nhật ký audit |

### User / MISC
| Method | Path | Mô tả |
|---|---|---|
| GET | `/api/user/profile` | Profile user hiện tại |
| GET | `/api/test` | Test JWT (AuthorizationController) |
| GET | `/` | Trả `indexLogin.html` (ViewController) |

---

## 12. Cách chạy — Local vs Docker

### Cách 1 — Docker Compose (một lệnh)
```
copy .env.example .env      # Windows (điền mail secret thật vào .env)
docker compose up --build
```
- `db` (MySQL 8, healthcheck, volume `ai_interview_compose_mysql_data`),
- `backend` (Spring Boot :8080),
- `frontend` (nginx :3000, proxy `/api/` → backend).

### Cách 2 — Local (IntelliJ + npm)
MySQL chạy qua container riêng:
```
docker run -d -p 3307:3306 --name ai_interview_mysql \
  -v springboot_mysql_data:/var/lib/mysql \
  -e MYSQL_DATABASE=ai_interview_db -e MYSQL_USER=aiuser \
  -e MYSQL_PASSWORD=aipassword -e MYSQL_ROOT_PASSWORD=rootpass mysql:8.0
```
- Backend: IntelliJ build/run (JDK 17), config trong `application.yml` trỏ `localhost:3307`.
- Frontend: `npm install && npm start` → :3000, proxy `/api` → :8080 (trong `package.json`).

### Admin seed (tự động)
`DataInitializer` (ApplicationRunner) tạo admin mặc định khi DB chưa có:
```
admin@aiinterview.com / Admin@12345 / ROLE_ADMIN
```
---

## 13. Kế hoạch cải tiến tiếp theo (đã chốt, chưa code)

### A. Bật Flyway làm nguồn chân lý
- `application.yml`: `flyway.enabled: true`, `ddl-auto: validate`.
- Rà soát entity ↔ migration (fix `enabled` default V1 = 0).
- **Backup + validate trên DB clone** trước khi áp lên DB local (an toàn data).

### B. Bảng `token_attempt` (JPA) — bảo vệ verify & reset password
Cách **Y** (1 dòng/token, cột `attempt++`):
| Cột | Mô tả |
|---|---|
| `id` | PK |
| `id_user` | FK → users.id (null nếu token sai) |
| `token` | Unique theo purpose |
| `email` | Đếm link reset/email |
| `ip_address` | Chống DoS theo IP |
| `purpose` | `VERIFY` / `RESET` |
| `success` / `token_valid` | Kết quả + token có tồn tại |
| `attempt` | Cột cộng dồn `++` |
| `lock_until` / `timestamp` | Thời gian |

**Logic áp dụng (reset password):**
- 3 link reset / email trong **15 phút** → chặn tạm.
- Token sai/hết hạn → `attempt++`; ≥ 5 → **vô hiệu URL** (bắt buộc tạo link mới).
- **Password yếu (token ĐÚNG)** → KHÔNG cộng attempt (tránh khóa oan user thật — chỉ khi user bỏ qua validate client).
- Chống DoS theo IP: ≥ 15 request / 15 phút → chặn.
- Áp dụng tương tự cho `verifyEmail` với `purpose=VERIFY`.

### C. Frontend
- Thêm client-side validate password trước khi submit (ResetPasswordPage hiện chỉ check password === confirmPassword; RegisterPage đã check đầy đủ).

---

## Phụ lục — Các file quan trọng tham khảo

| File | Vai trò |
|---|---|
| `backend/src/main/resources/application.yml` | Config chính |
| `.../security/jwt/JwtUtil.java` | Tạo/validate JWT |
| `.../security/jwt/JwtFilter.java` | Lọc JWT mỗi request |
| `.../security/jwt/JwtSecurityConfig.java` | Security chain |
| `.../common/exception/GlobalExceptionHandler.java` | Bắt lỗi → JSON |
| `.../service/impl/RegisterInterfaceImpl.java` | Register + verify |
| `.../service/impl/LoginInterfaceImpl.java` | Login + rate-limit |
| `.../service/impl/PasswordResetServiceImpl.java` | Forgot/reset password |
| `.../service/impl/AdminUserServiceImpl.java` | Admin actions + audit |
| `.../model/repository/*` | Custom + JPA + Fake repos |
| `frontend/src/services/http.service.ts` | Axios + interceptor + refresh |
| `frontend/src/context/AuthContext.tsx` | State auth |

---

> **Kết thúc tài liệu.**
> Chỉnh sửa khi có thay đổi trong mã nguồn. Cập nhật mục 13 khi triển khai.
