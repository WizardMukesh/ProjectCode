package com.jaipurice.app.adpaters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaipurice.app.Model.CustomerModel;
import com.jaipurice.app.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by len on 6/30/2017.
 */

public class CustomerrAdapter extends BaseAdapter {
    private Context mContext;
    public static int mSelectedPosition;


    private List<CustomerModel> mData = new ArrayList<CustomerModel>();

    public CustomerrAdapter(Context mContext, ArrayList<CustomerModel> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }


    public void setData(List<CustomerModel> mListData) {
        this.mData = mListData;
        notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final ViewHolder holder;


        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(R.layout.customer_row, parent, false);
            holder = new ViewHolder();
            holder.customer_name= (TextView) row.findViewById(R.id.customer_name);
            holder.customer_phone_no= (TextView) row.findViewById(R.id.customer_number);
            holder.imageView_user= (CircleImageView) row.findViewById(R.id.imageView_user);
            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }
        final CustomerModel item = mData.get(position);
        if (item.getCustomerName() != null) {
            holder.customer_name.setText(item.getCustomerName());
        }
        else {
            holder.customer_name.setText("no data");
        }
        if (item.getCustomerPhone() != null) {
            holder.customer_phone_no.setText(item.getCustomerName());
        }
        else {
            holder.customer_phone_no.setText("no data");
        }

        Picasso.with(mContext).load(item.getCustomerPhoto()).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.imageView_user);

        Log.e("getView: ","http://lamigraapp.com/icealert/"+item.getCustomerPhone());

//        Picasso.Builder builder = new Picasso.Builder(mContext);
//        builder.listener(new Picasso.Listener() {
//            @Override
//            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
//                Log.e("IIII",exception.toString());
//            }
//        });
//        builder.build().load("http://lamigraapp.com/icealert/"+item.getImage()).transform(new CircleTransform()).into(holder.imageView_user);

        return row;

    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    class ViewHolder {
        TextView customer_name,customer_phone_no;
        ImageView imageView_user;

    }
}

