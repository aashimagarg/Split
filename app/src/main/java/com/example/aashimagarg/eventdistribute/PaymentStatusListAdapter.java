package com.example.aashimagarg.eventdistribute;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class PaymentStatusListAdapter extends BaseAdapter {

    private final Map<String, Integer> paidInfo = new HashMap<>();
    private final List<User> users = new ArrayList<>();
    private String host;

    public PaymentStatusListAdapter() {
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public User getItem(int position) {
        return users.get(position);
    }

    @Override
    //this returns the id of the listview section 
    //is a necessary method for the BaseAdapter extension
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final User user = getItem(position);
        Set<String> paidUsers = paidInfo.keySet();
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_status, parent, false);
        }
        TextView tvHost = (TextView) convertView.findViewById(R.id.tv_host);
        if (user.getUid().equals(host)){
            tvHost.setText("Host");
        }
        else {
            tvHost.setText("");
        }
        TextView tvName = (TextView) convertView.findViewById(R.id.tv_name);
        tvName.setText(user.getName());
        TextView tvPaymentStatus = (TextView) convertView.findViewById(R.id.tv_payment_status);
        if(paidUsers.contains(user.getFbId())) {
            tvPaymentStatus.setText(parent.getContext().getString(R.string.money, paidInfo.get(user.getFbId())));
        } else {
            tvPaymentStatus.setText(R.string.pending);
        }
        final ImageView ivProfilePicture = (ImageView) convertView.findViewById(R.id.iv_profile_pic);
        Picasso.with(ivProfilePicture.getContext())
                .load(user.getProfileUrl())
                .transform(new RoundedCornersTransformation(5, 5))
                .into(ivProfilePicture);
        ivProfilePicture.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return convertView;
    }

    public void add(User user, int amount) {
        users.add(user);
        if (amount > 0) {
            paidInfo.put(user.getFbId(), amount);
        }
    }

    public void updateHost(String hostId){
        host = hostId;
    }
}
