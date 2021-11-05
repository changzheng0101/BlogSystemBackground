package net.cz.blog.services.Impl;

import net.cz.blog.utils.EmailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

@Service
public class TaskService {

    @Async
    public  void sendEmailVerifyCode(String emailAddress,String code) throws Exception {
        EmailSender.sendEmail(emailAddress,code);
    }
}
