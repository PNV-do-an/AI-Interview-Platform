package com.hoc.backend.SpringBoot.service.InterviewPlatform;

import com.hoc.backend.SpringBoot.dto.ResgiterResponse;
import com.hoc.backend.SpringBoot.exception.InvalidAccountException;
import com.hoc.backend.SpringBoot.exception.InvalidPassWordException;
import com.hoc.backend.SpringBoot.model.RegisterAttempt;
import com.hoc.backend.SpringBoot.model.User;
import com.hoc.backend.SpringBoot.repository.RegisterAttemptRepository;
import com.hoc.backend.SpringBoot.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Setter;
import org.springframework.stereotype.Service;

import static com.hoc.backend.SpringBoot.security.utils.SecurityUtils.getClientIP;

@Service
public class RegisterService {
    private UserRepository userRepository;
    private RegisterAttemptRepository registerAttemptRepository;

    public RegisterService (UserRepository userRepository, RegisterAttemptRepository registerAttemptRepository) {
        this.userRepository = userRepository;
        this.registerAttemptRepository = registerAttemptRepository;
    }


    public ResgiterResponse resgiter (String account, String passWord, HttpServletRequest request) {

        String ipClient = getClientIP(request); // <- call function to get user's real IP

        RegisterAttempt attempt = new RegisterAttempt(ipClient,account); //

        Boolean result = userRepository.checkExistUser(account);// verify account existed or not


        if (!result) {
            if (passWord.length() >= 8) { // logic kiểm tra mật khẩu sẽ được dùng ở đăng ký / đặt lại mật khẩu -> thư viện
                User newUser = new User(account, passWord, "user");
                userRepository.addUser(newUser);
                registerAttemptRepository.findRegisterAttempt(ipClient).setIdUser(newUser.getId());

            }
            else {
                RegisterAttempt temp = registerAttemptRepository.findRegisterAttempt(ipClient);
                temp.setCounterFail(temp.getCounterFail()+1);
                registerAttemptRepository.findRegisterAttempt(ipClient).setCounterFail(registerAttemptRepository.findRegisterAttempt(ipClient).getCounterFail() +1);
                throw new InvalidPassWordException("Length of password need more than 8");
            }
        }
        else {
            throw new InvalidAccountException("Account existed");
        }

        return new ResgiterResponse("Create Success");
    }


}

// Note :
// Vấn đề chính : khi chưa đăng nhập bằng cách nào để chúng ta biết các bản ghi RegisterAttempt thuộc về ai -> IP
// Vậy thì ta cần gì lưu lại nó ( RegisterAttempt lâu dài ) vì đâu ai lại dùng chính tài khoản mà mình vừa đăng ký để
// đăng ký tài khoản mới và phải liên kết với ID user ->
// 1. Lưu lại nhật ký đăng ký thất bại : phục vụ cho việc phân tích và tối ưu : -> vì sao khách hàng lại đăng ký sai vấn
//đề đang nằm ở đâu
// 2. Hacker ( Account Enumeration ) : mục đích lọc ra các tài khoản đã đăng ký hay chưa -> từ những tài khooản đó có
// thể thực hiện các cuộc tấn công mang tính nguy hiểm hơn ( dò mật khẩu )
// 3. Tránh lỗi toàn vẹn dữ liệu khi một số tài khoản -> tránh lưu lại những thông tin từ những tài khoản đã xóa ( dữ liệu rác )

