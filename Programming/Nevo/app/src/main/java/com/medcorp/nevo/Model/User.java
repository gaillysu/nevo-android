package com.medcorp.nevo.model;

public class User {

    private  int id;

    private long birthday;

    private int age;

    private int weight;

    private int height;

    private final long createdDate;

    private int sex;

    private String firstname;

    private String lastname;

    private String remarks;

    public User(long createdDate) {
        this.createdDate = createdDate;
    }

    public User(String firstname,String lastname,int sex, long birthday, int age, int weight, int height, long createdDate, String remarks) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.sex = sex;
        this.birthday = birthday;
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.createdDate = createdDate;
        this.remarks = remarks;
    }


    public void setAge(int age) {
        this.age = age;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }


    public int getAge() {
        return age;
    }

    public int getWeight() {
        return weight;
    }

    public int getHeight() {
        return height;
    }

    public long getCreatedDate() {
        return createdDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }
}
