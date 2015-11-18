package com.medcorp.nevo.model;

public class User {

    private final int id ;

    private long birthday;

    private int age;

    private int weight;

    private int height;

    private final long createdDate;

    private String remarks;

    public User(int id, long createdDate) {
        this.id = id;
        this.createdDate = createdDate;
    }

    public User(int id, long birthday, int age, int weight, int height, long createdDate, String remarks) {
        this.id = id;
        this.birthday = birthday;
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.createdDate = createdDate;
        this.remarks = remarks;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
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

    public int getId() {
        return id;
    }

    public long getBirthday() {
        return birthday;
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
}
