package com.example.smartid;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Profile extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageView profileImage;
    private TextView tvUserName, tvInstitution, tvStudentId;
    private LinearLayout menuEditProfile, menuNotifications, menuSecurity, menuLogOut;

    private ActivityResultLauncher<Intent> pickImageLauncher;
    private SessionManager sessionManager;
    private ApiService apiService; // --- Added ApiService ---

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new SessionManager(getApplicationContext());
        if (!sessionManager.isLoggedIn()) {
            redirectToLogin();
            return;
        }

        // --- Initialize API Service ---
        apiService = ApiClient.getClient().create(ApiService.class);

        initializeViews();
        setupImagePicker();
        setupClickListeners();
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
    }

    private void setupImagePicker() {
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                        android.net.Uri imageUri = result.getData().getData();
                        profileImage.setImageURI(imageUri);
                        Toast.makeText(this, "Profile picture updated locally", Toast.LENGTH_SHORT).show();
                        // TODO: Call uploadImage API here to save it permanently
                    }
                });
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        profileImage.setOnClickListener(v -> changeProfilePicture());
        menuEditProfile.setOnClickListener(v -> Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show());
        menuNotifications.setOnClickListener(v -> Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show());
        menuSecurity.setOnClickListener(v -> Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show());
        menuLogOut.setOnClickListener(v -> showLogoutConfirmation());
    }

    // --- UPDATED: Loads data from API ---
    private void loadUserData() {
        // 1. Show basic info from session immediately
        tvUserName.setText(sessionManager.getUserName());
        tvInstitution.setText("Loading...");

        String rfid = sessionManager.getUserRfid();
        if (rfid == null) return;

        // 2. Fetch full details (like School and Student ID) from the server
        apiService.getStudentProfile(rfid).enqueue(new Callback<StudentProfile>() {
            @Override
            public void onResponse(Call<StudentProfile> call, Response<StudentProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StudentProfile profile = response.body();
                    // Update UI with real data from server
                    tvUserName.setText(profile.name);
                    tvInstitution.setText(profile.school);
                    tvStudentId.setText("Student ID: " + profile.student_id);
                } else {
                    tvInstitution.setText("Error loading school");
                }
            }

            @Override
            public void onFailure(Call<StudentProfile> call, Throwable t) {
                Log.e("Profile", "Network error", t);
                tvInstitution.setText("Network Error");
            }
        });
    }
    // --- END UPDATE ---

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Log Out", (dialog, which) -> {
                    sessionManager.logoutUser();
                    redirectToLogin();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(Profile.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void changeProfilePicture() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        try {
            pickImageLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}