package com.example.aashimagarg.eventdistribute.timeline;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.astuetz.PagerSlidingTabStrip;
import com.example.aashimagarg.eventdistribute.FragmentTimelinePagerAdapter;
import com.example.aashimagarg.eventdistribute.LogoutAlertDialogFragment;
import com.example.aashimagarg.eventdistribute.create.CreateActivity;
import com.example.aashimagarg.eventdistribute.LogInActivity;
import com.example.aashimagarg.eventdistribute.R;
import com.example.aashimagarg.eventdistribute.create.PhotoAlertDialogFragment;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class TimelineActivity extends AppCompatActivity {

    private static final String TAG = "TimelineActivity";
    private static final String CURRENT_USER = "currentUser";

    FragmentTimelinePagerAdapter adapterViewPager;
    private String currentUserId;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    //start new task on log out
                    Intent intent = new Intent(TimelineActivity.this, LogInActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        };

        //set up view
        ViewPager vpPager = (ViewPager) findViewById(R.id.vp_view_pager);
        adapterViewPager = new TimelineActivity.EventsPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.ts_tabs);
        tabStrip.setViewPager(vpPager);
        currentUserId = getIntent().getStringExtra("currentUser");
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void onLogOut(){
        Log.d(TAG, "User after LogOut: " + FirebaseAuth.getInstance().getCurrentUser());
        //log out of firebase and fb and go to login activity
        FirebaseAuth.getInstance().signOut();
        //used solely to reset the automated facebook login button
        LoginManager.getInstance().logOut();
    }

    public void onCreate(View view){
        //Launch the create view
        Intent intent = new Intent(this, CreateActivity.class);
        startActivity(intent);
    }

    //return order of fragments in viewpager
    public class EventsPagerAdapter extends FragmentTimelinePagerAdapter {
        private String tabTitles[] = {"All Events", "My Events"};
        public EventsPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0){
                return newInstanceAllEventsFragment(currentUserId);
            }
            else if (position == 1){
                return newInstanceMyEventsFragment(currentUserId);
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

    //this function allows the fragment to get passed the current user
    public static AllEventsFragment newInstanceAllEventsFragment(String currentUser) {
        AllEventsFragment fragment = new AllEventsFragment();
        Bundle args = new Bundle();
        args.putString(CURRENT_USER, currentUser);
        fragment.setArguments(args);
        return fragment;
    }

    public static MyEventsFragment newInstanceMyEventsFragment(String currentUser) {
        MyEventsFragment fragment = new MyEventsFragment();
        Bundle args = new Bundle();
        args.putString(CURRENT_USER, currentUser);
        fragment.setArguments(args);
        return fragment;
    }

    public void onLogOut(MenuItem mi){
        LogoutAlertDialogFragment alertDialog = LogoutAlertDialogFragment.newInstance("Logout");
        alertDialog.show(getFragmentManager(), "string");
    }
}
