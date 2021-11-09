package net.cz.blog;

import org.springframework.util.DigestUtils;

import javax.xml.crypto.dsig.DigestMethod;

public class TestCreateMd5Value {
    public static void main(String[] args) {
        //1ef9e42e93bd3a7122d7a463dbcaa49f
        String jwtKeyMd5Str = DigestUtils.md5DigestAsHex("cz_blog_system".getBytes());
        System.out.println(jwtKeyMd5Str);
    }
}
