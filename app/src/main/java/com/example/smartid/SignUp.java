package com.example.smartid; // Make sure this package name matches yours

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class SignUp extends AppCompatActivity {

    // --- UPDATED: Removed etConfirmPassword ---
    private TextInputEditText etFullName, etEmail, etStudentId, etSchool, etPassword;
    private Button btnSignUp;
    private TextView tvSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Find views by their ID
        etFullName = findViewById(R.id.et_full_name);
        etEmail = findViewById(R.id.et_email);
        etStudentId = findViewById(R.id.et_student_id);
        etSchool = findViewById(R.id.et_school);
        etPassword = findViewById(R.id.et_password);
        btnSignUp = findViewById(R.id.btn_signup);
        tvSignIn = findViewById(R.id.tv_sign_in);

        // --- Set Click Listeners ---

        // Sign Up Button Click
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get text from all fields
                String fullName = etFullName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String studentId = etStudentId.getText().toString().trim();
                String school = etSchool.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // --- Form Validation (UPDATED) ---
                if (fullName.isEmpty() || email.isEmpty() || studentId.isEmpty() || school.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignUp.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    // --- THIS IS THE FIX ---
                    Toast.makeText(SignUp.this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(SignUp.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                // --- TODO: Add your database registration logic here ---
                // (e.g., call your API to create a new student)

                // If registration is successful:
                Toast.makeText(SignUp.this, "Sign Up Successful!", Toast.LENGTH_SHORT).show();

                // Navigate to HomePage
                Intent intent = new Intent(SignUp.this, HomePage.class);
                // Clear the back stack so user can't go back to Login/SignUp
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish(); // Close the signup activity
            }
        });

        // "Sign in" Text Click
        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to the Login activity
                finish(); // Simply close this activity to return to Login
            }
        });

        // --- Make "Sign in" text colored ---
        styleSignInText();
    }

    // --- THIS METHOD IS NOW FIXED ---
    private void styleSignInText() {
        // 1. Get the full text from the TextView
        String text = tvSignIn.getText().toString(); // This will be "Sign in"
        SpannableString ss = new SpannableString(text);

        // 2. Find the color from your colors.xml
        // (Make sure R.color.green exists in your colors.xml)
        int highlightColor = getResources().getColor(R.color.green);

        // 3. UPDATED: Apply the color to the "Sign in" part (which now starts at index 0)
        ss.setSpan(new ForegroundColorSpan(highlightColor), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvSignIn.setText(ss);
    }
}