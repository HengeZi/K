package com.example.k.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.k.R;
import com.example.k.adapter.ProductAdapter;
import com.example.k.database.AppDatabase;
import com.example.k.model.Product;
import com.example.k.model.UserProduct;
import com.example.k.model.Transaction;
import com.example.k.ui.profile.ProfileActivity;
import com.example.k.ui.transactions.TransactionsActivity;
import com.example.k.ui.holdings.HoldingsActivity;
import com.example.k.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeActivity extends AppCompatActivity {
    private ListView lvProducts;
    private SwipeRefreshLayout swipeRefresh;
    private TextView tvWelcome, tvTotalValue;
    private Button btnProfile, btnTransactions, btnHoldings;
    private AppDatabase database;
    private SessionManager sessionManager;
    private ExecutorService executor;
    private ProductAdapter adapter;
    private List<Product> productList;
    private double totalValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        database = AppDatabase.getInstance(this);
        sessionManager = new SessionManager(this);
        executor = Executors.newSingleThreadExecutor();

        lvProducts = findViewById(R.id.lvProducts);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        tvWelcome = findViewById(R.id.tvWelcome);
        tvTotalValue = findViewById(R.id.tvTotalValue);
        btnProfile = findViewById(R.id.btnProfile);
        btnTransactions = findViewById(R.id.btnTransactions);
        btnHoldings = findViewById(R.id.btnHoldings);

        String username = sessionManager.getUsername();
        tvWelcome.setText("欢迎，" + username);

        productList = new ArrayList<>();
        adapter = new ProductAdapter(this, productList);
        lvProducts.setAdapter(adapter);

        loadProducts();
        calculateTotalValue();

        swipeRefresh.setOnRefreshListener(() -> {
            loadProducts();
            calculateTotalValue();
            swipeRefresh.setRefreshing(false);
        });

        lvProducts.setOnItemClickListener((parent, view, position, id) -> {
            Product product = productList.get(position);
            showTransactionDialog(product);
        });

        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        });

        btnTransactions.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, TransactionsActivity.class));
        });

        btnHoldings.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, HoldingsActivity.class);
            intent.putExtra("USER_ID", sessionManager.getUserId());
            startActivity(intent);
        });
    }

    private void loadProducts() {
        executor.execute(() -> {
            List<Product> products = database.productDao().getAllProducts();
            runOnUiThread(() -> {
                if (products != null && !products.isEmpty()) {
                    productList.clear();
                    productList.addAll(products);
                    adapter.notifyDataSetChanged();
                } else {
                    insertSampleProducts();
                }
            });
        });
    }

    private void insertSampleProducts() {
        executor.execute(() -> {
            database.productDao().insert(new Product("余额宝", "货币基金", 1.0, 2.5, "低", "灵活存取，风险低"));
            database.productDao().insert(new Product("沪深 300 指数基金", "股票基金", 1.0, 8.5, "中", "跟踪沪深 300 指数"));
            database.productDao().insert(new Product("国债", "债券", 100.0, 3.5, "低", "国家信用背书"));
            database.productDao().insert(new Product("黄金 ETF", "商品基金", 1.0, 6.0, "中", "投资黄金市场"));
            database.productDao().insert(new Product("科技成长基金", "股票基金", 1.0, 12.0, "高", "投资科技行业"));
            
            List<Product> products = database.productDao().getAllProducts();
            runOnUiThread(() -> {
                if (products != null) {
                    productList.clear();
                    productList.addAll(products);
                    adapter.notifyDataSetChanged();
                }
            });
        });
    }

    private void calculateTotalValue() {
        executor.execute(() -> {
            int userId = sessionManager.getUserId();
            List<UserProduct> userProducts = database.userProductDao().getUserProducts(userId);
            double total = 0;
            for (UserProduct up : userProducts) {
                total += up.getAmount() * up.getBuyPrice();
            }
            totalValue = total;
            runOnUiThread(() -> {
                tvTotalValue.setText(String.format("总资产：¥%.2f", totalValue));
            });
        });
    }

    private void showTransactionDialog(Product product) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_buy_sell, null);
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        TextView tvProductName = dialogView.findViewById(R.id.tvProductName);
        TextView tvProductPrice = dialogView.findViewById(R.id.tvProductPrice);
        TextView tvHoldAmount = dialogView.findViewById(R.id.tvHoldAmount);
        TextView tvDialogTitle = dialogView.findViewById(R.id.tvDialogTitle);
        RadioGroup rgTransactionType = dialogView.findViewById(R.id.rgTransactionType);
        RadioButton rbBuy = dialogView.findViewById(R.id.rbBuy);
        RadioButton rbSell = dialogView.findViewById(R.id.rbSell);
        android.widget.EditText etAmount = dialogView.findViewById(R.id.etAmount);
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        tvProductName.setText(product.getName());
        tvProductPrice.setText(String.format("当前价格：¥%.2f", product.getPrice()));

        executor.execute(() -> {
            int userId = sessionManager.getUserId();
            Double heldAmount = database.userProductDao().getTotalAmount(userId, product.getId());
            double holdAmt = (heldAmount != null) ? heldAmount : 0.0;
            runOnUiThread(() -> {
                tvHoldAmount.setText(String.format("持有数量：%.2f", holdAmt));
                if (holdAmt <= 0) {
                    rbSell.setEnabled(false);
                    rbBuy.setChecked(true);
                }
            });
        });

        rgTransactionType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbBuy) {
                btnConfirm.setText("确认买入");
                etAmount.setHint("买入数量");
            } else {
                btnConfirm.setText("确认卖出");
                etAmount.setHint("卖出数量");
            }
        });

        btnConfirm.setOnClickListener(v -> {
            String amountStr = etAmount.getText().toString().trim();
            if (amountStr.isEmpty()) {
                Toast.makeText(this, "请输入数量", Toast.LENGTH_SHORT).show();
                return;
            }
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                Toast.makeText(this, "数量必须大于 0", Toast.LENGTH_SHORT).show();
                return;
            }

            if (rbBuy.isChecked()) {
                buyProduct(product, amount);
            } else {
                sellProduct(product, amount);
            }
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void buyProduct(Product product, double amount) {
        executor.execute(() -> {
            int userId = sessionManager.getUserId();
            long buyDate = System.currentTimeMillis();
            
            UserProduct userProduct = new UserProduct(
                    userId, product.getId(), amount, product.getPrice(), buyDate, product.getName());
            database.userProductDao().insert(userProduct);

            Transaction transaction = new Transaction(
                    userId, product.getId(), "买入", amount, product.getPrice(), amount * product.getPrice(), buyDate, product.getName());
            database.transactionDao().insert(transaction);

            runOnUiThread(() -> {
                Toast.makeText(this, "买入成功", Toast.LENGTH_SHORT).show();
                calculateTotalValue();
            });
        });
    }

    private void sellProduct(Product product, double amount) {
        executor.execute(() -> {
            int userId = sessionManager.getUserId();
            
            Double currentAmount = database.userProductDao().getTotalAmount(userId, product.getId());
            double holdAmt = (currentAmount != null) ? currentAmount : 0.0;
            
            if (amount > holdAmt) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "卖出数量不能超过持有数量", Toast.LENGTH_SHORT).show();
                });
                return;
            }
            
            long sellDate = System.currentTimeMillis();
            
            Transaction transaction = new Transaction(
                    userId, product.getId(), "卖出", amount, product.getPrice(), amount * product.getPrice(), sellDate, product.getName());
            database.transactionDao().insert(transaction);
            
            if (Math.abs(amount - holdAmt) < 0.001) {
                database.userProductDao().deleteUserProductsByProduct(userId, product.getId());
            } else {
                UserProduct existingProduct = database.userProductDao().getUserProduct(userId, product.getId());
                if (existingProduct != null) {
                    existingProduct.setAmount(holdAmt - amount);
                    database.userProductDao().update(existingProduct);
                }
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "卖出成功", Toast.LENGTH_SHORT).show();
                calculateTotalValue();
            });
        });
    }
}
