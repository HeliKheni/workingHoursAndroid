package com.hk.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class addJob extends AppCompatActivity {

    private EditText jobTitleEditText;
    private EditText detailsEditText;
    private EditText hourlyRateEditText;
    private EditText holidayRateEditText;
    private Switch defaultTaskSwitch;
    private Button btnSave;
    private Button btnCancel;
    public DatabaseHelper dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addjob);

        // Initialize views
        jobTitleEditText = findViewById(R.id.jobTitleEditText);
        detailsEditText = findViewById(R.id.detailsEditText);
        hourlyRateEditText = findViewById(R.id.hourlyRateEditText);
        holidayRateEditText = findViewById(R.id.holidayRateEditText);
        defaultTaskSwitch = findViewById(R.id.defaultTaskSwitch);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        // Set click listeners for buttons
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveJobDetails();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an explicit intent to navigate to the "Job Management" activity
                Intent intent = new Intent(addJob.this, jobsManagement.class);
                startActivity(intent);

            }
        });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the database connection when the activity is destroyed
        dataSource.close();
    }

    private void saveJobDetails() {
        // Get the data entered by the user
        String jobTitle = jobTitleEditText.getText().toString().trim();
        String details = detailsEditText.getText().toString().trim();
        String hourlyRate = hourlyRateEditText.getText().toString().trim();
        String holidayRate = holidayRateEditText.getText().toString().trim();
        boolean isDefaultTask = defaultTaskSwitch.isChecked();

        // Get a writable database using the DatabaseHelper singleton instance
        SQLiteDatabase database = DatabaseHelper.getInstance(this).getWritableDatabase();

        // If the new job is set as default, update all other jobs to false
        if (isDefaultTask) {
            ContentValues defaultTaskValues = new ContentValues();
            defaultTaskValues.put(DatabaseHelper.COLUMN_DEFAULT_TASK, 0); // Set default_task to false (0)

            // Update all rows in the table to set default_task to false
            int rowsAffected = database.update(
                    DatabaseHelper.TABLE_JOB_DETAILS,
                    defaultTaskValues,
                    DatabaseHelper.COLUMN_DEFAULT_TASK + " = ?",
                    new String[]{String.valueOf(1)} // 1 represents true (default task)
            );

            // Check if any rows were affected by the update
            if (rowsAffected > 0) {
                // Toast to indicate that other jobs have been updated to false
                Toast.makeText(this, "All other jobs set to non-default and this to default.", Toast.LENGTH_SHORT).show();
            }
        }
        // Create a ContentValues object to store the data to be inserted
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_JOB, jobTitle);
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, details);
        values.put(DatabaseHelper.COLUMN_HOUR_RATE, hourlyRate);
        values.put(DatabaseHelper.COLUMN_HOLIDAY_RATE, holidayRate);
        values.put(DatabaseHelper.COLUMN_DEFAULT_TASK, isDefaultTask);

        // Insert the data into the table
        long newRowId = database.insert(DatabaseHelper.TABLE_JOB_DETAILS, null, values);

        // Check if the insertion was successful
        if (newRowId != -1) {
            Toast.makeText(this, "Job Data inserted successfully!", Toast.LENGTH_SHORT).show();

            // Check if the job is added or updated as default
            if (isDefaultTask) {

            }
        } else {
            Toast.makeText(this, "Failed to insert data.", Toast.LENGTH_SHORT).show();
        }
        // Close the database
        database.close();
        // Create an explicit intent to navigate to the "Job Management" activity
        Intent intent = new Intent(this, jobsManagement.class);
        startActivity(intent);

    }
}
