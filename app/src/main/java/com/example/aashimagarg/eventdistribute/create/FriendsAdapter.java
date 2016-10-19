package com.example.aashimagarg.eventdistribute.create;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.aashimagarg.eventdistribute.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    private ArrayList<String> selectedFriends = new ArrayList<>();

    //Provide a direct reference to each of the views within a data item
    //Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvName;
        public final ImageView ivProfilePic;
        public final CheckBox cbSelected;

        //view lookups to find each subview of entire item row
        public ViewHolder(View itemView) {
            //Stores the itemView in a public final member variable that can be used to access the context from any ViewHolder instance.
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            ivProfilePic = (ImageView) itemView.findViewById(R.id.iv_profile_pic);
            cbSelected = (CheckBox) itemView.findViewById(R.id.cb_selected);
        }
    }

    private final List<Friend> friendsList = new ArrayList<>();
    private TextView tvCheckCount;
    private TextView tvSelectAll;

    public FriendsAdapter(TextView count, TextView all) {
        tvCheckCount = count;
        tvCheckCount.setText(String.valueOf(getInvitedFriendCount()) + " Selected");
        tvSelectAll = all;
    }

    @Override
    public FriendsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        //Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_friend, parent, false);
        //Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    //populating data into the item through holder
    @Override
    public void onBindViewHolder(final FriendsAdapter.ViewHolder viewHolder, int position) {
        //Get the data model based on position
        final Friend friend = friendsList.get(position);
        //Set item views based on your views and data model
        TextView textView = viewHolder.tvName;
        textView.setText(friend.getName());
        if (friend.getProfileUrl() != null){
            viewHolder.ivProfilePic.setVisibility(View.VISIBLE);
            Glide.with(viewHolder.itemView.getContext()).load(friend.getProfileUrl()).asBitmap().centerCrop().into(viewHolder.ivProfilePic);
        } else {
            viewHolder.ivProfilePic.setVisibility(View.GONE);
        }

        if (friend.invited) {
            viewHolder.cbSelected.setChecked(true);
            if (!selectedFriends.contains(friend.getFbId())) {
                selectedFriends.add(friend.getFbId());
                tvCheckCount.setText(String.valueOf(getInvitedFriendCount()) + " Selected");
            }
        } else {
            viewHolder.cbSelected.setChecked(false);
            if (selectedFriends.contains(friend.getFbId())) {
                selectedFriends.remove(friend.getFbId());
                tvCheckCount.setText(String.valueOf(getInvitedFriendCount()) + " Selected");
            }
        }

        //poll for checkbox clicks
        viewHolder.cbSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.cbSelected.isChecked()) {
                    selectedFriends.add(friend.getFbId());
                    friend.invited = true;
                    tvCheckCount.setText(String.valueOf(getInvitedFriendCount()) + " Selected");
                    tvSelectAll.setText(R.string.unselect_all);
                } else {
                    selectedFriends.remove(friend.getFbId());
                    friend.invited = false;
                    tvCheckCount.setText(String.valueOf(getInvitedFriendCount()) + " Selected");
                    tvSelectAll.setText(R.string.select_all);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    public void addAll(ArrayList<Friend> friends) {
        for (int i = 0 ; i < friends.size(); i++) {
            friendsList.add(friends.get(i));
        }
    }

    public ArrayList<String> getSelectedFriends() {
        return selectedFriends;
    }

    public int getInvitedFriendCount() {
        int count = 0;
        for (int i = 0; i < friendsList.size(); i++) {
            if (friendsList.get(i).invited) {
                count++;
            }
        }
        return count;
    }
}
