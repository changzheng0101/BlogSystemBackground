package net.cz.blog.utils;

public interface Constants {

    int DEFAULT_SIZE = 10;

    interface TimeValue {
        int HOUR_2 = 60 * 60 * 2 * 1000;
    }

    interface User {
        String ROLE_ADMIN = "role_admin";
        String ROLE_NORMAL = "role_normal";
        String DEFAULT_AVATAR = "https://tse1-mm.cn.bing.net/th/id/OIP-C.50imJhqOFGaS71eWQPnehwE8DF?pid=ImgDet&rs=1";
        String DEFAULT_STATE = "1";
        String KEY_CAPTCHA_CONTENT = "key_captcha_content_";
        String KEY_EMAIL_CODE_CONTENT = "key_email_code_content";
        String KEY_EMAIL_SEND_TIMES = "key_email_send_times";
        String KEY_EMAIL_SEND_STATE = "key_email_send_state";
        String KEY_TOKEN = "key_token_";
<<<<<<< HEAD
        String COOKIE_TOKEN_KEY = "blog_token";
=======
        String COOKIE_TOKE_KEY = "blog_token";
>>>>>>> 8697b170dac72fcea67092d97b8ae5547291a0b9
    }

    interface Settings {
        String MANAGE_ACCOUNT_INIT_STATE = "manage_account_init_state";

    }
}
