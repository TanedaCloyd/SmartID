package com.example.smartid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class Transactions extends AppCompatActivity {

    private ImageButton btnBack;
    private SearchView searchTransactions;
    private Button btnAll, btnTrainRides, btnLoad;

    // Transaction data structure
    public static class Transaction {
        public String type;
        public String description;
        public String dateTime;
        public double amount;
        public boolean isDebit;

        public Transaction(String type, String description, String dateTime, double amount, boolean isDebit) {
            this.type = type;
            this.description = description;
            this.dateTime = dateTime;
            this.amount = amount;
            this.isDebit = isDebit;
        }
    }

    private List<Transaction> transactionList;
    private String currentFilter = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        initializeViews();
        initializeTransactions();
        setupClickListeners();
        setupSearchView();
        setupBottomNavigation();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btn_back);
        searchTransactions = findViewById(R.id.search_transactions);
        btnAll = findViewById(R.id.btn_all);
        btnTrainRides = findViewById(R.id.btn_train_rides);
        btnLoad = findViewById(R.id.btn_load);
    }

    private void initializeTransactions() {
        transactionList = new ArrayList<>();

        // Sample transaction data based on the image
        transactionList.add(new Transaction("train_ride", "Train Ride", "Today, 10:00 AM", 20.00, true));
        transactionList.add(new Transaction("load", "Loaded Amount", "Yesterday, 2:30 PM", 500.00, false));
        transactionList.add(new Transaction("train_ride", "Train Ride", "3 days ago, 9:45 AM", 20.00, true));
    }

    private void setupClickListeners() {
        // Back button - return to previous screen
        btnBack.setOnClickListener(v -> {
            finish(); // Close this activity and return to previous one
        });

        // Filter buttons
        btnAll.setOnClickListener(v -> {
            filterTransactions("all");
            updateFilterButtonStyles("all");
        });

        btnTrainRides.setOnClickListener(v -> {
            filterTransactions("train_ride");
            updateFilterButtonStyles("train_ride");
        });

        btnLoad.setOnClickListener(v -> {
            filterTransactions("load");
            updateFilterButtonStyles("load");
        });
    }

    private void setupSearchView() {
        searchTransactions.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchTransactions(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    filterTransactions(currentFilter);
                } else {
                    searchTransactions(newText);
                }
                return false;
            }
        });
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // No item selected initially since we're on Transactions page
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.Home_Button) {
                // Navigate back to HomePage
                startActivity(new Intent(Transactions.this, HomePage.class));
                finish(); // Close current activity
                return true;
            } else if (itemId == R.id.Profile_Button) {
                // Navigate to Profile page
                Toast.makeText(this, "Go to Profile Page", Toast.LENGTH_SHORT).show();
                // Example: startActivity(new Intent(Transactions.this, ProfileActivity.class));
                return true;
            } else if (itemId == R.id.CardDetails_Button) {
                // Navigate to Card Details page
                Toast.makeText(this, "Show Card Details", Toast.LENGTH_SHORT).show();
                // Example: startActivity(new Intent(Transactions.this, CardDetailsActivity.class));
                return true;
            }
            return false;
        });
    }

    private void filterTransactions(String filter) {
        currentFilter = filter;
        List<Transaction> filteredList = new ArrayList<>();

        for (Transaction transaction : transactionList) {
            if (filter.equals("all") || transaction.type.equals(filter)) {
                filteredList.add(transaction);
            }
        }

        // Here you would update your RecyclerView or ListView with the filtered data
        updateTransactionDisplay(filteredList);

        // Show feedback to user
        String message = filter.equals("all") ? "Showing all transactions" :
                filter.equals("train_ride") ? "Showing train rides only" :
                        "Showing load transactions only";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void searchTransactions(String query) {
        List<Transaction> searchResults = new ArrayList<>();
        String lowercaseQuery = query.toLowerCase();

        for (Transaction transaction : transactionList) {
            if (transaction.description.toLowerCase().contains(lowercaseQuery) ||
                    transaction.dateTime.toLowerCase().contains(lowercaseQuery)) {
                searchResults.add(transaction);
            }
        }

        updateTransactionDisplay(searchResults);

        // Show search feedback
        if (searchResults.isEmpty()) {
            Toast.makeText(this, "No transactions found for: " + query, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Found " + searchResults.size() + " transaction(s)", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateFilterButtonStyles(String activeFilter) {
        // Reset all buttons to default style
        resetButtonStyle(btnAll);
        resetButtonStyle(btnTrainRides);
        resetButtonStyle(btnLoad);

        // Highlight the active filter button
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
        button.setBackgroundTintList(getResources().getColorStateList(R.color.green));
        button.setAlpha(0.7f);
    }

    private void setActiveButtonStyle(Button button) {
        button.setBackgroundTintList(getResources().getColorStateList(R.color.green));
        button.setAlpha(1.0f);
    }

    private void updateTransactionDisplay(List<Transaction> transactions) {
        // This method would typically update a RecyclerView or ListView
        // For a complete implementation, you would need to create an adapter
        // and update the display with the filtered transactions

        // For now, we'll just log the count
        System.out.println("Displaying " + transactions.size() + " transactions");

        // Example: adapter.updateTransactions(transactions);
        // adapter.notifyDataSetChanged();
    }

    // Method to add a new transaction (useful for testing or when called from other activities)
    public void addTransaction(String type, String description, String dateTime, double amount, boolean isDebit) {
        Transaction newTransaction = new Transaction(type, description, dateTime, amount, isDebit);
        transactionList.add(0, newTransaction); // Add to the beginning of the list
        filterTransactions(currentFilter); // Refresh the display

        Toast.makeText(this, "New transaction added", Toast.LENGTH_SHORT).show();
    }

    // Method to get transaction summary
    public double getTotalBalance() {
        double balance = 0;
        for (Transaction transaction : transactionList) {
            if (transaction.isDebit) {
                balance -= transaction.amount;
            } else {
                balance += transaction.amount;
            }
        }
        return balance;
    }

    // Method to get transaction count by type
    public int getTransactionCount(String type) {
        int count = 0;
        for (Transaction transaction : transactionList) {
            if (type.equals("all") || transaction.type.equals(type)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}