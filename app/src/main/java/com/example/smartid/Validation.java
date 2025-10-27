package com.example.smartid;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Validation extends AppCompatActivity {

    // This is the SessionManager that gets the LOGGED IN user's rfid
    private SessionManager sessionManager;

    private ImageButton btnBack;
    private ImageView profileImage;
    private TextView tvUserName, tvStudentId;
    private Button btnProofEnrollment, btnSubmit;

    // These are just for display, you can update them later from the session
    private String userName = "Cloyd Harley V. Taneda";
    private String studentId = "2025-12345";
    private Uri selectedDocumentUri = null;

    private ActivityResultLauncher<String> documentPickerLauncher;

    // Networking
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validation);

        // Initialize the SessionManager
        sessionManager = new SessionManager(getApplicationContext());

        // Setup the API service
        apiService = ApiClient.getClient().create(ApiService.class);

        initializeViews();
        setupDocumentPicker();
        setupClickListeners();
        loadUserData();

        btnSubmit.setEnabled(false);
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btn_back);
        profileImage = findViewById(R.id.profile_image);
        tvUserName = findViewById(R.id.tv_user_name);
        tvStudentId = findViewById(R.id.tv_student_id);
        btnProofEnrollment = findViewById(R.id.btn_proof_enrollment);
        btnSubmit = findViewById(R.id.btn_submit);
    }

    private void setupDocumentPicker() {
        documentPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedDocumentUri = uri;
                        onDocumentSelected(uri);
                        btnSubmit.setEnabled(true);
                    } else {
                        Toast.makeText(this, "File selection cancelled", Toast.LENGTH_SHORT).show();
                        if (selectedDocumentUri == null) {
                            btnSubmit.setEnabled(false);
                        }
                    }
                }
        );
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnProofEnrollment.setOnClickListener(v -> openDocumentPicker());

        // THIS IS THE MODIFIED PART
        btnSubmit.setOnClickListener(v -> submitValidation());
    }

    private void loadUserData() {
        // TODO: Later, you can get the user's name from sessionManager.getUserName()
        tvUserName.setText(userName);
        tvStudentId.setText("Student ID: " + studentId);
    }

    private void openDocumentPicker() {
        try {
            documentPickerLauncher.launch("*/*"); // Allows any file type
        } catch (Exception e) {
            Toast.makeText(this, "Error opening file picker: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void onDocumentSelected(Uri uri) {
        String fileName = getFileName(uri);
        String buttonText = "📁 " + (fileName != null ? fileName : "Document Selected");
        btnProofEnrollment.setText(buttonText);
    }

    /**
     * Converts a file Uri to a MultipartBody.Part for Retrofit
     */
    private MultipartBody.Part getFilePartFromUri(Uri uri, String partName) {
        try {
            ContentResolver resolver = getContentResolver();
            String fileName = getFileName(uri);
            String mimeType = resolver.getType(uri);

            File cacheDir = getCacheDir();
            File tempFile = new File(cacheDir, fileName);

            try (InputStream is = resolver.openInputStream(uri);
                 OutputStream os = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer)) > 0) {
                    os.write(buffer, 0, len);
                }
            }

            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), tempFile);
            return MultipartBody.Part.createFormData(partName, tempFile.getName(), requestFile);

        } catch (Exception e) {
            Log.e("ValidationActivity", "Error creating file part", e);
            return null;
        }
    }

    /**
     * This is the main network logic
     */
    private void submitValidation() {
        if (selectedDocumentUri == null) {
            Toast.makeText(this, "Please select a document.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmit.setEnabled(false);
        btnSubmit.setText("Uploading...");

        MultipartBody.Part filePart = getFilePartFromUri(selectedDocumentUri, "imageFile");

        if (filePart == null) {
            Toast.makeText(this, "Error preparing file for upload", Toast.LENGTH_SHORT).show();
            btnSubmit.setEnabled(true);
            btnSubmit.setText("Submit");
            return;
        }

        apiService.uploadImage(filePart).enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String imageUrl = response.body().imageUrl;
                    Log.d("ValidationActivity", "Image uploaded: " + imageUrl);

                    saveImageUrlToProfile(imageUrl);
                } else {
                    Toast.makeText(Validation.this, "Upload failed. Server error.", Toast.LENGTH_SHORT).show();
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("Submit");
                }
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                Toast.makeText(Validation.this, "Upload failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                btnSubmit.setEnabled(true);
                btnSubmit.setText("Submit");
            }
        });
    }

    /**
     * Step 2 of the submission process
     * --- THIS FUNCTION IS NOW CORRECTED ---
     */
    private void saveImageUrlToProfile(String imageUrl) {
        btnSubmit.setText("Saving...");

        UpdateUserRequest requestBody = new UpdateUserRequest(imageUrl);

        // 1. Get the real RFID from the session
        String rfid = sessionManager.getUserRfid();

        // 2. Check if the user is logged in
        if (rfid == null) {
            Toast.makeText(this, "Error: You are not logged in.", Toast.LENGTH_LONG).show();
            btnSubmit.setEnabled(true);
            btnSubmit.setText("Submit");

            // Optional: Send them back to the login screen
            Intent intent = new Intent(Validation.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return;
        }

        // 3. Use the real rfid in the API call
        apiService.updateStudent(rfid, requestBody).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Validation.this, "Validation Submitted Successfully!", Toast.LENGTH_LONG).show();
                    // Reset the form
                    selectedDocumentUri = null;
                    btnProofEnrollment.setText("📁  Proof of Enrollment / Registration");
                    btnSubmit.setText("Submit");
                    btnSubmit.setEnabled(false);
                    finish(); // Go back to home
                } else {
                    Toast.makeText(Validation.this, "Failed to save to profile.", Toast.LENGTH_SHORT).show();
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("Submit");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(Validation.this, "Failed to save: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                btnSubmit.setEnabled(true);
                btnSubmit.setText("Submit");
            }
        });
    }


    // Utility method to get filename from URI
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
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
}