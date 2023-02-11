package com.example.bookit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AdminActivity extends AppCompatActivity {

    private EditText name,price,room,about,location;
    private ImageView imageView;
    private Button upbtn,addbtn;
    private ProgressBar progbar;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;
    private static final int picimage=1;
    private Uri uriImage;
    private int x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        getSupportActionBar().setTitle("Admin Panel");

        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseStorage= FirebaseStorage.getInstance();

        name=findViewById(R.id.hotelname);
        price=findViewById(R.id.price);
        room=findViewById(R.id.rooms);
        about=findViewById(R.id.description);
        location=findViewById(R.id.location);
        imageView=findViewById(R.id.imageView);
        upbtn=findViewById(R.id.chsbtn);
        addbtn=findViewById(R.id.upbtn);
        progbar=findViewById(R.id.progbar);

        upbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progbar.setVisibility((View.VISIBLE));
                if (uriImage != null)
                {
                    final StorageReference storageReference = firebaseStorage.getReference().child("Hoteldata")
                            .child(System.currentTimeMillis() + "");

                    storageReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Hoteldata hoteldata = new Hoteldata();
                                    hoteldata.setImageview(uri.toString());

                                    hoteldata.setName(name.getText().toString().trim());
                                    hoteldata.setLocation(location.getText().toString().trim());
                                    hoteldata.setRoom(room.getText().toString().trim());
                                    hoteldata.setAbout(about.getText().toString().trim());
                                    hoteldata.setPrice(price.getText().toString().trim());
                                    hoteldata.setRating("0.0");
                                    hoteldata.setUserCount("0");

                                    firebaseDatabase.getReference().child("Hoteldata").push().setValue(hoteldata)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    progbar.setVisibility(View.GONE);
                                                    Toast.makeText(AdminActivity.this, "Hotel with details added successfully!", Toast.LENGTH_LONG).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progbar.setVisibility(View.GONE);
                                                    Toast.makeText(AdminActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                }
                            });
                        }
                    });
                }
                else
                {
                    progbar.setVisibility(View.GONE);
                    Toast.makeText(AdminActivity.this,"No File Selected!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void openFileChooser() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,picimage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==picimage && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            uriImage=data.getData();
            imageView.setImageURI(uriImage);
        }
    }

    //Creating ActionBar Menu


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //Inflate menu items
        getMenuInflater().inflate(R.menu.admin_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //When any menu item is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id=item.getItemId();
        if(id==R.id.logout)
        {
            Toast.makeText(AdminActivity.this,"Signed Out",Toast.LENGTH_LONG).show();
            Intent intent=new Intent(AdminActivity.this,LoginActivity.class);

            //Clear all stacks
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();               //Close ProfileActivity
        }
        else
        {
            Toast.makeText(AdminActivity.this,"Something went wrong!",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}