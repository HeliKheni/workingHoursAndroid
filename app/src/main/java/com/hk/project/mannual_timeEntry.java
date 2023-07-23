package com.hk.project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
        String[] jobOptions = {"Job 1", "Job 2", "Job 3", "Job 4", "Job 5"};

        // Find the Spinner for the job dropdown
        spJob= findViewById(R.id.spJob);

        // Create an ArrayAdapter using the temporary fixed data and set it to the Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, jobOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spJob.setAdapter(adapter);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    insertDataIntoDatabase();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the Cancel button action
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

            // Calculate the time difference between start and end times in minutes
            long timeDifferenceInMillis = endTimeCalendar.getTimeInMillis() - startTimeCalendar.getTimeInMillis();
            workingTimeInMinutes = timeDifferenceInMillis / (60 * 1000);

            // Format the working time as "HH:mm" (hours:minutes)
            String workingTime = String.format(Locale.getDefault(), "%02d:%02d",
                    workingTimeInMinutes / 60, workingTimeInMinutes % 60);

            // Set the calculated working time to the etWorkingTime EditText
            tvWorkingTime.setText(workingTime);
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
    private void insertDataIntoDatabase() throws ParseException {
        Toast.makeText(mannual_timeEntry.this, "Inside the function!", Toast.LENGTH_SHORT).show();

        String description = etDescription.getText().toString();
        String job = spJob.getSelectedItem().toString();

        String day = etDay.getText().toString();
        String startTime = etStartWork.getText().toString();
        String endTime = etEndWork.getText().toString();
        String workingTime = tvWorkingTime.getText().toString();

        Log.d("DEBUG", "Description: " + description);
        Log.d("DEBUG", "Job: " + job);
        Log.d("DEBUG", "Day: " + day);
        Log.d("DEBUG", "StartTime: " + startTime);
        Log.d("DEBUG", "EndTime: " + endTime);
        Log.d("DEBUG", "WorkingTime: " + workingTime);



    }

}
