package com.example.tuneinn.profilePackage;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tuneinn.HomeActivity;
import com.example.tuneinn.R;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private TextView genreText, userNameText, emailText;
    private ImageView profileImageView;
    private Button goBackButton, editProfileButton;
    private String name, email, genre, url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // set title of the page
        setTitle("Your Profile");

        goBackButton = (Button) findViewById(R.id.declineButton);
        editProfileButton = (Button) findViewById(R.id.addFriendButton);

        goBackButton.setOnClickListener(view -> {
            startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
            finish();
        });
        editProfileButton.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, editProfileActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("email", email);
            intent.putExtra("genre", genre);
            intent.putExtra("url", url);
            startActivity(intent);
//                finish();
        });

        userNameText = (TextView) findViewById(R.id.userName);
        emailText = (TextView) findViewById(R.id.userEmail);
        profileImageView = (ImageView) findViewById(R.id.profileImage);
        genreText = (TextView) findViewById(R.id.userGenre);

        // getting data from HomeActivity intent

        Intent data = getIntent();
        name = data.getStringExtra("name");
        email = data.getStringExtra("email");
        genre = data.getStringExtra("genre");
        url = data.getStringExtra("url");



        if(name != null){
            userNameText.setText(name);
            emailText.setText(email);
            if(genre != null)genreText.setText((genre));
            if(url != null && !url.equals("")){
                Picasso.get().load(url).into(profileImageView);
            }
        }
        else {
//            startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
            Toast.makeText(ProfileActivity.this, "Network Problem! Reload again! ", Toast.LENGTH_LONG).show();
        }
    }
}