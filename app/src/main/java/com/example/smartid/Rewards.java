package com.example.smartid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;

public class Rewards extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageView profileImage;
    private TextView tvUserName, tvStudentId, tvPointsValue;
    private MaterialCardView rewardCard, pointsCard;
    private Button btnRedeemNow;
    private Button cardDetailsButton, homeButton, profileButton;

    // User data
    private String userName = "Cloyd Harley V. Taneda";
    private String studentId = "2025-12345";
    private int loyaltyPoints = 300;

    // Reward data
    private String rewardTitle = "100% OFF";
    private String rewardDescription = "Train Fare Discount";
    private String rewardExpiry = "Valid until Sept. 16, 2025";
    private boolean isRewardRedeemed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards);

        initializeViews();
        setupClickListeners();
        setupBottomNavigation();
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
        cardDetailsButton = findViewById(R.id.CardDetails_Button);
        homeButton = findViewById(R.id.Home_Button);
        profileButton = findViewById(R.id.Profile_Button);
    }

    private void setupClickListeners() {
        // Back button - return to previous screen
        btnBack.setOnClickListener(v -> finish());

        // Redeem Now button
        btnRedeemNow.setOnClickListener(v -> redeemReward());

        // Reward card click - show reward details
        rewardCard.setOnClickListener(v -> showRewardDetails());

        // Points card click - show points history
        pointsCard.setOnClickListener(v -> showPointsHistory());

        // Profile image click
        profileImage.setOnClickListener(v ->
                Toast.makeText(this, "Profile picture functionality coming soon", Toast.LENGTH_SHORT).show());
    }

    private void setupBottomNavigation() {
        // Card Details button
        cardDetailsButton.setOnClickListener(v ->
                Toast.makeText(this, "Show Card Details", Toast.LENGTH_SHORT).show());

        // Home button
        homeButton.setOnClickListener(v -> {
            startActivity(new Intent(Rewards.this, HomePage.class));
            finish();
        });

        // Profile button
        profileButton.setOnClickListener(v ->
                Toast.makeText(this, "Go to Profile Page", Toast.LENGTH_SHORT).show());
    }

    private void loadUserData() {
        // Load user data (in a real app, this would come from SharedPreferences, database, or API)
        tvUserName.setText(userName);
        tvStudentId.setText("Student ID: " + studentId);
    }

    private void loadRewardsData() {
        // Load loyalty points
        tvPointsValue.setText(String.valueOf(loyaltyPoints));

        // Update redeem button state
        updateRedeemButtonState();
    }

    private void redeemReward() {
        if (isRewardRedeemed) {
            Toast.makeText(this, "This reward has already been redeemed", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show confirmation dialog or process redemption
        processRewardRedemption();
    }

    private void processRewardRedemption() {
        // Show processing message
        Toast.makeText(this, "Processing reward redemption...", Toast.LENGTH_SHORT).show();

        // Disable button during processing
        btnRedeemNow.setEnabled(false);
        btnRedeemNow.setText("Processing...");

        // Simulate network request
        new Thread(() -> {
            try {
                Thread.sleep(2000); // Simulate 2 second processing time

                runOnUiThread(() -> {
                    // Mark reward as redeemed
                    isRewardRedeemed = true;

                    // Update UI
                    updateRedeemButtonState();

                    // Show success message
                    showRedemptionSuccess();
                });
            } catch (InterruptedException e) {
                runOnUiThread(() -> {
                    btnRedeemNow.setEnabled(true);
                    btnRedeemNow.setText("Redeem Now");
                    Toast.makeText(this, "Redemption failed. Please try again.",
                            Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void updateRedeemButtonState() {
        if (isRewardRedeemed) {
            btnRedeemNow.setText("Redeemed");
            btnRedeemNow.setEnabled(false);
            btnRedeemNow.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray));
        } else {
            btnRedeemNow.setText("Redeem Now");
            btnRedeemNow.setEnabled(true);
            btnRedeemNow.setBackgroundTintList(getResources().getColorStateList(R.color.white));
        }
    }

    private void showRedemptionSuccess() {
        Toast.makeText(this, "Reward redeemed successfully! " +
                        "Your discount code has been applied to your account.",
                Toast.LENGTH_LONG).show();
    }

    private void showRewardDetails() {
        String message = String.format("Reward: %s\n%s\n%s",
                rewardTitle, rewardDescription, rewardExpiry);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void showPointsHistory() {
        String message = String.format("Current Points: %d\nValue: ₱%.2f\n100 points = ₱10",
                loyaltyPoints, (loyaltyPoints / 100.0) * 10);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    // Method to add points (called when user makes transactions)
    public void addPoints(int points) {
        this.loyaltyPoints += points;
        tvPointsValue.setText(String.valueOf(loyaltyPoints));

        Toast.makeText(this, "Earned " + points + " loyalty points!", Toast.LENGTH_SHORT).show();
    }

    // Method to redeem points for rewards
    public boolean redeemPoints(int pointsCost) {
        if (loyaltyPoints >= pointsCost) {
            loyaltyPoints -= pointsCost;
            tvPointsValue.setText(String.valueOf(loyaltyPoints));
            return true;
        }
        return false;
    }

    // Method to check if reward is available
    public boolean isRewardAvailable() {
        return !isRewardRedeemed;
    }

    // Method to get current points value in pesos
    public double getPointsValueInPesos() {
        return (loyaltyPoints / 100.0) * 10;
    }

    // Method to update user information
    public void updateUserInfo(String name, String id) {
        this.userName = name;
        this.studentId = id;
        loadUserData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}