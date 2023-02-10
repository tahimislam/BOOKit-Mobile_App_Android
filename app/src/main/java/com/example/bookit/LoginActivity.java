package com.example.bookit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText user,pass;
    private TextView regbtn,forgotbtn;
    private Button logbtn,googlebtn;
    private ProgressBar progbar;
    private FirebaseAuth authProfile;
    private static final String TAG="LoginActivity";
    boolean vispass;

    private GoogleSignInOptions googleSignInOptions;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        user=findViewById(R.id.user);
        pass=findViewById(R.id.logpass);
        forgotbtn=findViewById(R.id.forgotpass);
        regbtn=(TextView) findViewById(R.id.register);
        logbtn=(Button)findViewById(R.id.logbtn);
        googlebtn=findViewById(R.id.googlebtn);
        progbar=findViewById(R.id.progbar);

        authProfile = FirebaseAuth.getInstance();

        //Toggle password visibility
        pass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int flag=2;
                if(motionEvent.getAction()==MotionEvent.ACTION_UP)
                {
                    if(motionEvent.getRawX()>=pass.getRight()-pass.getCompoundDrawables()[flag].getBounds().width())
                    {
                        int select=pass.getSelectionEnd();
                        if(vispass)
                        {
                            pass.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_24,0);

                            pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            vispass=false;
                        }
                        else
                        {
                            pass.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_baseline_visibility_off_24,0);

                            pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            vispass=true;
                        }
                        pass.setSelection(select);
                        return true;
                    }
                }
                return false;
            }
        });

        googleSignInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient= GoogleSignIn.getClient(this,googleSignInOptions);

        googlebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signin=googleSignInClient.getSignInIntent();
                startActivityForResult(signin,1000);
            }
        });

        logbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String temail=user.getText().toString().trim();
                String tpass=pass.getText().toString().trim();

                if(TextUtils.isEmpty(temail))
                {
                    Toast.makeText(LoginActivity.this,"Please enter your email",Toast.LENGTH_LONG).show();
                    user.setError("Email is required");
                    user.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(temail).matches())
                {
                    Toast.makeText(LoginActivity.this,"Please re-enter your email",Toast.LENGTH_LONG).show();
                    user.setError("Valid email is required");
                    user.requestFocus();
                }
                else if(TextUtils.isEmpty(tpass))
                {
                    Toast.makeText(LoginActivity.this,"Please enter password",Toast.LENGTH_LONG).show();
                    pass.setError("Password is required");
                    pass.requestFocus();
                }
                else
                {
                    progbar.setVisibility(View.VISIBLE);
                    if(temail.equals("admin@bookit.com") && tpass.equals("admin"))
                    {
                        Toast.makeText(LoginActivity.this, "Admin Login Successful!", Toast.LENGTH_LONG).show();
                        Intent intent=new Intent(LoginActivity.this, AdminActivity.class);
                        startActivity(intent);

//                        //To prevent admin from returning back to Login Activity on pressing back button after Login
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
//                                | Intent.FLAG_ACTIVITY_NEW_TASK);
//
//
//                        finish();               //To close Login Activity

                        progbar.setVisibility(View.GONE);
                    }
                    else
                    {
                        loginuser(temail,tpass);
                    }
                }
            }
        });


        //Reset Password
        forgotbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this, ForgotpassActivity.class);
                startActivity(intent);
            }
        });

        //Register user
        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progbar.setVisibility(View.VISIBLE);
        if(requestCode==1000)
        {
            Task<GoogleSignInAccount> task =GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                Intent intent=new Intent(LoginActivity.this, HomeActivity.class);
                Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_LONG).show();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                finish();               //To close Login Activity
                progbar.setVisibility(View.GONE);

            } catch (ApiException e) {
                Toast.makeText(LoginActivity.this,"Something went wrong!",Toast.LENGTH_LONG).show();
                progbar.setVisibility(View.GONE);
            }
        }
    }

    private void loginuser(String email, String pass) {
        authProfile.signInWithEmailAndPassword(email,pass).addOnCompleteListener(LoginActivity.this,
                            new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    //Get instance of the current user
                    FirebaseUser curruser=authProfile.getCurrentUser();

                    //Check if email is verified
                    if(curruser.isEmailVerified())
                    {
                        Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_LONG).show();
                        Intent intent=new Intent(LoginActivity.this, HomeActivity.class);

                        //To prevent user from returning back to Login Activity on pressing back button after Login
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        finish();               //To close Login Activity
                    }
                    else
                    {
                        curruser.sendEmailVerification();
                        authProfile.signOut();
                        Alertbox();
                    }
                }
                else
                {
                    try{
                        throw task.getException();
                    }
                    catch (FirebaseAuthInvalidUserException e){
                        user.setError("User does not exist or no longer valid. Please register again!");
                        user.requestFocus();
                    }
                    catch (FirebaseAuthInvalidCredentialsException e){
                        Toast.makeText(LoginActivity.this, "Login Failed! Incorrect password or email", Toast.LENGTH_LONG).show();
                    }
                    catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(LoginActivity.this,e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                progbar.setVisibility(View.GONE);
            }
        });
    }

    private void Alertbox() {
        //Setup the alert builder
        AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email now. You can not login without email verification.");

        //Open email apps if user click continue button
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent=new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);     //To open email app in new window
                startActivity(intent);
            }
        });

        //Create the AlertBox
        AlertDialog alertDialog=builder.create();

        //Show the AlertBox
        alertDialog.show();
    }

    //Check if user is already logged in
    @Override
    protected void onStart() {
        super.onStart();
        if(authProfile.getCurrentUser()!=null)
        {
            //Redirecting to HomePage
            Intent intent=new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }
}