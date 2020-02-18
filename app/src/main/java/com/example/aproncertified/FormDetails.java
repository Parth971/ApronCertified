package com.example.aproncertified;

import java.io.Serializable;
import java.util.List;

public class FormDetails implements Serializable {

    String fNameOfSeller;
    String lNameOfSeller;
    String businessName;
    String status;
    String phoneNum;
    double latitude;
    double longitude;
    String date;
    String security;
    List<Timings> TimingList;
    float ratings;
    String profilePicUri;
    List<InspectorClass> inspectorList;
    String businessType;
    String inspectorAssigned;

    public FormDetails() {
    }

    public FormDetails(String fNameOfSeller, String lNameOfSeller, String businessName, String status,
                       String phoneNum, double latitude, double longitude, String date, String security,
                       List<Timings> timingList, float ratings, String profilePicUri, List<InspectorClass> inspectorList,
                       String businessType, String inspectorAssigned) {
        this.fNameOfSeller = fNameOfSeller;
        this.lNameOfSeller = lNameOfSeller;
        this.businessName = businessName;
        this.status = status;
        this.phoneNum = phoneNum;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.security = security;
        TimingList = timingList;
        this.ratings = ratings;
        this.profilePicUri = profilePicUri;
        this.inspectorList = inspectorList;
        this.businessType = businessType;
        this.inspectorAssigned = inspectorAssigned;
    }

    public String getfNameOfSeller() {
        return fNameOfSeller;
    }

    public void setfNameOfSeller(String fNameOfSeller) {
        this.fNameOfSeller = fNameOfSeller;
    }

    public String getlNameOfSeller() {
        return lNameOfSeller;
    }

    public void setlNameOfSeller(String lNameOfSeller) {
        this.lNameOfSeller = lNameOfSeller;
    }

    public String getInspectorAssigned() {
        return inspectorAssigned;
    }

    public void setInspectorAssigned(String inspectorAssigned) {
        this.inspectorAssigned = inspectorAssigned;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public List<InspectorClass> getInspectorList() {
        return inspectorList;
    }

    public void setInspectorList(List<InspectorClass> inspectorList) {
        this.inspectorList = inspectorList;
    }

    public String getProfilePicUri() {
        return profilePicUri;
    }

    public void setProfilePicUri(String profilePicUri) {
        this.profilePicUri = profilePicUri;
    }

    public float getRatings() {
        return ratings;
    }

    public void setRatings(float ratings) {
        this.ratings = ratings;
    }

    public List<Timings> getTimingList() {
        return TimingList;
    }

    public void setTimingList(List<Timings> timingList) {
        TimingList = timingList;
    }

    public String getSecurity() {
        return security;
    }

    public void setSecurity(String security) {
        this.security = security;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }



}
