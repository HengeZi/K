package com.example.k.ui.transactions;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.k.R;
import com.example.k.adapter.TransactionAdapter;
import com.example.k.database.AppDatabase;
import com.example.k.model.Transaction;
import com.example.k.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionsActivity extends AppCompatActivity {
    private ListView lvTransactions;
    private SwipeRefreshLayout swipeRefresh;
    private AppDatabase database;
    private SessionManager sessionManager;
    private ExecutorService executor;
    private TransactionAdapter adapter;
    private List<Transaction> transactionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        database = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);
        executor = Executors.newSingleThreadExecutor();

        lvTransactions = findViewById(R.id.lvTransactions);
        swipeRefresh = findViewById(R.id.swipeRefresh);

        transactionList = new ArrayList<>();
        adapter = new TransactionAdapter(this, transactionList);
        lvTransactions.setAdapter(adapter);

        loadTransactions();

        swipeRefresh.setOnRefreshListener(() -> {
            loadTransactions();
            swipeRefresh.setRefreshing(false);
        });
    }

    private void loadTransactions() {
        executor.execute(() -> {
            int userId = sessionManager.getUserId();
            List<Transaction> transactions = database.transactionDao().getUserTransactions(userId);
            runOnUiThread(() -> {
                if (transactions != null && !transactions.isEmpty()) {
                    transactionList.clear();
                    transactionList.addAll(transactions);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "暂无交易记录", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
