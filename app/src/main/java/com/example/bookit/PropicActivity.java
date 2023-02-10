package com.example.bookit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class PropicActivity extends AppCompatActivity {

    private Button upbtn,chsbtn;
    private ProgressBar progbar;
    private ImageView imageView;
    private FirebaseAuth authProfile;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private static final int picimage=1;
    private Uri uriImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_propic);

        getSupportActionBar().setTitle("Update Profile Picture");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        authProfile=FirebaseAuth.getInstance();
        firebaseUser=authProfile.getCurrentUser();

        upbtn=findViewById(R.id.upbtn);
        chsbtn=findViewById(R.id.chsbtn);
        progbar=findViewById(R.id.progbar);
        imageView=findViewById(R.id.propic);

        storageReference= FirebaseStorage.getInstance().getReference("DisplayPictures");
        Uri uri=firebaseUser.getPhotoUrl();

        //Set user's current DP in Imageview(if uploaded already)
        Picasso.get().load(uri).into(imageView);

        //Choosing image to upload
        chsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        //Upload image to database
        upbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progbar.setVisibility(View.VISIBLE);
                uploadPic();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==android.R.id.home)
        {
            NavUtils.navigateUpFromSameTask(PropicActivity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    private void uploadPic() {
        if(uriImage!=null)
        {
            //Save image with uid of the currently logged user
            StorageReference fileReference=storageReference.child(authProfile.getCurrentUser().getUid()+"."
                                +getFileExtension(uriImage));

            //Upload image to Storage
            fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri=uri;
                            firebaseUser=authProfile.getCurrentUser();

                            //Setting Display Image
                            UserProfileChangeRequest profileUpdate=new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(downloadUri).build();
                            firebaseUser.updateProfile(profileUpdate);
                        }
                    });
                    progbar.setVisibility(View.GONE);
                    Toast.makeText(PropicActivity.this,"Upload Successful!",Toast.LENGTH_LONG).show();

                    Intent intent=new Intent(PropicActivity.this,ProfileActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PropicActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }
        else
        {
            progbar.setVisibility(View.GONE);
            Toast.makeText(PropicActivity.this,"No File Selected!",Toast.LENGTH_LONG).show();
        }
    }

    //Obtain File Extension of the image
    private String getFileExtension(Uri uriImage) {
        ContentResolver cR=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uriImage));
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
}