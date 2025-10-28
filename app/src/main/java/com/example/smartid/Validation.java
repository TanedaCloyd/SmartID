package com.example.smartid;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

// ** Import Glide **
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException; // Import ParseException
import java.text.SimpleDateFormat; // Import SimpleDateFormat
import java.util.Date; // Import Date
import java.util.Locale; // Import Locale

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Validation extends AppCompatActivity {

    // UI Elements
    private ImageButton btnBack;
    private TextView tvValidationStatus, tvCardExpiry, tvAnnualRenewal;
    private ImageView ivProofThumbnail, ivSelfieThumbnail;
    private Button btnUpdateProof, btnUpdateSelfie, btnSubmitUpdates;
    private TextView tvProofUploadStatus, tvSelfieUploadStatus;

    // State & Data
    private SessionManager sessionManager;
    private ApiService apiService;
    private ActivityResultLauncher<String> proofPickerLauncher, selfiePickerLauncher;
    private Uri newProofUri = null;
    private Uri newSelfieUri = null;
    private String currentProofUrl = null; // Store fetched URLs
    private String currentSelfieUrl = null;

    private SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US); // Adjust if your API date format is different
    private SimpleDateFormat displayDateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validation); // Ensure this matches your layout file name

        sessionManager = new SessionManager(getApplicationContext());
        apiService = ApiClient.getClient().create(ApiService.class);

        // Check login
        if (!sessionManager.isLoggedIn()) {
            // Redirect to login
            Intent intent = new Intent(Validation.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        initializeViews();
        setupClickListeners();
        setupFilePickers();
        fetchValidationStatus(); // Fetch data when screen loads
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btn_back);
        tvValidationStatus = findViewById(R.id.tv_validation_status);
        tvCardExpiry = findViewById(R.id.tv_card_expiry);
        tvAnnualRenewal = findViewById(R.id.tv_annual_renewal);
        ivProofThumbnail = findViewById(R.id.iv_proof_thumbnail);
        ivSelfieThumbnail = findViewById(R.id.iv_selfie_thumbnail);
        btnUpdateProof = findViewById(R.id.btn_update_proof);
        btnUpdateSelfie = findViewById(R.id.btn_update_selfie);
        btnSubmitUpdates = findViewById(R.id.btn_submit_updates);
        tvProofUploadStatus = findViewById(R.id.tv_proof_upload_status);
        tvSelfieUploadStatus = findViewById(R.id.tv_selfie_upload_status);

        // Initial state
        tvValidationStatus.setText("Loading...");
        tvCardExpiry.setText("Card Expires: Loading...");
        tvAnnualRenewal.setText("Discount Renewal Due: Loading...");
        btnSubmitUpdates.setEnabled(false); // Disabled until a new file is chosen
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnUpdateProof.setOnClickListener(v -> proofPickerLauncher.launch("image/*")); // Launch proof picker
        btnUpdateSelfie.setOnClickListener(v -> selfiePickerLauncher.launch("image/*")); // Launch selfie picker
        btnSubmitUpdates.setOnClickListener(v -> submitDocumentUpdates());
    }

    private void setupFilePickers() {
        proofPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        newProofUri = uri;
                        // Show selected image locally immediately
                        Glide.with(this).load(uri).placeholder(R.drawable.ic_baseline_credit_card_24).into(ivProofThumbnail);
                        tvProofUploadStatus.setText("New file selected");
                        tvProofUploadStatus.setVisibility(View.VISIBLE);
                        btnSubmitUpdates.setEnabled(true); // Enable submit
                    }
                });

        selfiePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        newSelfieUri = uri;
                        // Show selected image locally immediately
                        Glide.with(this).load(uri).placeholder(R.drawable.ic_baseline_account_person_24).into(ivSelfieThumbnail);
                        tvSelfieUploadStatus.setText("New file selected");
                        tvSelfieUploadStatus.setVisibility(View.VISIBLE);
                        btnSubmitUpdates.setEnabled(true); // Enable submit
                    }
                });
    }

    private void fetchValidationStatus() {
        String rfid = sessionManager.getUserRfid();
        if (rfid == null) { /* Handle not logged in */ return; }

        tvValidationStatus.setText("Loading..."); // Show loading state

        apiService.getStudentProfile(rfid).enqueue(new Callback<StudentProfile>() {
            @Override
            public void onResponse(Call<StudentProfile> call, Response<StudentProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StudentProfile profile = response.body();
                    updateUI(profile);
                } else {
                    Toast.makeText(Validation.this, "Failed to load validation status", Toast.LENGTH_SHORT).show();
                    tvValidationStatus.setText("Error");
                    // Show defaults or error states for other fields
                    tvCardExpiry.setText("Card Expires: Error");
                    tvAnnualRenewal.setText("Discount Renewal Due: Error");
                }
            }

            @Override
            public void onFailure(Call<StudentProfile> call, Throwable t) {
                Toast.makeText(Validation.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ValidationActivity", "Error fetching profile", t);
                tvValidationStatus.setText("Network Error");
                // Show defaults or error states for other fields
                tvCardExpiry.setText("Card Expires: Network Error");
                tvAnnualRenewal.setText("Discount Renewal Due: Network Error");
            }
        });
    }

    private void updateUI(StudentProfile profile) {
        // Display Status and Dates
        tvValidationStatus.setText(profile.status != null ? profile.status : "Unknown");
        // You might need to adjust date formatting based on your backend response
        tvCardExpiry.setText("Card Expires: " + formatDate(profile.card_expiry_date));
        tvAnnualRenewal.setText("Discount Renewal Due: " + formatDate(profile.annual_renewal_date));

        // Store current URLs
        currentProofUrl = profile.proof_of_enrollment_url;
        currentSelfieUrl = profile.selfie_url;

        // Load Images using Glide
        Glide.with(this)
                .load(currentProofUrl)
                .placeholder(R.drawable.ic_baseline_credit_card_24) // Placeholder drawable
                .error(R.drawable.ic_baseline_credit_card_24) // Error drawable
                .into(ivProofThumbnail);

        Glide.with(this)
                .load(currentSelfieUrl)
                .placeholder(R.drawable.ic_baseline_account_person_24) // Placeholder drawable
                .error(R.drawable.ic_baseline_account_person_24) // Error drawable
                .into(ivSelfieThumbnail);

        // Reset upload status indicators
        tvProofUploadStatus.setVisibility(View.GONE);
        tvSelfieUploadStatus.setVisibility(View.GONE);
        newProofUri = null;
        newSelfieUri = null;
        btnSubmitUpdates.setEnabled(false); // Disable submit until new file chosen
    }

    // Helper to format date strings
    private String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return "N/A";
        }
        try {
            // Try parsing the date assuming it comes in API format (e.g., ISO 8601 or YYYY-MM-DD)
            // Adjust apiDateFormat if your backend sends a different format (e.g., "yyyy-MM-dd")
            Date date = apiDateFormat.parse(dateString); // Or use a simpler format if just YYYY-MM-DD
            return displayDateFormat.format(date);
        } catch (ParseException e) {
            Log.w("ValidationActivity", "Could not parse date: " + dateString);
            return dateString; // Return original string if parsing fails
        } catch (Exception e) {
            Log.e("ValidationActivity", "Error formatting date", e);
            return "Invalid Date";
        }
    }


    private void submitDocumentUpdates() {
        if (newProofUri == null && newSelfieUri == null) {
            Toast.makeText(this, "Please select a new file to update.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmitUpdates.setEnabled(false);
        btnSubmitUpdates.setText("Uploading...");

        // Upload Proof if selected
        if (newProofUri != null) {
            tvProofUploadStatus.setText("Uploading proof...");
            tvProofUploadStatus.setVisibility(View.VISIBLE);
            uploadFileAndUpdateProfile(newProofUri, "proof");
        }
        // Upload Selfie if selected (chain after proof or run in parallel carefully)
        // For simplicity, we'll chain them. The second upload starts in the callback of the first.
        else if (newSelfieUri != null) {
            tvSelfieUploadStatus.setText("Uploading selfie...");
            tvSelfieUploadStatus.setVisibility(View.VISIBLE);
            uploadFileAndUpdateProfile(newSelfieUri, "selfie");
        }
    }

    private void uploadFileAndUpdateProfile(Uri fileUri, String fileType) {
        MultipartBody.Part filePart = getFilePartFromUri(fileUri, "imageFile");
        if (filePart == null) {
            Toast.makeText(this, "Error preparing " + fileType + " file.", Toast.LENGTH_SHORT).show();
            resetSubmitButton(); // Re-enable submit button
            return;
        }

        apiService.uploadImage(filePart).enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String newUrl = response.body().imageUrl;
                    Log.d("ValidationActivity", fileType + " uploaded: " + newUrl);

                    // If proof was uploaded, check if selfie also needs uploading
                    if (fileType.equals("proof")) {
                        currentProofUrl = newUrl; // Store new URL locally
                        tvProofUploadStatus.setText("Proof Uploaded!");
                        newProofUri = null; // Mark as uploaded

                        if (newSelfieUri != null) { // If selfie is pending, upload it now
                            tvSelfieUploadStatus.setText("Uploading selfie...");
                            tvSelfieUploadStatus.setVisibility(View.VISIBLE);
                            uploadFileAndUpdateProfile(newSelfieUri, "selfie");
                        } else {
                            // Only proof was changed, now save to backend
                            saveUrlsToBackend();
                        }
                    } else { // Selfie was uploaded
                        currentSelfieUrl = newUrl;
                        tvSelfieUploadStatus.setText("Selfie Uploaded!");
                        newSelfieUri = null; // Mark as uploaded
                        // Since we chained, both should be done now (or only selfie was changed)
                        saveUrlsToBackend();
                    }
                } else {
                    Toast.makeText(Validation.this, fileType + " upload failed. Server error.", Toast.LENGTH_SHORT).show();
                    resetSubmitButton(); // Re-enable on failure
                    if(fileType.equals("proof")) tvProofUploadStatus.setText("Upload Failed"); else tvSelfieUploadStatus.setText("Upload Failed");
                }
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                Toast.makeText(Validation.this, fileType + " upload network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                resetSubmitButton(); // Re-enable on failure
                if(fileType.equals("proof")) tvProofUploadStatus.setText("Network Error"); else tvSelfieUploadStatus.setText("Network Error");
            }
        });
    }

    private void saveUrlsToBackend() {
        String rfid = sessionManager.getUserRfid();
        if (rfid == null) { /* Handle not logged in */ resetSubmitButton(); return; }

        btnSubmitUpdates.setText("Saving...");

        UpdateUserRequest request = new UpdateUserRequest();
        // Only send URLs if they were actually updated (check against fetched URLs or use newProof/SelfieUri flags)
        // For simplicity, we send both potentially updated URLs from currentProofUrl/currentSelfieUrl
        request.proof_of_enrollment_url = currentProofUrl;
        request.selfie_url = currentSelfieUrl;


        apiService.updateStudent(rfid, request).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Validation.this, "Documents Updated Successfully!", Toast.LENGTH_SHORT).show();
                    // Optionally refresh the whole status display
                    // fetchValidationStatus();
                    resetSubmitButton(); // Reset button text, keep disabled
                    btnSubmitUpdates.setEnabled(false); // Ensure it's disabled after save
                    tvProofUploadStatus.setVisibility(View.GONE); // Hide status texts
                    tvSelfieUploadStatus.setVisibility(View.GONE);

                } else {
                    Toast.makeText(Validation.this, "Failed to save URLs to profile.", Toast.LENGTH_SHORT).show();
                    resetSubmitButton();
                    btnSubmitUpdates.setEnabled(true); // Allow retry on save failure
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(Validation.this, "Network error saving URLs: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                resetSubmitButton();
                btnSubmitUpdates.setEnabled(true); // Allow retry on save failure
            }
        });
    }

    private void resetSubmitButton() {
        btnSubmitUpdates.setText("Save Document Updates");
        // Only re-enable if there's still a pending upload (or on failure to allow retry)
        btnSubmitUpdates.setEnabled(newProofUri != null || newSelfieUri != null);
    }


    // --- Utility Methods ---
    // (getFilePartFromUri and getFileName are the same as in SignUp.java)
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
            } catch (Exception e) { Log.e("ValidationActivity", "Error getting filename", e); }
        }
        if (result == null && uri != null) {
            result = uri.getPath();
            if (result != null) {
                int cut = result.lastIndexOf('/');
                if (cut != -1) { result = result.substring(cut + 1); }
            }
        }
        return (result != null && !result.isEmpty()) ? result : "upload_file";
    }
    // --- End Utility Methods ---


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}