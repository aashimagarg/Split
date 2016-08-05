package com.example.aashimagarg.eventdistribute;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class VenmoActivity extends AppCompatActivity {

    private static final String HOST = "host";
    private static final String CONTRIBUTION = "contribution";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venmo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        EditText etVenmoHost = (EditText) findViewById(R.id.et_venmo_host);
        etVenmoHost.setHint(getIntent().getStringExtra(HOST));
        EditText etVenmoContribution = (EditText) findViewById(R.id.et_venmo_contribution);
        etVenmoContribution.setText("$");
        etVenmoContribution.setSelection(1);
    }

    public void onVenmoContribute(View view) {
        EditText etVenmoContribution = (EditText) findViewById(R.id.et_venmo_contribution);
        String value = etVenmoContribution.getText().toString().substring(1);
        int contribution = Integer.valueOf(value);
        Intent intent = new Intent();
        intent.putExtra(CONTRIBUTION, contribution);
        setResult(RESULT_OK, intent);
        finish();
    }
}
