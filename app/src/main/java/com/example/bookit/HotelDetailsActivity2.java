package com.example.bookit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.HashMap;

public class HotelDetailsActivity2 extends AppCompatActivity {

    private TextView name,price,location,date,about,rating,room,feedback;
    private ImageView Hotelimage;
    private Button bookbtn;
    private RatingBar ratingbar;
    String myDate;
    float Rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_details2);

        name=findViewById(R.id.Dhotelname);
        price=findViewById(R.id.Dprice);
        location=findViewById(R.id.Dlocation);
        date=findViewById(R.id.Ddate);
        about=findViewById(R.id.Dabout);
        room=findViewById(R.id.Drooms);
        Hotelimage=findViewById(R.id.Dhotelimage);
        bookbtn=findViewById(R.id.bookbtn);
        feedback=findViewById(R.id.feedback);
        ratingbar = findViewById(R.id.Dratingbar);
        rating=findViewById(R.id.Drating);


        Hoteldata hoteldata = (Hoteldata)getIntent().getSerializableExtra("hotel");
        if(hoteldata == null) return;

        Picasso.get().load(hoteldata.getImageview())
                .placeholder(R.drawable.hotel).into(Hotelimage);


        rating.setText(new DecimalFormat("#.#").format(hoteldata.getFloatRating()));
        ratingbar.setRating(hoteldata.getFloatRating());

        name.setText(hoteldata.getName());
        location.setText(hoteldata.getLocation());
        myDate = getIntent().getStringExtra("Ddate");
        date.setText(myDate);

        about.setText(hoteldata.getAbout());

        String myRoom = getIntent().getStringExtra("DselectRoom");
        room.setText(myRoom);

        price.setText(hoteldata.getPrice());

        String title= hoteldata.getName();
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialog dialog=new Dialog(HotelDetailsActivity2.this);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.dialog_main);

                dialog.show();

                RatingBar ratingbar=dialog.findViewById(R.id.ratingbar);
                Button ratebtn=dialog.findViewById(R.id.ratebtn);

                ratebtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Rating =ratingbar.getRating();

                        Hoteldata hoteldata = (Hoteldata)getIntent().getSerializableExtra("hotel");
                        if(hoteldata == null) return;

                        String hotelId = hoteldata.getId();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                                .child("Hoteldata").child(hotelId);

                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                String rtt = String.valueOf(snapshot.child("rating").getValue());
                                String rttCount = String.valueOf(snapshot.child("userCount").getValue());

                                if(rtt.equals("null")) rtt = "0.0";
                                if(rttCount.equals("null")) rttCount="0";

                                float oldrating = Float.parseFloat(rtt);
                                int count =Integer.parseInt(rttCount);

                                count+=1;

                                oldrating =  ( (oldrating*(count-1)) + Rating)/count;
                                //oldrating=(float)Math.round(oldrating*1000)/1000;

                                HashMap<String,Object> map= new HashMap<>();
                                map.put("rating",oldrating+"");
                                map.put("userCount",count+"");

                                ref.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        dialog.dismiss();
                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(HotelDetailsActivity2.this, "Feedback Added Successfully!", Toast.LENGTH_LONG).show();
                                            //finish();
                                        }
                                        else {
                                            Toast.makeText(HotelDetailsActivity2.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                                Toast.makeText(HotelDetailsActivity2.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                });
            }
        });

        bookbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HotelDetailsActivity2.this, ConfirmActivity.class);
                intent.putExtra("hotel",hoteldata);
                intent.putExtra("Ddate",myDate);
                intent.putExtra("DselectRoom",myRoom);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==android.R.id.home)
        {
            NavUtils.navigateUpFromSameTask(HotelDetailsActivity2.this);
        }
        return super.onOptionsItemSelected(item);
    }
}