package com.example.smartid;
import com.google.gson.annotations.SerializedName;

public class AuthRequest {
    @SerializedName("email")
    String email;
    @SerializedName("password")
    String password;

    public AuthRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}