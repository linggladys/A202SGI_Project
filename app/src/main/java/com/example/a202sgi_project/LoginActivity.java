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


public class LoginActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText mloginEmail,mloginPassword;
    private Button mloginBtn;
    private TextView loginQtn;

    private FirebaseAuth mAuth;


    private ProgressDialog loader;

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
        setContentView(R.layout.activity_login);

        toolbar = findViewById(R.id.loginToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");

        mAuth = FirebaseAuth.getInstance();
        loader = new ProgressDialog(this);

        if (mAuth.getCurrentUser() !=null){
            Intent intent = new Intent (LoginActivity.this,HomeActivity.class);
            startActivity(intent);
        }

        //random quotes will popout
        TextView mquoteLogin = findViewById(R.id.quoteLogin);
        mquoteLogin.setText(quotes[randomElementIndex]);


        mloginEmail = findViewById(R.id.loginEmail);
        mloginPassword = findViewById(R.id.loginPassword);
        mloginBtn = findViewById(R.id.loginBtn);
        loginQtn = findViewById(R.id.loginPageQuestion);

        //we can create an onclick on login question page so we can move on to registration page
        loginQtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegistrationActivity.class);
                startActivity(intent);
            }
        });

        mloginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mloginEmail.getText().toString().trim();
                String password = mloginPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    mloginEmail.setError("Email is required!");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    mloginPassword.setError("Password is required");
                    return;
                }else{
                    //perform login functionality
                    loader.setMessage("Login in progress");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                                startActivity(intent);
                                finish();
                                loader.dismiss();
                            }else{
                                String error = task.getException().toString();
                                Toast.makeText(LoginActivity.this, "Sorry, login failed " + error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}