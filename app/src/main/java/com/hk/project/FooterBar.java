package com.hk.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FooterBar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.footer_bar);
    }

    public void calenderPage(View view) {
        Toast.makeText(this, "Button clicked!", Toast.LENGTH_SHORT).show();

       // Intent intent = new Intent(this, CalenderViewActivity.class);
       // startActivity(intent);
    }


}
