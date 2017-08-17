package com.jaipurice.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jaipurice.app.R;
import com.jaipurice.app.adpaters.CartAdapter;
import com.jaipurice.app.model.Product;
import com.jaipurice.app.utils.Constants;
import com.jaipurice.app.utils.SharedPreferenceUtility;
import com.jaipurice.app.utils.UnicodeFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by Nishant on 8/12/2017.
 */

public class CartActivity extends BaseActivity{
    private ArrayList<Product> productArrayList;
    private String TAG = this.getClass().getName();
    private ListView listCartItems;
    private TextView textCartTotalAmount, textTax1, textTax2, textCartTotalDiscount, textCartNetAmount;
    private CartAdapter cartAdapter;
    private Button btnCheckOut;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listCartItems = (ListView) findViewById(R.id.listCartItems);
        productArrayList = (ArrayList<Product>) getIntent().getSerializableExtra("list");
        cartAdapter = new CartAdapter(CartActivity.this,R.layout.cart_item_row, productArrayList);
        listCartItems.setAdapter(cartAdapter);
        findViews();

        cartAdapter.refreshBottomViews(textCartTotalAmount, textTax1, textTax2, textCartTotalDiscount, textCartNetAmount);

        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog(CartActivity.this,"Are you sure to checkout these items","Checkout");
            }
        });
    }

    private void findViews() {
        textCartTotalAmount = (TextView) findViewById(R.id.textCartTotalAmount);
        textTax1 = (TextView) findViewById(R.id.textTax1);
        textTax2 = (TextView) findViewById(R.id.textTax2);
        textCartTotalDiscount = (TextView) findViewById(R.id.textCartTotalDiscount);
        textCartNetAmount = (TextView) findViewById(R.id.textCartNetAmount);
        btnCheckOut = (Button) findViewById(R.id.buttonCheckout);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_cart;
    }

    private void alertDialog(final Activity activity, String msg, String titile){
        AlertDialog.Builder adb = new AlertDialog.Builder(activity);
        adb.setMessage(msg);
        adb.setTitle(titile);
        adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkOut();
                dialog.dismiss();
            }
        });
        adb.setNegativeButton("Cancel",null);
        adb.show();

    }

    private void checkOut() {
        String json = buildJson();
        printBill();
    }

    private String buildJson() {
        String json="";
        float totalPrice=0f;
        try{
            JSONObject mainObject = new JSONObject();
            JSONObject detailsObject = new JSONObject();
            JSONArray orders = new JSONArray();
            JSONObject priceObject = new JSONObject();

            detailsObject.put("employee_id", SharedPreferenceUtility.getInstance().get(Constants.PREF_EMP_ID));
            detailsObject.put("customer_id", CustomerActivity.customerId);
            mainObject.put("details",detailsObject);

            for (int i = 0; i < productArrayList.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("item_id",productArrayList.get(i).getItemId());
                jsonObject.put("quantity",productArrayList.get(i).getUserSelectedQty()+"");
                float subTotal = productArrayList.get(i).getUserSelectedQty() * Float.parseFloat(productArrayList.get(i).getItemPrice());
                totalPrice = totalPrice+subTotal;
                jsonObject.put("sub_total",subTotal+"");
                orders.put(i,jsonObject);
            }

            mainObject.put("orders",orders);

            float tax = (totalPrice * 5)/100;
            float discount = 10f;

            priceObject.put("service_tax",tax+"");
            priceObject.put("discount",discount+"");
            priceObject.put("total_price",textCartNetAmount.getText().toString());
            mainObject.put("price",priceObject);

            json = mainObject.toString();
            Log.e("json",json);
        }catch (JSONException e){e.printStackTrace();}
        return json;
    }

    private void printBill(){
        Thread t = new Thread() {
            public void run() {
                try {
                    OutputStream os = ConnectionActivity.mBluetoothSocket
                            .getOutputStream();

                    String temp = "PRINT";
                    os.write(temp.getBytes());

                    String BILL = "Hello";

                    BILL = "           XXXX MART    \n"
                            + "           XX.AA.BB.CC.     \n " +
                            "         NO 25 ABC ABCDE    \n" +
                            "          XXXXX YYYYYY      \n" +
                            "           MMM 590019091      \n";
                    BILL = BILL
                            + "----------------------------\n";


                    BILL = BILL + String.format("%1$-10s %2$10s %3$13s %4$10s", "Item", "Qty", "Rate", "Totel");
                    BILL = BILL + "\n";
                    BILL = BILL
                            + "------------------------------";
                    BILL = BILL + "\n " + String.format("%1$-10s %2$10s %3$11s %4$10s", "item-001", "5", "10", "50.00");
                    BILL = BILL + "\n " + String.format("%1$-10s %2$10s %3$11s %4$10s", "item-002", "10", "5", "50.00");
                    BILL = BILL + "\n " + String.format("%1$-10s %2$10s %3$11s %4$10s", "item-003", "20", "10", "200.00");
                    BILL = BILL + "\n " + String.format("%1$-10s %2$10s %3$11s %4$10s", "item-004", "50", "10", "500.00");

                    BILL = BILL
                            + "\n-----------------------------";
                    BILL = BILL + "\n\n ";

                    BILL = BILL + "             Total Qty:" + "      " + "85" + "\n";
                    BILL = BILL + "             Total Value:" + "     " + "700.00" + "\n";

                    BILL = BILL
                            + "--------------------------------\n";
                    BILL = BILL + "\n\n ";
                    os.write(BILL.getBytes());
                    //This is printer specific code you can comment ==== > Start

                    // Setting height
                    int gs = 29;
                    os.write(intToByteArray(gs));
                    int h = 104;
                    os.write(intToByteArray(h));
                    int n = 162;
                    os.write(intToByteArray(n));

                    // Setting Width
                    int gs_width = 29;
                    os.write(intToByteArray(gs_width));
                    int w = 119;
                    os.write(intToByteArray(w));
                    int n_width = 2;
                    os.write(intToByteArray(n_width));


                } catch (Exception e) {
                    Log.e("MainActivity", "Exe ", e);
                }
            }
        };
        Log.e("DSFF","dfsfds");
        t.start();

    }

    public static byte intToByteArray(int value) {
        byte[] b = ByteBuffer.allocate(4).putInt(value).array();

        for (int k = 0; k < b.length; k++) {
            System.out.println("Selva  [" + k + "] = " + "0x"
                    + UnicodeFormatter.byteToHex(b[k]));
        }

        return b[3];
    }

}