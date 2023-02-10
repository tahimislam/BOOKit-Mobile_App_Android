package com.example.bookit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotpassActivity extends AppCompatActivity {

    private Button resbtn;
    private EditText email;
    private ProgressBar progbar;
    private FirebaseAuth authProfile;
    private final static String TAG="ForgotpassActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpass);

        getSupportActionBar().hide();

        resbtn=findViewById(R.id.resetbtn);
        email=findViewById(R.id.email);
        progbar=findViewById(R.id.progbar);


        resbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String temail=email.getText().toString().trim();

                if(TextUtils.isEmpty(temail))
                {
                    Toast.makeText(ForgotpassActivity.this,"Please enter your email",Toast.LENGTH_LONG).show();
                    email.setError("Email is required");
                    email.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(temail).matches())
                {
                    Toast.makeText(ForgotpassActivity.this,"Please re-enter your email",Toast.LENGTH_LONG).show();
                    email.setError("Valid email is required");
                    email.requestFocus();
                }
                else
                {
                    progbar.setVisibility(View.VISIBLE);
                    resetPassword(temail);
                }
            }
        });
    }

    private void resetPassword(String temail) {
        authProfile=FirebaseAuth.getInstance();
        authProfile.sendPasswordResetEmail(temail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(ForgotpassActivity.this, "Please check your inbox for password reset link",
                                                Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(ForgotpassActivity.this, LoginActivity.class);

                    //To prevent user from returning back to ForgotPassword Activity on pressing back button after reset password
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();               //To close ForgotPassword Activity
                }
                else
                {
                    try{
                        throw task.getException();
                    }
                    catch (FirebaseAuthInvalidUserException e){
                        email.setError("User does not exist or no longer valid. Please register again!");
                        email.requestFocus();
                    }
                    catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(ForgotpassActivity.this,e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                progbar.setVisibility(View.GONE);
            }
        });
    }
}