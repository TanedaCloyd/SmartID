package com.example.smartid;
import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @SerializedName("token")
    String token;
    @SerializedName("user")
    LoggedInUser user;
}