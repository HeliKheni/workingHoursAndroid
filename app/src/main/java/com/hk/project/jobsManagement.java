package com.hk.project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class jobsManagement extends AppCompatActivity {

    private RecyclerView recyclerViewJobs;
    private JobDetailsAdapter adapter;
    private List<JobDetails> jobDetailsList;
    private DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jobs_management);
        footerAllButtonClicked();
        recyclerViewJobs = findViewById(R.id.recyclerViewJobs);
        recyclerViewJobs.setLayoutManager(new LinearLayoutManager(this));

        jobDetailsList = new ArrayList<>();
        databaseHelper = new DatabaseHelper(this);

        // Initialize the adapter with the job details list
        adapter = new JobDetailsAdapter(this, jobDetailsList);
        recyclerViewJobs.setAdapter(adapter);
        }

    private void fetchJobDetailsFromDatabase() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String[] projection = {
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_JOB,
                DatabaseHelper.COLUMN_DESCRIPTION,
                DatabaseHelper.COLUMN_HOUR_RATE,
                DatabaseHelper.COLUMN_HOLIDAY_RATE,
                DatabaseHelper.COLUMN_DEFAULT_TASK
        };

        Cursor cursor = db.query(DatabaseHelper.TABLE_JOB_DETAILS, projection, null, null, null, null, null);

        while (cursor.moveToNext()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
            @SuppressLint("Range") String job = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_JOB));
            @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION));
            @SuppressLint("Range") String hourlyRate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_HOUR_RATE));
            @SuppressLint("Range") String holidayRate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_HOLIDAY_RATE));
            @SuppressLint("Range") int defaultTaskValue = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_DEFAULT_TASK));
            boolean isDefaultTask = defaultTaskValue == 1;

            JobDetails jobDetails = new JobDetails(id, job, description, hourlyRate, holidayRate, isDefaultTask);
            jobDetailsList.add(jobDetails);
        }

        cursor.close();
    }



    public void buttonPlusClicked(View view) {
        Intent intent = new Intent(this, addJob.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the data from SQLite whenever the activity is resumed
        jobDetailsList.clear(); // Clear the list before fetching new data
        fetchJobDetailsFromDatabase();
        adapter.notifyDataSetChanged();
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
