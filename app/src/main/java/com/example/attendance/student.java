package com.example.attendance;

import java.io.Serializable;

public class student implements Serializable {
    private String name;
    private int age;
    private int Sno;
    private String cls;
    private String passward;

    public student(String name,int age,int Sno,String cls,String passward){
        this.name=name;
        this.age=age;
        this.Sno=Sno;
        this.cls=cls;
        this.passward=passward;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCls() {
        return cls;
    }
    public void setCls(String cls) {
        this.cls = cls;
    }
    public int getSno() {
        return Sno;
    }
    public void setSno(int sno) {
        Sno = sno;
    }
    public void setPassward(String passward) {
        this.passward = passward;
    }

    public String getPassward() {
        return passward;
    }
}
