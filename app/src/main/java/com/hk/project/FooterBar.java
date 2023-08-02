package com.hk.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class FooterBar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.footer_bar);
        Log.d("FooterBar", "onCreate() method called.");
        // Find the ImageButton with id 'jobs'
        ImageButton jobsButton = findViewById(R.id.jobs);

        // Set the click listener for the jobsButton
        jobsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the buttonJobClicked method when the button is clicked
                buttonJobClicked(v);
            }
        });
    }

    public void buttonHomeClicked(View view) {
        Toast.makeText(this, "home clicked", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, workStart.class);
        startActivity(intent);
    }

    public void buttonJobClicked(View view) {
        Log.d("FooterBar", "buttonJobClicked method called");
        Toast.makeText(this, "job clicked", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, jobsManagement.class);
        startActivity(intent);
    }
    public void buttonSettingsClicked(View view) {
        Toast.makeText(this, "setting clicked", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, settings.class);
        startActivity(intent);
    }
    public void buttonReportsClicked(View view) {
        Intent intent = new Intent(this, Reports.class);
        startActivity(intent);
    }
    public void buttonCalenderClicked(View view) {
        Intent intent = new Intent(this, CalenderViewActivity.class);
        startActivity(intent);
    }
}