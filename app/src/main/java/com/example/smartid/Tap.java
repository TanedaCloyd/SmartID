package com.example.smartid;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.Locale; // ** ADD THIS IMPORT **
import android.util.Log;  // ** ADD THIS IMPORT if using Log **

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
        // Log the type being processed (optional, for debugging)
        // Log.d("TapDescription", "Processing tapType: '" + tapType + "'");

        // Handle null values gracefully
        String origin = (originStation != null && !originStation.isEmpty()) ? originStation : "Unknown Station";
        String destination = (destinationStation != null && !destinationStation.isEmpty()) ? destinationStation : "Unknown Station";
        String type = (tapType != null) ? tapType : "null"; // Handle null tapType

        switch (type) {
            case "top_up":
                return "Load via " + origin;
            case "entry":
                return "Entered at: " + origin;
            case "journey": // Handle the 'journey' type sent by the backend
                return "Ride: " + origin + " → " + destination;
            case "admin_correction":
                return "Correction: " + destination;
            case "admin_penalty":
                return "Admin Action (Penalty/Reset)";
            default:
                // Log if the fallback is reached (optional, for debugging)
                // Log.w("TapDescription", "Unknown tapType encountered: '" + type + "'");
                return "Unknown Transaction [" + type + "]"; // Show the unknown type
        }
    }

    // --- ONLY ONE getAmountString() method ---
    public String getAmountString() {
        String type = (tapType != null) ? tapType : "null";
        switch (type) {
            case "top_up":
                // Use Locale.US for consistent formatting
                return "+₱" + String.format(Locale.US, "%.2f", fareAmount);
            case "admin_penalty":
                // Show P0.00 specifically for zero-amount penalties
                if (fareAmount == 0) return "₱0.00";
                // Otherwise treat penalty like a fare deduction
                // FALL THROUGH intentional for non-zero penalty
            case "entry": // Entry usually has no fare amount displayed, but handle if it does
            case "journey": // Treat journey like exit
            case "exit": // Keep exit handling
            case "admin_correction":
                // Use Locale.US for consistent formatting
                return "-₱" + String.format(Locale.US, "%.2f", fareAmount);
            default:
                return "₱?.??";
        }
    }
    // --- END getAmountString() ---

    // isLoad() remains the same
    public boolean isLoad() { return "top_up".equals(tapType); }
}