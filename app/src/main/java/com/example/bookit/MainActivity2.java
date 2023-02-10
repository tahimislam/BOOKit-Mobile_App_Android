package com.example.bookit;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity2 extends AppCompatActivity {

    private Button btn;
    private TextView txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //Check if application is opened for the first time
        SharedPreferences preferences=getSharedPreferences("PREFERENCE",MODE_PRIVATE);
        String FirstTime=preferences.getString("FirstTimeInstall","");

        if(FirstTime.equals("yes"))
        {
            Intent intent=new Intent(MainActivity2.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            SharedPreferences.Editor editor=preferences.edit();
            editor.putString("FirstTimeInstall","yes");
            editor.apply();

            //If App is opened for the first time
            getSupportActionBar().hide();

            btn=(Button) findViewById(R.id.getstart);
            txt=(TextView) findViewById(R.id.skip);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(MainActivity2.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(MainActivity2.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
}