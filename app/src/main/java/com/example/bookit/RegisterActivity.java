package com.example.bookit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText name,email,mobile,pass,repass,dob;
    private TextView backbtn,gender;
    private Button regbtn;
    private ProgressBar progbar;
    private RadioGroup gengrp;
    private RadioButton genbtn;
    private FirebaseAuth mAuth;
    private DatePickerDialog picker;
    private static final String TAG="RegisterActivity";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();

        name=findViewById(R.id.fullname);
        email=findViewById(R.id.email);
        mobile=findViewById(R.id.mobile);
        pass=findViewById(R.id.pass);
        repass=findViewById(R.id.repass);
        dob=findViewById(R.id.dob);
        gender=findViewById(R.id.gender);
        progbar=findViewById(R.id.progbar);

        //RadioButton for gender
        gengrp=findViewById(R.id.genderbtn);
        gengrp.clearCheck();

        //Setting up DatePicker on EditText
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cal=Calendar.getInstance();
                int day=cal.get(Calendar.DAY_OF_MONTH);
                int month=cal.get(Calendar.MONTH);
                int year=cal.get(Calendar.YEAR);

                //Date Picker Dialog
                picker = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        dob.setText(day+"/"+(month+1)+"/"+year);
                    }
                },year,month,day);
                picker.show();
            }
        });

        backbtn=(TextView) findViewById(R.id.backloginbtn);
        regbtn=(Button)findViewById(R.id.regbtn);

        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int genid=gengrp.getCheckedRadioButtonId();
                genbtn=findViewById(genid);

                //Obtaining the entered data
                String tname=name.getText().toString().trim();
                String temail=email.getText().toString().trim();
                String tmobile=mobile.getText().toString().trim();
                String tpass=pass.getText().toString().trim();
                String trepass=repass.getText().toString().trim();
                String tdob=dob.getText().toString().trim();
                String tgen;

                if(TextUtils.isEmpty(tname))
                {
                    Toast.makeText(RegisterActivity.this,"Please enter your full name",Toast.LENGTH_LONG).show();
                    name.setError("Full name is required");
                    name.requestFocus();
                }
                else if(TextUtils.isEmpty(temail))
                {
                    Toast.makeText(RegisterActivity.this,"Please enter your email",Toast.LENGTH_LONG).show();
                    email.setError("Email is required");
                    email.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(temail).matches())
                {
                    Toast.makeText(RegisterActivity.this,"Please re-enter your email",Toast.LENGTH_LONG).show();
                    email.setError("Valid email is required");
                    email.requestFocus();
                }
                else if(TextUtils.isEmpty(tdob))
                {
                    Toast.makeText(RegisterActivity.this,"Please enter your date of birth",Toast.LENGTH_LONG).show();
                    dob.setError("Date of birth is required");
                    dob.requestFocus();
                }
                else if(TextUtils.isEmpty(tmobile))
                {
                    Toast.makeText(RegisterActivity.this,"Please enter your mobile number",Toast.LENGTH_LONG).show();
                    mobile.setError("Mobile number is required");
                    mobile.requestFocus();
                }
                else if(tmobile.length()!=11)
                {
                    Toast.makeText(RegisterActivity.this,"Please re-enter your mobile number",Toast.LENGTH_LONG).show();
                    mobile.setError("Mobile number should be 11 digits!");
                    mobile.requestFocus();
                }
                else if(gengrp.getCheckedRadioButtonId()==-1)
                {
                    Toast.makeText(RegisterActivity.this,"Please select your gender",Toast.LENGTH_LONG).show();
                    //gender.setError("Gender is required");
                    //gender.requestFocus();
                }
                else if(TextUtils.isEmpty(tpass))
                {
                    Toast.makeText(RegisterActivity.this,"Please enter password",Toast.LENGTH_LONG).show();
                    pass.setError("Password is required");
                    pass.requestFocus();
                }
                else if(tpass.length()<6)
                {
                    Toast.makeText(RegisterActivity.this,"Password should be at least 6 digits!",Toast.LENGTH_LONG).show();
                    pass.setError("Password is too weak!");
                    pass.requestFocus();
                }
                else if(TextUtils.isEmpty(trepass))
                {
                    Toast.makeText(RegisterActivity.this,"Please confirm your password",Toast.LENGTH_LONG).show();
                    repass.setError("Password confirmation is required");
                    repass.requestFocus();
                }
                else if(!tpass.equals(trepass))
                {
                    Toast.makeText(RegisterActivity.this,"Please re-confirm your password",Toast.LENGTH_LONG).show();
                    repass.setError("Password not matched!");
                    repass.requestFocus();

                    //Clear the entered password
                    pass.clearComposingText();
                    repass.clearComposingText();
                }
                else
                {
                    tgen=genbtn.getText().toString().trim();
                    progbar.setVisibility(View.VISIBLE);
                    registeruser(tname,temail,tdob,tgen,tmobile,tpass);
                }
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    //Register user using the credentials given
    private void registeruser(String tname, String temail, String tdob, String tgen, String tmobile, String tpass) {

        //FirebaseAuth mAuth = FirebaseAuth.getInstance();

            //Create User Profile
            mAuth.createUserWithEmailAndPassword(temail,tpass).addOnCompleteListener(RegisterActivity.this,
                    new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            FirebaseUser user=mAuth.getCurrentUser();

                            //Enter user data in Firebase Realtime Database
                            Userdata userdata=new Userdata(tname,tdob,tgen,tmobile);

                            //Extracting user reference from database for Registered users
                            DatabaseReference profile = FirebaseDatabase.getInstance().getReference("Userdata");

                            //Importing userdata to Realtime Database
                            profile.child(user.getUid()).setValue(userdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        //Send Verification Email
                                        user.sendEmailVerification();

                                        Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_LONG).show();

                                        //Back to login page
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);

                                        //To prevent user from returning back to Register Activity on pressing back button after registration
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);

                                        finish();               //To close Register Activity
                                    }
                                    else
                                    {
                                        Toast.makeText(RegisterActivity.this,"Registration Failed! Please try Again",
                                                Toast.LENGTH_LONG).show();
                                    }
                                    progbar.setVisibility(View.GONE);
                                }
                            });
                        }
                        else
                        {
                            try{
                                throw task.getException();
                            }
                            catch (FirebaseAuthInvalidCredentialsException e){
                                email.setError("This email is invalid or already in use");
                                email.requestFocus();
                            }
                            catch (FirebaseAuthUserCollisionException e){
                                email.setError("User is already registered with this email! Use another email");
                                email.requestFocus();
                            }
                            catch (Exception e){
                                Log.e(TAG, e.getMessage());
                                Toast.makeText(RegisterActivity.this,e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    progbar.setVisibility(View.GONE);
                }
            });
    }
}