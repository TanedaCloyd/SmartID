package com.example.smartid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

public class Validation extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageView profileImage;
    private TextView tvUserName, tvStudentId;
    private Button btnProofEnrollment, btnSubmit;
    private LinearLayout cardDetailsButton, homeButton, profileButton;

    // User data
    private String userName = "Cloyd Harley V. Taneda";
    private String studentId = "2025-12345";
    private Uri selectedDocumentUri = null;

    // Activity result launcher for file selection
    private ActivityResultLauncher<String> documentPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validation);

        initializeViews();
        setupClickListeners();
        setupBottomNavigation();
        setupDocumentPicker();
        loadUserData();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btn_back);
        profileImage = findViewById(R.id.profile_image);
        tvUserName = findViewById(R.id.tv_user_name);
        tvStudentId = findViewById(R.id.tv_student_id);
        btnProofEnrollment = findViewById(R.id.btn_proof_enrollment);
        btnSubmit = findViewById(R.id.btn_submit);
        cardDetailsButton = findViewById(R.id.CardDetails_Button);
        homeButton = findViewById(R.id.Home_Button);
        profileButton = findViewById(R.id.Profile_Button);
    }

    private void setupClickListeners() {
        // Back button - return to previous screen
        btnBack.setOnClickListener(v -> finish());

        // Proof of Enrollment button - open document picker
        btnProofEnrollment.setOnClickListener(v -> openDocumentPicker());

        // Submit button - process validation
        btnSubmit.setOnClickListener(v -> submitValidation());

        // Profile image click - option to change profile picture
        profileImage.setOnClickListener(v ->
                Toast.makeText(this, "Profile picture functionality coming soon", Toast.LENGTH_SHORT).show());
    }

    private void setupBottomNavigation() {
        // Card Details button
        cardDetailsButton.setOnClickListener(v ->
                Toast.makeText(this, "Show Card Details", Toast.LENGTH_SHORT).show());

        // Home button
        homeButton.setOnClickListener(v -> {
            startActivity(new Intent(Validation.this, HomePage.class));
            finish();
        });

        // Profile button
        profileButton.setOnClickListener(v ->
                Toast.makeText(this, "Go to Profile Page", Toast.LENGTH_SHORT).show());
    }

    private void setupDocumentPicker() {
        // Initialize the document picker launcher
        documentPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedDocumentUri = uri;
                        onDocumentSelected(uri);
                    }
                }
        );
    }

    private void loadUserData() {
        // Load user data (in a real app, this would come from SharedPreferences, database, or API)
        tvUserName.setText(userName);
        tvStudentId.setText("Student ID: " + studentId);
    }

    private void openDocumentPicker() {
        try {
            // Launch document picker for PDF, images, or documents
            documentPickerLauncher.launch("*/*");
            Toast.makeText(this, "Select your enrollment document", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error opening file picker", Toast.LENGTH_SHORT).show();
        }
    }

    private void onDocumentSelected(Uri uri) {
        // Handle the selected document
        String fileName = getFileName(uri);

        // Update button text to show selected file
        btnProofEnrollment.setText("📁  " + (fileName != null ? fileName : "Document Selected"));

        Toast.makeText(this, "Document selected: " + (fileName != null ? fileName : "Unknown"),
                Toast.LENGTH_SHORT).show();
    }

    private void submitValidation() {
        if (selectedDocumentUri == null) {
            Toast.makeText(this, "Please select a proof of enrollment document first",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Show loading/processing message
        Toast.makeText(this, "Submitting validation request...", Toast.LENGTH_SHORT).show();

        // In a real app, this would upload the document and user data to a server
        processValidationSubmission();
    }

    private void processValidationSubmission() {
        // Simulate processing time
        btnSubmit.setEnabled(false);
        btnSubmit.setText("Processing...");

        // Simulate network request (replace with actual API call)
        new Thread(() -> {
            try {
                Thread.sleep(2000); // Simulate 2 second processing time

                runOnUiThread(() -> {
                    // Reset button state
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("Submit");

                    // Show success message
                    showValidationSuccess();
                });
            } catch (InterruptedException e) {
                runOnUiThread(() -> {
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("Submit");
                    Toast.makeText(this, "Submission failed. Please try again.",
                            Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void showValidationSuccess() {
        Toast.makeText(this, "Validation submitted successfully! " +
                        "You will receive confirmation within 24-48 hours.",
                Toast.LENGTH_LONG).show();

        // Reset the form
        selectedDocumentUri = null;
        btnProofEnrollment.setText("📁  Proof of Enrollment / Registration");

        // Optionally navigate back to home
        // finish();
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } catch (Exception e) {
                // Handle exception
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    // Method to update user information (if called from other activities)
    public void updateUserInfo(String name, String id) {
        this.userName = name;
        this.studentId = id;
        loadUserData();
    }

    // Method to check validation status
    public boolean hasValidationDocument() {
        return selectedDocumentUri != null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}