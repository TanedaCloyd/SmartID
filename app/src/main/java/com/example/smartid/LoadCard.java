package com.example.smartid;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button; // Keep Button
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
    // --- FIX 1: Change variable type and name ---
    private Button btnOtherAmount; // Changed from RadioButton rbOtherAmount
    private RadioButton rbOthersPayment;
    private LinearLayout layoutOtherBanks;
    private Button btnConfirmLoad;
    private Map<Integer, Integer> fixedAmountButtons = new HashMap<>();
    private int selectedLoadAmount = 50; // Default selection

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        initializeViews();
        setupFixedAmountButtons();
        setupCustomAmountInput();
        setupPaymentMethodSelection();

        btnConfirmLoad.setOnClickListener(v -> confirmLoad());
    }

    private void initializeViews() {
        tilCustomAmount = findViewById(R.id.til_custom_amount);
        etCustomAmount = findViewById(R.id.et_custom_amount);
        // --- FIX 2: Use the new variable name ---
        btnOtherAmount = findViewById(R.id.btn_amount_other); // Changed from rbOtherAmount
        rbOthersPayment = findViewById(R.id.rb_others);
        layoutOtherBanks = findViewById(R.id.layout_other_banks);
        btnConfirmLoad = findViewById(R.id.btn_confirm_load);
    }

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
            btn.setOnClickListener(v -> handleAmountSelection(v, entry.getValue()));
        }

        // Initial button selection (e.g., 50)
        findViewById(R.id.btn_amount_50).setSelected(true);

        // Handle 'Other' button
        // --- FIX 3: Use the new variable name ---
        btnOtherAmount.setOnClickListener(v -> { // Changed from rbOtherAmount
            tilCustomAmount.setVisibility(View.VISIBLE);
            selectedLoadAmount = 0; // Clear fixed amount
            // Visually deselect other buttons
            for (int id : fixedAmountButtons.keySet()) {
                findViewById(id).setSelected(false);
            }
            // Also need to visually mark the "Other" button as selected
            // (assuming your AmountButton style handles the selected state visually)
            btnOtherAmount.setSelected(true); // Added this line
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
        btnOtherAmount.setSelected(false); // Added this line
    }

    private void setupCustomAmountInput() {
        etCustomAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tilCustomAmount.getVisibility() != View.VISIBLE) {
                    return; // Only validate if the custom field is visible
                }
                try {
                    // Prevent leading zeros unless it's the only digit
                    String input = s.toString();
                    if (input.length() > 1 && input.startsWith("0")) {
                        input = input.substring(1);
                        etCustomAmount.setText(input);
                        etCustomAmount.setSelection(input.length()); // Move cursor to end
                    }

                    if (input.isEmpty()) {
                        tilCustomAmount.setError(null); // Clear error if empty
                        btnConfirmLoad.setEnabled(false); // Disable if empty
                        selectedLoadAmount = 0; // Reset selected amount
                        return;
                    }

                    int amount = Integer.parseInt(input);
                    if (amount < 20) {
                        tilCustomAmount.setError("Minimum load amount is ₱20.");
                        btnConfirmLoad.setEnabled(false);
                        selectedLoadAmount = 0; // Reset selected amount if invalid
                    } else {
                        tilCustomAmount.setError(null);
                        selectedLoadAmount = amount; // Update selected amount only if valid
                        btnConfirmLoad.setEnabled(true);
                    }
                } catch (NumberFormatException e) {
                    // This handles cases where the input might become non-numeric temporarily
                    // during typing, or if it exceeds Integer.MAX_VALUE
                    tilCustomAmount.setError("Invalid amount.");
                    btnConfirmLoad.setEnabled(false);
                    selectedLoadAmount = 0; // Reset selected amount
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        // Initially disable button if custom amount field is visible but empty
        if (tilCustomAmount.getVisibility() == View.VISIBLE && etCustomAmount.getText().toString().isEmpty()) {
            btnConfirmLoad.setEnabled(false);
        }
    }


    private void setupPaymentMethodSelection() {
        RadioGroup rgPaymentMethods = findViewById(R.id.rg_payment_methods);

        // Listener for the main payment options (Credit/Debit, Gcash, Maya, Others)
        rgPaymentMethods.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_others) {
                layoutOtherBanks.setVisibility(View.VISIBLE);
            } else {
                layoutOtherBanks.setVisibility(View.GONE);
                // Optional: Clear selection within the nested RadioGroup if needed
                // RadioGroup rgOtherBanks = findViewById(R.id.rg_other_banks); // Assuming you add an ID to the nested group
                // rgOtherBanks.clearCheck();
            }
        });


        // Setup for the nested bank radio buttons (BDO, BPI, Metrobank)
        RadioButton rbBdo = findViewById(R.id.rb_bdo);
        RadioButton rbBpi = findViewById(R.id.rb_bpi);
        RadioButton rbMetrobank = findViewById(R.id.rb_metrobank);

        // When a specific bank is clicked, ensure "Others" stays checked
        View.OnClickListener bankClickListener = v -> {
            if (!rbOthersPayment.isChecked()) {
                rbOthersPayment.setChecked(true); // This will trigger the listener above to show the banks
            }
            // You might want to store which specific bank was selected here
            // e.g., String selectedBank = ((RadioButton) v).getText().toString();
        };

        rbBdo.setOnClickListener(bankClickListener);
        rbBpi.setOnClickListener(bankClickListener);
        rbMetrobank.setOnClickListener(bankClickListener);

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

        if (selectedId == R.id.rb_others) {
            // Check the nested RadioGroup for BDO, BPI, Metrobank
            RadioGroup rgOtherBanks = layoutOtherBanks.findViewById(R.id.rg_other_banks); // **You need to add this ID in your XML**
            int selectedBankId = rgOtherBanks.getCheckedRadioButtonId();
            if (selectedBankId != -1) {
                RadioButton selectedBankButton = findViewById(selectedBankId);
                return selectedBankButton.getText().toString(); // Return "BDO", "BPI", or "Metrobank"
            } else {
                return "Others (No specific bank selected)"; // Or just "Others"
            }
        } else {
            // For Credit/Debit, Gcash, Maya
            return selectedRadioButton.getText().toString();
        }
    }


    private void confirmLoad() {
        // --- FIX 4: Get the actual selected payment method ---
        String paymentMethod = getSelectedPaymentMethod();

        // Amount validation
        int currentAmount = 0;
        if (tilCustomAmount.getVisibility() == View.VISIBLE) {
            try {
                currentAmount = Integer.parseInt(etCustomAmount.getText().toString());
            } catch (NumberFormatException e) {
                // Handle empty or invalid input in the custom field
                Toast.makeText(this, "Please enter a valid amount (Min ₱20).", Toast.LENGTH_SHORT).show();
                return;
            }
            if (currentAmount < 20) {
                Toast.makeText(this, "Minimum load amount is ₱20.", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            // Use the fixed amount if custom field is hidden
            currentAmount = selectedLoadAmount;
            if (currentAmount == 0) { // Should not happen if logic is correct, but safe check
                Toast.makeText(this, "Please select or enter a load amount.", Toast.LENGTH_SHORT).show();
                return;
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


        // If all validations pass
        Toast.makeText(this,
                String.format("Confirmed load of ₱%d via %s.", currentAmount, paymentMethod),
                Toast.LENGTH_LONG).show();

        // Here you would typically proceed with the actual payment processing
        // (e.g., call an API, integrate with a payment gateway)
    }

}