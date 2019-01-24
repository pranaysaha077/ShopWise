package com.example.shopwise;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shopwise.model.Users;
import com.example.shopwise.prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class loginActivity extends AppCompatActivity {

    private EditText InputPassword, InputPhoneNumber;
    private Button LoginButton;
    private ProgressDialog progressDialog;
    private String parentDbName = "Users";
    //private CheckBox chkBoxRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        InputPassword = findViewById(R.id.login_password_input);
        InputPhoneNumber = findViewById(R.id.login_phone_number_input);
        LoginButton = findViewById(R.id.login_btn);
    //    chkBoxRememberMe = findViewById(R.id.remember_me_chkb);// having issues with check box  remember_ME

        progressDialog = new ProgressDialog(this);

        Paper.init(this);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logInUser();
            }
        });
    }

    private void logInUser() {

        String phoneNumber = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();
        if (TextUtils.isEmpty(phoneNumber))
        {
            InputPhoneNumber.setError("Please Enter your Phone Number");
        }
        else if (TextUtils.isEmpty(password))
        {
            InputPassword.setError("Please Enter your desired password");
        }
        else
        {
            progressDialog.setTitle("LogIn");
            progressDialog.setMessage("Please Wait! while we are checking the credentials ");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();


            allowAccessToAccount(phoneNumber,password);
        }
    }

    private void allowAccessToAccount(final String phoneNumber, final String password) {


        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(parentDbName).child(phoneNumber).exists())
                {
                    Users userData = dataSnapshot.child(parentDbName).child(phoneNumber).getValue(Users.class);
                    if (userData.getPhone().equals(phoneNumber))
                    {
                        if (userData.getPassword().equals(password))
                        {
                            Toast.makeText(loginActivity.this,
                                    "LogIn Successful...",Toast.LENGTH_SHORT).show();

                            progressDialog.dismiss();

                            Intent intent = new Intent(loginActivity.this,HomeActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(loginActivity.this,"Please Enter the correct password",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                }
                else
                {
                    Toast.makeText(loginActivity.this,
                            "Please try to login with correct phoneNumber",Toast.LENGTH_SHORT).show();

                    progressDialog.dismiss();

                    Toast.makeText(loginActivity.this,
                            "please try to sign up with this Phine number",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
