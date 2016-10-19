package com.example.aashimagarg.eventdistribute;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.aashimagarg.eventdistribute.timeline.TimelineActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class LogInActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final String EMAIL = "email";
    private static final String FRIENDS = "user_friends";
    private static final String PROFILE = "public_profile";

    private CallbackManager callbackManager;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            handleFacebookAccessToken(accessToken);
        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onError(FacebookException exception) {
            Toast.makeText(getApplicationContext(), getString(R.string.error) + exception.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "I got an error", exception);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_log_in);
        //set views
        LoginButton lbLogin = (LoginButton) findViewById(R.id.lb_login);
        ImageView ivBackground = (ImageView) findViewById(R.id.iv_background);
        TextView tvTagLine = (TextView) findViewById(R.id.tv_tagline);
        //set font
        tvTagLine.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Montserrat_Regular.ttf"));
        //upload photo and add black tint
        ivBackground.setImageResource(R.drawable.background_cupcake);
        ivBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ivBackground.setColorFilter(Color.argb(120, 0, 0, 0));
        //removes facebook automated pop in
        lbLogin.setToolTipMode(LoginButton.ToolTipMode.NEVER_DISPLAY);
        lbLogin.setReadPermissions(EMAIL, FRIENDS, PROFILE);
        lbLogin.registerCallback(callbackManager, callback);
        //set authentication listener
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    //User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + firebaseUser.getUid());
                    Intent intent = new Intent(LogInActivity.this, TimelineActivity.class);
                    intent.putExtra("currentUser", firebaseUser.getUid());
                    startActivity(intent);
                    finish();
                }
                else{
                    //User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
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

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                FirebaseUser fireUser = task.getResult().getUser();
                AccessToken accessToken = AccessToken.getCurrentAccessToken();

                if (fireUser != null) {
                    String uid = fireUser.getUid();
                    String name = fireUser.getDisplayName();
                    String photoURL = fireUser.getPhotoUrl().toString();
                    String fbId = accessToken.getUserId();
                    User user = new User(uid, name, photoURL, fbId);
                    //Adding user info and userMap to database
                    Map<String, Object> updatedUserData = new HashMap<>();
                    updatedUserData.put("users/" + fireUser.getUid(), user.toMap());
                    updatedUserData.put("userMap/" + user.getFbId(), user.getUid());
                    //deep path update
                    FirebaseDatabase.getInstance().getReference().updateChildren(updatedUserData, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                System.out.println("Error updating data: " + databaseError.getMessage());
                            }
                        }
                    });
                }
                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Log.w(TAG, "signInWithCredential", task.getException());
                    Toast.makeText(LogInActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
