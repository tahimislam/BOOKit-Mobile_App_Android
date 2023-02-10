package com.example.bookit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchedHotelActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    HotelAdapter2 adapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searched_hotel);

        getSupportActionBar().setTitle("Available Hotels");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));

        String Destination = getIntent().getStringExtra("destination");
        String Date=getIntent().getStringExtra("date");
        String Room = getIntent().getStringExtra("room");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Hoteldata");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Hoteldata> recyclelist = new ArrayList<>();

                int flag=0;

                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Hoteldata hoteldata=dataSnapshot.getValue(Hoteldata.class);

                    String uid=dataSnapshot.getKey();

                    if(hoteldata == null) continue;
                    hoteldata.setId(String.valueOf(dataSnapshot.getKey()));

                    if(hoteldata.location.equalsIgnoreCase(Destination)){
                        try{

                            int rooms = Integer.parseInt(hoteldata.getRoom());
                            int searchRoom = Integer.parseInt(Room);

                            if(searchRoom <= rooms){
                                recyclelist.add(hoteldata);
                                flag = 1;
                            }

                        }catch (Exception ignored){
                            Toast.makeText(SearchedHotelActivity.this,"No available hotel found!",Toast.LENGTH_LONG).show();
                        }
                    }
                }

                if(flag==0)
                {
                    Toast.makeText(SearchedHotelActivity.this,"No available hotel found!",Toast.LENGTH_LONG).show();
                }

                adapter2= new HotelAdapter2(recyclelist,SearchedHotelActivity.this,Room,Date);
                recyclerView.setAdapter(adapter2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SearchedHotelActivity.this,"Something went wrong!",Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==android.R.id.home)
        {
            NavUtils.navigateUpFromSameTask(SearchedHotelActivity.this);
        }
        return super.onOptionsItemSelected(item);
    }
}