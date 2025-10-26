package com.example.smartid;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore; // Import for image picker
import android.widget.Button; // Button import is no longer needed unless used elsewhere
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher; // Import for modern activity results
import androidx.activity.result.contract.ActivityResultContracts; // Import for contracts
import androidx.appcompat.app.AppCompatActivity;

public class Profile extends AppCompatActivity {

    // --- Removed bottom navigation Buttons ---
    // private Button cardDetailsButton, homeButton, profileButton;

    private ImageButton btnBack;
    private ImageView profileImage;
    private TextView tvUserName, tvInstitution, tvStudentId;
    private LinearLayout menuEditProfile, menuNotifications, menuSecurity, menuLogOut;

    // --- Added ActivityResultLauncher for image picker ---
    private ActivityResultLauncher<Intent> pickImageLauncher;

    // User data (consider fetching this from a more persistent source)
    private String userName = "Cloyd Harley V. Taneda";
    private String institution = "Technological Institute of the Philippines";
    private String studentId = "2025-12345";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeViews();
        setupImagePicker(); // Initialize the image picker launcher
        setupClickListeners();
        // --- Removed setupBottomNavigation() call ---
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

        // --- Removed findViewById for bottom navigation Buttons ---
        // cardDetailsButton = findViewById(R.id.CardDetails_Button);
        // homeButton = findViewById(R.id.Home_Button);
        // profileButton = findViewById(R.id.Profile_Button);
    }

    // --- Added method to initialize the image picker ---
    private void setupImagePicker() {
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                        android.net.Uri imageUri = result.getData().getData();
                        // Set the selected image to the ImageView
                        profileImage.setImageURI(imageUri);
                        // Here you would typically upload the new image URI/file to your server
                        Toast.makeText(this, "Profile picture updated (locally)", Toast.LENGTH_SHORT).show();
                    }
                });
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

    // --- REMOVED setupBottomNavigation() method ---
    /*
    private void setupBottomNavigation() {
        // ... method content removed ...
    }
    */

    private void loadUserData() {
        // Load user data (in a real app, this would come from SharedPreferences, database, or API)
        tvUserName.setText(userName);
        tvInstitution.setText(institution);
        tvStudentId.setText("Student ID: " + studentId);
        // Load profile image from URL/storage in a real app (e.g., using Glide or Picasso)
        // For now, it uses the placeholder from XML
    }

    private void changeProfilePicture() {
        // --- Updated to use the ActivityResultLauncher ---
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // You could also use Intent.ACTION_GET_CONTENT
        // intent.setType("image/*");
        try {
            pickImageLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open image picker: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void openEditProfile() {
        Toast.makeText(this, "Opening Edit Profile...", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to a real EditProfileActivity
        // Intent intent = new Intent(Profile.this, EditProfileActivity.class);
        // startActivity(intent);
    }

    private void openNotifications() {
        Toast.makeText(this, "Opening Notifications settings...", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to a real NotificationsActivity
        // Intent intent = new Intent(Profile.this, NotificationsActivity.class);
        // startActivity(intent);
    }

    private void openSecurity() {
        Toast.makeText(this, "Opening Security settings...", Toast.LENGTH_SHORT).show();
        // TODO: Navigate to a real SecurityActivity
        // Intent intent = new Intent(Profile.this, SecurityActivity.class);
        // startActivity(intent);
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Log Out", (dialog, which) -> performLogout())
                .setNegativeButton("Cancel", null) // Simpler way to dismiss
                .show();
    }

    private void performLogout() {
        Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();

        // TODO: Implement actual logout logic:
        // 1. Clear user session/token (SharedPreferences, database)
        // 2. Clear sensitive cached data
        // 3. Navigate to LoginActivity and clear the back stack

        // Simulate logout process (keep for now if needed)
        /* new Thread(() -> { ... }).start(); */

        // Example Navigation (Uncomment when LoginActivity exists)
        /*
        Intent intent = new Intent(Profile.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        */
        finish(); // Close profile screen for now
    }

    // --- Removed unused methods related to logout simulation ---
    /*
    private void clearUserSession() { ... }
    private void navigateToLogin() { ... }
    */

    // Method to update user information (e.g., called from EditProfileActivity result)
    public void updateUserInfo(String name, String inst, String id) {
        this.userName = name;
        this.institution = inst;
        this.studentId = id;
        loadUserData(); // Refresh the display
        // TODO: Save updated info persistently
        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
    }

    // Method to get current user info (useful for EditProfileActivity)
    public String[] getUserInfo() {
        return new String[]{userName, institution, studentId};
    }

    // Method to check if user is logged in (should query actual session state)
    public boolean isUserLoggedIn() {
        // TODO: Replace placeholder with real check
        // SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        // return prefs.contains("authToken"); // Example check
        return true; // Placeholder
    }

    // onBackPressed is fine as is, finishes the activity.
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // finish(); // This is the default behavior, no need to call it explicitly
    }
}