package com.example.k.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.k.R;
import com.example.k.model.UserProduct;
import com.example.k.model.Product;
import com.google.android.material.button.MaterialButton;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

public class HoldingsAdapter extends RecyclerView.Adapter<HoldingsAdapter.ViewHolder> {

    public interface OnSellClickListener {
        void onSellClick(UserProduct userProduct, Product product);
    }

    private Context context;
    private List<HoldingItem> itemList;
    private OnSellClickListener listener;

    public static class HoldingItem {
        public UserProduct userProduct;
        public Product product;
        public BigDecimal marketValue;
        public BigDecimal profit;

        public HoldingItem(UserProduct userProduct, Product product, BigDecimal marketValue, BigDecimal profit) {
            this.userProduct = userProduct;
            this.product = product;
            this.marketValue = marketValue;
            this.profit = profit;
        }
    }

    public HoldingsAdapter(Context context, List<HoldingItem> itemList, OnSellClickListener listener) {
        this.context = context;
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_holding, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HoldingItem item = itemList.get(position);
        
        holder.tvProductName.setText(item.product.getName());
        holder.tvHoldings.setText(String.format("持有：%.2f 份", item.userProduct.getShares()));
        holder.tvNetValue.setText(String.format("¥%s", item.product.getNetValue()));
        holder.tvMarketValue.setText(String.format("¥%.2f", item.marketValue));
        
        String profitText = String.format("%s¥%.2f", item.profit.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "", item.profit);
        holder.tvProfit.setText(profitText);
        if (item.profit.compareTo(BigDecimal.ZERO) >= 0) {
            holder.tvProfit.setTextColor(context.getColor(R.color.success));
        } else {
            holder.tvProfit.setTextColor(context.getColor(R.color.danger));
        }

        // 设置风险等级
        setRiskLevel(holder.tvRiskLevel, item.product.getRiskLevel());

        holder.btnSell.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSellClick(item.userProduct, item.product);
            }
        });
    }

    private void setRiskLevel(TextView tv, String riskLevel) {
        tv.setText(riskLevel);
        int colorRes;
        switch (riskLevel) {
            case "低风险":
                colorRes = R.color.risk_low;
                break;
            case "中风险":
                colorRes = R.color.risk_medium;
                break;
            case "高风险":
                colorRes = R.color.risk_high;
                break;
            default:
                colorRes = R.color.risk_low;
        }
        tv.setBackgroundColor(context.getColor(colorRes));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvHoldings, tvNetValue, tvMarketValue, tvProfit, tvRiskLevel;
        MaterialButton btnSell;

        ViewHolder(View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvHoldings = itemView.findViewById(R.id.tvHoldings);
            tvNetValue = itemView.findViewById(R.id.tvNetValue);
            tvMarketValue = itemView.findViewById(R.id.tvMarketValue);
            tvProfit = itemView.findViewById(R.id.tvProfit);
            tvRiskLevel = itemView.findViewById(R.id.tvRiskLevel);
            btnSell = itemView.findViewById(R.id.btnSell);
        }
    }
}
