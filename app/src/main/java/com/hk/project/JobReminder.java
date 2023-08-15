package com.hk.project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class JobReminder extends AppCompatActivity {

    private EditText startDate;
    private EditText startTime;
    private EditText endTime, title, dis, invitees;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_reminder);


        startDate = findViewById(R.id.date);
        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);
        title = findViewById(R.id.title);
        dis = findViewById(R.id.description);
        invitees = findViewById(R.id.invitees);

        calendar = Calendar.getInstance();


        //handling click event on start date
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(startDate);
            }
        });

        // Click event on start time
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(startTime);
            }
        });

        //Click event in end time
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(endTime);
            }
        });

        // click event on add event button
        Button addEventButton = findViewById(R.id.addEventButton);
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventButtonClicked(v);
            }
        });

        // click event on clear button
        Button clearButton = findViewById(R.id.btnClear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAll();
            }
        });

        footerAllButtonClicked();
    }

    private void clearAll()
    {
        startDate.setText("");
        startTime.setText("");
        endTime.setText("");
        title.setText("");
        dis.setText("");
        invitees.setText("");
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
                    }
                }, hour, minute, false);
        timePickerDialog.show();
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

    private void eventButtonClicked(View view) {
        // getting all controls data
        String eventTitle = ((EditText) findViewById(R.id.title)).getText().toString();
        String startDate = ((EditText) findViewById(R.id.date)).getText().toString();
        boolean isAllDayEvent = ((CheckBox) findViewById(R.id.allDayCheckbox)).isChecked();
        String eventDescription = ((EditText) findViewById(R.id.description)).getText().toString();
        String inviteesEmails = ((EditText) findViewById(R.id.invitees)).getText().toString();
        boolean isPrivateAccess = ((Switch) findViewById(R.id.accessTypeSwitch)).isChecked();

        //creating intent and storing some data
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, eventTitle)
                .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, isAllDayEvent);

        // starting calender and adding all data in that
        Calendar startDateCalendar = convertDateStringToCalendar(startDate);
        if (startDateCalendar != null) {
            if (isAllDayEvent) {
                //checking if its all day event then start date and end time same
                if (isAllDayEvent) {
                    // Set the start date
                    startDateCalendar.set(Calendar.HOUR_OF_DAY, 0);
                    startDateCalendar.set(Calendar.MINUTE, 0);
                    startDateCalendar.set(Calendar.SECOND, 0);
                    startDateCalendar.set(Calendar.MILLISECOND, 0);

                    // Set the end date
                    Calendar endDateCalendar = (Calendar) startDateCalendar.clone();
                    endDateCalendar.add(Calendar.DAY_OF_MONTH, 1);
                    endDateCalendar.set(Calendar.HOUR_OF_DAY, 0);
                    endDateCalendar.set(Calendar.MINUTE, 0);
                    endDateCalendar.set(Calendar.SECOND, 0);
                    endDateCalendar.set(Calendar.MILLISECOND, 0);

                    intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startDateCalendar.getTimeInMillis());
                    intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endDateCalendar.getTimeInMillis());
                } }
            else {
                String startTime = ((EditText) findViewById(R.id.startTime)).getText().toString();
                String endTime = ((EditText) findViewById(R.id.endTime)).getText().toString();
                Calendar startTimeCalendar = convertTimeStringToCalendar(startTime);
                Calendar endTimeCalendar = convertTimeStringToCalendar(endTime);

                if (startTimeCalendar != null && endTimeCalendar != null) {
                    // Set the date and time parts of the start and end times
                    startTimeCalendar.set(Calendar.YEAR, startDateCalendar.get(Calendar.YEAR));
                    startTimeCalendar.set(Calendar.MONTH, startDateCalendar.get(Calendar.MONTH));
                    startTimeCalendar.set(Calendar.DAY_OF_MONTH, startDateCalendar.get(Calendar.DAY_OF_MONTH));
                    endTimeCalendar.set(Calendar.YEAR, startDateCalendar.get(Calendar.YEAR));
                    endTimeCalendar.set(Calendar.MONTH, startDateCalendar.get(Calendar.MONTH));
                    endTimeCalendar.set(Calendar.DAY_OF_MONTH, startDateCalendar.get(Calendar.DAY_OF_MONTH));

                    intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTimeCalendar.getTimeInMillis());
                    intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTimeCalendar.getTimeInMillis());
                } else {
                    Toast.makeText(this, "Invalid start or end time", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        } else {
            Toast.makeText(this, "Invalid start date", Toast.LENGTH_SHORT).show();
            return;
        }

        intent.putExtra(CalendarContract.Events.DESCRIPTION, eventDescription);

        //getting inviteesEmails
        if (!inviteesEmails.isEmpty()) {
            String[] invitees = inviteesEmails.split(",");
            intent.putExtra(Intent.EXTRA_EMAIL, invitees);
        }

        //storing acess level
        intent.putExtra(CalendarContract.Events.ACCESS_LEVEL, isPrivateAccess
                ? CalendarContract.Events.ACCESS_PRIVATE
                : CalendarContract.Events.ACCESS_PUBLIC);

        startActivity(intent);
        Toast.makeText(this, "Event added successfully", Toast.LENGTH_SHORT).show();
    }

    private Calendar convertDateStringToCalendar(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(dateString));
            return calendar;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Calendar convertTimeStringToCalendar(String timeString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(timeString));
            return calendar;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        clearAll();
    }
}