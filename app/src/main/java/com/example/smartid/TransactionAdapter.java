package com.example.smartid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Tap> transactionList;
    private Context context;
    private SimpleDateFormat dateFormat;

    public TransactionAdapter(Context context, List<Tap> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
        // This formats the date from the server
        this.dateFormat = new SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault());
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflates the list_item_transaction.xml layout for each row
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        // Gets the transaction for the current row
        Tap tap = transactionList.get(position);

        // --- Bind the data to the views ---
        holder.tvDescription.setText(tap.getDescription());
        holder.tvDate.setText(dateFormat.format(tap.tapTime));
        holder.tvAmount.setText(tap.getAmountString());

        // --- Set the icon and amount color ---
        if (tap.isLoad()) {
            holder.ivIcon.setImageResource(R.drawable.load); // Use your 'load.png'
            holder.tvAmount.setTextColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
        } else {
            holder.ivIcon.setImageResource(R.drawable.train); // Use your 'train.png'
            holder.tvAmount.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    // Call this from your Activity to update the list
    public void updateTransactions(List<Tap> newTaps) {
        this.transactionList = newTaps;
        notifyDataSetChanged(); // Refreshes the list
    }

    /**
     * This "ViewHolder" holds the views for a single row
     */
    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvDescription, tvDate, tvAmount;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_transaction_icon);
            tvDescription = itemView.findViewById(R.id.tv_transaction_description);
            tvDate = itemView.findViewById(R.id.tv_transaction_date);
            tvAmount = itemView.findViewById(R.id.tv_transaction_amount);
        }
    }
}