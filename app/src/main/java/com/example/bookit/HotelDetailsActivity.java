package com.example.bookit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

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

public class HotelDetailsActivity extends AppCompatActivity {

    private TextView name,price,location,about,rating,room,feedback;
    private ImageView Hotelimage;
    private RatingBar  ratingbar;
    float Rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_details);

        name=findViewById(R.id.Dhotelname);
        price=findViewById(R.id.Dprice);
        location=findViewById(R.id.Dlocation);
        about=findViewById(R.id.Dabout);
        room=findViewById(R.id.Drooms);
        Hotelimage=findViewById(R.id.Dhotelimage);
        feedback=findViewById(R.id.feedback);

        ratingbar = findViewById(R.id.ratingbar);
        rating = findViewById(R.id.rating);


        Hoteldata hotel = (Hoteldata)getIntent().getSerializableExtra("hotel");
        if(hotel == null) return;



        Picasso.get().load(hotel.getImageview())
                        .placeholder(R.drawable.hotel).into(Hotelimage);

        name.setText(hotel.getName());
        location.setText(hotel.getLocation());
        about.setText(hotel.getAbout());
        room.setText(hotel.getRoom());
        price.setText(hotel.getPrice());

        rating.setText(new DecimalFormat("#.#").format(hotel.getFloatRating()));
        ratingbar.setRating(hotel.getFloatRating());



        String title= hotel.getName();
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialog dialog=new Dialog(HotelDetailsActivity.this);
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
                                            Toast.makeText(HotelDetailsActivity.this, "Feedback Added Successfully!", Toast.LENGTH_LONG).show();
                                            //finish();
                                        }
                                        else {
                                            Toast.makeText(HotelDetailsActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                                Toast.makeText(HotelDetailsActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                });

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==android.R.id.home)
        {
            NavUtils.navigateUpFromSameTask(HotelDetailsActivity.this);
        }
        return super.onOptionsItemSelected(item);
    }
}