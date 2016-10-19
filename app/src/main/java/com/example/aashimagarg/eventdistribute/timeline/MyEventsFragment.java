package com.example.aashimagarg.eventdistribute.timeline;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.aashimagarg.eventdistribute.Event;
import com.example.aashimagarg.eventdistribute.EventDetailsActivity;
import com.example.aashimagarg.eventdistribute.EventListenerMarkers;
import com.example.aashimagarg.eventdistribute.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class MyEventsFragment extends TimelineFragment {

    private static final String TAG = "My Events Fragment";
    private static final String EVENT = "event";
    private static final String CURRENT_USER = "currentUser";

    private DatabaseReference eventDatabaseRef;
    private DatabaseReference hostDatabaseRef;
    private DatabaseReference eventsPaidDatabaseRef;
    private ChildEventListener eventsPaidDatabaseListener;
    private ChildEventListener userDatabaseListener;
    private Map<String, EventListenerMarkers> listenerMap = new HashMap<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, parent, savedInstanceState);
        ListView lvEvents = (ListView) view.findViewById(R.id.lv_events);
        lvEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long longNum) {
                Intent intent = new Intent(getContext(), EventDetailsActivity.class);
                Event event = (Event) adapterView.getItemAtPosition(position);
                intent.putExtra(EVENT, event);
                //this takes the user to the details activity for that event
                startActivity(intent);
            }
        });
        Bundle args = getArguments();
        final String currentUserId = args.getString(CURRENT_USER);
        //find the event ids of the events that the current user is hosting
        hostDatabaseRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://eventribute.firebaseio.com/eventsHosting/" + currentUserId);
        userDatabaseListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String str) {
                String eventId = dataSnapshot.getKey().toString();
                eventDatabaseRef = FirebaseDatabase.getInstance()
                        .getReferenceFromUrl("https://eventribute.firebaseio.com/events");
                EventListenerMarkers newMarker = new EventListenerMarkers(eventDatabaseRef, eventId, eventsListAdapter);
                listenerMap.put(eventId, newMarker);
                listenerMap.get(eventId).onResume();
        }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String str) {
                // TODO(gbelton t12351250): implementing the other ChildEventListener functions
                Log.d(TAG, "events attending changed");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // TODO(gbelton t12351250): implementing the other ChildEventListener functions
                Log.d(TAG, "events attending removed");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String str) {
                // TODO(gbelton t12351250): implementing the other ChildEventListener functions
                Log.d(TAG, "events attending moved");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO(gbelton t12351250): implementing the other ChildEventListener functions
                Log.d(TAG, "events attending cancel");
            }
        };
        hostDatabaseRef.addChildEventListener(userDatabaseListener);
        eventsPaidDatabaseRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://eventribute.firebaseio.com/eventsContributedTo/" + currentUserId);
        eventsPaidDatabaseListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String str) {
                String eventId = dataSnapshot.getKey().toString();
                int contributionAmount = Integer.valueOf(dataSnapshot.getValue().toString());
                if (contributionAmount > 0){
                    //add the event id to the list of all events for which the
                    //current user has already contributed
                    //this then affects the views in the EventsListAdapter
                    eventsListAdapter.setPaid(eventId);
                    eventsListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String str) {
                // TODO(gbelton t12351250): implementing the other ChildEventListener functions
                Log.d(TAG, "events attending changed");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // TODO(gbelton t12351250): implementing the other ChildEventListener functions
                Log.d(TAG, "events attending removed");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String str) {
                // TODO(gbelton t12351250): implementing the other ChildEventListener functions
                Log.d(TAG, "events attending moved");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO(gbelton t12351250): implementing the other ChildEventListener functions
                Log.d(TAG, "events attending cancel");
            }
        };
        eventsPaidDatabaseRef.addChildEventListener(eventsPaidDatabaseListener);
        return view;
    }

    @Override
    public void onDestroyView() {
        if (hostDatabaseRef != null) {
            hostDatabaseRef.removeEventListener(userDatabaseListener);
        }
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        if (hostDatabaseRef != null) {
            hostDatabaseRef.removeEventListener(userDatabaseListener);
        }
        if (eventsPaidDatabaseRef != null) {
            eventsPaidDatabaseRef.removeEventListener(eventsPaidDatabaseListener);
        }
        for (int i = 0; i < listenerMap.keySet().size(); i++) {
            String key = listenerMap.keySet().toArray()[i].toString();
            listenerMap.get(key).onPause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        eventsPaidDatabaseRef.addChildEventListener(eventsPaidDatabaseListener);
        hostDatabaseRef.addChildEventListener(userDatabaseListener);
        for (int i = 0; i < listenerMap.keySet().size(); i++) {
            String key = listenerMap.keySet().toArray()[i].toString();
            listenerMap.get(key).onResume();
        }
        super.onResume();
    }
}
