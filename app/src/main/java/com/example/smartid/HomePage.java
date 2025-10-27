package com.example.smartid;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
// import android.view.MenuItem; // Not used
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage); // The main layout

        // --- 1. Setup Main 3 Buttons ---
        MaterialCardView btnLoadCard = findViewById(R.id.btn_load_card);
        MaterialCardView btnTransactions = findViewById(R.id.btn_transactions);
        MaterialCardView btnValidation = findViewById(R.id.btn_validation);
        // Assuming btn_rewards CardView was removed from activity_homepage.xml

        // Set click listeners for the main action buttons
        btnLoadCard.setOnClickListener(v -> navigateToScreen("Load Card"));
        btnTransactions.setOnClickListener(v -> navigateToScreen("Transactions"));
        btnValidation.setOnClickListener(v -> navigateToScreen("Validation"));

        // --- 2. Setup Bottom Navigation Bar (Tab Bar) ---
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // --- FIX 1: Removed or changed default selection ---
        // bottomNav.setSelectedItemId(R.id.Home_Button); // DELETE THIS LINE
        // Optional: Select a different default item if desired, e.g.:
        // bottomNav.setSelectedItemId(R.id.Rewards_Button); // Example: Select Rewards by default

        // --- FIX 2: Updated the listener ---
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // Prevent re-navigating if the item is already selected
            if (item.isChecked()) {
                return true;
            }

            // Removed the check for R.id.Home_Button
            if (itemId == R.id.Rewards_Button) { // Added handler for Rewards
                startActivity(new Intent(HomePage.this, Rewards.class));
                return true;
            } else if (itemId == R.id.Profile_Button) {
                startActivity(new Intent(HomePage.this, Profile.class));
                return true;
            } else if (itemId == R.id.CardDetails_Button) {
                // Placeholder for Card Details view - You'll need to create this Activity/Fragment
                Toast.makeText(this, "Show Card Details", Toast.LENGTH_SHORT).show();
                // Example:
                // startActivity(new Intent(HomePage.this, CardDetailsActivity.class));
                return true;
            }
            return false; // Should not happen if all IDs are handled
        });
    }

    /**
     * Navigates to the secondary pages (Load Card, Transactions, etc.).
     * @param screenName The name of the screen to navigate to.
     */
    private void navigateToScreen(String screenName) {
        Toast.makeText(this, "Navigating to " + screenName + "...", Toast.LENGTH_SHORT).show();

        Intent intent = null;
        if (screenName.equals("Load Card")) {
            intent = new Intent(HomePage.this, LoadCard.class);
        } else if (screenName.equals("Transactions")) {
            intent = new Intent(HomePage.this, Transactions.class);
        } else if (screenName.equals("Validation")) {
            intent = new Intent(HomePage.this, Validation.class);
        }
        // Removed the "Rewards" case here since it's now in the bottom nav

        if (intent != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Screen not found: " + screenName, Toast.LENGTH_SHORT).show();
        }
    }
}