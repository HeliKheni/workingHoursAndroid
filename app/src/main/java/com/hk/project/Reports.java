package com.hk.project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Reports extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private Spinner jobDropdown;
    private EditText editTextFromDate;
    private EditText editTextToDate;
    private RadioGroup radioGroupType;
    private RadioButton radioButtonWorkingTime;
    private RadioButton radioButtonEarnings;
    private TextView textViewTotal;
private TextView imageViewDetails;


    // Define the array of predefined timespan options
    private final String[] timespanOptions = {
            "Today", "Yesterday", "Last Month", "Last Year", "6 Months Ago"
    };

    // Calendar instance to manage dates
    private Calendar calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        footerAllButtonClicked();
        databaseHelper = DatabaseHelper.getInstance(this);
        jobDropdown = findViewById(R.id.jobDropdown);

        editTextFromDate = findViewById(R.id.editTextFromDate);
        editTextToDate = findViewById(R.id.editTextToDate);
        radioGroupType = findViewById(R.id.radioGroupType);
        radioButtonWorkingTime = findViewById(R.id.radioButtonWorkingTime);
        radioButtonEarnings = findViewById(R.id.radioButtonEarnings);
        textViewTotal = findViewById(R.id.textViewTotal);
        // Initialize the ImageView
        imageViewDetails = findViewById(R.id.imageViewGraph);
        // Populate the job dropdown with job titles from the database
        populateJobDropdown();

        // Initialize the Calendar instance
        calendar = Calendar.getInstance();

        // Add a listener to the fromDate EditText to show date picker
        editTextFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(editTextFromDate);
            }
        });

        // Add a listener to the toDate EditText to show date picker
        editTextToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(editTextToDate);
            }
        });



    }
    public void onCalculateButtonClick(View view) {
        updateTotal();
    }
    // Method to show the date picker dialog
    private void showDatePicker(final EditText editText) {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateDateText(editText);

            }
        };

        // Create the date picker dialog with the current date
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                Reports.this, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        // Show the date picker dialog
        datePickerDialog.show();
    }

    // Method to update the date text in EditText
    private void updateDateText(EditText editText) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        editText.setText(sdf.format(calendar.getTime()));
    }
    // Method to populate the job dropdown with job titles from the database
    private void populateJobDropdown() {
        List<String> jobTitles = databaseHelper.getAllJobTitles();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, jobTitles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jobDropdown.setAdapter(adapter);
    }


    private void updateTotal() {
        String selectedJob = (String) jobDropdown.getSelectedItem();
        String fromDate = editTextFromDate.getText().toString();
        String toDate = editTextToDate.getText().toString();
        int selectedRadioId = radioGroupType.getCheckedRadioButtonId();
        boolean isWorkingTimeSelected = selectedRadioId == R.id.radioButtonWorkingTime;

        if (selectedJob == null) {
            // If either job or timespan is not selected, show 0.00 as total
            textViewTotal.setText("Total: $0.00");
            imageViewDetails.setText("");
            return;
        }

        // Get the job ID based on the selected job title
        int jobId = databaseHelper.findJobIdFromTitle(selectedJob);

        // Retrieve hourly rate for the selected job from the database using the job ID
        JobDetails jobDetails = databaseHelper.getJobDetails(jobId);
        if (jobDetails == null) {
            // If job details not found, show 0.00 as total
            Log.d("DatabaseHelper", "Job details not found for ID: " + jobId);
            textViewTotal.setText("Total: $0.00");
            imageViewDetails.setText("");
            return;
        }

        // Get the hourly rate for the selected job
        double hourlyRate = Double.parseDouble(jobDetails.getHourlyRate());

        // Get the time entries for the selected job and timespan
        List<WorkItem> timeEntries = databaseHelper.getTimeEntriesForJobWithinTimespan(selectedJob, fromDate, toDate);

        // Calculate the total hours worked and total earnings for the selected job and timespan
        double totalHours = 0.0;
        StringBuilder jobDetailsStringBuilder = new StringBuilder();

        for (WorkItem timeEntry : timeEntries) {
            try {
                // Parse the working time to total minutes (hours * 60 + minutes)
                String[] workingTimeParts = timeEntry.getWorkingTime().split(":");
                int hours = Integer.parseInt(workingTimeParts[0]);
                int minutes = Integer.parseInt(workingTimeParts[1]);
                int totalMinutes = hours * 60 + minutes;

                totalHours += totalMinutes / 60.0; // Convert total minutes back to hours
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            // Build the job details string
            jobDetailsStringBuilder.append("<b>Shift Title:</b> ").append(timeEntry.getDescription()).append("<br>")
                    .append("<b>Date:</b> ").append(timeEntry.getDay()).append("<br>")
                    .append("<b>Hours:</b> ").append(timeEntry.getWorkingTime()).append("<br><br>");

        }

        // Calculate the total earnings based on the selected criteria
        double totalEarnings = totalHours * hourlyRate;

        if(isWorkingTimeSelected)
        {
            textViewTotal.setText("Total Hour:"+ totalHours);
        }
        else {
            // Update the textViewTotal with the calculated total earnings
            textViewTotal.setText(String.format(Locale.US, "Total Earnings: $%.2f", totalEarnings));
        }
        // Create a formatted string containing the job details and display it in imageViewDetails
        String jobDetailsString = String.format(Locale.US,
                "<b>Job:</b> %s<br><b>Hourly Rate:</b> %.2f<br><br>%s",
                selectedJob, hourlyRate, jobDetailsStringBuilder.toString());
        // Set the formatted string as HTML text to imageViewDetails
        imageViewDetails.setText(Html.fromHtml(jobDetailsString, Html.FROM_HTML_MODE_LEGACY));
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
            startActivity(JobReminder.class);
        }
    }
    private void startActivity(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }


}
