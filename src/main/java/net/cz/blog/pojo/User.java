package net.cz.blog.pojo;

import com.sun.org.apache.xml.internal.utils.SerializableLocatorImpl;


//这个包中都是简单的java对象 一般只有属性 构造函数 set和get方法
public class User extends SerializableLocatorImpl {
    private String name;
    private int age;
    private boolean sex;
    private House house;

    public User(String name, int age, boolean sex) {
        this.name = name;
        this.age = age;
        this.sex = sex;
    }

    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }
}
