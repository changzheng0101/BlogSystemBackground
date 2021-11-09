package net.cz.blog;

import io.jsonwebtoken.Claims;
import net.cz.blog.utils.JwtUtil;

public class TestParesToken {
    public static void main(String[] args) {
        Claims claims = JwtUtil.parseJWT("");
        //==============================================//
        Object id = claims.get("id");
        Object name = claims.get("userName");
        Object role = claims.get("role");
        Object avatar = claims.get("avatar");
        Object email = claims.get("email");

        System.out.println("id == > " + id);
        System.out.println("name == > " + name);
        System.out.println("role == > " + role);
        System.out.println("avatar == > " + avatar);
        System.out.println("email == > " + email);
    }
}
