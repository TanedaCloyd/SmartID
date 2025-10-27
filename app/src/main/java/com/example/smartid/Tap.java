package com.example.smartid;
import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class Tap {
    @SerializedName("tap_type")
    String tapType;
    @SerializedName("tap_time")
    Date tapTime;
    @SerializedName("origin_station")
    String originStation;
    @SerializedName("destination_station")
    String destinationStation;
    @SerializedName("fare_amount")
    double fareAmount;

    public String getDescription() {
        if ("top_up".equals(tapType)) return "Loaded Amount via " + originStation;
        if ("entry".equals(tapType)) return "Entry at " + originStation;
        if ("exit".equals(tapType)) return "Ride: " + originStation + " to " + destinationStation;
        if ("admin_correction".equals(tapType)) return "Admin Correction: " + destinationStation;
        if ("admin_penalty".equals(tapType)) return "Admin Action: Penalty/Reset"; // Added
        return "Transaction";
    }
    public String getAmountString() {
        if ("top_up".equals(tapType)) return "+₱" + String.format("%.2f", fareAmount);
        // Consider admin_penalty might have 0 amount
        if ("admin_penalty".equals(tapType) && fareAmount == 0) return "₱0.00";
        return "-₱" + String.format("%.2f", fareAmount);
    }
    public boolean isLoad() { return "top_up".equals(tapType); }
}