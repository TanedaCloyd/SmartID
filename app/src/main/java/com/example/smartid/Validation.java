package com.example.smartid;

import android.content.Intent;
import android.database.Cursor; // Import Cursor
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns; // Import OpenableColumns
// import android.view.View; // View import no longer needed unless used elsewhere
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
// import android.widget.LinearLayout; // LinearLayout import no longer needed unless used elsewhere
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

public class Validation extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageView profileImage; // Consider making this non-interactive or load actual user image
    private TextView tvUserName, tvStudentId;
    private Button btnProofEnrollment, btnSubmit;
    // --- Removed bottom navigation Buttons ---
    // private Button cardDetailsButton, homeButton, profileButton;

    // User data (better to pass via Intent or load from storage)
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
        setupDocumentPicker(); // Initialize before setting click listeners that use it
        setupClickListeners();
        // --- setupBottomNavigation() call removed ---
        loadUserData();

        // Initially disable submit button until a document is selected
        btnSubmit.setEnabled(false);
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btn_back);
        profileImage = findViewById(R.id.profile_image);
        tvUserName = findViewById(R.id.tv_user_name);
        tvStudentId = findViewById(R.id.tv_student_id);
        btnProofEnrollment = findViewById(R.id.btn_proof_enrollment);
        btnSubmit = findViewById(R.id.btn_submit);

        // --- Removed findViewById for bottom navigation Buttons ---
        // cardDetailsButton = findViewById(R.id.CardDetails_Button);
        // homeButton = findViewById(R.id.Home_Button);
        // profileButton = findViewById(R.id.Profile_Button);
    }

    private void setupDocumentPicker() {
        // Initialize the document picker launcher
        documentPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    // This lambda is called when a file is selected
                    if (uri != null) {
                        selectedDocumentUri = uri;
                        onDocumentSelected(uri);
                        btnSubmit.setEnabled(true); // Enable submit button after selection
                    } else {
                        // User cancelled picker
                        Toast.makeText(this, "File selection cancelled", Toast.LENGTH_SHORT).show();
                        // Keep submit button disabled if no document was previously selected
                        if (selectedDocumentUri == null) {
                            btnSubmit.setEnabled(false);
                        }
                    }
                }
        );
    }

    private void setupClickListeners() {
        // Back button - return to previous screen
        btnBack.setOnClickListener(v -> finish());

        // Proof of Enrollment button - open document picker
        btnProofEnrollment.setOnClickListener(v -> openDocumentPicker());

        // Submit button - process validation
        btnSubmit.setOnClickListener(v -> submitValidation());

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
        // Load actual profile image here if available
    }

    private void openDocumentPicker() {
        try {
            // Launch document picker - restrict types if possible (e.g., PDF and images)
            // documentPickerLauncher.launch("application/pdf|image/*"); // Example filter
            documentPickerLauncher.launch("*/*"); // Allows any file type
            // Toast.makeText(this, "Select your enrollment document", Toast.LENGTH_SHORT).show(); // Maybe redundant
        } catch (android.content.ActivityNotFoundException e) {
            // Handle case where no app can handle the GetContent intent
            Toast.makeText(this, "No app found to pick files.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error opening file picker: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void onDocumentSelected(Uri uri) {
        String fileName = getFileName(uri);
        String buttonText = "📁 " + (fileName != null ? fileName : "Document Selected");

        btnProofEnrollment.setText(buttonText);
        // Optional: Update icon tint or state if using an icon on the button
        // btnProofEnrollment.setIconTint(...)

        Toast.makeText(this, "Selected: " + (fileName != null ? fileName : "Unknown file"), Toast.LENGTH_SHORT).show();
    }

    private void submitValidation() {
        if (selectedDocumentUri == null) {
            Toast.makeText(this, "Please select a proof of enrollment document first.", Toast.LENGTH_LONG).show();
            // Ensure button remains disabled if somehow clicked when URI is null
            btnSubmit.setEnabled(false);
            return;
        }

        // Show loading/processing message
        Toast.makeText(this, "Submitting validation request...", Toast.LENGTH_SHORT).show();
        btnSubmit.setEnabled(false); // Disable during processing
        btnSubmit.setText("Processing...");

        // TODO: Implement actual document upload to your server here
        // This involves network calls (use Volley, Retrofit, etc.)
        // and handling the file URI correctly (using ContentResolver)

        processValidationSubmission(); // Keep simulation for now
    }

    // --- SIMULATION METHOD --- Replace with real upload logic ---
    private void processValidationSubmission() {
        // Simulate network request
        new android.os.Handler(getMainLooper()).postDelayed(() -> {
            // Simulate success or failure
            boolean success = true; // Change to false to test failure

            if (success) {
                showValidationSuccess();
            } else {
                showValidationFailure();
            }
            // Re-enable button regardless of success/failure after processing
            btnSubmit.setText("Submit");
            // Keep it disabled after successful submission? Or allow re-submission?
            // btnSubmit.setEnabled(!success); // Example: Disable after success
            btnSubmit.setEnabled(true); // Re-enable for now

        }, 2000); // Simulate 2 second delay
    }

    private void showValidationSuccess() {
        Toast.makeText(this, "Validation submitted! Confirmation within 24-48 hours.", Toast.LENGTH_LONG).show();

        // Reset the form
        selectedDocumentUri = null;
        btnProofEnrollment.setText("📁  Proof of Enrollment / Registration");
        btnSubmit.setEnabled(false); // Disable after successful submission

        // Optionally navigate back after a short delay
        new android.os.Handler(getMainLooper()).postDelayed(this::finish, 1500); // Close after 1.5s
    }

    private void showValidationFailure() {
        Toast.makeText(this, "Submission failed. Please check connection and try again.", Toast.LENGTH_LONG).show();
        // Button is re-enabled in processValidationSubmission's final block
    }
    // --- END SIMULATION ---

    // Utility method to get filename from URI
    private String getFileName(Uri uri) {
        String result = null;
        if (uri != null && "content".equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } catch (Exception e) {
                // Log error or handle gracefully
                result = "File"; // Fallback name
            }
        }
        if (result == null && uri != null) {
            result = uri.getPath();
            if (result != null) {
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            } else {
                result = "File"; // Fallback name
            }
        }
        return result != null ? result : "File"; // Final fallback
    }

    // Unused methods can be kept or removed
    /*
    public void updateUserInfo(String name, String id) { ... }
    public boolean hasValidationDocument() { ... }
    */

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Default behavior finishes the activity
    }
}