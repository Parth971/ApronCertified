package com.example.aproncertified;

public class Details {
    String fName;
    String lName;
    String email;
    String password;
    String phone;
    String ProfilePicUrl;
    String userType;

    public Details() {
    }

    public Details(String fName, String lName, String email, String password, String phone, String profilePicUrl, String userType) {
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        ProfilePicUrl = profilePicUrl;
        this.userType = userType;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getProfilePicUrl() {
        return ProfilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        ProfilePicUrl = profilePicUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
