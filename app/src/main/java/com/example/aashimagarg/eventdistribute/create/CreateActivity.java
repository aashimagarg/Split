package com.example.aashimagarg.eventdistribute.create;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import com.example.aashimagarg.eventdistribute.Event;
import com.example.aashimagarg.eventdistribute.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class CreateActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final int INVITE_REQUEST_CODE = 50;
    private static final String INVITED = "invitedFriends";
    private static final int PHOTO_REQUEST_CODE = 666;
    private static final String EVENT = "event";
    private static final String EDIT = "edit";
    private static final String TAG = "Create Activity";
    private static final String EDITED_EVENT = "edited_event";

    private final Calendar startCal = Calendar.getInstance();

    private EditText etTitle;
    private EditText etStartDate;
    private EditText etStartTime;
    private EditText etLocation;
    private EditText etDescription;
    private EditText etGoal;
    private MenuItem miSubmit;
    ImageView ivCircle;
    ImageView ivCamera;
    private ArrayList<String> guests = new ArrayList<>();
    private Bitmap selectedImage;
    protected String photoUrl = "";
    private boolean edit;
    private Event editEvent;

    private TextInputLayout floatingTitleLabel;
    private TextInputLayout floatingDateLabel;
    private TextInputLayout floatingTimeLabel;
    private TextInputLayout floatingLocationLabel;
    private TextInputLayout floatingDescriptionLabel;
    private TextInputLayout floatingGoalLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        Toolbar tbToolbar = (Toolbar) findViewById(R.id.tb_toolbar);
        setSupportActionBar(tbToolbar);
        tbToolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
        setSupportActionBar(tbToolbar);
        getSupportActionBar().setTitle(R.string.new_event);

        //see if this was called because the user wants to edit their event
        edit = getIntent().getBooleanExtra(EDIT,false);
        if (edit) {
            editEvent = (Event) getIntent().getSerializableExtra(EVENT);

            //populate views with the old values
            etTitle = (EditText) findViewById(R.id.et_title);
            etTitle.setText(editEvent.getName());
            etStartDate = (EditText) findViewById(R.id.et_start_date);
            etStartDate.setText(editEvent.getStartDate());
            etStartDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerFragment newFragment = new DatePickerFragment();
                    newFragment.show(getSupportFragmentManager(), "datePicker");
                }
            });
            etStartTime = (EditText) findViewById(R.id.et_start_time);
            etStartTime.setText(editEvent.getStartTime());
            etStartTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimePickerFragment newFragment = new TimePickerFragment();
                    newFragment.show(getSupportFragmentManager(), "timePicker");
                }
            });
            etLocation = (EditText) findViewById(R.id.et_location);
            etLocation.setText(editEvent.getLocation());
            etDescription = (EditText) findViewById(R.id.et_description);
            etDescription.setText(editEvent.getDescription());

            String testing2 = etDescription.getText().toString();
            etGoal = (EditText) findViewById(R.id.et_goal);
            etGoal.setText(String.valueOf(editEvent.getAmountNeeded()));
            photoUrl = editEvent.getCoverImageUrl();
            ivCircle = (ImageView) findViewById(R.id.iv_cover_photo);
            Picasso.with(ivCircle.getContext())
                    .load(editEvent.getCoverImageUrl())
                    .transform(new RoundedCornersTransformation(2, 2))
                    .into(ivCircle);
            //set up error message
            setupFloatingLabelError();
        }
        //if this is on a normal create event flow
        else {
            //set views
            etTitle = (EditText) findViewById(R.id.et_title);
            etStartDate = (EditText) findViewById(R.id.et_start_date);
            etStartDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerFragment newFragment = new DatePickerFragment();
                    newFragment.show(getSupportFragmentManager(), "datePicker");
                }
            });
            etStartTime = (EditText) findViewById(R.id.et_start_time);
            etStartTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimePickerFragment newFragment = new TimePickerFragment();
                    newFragment.show(getSupportFragmentManager(), "timePicker");
                }
            });
            etLocation = (EditText) findViewById(R.id.et_location);
            etDescription = (EditText) findViewById(R.id.et_description);
            etGoal = (EditText) findViewById(R.id.et_goal);
            ivCircle = (ImageView) findViewById(R.id.iv_cover_photo);
            ivCamera = (ImageView) findViewById(R.id.iv_camera);
            //set up error message
            setupFloatingLabelError();
        }
    }

    public void onInvite(View view) {
        Intent intent = new Intent(CreateActivity.this, InviteActivity.class);
        startActivityForResult(intent, INVITE_REQUEST_CODE);
    }

    public void onSubmit(MenuItem mi) {
        //validation
        if (etTitle.getText().toString().equals("")) {
            floatingTitleLabel.setError(getString(R.string.must_enter_title_error));
            floatingTitleLabel.setErrorEnabled(true);
        }
        if (etStartDate.getText().toString().equals("")) {
            floatingDateLabel.setError(getString(R.string.must_enter_date_error));
            floatingDateLabel.setErrorEnabled(true);
        }
        if (etStartTime.getText().toString().equals("")) {
            floatingTimeLabel.setError(getString(R.string.must_enter_time_error));
            floatingTimeLabel.setErrorEnabled(true);
        }
        if (etLocation.getText().toString().equals("")) {
            floatingLocationLabel.setError(getString(R.string.must_enter_location_error));
            floatingLocationLabel.setErrorEnabled(true);
        }
        if (etDescription.getText().toString().equals("")) {
            floatingDescriptionLabel.setError(getString(R.string.must_enter_description_error));
            floatingDescriptionLabel.setErrorEnabled(true);
        }
        if (etGoal.getText().toString().equals("")) {
            floatingGoalLabel.setError(getString(R.string.must_enter_goal_error));
            floatingGoalLabel.setErrorEnabled(true);
        }
        if (photoUrl.equals("")) {
            if (!floatingTitleLabel.isErrorEnabled() &&
                    !floatingDateLabel.isErrorEnabled() &&
                    !floatingTimeLabel.isErrorEnabled() &&
                    !floatingLocationLabel.isErrorEnabled() &&
                    !floatingDescriptionLabel.isErrorEnabled() &&
                    !floatingGoalLabel.isErrorEnabled()){
                showAlertDialog();
            }
            ivCamera.setImageResource(R.drawable.camera_red);
            ivCircle.setImageResource(R.drawable.circle_red);
        }
        else {
            onSuccessfulSubmit();
            finish();
        }
    }

    // Trigger gallery selection for a photo
    public void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PHOTO_REQUEST_CODE);
        }
    }

    //handle date selected
    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        //Set up epoch and store set date
        startCal.set(Calendar.YEAR, year);
        startCal.set(Calendar.MONTH, monthOfYear);
        startCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        //Format date and display
        SimpleDateFormat format = new SimpleDateFormat("MM/d/yyyy");
        String startDateStr = format.format(startCal.getTime());
        etStartDate.setText(startDateStr);
    }

    //Handle time selected
    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        //Set up epoch and store set time
        startCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        startCal.set(Calendar.MINUTE, minute);
        //Format time and display
        SimpleDateFormat format = new SimpleDateFormat("h:mm a");
        String startTimeStr = format.format(startCal.getTime());
        etStartTime.setText(startTimeStr);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReferenceFromUrl("gs://eventribute.appspot.com");
        StorageReference imagesRef = storageRef.child("images/" + UUID.randomUUID().toString());

        if (requestCode == INVITE_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> addedGuests = data.getExtras().getStringArrayList(INVITED);
            for (int i = 0; i < addedGuests.size(); i++) {
                guests.add(addedGuests.get(i));
            }
        }

        if (data != null) {
            if (requestCode == PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
                final Uri photoUri = data.getData();
                miSubmit.setEnabled(false);
                // Do something with the photo based on Uri
                try {
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    selectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    imagesRef.putBytes(byteArray).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            photoUrl = taskSnapshot.getDownloadUrl().toString();
                            miSubmit.setEnabled(true);
                        }
                    });
                }
                catch (Exception e){
                    Log.e(TAG, "error in picking photo", e);
                }
                // Load the selected image into a preview
                ivCircle.setImageBitmap(getCircularBitmap(selectedImage));
                selectedImage.recycle();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create, menu);
        miSubmit = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupFloatingLabelError() {
        floatingTitleLabel = (TextInputLayout) findViewById(R.id.til_title);
        floatingDateLabel = (TextInputLayout) findViewById(R.id.til_start_date);
        floatingTimeLabel = (TextInputLayout) findViewById(R.id.til_start_time);
        floatingLocationLabel = (TextInputLayout) findViewById(R.id.til_location);
        floatingDescriptionLabel = (TextInputLayout) findViewById(R.id.til_description);
        floatingGoalLabel = (TextInputLayout) findViewById(R.id.til_goal);
        floatingTitleLabel.getEditText().addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() == 0) {
                    floatingTitleLabel.setError(getString(R.string.must_enter_title_error));
                    floatingTitleLabel.setErrorEnabled(true);
                } else {
                    floatingTitleLabel.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() == 0) {
                    floatingTitleLabel.setError(getString(R.string.must_enter_title_error));
                    floatingTitleLabel.setErrorEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        floatingDateLabel.getEditText().addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() == 0) {
                    floatingDateLabel.setError(getString(R.string.must_enter_date_error));
                    floatingDateLabel.setErrorEnabled(true);
                } else {
                    floatingDateLabel.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        floatingTimeLabel.getEditText().addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() == 0) {
                    floatingTimeLabel.setError(getString(R.string.must_enter_time_error));
                    floatingTimeLabel.setErrorEnabled(true);
                } else {
                    floatingTimeLabel.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        floatingLocationLabel.getEditText().addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() == 0) {
                    floatingLocationLabel.setError(getString(R.string.must_enter_location_error));
                    floatingLocationLabel.setErrorEnabled(true);
                } else {
                    floatingLocationLabel.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        floatingDescriptionLabel.getEditText().addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() == 0) {
                    floatingDescriptionLabel.setError(getString(R.string.must_enter_description_error));
                    floatingDescriptionLabel.setErrorEnabled(true);
                } else {
                    floatingDescriptionLabel.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        floatingGoalLabel.getEditText().addTextChangedListener(new TextWatcher() {
            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() == 0) {
                    floatingGoalLabel.setError(getString(R.string.must_enter_goal_error));
                    floatingGoalLabel.setErrorEnabled(true);
                } else {
                    floatingGoalLabel.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public long setEpoch(Calendar epochCal) {
        return epochCal.getTimeInMillis();
    }

    private void showAlertDialog() {
        FragmentManager fm = getSupportFragmentManager();
        PhotoAlertDialogFragment alertDialog = PhotoAlertDialogFragment.newInstance("No Cover Photo!");
        alertDialog.show(fm, "fragment_alert");
    }

    public void onSuccessfulSubmit() {
        //Create event
        String name = etTitle.getText().toString();
        String description = etDescription.getText().toString();
        String host = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        int amountNeeded = Integer.valueOf(etGoal.getText().toString());
        String eventId;
        long startTime;
        if (edit) {
            if (etStartDate.getText().toString().equals(editEvent.getStartDate()) || etStartTime.getText().toString().equals(editEvent.getStartTime())) {
                startTime = editEvent.getEpochTime();
            } else {
                startTime = setEpoch(startCal);
            }
            eventId = editEvent.getUid();
        } else {
            eventId = UUID.randomUUID().toString();
            startTime = setEpoch(startCal);
        }
        String location = etLocation.getText().toString();
        boolean hostContribution = false;
        String coverImageRef = photoUrl;
        Event created = new Event(eventId, name, host, hostContribution, startTime, location, description, coverImageRef, amountNeeded);

        //Create firebase key path
        Map<String, Object> updatedEventData = new HashMap<>();
        updatedEventData.put("events/" + created.getUid(), created.toMap());
        updatedEventData.put("eventsAttending/" + created.getHost() + "/" + created.getUid(), true);
        updatedEventData.put("eventMembers/" + created.getUid() + "/" + created.getHost(), true);
        for (int i = 0; i < guests.size(); i++) {
            updatedEventData.put("eventsAttending/" + guests.get(i) + "/" + created.getUid(), true);
            updatedEventData.put("eventMembers/" + created.getUid() + "/" + guests.get(i), true);
        }
        updatedEventData.put("eventsHosting/" + created.getHost() + "/" + created.getUid(), true);
        //Do a deep-path update
        FirebaseDatabase.getInstance().getReference().updateChildren(updatedEventData, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Error updating data: " + databaseError.getMessage());
                }
            }
        });
        if (edit) {
            Intent intent = new Intent();
            intent.putExtra(EDITED_EVENT, created);
            setResult(RESULT_OK, intent);
        }
    }

    //creates circular image
    public Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
}
