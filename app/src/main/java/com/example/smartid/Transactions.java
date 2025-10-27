package com.example.smartid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.widget.SearchView; // Correct import
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat; // For getting colors correctly
import android.content.res.ColorStateList; // For setting tint list

// Recommendation: Import RecyclerView related classes when ready
// import androidx.recyclerview.widget.LinearLayoutManager;
// import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors; // For cleaner filtering/searching if using Java 8+

public class Transactions extends AppCompatActivity {

    private ImageButton btnBack;
    private SearchView searchTransactions;
    private Button btnAll, btnTrainRides, btnLoad;

    // Recommendation: Add RecyclerView and its Adapter
    // private RecyclerView transactionRecyclerView;
    // private TransactionAdapter transactionAdapter;

    // Transaction data structure (remains the same)
    public static class Transaction {
        public String type; // e.g., "train_ride", "load"
        public String description;
        public String dateTime;
        public double amount;
        public boolean isDebit; // true if amount is deducted, false if added

        public Transaction(String type, String description, String dateTime, double amount, boolean isDebit) {
            this.type = type;
            this.description = description;
            this.dateTime = dateTime;
            this.amount = amount;
            this.isDebit = isDebit;
        }
    }

    private List<Transaction> allTransactions; // Store all transactions here
    private String currentFilter = "all";
    private String currentSearchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        initializeViews();
        initializeTransactions(); // Load initial data
        // Recommendation: Setup RecyclerView here
        // setupRecyclerView();
        setupClickListeners();
        setupSearchView();
        // --- setupBottomNavigation() call removed ---

        // Apply initial filter and button style
        updateFilterButtonStyles(currentFilter);
        filterAndDisplayTransactions(); // Display initial list
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btn_back);
        searchTransactions = findViewById(R.id.search_transactions);
        btnAll = findViewById(R.id.btn_all);
        btnTrainRides = findViewById(R.id.btn_train_rides);
        btnLoad = findViewById(R.id.btn_load);
        // Recommendation: Initialize RecyclerView
        // transactionRecyclerView = findViewById(R.id.transaction_recycler_view);
    }

    // Recommendation: Add method to setup RecyclerView
    /*
    private void setupRecyclerView() {
        transactionAdapter = new TransactionAdapter(new ArrayList<>()); // Start with empty list
        transactionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionRecyclerView.setAdapter(transactionAdapter);
    }
    */


    private void initializeTransactions() {
        // In a real app, load this from a database or API
        allTransactions = new ArrayList<>();

        // Sample transaction data
        allTransactions.add(new Transaction("train_ride", "LRT-1 Ride (Monumento to Balintawak)", "Today, 10:00 AM", 20.00, true));
        allTransactions.add(new Transaction("load", "Loaded Amount via GCash", "Yesterday, 2:30 PM", 500.00, false));
        allTransactions.add(new Transaction("train_ride", "LRT-1 Ride (Balintawak to Monumento)", "3 days ago, 9:45 AM", 20.00, true));
        allTransactions.add(new Transaction("load", "Loaded Amount via Maya", "4 days ago, 5:00 PM", 100.00, false));
        // Add more sample data if needed
    }

    private void setupClickListeners() {
        // Back button - return to previous screen
        btnBack.setOnClickListener(v -> finish()); // finish() is correct here

        // Filter buttons
        btnAll.setOnClickListener(v -> {
            currentFilter = "all";
            updateFilterButtonStyles(currentFilter);
            filterAndDisplayTransactions();
        });

        btnTrainRides.setOnClickListener(v -> {
            currentFilter = "train_ride";
            updateFilterButtonStyles(currentFilter);
            filterAndDisplayTransactions();
        });

        btnLoad.setOnClickListener(v -> {
            currentFilter = "load";
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
                return true; // Indicate query handled
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentSearchQuery = newText.trim();
                filterAndDisplayTransactions(); // Filter results as user types
                return true; // Indicate change handled
            }
        });

        // Optional: Handle clearing the search query
        searchTransactions.setOnCloseListener(() -> {
            currentSearchQuery = "";
            filterAndDisplayTransactions();
            return false; // Allow default behavior (clearing text)
        });
    }

    // Combined filtering and searching logic
    private void filterAndDisplayTransactions() {
        List<Transaction> filteredList;

        if ("all".equals(currentFilter)) {
            filteredList = new ArrayList<>(allTransactions);
        } else {
            // Using Java 8 streams
            filteredList = allTransactions.stream()
                    .filter(t -> currentFilter.equals(t.type))
                    .collect(Collectors.toList());
        }

        if (!currentSearchQuery.isEmpty()) {
            String lowerCaseQuery = currentSearchQuery.toLowerCase();
            // Using Java 8 streams
            filteredList = filteredList.stream()
                    .filter(t -> t.description.toLowerCase().contains(lowerCaseQuery) ||
                            t.dateTime.toLowerCase().contains(lowerCaseQuery))
                    .collect(Collectors.toList());
        }

        updateTransactionDisplay(filteredList);

        if (filteredList.isEmpty() && !currentSearchQuery.isEmpty()) {
            Toast.makeText(this, "No transactions found matching '" + currentSearchQuery + "'", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateFilterButtonStyles(String activeFilter) {
        resetButtonStyle(btnAll);
        resetButtonStyle(btnTrainRides);
        resetButtonStyle(btnLoad);

        switch (activeFilter) {
            case "all":
                setActiveButtonStyle(btnAll);
                break;
            case "train_ride":
                setActiveButtonStyle(btnTrainRides);
                break;
            case "load":
                setActiveButtonStyle(btnLoad);
                break;
        }
    }

    private void resetButtonStyle(Button button) {
        if (button == null) return;
        button.setAlpha(0.7f);
    }

    private void setActiveButtonStyle(Button button) {
        if (button == null) return;
        button.setAlpha(1.0f);
    }

    private void updateTransactionDisplay(List<Transaction> transactions) {
        // TODO: Replace placeholder with RecyclerView update logic
        /*
        if (transactionAdapter != null) {
            transactionAdapter.updateTransactions(transactions);
        } else {
            System.out.println("Adapter not initialized");
        }
        */
        System.out.println("Displaying " + transactions.size() + " transactions");
    }

    public void addTransaction(String type, String description, String dateTime, double amount, boolean isDebit) {
        Transaction newTransaction = new Transaction(type, description, dateTime, amount, isDebit);
        allTransactions.add(0, newTransaction);
        filterAndDisplayTransactions();
        Toast.makeText(this, "New transaction added", Toast.LENGTH_SHORT).show();
    }

    public double getTotalBalance() {
        double balance = 0;
        for (Transaction transaction : allTransactions) {
            balance += (transaction.isDebit ? -transaction.amount : transaction.amount);
        }
        return balance;
    }

    public int getTransactionCount(String type) {
        int count = 0;
        for (Transaction transaction : allTransactions) {
            if ("all".equals(type) || transaction.type.equals(type)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void onBackPressed() {
        if (!searchTransactions.isIconified()) {
            searchTransactions.setIconified(true);
            searchTransactions.setQuery("", false);
        } else {
            super.onBackPressed();
        }
    }
}