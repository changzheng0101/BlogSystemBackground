package net.cz.blog;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestPasswordEncoder {
    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode("123456");

        //每次生成的密文都不一样，但是每一个都可以用
        //数据库中保存这些密文 与用户输入的密码进行匹配
        //$2a$10$35E8wI8tvkJAdf1s2f74dOdr6TYQNdD5Fde3nV7yf06OVs6lwPcrq
        //$2a$10$vJIXmVn1j3oNGFjZuFPmouYNmpuN6p5q0egPF7p1DOVB8Umvzbhl2
        System.out.println("encode==>"+encode);

        String originPassword = "1234567";
        boolean isMatch = passwordEncoder.matches(originPassword, "$2a$10$35E8wI8tvkJAdf1s2f74dOdr6TYQNdD5Fde3nV7yf06OVs6lwPcrq");
        System.out.println("isMatch-->"+isMatch);
    }
}
