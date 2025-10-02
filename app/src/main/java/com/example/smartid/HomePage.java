package com.example.smartid;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.material.card.MaterialCardView;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        // --- 1. Setup Main 4 Buttons ---
        MaterialCardView btnLoadCard = findViewById(R.id.btn_load_card);
        MaterialCardView btnTransactions = findViewById(R.id.btn_transactions);
        MaterialCardView btnValidation = findViewById(R.id.btn_validation);
        MaterialCardView btnRewards = findViewById(R.id.btn_rewards);

        // Set click listeners for the main action buttons
        btnLoadCard.setOnClickListener(v -> navigateToScreen("LoadCard"));
        btnTransactions.setOnClickListener(v -> navigateToScreen("Transactions"));
        btnValidation.setOnClickListener(v -> navigateToScreen("Validation"));
        btnRewards.setOnClickListener(v -> navigateToScreen("Rewards"));

        // --- 2. Setup Bottom Navigation Buttons (Same as main action in this case) ---
        Button btnCardDetails = findViewById(R.id.CardDetails_Button);
        Button btnHome = findViewById(R.id.Home_Button);
        Button btnProfile = findViewById(R.id.Profile_Button);

        btnCardDetails.setOnClickListener(v -> navigateToScreen("CardDetails")); // New action for Card Details

        btnHome.setOnClickListener(v -> {
            Toast.makeText(this, "Welcome Home!", Toast.LENGTH_SHORT).show();
            // Since we're on the HomePage, no navigation needed.
        });

        btnProfile.setOnClickListener(v -> {
            Toast.makeText(this, "Go to Profile Page", Toast.LENGTH_SHORT).show();
            // Implement navigation to a ProfileActivity/Fragment here
        });
    }

    // FIX: This method now uses Intents to launch the target Activity
    private void navigateToScreen(String screenName) {
        Class<?> targetActivity;

        switch (screenName) {
            case "LoadCard":
                // Assuming you use a LoadCardActivity to host your LoadCard Fragment.
                targetActivity = LoadCard.class;
                break;
            case "Transactions":
                targetActivity = Transactions.class;
                break;
            case "Validation":
                targetActivity = Validation.class;
                break;
            case "Rewards":
                targetActivity = Rewards.class;
                break;
            case "CardDetails":
                Toast.makeText(this, "Show Card Details", Toast.LENGTH_SHORT).show();
                // If you had a CardDetailsActivity, you would launch it here.
                return;
            default:
                Toast.makeText(this, "Screen not configured: " + screenName, Toast.LENGTH_SHORT).show();
                return;
        }

        // Launch the selected Activity
        Intent intent = new Intent(HomePage.this, targetActivity);
        startActivity(intent);

        Toast.makeText(this, "Navigating to " + screenName + "...", Toast.LENGTH_SHORT).show();
    }
}