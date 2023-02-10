package com.example.bookit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DeleteProfileActivity extends AppCompatActivity {

    private EditText pass;
    private Button delbtn;
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private ProgressBar progbar;
    private String Pass;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_profile);

        getSupportActionBar().setTitle("Delete Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pass=findViewById(R.id.pass);
        delbtn=findViewById(R.id.delbtn);
        progbar=findViewById(R.id.progbar);

        authProfile=FirebaseAuth.getInstance();
        firebaseUser=authProfile.getCurrentUser();

        if(firebaseUser.equals(""))
        {
            Toast.makeText(DeleteProfileActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
        }
        else
        {
            reAuth(firebaseUser);
        }

    }

    private void reAuth(FirebaseUser firebaseUser) {

        delbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pass=pass.getText().toString().trim();

                if(TextUtils.isEmpty(Pass))
                {
                    Toast.makeText(DeleteProfileActivity.this,"Please enter your password",Toast.LENGTH_LONG).show();
                    pass.setError("Password is required");
                    pass.requestFocus();
                }
                else {
                    progbar.setVisibility(View.VISIBLE);
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), Pass);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progbar.setVisibility(View.VISIBLE);
                                deleteuser(firebaseUser);
                            }
                            else {
                                Toast.makeText(DeleteProfileActivity.this, "Wrong Password!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    progbar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void deleteuser(FirebaseUser firebaseUser) {
        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    deleteuserData();
                    authProfile.signOut();
                    Toast.makeText(DeleteProfileActivity.this, "User has been deleted!", Toast.LENGTH_LONG).show();

                    Intent intent=new Intent(DeleteProfileActivity.this,LoginActivity.class);

                    //Clear all stacks
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();               //Close ProfileActivity
                }
                else
                {
                    Toast.makeText(DeleteProfileActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
                progbar.setVisibility(View.GONE);
            }
        });

    }

    private void deleteuserData() {
        FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();
        StorageReference storageReference=firebaseStorage.getReferenceFromUrl(firebaseUser.getPhotoUrl().toString());
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Userdata");
        databaseReference.child(firebaseUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==android.R.id.home)
        {
            NavUtils.navigateUpFromSameTask(DeleteProfileActivity.this);
        }
        return super.onOptionsItemSelected(item);
    }
}