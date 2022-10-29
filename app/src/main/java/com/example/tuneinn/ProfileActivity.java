package com.example.tuneinn;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private TextView genreText, userNameText, emailText;
    private CircleImageView profileImageView;
    private Button goBackButton, editProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        goBackButton = (Button) findViewById(R.id.goBackButton);
        editProfileButton = (Button) findViewById(R.id.saveProfileButton);

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

        userNameText = (TextView) findViewById(R.id.userNameText);
        emailText = (TextView) findViewById(R.id.emailText);
        profileImageView = (CircleImageView) findViewById(R.id.profileImageView);
        genreText = (TextView) findViewById(R.id.genreText);



        if(HomeActivity.userName != null){
            userNameText.setText(HomeActivity.userName);
            emailText.setText(HomeActivity.userEmail);
            genreText.setText((HomeActivity.favGenre));
            Picasso.get().load(HomeActivity.imageURL).into(profileImageView);
//            System.out.println("now " + HomeActivity.imageURL);
        }
        else {
//            startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
            Toast.makeText(ProfileActivity.this, "Network Problem! Reload again! ", Toast.LENGTH_LONG).show();
        }
    }
}