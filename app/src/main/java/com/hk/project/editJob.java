package com.hk.project;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class editJob extends AppCompatActivity {

    private EditText jobTitleEditText;
    private EditText detailsEditText;
    private EditText hourlyRateEditText;
    private EditText holidayRateEditText;

    private Switch defaultSwitch;

    private DatabaseHelper databaseHelper;
    private int jobId;
    private JobDetails jobDetails;
    private Button btnUpdate;
    private Button btnDelete;
    private Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_job_dialog);

        databaseHelper = DatabaseHelper.getInstance(this);

        // Retrieve the job ID from the Intent
        jobId = getIntent().getIntExtra("jobId", -1);

        // Load the job details from the database based on the job ID
        jobDetails = databaseHelper.getJobDetails(jobId);

        // Initialize views
        jobTitleEditText = findViewById(R.id.jobTitleEditText);
        detailsEditText = findViewById(R.id.detailsEditText);
        hourlyRateEditText = findViewById(R.id.hourlyRateEditText);
        holidayRateEditText = findViewById(R.id.holidayRateEditText);
        defaultSwitch = findViewById(R.id.defaultTaskSwitch);

        // Populate the EditText fields with the respective job details
        if (jobDetails != null) {
            jobTitleEditText.setText(jobDetails.getJob());
            detailsEditText.setText(jobDetails.getDescription());
            hourlyRateEditText.setText(String.valueOf(jobDetails.getHourlyRate()));
            holidayRateEditText.setText(String.valueOf(jobDetails.getHolidayRate()));
            // Set the default switch according to the jobDetails
            defaultSwitch.setChecked(jobDetails.isDefaultTask());
        }

        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        btnCancel = findViewById(R.id.btnCancel);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the Update button click
                updateJob();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the Delete button click
                deleteJob();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the Cancel button click
                cancelUpdate();
            }
        });

    }

    private void updateJob() {
        // Get the updated values from EditTexts
        String updatedJobTitle = jobTitleEditText.getText().toString().trim();
        String updatedDetails = detailsEditText.getText().toString().trim();
        String updatedHourlyRate = hourlyRateEditText.getText().toString().trim();
        String updatedHolidayRate = holidayRateEditText.getText().toString().trim();
        boolean updatedDefaultTask = defaultSwitch.isChecked();

        // Update the data in SQLite database
        updateData(updatedJobTitle, updatedDetails, updatedHourlyRate, updatedHolidayRate, updatedDefaultTask);

        // Show a toast message indicating successful update
        Toast.makeText(this, "Job details updated successfully!", Toast.LENGTH_SHORT).show();

        // Go back to the previous activity
        finish();
    }

    private void updateData(String updatedJobTitle, String updatedDetails, String updatedHourlyRate,
                            String updatedHolidayRate, boolean updatedDefaultTask) {
        // Get a writable database using the DatabaseHelper
        SQLiteDatabase database = databaseHelper.getWritableDatabase();


        // If the new job is set as default, update all other jobs to false
        if (updatedDefaultTask) {
            ContentValues defaultTaskValues = new ContentValues();
            defaultTaskValues.put(DatabaseHelper.COLUMN_DEFAULT_TASK, 0); // Set default_task to false (0)

            // Update all rows in the table to set default_
            // task to false
            int rowsAffected = database.update(
                    DatabaseHelper.TABLE_JOB_DETAILS,
                    defaultTaskValues,
                    DatabaseHelper.COLUMN_DEFAULT_TASK + " = ?",
                    new String[]{String.valueOf(1)} // 1 represents true (default task)
            );

            // Check if any rows were affected by the update
            if (rowsAffected > 0) {
                // Toast to indicate that other jobs have been updated to false
                //Toast.makeText(this, "All other jobs set to non-default.", Toast.LENGTH_SHORT).show();
            }
        }
        // Create a ContentValues object to store the updated data
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_JOB, updatedJobTitle);
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, updatedDetails);
        values.put(DatabaseHelper.COLUMN_HOUR_RATE, updatedHourlyRate);
        values.put(DatabaseHelper.COLUMN_HOLIDAY_RATE, updatedHolidayRate);
        values.put(DatabaseHelper.COLUMN_DEFAULT_TASK, updatedDefaultTask ? 1 : 0);

        // Check if the job is added or updated as default
        if (updatedDefaultTask) {
            // Set the defaultJob variable with the default job title using AppData
           // AppData.getInstance().setDefaultJob(updatedJobTitle);
        }
        // Define the WHERE clause to update the specific row based on the job ID
        String selection = DatabaseHelper.COLUMN_ID + " = ?";

        // Define the selection arguments based on the job ID
        String[] selectionArgs = {String.valueOf(jobId)};

        // Update the data in the database
        database.update(DatabaseHelper.TABLE_JOB_DETAILS, values, selection, selectionArgs);


        // Close the database
        database.close();
    }
    private void deleteJob() {
        // Show a confirmation dialog before deleting the job
        showDeleteConfirmationDialog();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Delete");
        builder.setMessage("Are you sure you want to delete this job?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Delete the job from the SQLite database
                deleteData();

                // Show a toast message indicating successful deletion
                Toast.makeText(editJob.this, "Job deleted successfully!", Toast.LENGTH_SHORT).show();

                // Go back to the previous activity
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog and do nothing
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteData() {
        // Get a writable database using the DatabaseHelper
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        // Define the WHERE clause to delete the specific row based on the job ID
        String selection = DatabaseHelper.COLUMN_ID + " = ?";

        // Define the selection arguments based on the job ID
        String[] selectionArgs = {String.valueOf(jobId)};

        // Delete the job from the database
        database.delete(DatabaseHelper.TABLE_JOB_DETAILS, selection, selectionArgs);

        // Close the database
        database.close();
    }


    private void cancelUpdate() {
        // Handle the Cancel button click
        // Finish the activity and return to the previous screen without making any changes
        finish();
    }
}
