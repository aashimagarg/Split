package com.example.aashimagarg.eventdistribute;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.astuetz.PagerSlidingTabStrip;

public class MembersPaymentStatusActivity extends AppCompatActivity {

    private static final String CURRENT_EVENT = "currentEvent";

    FragmentTimelinePagerAdapter adapterViewPager;
    private String currentEventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members_payment_status);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ViewPager vpPager = (ViewPager) findViewById(R.id.vp_view_pager);
        adapterViewPager = new MembersPaymentStatusActivity.PaymentsPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.ts_tabs);
        tabStrip.setViewPager(vpPager);
        currentEventId = getIntent().getStringExtra(CURRENT_EVENT);
    }

    //return order of fragments in viewpager
    public class PaymentsPagerAdapter extends FragmentTimelinePagerAdapter {
        private String tabTitles[] = {"Not Paid", "Paid"};
        public PaymentsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return newInstanceNotPaidFragment(currentEventId);
            }
            else if (position == 1) {
                return newInstancePaidFragment(currentEventId);
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

    public static NotPaidFragment newInstanceNotPaidFragment(String currentEventId) {
        NotPaidFragment fragment = new NotPaidFragment();
        Bundle args = new Bundle();
        args.putString(CURRENT_EVENT, currentEventId);
        fragment.setArguments(args);
        return fragment;
    }

    public static PaidFragment newInstancePaidFragment(String currentEventId) {
        PaidFragment fragment = new PaidFragment();
        Bundle args = new Bundle();
        args.putString(CURRENT_EVENT, currentEventId);
        fragment.setArguments(args);
        return fragment;
    }
}
