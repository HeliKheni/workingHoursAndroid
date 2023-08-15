package com.hk.project;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends AppCompatActivity {

    private EditText unEditText, passEditText;
    private TextView errorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        unEditText = findViewById(R.id.unEditText);
        passEditText = findViewById(R.id.passEditText);
        errorTextView = findViewById(R.id.errorTextView);

        TextView register = findViewById(R.id.lnkRegister);
        register.setMovementMethod(LinkMovementMethod.getInstance());

        unEditText.addTextChangedListener(textWatcher);
        passEditText.addTextChangedListener(textWatcher);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Registration.class);
                startActivity(intent);
            }
        });

        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });
        Button btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAll();
            }
        });
    }
    private void clearAll()
    {
        unEditText.setText("");
        passEditText.setText("");
    }
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {   }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        @Override
        public void afterTextChanged(Editable editable) {
            errorTextView.setText("");

        }
    };
    private void handleLogin() {

        String username = unEditText.getText().toString().trim();
        String password = passEditText.getText().toString().trim();

        // Perform validations
        if (username.isEmpty() || password.isEmpty()) {
            errorTextView.setText("All fields are required");
        } else {
            // Check if the user exists
            if (DatabaseHelper.getInstance(this).isUserExists(username)) {
                // Check the password
                if (checkPassword(username, password)) {
                    // Password is correct, move to the WorkStart page
                    Intent intent = new Intent(Login.this, workStart.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Password is incorrect
                    errorTextView.setText("Incorrect password");
                }
            } else {
                // User does not exist
                errorTextView.setText("User does not exist with this username");
            }
        }
    }

    private boolean checkPassword(String username, String password) {
        // Query the database to check if the given password matches the username
        SQLiteDatabase db = DatabaseHelper.getInstance(this).getReadableDatabase();
        String[] projection = {DatabaseHelper.COLUMN_PASSWORD};
        String selection = DatabaseHelper.COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, projection, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") String storedPassword = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PASSWORD));
            cursor.close();
            db.close();
            return password.equals(storedPassword);
        }

        db.close();
        return false;
    }
}
