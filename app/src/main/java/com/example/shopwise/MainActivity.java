package com.example.shopwise;

//import android.app.ProgressDialog;
import android.app.ProgressDialog;
import android.content.Intent;
//import android.support.annotation.NonNull;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.text.TextUtils;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
//import android.widget.Toast;
//
//import com.example.shopwise.model.Users;
//import com.example.shopwise.prevalent.Prevalent;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;

import com.example.shopwise.model.Users;
import com.example.shopwise.prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button joinNowButton, logInButton;
   private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        joinNowButton = findViewById(R.id.main_join_now_btn);
        logInButton = findViewById(R.id.main_login_btn);
        progressDialog = new ProgressDialog(this);
        Paper.init(this);


        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, loginActivity.class);
                startActivity(intent);
            }
        });

        joinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        String userPhoneNum = Paper.book().read(Prevalent.UserPhoneKey);
        String userPassword = Paper.book().read(Prevalent.UserPasswordKey);

        if (userPhoneNum != " " && userPassword != " ")
        {
            if (!TextUtils.isEmpty(userPhoneNum) && !TextUtils.isEmpty(userPassword))
            {
                allowAccress(userPhoneNum,userPassword);
                progressDialog.setTitle("LogIn");
                progressDialog.setMessage("Please Wait! while we are checking the credentials ");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

            }
        }


    }

    private void allowAccress(final String phoneNumber, final String password) {


        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("Users").child(phoneNumber).exists())
                {
                    Users userData = dataSnapshot.child("Users").child(phoneNumber).getValue(Users.class);
                    if (userData.getPhone().equals(phoneNumber))
                    {
                        if (userData.getPassword().equals(password))
                        {
                            Toast.makeText(MainActivity.this,
                                    "LogIn Successful...",Toast.LENGTH_SHORT).show();

                            progressDialog.dismiss();

                            Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this,"Please Enter the correct password",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this,
                            "Please try to login with correct phoneNumber",Toast.LENGTH_SHORT).show();

                    progressDialog.dismiss();

                    Toast.makeText(MainActivity.this,
                            "please try to sign up with this Phine number",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}
