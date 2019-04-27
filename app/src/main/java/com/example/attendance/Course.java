package com.example.attendance;

public class Course {
    private String course;
    private String teacher;
    private String address;

    public Course(String course,String teacher,String address){
        this.course=course;
        this.teacher=teacher;
        this.address=address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }
}
