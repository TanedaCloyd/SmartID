package com.example.smartid;
import com.google.gson.annotations.SerializedName;

public class UpdateUserRequest {
    // Include both fields, make them optional by not initializing in constructor
    @SerializedName("proof_of_enrollment_url")
    String proof_of_enrollment_url;

    @SerializedName("selfie_url")
    String selfie_url;

    // Constructor is no longer needed, or can be empty
    public UpdateUserRequest() {}
}