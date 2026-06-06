package com.hoc.backend.SpringBoot.service.InterviewPlatform;

import com.hoc.backend.SpringBoot.dto.LoginResponse;
import com.hoc.backend.SpringBoot.exception.InvaildAccountLockedException;
import com.hoc.backend.SpringBoot.exception.InvalidPassWordException;
import com.hoc.backend.SpringBoot.model.User;
import com.hoc.backend.SpringBoot.repository.UserRepository;
import com.hoc.backend.SpringBoot.security.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoginService  {

    private final UserRepository userRepository;

    public LoginService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // private int counterFail = 0; // counter how many times password is incorect <- but if it's in here is a global variable, every user when enter
    // incorrect password always counterFail plus 1 -> often lock login function. -> so assign a new attribute at object user
    public final LoginResponse verify(String account, String passWord) {

        // verify input
        User user = userRepository.findUser(account); // verify exist account

        // verify account locked or unlocked
        if (user.getLocked()) {

            // verify time unlock
            if (LocalDateTime.now().isBefore(user.getLockedUntil())) {

                throw new InvaildAccountLockedException(
                        "Account locked until : " , user.getLockedUntil()
                );
            }

            // unlock account when time expired
            user.setLocked(false);
            user.setCounterFail(0);
            user.setLockedUntil(null);
        }

        // verify password
        if (!user.getPassWord().equals(passWord)) {
            user.setCounterFail(user.getCounterFail() +1);

//            userRepository.save(user); <- save status object to db // CounterFail trong mọi lần gọi không tăng giá trị CounterFail :
//            1. No save to db -> when CounterFail grow it's just in RAM -> need save to database.
//            2. Mock data wrong -> always create new object.

            // verify how many times incorrect password
            if (user.getCounterFail() >= 5) {

                user.setLocked(true);

                // lock account in 5 minutes
                user.setLockedUntil(
                        LocalDateTime.now().plusMinutes(5)
                );

                throw new InvaildAccountLockedException(
                        "Account locked in 5 minutes " , user.getLockedUntil()
                );
            }

            // save fail counter
            // userRepository.save(user);

//            throw new InvalidPassWordException(
//                    "Wrong password"
//            );
            System.out.println( "CounterFailed = " + user.getCounterFail());
            throw new InvalidPassWordException(("Wrong password")); //
        }

        // reset counter fail when login success
            user.setCounterFail(0);

        // unlock if login success
        user.setLocked(false);

        // clear time lock
        user.setLockedUntil(null);

        // save login success
        // userRepository.save(user);

       String  accessToken =  JwtUtil.generateAccessToken(
                user.getAccount(),
                user.getRole()
        );

       String refreshToken = JwtUtil.genarateRefreshToken(
               user.getAccount(),
               user.getRole()
       );



       return new LoginResponse(accessToken,refreshToken);
    }
    // kiểm tra refeshToken và tạo lai accessToken khi hết hạn
    public LoginResponse refreshAccessToken(String refreshToken) {
        Claims claims = JwtUtil.validateRefreshToken(refreshToken);

        String account = claims.getSubject();
        String role  = (String) claims.get("role");

        String newAccessToken = JwtUtil.generateAccessToken(account,role);

        return new LoginResponse(newAccessToken,refreshToken); // trả về exception

    }
}



//package com.hoc.backend.SpringBoot.service.InterviewPlatform;
//import com.hoc.backend.SpringBoot.dto.ResgiterResponse;
//import com.hoc.backend.SpringBoot.exception.InvalidAccountException;
//import com.hoc.backend.SpringBoot.exception.InvalidPassWordException;
//import com.hoc.backend.SpringBoot.exception.TooManyRequestsException; // Bạn có thể tạo thêm class exception này
//import com.hoc.backend.SpringBoot.model.RegisterAttempt;
//import com.hoc.backend.SpringBoot.model.User;
//import com.hoc.backend.SpringBoot.repository.RegisterAttemptRepository;
//import com.hoc.backend.SpringBoot.repository.UserRepository;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import java.time.LocalDateTime;
//
//@Service
//public class RegisterService {
//    private final UserRepository userRepository;
//    private final RegisterAttemptRepository registerAttemptRepository;
//
//    public RegisterService (UserRepository userRepository, RegisterAttemptRepository registerAttemptRepository) {
//        this.userRepository = userRepository;
//        this.registerAttemptRepository = registerAttemptRepository;
//    }
//
//    @Transactional // Nên có để đảm bảo tính toàn vẹn dữ liệu khi lưu cả 2 bảng
//    public ResgiterResponse resgiter (String account, String passWord, HttpServletRequest request) {
//
//        // BƯỚC 1: Lấy IP thật (xử lý qua Proxy/Load Balancer)
//        String clientIp = getClientIp(request);
//
//        // BƯỚC 2: Khởi tạo bản ghi RegisterAttempt TRƯỚC TIÊN
//        RegisterAttempt attempt = new RegisterAttempt();
//        attempt.setIpAddress(clientIp);
//        attempt.setAttemptedAccount(account.trim());
//        attempt.setAttemptTime(LocalDateTime.now());
//        // Lúc này user_id tự động là null
//
//        // BƯỚC 3: Kiểm tra spam dựa trên lịch sử trong DB
//        // Giả sử hàm này đếm số lần thử của IP này trong 1 phút qua
//        long attemptCount = registerAttemptRepository.countByIpAddressAndAttemptTimeAfter(clientIp, LocalDateTime.now().minusMinutes(1));
//        if (attemptCount >= 5) { // Quá 5 lần/phút thì block luôn
//            attempt.setStatus("BLOCKED_SPAM");
//            registerAttemptRepository.save(attempt); // Vẫn lưu lại vết là lượt này bị block
//            throw new TooManyRequestsException("Too many registration attempts. Please try again later.");
//        }
//
//        // BƯỚC 4: Chạy logic nghiệp vụ đăng ký của bạn
//        Boolean isExist = userRepository.checkExistUser(account.trim());
//
//        if (isExist) {
//            attempt.setStatus("FAILED_ACCOUNT_EXISTED");
//            registerAttemptRepository.save(attempt); // Lưu vết thất bại
//            throw new InvalidAccountException("Account existed");
//        }
//
//        if (passWord.length() < 8) {
//            attempt.setStatus("FAILED_INVALID_PASSWORD");
//            registerAttemptRepository.save(attempt); // Lưu vết thất bại
//            throw new InvalidPassWordException("Length of password need more than 8");
//        }
//
//        // BƯỚC 5: Đăng ký THÀNH CÔNG
//        User newUser = new User(account.trim(), passWord, "user");
//        User savedUser = userRepository.save(newUser); // Lưu user và lấy ra object có ID vừa sinh
//
//        // Cập nhật thông tin thành công cho bản ghi attempt
//        attempt.setStatus("SUCCESS");
//        attempt.setUser(savedUser); // Link cái ID của user mới tạo vào đây!
//        registerAttemptRepository.save(attempt);
//
//        return new ResgiterResponse("Create Success");
//    }
//
//    // Hàm helper lấy IP gốc từ request
//    private String getClientIp(HttpServletRequest request) {
//        String xForwardedFor = request.getHeader("X-Forwarded-For");
//        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
//            return xForwardedFor.split(",")[0].trim();
//        }
//        return request.getRemoteAddr();
//    }
//}