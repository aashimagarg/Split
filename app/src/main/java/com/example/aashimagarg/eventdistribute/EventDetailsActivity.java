package com.example.aashimagarg.eventdistribute;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.example.aashimagarg.eventdistribute.create.CreateActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class EventDetailsActivity extends AppCompatActivity {

    private static final String EVENT = "event";
    private static final String CONTRIBUTION = "contribution";
    private static final String HOST = "host";
    private static final int VENMO_REQUEST_CODE = 99;
    private static final String EDIT = "edit";
    private static final String EDITED_EVENT = "edited_event";
    private static final String TAG = "Event Details Activity";
    private static final String CURRENT_EVENT = "currentEvent";
    private static final String HOST_ID = "hostId";
    private static final int EDIT_REQUEST_CODE = 80;

    private Event event;
    private int amountRaised;
    private ProgressBar pbContributionProgress;
    private Button btnContribute;
    private TextView tvAmountNeeded;
    private boolean userHasContributed;
    private String currentUserId;
    private TextView tvLocation;
    private TextView tvHostName;
    private TextView tvContributionAmount;
    private String hostName;
    ChildEventListener contributionDatabaseListener;
    DatabaseReference contributionDatabaseRef;
    ChildEventListener hostDatabaseListener;
    DatabaseReference hostDatabaseRef;
    ImageView ivCoverImage;
    FloatingActionButton editEvent;
    TextView tvPaidInfo;
    FragmentTimelinePagerAdapter adapterViewPager;
    private String currentEventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ViewPager vpPager = (ViewPager) findViewById(R.id.vp_paid_pager);
        tvPaidInfo = (TextView) findViewById(R.id.tv_paid_info);
        adapterViewPager = new EventDetailsActivity.PaymentsPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.ts_tabs);
        tabStrip.setViewPager(vpPager);
        event = (Event) getIntent().getSerializableExtra(EVENT);
        currentEventId= event.getUid();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        populateActivity();
        if(!currentUserId.equals(event.getHost())) {
            editEvent = (FloatingActionButton) findViewById(R.id.fab_edit_event);
            editEvent.setVisibility(View.INVISIBLE);
        }
    }

    public class PaymentsPagerAdapter extends FragmentTimelinePagerAdapter {
        private String tabTitles[] = {"Not Paid", "Paid"};
        public PaymentsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return newInstanceNotPaidFragment(currentEventId, event.getHost());
            }
            else if (position == 1) {
                return newInstancePaidFragment(currentEventId, event.getHost());
            }
            else {
                return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }

    public static NotPaidFragment newInstanceNotPaidFragment(String currentEventId, String hostId) {
        NotPaidFragment fragment = new NotPaidFragment();
        Bundle args = new Bundle();
        args.putString(CURRENT_EVENT, currentEventId);
        args.putString(HOST_ID, hostId);
        fragment.setArguments(args);
        return fragment;
    }

    public static PaidFragment newInstancePaidFragment(String currentEventId, String hostId) {
        PaidFragment fragment = new PaidFragment();
        Bundle args = new Bundle();
        args.putString(CURRENT_EVENT, currentEventId);
        args.putString(HOST_ID, hostId);
        fragment.setArguments(args);
        return fragment;
    }

    private void populateActivity() {
        //get the views
        ivCoverImage = (ImageView) findViewById(R.id.iv_cover_photo);
        final TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        final TextView tvDescription = (TextView) findViewById(R.id.tv_description);
        final TextView tvStartTime = (TextView) findViewById(R.id.tv_start_time);
        tvAmountNeeded = (TextView) findViewById(R.id.tv_amount_needed);
        tvLocation = (TextView) findViewById(R.id.tv_location);
        btnContribute = (Button) findViewById(R.id.btn_contribute);
        btnContribute.setVisibility(View.VISIBLE);
        tvContributionAmount = (TextView) findViewById(R.id.et_contribution_amount);
        tvContributionAmount.setVisibility(View.VISIBLE);
        pbContributionProgress = (ProgressBar) findViewById(R.id.pb_contribution_progress);
        getHostName();
        //assign the views their proper content
        Typeface mainFont = Typeface.createFromAsset(getAssets(), "fonts/gnuolane rg.ttf");
        tvTitle.setTypeface(mainFont);
        tvTitle.setText(event.getName());
        tvLocation.setText(event.getLocation());
        tvDescription.setText(event.getDescription());
        tvStartTime.setText(event.getFullStartTime());
        Picasso.with(ivCoverImage.getContext())
                .load(event.getCoverImageUrl())
                .transform(new RoundedCornersTransformation(5, 5))
                .into(ivCoverImage);
        ivCoverImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ivCoverImage.setColorFilter(Color.argb(100, 0, 0, 0)); // black Tint
        pbContributionProgress.setMax(event.getAmountNeeded());
        tvAmountNeeded.setText(getString(R.string.goal, amountRaised, event.getAmountNeeded()));

        //set the correct value for the progress bar
        getContributionProgress();
    }

    public void getHostName(){
        hostDatabaseRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://eventribute.firebaseio.com/users/" + event.getHost());
        hostDatabaseListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String str) {
                if (dataSnapshot.getKey().equals("name")) {
                    hostName = dataSnapshot.getValue().toString();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String str) {
                // TODO(gbelton t12351250): implementing the other ChildEventListener functions
                Log.d(TAG, "host changed");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // TODO(gbelton t12351250): implementing the other ChildEventListener functions
                Log.d(TAG, "host removed");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String str) {
                // TODO(gbelton t12351250): implementing the other ChildEventListener functions
                Log.d(TAG, "host moved");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO(gbelton t12351250): implementing the other ChildEventListener functions
                Log.d(TAG, "host cancel");
            }
        };
        hostDatabaseRef.addChildEventListener(hostDatabaseListener);
    }

    private void getContributionProgress() {
        contributionDatabaseRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://eventribute.firebaseio.com/eventContributors/" + event.getUid().toString());
        contributionDatabaseListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String str) {
                //check if the user has contributed already to this event
                //if so don't let them contribute again
                int contribution = Integer.valueOf(dataSnapshot.getValue().toString());
                if (dataSnapshot.getKey().toString().equals(currentUserId) && contribution > 0) {
                    userHasContributed = true;
                    btnContribute.setVisibility(View.GONE);
                    tvContributionAmount.setVisibility(View.GONE);
                    tvPaidInfo.setText(getString(R.string.you_contributed, contribution));
                }
                amountRaised += contribution;
                pbContributionProgress.setProgress(amountRaised);
                tvAmountNeeded.setText(getString(R.string.goal, amountRaised, event.getAmountNeeded()));
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
        contributionDatabaseRef.addChildEventListener(contributionDatabaseListener);
    }

    @Override
    protected void onDestroy() {
        contributionDatabaseRef.removeEventListener(contributionDatabaseListener);
        hostDatabaseRef.removeEventListener(hostDatabaseListener);
        super.onDestroy();
    }

    public void onContribute(View view) {
        Intent intent = new Intent(EventDetailsActivity.this, VenmoActivity.class);
        intent.putExtra(HOST, hostName);
        startActivityForResult(intent, VENMO_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VENMO_REQUEST_CODE && resultCode == RESULT_OK) {
            //update the database values
            int amountContributing = data.getIntExtra(CONTRIBUTION, -1);
            DatabaseReference dataRefContributedTo = FirebaseDatabase.getInstance().getReference("eventsContributedTo/" + currentUserId);
            dataRefContributedTo.child(event.getUid()).setValue(amountContributing);
            DatabaseReference dataRefContributors = FirebaseDatabase.getInstance().getReference("eventContributors/" + event.getUid());
            dataRefContributors.child(currentUserId).setValue(amountContributing);
            pbContributionProgress.setProgress(amountRaised);
            tvPaidInfo.setText(getString(R.string.you_contributed, amountContributing));
            btnContribute.setVisibility(View.GONE);
            tvContributionAmount.setVisibility(View.GONE);
        }
        if (requestCode == EDIT_REQUEST_CODE && resultCode == RESULT_OK) {
            event = (Event) data.getSerializableExtra(EDITED_EVENT);
            populateActivity();
        }
    }

    public void onEdit(View view) {
        Intent intent = new Intent(EventDetailsActivity.this, CreateActivity.class);
        intent.putExtra(EDIT, true);
        intent.putExtra(EVENT, event);
        startActivityForResult(intent, EDIT_REQUEST_CODE);
    }
}
