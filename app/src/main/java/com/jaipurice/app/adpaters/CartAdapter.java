package com.jaipurice.app.adpaters;

/**
 * Created by Rahul1 on 04-01-2016.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import java.util.ArrayList;

public class CartAdapter extends ArrayAdapter<Product> {
    private int resourceId;
    private Context context;
    private ArrayList<Product> productArrayList;
    private ViewHolder viewHolder;
    private TextView textCartTotalAmount, textTax1, textTax2, textCartTotalDiscount, textCartNetAmount;

    public CartAdapter(Context context, int resourceId, ArrayList<Product> productArrayList) {
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
        TextView textProductPrice, textProductName, textProductQty, textPayablePrice;
        ImageView imageProduct, imageDelete;
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
            viewHolder.textProductPrice = (TextView) convertView.findViewById(R.id.cart_product_price);
            viewHolder.textProductName = (TextView) convertView.findViewById(R.id.cart_product_name);
            viewHolder.textProductQty = (TextView) convertView.findViewById(R.id.cart_product_quantity);
            viewHolder.textPayablePrice = (TextView) convertView.findViewById(R.id.cart_product_total_price);
            viewHolder.imageProduct = (ImageView) convertView.findViewById(R.id.cart_product_image);
            viewHolder.layoutIncrease = (LinearLayout) convertView.findViewById(R.id.btn_increase);
            viewHolder.layoutDecrease = (LinearLayout) convertView.findViewById(R.id.btn_decrease);
            viewHolder.imageDelete = (ImageView) convertView.findViewById(R.id.image_delete);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textProductPrice.setText(productArrayList.get(position).getItemPrice());
        viewHolder.textProductName.setText(productArrayList.get(position).getItemName());
        viewHolder.textProductQty.setText(productArrayList.get(position).getUserSelectedQty()+"");
        int payableAmount = productArrayList.get(position).getUserSelectedQty() * Integer.parseInt(productArrayList.get(position).getItemPrice());
        viewHolder.textPayablePrice.setText(payableAmount+"");

        //Picasso.with(context).load(productArrayList.get(position).getItemImageURL()).error(android.R.drawable.stat_notify_error).into(viewHolder.imageProduct);

        viewHolder.layoutIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int empTotalItemQty = productArrayList.get(position).getEmpTotalItemsQty();
                int newQty = productArrayList.get(position).getUserSelectedQty() + 1;
                if(newQty <= empTotalItemQty) {
                    productArrayList.get(position).setUserSelectedQty(newQty);
                    refreshBottomViews(textCartTotalAmount, textTax1, textTax2, textCartTotalDiscount, textCartNetAmount);
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
                if(newQty > 0 ) {
                    productArrayList.get(position).setUserSelectedQty(newQty);
                    refreshBottomViews(textCartTotalAmount, textTax1, textTax2, textCartTotalDiscount, textCartNetAmount);
                    notifyDataSetChanged();
                }
            }
        });

        viewHolder.imageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog((Activity) context,"Are you sure to delete this item from cart","Cart", position);
            }
        });
        return convertView;
    }

    public void refreshBottomViews(TextView textCartTotalAmount, TextView textTax1, TextView textTax2, TextView textCartTotalDiscount, TextView textCartNetAmount) {
        float totalPrice = 0;
        float totalPriceWithTax;
        float totalPriceWithDiscount;
        if(this.textCartTotalAmount==null) {
            this.textCartTotalAmount = textCartTotalAmount;
            this.textTax1 = textTax1;
            this.textTax2 = textTax2;
            this.textCartTotalDiscount = textCartTotalDiscount;
            this.textCartNetAmount = textCartNetAmount;
        }

        for (int i = 0; i < productArrayList.size(); i++) {
            int productTotal = Integer.parseInt(productArrayList.get(i).getItemPrice()) * productArrayList.get(i).getUserSelectedQty();
            totalPrice = totalPrice + productTotal;
        }

        totalPriceWithTax = (totalPrice * 5)/100 + totalPrice;

        totalPriceWithDiscount = totalPriceWithTax - 10;

        textCartTotalAmount.setText(totalPrice+"");
        textCartNetAmount.setText(totalPriceWithDiscount+"");
    }

    private void alertDialog(final Activity activity, String msg, String titile, final int position){
        AlertDialog.Builder adb = new AlertDialog.Builder(activity);
        adb.setMessage(msg);
        adb.setTitle(titile);
        adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                productArrayList.remove(position);
                notifyDataSetChanged();
                refreshBottomViews(textCartTotalAmount, textTax1, textTax2, textCartTotalDiscount, textCartNetAmount);
                dialog.dismiss();
                if(productArrayList.size()==0)
                    ((Activity) context).finish();
            }
        });
        adb.setNegativeButton("Cancel",null);
        adb.show();

    }

}