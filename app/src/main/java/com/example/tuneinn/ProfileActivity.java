package com.example.tuneinn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.tuneinn.HomeActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Button goBackButton = (Button) findViewById(R.id.goBackButton);
        Button editProfileButton = (Button) findViewById(R.id.editProfileButton);

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                finish();
            }
        });
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, editProfileActivity.class));
//                finish();
            }
        });

        TextView userNameText = (TextView) findViewById(R.id.userNameText);
        TextView emailText = (TextView) findViewById(R.id.emailText);
        if(HomeActivity.userName != null){
            userNameText.setText(HomeActivity.userName);
            emailText.setText(HomeActivity.userEmail);
        }
        else {
            Toast.makeText(ProfileActivity.this, "Network Problem! Reload again! ", Toast.LENGTH_LONG).show();
        }
    }
}