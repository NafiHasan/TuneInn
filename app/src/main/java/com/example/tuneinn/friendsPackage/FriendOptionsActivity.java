package com.example.tuneinn.friendsPackage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.tuneinn.R;

public class FriendOptionsActivity extends AppCompatActivity {

    private Button friendsButton, requestsButton, searchUsersButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_options);

        setTitle("Connect with Friends");

        friendsButton = findViewById(R.id.friendsButton);
        searchUsersButton = findViewById(R.id.searchUsersButton);
        requestsButton = findViewById(R.id.requestsButton);


        searchUsersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FriendOptionsActivity.this, FindFriendActivity.class));
            }
        });

        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FriendOptionsActivity.this, FriendListActivity.class));
            }
        });

        requestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FriendOptionsActivity.this, RequestsActivity.class));
            }
        });
    }
}