package com.example.aashimagarg.eventdistribute;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class MemberPaymentStatusFragment extends Fragment {

    /* package */ PaymentStatusListAdapter paymentStatusListAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        paymentStatusListAdapter = new PaymentStatusListAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member_payment_status, parent, false);
        ListView lvEvents = (ListView) view.findViewById(R.id.lv_paid_statuses);
        lvEvents.setAdapter(paymentStatusListAdapter);
        return view;
    }
}
