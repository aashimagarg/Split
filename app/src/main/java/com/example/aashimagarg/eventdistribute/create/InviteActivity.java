package com.example.aashimagarg.eventdistribute.create;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aashimagarg.eventdistribute.R;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InviteActivity extends AppCompatActivity {

    private static final String TAG = "InviteActivity";
    private static final String DATA = "data";
    private static final String FIELDS = "fields";
    private static final String PARAMS = "id,name,picture.type(large)";
    private static final String INVITED = "invitedFriends";

    private TextView numInvites;
    private TextView selectAll;
    private RecyclerView rvFriends;
    private FriendsAdapter adapter;

    private ArrayList<String> firebaseIds = new ArrayList<>();
    private ArrayList<String> invitedFriends = new ArrayList<>();
    private ArrayList<Friend> friends;
    private DatabaseReference userDataRef;
    private ValueEventListener userIdDatabaseListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        Toolbar tbToolbar = (Toolbar) findViewById(R.id.tb_toolbar);
        setSupportActionBar(tbToolbar);
        tbToolbar.setNavigationIcon(R.drawable.ic_clear_white_24dp);
        setSupportActionBar(tbToolbar);
        getSupportActionBar().setTitle(R.string.select_friends);
        //setting views
        numInvites = (TextView) findViewById(R.id.tv_invite_count);
        selectAll = (TextView) findViewById(R.id.tv_select_all);
        rvFriends = (RecyclerView) findViewById(R.id.rvFriends);
        adapter = new FriendsAdapter(numInvites, selectAll);
        rvFriends.setAdapter(adapter);
        rvFriends.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        //network call
        GraphRequest request = GraphRequest.newMyFriendsRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(JSONArray array, GraphResponse response) {
                        JSONArray jsonArray = response.getJSONObject().optJSONArray(DATA);
                        if (jsonArray.length() != 0) {
                            friends = Friend.fromJSONArray(jsonArray);
                            //get firebase user id
                            ArrayList<String> facebookIds = new ArrayList<>();
                            for (int i = 0; i < friends.size(); i++) {
                                facebookIds.add(friends.get(i).getFbId());
                            }
                            //get facebook Ids, store to firebase, update adapter
                            facebookToFirebase(facebookIds, friends, 0);
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.no_friends), Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
        Bundle parameters = new Bundle();
        parameters.putString(FIELDS, PARAMS);
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_invite, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onSave(MenuItem mi){
        facebookToFirebase(adapter.getSelectedFriends(), null, 1);
    }

    public void facebookToFirebase(final ArrayList<String> facebookIds, final ArrayList<Friend> friends, final int caseValue) {
        userDataRef = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://eventribute.firebaseio.com/userMap");
        userIdDatabaseListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (caseValue == 0) {
                    for (int i = 0; i < facebookIds.size(); i++) {
                        //query for friends
                        firebaseIds.add(dataSnapshot.child(facebookIds.get(i)).getValue().toString());
                    }
                    //create firebaseId map for friends
                    Map<String, Boolean> friendFirebaseIds = new HashMap<>();
                    for (int i = 0; i < firebaseIds.size(); i++) {
                        friendFirebaseIds.put(firebaseIds.get(i), true);
                    }
                    //store friends
                    DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid().toString() + "/friends");
                    dataRef.setValue(friendFirebaseIds);
                    //populate view
                    adapter.addAll(friends);
                    adapter.notifyDataSetChanged();
                }
                else if (caseValue == 1) {
                    for (int i = 0; i < facebookIds.size(); i++) {
                        //query for friends
                        invitedFriends.add(dataSnapshot.child(facebookIds.get(i)).getValue().toString());
                    }
                    Intent intent = new Intent();
                    intent.putExtra(INVITED, invitedFriends);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        userDataRef.addListenerForSingleValueEvent(userIdDatabaseListener);
    }

    @Override
    protected void onDestroy() {
        if (userIdDatabaseListener != null) {
            userDataRef.removeEventListener(userIdDatabaseListener);
        }
        super.onDestroy();
    }

    public void onSelectAll(View view) {
        if (selectAll.getText().equals("Select All")) {
            for (int i = 0; i < friends.size(); i++) {
                friends.get(i).invited = true;
                adapter.notifyDataSetChanged();
            }
            selectAll.setText(R.string.unselect_all);
        } else {
            for (int i = 0; i < friends.size(); i++) {
                friends.get(i).invited = false;
                adapter.notifyDataSetChanged();
            }
            selectAll.setText(R.string.select_all);
        }
    }
}
