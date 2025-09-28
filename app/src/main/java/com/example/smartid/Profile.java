package com.example.smartid;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Profile extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageView profileImage;
    private TextView tvUserName, tvInstitution, tvStudentId;
    private LinearLayout menuEditProfile, menuNotifications, menuSecurity, menuLogOut;
    private Button cardDetailsButton, homeButton, profileButton;

    // User data
    private String userName = "Cloyd Harley V. Taneda";
    private String institution = "Technological Institute of the Philippines";
    private String studentId = "2025-12345";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeViews();
        setupClickListeners();
        setupBottomNavigation();
        loadUserData();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btn_back);
        profileImage = findViewById(R.id.profile_image);
        tvUserName = findViewById(R.id.tv_user_name);
        tvInstitution = findViewById(R.id.tv_institution);
        tvStudentId = findViewById(R.id.tv_student_id);
        menuEditProfile = findViewById(R.id.menu_edit_profile);
        menuNotifications = findViewById(R.id.menu_notifications);
        menuSecurity = findViewById(R.id.menu_security);
        menuLogOut = findViewById(R.id.menu_log_out);
        cardDetailsButton = findViewById(R.id.CardDetails_Button);
        homeButton = findViewById(R.id.Home_Button);
        profileButton = findViewById(R.id.Profile_Button);
    }

    private void setupClickListeners() {
        // Back button - return to previous screen
        btnBack.setOnClickListener(v -> finish());

        // Profile image click - option to change profile picture
        profileImage.setOnClickListener(v -> changeProfilePicture());

        // Menu items
        menuEditProfile.setOnClickListener(v -> openEditProfile());
        menuNotifications.setOnClickListener(v -> openNotifications());
        menuSecurity.setOnClickListener(v -> openSecurity());
        menuLogOut.setOnClickListener(v -> showLogoutConfirmation());
    }

    private void setupBottomNavigation() {
        // Card Details button
        cardDetailsButton.setOnClickListener(v ->
                Toast.makeText(this, "Show Card Details", Toast.LENGTH_SHORT).show());

        // Home button
        homeButton.setOnClickListener(v -> {
            startActivity(new Intent(Profile.this, HomePage.class));
            finish();
        });

        // Profile button - already on profile, show message
        profileButton.setOnClickListener(v ->
                Toast.makeText(this, "You are already on Profile page", Toast.LENGTH_SHORT).show());
    }

    private void loadUserData() {
        // Load user data (in a real app, this would come from SharedPreferences, database, or API)
        tvUserName.setText(userName);
        tvInstitution.setText(institution);
        tvStudentId.setText("Student ID: " + studentId);
    }

    private void changeProfilePicture() {
        Toast.makeText(this, "Profile picture change functionality coming soon", Toast.LENGTH_SHORT).show();
        // In a real app, this would open image picker or camera
        // Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openEditProfile() {
        Toast.makeText(this, "Opening Edit Profile...", Toast.LENGTH_SHORT).show();
        // In a real app, this would navigate to EditProfileActivity
        // Intent intent = new Intent(Profile.this, EditProfileActivity.class);
        // startActivity(intent);
    }

    private void openNotifications() {
        Toast.makeText(this, "Opening Notifications settings...", Toast.LENGTH_SHORT).show();
        // In a real app, this would navigate to NotificationsActivity
        // Intent intent = new Intent(Profile.this, NotificationsActivity.class);
        // startActivity(intent);
    }

    private void openSecurity() {
        Toast.makeText(this, "Opening Security settings...", Toast.LENGTH_SHORT).show();
        // In a real app, this would navigate to SecurityActivity
        // Intent intent = new Intent(Profile.this, SecurityActivity.class);
        // startActivity(intent);
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Log Out", (dialog, which) -> performLogout())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void performLogout() {
        // Show logout process
        Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();

        // In a real app, this would:
        // 1. Clear user session/preferences
        // 2. Clear cached data
        // 3. Navigate to login screen

        // Simulate logout process
        new Thread(() -> {
            try {
                Thread.sleep(1500); // Simulate logout process

                runOnUiThread(() -> {
                    // Clear user data (in real app, clear SharedPreferences, etc.)
                    clearUserSession();

                    // Navigate to login screen
                    navigateToLogin();
                });
            } catch (InterruptedException e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Logout failed. Please try again.",
                                Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void clearUserSession() {
        // In a real app, clear SharedPreferences, database, etc.
        // SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        // prefs.edit().clear().apply();

        Toast.makeText(this, "Session cleared successfully", Toast.LENGTH_SHORT).show();
    }

    private void navigateToLogin() {
        Toast.makeText(this, "Redirecting to login screen...", Toast.LENGTH_SHORT).show();

        // In a real app, navigate to LoginActivity
        // Intent intent = new Intent(Profile.this, LoginActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // startActivity(intent);
        // finish();

        // For now, just close the app or go to homepage
        finish();
    }

    // Method to update user information
    public void updateUserInfo(String name, String inst, String id) {
        this.userName = name;
        this.institution = inst;
        this.studentId = id;
        loadUserData();
        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
    }

    // Method to get current user info
    public String[] getUserInfo() {
        return new String[]{userName, institution, studentId};
    }

    // Method to check if user is logged in
    public boolean isUserLoggedIn() {
        // In a real app, check SharedPreferences or session status
        return true; // Placeholder
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}