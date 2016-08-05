package com.example.aashimagarg.eventdistribute;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaidFragment extends MemberPaymentStatusFragment {

    private static final String TAG = "Not Paid Fragment";
    private static final String CURRENT_EVENT = "currentEvent";
    private static final String HOST_ID = "hostId";

    private DatabaseReference userDatabaseRef;
    private DatabaseReference eventContributorsDatabaseRef;
    private ChildEventListener eventContributorsDatabaseListener;
    private ChildEventListener userDatabaseListener;
    private List<String> paidUsers = new ArrayList<>();
    private Map<String, Integer> contributionInfo = new HashMap<>();
    private String hostId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, parent, savedInstanceState);
        //this might not be implemented
        Bundle args = getArguments();
        final String eventId = args.getString(CURRENT_EVENT);
        hostId = args.getString(HOST_ID);
        paymentStatusListAdapter.notifyDataSetChanged();

        //find out which users have already contributed to the current event
        eventContributorsDatabaseRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://eventribute.firebaseio.com/eventContributors/" + eventId);
        eventContributorsDatabaseListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String str) {
                int contributionAmount = Integer.valueOf(dataSnapshot.getValue().toString());
                if (contributionAmount > 0) {
                    //add the event id to the list of all events for which the
                    //current user has already contributed
                    //this then affects the views in the PaymentStatusListAdapter
                    String userId = dataSnapshot.getKey();
                    contributionInfo.put(userId, Integer.valueOf(contributionAmount));
                    getUserFromId(userId);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String str) {
                // TODO(gbelton t12351250): implementing the other ChildEventListener functions
                Log.d(TAG, "events contributors changed");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // TODO(gbelton t12351250): implementing the other ChildEventListener functions
                Log.d(TAG, "events contributors removed");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String str) {
                // TODO(gbelton t12351250): implementing the other ChildEventListener functions
                Log.d(TAG, "events contributors moved");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO(gbelton t12351250): implementing the other ChildEventListener functions
                Log.d(TAG, "events contributors cancel");
            }
        };
        eventContributorsDatabaseRef.addChildEventListener(eventContributorsDatabaseListener);
        return view;
    }

    @Override
    public void onDestroyView() {
        if (userDatabaseRef != null) {
            userDatabaseRef.removeEventListener(userDatabaseListener);
        }
        if (eventContributorsDatabaseRef != null) {
            eventContributorsDatabaseRef.removeEventListener(eventContributorsDatabaseListener);
        }
        super.onDestroyView();
    }

    private void getUserFromId(final String userIdParam) {
        userDatabaseRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://eventribute.firebaseio.com/users");
        userDatabaseListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String str) {
                if (dataSnapshot.getKey().equals(userIdParam)) {
                    User newUser = dataSnapshot.getValue(User.class);
                    int contribution = contributionInfo.get(userIdParam);
                    paymentStatusListAdapter.add(newUser, contribution);
                    paymentStatusListAdapter.updateHost(hostId);
                    paymentStatusListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String str) {
                // TODO(gbelton t12351250): implementing the other ChildEventListener functions
                Log.d(TAG, "user changed");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // TODO(gbelton t12351250): implementing the other ChildEventListener functions
                Log.d(TAG, "user removed");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String str) {
                // TODO(gbelton t12351250): implementing the other ChildEventListener functions
                Log.d(TAG, "user moved");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO(gbelton t12351250): implementing the other ChildEventListener functions
                Log.d(TAG, "users cancel");
            }
        };
        userDatabaseRef.addChildEventListener(userDatabaseListener);
    }
}
