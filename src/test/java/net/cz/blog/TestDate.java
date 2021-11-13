package net.cz.blog;

import net.cz.blog.utils.TextUtils;

import java.util.Calendar;

public class TestDate {
    public static void main(String[] args) {
        // 时间的位数都是13位
        long currentTimeMillis = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2090, 11, 1);
        long timeInMillis = calendar.getTimeInMillis();

        System.out.println(String.valueOf(currentTimeMillis).length());
        System.out.println(currentTimeMillis);
        System.out.println(String.valueOf(timeInMillis).length());

        System.out.println(TextUtils.isEmpty(""));
    }
}
