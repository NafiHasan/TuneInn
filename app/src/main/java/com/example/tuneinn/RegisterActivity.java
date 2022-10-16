package com.example.tuneinn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    //firebase variables
    private FirebaseAuth mAuth;

    // register variables
    private EditText editUsername, editEmail, editPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //firebase variable
        mAuth = FirebaseAuth.getInstance();

        Button gotoSigninButton = (Button) findViewById(R.id.gotoSigninButton);
        Button signupButton = (Button) findViewById(R.id.signupButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        gotoSigninButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });


        editUsername = (EditText) findViewById(R.id.usernameEdit);
        editEmail = (EditText) findViewById(R.id.emailEdit);
        editPassword = (EditText) findViewById(R.id.passwordEdit);
    }

    private void registerUser() {
        String name = editUsername.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String email = editEmail.getText().toString().trim();

        // error check
        if(name.isEmpty()){
            editUsername.setError("Please enter an Username");
            editUsername.requestFocus();
            return;
        }

        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editEmail.setError("Please enter a valid email");
            editEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            editPassword.setError("Password field cannot be empty!");
            editPassword.requestFocus();
            return;
        }


        progressBar.setVisibility(View.VISIBLE);
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(RegisterActivity.this, "Authentication done.",
//                                    Toast.LENGTH_SHORT).show();
////                            progressBar.setVisibility(View.GONE);
//                            FirebaseUser user = mAuth.getCurrentUser();
//
//                            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
//                            finish();
//                        } else {
//                            Toast.makeText(RegisterActivity.this, "Authentication failed."
//                                            + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//
////                            progressBar.setVisibility(View.GONE);
//                        }
//                    }
//                });

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            User user = new User(name, email, password);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(RegisterActivity.this,
                                                        "Authentication done.", Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);

//                                                FirebaseUser user = mAuth.getCurrentUser();
                                                startActivity(new Intent(RegisterActivity.this,
                                                        HomeActivity.class));

                                                finish();
                                            }
                                            else {
                                                Toast.makeText(RegisterActivity.this,
                                                        "Authentication failed." + task.getException()
                                                                .getMessage(), Toast.LENGTH_SHORT).show();

                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                        }
                        else {
                            Toast.makeText(RegisterActivity.this, "Authentication failed."
                                            + task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}