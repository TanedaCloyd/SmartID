package com.example.smartid;
import com.google.gson.annotations.SerializedName;

public class User { // Simple user for update response
    @SerializedName("rfid")
    String rfid;
    @SerializedName("name")
    String name;
}