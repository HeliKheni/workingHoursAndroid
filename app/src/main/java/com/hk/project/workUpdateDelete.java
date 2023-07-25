package com.hk.project;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class workUpdateDelete extends AppCompatActivity {


    private EditText etDescription, etDay, etStartTime, etEndTime, etWorkingTime;
    private Spinner etJob;
    private Button btnUpdate, btnDelete, btnCancel;
    private String description, job, day, startTime, endTime, workingTime;
    private DatabaseHelper databaseHelper;
    private Calendar calendar;
    private long workingTimeInMinutes = 0;
    private List<String> jobList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_update_delete);

        databaseHelper = new DatabaseHelper(this);
        // Find the views
        etDescription = findViewById(R.id.etDescription);
        etJob = findViewById(R.id.spJob);
        etDay = findViewById(R.id.etDay);
        etStartTime = findViewById(R.id.etStartTime);
        etEndTime = findViewById(R.id.etEndTime);
        etWorkingTime = findViewById(R.id.etWorkingTime);
        calendar = Calendar.getInstance();
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        btnCancel = findViewById(R.id.btnCancel);

        // Populate job list
        jobList.add("Job1");
        jobList.add("Job2");
        jobList.add("Job3");
        jobList.add("Job4");
        jobList.add("Job5");
        // Create an adapter for the job list
        ArrayAdapter<String> jobAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, jobList);
        jobAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etJob.setAdapter(jobAdapter);

        // Make the working time EditText non-editable
        etWorkingTime.setEnabled(false);
        // Get the data from the intent extras
        Intent intent = getIntent();
        if (intent != null) {
            description = intent.getStringExtra("description");
            job = intent.getStringExtra("job");
            day = intent.getStringExtra("day");
            startTime = intent.getStringExtra("startTime");
            endTime = intent.getStringExtra("endTime");
            workingTime = intent.getStringExtra("workingTime");

            // Set the data to the EditTexts
            etDescription.setText(description);
            int position = jobAdapter.getPosition(job);
            etJob.setSelection(position);

            etDay.setText(day);
            etStartTime.setText(startTime);
            etEndTime.setText(endTime);
            etWorkingTime.setText(workingTime);

        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the updated values from EditTexts
                String updatedDescription = etDescription.getText().toString();
                String updatedJob = etJob.getSelectedItem().toString();

                String updatedDay = etDay.getText().toString();
                String updatedStartTime = etStartTime.getText().toString();
                String updatedEndTime = etEndTime.getText().toString();
                String updatedWorkingTime = etWorkingTime.getText().toString();

                // Update the data in SQLite database
                updateData(updatedDescription, updatedJob, updatedDay, updatedStartTime, updatedEndTime, updatedWorkingTime);

                // Show a toast message indicating successful update
                Toast.makeText(workUpdateDelete.this, "Data updated successfully!", Toast.LENGTH_SHORT).show();

                // Go back to the previous activity
                finish();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a confirmation dialog before deleting the data
                showDeleteConfirmationDialog();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to the previous activity
                finish();
            }
        });

        //handling click event on start date
        etDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(etDay);
            }
        });

        // Click event on start time
        etStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(etStartTime);
            }
        });

        //Click event in end time
        etEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(etEndTime);
            }
        });

        etStartTime.setOnFocusChangeListener(onFocusChangeListener);

        // Add OnFocusChangeListener for end time EditText
        etEndTime.setOnFocusChangeListener(onFocusChangeListener);
    }

    private View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                // Calculate and update the working time whenever the focus is lost from the start time or end time EditText
                updateWorkingTime();
            }
        }
    };

    //showing date picker
    private void showDatePickerDialog(final EditText editText) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateDateField(editText);
                    }
                }, year, month, dayOfMonth);

        datePickerDialog.show();
    }

    //Showing time picker
    private void showTimePickerDialog(final EditText editText) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        updateTimeField(editText);

                        // Calculate and update the working time whenever a new time is selected
                        if (editText == etStartTime) {
                            updateWorkingTime();
                        } else if (editText == etEndTime) {
                            updateWorkingTime();
                        }
                    }
                }, hour, minute, false);
        timePickerDialog.show();
    }

    private void updateWorkingTime() {
        // Get the start and end times from the EditText fields
        String startTime = etStartTime.getText().toString();
        String endTime = etEndTime.getText().toString();

        try {
            // Parse the start and end times into Calendar objects
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            Calendar startTimeCalendar = Calendar.getInstance();
            startTimeCalendar.setTime(sdf.parse(startTime));
            Calendar endTimeCalendar = Calendar.getInstance();
            endTimeCalendar.setTime(sdf.parse(endTime));

            // Check if the end time is before the start time (spanning across two days)
            boolean isNextDay = endTimeCalendar.before(startTimeCalendar);

            if (isNextDay) {
                // Add one day to the end time
                endTimeCalendar.add(Calendar.DATE, 1);
            }

            // Calculate the time difference between start and end times in minutes
            long timeDifferenceInMillis = endTimeCalendar.getTimeInMillis() - startTimeCalendar.getTimeInMillis();
            workingTimeInMinutes = timeDifferenceInMillis / (60 * 1000);

            // Format the working time as "HH:mm" (hours:minutes)
            String workingTime = String.format(Locale.getDefault(), "%02d:%02d",
                    workingTimeInMinutes / 60, workingTimeInMinutes % 60);

            // Set the calculated working time to the etWorkingTime EditText
            etWorkingTime.setText(workingTime);

            // Format the end time to show only the hours and minutes
            SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String endTimeWithTimeOnly = sdfTime.format(endTimeCalendar.getTime());

            // Set the end time with only the hours and minutes to the etEndWork EditText
            etEndTime.setText(endTimeWithTimeOnly);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private void updateDateField(EditText editText) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        String selectedDate = sdf.format(calendar.getTime());
        editText.setText(selectedDate);
    }

    private void updateTimeField(EditText editText) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String selectedTime = sdf.format(calendar.getTime());
        editText.setText(selectedTime);
    }
        private void updateData(String updatedDescription, String updatedJob, String updatedDay, String updatedStartTime, String updatedEndTime, String updatedWorkingTime) {
            // Get a writable database using the DatabaseHelper
            SQLiteDatabase database = databaseHelper.getWritableDatabase();

            // Create a ContentValues object to store the updated data
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_DESCRIPTION, updatedDescription);
            values.put(DatabaseHelper.COLUMN_JOB, updatedJob);
            values.put(DatabaseHelper.COLUMN_DAY, updatedDay);
            values.put(DatabaseHelper.COLUMN_START_TIME, updatedStartTime);
            values.put(DatabaseHelper.COLUMN_END_TIME, updatedEndTime);
            values.put(DatabaseHelper.COLUMN_WORKING_TIME, updatedWorkingTime);

            // Define the WHERE clause to update the specific row based on the original values
            String selection = DatabaseHelper.COLUMN_DESCRIPTION + " = ? AND "
                    + DatabaseHelper.COLUMN_JOB + " = ? AND "
                    + DatabaseHelper.COLUMN_DAY + " = ? AND "
                    + DatabaseHelper.COLUMN_START_TIME + " = ? AND "
                    + DatabaseHelper.COLUMN_END_TIME + " = ? AND "
                    + DatabaseHelper.COLUMN_WORKING_TIME + " = ?";

            // Define the selection arguments based on the original values
            String[] selectionArgs = {
                    description, job, day, startTime, endTime, workingTime
            };

            // Update the data in the database
            database.update(DatabaseHelper.TABLE_NAME, values, selection, selectionArgs);

            // Close the database
            database.close();
        }

        private void showDeleteConfirmationDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirm Delete");
            builder.setMessage("Are you sure you want to delete this data?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Delete the data from the SQLite database
                    deleteData();

                    // Show a toast message indicating successful deletion
                    Toast.makeText(workUpdateDelete.this, "Data deleted successfully!", Toast.LENGTH_SHORT).show();

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

            // Define the WHERE clause to delete the specific row based on the original values
            String selection = DatabaseHelper.COLUMN_DESCRIPTION + " = ? AND "
                    + DatabaseHelper.COLUMN_JOB + " = ? AND "
                    + DatabaseHelper.COLUMN_DAY + " = ? AND "
                    + DatabaseHelper.COLUMN_START_TIME + " = ? AND "
                    + DatabaseHelper.COLUMN_END_TIME + " = ? AND "
                    + DatabaseHelper.COLUMN_WORKING_TIME + " = ?";

            // Define the selection arguments based on the original values
            String[] selectionArgs = {
                    description, job, day, startTime, endTime, workingTime
            };

            // Delete the data from the database
            database.delete(DatabaseHelper.TABLE_NAME, selection, selectionArgs);

            // Close the database
            database.close();
        }


}