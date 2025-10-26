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

        // Set click listeners for the main action buttons
        btnLoadCard.setOnClickListener(v -> navigateToScreen("Load Card"));
        btnTransactions.setOnClickListener(v -> navigateToScreen("Transactions"));
        btnValidation.setOnClickListener(v -> navigateToScreen("Validation"));

        // --- 2. Setup Bottom Navigation Bar (Tab Bar) ---

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Ensure 'Home' is selected visually when this activity loads
        bottomNav.setSelectedItemId(R.id.Home_Button);

        // --- THIS IS THE FIXED NAVIGATION ---
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.Home_Button) {
                // Already on the Home page, do nothing or scroll to top
                Toast.makeText(this, "Welcome Home!", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.Profile_Button) {
                // START THE PROFILE ACTIVITY
                startActivity(new Intent(HomePage.this, Profile.class));
                return true;
            } else if (itemId == R.id.CardDetails_Button) {
                // Placeholder for Card Details view
                Toast.makeText(this, "Show Card Details", Toast.LENGTH_SHORT).show();
                // Example:
                // startActivity(new Intent(HomePage.this, CardDetailsActivity.class));
                return true;
            }
            return false;
        });
    }

    /**
     * Navigates to the secondary pages (Load Card, Transactions, etc.).
     * @param screenName The name of the screen to navigate to.
     */
    // --- THIS IS THE FIXED NAVIGATION METHOD ---
    private void navigateToScreen(String screenName) {
        Toast.makeText(this, "Navigating to " + screenName + "...", Toast.LENGTH_SHORT).show();

        // Start the correct Activity based on the button pressed
        if (screenName.equals("Load Card")) {
            startActivity(new Intent(HomePage.this, LoadCard.class));
        } else if (screenName.equals("Transactions")) {
            startActivity(new Intent(HomePage.this, Transactions.class));
        } else if (screenName.equals("Validation")) {
            startActivity(new Intent(HomePage.this, Validation.class));
        } else if (screenName.equals("Rewards")) {
            startActivity(new Intent(HomePage.this, Rewards.class));
        }
    }
}