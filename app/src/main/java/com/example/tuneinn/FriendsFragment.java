package com.example.tuneinn;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.tuneinn.friendsPackage.FindFriendActivity;
import com.example.tuneinn.friendsPackage.FriendListActivity;
import com.example.tuneinn.friendsPackage.RequestsActivity;


public class FriendsFragment extends Fragment {
    private Button friendsButton, requestsButton, searchUsersButton, recommendationsButton;
    View v;

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_friends, container, false);

        friendsButton = v.findViewById(R.id.friendsButtonf);
        searchUsersButton = v.findViewById(R.id.searchUsersButtonf);
        requestsButton = v.findViewById(R.id.requestsButtonf);
        recommendationsButton = v.findViewById(R.id.recommendationsButton);

        searchUsersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), FindFriendActivity.class));
            }
        });

        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), FriendListActivity.class));
            }
        });

        requestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), RequestsActivity.class));
            }
        });

        recommendationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), RecommendationsViewActivity.class));
            }
        });

        return v;
    }
}