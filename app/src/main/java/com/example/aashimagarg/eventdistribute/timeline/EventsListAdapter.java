package com.example.aashimagarg.eventdistribute.timeline;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aashimagarg.eventdistribute.Event;
import com.example.aashimagarg.eventdistribute.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class EventsListAdapter extends BaseAdapter {

    private final List<Event> events = new ArrayList<>();
    private final List<String> userPaid = new ArrayList<>();

    public EventsListAdapter() {
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public Event getItem(int i) {
        return events.get(i);
    }

    @Override
    //this returns the id of the listview section clicked
    //is a necessary method for the BaseAdapter extension
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Event event = getItem(position);
        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        }
        TextView tvName = (TextView) convertView.findViewById(R.id.tv_event_name);
        tvName.setText(event.getName());
        TextView tvDate = (TextView) convertView.findViewById(R.id.tv_date);
        tvDate.setText(event.getStartDate());
        //todo: < task 12351434>
        TextView tvPaid = (TextView) convertView.findViewById(R.id.tv_paid);
        if(userPaid.contains(event.getUid())){
            tvPaid.setText(R.string.paid);
        } else {
            tvPaid.setText(R.string.pending);
        }
        final ImageView ivCoverPhoto = (ImageView) convertView.findViewById(R.id.iv_cover_photo);
        ivCoverPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Picasso.with(ivCoverPhoto.getContext())
                .load(event.getCoverImageUrl())
                .transform(new RoundedCornersTransformation(5, 5))
                .into(ivCoverPhoto);
        return convertView;
    }

    public void add(Event event) {
        events.add(0, event);
    }

    public void addChronologically(Event event){
        if (events.size() == 0 && event.getEpochTime() >= System.currentTimeMillis()) {
            events.add(event);
        } else {
            for (int i = 0; i < events.size(); i++) {
                //remove past events
                if (event.getEpochTime() < System.currentTimeMillis()) {
                    break;
                }
                //checks for event update
                else if (event.getUid().equals(events.get(i).getUid())) {
                    events.set(i, event);
                    break;
                }
                //order upcoming events
                else if (event.getEpochTime() <= events.get(i).getEpochTime()) {
                    events.add(i, event);
                    break;
                } else if (i == events.size() - 1) {
                    events.add(event);
                    break;
                } else if (event.getEpochTime() > events.get(i).getEpochTime() && event.getEpochTime() < events.get(i + 1).getEpochTime()) {
                    events.add(i + 1, event);
                    break;
                }
            }
        }
    }

    public void setPaid(String eventId) {
        userPaid.add(eventId);
    }
}
