package com.witnsoft.interhis.bean;

/**
 * Created by ${liyan} on 2017/5/15.
 */

public class PatChatInfo {
    // 环信用户唯一标识
    private String userName;
    // 用户名
    private String name;
    private String sex;
    private String content;
    private String age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getUserName(){
        return userName;
    }

    public void setUserName(String userName){
        this.userName = userName;
    }

    public PatChatInfo(String userName, String name, String sex, String content, String age) {

        this.userName = userName;
        this.name = name;
        this.sex = sex;
        this.content = content;
        this.age = age;
    }
}
