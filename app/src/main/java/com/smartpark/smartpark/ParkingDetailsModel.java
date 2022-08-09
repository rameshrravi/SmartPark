package com.smartpark.smartpark;

import java.io.Serializable;

public class ParkingDetailsModel implements Serializable {

    String id;
    String dateTime;
    String dateFormat;
    String timeFormat;
    String plateNo;
    String bayNo;
    String countryCode;
    String phoneNo;
    String emailID;
    String startdatetimeformat;
    String endtimeformat;
    String enddateformat;
    String country_code;
    String hours;
    String amount_collect_USD;
    String currency;
    String amount_owned;
    String parking_marshal_name;
    String precinct_name;
    String supervisor_name;
    String type;
    String valid_until;
    String payment_type;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    String amount;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }

    String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }

    public String getBayNo() {
        return bayNo;
    }

    public void setBayNo(String bayNo) {
        this.bayNo = bayNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartdatetimeformat() {
        return startdatetimeformat;
    }

    public void setStartdatetimeformat(String startdatetimeformat) {
        this.startdatetimeformat = startdatetimeformat;
    }

    public String getEndtimeformat() {
        return endtimeformat;
    }

    public void setEndtimeformat(String endtimeformat) {
        this.endtimeformat = endtimeformat;
    }

    public String getEnddateformat() {
        return enddateformat;
    }

    public void setEnddateformat(String enddateformat) {
        this.enddateformat = enddateformat;
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getAmount_collect_USD() {
        return amount_collect_USD;
    }

    public void setAmount_collect_USD(String amount_collect_USD) {
        this.amount_collect_USD = amount_collect_USD;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAmount_owned() {
        return amount_owned;
    }

    public void setAmount_owned(String amount_owned) {
        this.amount_owned = amount_owned;
    }

    public String getParking_marshal_name() {
        return parking_marshal_name;
    }

    public void setParking_marshal_name(String parking_marshal_name) {
        this.parking_marshal_name = parking_marshal_name;
    }

    public String getPrecinct_name() {
        return precinct_name;
    }

    public void setPrecinct_name(String precinct_name) {
        this.precinct_name = precinct_name;
    }

    public String getSupervisor_name() {
        return supervisor_name;
    }

    public void setSupervisor_name(String supervisor_name) {
        this.supervisor_name = supervisor_name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValid_until() {
        return valid_until;
    }

    public void setValid_until(String valid_until) {
        this.valid_until = valid_until;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }
}
