package com.example.smartid;

import android.content.Intent; // ** Import Intent **
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors; // Keep if using stream filters

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Transactions extends AppCompatActivity {

    // --- Use SessionManager ---
    private SessionManager sessionManager;
    // --- Removed hardcodedRfid ---

    private ImageButton btnBack;
    private SearchView searchTransactions;
    private Button btnAll, btnTrainRides, btnLoad;

    private RecyclerView transactionRecyclerView;
    private TransactionAdapter transactionAdapter;
    private ApiService apiService;

    private List<Tap> allTransactions; // Store all transactions here
    private String currentFilter = "all";
    private String currentSearchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        // --- Initialize SessionManager ---
        sessionManager = new SessionManager(getApplicationContext());
        // --- End Init ---

        // Setup the API service
        apiService = ApiClient.getClient().create(ApiService.class);

        initializeViews();
        setupRecyclerView(); // Setup the (empty) adapter
        setupClickListeners();
        setupSearchView();

        updateFilterButtonStyles(currentFilter);

        // Fetch real data from the server
        fetchTransactions();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btn_back);
        searchTransactions = findViewById(R.id.search_transactions);
        btnAll = findViewById(R.id.btn_all);
        btnTrainRides = findViewById(R.id.btn_train_rides);
        btnLoad = findViewById(R.id.btn_load);
        transactionRecyclerView = findViewById(R.id.transaction_recycler_view);
    }

    private void setupRecyclerView() {
        allTransactions = new ArrayList<>(); // Initialize with empty list
        transactionAdapter = new TransactionAdapter(this, allTransactions);
        transactionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionRecyclerView.setAdapter(transactionAdapter);
    }

    // --- MODIFIED: Fetch data using SessionManager ---
    private void fetchTransactions() {
        // 1. Get the real RFID from the session
        String rfid = sessionManager.getUserRfid();

        // 2. Check if the user is logged in
        if (rfid == null) {
            Toast.makeText(this, "Error: Not logged in. Please log in again.", Toast.LENGTH_LONG).show();
            // Optional: Redirect to login
            Intent intent = new Intent(Transactions.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish(); // Close this activity
            return;
        }

        // 3. Use the real rfid in the API call
        apiService.getStudentProfile(rfid).enqueue(new Callback<StudentProfile>() {
            @Override
            public void onResponse(Call<StudentProfile> call, Response<StudentProfile> response) {
                if (response.isSuccessful() && response.body() != null && response.body().taps != null) {
                    // Save the fetched transactions
                    allTransactions = response.body().taps;
                    // Filter and display them
                    filterAndDisplayTransactions();
                } else {
                    // Handle API errors (e.g., user not found, server issue)
                    String errorMsg = "Failed to fetch transactions.";
                    if (response.errorBody() != null) {
                        try { errorMsg += " " + response.errorBody().string(); }
                        catch (Exception e) { Log.e("Transactions", "Error parsing error body", e); }
                    } else { errorMsg += " Code: " + response.code(); }
                    Toast.makeText(Transactions.this, errorMsg, Toast.LENGTH_LONG).show();
                    // Display empty list on error
                    allTransactions = new ArrayList<>();
                    filterAndDisplayTransactions();
                }
            }

            @Override
            public void onFailure(Call<StudentProfile> call, Throwable t) {
                // Handle network failures
                Log.e("Transactions", "API call failed: ", t);
                Toast.makeText(Transactions.this, "Network Error: Could not fetch transactions. " + t.getMessage(), Toast.LENGTH_LONG).show();
                // Display empty list on error
                allTransactions = new ArrayList<>();
                filterAndDisplayTransactions();
            }
        });
    }
    // --- END MODIFICATION ---

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        // Filter buttons
        btnAll.setOnClickListener(v -> {
            currentFilter = "all";
            updateFilterButtonStyles(currentFilter);
            filterAndDisplayTransactions();
        });

        btnTrainRides.setOnClickListener(v -> {
            currentFilter = "train_ride"; // Filters entry/exit/admin_correction
            updateFilterButtonStyles(currentFilter);
            filterAndDisplayTransactions();
        });

        btnLoad.setOnClickListener(v -> {
            currentFilter = "load"; // Filters top_up
            updateFilterButtonStyles(currentFilter);
            filterAndDisplayTransactions();
        });
    }

    private void setupSearchView() {
        searchTransactions.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentSearchQuery = query.trim();
                filterAndDisplayTransactions();
                searchTransactions.clearFocus(); // Hide keyboard
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentSearchQuery = newText.trim();
                filterAndDisplayTransactions(); // Filter as user types
                return true;
            }
        });

        // Optional: Handle clearing the search
        searchTransactions.setOnCloseListener(() -> {
            currentSearchQuery = "";
            filterAndDisplayTransactions();
            return false; // Allow default behavior (clearing text)
        });
    }

    // Filter logic using Java Streams (requires API level 24+)
    private void filterAndDisplayTransactions() {
        if (allTransactions == null) {
            allTransactions = new ArrayList<>(); // Ensure list is not null
        }

        List<Tap> filteredList = new ArrayList<>(allTransactions); // Start with all

        // 1. Filter by button type
        if ("train_ride".equals(currentFilter)) {
            filteredList = allTransactions.stream()
                    .filter(t -> t.tapType != null && (t.tapType.equals("entry") || t.tapType.equals("exit") || t.tapType.equals("admin_correction")))
                    .collect(Collectors.toList());
        } else if ("load".equals(currentFilter)) {
            filteredList = allTransactions.stream()
                    .filter(t -> t.tapType != null && t.tapType.equals("top_up"))
                    .collect(Collectors.toList());
        }
        // "all" case uses the initial full list

        // 2. Filter by search query (case-insensitive)
        if (!currentSearchQuery.isEmpty()) {
            String lowerCaseQuery = currentSearchQuery.toLowerCase();
            filteredList = filteredList.stream()
                    .filter(t -> t.getDescription() != null && t.getDescription().toLowerCase().contains(lowerCaseQuery))
                    .collect(Collectors.toList());
        }

        // 3. Update the RecyclerView adapter
        updateTransactionDisplay(filteredList);

        // Show message if search yields no results
        if (filteredList.isEmpty() && !currentSearchQuery.isEmpty()) {
            Toast.makeText(this, "No transactions found matching '" + currentSearchQuery + "'", Toast.LENGTH_SHORT).show();
        } else if (allTransactions.isEmpty() && currentSearchQuery.isEmpty()) {
            // Optional: Show a message if there are no transactions at all
            // Toast.makeText(this, "No transactions found.", Toast.LENGTH_SHORT).show();
        }
    }

    // --- UI Helper methods ---
    private void updateFilterButtonStyles(String activeFilter) {
        resetButtonStyle(btnAll);
        resetButtonStyle(btnTrainRides);
        resetButtonStyle(btnLoad);
        switch (activeFilter) {
            case "all": setActiveButtonStyle(btnAll); break;
            case "train_ride": setActiveButtonStyle(btnTrainRides); break;
            case "load": setActiveButtonStyle(btnLoad); break;
        }
    }
    private void resetButtonStyle(Button button) { if (button != null) button.setAlpha(0.7f); }
    private void setActiveButtonStyle(Button button) { if (button != null) button.setAlpha(1.0f); }
    // --- End UI Helpers ---

    private void updateTransactionDisplay(List<Tap> transactions) {
        if (transactionAdapter != null) {
            transactionAdapter.updateTransactions(transactions); // Tell adapter data changed
        } else {
            Log.e("Transactions", "Adapter not initialized when trying to update display");
        }
        Log.d("Transactions", "Displaying " + transactions.size() + " filtered transactions");
    }

    @Override
    public void onBackPressed() {
        // If search view is open, close it first
        if (!searchTransactions.isIconified()) {
            searchTransactions.setIconified(true); // Clears text and collapses view
        } else {
            super.onBackPressed(); // Otherwise, perform default back action
        }
    }
}