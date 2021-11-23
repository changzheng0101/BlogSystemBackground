package net.cz.blog.utils;

public interface Constants {

    int DEFAULT_SIZE = 10;

    //单位是s
    interface TimeValue {
        int MIN = 60;
        int HOUR = MIN * 60;
        int DAY = 24 * HOUR;
        int WEEK = 7 * DAY;
        int MONTH = 30 * DAY;
        int YEAR = 365 * DAY;
    }

    interface Page {
        int DEFAULT_PAGE = 1;
        int MIN_SIZE = 5;
    }

    interface Image {
        String PREFIX = "image/";
        String TYPE_JPG = "jpg";
        String TYPE_PNG = "png";
        String TYPE_GIF = "gif";
        String TYPE_JPG_WITH_PREFIX = PREFIX + "jpg";
        String TYPE_JPEG_WITH_PREFIX = PREFIX + "jpeg";
        String TYPE_PNG_WITH_PREFIX = PREFIX + "png";
        String TYPE_GIF_WITH_PREFIX = PREFIX + "gif";
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
        String COOKIE_TOKEN_KEY = "blog_token";
    }

    interface Settings {
        String MANAGE_ACCOUNT_INIT_STATE = "manage_account_init_state";
        String WEBSITE_TITLE = "website_title";
        String WEBSITE_DESCRIPTION = "website_description";
        String WEBSITE_KEYWORDS = "website_keywords";
        String WEBSITE_VIEW_COUNTS = "website_view_counts";
    }

    interface Article {
        int TITLE_MAX_LENGTH = 128;
        int SUMMARY_MAX_LENGTH = 256;
        // 0 删除 1 发布成功  2 草稿 3 置顶
        String ARTICLE_DELETE = "0";
        String ARTICLE_PUBLISH = "1";
        String ARTICLE_DRAFT = "2";
        String ARTICLE_TOP = "3";
    }
}
