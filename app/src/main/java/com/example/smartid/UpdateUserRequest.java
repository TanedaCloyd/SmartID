package com.example.smartid;
import com.google.gson.annotations.SerializedName;

public class UpdateUserRequest {
    @SerializedName("proof_of_enrollment_url")
    String proof_of_enrollment_url;
    public UpdateUserRequest(String proofUrl) { this.proof_of_enrollment_url = proofUrl; }
}