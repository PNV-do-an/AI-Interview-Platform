package com.aiinterview.platform.service.impl;

import com.aiinterview.platform.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;



    @Override
        try {
            mailSender.send(message);
        }
    }

    @Override
    }

    @Override
        }
    }
}
