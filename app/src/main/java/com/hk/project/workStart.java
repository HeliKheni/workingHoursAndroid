package com.hk.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class workStart extends AppCompatActivity {


    private RecyclerView recyclerView;
    private WorkAdapter workAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_start);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Call the method to display the data from SQLite
        displayDataFromSQLite();
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the data from SQLite whenever the activity is resumed
        displayDataFromSQLite();
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
}