package com.example.smartid;
import com.google.gson.annotations.SerializedName;

public class LoggedInUser {
    @SerializedName("rfid")
    String rfid;
    @SerializedName("name")
    String name;
    @SerializedName("email")
    String email;
    @SerializedName("student_id")
    String student_id;
    @SerializedName("status")
    String status;
}