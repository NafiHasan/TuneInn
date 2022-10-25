package com.example.tuneinn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    public static String userName, userEmail, userPassword, favGenre, imageURL;
    public static boolean done = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //buttons
        Button logoutButton = (Button) findViewById(R.id.logoutButton);
        Button profileButton = (Button) findViewById(R.id.profileButton);
        Button playerButton = (Button) findViewById(R.id.playerButton);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
//                finish();
            }
        });

        playerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, MusicActivity.class));
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if(userProfile != null){
                    userName = userProfile.name;
                    userEmail = userProfile.email;
                    userPassword = userProfile.password;
                    favGenre = userProfile.genre;
                    imageURL = userProfile.URL;
                    System.out.println("got " + imageURL);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Error in database!", Toast.LENGTH_LONG).show();
            }
        });
    }

    static void updateProfile(String name, String email, String password, String genre, String url){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        String userID = user.getUid();

//        boolean done = false;

        User userProfile = new User();
        System.out.println("Hello" + url);
        userProfile.updateUser(name, email, password, genre, url);

        reference.child(userID).setValue(userProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    done = true;
                    userName = userProfile.name;
                    userEmail = userProfile.email;
                    userPassword = userProfile.password;
                    favGenre = userProfile.genre;
//                    imageURL = userProfile.URL;
                }
                else {
                    done = false;
//                    System.out.println("Wrong");
                }
            }
        });
    }
}