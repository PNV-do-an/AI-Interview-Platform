package com.hoc.backend.SpringBoot.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// trong thực tế người ta dùng Redis để lưu.
public class RegisterAttempt {
    @Getter
    @Setter
    private Long idUser ; // (reference to user's id ) ko phải int/Interger vì nó có giới hạn nhỏ hơn | vì sao Long ko phải long ( nguyên thủy : primitive ) vì Long ( object ) : object có thể lưu null -> dễ dàng thể hiện trạng thái chưa lưu ( null ) thay vì dùng 0
    @Getter
    @Setter
    private String account;
    @Getter
    @Setter
    private String address;

    @Getter
    @Setter
    private int counterFail;

    @Getter
    @Setter
    private LocalDateTime timestamp;

    @Getter
    @Setter
    private Boolean locked;

    @Getter
    @Setter
    private LocalDateTime lockUntil;

    public RegisterAttempt (String address,String account) {
        this.address = address;
        this.account = account;
    }

    public RegisterAttempt (Long idUser, String address, int counterFail, LocalDateTime lockUntil) {
            this.idUser = idUser;
            this.address = address;
            this.counterFail = counterFail;
            this.lockUntil = lockUntil;
    }

}
