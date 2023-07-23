package com.hk.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class workStart extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_start);
    }

    public void buttonStartWorkClicked(View view) {
        Intent intent = new Intent(this, mannual_timeEntry.class);
        startActivity(intent);
    }
}