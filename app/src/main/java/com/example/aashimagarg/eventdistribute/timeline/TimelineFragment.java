package com.example.aashimagarg.eventdistribute.timeline;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.aashimagarg.eventdistribute.R;

public abstract class TimelineFragment extends Fragment {

    /* package */ EventsListAdapter eventsListAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventsListAdapter = new EventsListAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events_list, parent, false);
        ListView lvEvents = (ListView) view.findViewById(R.id.lv_events);
        lvEvents.setAdapter(eventsListAdapter);
        return view;
    }
}
