package com.example.tuneinn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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

public class RegisterActivity extends AppCompatActivity {
    //firebase variables
    private FirebaseAuth mAuth;

    // register variables
    private EditText editUsername, editEmail, editPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //firebase variable
        mAuth = FirebaseAuth.getInstance();

        Button GotoSigninButton = (Button) findViewById(R.id.gotoSigninButton);
        Button signupButton = (Button) findViewById(R.id.signupButton);

        GotoSigninButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                editUsername.setError("Please enter a valid email");
                editUsername.requestFocus();
                return;
            }

        if(password.isEmpty()){
            editUsername.setError("Password field cannot be empty!");
            editUsername.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Authentication done.",
                                    Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Authentication failed." + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}