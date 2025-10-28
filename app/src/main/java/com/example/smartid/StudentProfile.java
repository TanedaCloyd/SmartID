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
    @SerializedName("effective_status") // Assuming this is the status field
    String status;
    @SerializedName("school")
    String school;
    @SerializedName("student_id")
    String student_id;
    @SerializedName("points")
    int points;

    // --- ADD THESE FOUR FIELDS ---
    @SerializedName("card_expiry_date") // Make sure JSON key matches backend
            String card_expiry_date; // Keep as String

    @SerializedName("annual_renewal_date") // Make sure JSON key matches backend
    String annual_renewal_date; // Keep as String

    @SerializedName("proof_of_enrollment_url") // Make sure JSON key matches backend
    String proof_of_enrollment_url;

    @SerializedName("selfie_url") // Make sure JSON key matches backend
    String selfie_url;
    // --- END ADD ---

    @SerializedName("taps")
    List<Tap> taps;
}