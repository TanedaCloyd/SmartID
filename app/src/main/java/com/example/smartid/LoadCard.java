package com.example.smartid;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button; // Keep Button
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
    private Button btnOtherAmount; // Changed from RadioButton rbOtherAmount
    private RadioButton rbOthersPayment;
    private LinearLayout layoutOtherBanks;
    private Button btnConfirmLoad;
    private ImageButton btnBack; // Added Back Button
    private Map<Integer, Integer> fixedAmountButtons = new HashMap<>();
    private int selectedLoadAmount = 50; // Default selection

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        initializeViews();
        setupBackButton(); // Added setup for back button
        setupFixedAmountButtons();
        setupCustomAmountInput();
        setupPaymentMethodSelection();

        // Set initial state based on default selection (₱50)
        handleAmountSelection(findViewById(R.id.btn_amount_50), 50); // Ensure initial UI state matches default
        // Ensure confirm button is enabled/disabled correctly on start
        updateConfirmButtonState();

        btnConfirmLoad.setOnClickListener(v -> confirmLoad());
    }

    private void initializeViews() {
        tilCustomAmount = findViewById(R.id.til_custom_amount);
        etCustomAmount = findViewById(R.id.et_custom_amount);
        btnOtherAmount = findViewById(R.id.btn_amount_other);
        rbOthersPayment = findViewById(R.id.rb_others);
        layoutOtherBanks = findViewById(R.id.layout_other_banks);
        btnConfirmLoad = findViewById(R.id.btn_confirm_load);
        btnBack = findViewById(R.id.btn_back); // Initialize back button
    }

    // --- Added Back Button Logic ---
    private void setupBackButton() {
        btnBack.setOnClickListener(v -> {
            finish(); // Closes this activity and returns to the previous one (HomePage)
        });
    }
    // -----------------------------

    private void setupFixedAmountButtons() {
        // Map button IDs to their amounts
        fixedAmountButtons.put(R.id.btn_amount_50, 50);
        fixedAmountButtons.put(R.id.btn_amount_100, 100);
        fixedAmountButtons.put(R.id.btn_amount_200, 200);
        fixedAmountButtons.put(R.id.btn_amount_500, 500);
        fixedAmountButtons.put(R.id.btn_amount_1000, 1000);

        // Set initial state and click listeners
        for (Map.Entry<Integer, Integer> entry : fixedAmountButtons.entrySet()) {
            Button btn = findViewById(entry.getKey());
            // Make sure btn is not null before setting listener
            if (btn != null) {
                btn.setOnClickListener(v -> handleAmountSelection(v, entry.getValue()));
            }
        }

        // Handle 'Other' button
        btnOtherAmount.setOnClickListener(v -> {
            tilCustomAmount.setVisibility(View.VISIBLE);
            etCustomAmount.requestFocus(); // Focus the input field
            selectedLoadAmount = 0; // Clear fixed amount
            // Visually deselect other buttons
            for (int id : fixedAmountButtons.keySet()) {
                View fixedBtn = findViewById(id);
                if (fixedBtn != null) {
                    fixedBtn.setSelected(false);
                }
            }
            // Also need to visually mark the "Other" button as selected
            btnOtherAmount.setSelected(true);
            updateConfirmButtonState(); // Update button state based on custom input
        });
    }

    private void handleAmountSelection(View selectedButton, int amount) {
        // Hide custom input and update selection
        tilCustomAmount.setVisibility(View.GONE);
        etCustomAmount.setText(""); // Clear custom amount input
        tilCustomAmount.setError(null); // Clear any errors
        selectedLoadAmount = amount;

        // Update visual state of all fixed amount buttons
        for (int id : fixedAmountButtons.keySet()) {
            View view = findViewById(id);
            if (view != null) {
                view.setSelected(view.getId() == selectedButton.getId());
            }
        }
        // Also deselect the "Other" button
        btnOtherAmount.setSelected(false);
        updateConfirmButtonState(); // Confirm button should be enabled if a fixed amount is selected
    }

    private void setupCustomAmountInput() {
        etCustomAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tilCustomAmount.getVisibility() != View.VISIBLE) {
                    return; // Only process if the custom field is visible
                }
                updateConfirmButtonState(); // Update based on current input
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Helper method to enable/disable confirm button based on amount selection
    private void updateConfirmButtonState() {
        boolean isCustomAmountVisible = tilCustomAmount.getVisibility() == View.VISIBLE;
        boolean isValid = false;

        if (isCustomAmountVisible) {
            String input = etCustomAmount.getText().toString();
            if (!input.isEmpty()) {
                try {
                    int amount = Integer.parseInt(input);
                    if (amount >= 20) {
                        isValid = true;
                        tilCustomAmount.setError(null); // Clear error if valid
                        selectedLoadAmount = amount; // Update selected amount
                    } else {
                        tilCustomAmount.setError("Minimum load amount is ₱20.");
                        selectedLoadAmount = 0; // Reset amount if invalid
                    }
                } catch (NumberFormatException e) {
                    tilCustomAmount.setError("Invalid amount.");
                    selectedLoadAmount = 0; // Reset amount if invalid
                }
            } else {
                tilCustomAmount.setError(null); // Clear error if empty, but button remains disabled
                selectedLoadAmount = 0; // Reset amount if empty
            }
        } else {
            // If custom amount is not visible, a fixed amount must be selected
            isValid = selectedLoadAmount > 0; // selectedLoadAmount is set in handleAmountSelection
        }

        btnConfirmLoad.setEnabled(isValid);
    }


    private void setupPaymentMethodSelection() {
        RadioGroup rgPaymentMethods = findViewById(R.id.rg_payment_methods);
        rbOthersPayment = findViewById(R.id.rb_others); // Ensure this is initialized
        layoutOtherBanks = findViewById(R.id.layout_other_banks); // Ensure this is initialized

        // Listener for the main payment options (Credit/Debit, Gcash, Maya, Others)
        rgPaymentMethods.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_others) {
                layoutOtherBanks.setVisibility(View.VISIBLE);
            } else {
                layoutOtherBanks.setVisibility(View.GONE);
                // Clear selection within the nested RadioGroup when switching away from Others
                RadioGroup rgOtherBanks = findViewById(R.id.rg_other_banks);
                if (rgOtherBanks != null) {
                    rgOtherBanks.clearCheck();
                }
            }
        });


        // Setup for the nested bank radio buttons (BDO, BPI, Metrobank)
        RadioButton rbBdo = findViewById(R.id.rb_bdo);
        RadioButton rbBpi = findViewById(R.id.rb_bpi);
        RadioButton rbMetrobank = findViewById(R.id.rb_metrobank);

        // When a specific bank is clicked, ensure "Others" stays checked in the main group
        View.OnClickListener bankClickListener = v -> {
            if (!rbOthersPayment.isChecked()) {
                // This check might be redundant if the nested group is only visible when rb_others is checked,
                // but it's safe to keep.
                rbOthersPayment.setChecked(true);
            }
            // The RadioGroup rg_other_banks handles the selection visually.
            // We just need to make sure the main "Others" radio button stays conceptually selected.
        };

        if (rbBdo != null) rbBdo.setOnClickListener(bankClickListener);
        if (rbBpi != null) rbBpi.setOnClickListener(bankClickListener);
        if (rbMetrobank != null) rbMetrobank.setOnClickListener(bankClickListener);

        // Ensure the 'Others' section is hidden initially if 'Others' isn't checked by default
        if (!rbOthersPayment.isChecked()) {
            layoutOtherBanks.setVisibility(View.GONE);
        }
    }


    private String getSelectedPaymentMethod() {
        RadioGroup rgPaymentMethods = findViewById(R.id.rg_payment_methods);
        int selectedId = rgPaymentMethods.getCheckedRadioButtonId();

        if (selectedId == -1) { // No selection
            return "Not Selected";
        }

        RadioButton selectedRadioButton = findViewById(selectedId);
        if (selectedRadioButton == null) return "Not Selected"; // Safety check

        if (selectedId == R.id.rb_others) {
            RadioGroup rgOtherBanks = findViewById(R.id.rg_other_banks);
            if (rgOtherBanks == null) return "Error: Other Banks group not found"; // Should not happen

            int selectedBankId = rgOtherBanks.getCheckedRadioButtonId();
            if (selectedBankId != -1) {
                RadioButton selectedBankButton = findViewById(selectedBankId);
                return selectedBankButton != null ? selectedBankButton.getText().toString() : "Others (Error)";
            } else {
                return "Others (No specific bank selected)";
            }
        } else {
            // For Credit/Debit, Gcash, Maya
            return selectedRadioButton.getText().toString();
        }
    }


    private void confirmLoad() {
        String paymentMethod = getSelectedPaymentMethod();
        int amountToLoad = 0;

        // Determine the amount based on selection type
        if (tilCustomAmount.getVisibility() == View.VISIBLE) {
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
        } else {
            // A fixed amount button must be selected
            amountToLoad = selectedLoadAmount;
            if (amountToLoad <= 0) { // Should be > 0 if a fixed button is selected
                Toast.makeText(this, "Please select a load amount.", Toast.LENGTH_SHORT).show();
                return; // Exit if no valid amount selected
            }
        }


        // Payment method validation
        if (paymentMethod.equals("Not Selected")) {
            Toast.makeText(this, "Please select a payment method.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (paymentMethod.equals("Others (No specific bank selected)")) {
            Toast.makeText(this, "Please select a specific bank under 'Others'.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (paymentMethod.contains("Error")) {
            Toast.makeText(this, "There was an error getting the payment method.", Toast.LENGTH_SHORT).show();
            return;
        }


        // If all validations pass
        Toast.makeText(this,
                String.format("Confirmed load of ₱%d via %s.", amountToLoad, paymentMethod),
                Toast.LENGTH_LONG).show();

        // --- Add navigation back to HomePage after confirmation ---
        // finish(); // Option 1: Simply close LoadCard activity
        // Or
        Intent intent = new Intent(LoadCard.this, HomePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // Clears back stack up to HomePage
        startActivity(intent);
        finish(); // Close LoadCard after starting HomePage
        // --------------------------------------------------------

        // Here you would typically proceed with the actual payment processing
        // (e.g., call an API, integrate with a payment gateway)
    }

    // --- Added onBackPressed ---
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Default behavior is to finish the activity, which is usually correct here.
        // You could add custom behavior if needed.
    }
    // -------------------------

}