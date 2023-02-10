package com.example.bookit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText oldpass,newpass,pass;
    private Button authbtn,upbtn;
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private ProgressBar progbar;
    private String oldPass,newPass,Pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        getSupportActionBar().setTitle("Change Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        oldpass=findViewById(R.id.oldpass);
        pass=findViewById(R.id.pass);
        newpass=findViewById(R.id.newpass);
        progbar=findViewById(R.id.progbar);
        authbtn=findViewById(R.id.authbtn);
        upbtn=findViewById(R.id.upbtn);

        upbtn.setEnabled(false);

        authProfile=FirebaseAuth.getInstance();
        firebaseUser=authProfile.getCurrentUser();

        oldPass=firebaseUser.getEmail();

        if(firebaseUser.equals(""))
        {
            Toast.makeText(ChangePasswordActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
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
                oldPass=oldpass.getText().toString().trim();

                if(TextUtils.isEmpty(oldPass))
                {
                    Toast.makeText(ChangePasswordActivity.this,"Please enter your password",Toast.LENGTH_LONG).show();
                    oldpass.setError("Password is required");
                    oldpass.requestFocus();
                }
                else {
                    progbar.setVisibility(View.VISIBLE);

                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), oldPass);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                progbar.setVisibility(View.GONE);
                                Toast.makeText(ChangePasswordActivity.this, "Password is Verified. Enter New Password",
                                        Toast.LENGTH_LONG).show();

                                upbtn.setEnabled(true);

                                upbtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        newPass = newpass.getText().toString().trim();
                                        Pass=pass.getText().toString().trim();
                                        if (TextUtils.isEmpty(newPass)) {
                                            Toast.makeText(ChangePasswordActivity.this, "Please enter your Password", Toast.LENGTH_LONG).show();
                                            newpass.setError("Password is required");
                                            newpass.requestFocus();
                                        }
                                        else if (TextUtils.isEmpty(Pass)) {
                                            Toast.makeText(ChangePasswordActivity.this, "Please confirm your Password", Toast.LENGTH_LONG).show();
                                            pass.setError("Password is required");
                                            pass.requestFocus();
                                        }
                                        else if (!newPass.matches(Pass)) {
                                            Toast.makeText(ChangePasswordActivity.this, "Password not matched!", Toast.LENGTH_LONG).show();
                                            pass.setError("Password is required");
                                            pass.requestFocus();
                                        }
                                        else {
                                            progbar.setVisibility(View.VISIBLE);
                                            firebaseUser.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        Toast.makeText(ChangePasswordActivity.this,"Password has been changed!",
                                                                Toast.LENGTH_LONG).show();

                                                        Intent intent=new Intent(ChangePasswordActivity.this, ProfileActivity.class);

                                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                                | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(intent);

                                                        finish();               //To close Login Activity
                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(ChangePasswordActivity.this,"Something went wrong!",
                                                                Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                            progbar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(ChangePasswordActivity.this, "Wrong Password!", Toast.LENGTH_LONG).show();
                                progbar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==android.R.id.home)
        {
            NavUtils.navigateUpFromSameTask(ChangePasswordActivity.this);
        }
        return super.onOptionsItemSelected(item);
    }
}