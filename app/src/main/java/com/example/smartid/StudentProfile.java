package com.example.smartid;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class StudentProfile {
    @SerializedName("rfid")
    String rfid;
    @SerializedName("name")
    String name;
    @SerializedName("balance")
    double balance;
    @SerializedName("effective_status")
    String status;
    @SerializedName("school")   // --- ADD THIS ---
    String school;

    @SerializedName("student_id") // --- ADD THIS ---
    String student_id;
    @SerializedName("taps")
    List<Tap> taps;
}