package com.example.bookit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardActivity extends AppCompatActivity {

    private TextView hotelname,location,rooms,date,payment,confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        getSupportActionBar().setTitle("My Dashboard");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        hotelname=findViewById(R.id.hotelname);
        location=findViewById(R.id.location);
        rooms=findViewById(R.id.rooms);
        date=findViewById(R.id.date);
        payment=findViewById(R.id.payment);
        confirm=findViewById(R.id.confirm);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null) return;

        String uid = user.getUid();

        DatabaseReference reff= FirebaseDatabase.getInstance().getReference().child("Userdata").child(uid).child("hotelId");
        if(reff==null)
        {
            payment.setText("");
            confirm.setText("No Hotel Selected!");
        }
        else {
            reff.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String selected = String.valueOf(snapshot.child("selectedRoom").getValue());
                        String ddate = String.valueOf(snapshot.child("date").getValue());

                        Hoteldata hoteldata = snapshot.child("hotel").getValue(Hoteldata.class);

                        if (hoteldata == null) return;


                        hotelname.setText(hoteldata.getName());
                        location.setText(hoteldata.getLocation());
                        rooms.setText(selected);
                        date.setText(ddate);
                        payment.setText("Successful");
                        confirm.setText("Hotel Booked!");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(DashboardActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==android.R.id.home)
        {
            NavUtils.navigateUpFromSameTask(DashboardActivity.this);
        }
        return super.onOptionsItemSelected(item);
    }
}