package com.medcorp.model;

public class User {

    private  int id;

    private long birthday;

    private int age = 18;

    private int weight = 77;//kg

    private int height = 175;//cm

    private long createdDate;

    private int sex = 1;//gender,man:1,female:0

    private String firstName;

    private String lastName;

    private String remarks;

    private String nevoUserID;

    private String nevoUserToken;

    private String nevoUserEmail;

    private String validicUserID;

    private String validicUserToken;

    private boolean isLogin;

    private boolean isConnectValidic;

    public User(long createdDate) {
        this.createdDate = createdDate;
    }
    public User(String firstName,String lastName,int sex, long birthday, int age, int weight, int height, long createdDate, String remarks) {
        this.firstName = firstName;
        this.lastName = lastName;
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

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public String getNevoUserID() {
        return nevoUserID;
    }

    public void setNevoUserID(String nevoUserID) {
        this.nevoUserID = nevoUserID;
    }

    public String getNevoUserToken() {
        return nevoUserToken;
    }

    public void setNevoUserToken(String nevoUserToken) {
        this.nevoUserToken = nevoUserToken;
    }

    public String getNevoUserEmail() {
        return nevoUserEmail;
    }

    public void setNevoUserEmail(String nevoUserEmail) {
        this.nevoUserEmail = nevoUserEmail;
    }

    public String getValidicUserID() {
        return validicUserID;
    }

    public void setValidicUserID(String validicUserID) {
        this.validicUserID = validicUserID;
    }

    public String getValidicUserToken() {
        return validicUserToken;
    }

    public void setValidicUserToken(String validicUserToken) {
        this.validicUserToken = validicUserToken;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setIsLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }

    public boolean isConnectValidic() {
        return isConnectValidic;
    }

    public void setIsConnectValidic(boolean isConnectValidic) {
        this.isConnectValidic = isConnectValidic;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }


    public int getConsumedCalories(Steps steps){

        return (int) (2.0 * weight * 3.5 / 200 * (steps.getRunDuration() + steps.getWalkDuration()));
    }

    public double getDistanceTraveled(Steps steps){
        double distance = ((height * 0.45 / 100) * (steps.getSteps())/ 1000);
        return distance;
    }

}
