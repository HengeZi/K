package com.example.k.ui.holdings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.k.R;
import com.example.k.adapter.HoldingsAdapter;
import com.example.k.database.AppDatabase;
import com.example.k.model.UserProduct;
import com.example.k.model.Product;
import com.example.k.ui.home.HomeActivity;
import com.google.android.material.button.MaterialButton;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HoldingsActivity extends AppCompatActivity implements HoldingsAdapter.OnSellClickListener {

    private RecyclerView recyclerHoldings;
    private TextView tvTotalValue, tvTotalProfit;
    private LinearLayout layoutEmpty;
    private MaterialButton btnGoHome;
    private SwipeRefreshLayout swipeRefresh;

    private AppDatabase database;
    private ExecutorService executor;
    private HoldingsAdapter adapter;
    private List<UserProduct> holdingsList = new ArrayList<>();
    private List<Product> productList = new ArrayList<>();
    
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holdings);

        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        if (currentUserId == -1) {
            Toast.makeText(this, "用户信息错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupDatabase();
        loadData();
    }

    private void initViews() {
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViewById(R.id.toolbar).setOnClickListener(v -> finish());

        recyclerHoldings = findViewById(R.id.recyclerHoldings);
        tvTotalValue = findViewById(R.id.tvTotalValue);
        tvTotalProfit = findViewById(R.id.tvTotalProfit);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        btnGoHome = findViewById(R.id.btnGoHome);
        swipeRefresh = findViewById(R.id.swipeRefresh);

        recyclerHoldings.setLayoutManager(new LinearLayoutManager(this));
        
        btnGoHome.setOnClickListener(v -> {
            Intent intent = new Intent(HoldingsActivity.this, HomeActivity.class);
            intent.putExtra("USER_ID", currentUserId);
            startActivity(intent);
            finish();
        });

        swipeRefresh.setOnRefreshListener(() -> loadData());
        swipeRefresh.setColorSchemeResources(R.color.primary);
    }

    private void setupDatabase() {
        database = AppDatabase.getInstance(this);
        executor = Executors.newSingleThreadExecutor();
    }

    private void loadData() {
        executor.execute(() -> {
            // 加载持有产品
            holdingsList = database.userProductDao().getUserProducts(currentUserId);
            
            // 加载所有产品信息用于计算
            productList = database.productDao().getAllProducts();
            
            runOnUiThread(() -> {
                updateUI();
                swipeRefresh.setRefreshing(false);
            });
        });
    }

    private void updateUI() {
        if (holdingsList.isEmpty()) {
            recyclerHoldings.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
            tvTotalValue.setText("¥0.00");
            tvTotalProfit.setText("预估盈亏：¥0.00");
            return;
        }

        recyclerHoldings.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);

        // 创建产品映射便于查找
        java.util.Map<Integer, Product> productMap = new java.util.HashMap<>();
        for (Product p : productList) {
            productMap.put(p.getId(), p);
        }

        // 计算总资产和盈亏
        BigDecimal totalValue = BigDecimal.ZERO;
        BigDecimal totalCost = BigDecimal.ZERO;

        List<HoldingsAdapter.HoldingItem> displayList = new ArrayList<>();
        for (UserProduct up : holdingsList) {
            Product product = productMap.get(up.getProductId());
            if (product == null) continue;

            BigDecimal shares = new BigDecimal(up.getShares());
            BigDecimal netValue = new BigDecimal(product.getNetValue());
            BigDecimal buyPrice = new BigDecimal(up.getBuyPrice());

            BigDecimal marketValue = shares.multiply(netValue);
            BigDecimal cost = shares.multiply(buyPrice);
            BigDecimal profit = marketValue.subtract(cost);

            totalValue = totalValue.add(marketValue);
            totalCost = totalCost.add(cost);

            displayList.add(new HoldingsAdapter.HoldingItem(
                up,
                product,
                marketValue,
                profit
            ));
        }

        tvTotalValue.setText(String.format("¥%.2f", totalValue));
        BigDecimal totalProfit = totalValue.subtract(totalCost);
        String profitText = String.format("预估盈亏：¥%.2f", totalProfit);
        tvTotalProfit.setText(profitText);
        
        if (totalProfit.compareTo(BigDecimal.ZERO) >= 0) {
            tvTotalProfit.setTextColor(getColor(R.color.success));
        } else {
            tvTotalProfit.setTextColor(getColor(R.color.danger));
        }

        adapter = new HoldingsAdapter(this, displayList, this);
        recyclerHoldings.setAdapter(adapter);
    }

    @Override
    public void onSellClick(UserProduct userProduct, Product product) {
        showSellDialog(userProduct, product);
    }

    private void showSellDialog(UserProduct userProduct, Product product) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_buy_sell, null);
        
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("卖出 " + product.getName());
        builder.setView(dialogView);

        com.google.android.material.textfield.TextInputEditText etAmount = 
            dialogView.findViewById(R.id.etAmount);
        TextView tvHint = dialogView.findViewById(R.id.tvHint);
        
        tvHint.setText("最大可卖出：" + userProduct.getShares() + " 份");

        builder.setPositiveButton("确认卖出", (dialog, which) -> {
            String amountStr = etAmount.getText().toString().trim();
            if (amountStr.isEmpty()) {
                Toast.makeText(this, "请输入卖出份额", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double shares = Double.parseDouble(amountStr);
                if (shares <= 0) {
                    Toast.makeText(this, "卖出份额必须大于 0", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (shares > userProduct.getShares()) {
                    Toast.makeText(this, "卖出份额不能超过持有量", Toast.LENGTH_SHORT).show();
                    return;
                }

                performSell(userProduct, product, shares);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "请输入有效的数字", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void performSell(UserProduct userProduct, Product product, double shares) {
        executor.execute(() -> {
            try {
                // 更新持有量
                double newShares = userProduct.getShares() - shares;
                if (newShares <= 0.001) {
                    // 如果全部卖出，删除记录
                    database.userProductDao().delete(userProduct);
                } else {
                    userProduct.setShares(newShares);
                    database.userProductDao().update(userProduct);
                }

                // 创建交易记录
                com.example.k.model.Transaction transaction = new com.example.k.model.Transaction();
                transaction.setUserId(currentUserId);
                transaction.setProductId(product.getId());
                transaction.setType("卖出");
                transaction.setShares(shares);
                transaction.setPrice(Double.parseDouble(product.getNetValue()));
                transaction.setAmount(shares * Double.parseDouble(product.getNetValue()));
                transaction.setTimestamp(System.currentTimeMillis());

                database.transactionDao().insert(transaction);

                // 更新用户总资产
                double sellAmount = shares * Double.parseDouble(product.getNetValue());
                com.example.k.model.User user = database.userDao().getById(currentUserId);
                if (user != null) {
                    user.setTotalAssets(user.getTotalAssets() + sellAmount);
                    database.userDao().update(user);
                }

                runOnUiThread(() -> {
                    Toast.makeText(HoldingsActivity.this, "卖出成功", Toast.LENGTH_SHORT).show();
                    loadData();
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> 
                    Toast.makeText(HoldingsActivity.this, "卖出失败：" + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}
