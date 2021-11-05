package net.cz.blog;


import net.cz.blog.utils.EmailSender;

import javax.mail.MessagingException;

// todo 测试发送邮件
public class TestEmailSender {
    public static void main(String[] args) throws MessagingException {
        for (int i = 0; i < 100; i++) {
            EmailSender.subject("测试邮件发送")
                    .from("老公的测试")
                    .text("看到这个邮件就是看到了我")
                    .to("3423632146@qq.com")
                    .send();
        }

    }
}
