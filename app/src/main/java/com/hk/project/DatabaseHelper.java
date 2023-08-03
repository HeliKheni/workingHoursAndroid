package com.hk.project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "my_database.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_TIME_ENTRIES = "time_entries";
    public static final String TABLE_JOB_DETAILS = "job_details";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_JOB = "job";
    public static final String COLUMN_DAY = "day";
    public static final String COLUMN_START_TIME = "start_time";
    public static final String COLUMN_END_TIME = "end_time";
    public static final String COLUMN_WORKING_TIME = "working_time";
    public static final String COLUMN_HOUR_RATE = "hourly_rate";
    public static final String COLUMN_HOLIDAY_RATE = "holiday_rate";
    public static final String COLUMN_DEFAULT_TASK = "default_task";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the time_entries table
        String createTimeEntriesTableQuery = "CREATE TABLE " + TABLE_TIME_ENTRIES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_JOB + " TEXT, " +
                COLUMN_DAY + " TEXT, " +
                COLUMN_START_TIME + " TEXT, " +
                COLUMN_END_TIME + " TEXT, " +
                COLUMN_WORKING_TIME + " TEXT);";

        db.execSQL(createTimeEntriesTableQuery);

        // Create the job_details table
        String createJobDetailsTableQuery = "CREATE TABLE " + TABLE_JOB_DETAILS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_JOB + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_HOUR_RATE + " TEXT, " +
                COLUMN_HOLIDAY_RATE + " TEXT, " +
                COLUMN_DEFAULT_TASK + " BOOLEAN);";

        db.execSQL(createJobDetailsTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades if needed
        // Example: if (oldVersion < newVersion) { ... }
    }

   public JobDetails getJobDetails(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        JobDetails jobDetails = null;

        String[] projection = {
                COLUMN_ID,
                COLUMN_JOB,
                COLUMN_DESCRIPTION,
                COLUMN_HOUR_RATE,
                COLUMN_HOLIDAY_RATE,
                COLUMN_DEFAULT_TASK
        };

        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor cursor = db.query(TABLE_JOB_DETAILS, projection, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int jobId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            @SuppressLint("Range") String job = cursor.getString(cursor.getColumnIndex(COLUMN_JOB));
            @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
            @SuppressLint("Range") String hourlyRate = cursor.getString(cursor.getColumnIndex(COLUMN_HOUR_RATE));
            @SuppressLint("Range") String holidayRate = cursor.getString(cursor.getColumnIndex(COLUMN_HOLIDAY_RATE));
            @SuppressLint("Range") int isDefaultTask = cursor.getInt(cursor.getColumnIndex(COLUMN_DEFAULT_TASK));
            boolean defaultTask = (isDefaultTask == 1);

            jobDetails = new JobDetails(jobId, job, description, hourlyRate, holidayRate, defaultTask);

            cursor.close();
        }

        return jobDetails;
    }


    public List<String> getAllJobTitles() {
        List<String> jobTitles = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                COLUMN_ID,
                COLUMN_JOB
        };

        Cursor cursor = db.query(TABLE_JOB_DETAILS, projection, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String job = cursor.getString(cursor.getColumnIndex(COLUMN_JOB));
                jobTitles.add(job);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return jobTitles;
    }

    @SuppressLint("Range")
    public String getDefaultJob() {
        SQLiteDatabase db = this.getReadableDatabase();
        String defaultJob = null;

        String[] projection = {COLUMN_JOB};
        String selection = COLUMN_DEFAULT_TASK + " = ?";
        String[] selectionArgs = {String.valueOf(1)}; // 1 represents true (default task)

        Cursor cursor = db.query(TABLE_JOB_DETAILS, projection, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            defaultJob = cursor.getString(cursor.getColumnIndex(COLUMN_JOB));
            cursor.close();
        }

        return defaultJob;
    }

    public List<WorkItem> getTimeEntriesForJobWithinTimespan(String jobId, String fromDate, String toDate) {
        List<WorkItem> timeEntries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                COLUMN_ID,
                COLUMN_JOB,
                COLUMN_DESCRIPTION,
                COLUMN_DAY,
                COLUMN_START_TIME,
                COLUMN_END_TIME,
                COLUMN_WORKING_TIME
        };

        String selection = COLUMN_JOB + " = ? AND " + COLUMN_DAY + " >= ? AND " + COLUMN_DAY + " <= ?";
        String[] selectionArgs = {jobId, fromDate, toDate};

        Cursor cursor = db.query(TABLE_TIME_ENTRIES, projection, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
               // @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
                @SuppressLint("Range") String job = cursor.getString(cursor.getColumnIndex(COLUMN_JOB));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
                @SuppressLint("Range") String day = cursor.getString(cursor.getColumnIndex(COLUMN_DAY));
                @SuppressLint("Range") String startTime = cursor.getString(cursor.getColumnIndex(COLUMN_START_TIME));
                @SuppressLint("Range") String endTime = cursor.getString(cursor.getColumnIndex(COLUMN_END_TIME));
                @SuppressLint("Range") String workingTime = cursor.getString(cursor.getColumnIndex(COLUMN_WORKING_TIME));

                WorkItem workItem = new WorkItem( job, description, day, startTime, endTime, workingTime);
                timeEntries.add(workItem);
            } while (cursor.moveToNext());

            cursor.close();
        }

        Log.d("DatabaseHelper", "Query: " + db.compileStatement("SELECT * FROM " + TABLE_TIME_ENTRIES + " WHERE " + selection).toString());

        for (WorkItem item : timeEntries) {
            Log.d("DatabaseHelper", "Time Entry: " + item.getDescription() + " " + item.getWorkingTime());
        }

        return timeEntries;
    }

    // Method to find the job ID based on the selected job title
    @SuppressLint("Range")
    public int findJobIdFromTitle(String selectedJobTitle) {
        SQLiteDatabase db = this.getReadableDatabase();
        int jobId = -1;

        String[] projection = {COLUMN_ID};
        String selection = COLUMN_JOB + " = ?";
        String[] selectionArgs = {selectedJobTitle};

        Cursor cursor = db.query(TABLE_JOB_DETAILS, projection, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            jobId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            cursor.close();
        }

        return jobId;
    }





}
