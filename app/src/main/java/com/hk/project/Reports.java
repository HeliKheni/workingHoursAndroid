package com.hk.project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
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
    private Spinner timespanDropdown;
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

        databaseHelper = DatabaseHelper.getInstance(this);
        jobDropdown = findViewById(R.id.jobDropdown);
        timespanDropdown = findViewById(R.id.timespanDropdown);
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

        // Set up the ArrayAdapter for timespan dropdown with the predefined options
        ArrayAdapter<String> timespanAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, timespanOptions);
        timespanAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timespanDropdown.setAdapter(timespanAdapter);

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
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        editText.setText(sdf.format(calendar.getTime()));
    }
    // Method to populate the job dropdown with job titles from the database
    private void populateJobDropdown() {
        List<String> jobTitles = databaseHelper.getAllJobTitles();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, jobTitles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jobDropdown.setAdapter(adapter);
    }

    // Method to update the total based on the selected criteria (job, timespan, job hours or money)
    // Method to update the total based on the selected criteria (job, timespan, job hours, or money)
    private void updateTotal() {
        Toast.makeText(this, "inside upadtetotal",Toast.LENGTH_LONG).show();
        String selectedJob = (String) jobDropdown.getSelectedItem();
        String selectedTimespan = (String) timespanDropdown.getSelectedItem();
        String fromDate = editTextFromDate.getText().toString();
        String toDate = editTextToDate.getText().toString();
        int selectedRadioId = radioGroupType.getCheckedRadioButtonId();
        boolean isWorkingTimeSelected = selectedRadioId == R.id.radioButtonWorkingTime;
        Toast.makeText(this, selectedJob + " " + fromDate+" "+toDate,Toast.LENGTH_LONG).show();

        if (selectedJob == null) {
            // If either job or timespan is not selected, show 0.00 as total
            textViewTotal.setText("Total: $0.00");
            imageViewDetails.setText("");
            return;
        }
        // Get the job ID based on the selected job title
        int jobId = databaseHelper.findJobIdFromTitle(selectedJob);
        Toast.makeText(this,"JobId "+jobId, Toast.LENGTH_LONG).show();

        // Retrieve hourly rate for the selected job from the database using the job ID
        JobDetails jobDetails = databaseHelper.getJobDetails(jobId);
          if (jobDetails == null) {
            // If job details not found, show 0.00 as total
            Toast.makeText(this,"job details null",Toast.LENGTH_LONG).show();
            textViewTotal.setText("Total: $0.00");
            imageViewDetails.setText("");
            return;
        }
        // Get the hourly rate for the selected job
        double hourlyRate = Double.parseDouble(jobDetails.getHourlyRate());
        Toast.makeText(this,"rate: "+hourlyRate, Toast.LENGTH_LONG).show();

        List<WorkItem> timeEntries = databaseHelper.getTimeEntriesForJobWithinTimespan(selectedJob, fromDate, toDate);
         // Calculate the total hours worked for the selected job
        double totalHours = 0.0;
        StringBuilder jobDetailsStringBuilder = new StringBuilder();
        if(timeEntries == null)
        {
            Toast.makeText(this, "timeentriesnull", Toast.LENGTH_SHORT).show();
        }
        for (WorkItem timeEntry : timeEntries) {
            Toast.makeText(this, timeEntry.getDescription() + " "+ timeEntry.getWorkingTime(),Toast.LENGTH_LONG).show();

            totalHours += Double.parseDouble(timeEntry.getWorkingTime());
            jobDetailsStringBuilder.append("Shift Title: ").append(timeEntry.getDescription())
                    .append("\nDate: ").append(timeEntry.getDay())
                    .append("\nHours: ").append(timeEntry.getWorkingTime()).append("\n\n");
        }

        // Calculate the total earnings based on the selected criteria
        double totalEarnings = isWorkingTimeSelected ? totalHours * hourlyRate : 0.0;

        // Update the textViewTotal with the calculated total earnings
        textViewTotal.setText(String.format(Locale.US, "Total: $%.2f", totalEarnings));

        // Create a formatted string containing the job details and display it in imageViewDetails
        String jobDetailsString = String.format(Locale.US,
                "<b>Job:</b> %s<br><b>Total Hours:</b> %.2f<br><b>Total Earnings:</b> $%.2f<br><br>%s",
                selectedJob, totalHours, totalEarnings, jobDetailsStringBuilder.toString());

        imageViewDetails.setText(Html.fromHtml(jobDetailsString, Html.FROM_HTML_MODE_LEGACY));
    }

    // Method to retrieve time entries for a selected job within a specified timespan


}
