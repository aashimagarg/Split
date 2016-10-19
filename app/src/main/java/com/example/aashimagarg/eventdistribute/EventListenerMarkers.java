package com.example.aashimagarg.eventdistribute;

import android.util.Log;

import com.example.aashimagarg.eventdistribute.timeline.EventsListAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class EventListenerMarkers {

    private static final String TAG = "EventListenerMarkers";
    DatabaseReference eventReference;
    ChildEventListener eventListener;

    public EventListenerMarkers(DatabaseReference eventReferenceParam, final String eventId, final EventsListAdapter eventsListAdapter) {
        eventReference = eventReferenceParam;
        eventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String str) {
                if (dataSnapshot.getKey().equals(eventId)) {
                    Event newEvent = dataSnapshot.getValue(Event.class);
                    eventsListAdapter.addChronologically(newEvent);
                    eventsListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String str) {
                // TODO(gbelton t12351250): implementing the other ChildEventListener functions
                Log.d(TAG, "events changed");
                Event newEvent = dataSnapshot.getValue(Event.class);
                eventsListAdapter.addChronologically(newEvent);
                eventsListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // TODO(gbelton t12351250): implementing the other ChildEventListener functions
                Log.d(TAG, "events removed");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String str) {
                // TODO(gbelton t12351250): implementing the other ChildEventListener functions
                Log.d(TAG, "events moved");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO(gbelton t12351250): implementing the other ChildEventListener functions
                Log.d(TAG, "events cancel");
            }
        };
    }

    public void onResume() {
        eventReference.addChildEventListener(eventListener);
    }

    public void onPause() {
        if (eventReference != null) {
            eventReference.removeEventListener(eventListener);
        }
    }
}
