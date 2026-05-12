    package com.hoc.backend.SpringBoot.controller.InterviewPlatform;

    import com.hoc.backend.SpringBoot.dto.LoginRequest;
    import com.hoc.backend.SpringBoot.dto.LoginResponse;
    import com.hoc.backend.SpringBoot.service.InterviewPlatform.LoginService;
    import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/api/auth")
    public class LoginController {

        private final LoginService loginService;

        public LoginController(LoginService loginService) {
            this.loginService = loginService;
        }

        @PostMapping("/login")
        public LoginResponse login(@RequestBody LoginRequest request) { // create object of Login Request (Jackson) (
            String token = loginService.verify(
                    request.getAccount(),
                    request.getPassword()
            );
            return new LoginResponse(token); // just return token, account is example
        }


    }






    //    -- demo
    //    private LoginService loginService;
    //
    //    public Lo ginController(LoginService loginService) {
    //        this.loginService = loginService;
    //    };
    //
    //    @GetMapping("service/login")
    //    public String login(@RequestParam String account, @RequestParam String passWord) {
    //        return loginService.verify(account,passWord);
    //    }
