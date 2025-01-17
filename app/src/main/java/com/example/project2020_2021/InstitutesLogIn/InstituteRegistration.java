package com.example.project2020_2021.InstitutesLogIn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project2020_2021.IntroductoryAccount.AccountActivity;
import com.example.project2020_2021.InstitutesProfile.InstituteProfile;
import com.example.project2020_2021.InstitutesSignUp.InstituteSignUp;
import com.example.project2020_2021.R;
import com.example.project2020_2021.StudentsLogIn.StudentRegistration;
import com.example.project2020_2021.StudentsProfile.StudentProfile;
import com.example.project2020_2021.TeachersLogIn.TeacherRegistration;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InstituteRegistration extends AppCompatActivity {

    TextView lregis;
    Button forgotpass, login;
    RelativeLayout newuser;
    TextInputLayout username, password;
    float v=0;
    ImageView crossopt;
    RelativeLayout progressbar;
    final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference("Users");
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_institute_registration);

        mAuth = FirebaseAuth.getInstance();

        //Hooks
        forgotpass = (Button) findViewById(R.id.forgotpass);
        login = (Button) findViewById(R.id.login);
        newuser=(RelativeLayout) findViewById(R.id.newuser);
        username=(TextInputLayout) findViewById(R.id.username);
        password=(TextInputLayout) findViewById(R.id.password);

        progressbar = findViewById(R.id.pgb);

        lregis=(TextView) findViewById(R.id.lregister);

        crossopt =(ImageView)findViewById(R.id.crossopt);

        //Cross Button
        crossopt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(InstituteRegistration.this, AccountActivity.class);
                startActivity(intent);
            }
        });

        //Transitions
        username.setTranslationX(800);
        password.setTranslationX(800);
        login.setTranslationX(800);
        forgotpass.setTranslationX(800);
        newuser.setTranslationX(800);

        username.setAlpha(v);
        password.setAlpha(v);
        login.setAlpha(v);
        forgotpass.setAlpha(v);
        newuser.setAlpha(v);

        username.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        password.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        login.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();
        forgotpass.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        newuser.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();

        //Register Button
        lregis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), InstituteSignUp.class);
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View,String>(findViewById(R.id.lregister),"tregister");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(InstituteRegistration.this,pairs);
                startActivity(intent,options.toBundle());
            }
        });

        //Forgot Password Btuton
        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(InstituteRegistration.this, InsForgotPass.class);

                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View,String>(forgotpass,"transition_forgot_btn");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(InstituteRegistration.this,pairs);
                startActivity(intent,options.toBundle());
            }
        });

        //LogIn Button
        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Checking Wifi Connection
                if (!isConnected(this))
                {
                    showCustomDialog();
                }//Checking Wifi Connection



                if (!validateStuLEmail() | !validateStuLPass()){
                    return;
                }

                //getting data
               /* String _insemail = username.getEditText().getText().toString().trim();
                String _inspass = password.getEditText().getText().toString().trim();

                //Database
                Query checkuser = FirebaseDatabase.getInstance().getReference("Institutes").orderByChild("insemail").equalTo(_insemail);*/

                mAuth.signInWithEmailAndPassword(username.getEditText().getText().toString().trim(),password.getEditText().getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful())
                                {
                                    if (mAuth.getCurrentUser().isEmailVerified())
                                    {
                                        databaseReference.child("Institutes").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                try{
                                                    if(snapshot.exists()){
                                                        startActivity(new Intent(InstituteRegistration.this, InstituteProfile.class));
                                                    }
                                                    else
                                                        Toast.makeText(InstituteRegistration.this, "Institute login only!", Toast.LENGTH_LONG).show();

                                                }
                                                catch (Exception e){}
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });


                                    }
                                    else
                                    {
                                        Toast.makeText(InstituteRegistration.this, "Please Verify your Email Address!", Toast.LENGTH_LONG).show();
                                    }
                                }
                                else
                                {
                                    Toast.makeText(InstituteRegistration.this,task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }

                            }
                        });
            }


            //Checking Wifi Connection
            private boolean isConnected(View.OnClickListener onClickListener) {

                ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo wifiCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo mobileCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                if ((wifiCon != null && wifiCon.isConnected()) || (mobileCon != null && mobileCon.isConnected())) {
                    return true;
                } else {
                    return false;
                }

            }

            private void showCustomDialog() {

                AlertDialog.Builder builder= new AlertDialog.Builder(InstituteRegistration.this);
                builder.setMessage("Please Connect to the Internet to proceed further.")
                        .setCancelable(false).setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));

                    }
                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }//Checking Wifi Connection

        });

    }

    //Progress Bar
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    //Validation Function

    private boolean validateStuLEmail()
    {
        String val = username.getEditText().getText().toString().trim();

        if (val.isEmpty())
        {
            username.setError("Field can not be Empty");
            return false;
        }
        else
        {
            username.setError(null);
            username.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateStuLPass()
    {
        String val = password.getEditText().getText().toString().trim();

        if (val.isEmpty())
        {
            password.setError("Field can not be Empty");
            return false;
        }
        else
        {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }


}