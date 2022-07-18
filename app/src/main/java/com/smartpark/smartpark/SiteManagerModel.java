package com.smartpark.smartpark;

import java.io.Serializable;

public class SiteManagerModel implements Serializable {

    String firstName;
    String lastName;
    String loginID;
    String password;
    String phoneNo;
    String logoutPin;
    String id;

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

    public String getLoginID() {
        return loginID;
    }

    public void setLoginID(String loginID) {
        this.loginID = loginID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getLogoutPin() {
        return logoutPin;
    }

    public void setLogoutPin(String logoutPin) {
        this.logoutPin = logoutPin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
