package com.example.tuneinn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.tuneinn.friendsPackage.FriendOptionsActivity;
import com.example.tuneinn.profilePackage.ProfileActivity;
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
    private String userName, userEmail, userPassword, favGenre, imageURL;
    public static boolean done = false;

    private Button logoutButton, profileButton, playerButton, partyButton, friendsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //buttons
        logoutButton =  findViewById(R.id.logoutButton);
        profileButton = findViewById(R.id.profileButton);
        playerButton = findViewById(R.id.searchUsersButton);
        partyButton = findViewById(R.id.requestsButton);
        friendsButton = findViewById(R.id.friendsButton);

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
                Intent intent1 = new Intent(HomeActivity.this, ProfileActivity.class);
//                System.out.println("userName = " + userName);
                intent1.putExtra("name", userName);
                intent1.putExtra("email", userEmail);
//                intent.putExtra("userPassword", userPassword);
                intent1.putExtra("genre", favGenre);
                System.out.println("Printing " + imageURL);
                intent1.putExtra("url", imageURL);
                startActivity(intent1);
//                finish();
            }
        });

        playerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MusicActivity.class);
                intent.putExtra("playlistId","-1");
                startActivity(intent);
            }
        });

        partyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, PartyActivity.class));
            }
        });

        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, FriendOptionsActivity.class));
            }
        });



        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        // getting current user info

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if(userProfile != null){
                    userName = userProfile.name;
                    userEmail = userProfile.email;
                    userPassword = userProfile.password;
                    favGenre = userProfile.genre;
                    Toast.makeText(getApplicationContext(), "Success data", Toast.LENGTH_SHORT).show();
                    System.out.println("Nafi Success");
                    imageURL = userProfile.URL;
                    System.out.println("Gettingurl " + imageURL);
//                    System.out.println(imageURL);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error in data", Toast.LENGTH_SHORT).show();
                    System.out.println("Nafi Error");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "Error in database!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // updating profile
//    static void updateProfile(String name, String email, String password, String genre, String url){
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
//        String userID = user.getUid();
//
//        User userProfile = new User();
//        userProfile.updateUser(name, email, password, genre, url);
//
//        reference.child(userID).setValue(userProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if(task.isSuccessful()){
//                    done = true;
//                    userName = userProfile.name;
//                    userEmail = userProfile.email;
//                    userPassword = userProfile.password;
//                    favGenre = userProfile.genre;
////                    imageURL = userProfile.URL;
//                }
//                else {
//                    done = false;
////                    System.out.println("Wrong");
//                }
//            }
//        });
//    }
}