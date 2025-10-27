package com.example.smartid;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton; // Import ImageButton for back button
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

public class LoadCard extends AppCompatActivity {

    private TextInputLayout tilCustomAmount;
    private TextInputEditText etCustomAmount;
    private Button btnOtherAmount;

    // --- "Others" variables REMOVED ---
    // private RadioButton rbOthersPayment;
    // private LinearLayout layoutOtherBanks;

    private Button btnConfirmLoad;
    private ImageButton btnBack;
    private Map<Integer, Integer> fixedAmountButtons = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        initializeViews();
        setupBackButton();
        setupFixedAmountButtons();
        setupCustomAmountInput();
        // setupPaymentMethodSelection(); // This method is no longer needed

        // Ensure confirm button is disabled correctly on start
        updateConfirmButtonState();

        btnConfirmLoad.setOnClickListener(v -> confirmLoad());
    }

    private void initializeViews() {
        tilCustomAmount = findViewById(R.id.til_custom_amount);
        etCustomAmount = findViewById(R.id.et_custom_amount);
        btnOtherAmount = findViewById(R.id.btn_amount_other);

        // --- "Others" findViewById calls REMOVED ---
        // rbOthersPayment = findViewById(R.id.rb_others);
        // layoutOtherBanks = findViewById(R.id.layout_other_banks);

        btnConfirmLoad = findViewById(R.id.btn_confirm_load);
        btnBack = findViewById(R.id.btn_back);
    }

    private void setupBackButton() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupFixedAmountButtons() {
        // Map button IDs to their amounts
        fixedAmountButtons.put(R.id.btn_amount_50, 50);
        fixedAmountButtons.put(R.id.btn_amount_100, 100);
        fixedAmountButtons.put(R.id.btn_amount_200, 200);
        fixedAmountButtons.put(R.id.btn_amount_500, 500);
        fixedAmountButtons.put(R.id.btn_amount_1000, 1000);

        // Set click listeners
        for (Map.Entry<Integer, Integer> entry : fixedAmountButtons.entrySet()) {
            Button btn = findViewById(entry.getKey());
            if (btn != null) {
                btn.setOnClickListener(v -> handleAmountSelection(v, entry.getValue()));
            }
        }

        // Make "Other" button clear the text field
        btnOtherAmount.setOnClickListener(v -> {
            etCustomAmount.setText(""); // Clear the text field
            etCustomAmount.requestFocus(); // Focus the input field

            // Visually deselect other buttons
            for (int id : fixedAmountButtons.keySet()) {
                View fixedBtn = findViewById(id);
                if (fixedBtn != null) {
                    fixedBtn.setSelected(false);
                }
            }
            btnOtherAmount.setSelected(true);
        });
    }

    private void handleAmountSelection(View selectedButton, int amount) {
        etCustomAmount.setText(String.valueOf(amount));
        tilCustomAmount.setError(null); // Clear any errors

        // Update visual state of all fixed amount buttons
        for (int id : fixedAmountButtons.keySet()) {
            View view = findViewById(id);
            if (view != null) {
                view.setSelected(view.getId() == selectedButton.getId());
            }
        }
        btnOtherAmount.setSelected(false);
    }

    private void setupCustomAmountInput() {
        etCustomAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etCustomAmount.hasFocus()) {
                    clearFixedButtonSelection();
                }
                updateConfirmButtonState(); // Update based on current input
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void clearFixedButtonSelection() {
        for (int id : fixedAmountButtons.keySet()) {
            View fixedBtn = findViewById(id);
            if (fixedBtn != null) {
                fixedBtn.setSelected(false);
            }
        }
        btnOtherAmount.setSelected(true);
    }

    private void updateConfirmButtonState() {
        String input = etCustomAmount.getText().toString();
        boolean isValid = false;

        if (!input.isEmpty()) {
            try {
                int amount = Integer.parseInt(input);
                if (amount >= 20) {
                    isValid = true;
                    tilCustomAmount.setError(null); // Clear error
                } else {
                    tilCustomAmount.setError("Minimum load amount is ₱20.");
                }
            } catch (NumberFormatException e) {
                tilCustomAmount.setError("Invalid amount.");
            }
        } else {
            tilCustomAmount.setError(null); // No error if empty, just disabled
        }
        btnConfirmLoad.setEnabled(isValid);
    }

    // --- setupPaymentMethodSelection method REMOVED as it's no longer needed ---
    /*
    private void setupPaymentMethodSelection() {
        // All logic for "Others" was here
    }
    */

    // --- SIMPLIFIED getSelectedPaymentMethod ---
    private String getSelectedPaymentMethod() {
        RadioGroup rgPaymentMethods = findViewById(R.id.rg_payment_methods);
        int selectedId = rgPaymentMethods.getCheckedRadioButtonId();

        if (selectedId == -1) {
            return "Not Selected";
        }

        RadioButton selectedRadioButton = findViewById(selectedId);
        if (selectedRadioButton == null) {
            return "Not Selected";
        }

        // Return the text of the selected button (e.g., "Credit/Debit Card", "Gcash", "Maya")
        return selectedRadioButton.getText().toString();
    }


    private void confirmLoad() {
        String paymentMethod = getSelectedPaymentMethod();
        int amountToLoad = 0;

        String amountStr = etCustomAmount.getText().toString();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter an amount.", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            amountToLoad = Integer.parseInt(amountStr);
            if (amountToLoad < 20) {
                Toast.makeText(this, "Minimum load amount is ₱20.", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount entered.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Payment method validation
        if (paymentMethod.equals("Not Selected")) {
            Toast.makeText(this, "Please select a payment method.", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- "Others" validation logic REMOVED ---

        // If all validations pass
        Toast.makeText(this,
                String.format("Confirmed load of ₱%d via %s.", amountToLoad, paymentMethod),
                Toast.LENGTH_LONG).show();

        Intent intent = new Intent(LoadCard.this, HomePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}