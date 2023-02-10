package com.example.bookit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ConfirmActivity extends AppCompatActivity {


    private CardView bkash,nogod,rocket,visa;
    private TextView payment;
    private Button confirmbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        getSupportActionBar().setTitle("Confirm Booking");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bkash=findViewById(R.id.bkash);
        nogod=findViewById(R.id.nogod);
        rocket=findViewById(R.id.rocket);
        visa=findViewById(R.id.visa);
        confirmbtn=findViewById(R.id.confirmbtn);
        payment=findViewById(R.id.payment);


        Hoteldata hoteldata = (Hoteldata)getIntent().getSerializableExtra("hotel");
        if(hoteldata == null) return;

        String curRoom = hoteldata.getRoom();
        String myRoom = getIntent().getStringExtra("DselectRoom");
        String myDate = getIntent().getStringExtra("Ddate");
        String hotelId = hoteldata.getId();

        bkash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payment.setText("Bkash");
            }
        });

        nogod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payment.setText("Nogod");
            }
        });

        rocket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payment.setText("Rocket");
            }
        });

        visa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payment.setText("Visa");
            }
        });

        confirmbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(payment.getText().toString().equals(""))
                {
                    Toast.makeText(ConfirmActivity.this,"Please Select Payment Method!",Toast.LENGTH_LONG).show();
                }
                else
                {

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                                    .child("Hoteldata").child(hotelId).child("room");

                    try{
                        int availableRoom = Integer.parseInt(curRoom);
                        int selected = Integer.parseInt(myRoom);

                        int rem = Math.max(0,availableRoom-selected);

                        ref.setValue(rem+"").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                if(user == null) return;

                                String uid = user.getUid();

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Userdata")
                                                .child(uid).child("hotelId");

                                HashMap<String,Object> map = new HashMap<>();
                                map.put("selectedRoom",myRoom);
                                map.put("date",myDate);
                                map.put("hotel",hoteldata);

                                ref.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            Toast.makeText(ConfirmActivity.this, "Hotel Booked Successfully! See Your Dashboard", Toast.LENGTH_LONG).show();

                                            Intent intent = new Intent(ConfirmActivity.this, DashboardActivity.class);
//                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
//                                                    | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);

                                            finish();               //To close Login Activity
                                        }
                                    }
                                });
                            }
                        });
                    }
                    catch (Exception ignored){}
                }

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==android.R.id.home)
        {
            NavUtils.navigateUpFromSameTask(ConfirmActivity.this);
        }
        return super.onOptionsItemSelected(item);
    }
}