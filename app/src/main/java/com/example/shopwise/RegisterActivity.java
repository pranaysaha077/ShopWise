package com.example.shopwise;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button CreateAccountButton;
    private EditText InputName, InputPassword, InputPhoneNumber;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        CreateAccountButton = findViewById(R.id.register_btn);
        InputName = findViewById(R.id.register_username_input);
        InputPassword = findViewById(R.id.register_password_input);
        InputPhoneNumber = findViewById(R.id.register_phone_number_input);
        loadingBar =  new ProgressDialog(this);

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = InputName.getText().toString();
                String phoneNumber = InputPhoneNumber.getText().toString();
                String password = InputPassword.getText().toString();

                if (TextUtils.isEmpty(phoneNumber))
                {
                    InputName.setError("Please Enter your Phone Number");
                }
                else if (TextUtils.isEmpty(password))
                {
                    InputPassword.setError("Please Enter your desired password");
                }
                else if (TextUtils.isEmpty(name))
                {
                    InputName.setError("Please Enter Your Name");
                }
                else
                {
                     loadingBar.setTitle("Creating Account");
                     loadingBar.setMessage("Please wait while we are checking the credentials");
                     loadingBar.setCanceledOnTouchOutside(false);
                     loadingBar.show();

                     validatePhoneNumber(name,phoneNumber,password);
                }
            }
        });

    }

    private void validatePhoneNumber(final String name, final String phoneNumber, final String password) {

            final DatabaseReference rootRef;
            rootRef = FirebaseDatabase.getInstance().getReference();

            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if ((!dataSnapshot.child("Users").child(phoneNumber).exists()))
                    {
                        HashMap<String,Object> userDataMap= new HashMap<>();
                        userDataMap.put("phone",phoneNumber);
                        userDataMap.put("name",name);
                        userDataMap.put("password",password);

                        rootRef.child("Users").child(phoneNumber).updateChildren(userDataMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            Toast.makeText(RegisterActivity.this,
                                                    "Login is Successful! Congratulations!!",Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();
                                            Intent intent = new Intent(RegisterActivity.this,loginActivity.class);
                                            startActivity(intent);
                                        }
                                        else
                                        {
                                            Toast.makeText(RegisterActivity.this,
                                                    "Network Error ! Try again after some time",Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();
                                        }
                                    }
                                });
                    }
                    else
                    {

                        Toast.makeText(RegisterActivity.this,
                                "this" + phoneNumber + "is already available",Toast.LENGTH_SHORT).show();

                        loadingBar.dismiss();

                        Toast.makeText(RegisterActivity.this,"Please try with another Number" ,Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }
}
