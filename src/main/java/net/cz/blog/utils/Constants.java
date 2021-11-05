package net.cz.blog.utils;

public interface Constants {

    int DEFAULT_SIZE = 10;

    interface User {
        String ROLE_ADMIN = "role_admin";
        String DEFAULT_AVATAR = "https://tse1-mm.cn.bing.net/th/id/OIP-C.50imJhqOFGaS71eWQPnehwE8DF?pid=ImgDet&rs=1";
        String DEFAULT_STATE = "1";
        String KEY_CAPTCHA_CONTENT = "key_captcha_content_";
        String KEY_EMAIL_CODE_CONTENT = "key_email_code_content";
        String KEY_EMAIL_SEND_TIMES = "key_email_send_times";
        String KEY_EMAIL_SEND_STATE = "key_email_send_state";
    }

    interface Settings {
        String MANAGE_ACCOUNT_INIT_STATE = "manage_account_init_state";

    }
}
