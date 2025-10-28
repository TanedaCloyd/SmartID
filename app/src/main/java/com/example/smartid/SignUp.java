package com.example.smartid;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog; // Import DatePickerDialog
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.DatePicker; // Import DatePicker
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat; // Import SimpleDateFormat
import java.util.Calendar;      // Import Calendar
import java.util.Locale;      // Import Locale

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp extends AppCompatActivity {

    // --- All UI Fields ---
    private TextInputEditText etFullName, etEmail, etStudentId, etSchool, etPassword, etConfirmPassword;
    private TextInputEditText etRfid, etProgram, etAddress, etContactNumber, etDob; // Added new fields

    private Button btnSignUp, btnUploadProof, btnUploadSelfie;
    private TextView tvSignIn, tvProofStatus, tvSelfieStatus;

    private ApiService apiService;
    private SessionManager sessionManager;

    // File Uploading
    private ActivityResultLauncher<String> proofPickerLauncher, selfiePickerLauncher;
    private Uri proofUri = null;
    private Uri selfieUri = null;
    private String proofUrl = null;
    private String selfieUrl = null;

    // Date Picker
    private Calendar selectedDate = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Init API and Session
        apiService = ApiClient.getClient().create(ApiService.class);
        sessionManager = new SessionManager(getApplicationContext());

        // Find ALL views
        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etStudentId = findViewById(R.id.et_student_id);
        etSchool = findViewById(R.id.et_school);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnSignUp = findViewById(R.id.btn_signup);
        tvSignIn = findViewById(R.id.tv_sign_in);

        // --- Connect the new views ---
        etRfid = findViewById(R.id.et_rfid);
        etProgram = findViewById(R.id.et_program);
        etAddress = findViewById(R.id.et_address);
        etContactNumber = findViewById(R.id.et_contact_number);
        etDob = findViewById(R.id.et_dob); // Date of Birth field
        btnUploadProof = findViewById(R.id.btn_upload_proof);
        btnUploadSelfie = findViewById(R.id.btn_upload_selfie);
        tvProofStatus = findViewById(R.id.tv_proof_status);
        tvSelfieStatus = findViewById(R.id.tv_selfie_status);
        // --- End Connecting ---

        // Disable sign up until files are uploaded
        btnSignUp.setEnabled(false);

        setupClickListeners();
        setupFilePickers();
        setupDatePicker(); // Setup the date picker listener
        styleSignInText();
    }

    private void setupClickListeners() {
        btnSignUp.setOnClickListener(v -> performRegistration());
        tvSignIn.setOnClickListener(v -> finish());

        // --- Connect upload button listeners ---
        btnUploadProof.setOnClickListener(v -> proofPickerLauncher.launch("image/*")); // Limit to images
        btnUploadSelfie.setOnClickListener(v -> selfiePickerLauncher.launch("image/*"));// Limit to images
        // --- End Connecting ---

        // Make Date of Birth field non-editable, open picker on click
        etDob.setFocusable(false);
        etDob.setClickable(true);
        etDob.setOnClickListener(v -> showDatePickerDialog());
    }

    private void setupFilePickers() {
        proofPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        proofUri = uri;
                        uploadFile(proofUri, "proof");
                    }
                });

        selfiePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selfieUri = uri;
                        uploadFile(selfieUri, "selfie");
                    }
                });
    }

    // --- NEW: Date Picker Setup ---
    private void setupDatePicker() {
        // Initialize Date of Birth field with current date or placeholder
        updateDobLabel();
    }

    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            selectedDate.set(Calendar.YEAR, year);
            selectedDate.set(Calendar.MONTH, monthOfYear);
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDobLabel();
        };

        new DatePickerDialog(SignUp.this, dateSetListener,
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDobLabel() {
        // Format for display and API (YYYY-MM-DD)
        SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        etDob.setText(apiFormat.format(selectedDate.getTime()));
    }
    // --- End Date Picker ---

    private void uploadFile(Uri fileUri, String fileType) {
        // --- Update status TextViews ---
        if (fileType.equals("proof")) {
            tvProofStatus.setText("Uploading proof...");
            btnUploadProof.setEnabled(false); // Disable during upload
        } else {
            tvSelfieStatus.setText("Uploading selfie...");
            btnUploadSelfie.setEnabled(false); // Disable during upload
        }
        // --- End Update ---

        MultipartBody.Part filePart = getFilePartFromUri(fileUri, "imageFile");
        if (filePart == null) {
            Toast.makeText(this, "Error reading file", Toast.LENGTH_SHORT).show();
            // Re-enable button on error
            if (fileType.equals("proof")) btnUploadProof.setEnabled(true); else btnUploadSelfie.setEnabled(true);
            return;
        }

        apiService.uploadImage(filePart).enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                // Re-enable button after upload attempt
                if (fileType.equals("proof")) btnUploadProof.setEnabled(true); else btnUploadSelfie.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    // --- Update status TextViews on success ---
                    if (fileType.equals("proof")) {
                        proofUrl = response.body().imageUrl;
                        tvProofStatus.setText("Proof Uploaded!");
                    } else {
                        selfieUrl = response.body().imageUrl;
                        tvSelfieStatus.setText("Selfie Uploaded!");
                    }
                    // --- End Update ---
                    checkIfReadyToRegister();
                } else {
                    Toast.makeText(SignUp.this, "Upload failed. Server error.", Toast.LENGTH_SHORT).show();
                    // Reset status on failure
                    if (fileType.equals("proof")) tvProofStatus.setText("Upload Failed"); else tvSelfieStatus.setText("Upload Failed");
                }
            }
            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                // Re-enable button after upload attempt
                if (fileType.equals("proof")) btnUploadProof.setEnabled(true); else btnUploadSelfie.setEnabled(true);

                Toast.makeText(SignUp.this, "Upload network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                // Reset status on failure
                if (fileType.equals("proof")) tvProofStatus.setText("Network Error"); else tvSelfieStatus.setText("Network Error");
            }
        });
    }

    private void checkIfReadyToRegister() {
        // Enable sign up button ONLY when both files are successfully uploaded
        if (proofUrl != null && !proofUrl.isEmpty() && selfieUrl != null && !selfieUrl.isEmpty()) {
            btnSignUp.setEnabled(true);
        } else {
            btnSignUp.setEnabled(false);
        }
    }

    private void performRegistration() {
        // --- Get ALL data from the form ---
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String studentId = etStudentId.getText().toString().trim();
        String school = etSchool.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // --- Get text from the new fields ---
        String rfid = etRfid.getText().toString().trim();
        String program = etProgram.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String contactNumber = etContactNumber.getText().toString().trim();
        String dob = etDob.getText().toString().trim(); // Already in YYYY-MM-DD format
        // --- End Getting Text ---

        // --- Form Validation ---
        // Added checks for new required fields
        if (fullName.isEmpty() || email.isEmpty() || studentId.isEmpty() || rfid.isEmpty() ||
                school.isEmpty() || program.isEmpty() || address.isEmpty() || contactNumber.isEmpty() || dob.isEmpty()) {
            Toast.makeText(SignUp.this, "Please fill out ALL fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (proofUrl == null || selfieUrl == null) {
            Toast.makeText(SignUp.this, "Please upload both required documents", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(SignUp.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(SignUp.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(SignUp.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable button
        btnSignUp.setEnabled(false);
        btnSignUp.setText("Registering...");

        // --- 1. Create the Full Register Request ---
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.name = fullName; // Backend expects 'name', not 'fullName'
        registerRequest.email = email;
        registerRequest.student_id = studentId;
        registerRequest.school = school;
        registerRequest.password = password;
        // --- Add all other fields ---
        registerRequest.rfid = rfid;
        registerRequest.program = program;
        registerRequest.address = address;
        registerRequest.contact_number = contactNumber;
        registerRequest.date_of_birth = dob;
        // --- End Adding Fields ---

        // --- Add the URLs from the uploads ---
        registerRequest.proof_of_enrollment_url = proofUrl;
        registerRequest.selfie_url = selfieUrl;

        // --- 2. Call the API ---
        apiService.register(registerRequest).enqueue(new Callback<LoggedInUser>() {
            @Override
            public void onResponse(Call<LoggedInUser> call, Response<LoggedInUser> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SignUp.this, "Sign Up Successful! Logging in...", Toast.LENGTH_SHORT).show();

                    // --- 3. Automatically log the user in ---
                    autoLogin(email, password);

                } else {
                    // Handle specific errors like duplicates
                    String errorMsg = "Registration failed.";
                    try {
                        // Attempt to parse error body if backend sends JSON like { "error": "message" }
                        if (response.errorBody() != null) {
                            // This part needs adjustment based on actual error response format
                            // For now, just show the response code
                            errorMsg += " Code: " + response.code(); // e.g., 409 for conflict
                        }
                    } catch (Exception e) { /* Ignore parsing error */ }

                    Toast.makeText(SignUp.this, errorMsg, Toast.LENGTH_LONG).show();
                    btnSignUp.setEnabled(true);
                    btnSignUp.setText("Sign Up");
                }
            }

            @Override
            public void onFailure(Call<LoggedInUser> call, Throwable t) {
                Toast.makeText(SignUp.this, "Registration Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                btnSignUp.setEnabled(true);
                btnSignUp.setText("Sign Up");
            }
        });
    }

    private void autoLogin(String email, String password) {
        apiService.login(new AuthRequest(email, password)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    LoggedInUser user = authResponse.user;

                    sessionManager.createLoginSession(
                            authResponse.token,
                            user.rfid,
                            user.name,
                            user.email
                    );

                    // Navigate to HomePage
                    Intent intent = new Intent(SignUp.this, HomePage.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    // If auto-login fails, send to Login screen
                    Toast.makeText(SignUp.this, "Registration successful, please log in manually.", Toast.LENGTH_LONG).show();
                    finish(); // Close SignUp, user will see Login screen next
                }
            }
            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(SignUp.this, "Registration successful, but auto-login failed. Please log in manually.", Toast.LENGTH_LONG).show();
                finish(); // Close SignUp
            }
        });
    }

    // --- UTILITY METHODS (Unchanged) ---
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
            Log.e("SignUpActivity", "Error creating file part", e);
            return null;
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri != null && "content".equals(uri.getScheme())) { // Added null check for uri
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            } catch (Exception e) {
                Log.e("SignUpActivity", "Error getting filename from content URI", e);
                // Fallback or rethrow if needed
            }
        }
        if (result == null && uri != null) { // Added null check for uri
            result = uri.getPath();
            if (result != null) { // Added null check for path
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
        }
        // Provide a default filename if everything else fails
        return (result != null && !result.isEmpty()) ? result : "upload_file";
    }


    private void styleSignInText() {
        String text = tvSignIn.getText().toString();
        SpannableString ss = new SpannableString(text);
        int highlightColor = ContextCompat.getColor(this, R.color.green);
        // Ensure the text still matches "Already have an account? Sign in"
        // Adjust index 26 if the text in activity_signup.xml has changed
        if (text.length() >= 26) {
            ss.setSpan(new ForegroundColorSpan(highlightColor), 26, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        tvSignIn.setText(ss);
    }
}