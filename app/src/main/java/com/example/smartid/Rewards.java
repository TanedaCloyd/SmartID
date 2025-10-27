package com.example.smartid;

import android.content.Intent;
import android.app.AlertDialog;
import android.content.res.ColorStateList; // Import ColorStateList
import android.os.Bundle;
import android.os.Handler; // Import Handler
import android.os.Looper; // Import Looper
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat; // Import ContextCompat
import com.google.android.material.card.MaterialCardView;

public class Rewards extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageView profileImage; // Consider making non-interactive or loading actual image
    private TextView tvUserName, tvStudentId, tvPointsValue;
    private MaterialCardView rewardCard, pointsCard;
    private Button btnRedeemNow;
    // --- Removed bottom navigation Buttons ---
    // private Button cardDetailsButton, homeButton, profileButton;

    // Reward card TextViews (added from XML update)
    private TextView tvRewardTitle, tvRewardDescription, tvRewardExpiry;

    // User data (better to pass via Intent or load from storage)
    private String userName = "Cloyd Harley V. Taneda";
    private String studentId = "2025-12345";
    private int loyaltyPoints = 300; // Load from storage

    // Reward data (load from storage/API, this is just one example)
    private String rewardTitle = "100% OFF";
    private String rewardDescription = "Train Fare Discount";
    private String rewardExpiry = "Valid until Sept. 16, 2025";
    private boolean isRewardRedeemed = false; // Load from storage

    private Handler handler = new Handler(Looper.getMainLooper()); // Handler for delays/simulations

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards);

        initializeViews();
        setupClickListeners();
        // --- setupBottomNavigation() call removed ---
        loadUserData();
        loadRewardsData();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btn_back);
        profileImage = findViewById(R.id.profile_image);
        tvUserName = findViewById(R.id.tv_user_name);
        tvStudentId = findViewById(R.id.tv_student_id);
        tvPointsValue = findViewById(R.id.tv_points_value);
        rewardCard = findViewById(R.id.reward_card);
        pointsCard = findViewById(R.id.points_card);
        btnRedeemNow = findViewById(R.id.btn_redeem_now);

        // Initialize reward card text views
        tvRewardTitle = findViewById(R.id.tv_reward_title);
        tvRewardDescription = findViewById(R.id.tv_reward_description);
        tvRewardExpiry = findViewById(R.id.tv_reward_expiry);

        // --- Removed findViewById for bottom navigation Buttons ---
        // cardDetailsButton = findViewById(R.id.CardDetails_Button);
        // homeButton = findViewById(R.id.Home_Button);
        // profileButton = findViewById(R.id.Profile_Button);
    }

    private void setupClickListeners() {
        // Back button - return to previous screen
        btnBack.setOnClickListener(v -> finish());

        // Redeem Now button
        btnRedeemNow.setOnClickListener(v -> redeemReward());

        // Reward card click - show reward details
        rewardCard.setOnClickListener(v -> showRewardDetails());

        // Points card click - show points history/info
        pointsCard.setOnClickListener(v -> showPointsHistory());

        // Profile image click - currently does nothing useful
        /* profileImage.setOnClickListener(v ->
                Toast.makeText(this, "Profile picture functionality coming soon", Toast.LENGTH_SHORT).show());
        */
    }

    // --- setupBottomNavigation() method removed ---
    /*
    private void setupBottomNavigation() {
        // ... method content removed ...
    }
    */


    private void loadUserData() {
        // TODO: In a real app, load this from SharedPreferences, database, or API
        tvUserName.setText(userName);
        tvStudentId.setText("Student ID: " + studentId);
        // Load actual profile image
    }

    private void loadRewardsData() {
        // TODO: Load actual points, reward details, and redeemed status from storage/API
        loyaltyPoints = 300; // Example
        isRewardRedeemed = false; // Example

        // Display points
        tvPointsValue.setText(String.valueOf(loyaltyPoints));

        // Display details for the current reward
        tvRewardTitle.setText(rewardTitle);
        tvRewardDescription.setText(rewardDescription);
        tvRewardExpiry.setText(rewardExpiry);

        // Update redeem button state based on loaded data
        updateRedeemButtonState();
    }

    private void redeemReward() {
        if (isRewardRedeemed) {
            Toast.makeText(this, "This reward has already been redeemed.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Optional: Add points check if needed
        // int pointsCost = 1000; // Example cost for this reward
        // if (loyaltyPoints < pointsCost) {
        //     Toast.makeText(this, "Not enough points to redeem.", Toast.LENGTH_SHORT).show();
        //     return;
        // }

        // Show confirmation dialog before processing
        new AlertDialog.Builder(this)
                .setTitle("Confirm Redemption")
                .setMessage("Are you sure you want to redeem the '" + rewardTitle + "' reward?")
                .setPositiveButton("Redeem", (dialog, which) -> processRewardRedemption())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void processRewardRedemption() {
        Toast.makeText(this, "Processing redemption...", Toast.LENGTH_SHORT).show();
        btnRedeemNow.setEnabled(false);
        btnRedeemNow.setText("Processing...");

        // TODO: Replace simulation with actual API call to redeem the reward
        // This should handle points deduction if applicable and update server state.

        // Simulate network request
        handler.postDelayed(() -> {
            boolean success = true; // Simulate success/failure

            if (success) {
                // TODO: Update persistent storage (e.g., mark reward as redeemed, deduct points)
                isRewardRedeemed = true; // Update local state
                // loyaltyPoints -= pointsCost; // Deduct points if needed
                // tvPointsValue.setText(String.valueOf(loyaltyPoints)); // Update displayed points

                updateRedeemButtonState(); // Update button UI
                showRedemptionSuccess(); // Show success message
            } else {
                showRedemptionFailure(); // Show failure message
                // Re-enable button after failure
                btnRedeemNow.setEnabled(true);
                btnRedeemNow.setText("Redeem Now");
            }
        }, 2000); // Simulate 2-second delay
    }

    private void updateRedeemButtonState() {
        if (isRewardRedeemed) {
            btnRedeemNow.setText("Redeemed");
            btnRedeemNow.setEnabled(false);
            // Use ContextCompat for color resources
            btnRedeemNow.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.darker_gray)));
            btnRedeemNow.setTextColor(ContextCompat.getColor(this, android.R.color.black)); // Adjust text color if needed
        } else {
            btnRedeemNow.setText("Redeem Now");
            btnRedeemNow.setEnabled(true);
            // Use ContextCompat for color resources - Ensure R.color.light_green exists
            btnRedeemNow.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.light_green)));
            btnRedeemNow.setTextColor(ContextCompat.getColor(this, R.color.black)); // Ensure text color is correct
        }
    }

    private void showRedemptionSuccess() {
        Toast.makeText(this, "Reward redeemed successfully!", Toast.LENGTH_LONG).show();
        // Maybe navigate back or update UI further
    }

    private void showRedemptionFailure() {
        Toast.makeText(this, "Redemption failed. Please try again.", Toast.LENGTH_LONG).show();
    }


    private void showRewardDetails() {
        // Could show a more detailed dialog/bottom sheet instead of just a Toast
        String message = String.format("Reward: %s\n%s\n%s",
                rewardTitle, rewardDescription, rewardExpiry);
        new AlertDialog.Builder(this)
                .setTitle("Reward Details")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
        // Toast.makeText(this, message, Toast.LENGTH_LONG).show(); // Alternative
    }

    private void showPointsHistory() {
        // Could show a more detailed dialog/Activity showing points history
        String message = String.format("Current Points: %d\nValue: ₱%.2f\n(100 points = ₱10)",
                loyaltyPoints, getPointsValueInPesos());
        new AlertDialog.Builder(this)
                .setTitle("Loyalty Points")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
        // Toast.makeText(this, message, Toast.LENGTH_LONG).show(); // Alternative
    }

    // --- Utility Methods ---

    // Method to add points (called when user makes transactions - needs integration)
    public void addPoints(int points) {
        // TODO: Update persistent storage
        this.loyaltyPoints += points;
        if (tvPointsValue != null) { // Check if view is ready
            tvPointsValue.setText(String.valueOf(loyaltyPoints));
        }
        // Maybe show a less intrusive notification or update UI subtly
        // Toast.makeText(this, "Earned " + points + " loyalty points!", Toast.LENGTH_SHORT).show();
    }

    // Method to redeem points for rewards (logic might be in processRewardRedemption)
    public boolean canRedeemPoints(int pointsCost) {
        return loyaltyPoints >= pointsCost;
    }

    // Method to check if reward is available
    public boolean isRewardAvailable() {
        // TODO: Load actual status
        return !isRewardRedeemed;
    }

    // Method to get current points value in pesos
    public double getPointsValueInPesos() {
        // Ensure calculation is correct based on your rules
        return (loyaltyPoints / 100.0) * 10.0;
    }

    // Method to update user information (e.g., if profile changes elsewhere)
    public void updateUserInfo(String name, String id) {
        // TODO: Update persistent storage if needed
        this.userName = name;
        this.studentId = id;
        loadUserData(); // Refresh display
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Finishes the activity by default
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove callbacks to prevent memory leaks if simulation is running
        handler.removeCallbacksAndMessages(null);
    }
}