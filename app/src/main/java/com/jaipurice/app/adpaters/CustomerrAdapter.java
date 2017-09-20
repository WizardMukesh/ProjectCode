package com.jaipurice.app.adpaters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaipurice.app.model.CustomerModel;
import com.jaipurice.app.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by len on 6/30/2017.
 */

public class CustomerrAdapter extends BaseAdapter implements Filterable {
    private Context mContext;
    public static int mSelectedPosition;


    private List<CustomerModel> originalList = new ArrayList<>();
    private List<CustomerModel> filteredList;

    public CustomerrAdapter(Context mContext, ArrayList<CustomerModel> originalList) {
        this.mContext = mContext;
        this.originalList = originalList;
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
            holder.textAddress= (TextView) row.findViewById(R.id.customer_address);
            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }
        final CustomerModel item = originalList.get(position);
        if (item.getCustomerName() != null) {
            holder.customer_name.setText(item.getCustomerName());
        }
        else {
            holder.customer_name.setText("N/A");
        }
        if (item.getCustomerContactPerson() != null) {
            holder.customer_phone_no.setText(item.getCustomerContactPerson());
        }
        else {
            holder.customer_phone_no.setText("N/A");
        }

        holder.textAddress.setText(item.getAddress());


        Log.e("getView: ","http://lamigraapp.com/icealert/"+item.getCustomerContactPerson());


        return row;

    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return originalList.size();
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

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<CustomerModel> results = new ArrayList<>();
                if (filteredList == null)
                    filteredList = originalList;
                if (constraint != null) {
                    if (filteredList != null && filteredList.size() > 0) {
                        for (final CustomerModel model : filteredList) {
                            if (model.getCustomerName().toLowerCase()
                                    .contains(constraint.toString().toLowerCase()))
                                results.add(model);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                originalList = (ArrayList<CustomerModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    class ViewHolder {
        TextView customer_name,customer_phone_no, textAddress;

    }
}

