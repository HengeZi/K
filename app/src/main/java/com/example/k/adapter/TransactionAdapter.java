package com.example.k.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.k.R;
import com.example.k.model.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends BaseAdapter {
    private Context context;
    private List<Transaction> transactionList;
    private SimpleDateFormat dateFormat;

    public TransactionAdapter(Context context, List<Transaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    }

    @Override
    public int getCount() {
        return transactionList.size();
    }

    @Override
    public Object getItem(int position) {
        return transactionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return transactionList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
            holder = new ViewHolder();
            holder.tvProductName = convertView.findViewById(R.id.tvProductName);
            holder.tvType = convertView.findViewById(R.id.tvType);
            holder.tvAmount = convertView.findViewById(R.id.tvAmount);
            holder.tvPrice = convertView.findViewById(R.id.tvPrice);
            holder.tvTotal = convertView.findViewById(R.id.tvTotal);
            holder.tvDate = convertView.findViewById(R.id.tvDate);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Transaction transaction = transactionList.get(position);
        holder.tvProductName.setText(transaction.getProductName());
        holder.tvType.setText(transaction.getType().equals("BUY") ? "买入" : "卖出");
        holder.tvAmount.setText(String.format("数量：%.2f", transaction.getAmount()));
        holder.tvPrice.setText(String.format("单价：¥%.2f", transaction.getPrice()));
        holder.tvTotal.setText(String.format("总计：¥%.2f", transaction.getAmount() * transaction.getPrice()));
        holder.tvDate.setText(dateFormat.format(new Date(transaction.getDate())));

        int typeColor = transaction.getType().equals("BUY") ?
                context.getResources().getColor(android.R.color.holo_red_dark) :
                context.getResources().getColor(android.R.color.holo_green_dark);
        holder.tvType.setTextColor(typeColor);

        return convertView;
    }

    static class ViewHolder {
        TextView tvProductName;
        TextView tvType;
        TextView tvAmount;
        TextView tvPrice;
        TextView tvTotal;
        TextView tvDate;
    }
}
