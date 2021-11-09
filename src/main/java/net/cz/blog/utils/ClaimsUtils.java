package net.cz.blog.utils;

import io.jsonwebtoken.Claims;
import net.cz.blog.pojo.BlogUser;

import java.util.HashMap;
import java.util.Map;

public class ClaimsUtils {

    public static String ID = "id";
    public static String USER_NAME = "user_name";
    public static String ROLES = "roles";
    public static String AVATAR = "avatar";
    public static String EMAIL = "email";
    public static String SIGN = "sign";


    public static Map<String, Object> blogUser2Claims(BlogUser blogUser) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(ID, blogUser.getId());
        claims.put(USER_NAME, blogUser.getUserName());
        claims.put(ROLES, blogUser.getRoles());
        claims.put(AVATAR, blogUser.getAvatar());
        claims.put(EMAIL, blogUser.getEmail());
        claims.put(SIGN, blogUser.getSign());
        return claims;
    }

    public static BlogUser claims2BlogUser(Claims claims) {
        BlogUser blogUser = new BlogUser();
        String id = (String) claims.get(ID);
        blogUser.setId(id);
        String userName = (String) claims.get(USER_NAME);
        blogUser.setUserName(userName);
        String roles = (String) claims.get(ROLES);
        blogUser.setRoles(roles);
        String avatar = (String) claims.get(AVATAR);
        blogUser.setAvatar(avatar);
        String email = (String) claims.get(EMAIL);
        blogUser.setEmail(email);
        String sign = (String) claims.get(SIGN);
        blogUser.setSign(sign);
        return blogUser;
    }
}
