package com.example.smartid;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage); // The main layout

        // --- 1. Setup Main 4 Buttons ---

        MaterialCardView btnLoadCard = findViewById(R.id.btn_load_card);
        MaterialCardView btnTransactions = findViewById(R.id.btn_transactions);
        MaterialCardView btnValidation = findViewById(R.id.btn_validation);
        MaterialCardView btnRewards = findViewById(R.id.btn_rewards);

        // Set click listeners for the main action buttons
        btnLoadCard.setOnClickListener(v -> navigateToScreen("Load Card"));
        btnTransactions.setOnClickListener(v -> navigateToScreen("Transactions"));
        btnValidation.setOnClickListener(v -> navigateToScreen("Validation"));
        btnRewards.setOnClickListener(v -> navigateToScreen("Rewards"));

        // --- 2. Setup Bottom Navigation Bar (Tab Bar) ---

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Ensure 'Home' is selected visually when this activity loads
        bottomNav.setSelectedItemId(R.id.Home_Button);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.Home_Button) {
                // Already on the Home page, do nothing or scroll to top
                Toast.makeText(this, "Welcome Home!", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.Profile_Button) {
                // In a real app, you would start a new Activity or replace a Fragment.
                Toast.makeText(this, "Go to Profile Page", Toast.LENGTH_SHORT).show();
                // Example of navigation to a new activity (ProfilePageActivity)
                // startActivity(new Intent(HomePage.this, ProfilePageActivity.class));
                return true;
            } else if (itemId == R.id.CardDetails_Button) {
                // Placeholder for Card Details view
                Toast.makeText(this, "Show Card Details", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    /**
     * Placeholder for navigating to the secondary pages (Load Card, Transactions, etc.).
     * In a single-Activity structure, this usually loads a new Activity or uses Fragments.
     * @param screenName The name of the screen to navigate to.
     */
    private void navigateToScreen(String screenName) {
        Toast.makeText(this, "Navigating to " + screenName + "...", Toast.LENGTH_SHORT).show();
        // Example: If you built the other pages as Activities:
        // if (screenName.equals("Load Card")) {
        //     startActivity(new Intent(HomePage.this, LoadCardActivity.class));
        // }
    }
}

