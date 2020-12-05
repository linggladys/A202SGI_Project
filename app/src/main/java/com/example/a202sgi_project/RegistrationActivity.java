package com.example.a202sgi_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class RegistrationActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText mRegisterEmail,mRegisterPassword;
    private Button mbtnRegister;
    private TextView registerQtn;
    private FirebaseAuth mAuth;

    private ProgressDialog loader;
    //loading occur

    String[] quotes = new String[]{'"' + "If you spend too much time thinking about a thing, you'll never get it done.- Bruce Lee" + '"'
            ,'"' + "Focus on being productive instead of being busy. - Tim Ferriss" + '"'
            ,'"' + "The way to get started is to quit talking and begin doing. - Walt Disney" + '"'
    };
    int randomElementIndex = (int) (Math.random()*10)%3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this is used to hide the status bar and make the splash screen
        //as a full screen activity
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_registration);

        toolbar = findViewById(R.id.registerToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");

        mAuth = FirebaseAuth.getInstance();
        loader = new ProgressDialog(this);

        //random quotes will popout
        TextView mquoteRegister = findViewById(R.id.quoteRegister);
        mquoteRegister.setText(quotes[randomElementIndex]);


        mRegisterEmail = findViewById(R.id.registerEmail);
        mRegisterPassword = findViewById(R.id.registerPassword);
        mbtnRegister = findViewById(R.id.btnRegister);
        registerQtn = findViewById(R.id.registerPageQuestion);

        //when register is clicked, we will switch to login activity
        registerQtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        //we want to perform registration via clicking this button
        mbtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mRegisterEmail.getText().toString().trim();
                String password = mRegisterPassword.getText().toString().trim();

                //perform validations so that the user has been registered or not
                if (TextUtils.isEmpty(email)){
                    mRegisterEmail.setError("Email is required!");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    mRegisterPassword.setError("Password is required!");
                    return;
                }else{
                    loader.setMessage("Registration in progress");
                    loader.setCanceledOnTouchOutside(false);
                    //TODO:research
                    loader.show();
                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //perform validation here
                            if (task.isSuccessful()){
                                Intent intent = new Intent(RegistrationActivity.this,HomeActivity.class);
                                startActivity(intent);
                                finish();
                                loader.dismiss();
                            }else{
                                String error = task.getException().toString();
                                Toast.makeText(RegistrationActivity.this, "Sorry, registration fail " + error, Toast.LENGTH_SHORT).show();
                                loader.dismiss();
                            }

                        }
                    });
                }
                //perform the login functionality
            }

        });
    }
}