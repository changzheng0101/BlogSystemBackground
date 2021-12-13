package net.cz.blog.interceptor;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) //方法上的注解
@Retention(RetentionPolicy.RUNTIME) //运行时注解
public @interface checkTooFrequentCommit {
}
