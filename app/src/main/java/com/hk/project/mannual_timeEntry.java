package com.hk.project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;



public class mannual_timeEntry extends AppCompatActivity {

    private EditText etDescription, etDay, etStartWork, etEndWork;
    private TextView tvWorkingTime;
    private Button btnSave, btnCancel;
    private Calendar calendar;
    private Spinner spJob;
    private long workingTimeInMinutes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mannual_time_entry);

        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        etDay = findViewById(R.id.etDay);
        etStartWork= findViewById(R.id.etStartWork);
        etEndWork = findViewById(R.id.etEndWork);
        calendar = Calendar.getInstance();

        etDescription = findViewById(R.id.etDescription);
        spJob = findViewById(R.id.spJob);

        // Find the EditText field for working time
        tvWorkingTime = findViewById(R.id.tvDisplayWorkingTime);

        // Make the working time EditText non-editable
        tvWorkingTime.setEnabled(false);

        // Temporary fixed data for the job dropdown
       // String[] jobOptions = {"Job 1", "Job 2", "Job 3", "Job 4", "Job 5"};

        // Find the Spinner for the job dropdown
        spJob= findViewById(R.id.spJob);
        // Access the defaultJob variable from AppData

       // String defaultJob = AppData.getInstance().getDefaultJob();
       // Log.d("MannualTimeEntry", "Default Job Title: " + defaultJob);

        // Create an ArrayAdapter using the job titles from the database and set it to the Spinner
        List<String> jobTitles = DatabaseHelper.getInstance(this).getAllJobTitles();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, jobTitles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spJob.setAdapter(adapter);

        // Set the default job title as the selected item in the Spinner
      /*  if (defaultJob != null && !defaultJob.isEmpty()) {

            int position = adapter.getPosition(defaultJob);
            Log.d("MannualTimeEntry", "Position to Set: " + position);
            spJob.setSelection(position);

        }*/

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertDataIntoDatabase();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        etStartWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(etStartWork);
            }
        });

        //Click event in end time
        etEndWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(etEndWork);
            }
        });

        // Calculate and set the initial working time
        updateWorkingTime();
    }
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
                        if (editText == etStartWork) {
                            updateWorkingTime();
                        } else if (editText == etEndWork) {
                            updateWorkingTime();
                        }
                    }
                }, hour, minute, false);
        timePickerDialog.show();
    }

    private void updateWorkingTime() {
        // Get the start and end times from the EditText fields
        String startTime = etStartWork.getText().toString();
        String endTime = etEndWork.getText().toString();

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
            tvWorkingTime.setText(workingTime);

            // Format the end time to show only the hours and minutes
            SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            String endTimeWithTimeOnly = sdfTime.format(endTimeCalendar.getTime());

            // Set the end time with only the hours and minutes to the etEndWork EditText
            etEndWork.setText(endTimeWithTimeOnly);
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
    private void insertDataIntoDatabase() {
        try {
            String description = etDescription.getText().toString();
            String job = spJob.getSelectedItem().toString();
            String day = etDay.getText().toString();
            String startTime = etStartWork.getText().toString();
            String endTime = etEndWork.getText().toString();
            String workingTime = tvWorkingTime.getText().toString();

            // Get a writable database using the DatabaseHelper
            SQLiteDatabase database = new DatabaseHelper(this).getWritableDatabase();

            // Create a ContentValues object to store the data to be inserted
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_DESCRIPTION, description);
            values.put(DatabaseHelper.COLUMN_JOB, job);
            values.put(DatabaseHelper.COLUMN_DAY, day);
            values.put(DatabaseHelper.COLUMN_START_TIME, startTime);
            values.put(DatabaseHelper.COLUMN_END_TIME, endTime);
            values.put(DatabaseHelper.COLUMN_WORKING_TIME, workingTime);

            // Insert the data into the table
            long newRowId = database.insert(DatabaseHelper.TABLE_TIME_ENTRIES, null, values);

            // Check if the insertion was successful
            if (newRowId != -1) {
                Toast.makeText(this, "Data inserted successfully!", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "Failed to insert data.", Toast.LENGTH_SHORT).show();
            }

            // Close the database
            database.close();
            // Finish the current activity to go back to the previous activity
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
