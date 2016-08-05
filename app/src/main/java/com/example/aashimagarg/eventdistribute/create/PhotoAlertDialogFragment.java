package com.example.aashimagarg.eventdistribute.create;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import com.example.aashimagarg.eventdistribute.R;

public class PhotoAlertDialogFragment extends DialogFragment {

    private String[] defaultPhotos = {"https://firebasestorage.googleapis.com/v0/b/eventribute.appspot.com/o/images%2Ffood.png?alt=media&token=7c31f8c4-cd49-4a8b-9116-c878840f34a5",
            "https://firebasestorage.googleapis.com/v0/b/eventribute.appspot.com/o/images%2Ffriends.png?alt=media&token=6ecf698f-91ca-469a-94a6-0e7c3c79f5ba",
            "https://firebasestorage.googleapis.com/v0/b/eventribute.appspot.com/o/images%2Fhike.png?alt=media&token=fdcf2454-cc09-479c-b7a8-fea568decad4",
            "https://firebasestorage.googleapis.com/v0/b/eventribute.appspot.com/o/images%2Fconcert.png?alt=media&token=3bf149d0-fc8a-4a04-be89-81c4798062d1",
            "https://firebasestorage.googleapis.com/v0/b/eventribute.appspot.com/o/images%2Fcupcake.png?alt=media&token=bdf7e185-7bf8-48aa-9c9b-5f0df6a1a21e"};

    public PhotoAlertDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    public static PhotoAlertDialogFragment newInstance(String title) {
        PhotoAlertDialogFragment frag = new PhotoAlertDialogFragment();
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
        alertDialogBuilder.setMessage(R.string.no_cover_photo);
        alertDialogBuilder.setPositiveButton(R.string.yes,  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //On success
                CreateActivity create = (CreateActivity) getActivity();
                create.photoUrl = defaultPhotos[(int) (Math.random()*5)];
                create.onSuccessfulSubmit();
                dialog.dismiss();
                create.finish();
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
