package com.example.aashimagarg.eventdistribute;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import com.example.aashimagarg.eventdistribute.timeline.TimelineActivity;

/**
 * Created by aashimagarg on 8/2/16.
 */
public class LogoutAlertDialogFragment extends DialogFragment {

    public LogoutAlertDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    public static LogoutAlertDialogFragment newInstance(String title) {
        LogoutAlertDialogFragment frag = new LogoutAlertDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AppAlertTheme));
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(R.string.logout_are_you_sure);
        alertDialogBuilder.setPositiveButton(R.string.yes,  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //On success
                TimelineActivity timeline = (TimelineActivity) getActivity();
                timeline.onLogOut();
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return alertDialogBuilder.create();
    }
}
