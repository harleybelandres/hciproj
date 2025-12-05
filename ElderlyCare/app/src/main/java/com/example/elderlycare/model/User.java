package com.example.elderlycare.model;

import java.util.List;

public class User {
    private String uid;
    private String name;
    private String email;
    private String role;
    private String phone;
    private String birthday;
    private List<String> caretakers;
    private List<String> patients;
    private String fcmToken;

    public User() {}

    public User(String uid, String name, String email, String role, String phone, String birthday) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.role = role;
        this.phone = phone;
        this.birthday = birthday;
    }

    // Getters and setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }

    public List<String> getCaretakers() { return caretakers; }
    public void setCaretakers(List<String> caretakers) { this.caretakers = caretakers; }

    public List<String> getPatients() { return patients; }
    public void setPatients(List<String> patients) { this.patients = patients; }

    public String getFcmToken() { return fcmToken; }
    public void setFcmToken(String fcmToken) { this.fcmToken = fcmToken; }
}
