package com.example.tuneinn;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity {

    TextView genreText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Button goBackButton = (Button) findViewById(R.id.goBackButton);
        Button editProfileButton = (Button) findViewById(R.id.saveProfileButton);

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
                finish();
            }
        });

        TextView userNameText = (TextView) findViewById(R.id.userNameText);
        TextView emailText = (TextView) findViewById(R.id.emailText);
        genreText = findViewById(R.id.genreText);
        if(HomeActivity.userName != null){
            userNameText.setText(HomeActivity.userName);
            emailText.setText(HomeActivity.userEmail);
            genreText.setText((HomeActivity.favGenre));
        }
        else {
            Toast.makeText(ProfileActivity.this, "Network Problem! Reload again! ", Toast.LENGTH_LONG).show();
        }
    }
}