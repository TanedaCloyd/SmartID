package com.example.smartid;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

public class LoadCard extends Fragment {

    private TextInputLayout tilCustomAmount;
    private TextInputEditText etCustomAmount;
    private RadioButton rbOtherAmount;
    private RadioButton rbOthersPayment;
    private LinearLayout layoutOtherBanks;
    private Button btnConfirmLoad;
    private Map<Integer, Integer> fixedAmountButtons = new HashMap<>();
    private int selectedLoadAmount = 50; // Default selection

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_load, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupFixedAmountButtons(view);
        setupCustomAmountInput();
        setupPaymentMethodSelection(view);

        btnConfirmLoad.setOnClickListener(v -> confirmLoad());
    }

    private void initializeViews(View view) {
        tilCustomAmount = view.findViewById(R.id.til_custom_amount);
        etCustomAmount = view.findViewById(R.id.et_custom_amount);
        rbOtherAmount = view.findViewById(R.id.btn_amount_other);
        rbOthersPayment = view.findViewById(R.id.rb_others);
        layoutOtherBanks = view.findViewById(R.id.layout_other_banks);
        btnConfirmLoad = view.findViewById(R.id.btn_confirm_load);
    }

    private void setupFixedAmountButtons(View view) {
        // Map button IDs to their amounts
        fixedAmountButtons.put(R.id.btn_amount_50, 50);
        fixedAmountButtons.put(R.id.btn_amount_100, 100);
        fixedAmountButtons.put(R.id.btn_amount_200, 200);
        fixedAmountButtons.put(R.id.btn_amount_500, 500);
        fixedAmountButtons.put(R.id.btn_amount_1000, 1000);

        // Set initial state and click listeners
        for (Map.Entry<Integer, Integer> entry : fixedAmountButtons.entrySet()) {
            Button btn = view.findViewById(entry.getKey());
            btn.setOnClickListener(v -> handleAmountSelection(v, entry.getValue()));
        }

        // Initial button selection (e.g., 50)
        view.findViewById(R.id.btn_amount_50).setSelected(true);

        // Handle 'Other' button
        rbOtherAmount.setOnClickListener(v -> {
            tilCustomAmount.setVisibility(View.VISIBLE);
            selectedLoadAmount = 0; // Clear fixed amount
            // Visually deselect other buttons
            for (int id : fixedAmountButtons.keySet()) {
                view.findViewById(id).setSelected(false);
            }
        });
    }

    private void handleAmountSelection(View selectedButton, int amount) {
        // Hide custom input and update selection
        tilCustomAmount.setVisibility(View.GONE);
        selectedLoadAmount = amount;

        // Update visual state of all fixed amount buttons
        for (int id : fixedAmountButtons.keySet()) {
            View view = getView().findViewById(id);
            if (view != null) {
                view.setSelected(view.getId() == selectedButton.getId());
            }
        }
    }

    private void setupCustomAmountInput() {
        etCustomAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    int amount = Integer.parseInt(s.toString());
                    if (amount < 20) {
                        tilCustomAmount.setError("Minimum load amount is ₱20.");
                        btnConfirmLoad.setEnabled(false);
                    } else {
                        tilCustomAmount.setError(null);
                        selectedLoadAmount = amount;
                        btnConfirmLoad.setEnabled(true);
                    }
                } catch (NumberFormatException e) {
                    if (s.length() > 0) {
                        tilCustomAmount.setError("Invalid amount.");
                        btnConfirmLoad.setEnabled(false);
                    } else {
                        tilCustomAmount.setError(null);
                        btnConfirmLoad.setEnabled(false); // Disable if empty
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupPaymentMethodSelection(View view) {
        // Toggle visibility of the local banks when 'Others' is selected
        rbOthersPayment.setOnClickListener(v -> {
            boolean isChecked = ((RadioButton) v).isChecked();
            if (isChecked) {
                layoutOtherBanks.setVisibility(View.VISIBLE);
            } else {
                layoutOtherBanks.setVisibility(View.GONE);
            }
        });

        // Set up click listeners for the local banks to ensure the 'Others' radio button stays selected
        RadioGroup rgPaymentMethods = view.findViewById(R.id.rg_payment_methods);
        RadioButton rbBdo = view.findViewById(R.id.rb_bdo);
        RadioButton rbBpi = view.findViewById(R.id.rb_bpi);
        RadioButton rbMetrobank = view.findViewById(R.id.rb_metrobank);

        View.OnClickListener bankClickListener = v -> {
            rgPaymentMethods.check(rbOthersPayment.getId());
            // Optionally store the selected bank name
        };

        rbBdo.setOnClickListener(bankClickListener);
        rbBpi.setOnClickListener(bankClickListener);
        rbMetrobank.setOnClickListener(bankClickListener);
    }

    private void confirmLoad() {
        String paymentMethod = "Not Selected"; // Get selected radio button text

        // Basic validation and confirmation
        if (selectedLoadAmount < 20 && tilCustomAmount.getVisibility() == View.VISIBLE) {
            Toast.makeText(getContext(), "Please enter a valid amount (Min ₱20).", Toast.LENGTH_SHORT).show();
            return;
        } else if (selectedLoadAmount == 0 && tilCustomAmount.getVisibility() == View.GONE) {
            Toast.makeText(getContext(), "Please select a load amount.", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getContext(),
                String.format("Confirmed load of ₱%d via %s.", selectedLoadAmount, paymentMethod),
                Toast.LENGTH_LONG).show();
    }
}
