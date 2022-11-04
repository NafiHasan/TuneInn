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
    private Button addFriendButton, declineButton;

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

        //button
        addFriendButton = findViewById(R.id.addFriendButton);
//        cancelFriendButton = findViewById(R.id.cancelFriendButton);
        declineButton = findViewById(R.id.declineButton);

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

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Unfriend(userID);
            }
        });

        LoadCurrentUser();
        checkUserExistence(userID);

    }

    private void Unfriend(String userID) {
        //if both are friend
        if(CurrentState.equals("Friend")){
            friendsRef.child(mUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        friendsRef.child(userID).child(mUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    CurrentState = "notFriend";
                                    addFriendButton.setText("Send Request");
                                    declineButton.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                }
            });
        }
        if(CurrentState.equals("receivedPending")){
            HashMap hashMap = new HashMap();
            hashMap.put("Status", "decline");
            requestRef.child(userID).child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Request Declined", Toast.LENGTH_SHORT).show();
                        CurrentState = "Declined";
                        addFriendButton.setText("Send Request");
                        declineButton.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    private void checkUserExistence(String userID) {
        //check if already friend or not
        friendsRef.child(mUser.getUid()).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    CurrentState = "Friend";
                    addFriendButton.setText("Unfriend");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        friendsRef.child(userID).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    CurrentState = "Friend";
                    addFriendButton.setText("Unfriend");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // check if request is received
        requestRef.child(userID).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("Status").getValue().toString().equals("pending")){
                        CurrentState = "receivedPending";
                        addFriendButton.setText("Accept Request");
                        declineButton.setVisibility(View.VISIBLE);
                        // make cancel button visible
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        // check if request is sent or not
        requestRef.child(mUser.getUid()).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("Status").getValue().toString().equals("pending")){
                        CurrentState = "sentPending";
                        addFriendButton.setText("Cancel Request");
                    }else if(snapshot.child("Status").getValue().toString().equals("declined")){
                        CurrentState = "sentDeclined";
                        addFriendButton.setText("Send Request");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void FriendAction(String userID) {
        // if the user is not friend then request can be sent
        if(CurrentState.equals("notFriend")){
            HashMap hashmap = new HashMap();
            hashmap.put("Status", "pending");
            requestRef.child(mUser.getUid()).child(userID).updateChildren(hashmap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Request Sent!", Toast.LENGTH_SHORT).show();
                        addFriendButton.setText("Cancel Request");
                        CurrentState = "sentPending";
                    }else {
                        Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        // if request is already sent then , user can cancel request
        else if(CurrentState.equals("sentPending")){
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
        }
        // if request is received then user can unfriend
        else if(CurrentState.equals("receivedPending")){
            requestRef.child(userID).child(mUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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
                                            Toast.makeText(getApplicationContext(), "Added User", Toast.LENGTH_SHORT).show();
                                            CurrentState = "Friend";
                                            addFriendButton.setText("Unfriend");
                                            declineButton.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });
        }else {
            //if friend already
            Unfriend(userID);
        }
    }

    private void LoadCurrentUser() {
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    name = snapshot.child("name").getValue().toString();
                    email = snapshot.child("email").getValue().toString();
                    if(snapshot.child("genre").exists())genre = snapshot.child("genre").getValue().toString();
                    if(snapshot.child("URL").exists())url = snapshot.child("URL").getValue().toString();

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