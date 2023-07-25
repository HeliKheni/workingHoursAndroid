package com.hk.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class workStart extends AppCompatActivity {


    private RecyclerView recyclerView;
    private WorkAdapter workAdapter;
    private boolean isWorking = false;
    private TextView tvDescriptionLabel, tvJobLabel, tvCurrentDate;
    private EditText etDescription;
    private Spinner spJob;
    private LinearLayout lyStopWork;
    private Button btnStop;
    private ImageView ivstart;
    private Chronometer chronometer;
    private long startTimeMillis, endTimeMillis;

    private List<String> jobList = new ArrayList<>();
    private Handler handler = new Handler();
    private Runnable updateTimerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_start);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tvDescriptionLabel = findViewById(R.id.tvDescriptionLabel);
        tvJobLabel = findViewById(R.id.tvJobLabel);
        tvCurrentDate = findViewById(R.id.tvCurrentDate);
        etDescription = findViewById(R.id.etDescription);
        spJob = findViewById(R.id.spJob);
        btnStop = findViewById(R.id.btnStop);
        chronometer = findViewById(R.id.chronometer);
        ivstart = findViewById(R.id.ivstart);
        lyStopWork = findViewById(R.id.lyStopWork);

        // Populate job list
        jobList.add("Job1");
        jobList.add("Job2");
        jobList.add("Job3");
        jobList.add("Job4");
        jobList.add("Job5");
        // Create an adapter for the job list
        ArrayAdapter<String> jobAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, jobList);
        jobAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spJob.setAdapter(jobAdapter);

        // Call the method to display the data from SQLite
        displayDataFromSQLite();


        // Start Work button click
        // Start Work button click
        // Start Work button click
        ivstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isWorking) {
                    // Show work details layout
                    lyStopWork.setVisibility(View.VISIBLE);

                    // Hide the main content layout
                    recyclerView.setVisibility(View.GONE);

                    // Start the chronometer
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.start();

                    // Show current date
                    tvCurrentDate.setText(getCurrentDate());

                    // Change Start Work icon to Stop Work icon
                    ivstart.setImageResource(R.drawable.ic_stop);
                    isWorking = true;

                    // Start updating the timer every second
                    updateTimerRunnable = new Runnable() {
                        @Override
                        public void run() {
                            updateTimer();
                            handler.postDelayed(this, 1000);
                        }
                    };
                    handler.postDelayed(updateTimerRunnable, 1000);
                }
            }
        });


        // Stop Work button click
        // Stop Work button click
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWorking) {
                    // Stop the chronometer
                    chronometer.stop();

                    // Change Stop Work icon back to Start Work icon
                    ivstart.setImageResource(R.drawable.ic_play);
                    isWorking = false;

                    // Save work data to SQLite database
                    saveWorkDataToDatabase();
                    // Hide work details layout
                    lyStopWork.setVisibility(View.GONE);

                    // Show the main content layout
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });

    }


    private void saveWorkDataToDatabase() {
        // Get the work details entered by the user
        String description = etDescription.getText().toString();
        String job = spJob.getSelectedItem().toString();
        String startTime = getCurrentTime();
        String endTime = getCurrentTime();

        // Calculate working time and format it as HH:mm:ss
        long elapsedTimeMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
        String workingTime = getFormattedTime(elapsedTimeMillis);

        // Save the work data to the SQLite database
        SQLiteDatabase database = new DatabaseHelper(this).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, description);
        values.put(DatabaseHelper.COLUMN_JOB, job);
        values.put(DatabaseHelper.COLUMN_DAY, getCurrentDate());
        values.put(DatabaseHelper.COLUMN_START_TIME, startTime);
        values.put(DatabaseHelper.COLUMN_END_TIME, endTime);
        values.put(DatabaseHelper.COLUMN_WORKING_TIME, workingTime);
        database.insert(DatabaseHelper.TABLE_NAME, null, values);
        database.close();
        displayDataFromSQLite();
    }
    public void buttonCancelClicked(View view) {
        // Clear the work details layout
        etDescription.setText("");
        spJob.setSelection(0);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.stop();
        // Hide work details layout
        lyStopWork.setVisibility(View.GONE);

        // Change Stop Work icon back to Start Work icon
        ivstart.setImageResource(R.drawable.ic_play);
        isWorking = false;

        // Show the main content layout
        recyclerView.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the data from SQLite whenever the activity is resumed
        displayDataFromSQLite();
    }
    public void buttonStartButtonClicked(View view) {
        if (isWorking) {
            // Stop the chronometer
            chronometer.stop();

            // Change Stop Work icon back to Start Work icon
            ivstart.setImageResource(R.drawable.ic_play);
            isWorking = false;

            // Save work data to SQLite database
            saveWorkDataToDatabase();

            // Hide work details layout
            lyStopWork.setVisibility(View.GONE);

            // Show the main content layout
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void updateTimer() {
        long elapsedTimeMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
        String formattedTime = getFormattedTime(elapsedTimeMillis);
        chronometer.setText(formattedTime);
    }

    private String getFormattedTime(long millis) {
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        int hours = minutes / 60;

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes % 60, seconds % 60);
    }
    private void displayDataFromSQLite() {
        // Get a readable database using the DatabaseHelper
        SQLiteDatabase database = new DatabaseHelper(this).getReadableDatabase();

        // Define the columns you want to retrieve from the database
        String[] projection = {
                DatabaseHelper.COLUMN_DESCRIPTION,
                DatabaseHelper.COLUMN_JOB,
                DatabaseHelper.COLUMN_DAY,
                DatabaseHelper.COLUMN_START_TIME,
                DatabaseHelper.COLUMN_END_TIME,
                DatabaseHelper.COLUMN_WORKING_TIME
        };

        // Define the selection criteria (if any)
        String selection = null;
        String[] selectionArgs = null;

        // Define the sort order (if any)
        String sortOrder = null;

        // Perform the query to retrieve the data
        Cursor cursor = database.query(
                DatabaseHelper.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        // Create a list to hold all the rows from the database
        List<WorkItem> workItemList = new ArrayList<>();

        // Check if the cursor is valid and move through all rows
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Retrieve the data from the cursor for each row
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION));
                String job = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_JOB));
                String day = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DAY));
                String startTime = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_START_TIME));
                String endTime = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_END_TIME));
                String workingTime = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WORKING_TIME));

                // Create a new WorkItem object to hold the data for each row
                WorkItem workItem = new WorkItem(description, job, day, startTime, endTime, workingTime);

                // Add the WorkItem object to the list
                workItemList.add(workItem);
            } while (cursor.moveToNext());

            // Close the cursor
            cursor.close();
        }

        // Close the database
        database.close();

        // Create the adapter and set it to the RecyclerView
        workAdapter = new WorkAdapter(workItemList);
        recyclerView.setAdapter(workAdapter);
    }

    public void buttonPlusClicked(View view) {
        Intent intent = new Intent(this, mannual_timeEntry.class);
        startActivity(intent);
    }


    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(Calendar.getInstance().getTime());
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sdf.format(Calendar.getInstance().getTime());
    }


}