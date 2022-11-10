package com.example.tuneinn;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tuneinn.profilePackage.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ProfileFragment extends Fragment {
    View v;

    private FirebaseUser user;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private String userID;
    public static boolean done = false;

    private Button logoutButton;

    Button profilePageButton;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_profile, container, false);

        profilePageButton = v.findViewById(R.id.profileFragGotobutton);
        logoutButton = v.findViewById(R.id.profileFraglogout);
        mAuth = FirebaseAuth.getInstance();

       profilePageButton.setOnClickListener(view -> {
           startActivity(new Intent(requireActivity().getApplicationContext(), ProfileActivity.class));
       });

       logoutButton.setOnClickListener(view -> {
            mAuth.signOut();
           startActivity(new Intent(requireActivity().getApplicationContext(), LoginActivity.class));
            getActivity().finish();
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
                    HomeBNB.currentUser.setName(userProfile.name);
                    HomeBNB.currentUser.setEmail(userProfile.email);
                    HomeBNB.currentUser.setGenre(userProfile.genre);
                    HomeBNB.currentUser.setUrl(userProfile.URL);
//                    Toast.makeText(getContext(), "Success retrieving data", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(requireActivity().getApplicationContext(), "Error retrieving data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireActivity().getApplicationContext(), "Error in database!", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}