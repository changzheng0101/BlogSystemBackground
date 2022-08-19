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
        int SECOND_10=10;
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
        String KEY_COMMIT_TOKEN_RECORD = "key_commit_token_record_";
    }

    interface Settings {
        String MANAGE_ACCOUNT_INIT_STATE = "manage_account_init_state";
        String WEBSITE_TITLE = "website_title";
        String WEBSITE_DESCRIPTION = "website_description";
        String WEBSITE_KEYWORDS = "website_keywords";
        String WEBSITE_VIEW_COUNTS = "website_view_counts";
    }

    interface Article {
        String TYPE_MARKDOWN="1";
        String TYPE_RICH_TEXT="0";
        int TITLE_MAX_LENGTH = 128;
        int SUMMARY_MAX_LENGTH = 256;
        // 0 删除 1 发布成功  2 草稿 3 置顶 4 精选文章
        String ARTICLE_DELETE = "0";
        String ARTICLE_PUBLISH = "1";
        String ARTICLE_DRAFT = "2";
        String ARTICLE_TOP = "3";
        String ARTICLE_SELECTED="4";
        String KEY_ARTICLE_CACHE = "key_article_cache_";
        String KEY_ARTICLE_VIEW_COUNT = "key_article_view_count_";
        String KEY_FIRST_PAGE_ARTICLE= "key_first_page_article";
        String KEY_SELECTED_ARTICLE = "key_selected_article";
    }

    interface Comment {
        // 0 删除 1 发布成功 3 置顶
        String COMMENT_DELETE = "0";
        String COMMENT_PUBLISH = "1";
        String COMMENT_TOP = "3";
        String KEY_FIRST_PAGE_COMMENTS = "key_first_page_comments_";
    }
}
