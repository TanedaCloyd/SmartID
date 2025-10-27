package com.example.smartid;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView; // Make sure this is imported
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Locale; // ** Import Locale **
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoadCard extends AppCompatActivity {

    // SessionManager to get the logged-in user's RFID
    private SessionManager sessionManager;

    private TextInputLayout tilCustomAmount;
    private TextInputEditText etCustomAmount;
    private Button btnOtherAmount;
    private Button btnConfirmLoad;
    private ImageButton btnBack;
    private TextView tvCurrentBalance;
    private Map<Integer, Integer> fixedAmountButtons = new HashMap<>();

    // Networking
    private ApiService apiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        // Initialize SessionManager
        sessionManager = new SessionManager(getApplicationContext());

        // Setup the API service
        apiService = ApiClient.getClient().create(ApiService.class);

        initializeViews();
        setupBackButton();
        setupFixedAmountButtons();
        setupCustomAmountInput();

        updateConfirmButtonState();

        // Set the listener for the confirm button
        btnConfirmLoad.setOnClickListener(v -> confirmLoad());

        // --- FETCH BALANCE ON CREATE ---
        fetchCurrentBalance(); // Fetch and display the balance when the screen starts
        // --- END FETCH ---
    }

    private void initializeViews() {
        tilCustomAmount = findViewById(R.id.til_custom_amount);
        etCustomAmount = findViewById(R.id.et_custom_amount);
        btnOtherAmount = findViewById(R.id.btn_amount_other);
        btnConfirmLoad = findViewById(R.id.btn_confirm_load);
        btnBack = findViewById(R.id.btn_back);
        tvCurrentBalance = findViewById(R.id.tv_current_balance);
        // Set an initial placeholder or loading text
        tvCurrentBalance.setText("Loading..."); // Changed from dashline
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupFixedAmountButtons() {
        fixedAmountButtons.put(R.id.btn_amount_50, 50);
        fixedAmountButtons.put(R.id.btn_amount_100, 100);
        fixedAmountButtons.put(R.id.btn_amount_200, 200);
        fixedAmountButtons.put(R.id.btn_amount_500, 500);
        fixedAmountButtons.put(R.id.btn_amount_1000, 1000);

        for (Map.Entry<Integer, Integer> entry : fixedAmountButtons.entrySet()) {
            Button btn = findViewById(entry.getKey());
            if (btn != null) {
                btn.setOnClickListener(v -> handleAmountSelection(v, entry.getValue()));
            }
        }

        btnOtherAmount.setOnClickListener(v -> {
            etCustomAmount.setText("");
            etCustomAmount.requestFocus();
            for (int id : fixedAmountButtons.keySet()) {
                View fixedBtn = findViewById(id);
                if (fixedBtn != null) fixedBtn.setSelected(false);
            }
            btnOtherAmount.setSelected(true);
        });
    }

    private void handleAmountSelection(View selectedButton, int amount) {
        etCustomAmount.setText(String.valueOf(amount));
        tilCustomAmount.setError(null);
        for (int id : fixedAmountButtons.keySet()) {
            View view = findViewById(id);
            if (view != null) view.setSelected(view.getId() == selectedButton.getId());
        }
        btnOtherAmount.setSelected(false);
    }

    private void setupCustomAmountInput() {
        etCustomAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etCustomAmount.hasFocus()) clearFixedButtonSelection();
                updateConfirmButtonState();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void clearFixedButtonSelection() {
        for (int id : fixedAmountButtons.keySet()) {
            View fixedBtn = findViewById(id);
            if (fixedBtn != null) fixedBtn.setSelected(false);
        }
        btnOtherAmount.setSelected(true);
    }

    private void updateConfirmButtonState() {
        String input = etCustomAmount.getText().toString();
        boolean isValid = false;
        if (!input.isEmpty()) {
            try {
                // Use double here to match confirmLoad logic
                double amount = Double.parseDouble(input);
                if (amount >= 20) {
                    isValid = true;
                    tilCustomAmount.setError(null);
                } else {
                    tilCustomAmount.setError("Minimum load amount is ₱20.");
                }
            } catch (NumberFormatException e) {
                tilCustomAmount.setError("Invalid amount.");
            }
        } else {
            tilCustomAmount.setError(null);
        }
        btnConfirmLoad.setEnabled(isValid);
    }

    private String getSelectedPaymentMethod() {
        RadioGroup rgPaymentMethods = findViewById(R.id.rg_payment_methods);
        int selectedId = rgPaymentMethods.getCheckedRadioButtonId();
        if (selectedId == -1) return "Not Selected";
        RadioButton selectedRadioButton = findViewById(selectedId);
        return selectedRadioButton.getText().toString();
    }

    /**
     * Handles confirming the load amount and making the API call.
     */
    private void confirmLoad() {
        String paymentMethod = getSelectedPaymentMethod();
        double amountToLoad = 0;

        String amountStr = etCustomAmount.getText().toString();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter an amount.", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            amountToLoad = Double.parseDouble(amountStr);
            if (amountToLoad < 20) {
                Toast.makeText(this, "Minimum load amount is ₱20.", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount entered.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (paymentMethod.equals("Not Selected")) {
            Toast.makeText(this, "Please select a payment method.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable button, show loading
        btnConfirmLoad.setEnabled(false);
        btnConfirmLoad.setText("Processing...");

        // 1. Create the request body
        AddBalanceRequest requestBody = new AddBalanceRequest(amountToLoad);

        // 2. Get the logged-in user's RFID
        String rfid = sessionManager.getUserRfid();
        if (rfid == null) {
            Toast.makeText(this, "Error: Not logged in. Please log in again.", Toast.LENGTH_LONG).show();
            btnConfirmLoad.setEnabled(true); // Re-enable button
            btnConfirmLoad.setText("Confirm Load");
            // Optional: Redirect to login
            Intent intent = new Intent(LoadCard.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return;
        }

        // 3. Call the API using enqueue for asynchronous network call
        apiService.addBalance(rfid, requestBody).enqueue(new Callback<AddBalanceResponse>() {
            @Override
            public void onResponse(Call<AddBalanceResponse> call, Response<AddBalanceResponse> response) {
                // Network call finished, re-enable button
                btnConfirmLoad.setEnabled(true);
                btnConfirmLoad.setText("Confirm Load");

                if (response.isSuccessful() && response.body() != null) {
                    // --- Handle successful API response ---
                    String newBalanceString = response.body().newBalance;
                    // Try parsing the new balance to update the display accurately
                    try {
                        double newBalanceValue = Double.parseDouble(newBalanceString);
                        tvCurrentBalance.setText("₱" + String.format(Locale.US, "%.2f", newBalanceValue));
                    } catch(NumberFormatException e) {
                        // Fallback if parsing fails
                        tvCurrentBalance.setText("₱" + newBalanceString);
                    }


                    Toast.makeText(LoadCard.this, "Load successful! New balance is ₱" + newBalanceString, Toast.LENGTH_LONG).show();

                    // Go back to the previous screen (e.g., HomePage)
                    finish();
                } else {
                    // --- Handle API error response (e.g., user not found, server issue) ---
                    String errorMsg = "Load failed. Server responded with error.";
                    if (response.errorBody() != null) {
                        try {
                            errorMsg += " " + response.errorBody().string();
                        } catch (Exception e) { Log.e("LoadCard", "Error parsing error body", e); }
                    } else {
                        errorMsg += " Code: " + response.code();
                    }
                    Toast.makeText(LoadCard.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AddBalanceResponse> call, Throwable t) {
                // --- Handle network failure (e.g., no internet connection) ---
                btnConfirmLoad.setEnabled(true);
                btnConfirmLoad.setText("Confirm Load");

                Log.e("LoadCard", "Network failure: ", t);
                Toast.makeText(LoadCard.this, "Load failed: Could not connect to server. " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed(); // Default behavior finishes the activity
    }

    // --- NEW IMPLEMENTATION: Fetch balance when screen loads ---
    private void fetchCurrentBalance() {
        String rfid = sessionManager.getUserRfid();
        if (rfid != null) {
            tvCurrentBalance.setText("Loading..."); // Show loading state
            apiService.getStudentProfile(rfid).enqueue(new Callback<StudentProfile>() {
                @Override
                public void onResponse(Call<StudentProfile> call, Response<StudentProfile> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        // Display balance formatted to 2 decimal places using Locale.US for consistency
                        tvCurrentBalance.setText("₱" + String.format(Locale.US, "%.2f", response.body().balance));
                    } else {
                        tvCurrentBalance.setText("₱?.??"); // Indicate error fetching
                        Toast.makeText(LoadCard.this, "Could not fetch current balance.", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<StudentProfile> call, Throwable t) {
                    tvCurrentBalance.setText("₱?.??"); // Indicate error fetching
                    Toast.makeText(LoadCard.this, "Network Error fetching balance.", Toast.LENGTH_SHORT).show();
                    Log.e("LoadCard", "Failed to fetch initial balance", t);
                }
            });
        } else {
            // Handle not being logged in
            tvCurrentBalance.setText("₱----.--");
            Toast.makeText(this, "Error: Not logged in.", Toast.LENGTH_SHORT).show();
            // Optional: Redirect to login
            Intent intent = new Intent(LoadCard.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
    // --- END NEW IMPLEMENTATION ---
}