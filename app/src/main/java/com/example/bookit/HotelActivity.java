package com.example.bookit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class HotelActivity extends AppCompatActivity {

    private EditText destination,date,room;
    private Button searchbtn;
    private DatePickerDialog picker;
    private BottomNavigationView bottomNavigationView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel);

        getSupportActionBar().hide();

        bottomNavigationView=findViewById(R.id.bottomview);
        bottomNavigationView.setSelectedItemId(R.id.hotel);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.hotel:
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.flight:
                        startActivity(new Intent(getApplicationContext(),FlightActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        searchbtn=findViewById(R.id.searchbtn);
        destination=findViewById(R.id.destination);
        date=findViewById(R.id.date);
        room=findViewById(R.id.rooms);

        //Setting up DatePicker on EditText
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cal=Calendar.getInstance();
                int day=cal.get(Calendar.DAY_OF_MONTH);
                int month=cal.get(Calendar.MONTH);
                int year=cal.get(Calendar.YEAR);

                //Date Picker Dialog
                picker = new DatePickerDialog(HotelActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        date.setText(day+"/"+(month+1)+"/"+year);
                    }
                },year,month,day);
                picker.show();
            }
        });

        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String Destination=destination.getText().toString().trim();
                String Date=date.getText().toString().trim();
                String Room=room.getText().toString().trim();

                if(TextUtils.isEmpty(Destination))
                {
                    Toast.makeText(HotelActivity.this,"Please enter your destination",Toast.LENGTH_LONG).show();
                    destination.setError("Location is required");
                    destination.requestFocus();
                }
                else if(TextUtils.isEmpty(Date))
                {
                    Toast.makeText(HotelActivity.this,"Please enter travel date",Toast.LENGTH_LONG).show();
                }
                else if(TextUtils.isEmpty(Room))
                {
                    Toast.makeText(HotelActivity.this,"Please enter required rooms",Toast.LENGTH_LONG).show();
                    room.setError("Number of rooms is required");
                    room.requestFocus();
                }
                else {
                    Intent intent = new Intent(HotelActivity.this, SearchedHotelActivity.class);
                    intent.putExtra("destination", Destination);
                    intent.putExtra("date", Date);
                    intent.putExtra("room", Room);
                    startActivity(intent);
                }

//                Intent intent2=new Intent(HotelActivity.this, HotelAdapter2.class);
//                intent2.putExtra("date",Date);
//                intent2.putExtra("room",Room);
//                startActivity(intent2);

            }
        });

    }
}