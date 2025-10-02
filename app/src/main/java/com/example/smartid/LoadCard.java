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
    private Button btnOtherAmount;
    private RadioButton rbOthersPayment;
    private LinearLayout layoutOtherBanks;
    private Button btnConfirmLoad;
    private RadioGroup rgPaymentMethods;
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
        setupBottomNavigation(view);

        btnConfirmLoad.setOnClickListener(v -> confirmLoad());
    }

    private void initializeViews(View view) {
        tilCustomAmount = view.findViewById(R.id.til_custom_amount);
        etCustomAmount = view.findViewById(R.id.et_custom_amount);
        btnOtherAmount = view.findViewById(R.id.btn_amount_other);
        rbOthersPayment = view.findViewById(R.id.rb_others);
        layoutOtherBanks = view.findViewById(R.id.layout_other_banks);
        btnConfirmLoad = view.findViewById(R.id.btn_confirm_load);
        rgPaymentMethods = view.findViewById(R.id.rg_payment_methods);
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
            if (btn != null) {
                btn.setOnClickListener(v -> handleAmountSelection(v, entry.getValue()));
            }
        }

        // Initial button selection (e.g., 50)
        Button btn50 = view.findViewById(R.id.btn_amount_50);
        if (btn50 != null) {
            btn50.setSelected(true);
        }

        // Handle 'Other' button
        if (btnOtherAmount != null) {
            btnOtherAmount.setOnClickListener(v -> {
                tilCustomAmount.setVisibility(View.VISIBLE);
                selectedLoadAmount = 0; // Clear fixed amount
                btnOtherAmount.setSelected(true);
                // Visually deselect other buttons
                for (int id : fixedAmountButtons.keySet()) {
                    Button btn = view.findViewById(id);
                    if (btn != null) {
                        btn.setSelected(false);
                    }
                }
            });
        }
    }

    private void handleAmountSelection(View selectedButton, int amount) {
        // Hide custom input and update selection
        tilCustomAmount.setVisibility(View.GONE);
        etCustomAmount.setText(""); // Clear custom amount
        selectedLoadAmount = amount;

        // Deselect "Other" button
        if (btnOtherAmount != null) {
            btnOtherAmount.setSelected(false);
        }

        // Update visual state of all fixed amount buttons
        for (int id : fixedAmountButtons.keySet()) {
            View view = getView();
            if (view != null) {
                Button btn = view.findViewById(id);
                if (btn != null) {
                    btn.setSelected(btn.getId() == selectedButton.getId());
                }
            }
        }
    }

    private void setupCustomAmountInput() {
        if (etCustomAmount != null) {
            etCustomAmount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        if (s.length() > 0) {
                            int amount = Integer.parseInt(s.toString());
                            if (amount < 20) {
                                tilCustomAmount.setError("Minimum load amount is ₱20.");
                                btnConfirmLoad.setEnabled(false);
                            } else {
                                tilCustomAmount.setError(null);
                                selectedLoadAmount = amount;
                                btnConfirmLoad.setEnabled(true);
                            }
                        } else {
                            tilCustomAmount.setError(null);
                            btnConfirmLoad.setEnabled(true);
                        }
                    } catch (NumberFormatException e) {
                        tilCustomAmount.setError("Invalid amount.");
                        btnConfirmLoad.setEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void setupPaymentMethodSelection(View view) {
        // Toggle visibility of the local banks when 'Others' is selected
        if (rbOthersPayment != null) {
            rbOthersPayment.setOnClickListener(v -> {
                boolean isChecked = ((RadioButton) v).isChecked();
                if (isChecked && layoutOtherBanks != null) {
                    layoutOtherBanks.setVisibility(View.VISIBLE);
                }
            });
        }

        // Hide other banks when different payment method is selected
        if (rgPaymentMethods != null) {
            rgPaymentMethods.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId != R.id.rb_others && layoutOtherBanks != null) {
                    layoutOtherBanks.setVisibility(View.GONE);
                }
            });
        }

        // Set up click listeners for the local banks
        RadioButton rbBdo = view.findViewById(R.id.rb_bdo);
        RadioButton rbBpi = view.findViewById(R.id.rb_bpi);
        RadioButton rbMetrobank = view.findViewById(R.id.rb_metrobank);

        View.OnClickListener bankClickListener = v -> {
            if (rgPaymentMethods != null && rbOthersPayment != null) {
                rgPaymentMethods.check(rbOthersPayment.getId());
            }
        };

        if (rbBdo != null) rbBdo.setOnClickListener(bankClickListener);
        if (rbBpi != null) rbBpi.setOnClickListener(bankClickListener);
        if (rbMetrobank != null) rbMetrobank.setOnClickListener(bankClickListener);
    }

    private void setupBottomNavigation(View view) {
        Button btnCardDetails = view.findViewById(R.id.CardDetails_Button);
        Button btnHome = view.findViewById(R.id.Home_Button);
        Button btnProfile = view.findViewById(R.id.Profile_Button);

        if (btnCardDetails != null) {
            btnCardDetails.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Show Card Details", Toast.LENGTH_SHORT).show();
                // Navigate to Card Details
            });
        }

        if (btnHome != null) {
            btnHome.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Go to Home", Toast.LENGTH_SHORT).show();
                // Navigate to Home
            });
        }

        if (btnProfile != null) {
            btnProfile.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Go to Profile", Toast.LENGTH_SHORT).show();
                // Navigate to Profile
            });
        }
    }

    private void confirmLoad() {
        // Get selected payment method
        String paymentMethod = "Not Selected";
        if (rgPaymentMethods != null) {
            int selectedId = rgPaymentMethods.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton selectedRb = getView().findViewById(selectedId);
                if (selectedRb != null) {
                    paymentMethod = selectedRb.getText().toString();
                }
            }
        }

        // Basic validation and confirmation
        if (selectedLoadAmount < 20) {
            Toast.makeText(getContext(), "Please enter a valid amount (Min ₱20).", Toast.LENGTH_SHORT).show();
            return;
        }

        if (paymentMethod.equals("Not Selected")) {
            Toast.makeText(getContext(), "Please select a payment method.", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getContext(),
                String.format("Confirmed load of ₱%d via %s.", selectedLoadAmount, paymentMethod),
                Toast.LENGTH_LONG).show();
    }
}