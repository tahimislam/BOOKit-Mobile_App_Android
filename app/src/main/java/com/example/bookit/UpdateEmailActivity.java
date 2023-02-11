package com.example.bookit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdateEmailActivity extends AppCompatActivity {

    private EditText email,pass,new_email;
    private Button authbtn,upbtn;
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private ProgressBar progbar;
    private String oldEmail,newEmail,password,Email;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);

        getSupportActionBar().setTitle("Update Email");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        email=findViewById(R.id.email);
        pass=findViewById(R.id.pass);
        new_email=findViewById(R.id.new_email);
        progbar=findViewById(R.id.progbar);
        authbtn=findViewById(R.id.authbtn);
        upbtn=findViewById(R.id.upbtn);

        upbtn.setEnabled(false);

        authProfile=FirebaseAuth.getInstance();
        firebaseUser=authProfile.getCurrentUser();

        oldEmail=firebaseUser.getEmail();

        if(firebaseUser.equals(""))
        {
            Toast.makeText(UpdateEmailActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
        }
        else
        {
            reAuth(firebaseUser);
        }


    }

    private void reAuth(FirebaseUser firebaseUser) {

        authbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Email=email.getText().toString().trim();
                password=pass.getText().toString().trim();

                if(TextUtils.isEmpty(Email))
                {
                    Toast.makeText(UpdateEmailActivity.this,"Please enter your current email",Toast.LENGTH_LONG).show();
                    email.setError("Email is required");
                    email.requestFocus();
                }
                else if(TextUtils.isEmpty(password))
                {
                    Toast.makeText(UpdateEmailActivity.this,"Please enter your password",Toast.LENGTH_LONG).show();
                    pass.setError("Password is required");
                    pass.requestFocus();
                }
                else {
                    progbar.setVisibility(View.VISIBLE);

                    AuthCredential credential = EmailAuthProvider.getCredential(oldEmail, password);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                progbar.setVisibility(View.GONE);
                                Toast.makeText(UpdateEmailActivity.this, "Password is Verified. Enter New Email",
                                        Toast.LENGTH_LONG).show();

                                upbtn.setEnabled(true);

                                upbtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        newEmail = new_email.getText().toString().trim();
                                        if (TextUtils.isEmpty(newEmail)) {
                                            Toast.makeText(UpdateEmailActivity.this, "Please enter your email", Toast.LENGTH_LONG).show();
                                            new_email.setError("Email is required");
                                            new_email.requestFocus();
                                        } else if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                                            Toast.makeText(UpdateEmailActivity.this, "Please re-enter your email", Toast.LENGTH_LONG).show();
                                            new_email.setError("Valid email is required");
                                            new_email.requestFocus();
                                        } else if (oldEmail.matches((newEmail))) {
                                            Toast.makeText(UpdateEmailActivity.this, "New email cannot be same as old email", Toast.LENGTH_LONG).show();
                                            new_email.setError("New email is required");
                                            new_email.requestFocus();
                                        } else {
                                            progbar.setVisibility(View.VISIBLE);
                                            updateEmail(firebaseUser);
                                        }
                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(UpdateEmailActivity.this, "Wrong Password!", Toast.LENGTH_LONG).show();
                                progbar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });

    }

    private void updateEmail(FirebaseUser firebaseUser) {
        firebaseUser.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                firebaseUser.sendEmailVerification();

                Toast.makeText(UpdateEmailActivity.this,"Email has been updated. Please verify your email!",
                        Toast.LENGTH_LONG).show();
                progbar.setVisibility(View.GONE);

                Intent intent=new Intent(UpdateEmailActivity.this, ProfileActivity.class);

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                finish();               //To close Login Activity
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==android.R.id.home)
        {
            NavUtils.navigateUpFromSameTask(UpdateEmailActivity.this);
        }
        return super.onOptionsItemSelected(item);
    }
}