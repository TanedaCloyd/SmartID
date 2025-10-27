package com.example.smartid; // Make sure this package name matches yours

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class Login extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignUp;
    // private TextView tvForgotPassword; // Removed, this was pointing to the wrong ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Find views by their ID
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvSignUp = findViewById(R.id.tv_sign_up);
        // tvForgotPassword = findViewById(R.id.tv_sign_up); // This was a bug

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
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clears the login history
                startActivity(intent);
                finish(); // Close the login activity
            }
        });

        // Sign Up Text Click
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // --- THIS IS THE UPDATE ---
                // Navigate to your SignUp activity
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });

        // "Forgot Password" listener removed

        // "styleSignUpText()" method removed as text is already set in XML
    }

    // "styleSignUpText()" method removed
}