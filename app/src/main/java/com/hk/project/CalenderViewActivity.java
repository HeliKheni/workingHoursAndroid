package com.hk.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class CalenderViewActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_view);
        footerAllButtonClicked();
    }
    private void footerAllButtonClicked() {
        // Find the FooterBar views and set click listeners
        int[] buttonIds = {R.id.jobs, R.id.home, R.id.analytics, R.id.cal};
        for (int id : buttonIds) {
            View button = findViewById(id);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleButtonClick(v.getId());
                }
            });
        }
    }
    private void handleButtonClick(int viewId) {
        if (viewId == R.id.jobs) {
            startActivity(jobsManagement.class);
        } else if (viewId == R.id.home) {
            startActivity(workStart.class);
        } else if (viewId == R.id.analytics) {
            startActivity(Reports.class);
        } else if (viewId == R.id.cal) {
            startActivity(CalenderViewActivity.class);
        }
    }
    private void startActivity(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }
}
