package com.example.tuneinn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendViewActivity extends AppCompatActivity {


    private DatabaseReference mUserRef, requestRef, friendsRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private String name, genre, email, url;

    private CircleImageView profileImage;
    private TextView userName, userEmail, userGenre;
    private Button addFriendButton, cancelFriendButton;

    private String CurrentState = "notFriend";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_view);

        String userID = getIntent().getStringExtra("userID");

        //firebase info
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        requestRef = FirebaseDatabase.getInstance().getReference().child("Requests");
        friendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        LoadCurrentUser();

        //button
        addFriendButton = findViewById(R.id.addFriendButton);
        cancelFriendButton = findViewById(R.id.cancelFriendButton);

        // views
        profileImage = findViewById(R.id.profileImage);
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        userGenre = findViewById(R.id.userGenre);


        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FriendAction(userID);
            }
        });
    }

    private void FriendAction(String userID) {
        if(CurrentState.equals("notFriend")){
            HashMap hashmap = new HashMap();
            hashmap.put("curStatus", "pending");
            requestRef.child(mUser.getUid()).child(userID).updateChildren(hashmap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Request Sent!", Toast.LENGTH_SHORT).show();
                        addFriendButton.setText("Request Sent");
                        CurrentState = "pending";
                    }else {
                        Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else if(CurrentState.equals("pending")){
            requestRef.child(mUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Request Cancelled", Toast.LENGTH_SHORT).show();
                        CurrentState = "notFriend";
                        addFriendButton.setText("Send Request");
                    }else{
                        Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else if(CurrentState.equals("gotRequest")){
            requestRef.child(mUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        HashMap hashMap = new HashMap();
                        hashMap.put("Status", "Friend");
                        hashMap.put("name", name);
                        hashMap.put("url", url);
                        friendsRef.child(mUser.getUid()).child(userID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if(task.isSuccessful()){
                                    friendsRef.child(userID).child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            Toast.makeText(getApplicationContext(), "Added", Toast.LENGTH_SHORT).show();
                                            CurrentState = "Friend";
                                            addFriendButton.setText("Unfriend");
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });
        }else {

        }
    }

    private void LoadCurrentUser() {
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    name = snapshot.child("name").getValue().toString();
                    email = snapshot.child("email").getValue().toString();
                    genre = snapshot.child("genre").getValue().toString();
                    url = snapshot.child("URL").getValue().toString();

                    // load
                    Picasso.get().load(url).into(profileImage);
                    userName.setText(name);
                    userEmail.setText(email);
                    userGenre.setText(genre);
                }else{
                    Toast.makeText(getApplicationContext(), "Data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}