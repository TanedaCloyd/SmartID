package com.example.smartid;
import com.google.gson.annotations.SerializedName;

public class AddBalanceRequest {
    @SerializedName("amount")
    double amount;
    public AddBalanceRequest(double amount) { this.amount = amount; }
}