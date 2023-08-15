package com.hk.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Registration extends AppCompatActivity {

    private EditText unEditText, passEditText, CPassEditText;
    private TextView errorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        unEditText = findViewById(R.id.unEditText);
        passEditText = findViewById(R.id.passEditText);
        CPassEditText = findViewById(R.id.CPassEditText);
        errorTextView = findViewById(R.id.errorTextView);

        Button btnReg = findViewById(R.id.btnReg);
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndRegister();
            }
        });


        Button btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAll();
            }
        });
        TextView login = (TextView)findViewById(R.id.lnkLogin);
        login.setMovementMethod(LinkMovementMethod.getInstance());
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Registration.this, Login.class);
                startActivity(intent);
            }
        });

        unEditText.addTextChangedListener(textWatcher);
        passEditText.addTextChangedListener(textWatcher);
        CPassEditText.addTextChangedListener(textWatcher);

    }
    private void clearAll()
    {
        CPassEditText.setText("");
        unEditText.setText("");
        passEditText.setText("");
    }
    private void validateAndRegister() {
        String username = unEditText.getText().toString().trim();
        String password = passEditText.getText().toString().trim();
        String confirmPassword = CPassEditText.getText().toString().trim();

        // Perform validations
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorTextView.setText("All fields are required");
        } else if (username.length() < 6) {
            errorTextView.setText("Username must be at least 6 characters long");
        } else if (password.length() < 8) {
            errorTextView.setText("Password must be at least 8 characters long");
        } else if (!password.matches(".*[0-9].*") || !password.matches(".*[!@#$%^&*()].*")) {
            errorTextView.setText("Password must include at least one number and one special character");
        } else if (!password.equals(confirmPassword)) {
            errorTextView.setText("Passwords do not match");
        } else {
                // Check if the user already exists
                if (DatabaseHelper.getInstance(this).isUserExists(username)) {
                    errorTextView.setText("Username already exists. Please choose a different username.");
                } else {
                    // Register the user
                    boolean isRegistered = registerUser(username, password);
                    if (isRegistered) {
                        errorTextView.setText("");
                        // Registration successful, move to the login page
                        Intent intent = new Intent(Registration.this, Login.class);
                        startActivity(intent);
                        finish();
                    } else {
                        errorTextView.setText("Failed to register user. Please try again later.");
                    }
                }
        }
    }

    private boolean registerUser(String username, String password) {
        // Insert the user into the users table
        SQLiteDatabase db = DatabaseHelper.getInstance(this).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_USERNAME, username);
        values.put(DatabaseHelper.COLUMN_PASSWORD, password);

        long result = db.insert(DatabaseHelper.TABLE_USERS, null, values);
        db.close();
        return result != -1;
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
}