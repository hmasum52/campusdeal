package com.codervai.campusdeal.model;

import com.google.firebase.auth.FirebaseUser;

public class User {
    private String uid;
    private String name;
    private String email;
    private String phone;
    private String profileImageUrl;
    private Campus campus;

    public User() {
    }

    public User(String uid, String name, String email, String phone, String profileImageUrl, Campus campus) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.profileImageUrl = profileImageUrl;
        this.campus = campus;
    }

    public User(FirebaseUser fUser){
        this.uid = fUser.getUid();
        this.name = fUser.getDisplayName();
        this.email = fUser.getEmail();

        if(fUser.getPhotoUrl()!=null){
            this.profileImageUrl = fUser.getPhotoUrl().toString();
        }
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public Campus getCampus() {
        return campus;
    }

    public void setCampus(Campus campus) {
        this.campus = campus;
    }

    // a helper function
    public boolean checkIfProfileIsComplete(){
        return phone != null && campus != null && !phone.isEmpty();
    }
}
