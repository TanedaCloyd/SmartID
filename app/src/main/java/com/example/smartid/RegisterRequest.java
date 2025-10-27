package com.example.smartid;
import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("rfid")
    String rfid;
    @SerializedName("name")
    String name; // Changed from fullName to match SignUp.java logic
    @SerializedName("student_id")
    String student_id;
    @SerializedName("email")
    String email;
    @SerializedName("password")
    String password;
    @SerializedName("program")
    String program;
    @SerializedName("school")
    String school;
    @SerializedName("address")
    String address;
    @SerializedName("contact_number")
    String contact_number;
    @SerializedName("proof_of_enrollment_url")
    String proof_of_enrollment_url;
    @SerializedName("selfie_url")
    String selfie_url;
    @SerializedName("date_of_birth")
    String date_of_birth;
}