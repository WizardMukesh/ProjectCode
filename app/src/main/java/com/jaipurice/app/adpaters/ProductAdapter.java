package com.jaipurice.app.adpaters;

/**
 * Created by Rahul1 on 04-01-2016.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaipurice.app.R;
import com.jaipurice.app.application.MyApplication;
import com.jaipurice.app.model.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductAdapter extends ArrayAdapter<Product> {
    private int resourceId;
    private Context context;
    private ArrayList<Product> productArrayList;
    ViewHolder viewHolder;

    public ProductAdapter(Context context, int resourceId, ArrayList<Product> productArrayList) {
        super(context, resourceId);
        // TODO Auto-generated constructor stub
        this.productArrayList = productArrayList;
        this.context = context;
        this.resourceId = resourceId;
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return productArrayList.size();
    }
    private class ViewHolder {
        TextView textProductPrice, textProductName, textProductQty, textRemainingPkt;
        ImageView imageProduct;
        LinearLayout layoutIncrease, layoutDecrease;
        RelativeLayout layoutAddToCart;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // TODO Auto-generated method stub
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resourceId, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.textProductPrice = (TextView) convertView.findViewById(R.id.product_price_pay);
            viewHolder.textProductName = (TextView) convertView.findViewById(R.id.product_name);
            viewHolder.textProductQty = (TextView) convertView.findViewById(R.id.text_quantity);
            viewHolder.textRemainingPkt = (TextView) convertView.findViewById(R.id.textRemainingPkt);
            viewHolder.imageProduct = (ImageView) convertView.findViewById(R.id.product_thumb);
            viewHolder.layoutIncrease = (LinearLayout) convertView.findViewById(R.id.btn_increase);
            viewHolder.layoutDecrease = (LinearLayout) convertView.findViewById(R.id.btn_decrease);
            viewHolder.layoutAddToCart = (RelativeLayout) convertView.findViewById(R.id.layout_add_to_cart);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textProductPrice.setText(productArrayList.get(position).getItemPrice());
        viewHolder.textProductName.setText(productArrayList.get(position).getItemName());
        viewHolder.textProductQty.setText(productArrayList.get(position).getUserSelectedQty()+"");
        viewHolder.textRemainingPkt.setText("Available Pkts - "+productArrayList.get(position).getEmpTotalItemsQty());

        Picasso.with(context).load(productArrayList.get(position).getItemImageURL()).error(android.R.drawable.stat_notify_error).into(viewHolder.imageProduct);

        viewHolder.layoutIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int empTotalItemQty = productArrayList.get(position).getEmpTotalItemsQty();
                int newQty = productArrayList.get(position).getUserSelectedQty() + 1;
                if(newQty <= empTotalItemQty) {
                    productArrayList.get(position).setUserSelectedQty(newQty);
                    notifyDataSetChanged();
                }
                else
                    MyApplication.alertDialog((Activity) context, "You have only "+empTotalItemQty + " items left", "Employee Items");
            }
        });

        viewHolder.layoutDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newQty = productArrayList.get(position).getUserSelectedQty() - 1;
                if(newQty >= 0 ) {
                    productArrayList.get(position).setUserSelectedQty(newQty);
                    notifyDataSetChanged();
                }
            }
        });

        return convertView;
    }

}