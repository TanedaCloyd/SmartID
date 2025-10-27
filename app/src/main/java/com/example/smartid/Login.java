package com.example.smartid; // Make sure this package name matches yours

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class Login extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignUp, tvForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Find views by their ID
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvSignUp = findViewById(R.id.tv_sign_up);
        tvForgotPassword = findViewById(R.id.tv_sign_up);

        // --- Set Click Listeners ---

        // Login Button Click
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // Simple validation
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Login.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // --- TODO: Add your authentication logic here ---

                // If login is successful (placeholder logic):
                Toast.makeText(Login.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                // Navigate to HomePage
                Intent intent = new Intent(Login.this, HomePage.class);
                startActivity(intent);
                finish(); // Close the login activity
            }
        });

        // Sign Up Text Click
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // --- TODO: Create and launch your SignUpActivity ---
                Toast.makeText(Login.this, "Sign up clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        // Forgot Password Click
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // --- TODO: Create and launch your ForgotPasswordActivity ---
                Toast.makeText(Login.this, "Forgot password clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        // --- Make "Sign up" text pink ---
        styleSignUpText();
    }

    private void styleSignUpText() {
        String text = "Don't have an Account? Sign up";
        SpannableString ss = new SpannableString(text);

        // Find the color from your colors.xml
        int pinkColor = getResources().getColor(R.color.background_white);

        // Apply the color to the "Sign up" part
        ss.setSpan(new ForegroundColorSpan(pinkColor), 25, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvSignUp.setText(ss);
    }
}