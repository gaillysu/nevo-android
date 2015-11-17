package com.medcorp.nevo.model;

import java.util.Calendar;

/**
 * Created by karl-john on 17/11/15.
 */
public class ExampleUser {

    private final int id;
    private Calendar birthday;
    private int age;
    private int weight;
    private int length;
    private Calendar created;

    public ExampleUser(int id, Calendar birthday, int age, int weight, int length, Calendar created) {
        this.id = id;
        this.birthday = birthday;
        this.age = age;
        this.weight = weight;
        this.length = length;
        this.created = created;
    }

    public int getId() {
        return id;
    }

    public Calendar getBirthday() {
        return birthday;
    }

    public int getAge() {
        return age;
    }

    public int getWeight() {
        return weight;
    }

    public int getLength() {
        return length;
    }

    public void setBirthday(Calendar birthday) {
        this.birthday = birthday;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
