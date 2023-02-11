package com.example.bookit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private TextView fullname,name,email,dob,mobile,gender,signout,dashboard;
    private String sfullname,semail,sdob,smobile,sgender;
    private ImageView imageView;
    private FirebaseAuth authProfile;
    private ProgressBar progbar;
    private BottomNavigationView bottomNavigationView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setTitle("Profile");

        bottomNavigationView=findViewById(R.id.bottomview);
        bottomNavigationView.setSelectedItemId(R.id.profile);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.hotel:
                        startActivity(new Intent(getApplicationContext(),HotelActivity.class));
                        overridePendingTransition(0,0);
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
                        return true;
                }
                return false;
            }
        });

        name=findViewById(R.id.proname);
        fullname=findViewById(R.id.fullname);
        email=findViewById(R.id.email);
        dob=findViewById(R.id.dob);
        mobile=findViewById(R.id.mobile);
        gender=findViewById(R.id.gender);
        signout=findViewById(R.id.signout);
        dashboard=findViewById(R.id.dashboard);
        imageView=findViewById(R.id.propic);
        authProfile=FirebaseAuth.getInstance();
        progbar=findViewById(R.id.progbar);
        FirebaseUser firebaseUser=authProfile.getCurrentUser();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ProfileActivity.this,PropicActivity.class);
                startActivity(intent);
            }
        });

        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ProfileActivity.this,DashboardActivity.class);
                startActivity(intent);
            }
        });

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authProfile.signOut();
                Toast.makeText(ProfileActivity.this,"Signed Out",Toast.LENGTH_LONG).show();
                Intent intent=new Intent(ProfileActivity.this,LoginActivity.class);

                //Clear all stacks
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();               //Close ProfileActivity
            }
        });

        if(firebaseUser==null)
        {
            Toast.makeText(ProfileActivity.this,"Something went wrong! User's details are not available at the moment"
                    ,Toast.LENGTH_LONG).show();
        }
        else
        {
            progbar.setVisibility(View.VISIBLE);
            UserProfile(firebaseUser);
        }
    }

    private void UserProfile(FirebaseUser firebaseUser) {
        String userid=firebaseUser.getUid();

        //Extracting user reference from database
        DatabaseReference reff= FirebaseDatabase.getInstance().getReference("Userdata");
        reff.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Userdata readData=snapshot.getValue(Userdata.class);
                if(readData!=null)
                {
                    sfullname=readData.Name;
                    semail=firebaseUser.getEmail();
                    sdob=readData.Dob;
                    smobile=readData.Mobile;
                    sgender=readData.Gender;

                    name.setText("Welcome, "+sfullname+"!");
                    fullname.setText(sfullname);
                    email.setText(semail);
                    dob.setText(sdob);
                    mobile.setText(smobile);
                    gender.setText(sgender);

                    //Set user DP
                    Uri uri=firebaseUser.getPhotoUrl();
                    Picasso.get().load(uri).into(imageView);
                }
                else
                {
                    Toast.makeText(ProfileActivity.this,"Something went wrong!",Toast.LENGTH_LONG).show();
                }
                progbar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this,"Something went wrong!",Toast.LENGTH_LONG).show();
                progbar.setVisibility(View.GONE);
            }
        });
    }

    //Creating ActionBar Menu


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Inflate menu items
        getMenuInflater().inflate(R.menu.common_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //When any menu item is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id=item.getItemId();
        if(id==R.id.refresh)
        {
            //Refresh Activity
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
        }
        else if(id==R.id.upEmail)
        {
            Intent intent=new Intent(ProfileActivity.this,UpdateEmailActivity.class);
            startActivity(intent);
        }
        else if(id==R.id.chngpass)
        {
            Intent intent=new Intent(ProfileActivity.this,ChangePasswordActivity.class);
            startActivity(intent);
        }
        else if(id==R.id.delProfile)
        {
            Intent intent=new Intent(ProfileActivity.this,DeleteProfileActivity.class);
            startActivity(intent);
        }
        else if(id==R.id.logout)
        {
            authProfile.signOut();
            Toast.makeText(ProfileActivity.this,"Signed Out",Toast.LENGTH_LONG).show();
            Intent intent=new Intent(ProfileActivity.this,LoginActivity.class);

            //Clear all stacks
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();               //Close ProfileActivity
        }
        else
        {
            Toast.makeText(ProfileActivity.this,"Something went wrong!",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}