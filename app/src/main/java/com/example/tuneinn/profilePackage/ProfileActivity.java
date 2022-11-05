package com.example.tuneinn.profilePackage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tuneinn.HomeActivity;
import com.example.tuneinn.HomeBNB;
import com.example.tuneinn.R;
import com.example.tuneinn.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private TextView genreText, userNameText, emailText;
    private ImageView profileImageView;
    private Button editProfileButton;
    private String name, email, genre, url;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // set title of the page
        setTitle("Your Profile");

        editProfileButton = (Button) findViewById(R.id.addFriendButton);
//        progressBar = new ProgressBar(ProfileActivity.this);

        editProfileButton.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, editProfileActivity.class);
//            intent.putExtra("name", name);
//            intent.putExtra("email", email);
//            intent.putExtra("genre", genre);
//            intent.putExtra("url", url);
            startActivity(intent);
            finish();
        });

        userNameText = (TextView) findViewById(R.id.userName);
        emailText = (TextView) findViewById(R.id.userEmail);
        profileImageView = (ImageView) findViewById(R.id.profileImage);
        genreText = (TextView) findViewById(R.id.userGenre);


        name = HomeBNB.currentUser.getName();
        email = HomeBNB.currentUser.getEmail();
        genre = HomeBNB.currentUser.getGenre();
        url = HomeBNB.currentUser.getUrl();


        if(name != null){
            userNameText.setText(name);
            emailText.setText(email);
            if(genre != null)genreText.setText((genre));
            if(url != null && !url.equals("")){
                Picasso.get().load(url).into(profileImageView);
            }
        }
        else {
            startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
            finish();
//            Toast.makeText(ProfileActivity.this, "Network Problem! Reload again! ", Toast.LENGTH_LONG).show();
        }
    }
}