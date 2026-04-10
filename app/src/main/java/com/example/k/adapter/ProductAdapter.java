package com.example.k.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.k.R;
import com.example.k.model.Product;

import java.util.List;

public class ProductAdapter extends BaseAdapter {
    private Context context;
    private List<Product> productList;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return productList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
            holder = new ViewHolder();
            holder.tvName = convertView.findViewById(R.id.tvName);
            holder.tvType = convertView.findViewById(R.id.tvType);
            holder.tvPrice = convertView.findViewById(R.id.tvPrice);
            holder.tvReturn = convertView.findViewById(R.id.tvReturn);
            holder.tvRisk = convertView.findViewById(R.id.tvRisk);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product product = productList.get(position);
        holder.tvName.setText(product.getName());
        holder.tvType.setText(product.getType());
        holder.tvPrice.setText(String.format("¥%.2f", product.getPrice()));
        holder.tvReturn.setText(String.format("预期收益：%.2f%%", product.getExpectedReturn()));
        
        int riskColor;
        switch (product.getRiskLevel()) {
            case "低":
                riskColor = context.getResources().getColor(android.R.color.holo_green_dark);
                break;
            case "中":
                riskColor = context.getResources().getColor(android.R.color.holo_orange_dark);
                break;
            case "高":
                riskColor = context.getResources().getColor(android.R.color.holo_red_dark);
                break;
            default:
                riskColor = context.getResources().getColor(android.R.color.black);
        }
        holder.tvRisk.setText(product.getRiskLevel() + "风险");
        holder.tvRisk.setTextColor(riskColor);

        return convertView;
    }

    static class ViewHolder {
        TextView tvName;
        TextView tvType;
        TextView tvPrice;
        TextView tvReturn;
        TextView tvRisk;
    }
}
