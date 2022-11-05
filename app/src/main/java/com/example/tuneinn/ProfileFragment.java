package com.example.tuneinn;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {
    View v;

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private String userName, userEmail, userPassword, favGenre, imageURL;
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


       profilePageButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent1 = new Intent(getContext(), ProfileActivity.class);
//                System.out.println("userName = " + userName);
               intent1.putExtra("name", userName);
               intent1.putExtra("email", userEmail);
//                intent.putExtra("userPassword", userPassword);
               intent1.putExtra("genre", favGenre);
               System.out.println("Printing " + imageURL);
               intent1.putExtra("url", imageURL);
               startActivity(intent1);
           }

       });

       logoutButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               startActivity(new Intent(getContext(), LoginActivity.class));
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
                   // Toast.makeText(getContext(), "Success data", Toast.LENGTH_SHORT).show();
                    System.out.println("Nafi Success");
                    imageURL = userProfile.URL;
                    System.out.println("Gettingurl " + imageURL);
//                    System.out.println(imageURL);
                }
                else {
                    Toast.makeText(getContext(), "Error in data", Toast.LENGTH_SHORT).show();
                    System.out.println("Nafi Error");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error in database!", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}