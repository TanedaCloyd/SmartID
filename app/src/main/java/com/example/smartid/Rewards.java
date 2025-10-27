package com.example.smartid;

import android.content.Intent;
import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler; // Keep for simulation
import android.os.Looper; // Keep for simulation
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.card.MaterialCardView;

// Imports needed for simulation (even if backend isn't ready)
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// Keep Retrofit imports if simulation uses dummy API calls
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Rewards extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageView profileImage;
    private TextView tvUserName, tvStudentId, tvPointsValue;
    private MaterialCardView rewardCard, pointsCard;
    private Button btnRedeemNow;

    private TextView tvRewardTitle, tvRewardDescription, tvRewardExpiry;

    // --- Add SessionManager ---
    private SessionManager sessionManager;
    // --- Keep ApiService if needed for simulation ---
    private ApiService apiService;
    // ---

    // --- Keep Simulation Data Variables ---
    private String userName_simulated = "Cloyd Harley V. Taneda"; // Original hardcoded name
    private String studentId_simulated = "2025-12345";       // Original hardcoded ID
    private int loyaltyPoints_simulated = 300;
    private String rewardTitle_simulated = "100% OFF";
    private String rewardDescription_simulated = "Train Fare Discount";
    private String rewardExpiry_simulated = "Valid until Sept. 16, 2025";
    private boolean isRewardRedeemed_simulated = false;
    private Handler handler = new Handler(Looper.getMainLooper()); // For simulation delays
    // --- End Simulation Data ---


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards);

        // --- Initialize SessionManager ---
        sessionManager = new SessionManager(getApplicationContext());
        // --- Initialize ApiService (if simulation uses it) ---
        apiService = ApiClient.getClient().create(ApiService.class);


        // Check login status
        if (!sessionManager.isLoggedIn()) {
            Intent intent = new Intent(Rewards.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        initializeViews();
        setupClickListeners();
        loadUserData(); // *** This will now load REAL user data ***
        loadRewardsData_Simulated(); // Call the simulation function
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
        tvRewardTitle = findViewById(R.id.tv_reward_title);
        tvRewardDescription = findViewById(R.id.tv_reward_description);
        tvRewardExpiry = findViewById(R.id.tv_reward_expiry);

        // Initial states
        rewardCard.setVisibility(View.VISIBLE); // Keep visible for simulation
        tvPointsValue.setText("..."); // Loading state
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        // Keep redeem logic pointing to simulation
        btnRedeemNow.setOnClickListener(v -> confirmRedeemReward_Simulated());
        // Keep placeholder listeners
        rewardCard.setOnClickListener(v -> Toast.makeText(this, "Reward details (simulated)", Toast.LENGTH_SHORT).show());
        pointsCard.setOnClickListener(v -> Toast.makeText(this, "Points history (simulated)", Toast.LENGTH_SHORT).show());
    }

    // --- THIS FUNCTION IS NOW CORRECT ---
    // Load REAL basic user info from session
    private void loadUserData() {
        String savedUserName = sessionManager.getUserName();
        // Using email as a placeholder for Student ID
        String savedUserIdentifier = sessionManager.getUserEmail();

        // Set the TextViews with real data
        tvUserName.setText(savedUserName != null ? savedUserName : "User Name");
        tvStudentId.setText("User: " + (savedUserIdentifier != null ? savedUserIdentifier : "N/A"));

        // TODO: Load actual profile image
    }
    // --- END CORRECTION ---


    // --- SIMULATION: Load hardcoded/simulated rewards data ---
    private void loadRewardsData_Simulated() {
        // Display simulated points
        tvPointsValue.setText(String.valueOf(loyaltyPoints_simulated));

        // Display simulated reward details
        tvRewardTitle.setText(rewardTitle_simulated);
        tvRewardDescription.setText(rewardDescription_simulated);
        tvRewardExpiry.setText(rewardExpiry_simulated);

        // Update redeem button state based on simulated data
        updateRedeemButtonState_Simulated();
    }
    // --- END SIMULATION ---


    // --- SIMULATION: Show confirmation before redeeming ---
    private void confirmRedeemReward_Simulated() {
        if (isRewardRedeemed_simulated) {
            Toast.makeText(this, "This reward has already been redeemed (simulated).", Toast.LENGTH_SHORT).show();
            return;
        }
        // Optional points check simulation
        // int pointsCost_simulated = 1000;
        // if (loyaltyPoints_simulated < pointsCost_simulated) {
        //     Toast.makeText(this, "Not enough points (simulated).", Toast.LENGTH_SHORT).show();
        //     return;
        // }

        new AlertDialog.Builder(this)
                .setTitle("Confirm Redemption")
                .setMessage("Are you sure you want to redeem the '" + rewardTitle_simulated + "' reward?")
                .setPositiveButton("Redeem", (dialog, which) -> processRewardRedemption_Simulated())
                .setNegativeButton("Cancel", null)
                .show();
    }
    // --- END SIMULATION ---

    // --- SIMULATION: Process redemption with delay ---
    private void processRewardRedemption_Simulated() {
        Toast.makeText(this, "Processing redemption (simulated)...", Toast.LENGTH_SHORT).show();
        btnRedeemNow.setEnabled(false);
        btnRedeemNow.setText("Processing...");

        // Simulate network delay
        handler.postDelayed(() -> {
            boolean success = true; // Simulate success

            if (success) {
                isRewardRedeemed_simulated = true;
                // Simulate points deduction if needed
                // loyaltyPoints_simulated -= pointsCost_simulated;
                // tvPointsValue.setText(String.valueOf(loyaltyPoints_simulated));

                updateRedeemButtonState_Simulated(); // Update button UI
                showRedemptionSuccess(); // Show success message
            } else {
                showRedemptionFailure(); // Show failure message
                btnRedeemNow.setEnabled(true);
                btnRedeemNow.setText("Redeem Now");
            }
        }, 1500); // Simulate 1.5 second delay
    }
    // --- END SIMULATION ---

    // --- SIMULATION: Update button appearance ---
    private void updateRedeemButtonState_Simulated() {
        if (isRewardRedeemed_simulated) {
            btnRedeemNow.setText("Redeemed");
            btnRedeemNow.setEnabled(false);
            btnRedeemNow.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.darker_gray)));
        } else {
            btnRedeemNow.setText("Redeem Now");
            btnRedeemNow.setEnabled(true);
            btnRedeemNow.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.light_green)));
        }
        btnRedeemNow.setVisibility(View.VISIBLE); // Keep button visible
    }
    // --- END SIMULATION ---

    private void showRedemptionSuccess() {
        Toast.makeText(this, "Reward redeemed successfully! (Simulated)", Toast.LENGTH_LONG).show();
    }

    private void showRedemptionFailure() {
        Toast.makeText(this, "Redemption failed (Simulated).", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove simulation callbacks to prevent memory leaks
        handler.removeCallbacksAndMessages(null);
    }
}