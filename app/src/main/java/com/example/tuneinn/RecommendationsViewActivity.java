package com.example.tuneinn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tuneinn.friendsPackage.FriendListActivity;
import com.example.tuneinn.friendsPackage.FriendViewActivity;
import com.example.tuneinn.friendsPackage.Friends;
import com.example.tuneinn.friendsPackage.FriendsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class RecommendationsViewActivity extends AppCompatActivity {


    private RecyclerView recommendationsRecycler;
    private TextView song_name;


    FirebaseRecyclerOptions<Friends> options;
    FirebaseRecyclerAdapter<Friends, RecSongsViewHolder> adapter;

    private DatabaseReference mUserRef, mRecRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations_view);

        setTitle("Songs Recommended by Friends");

//        song_name = findViewById(R.id.song_name);
        recommendationsRecycler = findViewById(R.id.recommendationsRecycler);
        recommendationsRecycler.setLayoutManager(new LinearLayoutManager(this));

        // database variables

        mUserRef = FirebaseDatabase.getInstance().getReference("Users");
        mRecRef = FirebaseDatabase.getInstance().getReference().child("Recommendations");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        LoadFriends("");

    }
    private void LoadFriends(String s) {

        Query query = mRecRef.child(mUser.getUid()).orderByChild("Song Name").startAt(s.toLowerCase()).endAt(s.toLowerCase() + "\uf8ff");
        options = new FirebaseRecyclerOptions.Builder<Friends>().setQuery(query, Friends.class).build();
        adapter = new FirebaseRecyclerAdapter<Friends, RecSongsViewHolder>(options) {

            @NonNull
            @Override
            public RecSongsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommended_song_view, parent, false);
                return new RecSongsViewHolder(view);
            }
            String songName = "no";
            String txt = "Song : ";
            @Override
            protected void onBindViewHolder(@NonNull RecSongsViewHolder holder, int position, @NonNull Friends model) {
                String id = getRef(position).getKey().toString();
                // data

                mUserRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User userProfile = snapshot.getValue(User.class);
                        if(userProfile != null){
                            mRecRef.child(mUser.getUid()).child(id).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists() && snapshot.child("Song Name") != null){
                                        songName = snapshot.child("Song Name").getValue().toString();
                                        holder.song_name.setText(txt + songName +"\nRecommended By : " + userProfile.name);
                                        if(holder.DP != null && !holder.DP.equals(""))Picasso.get().load(userProfile.URL).into(holder.DP);
                                        Toast.makeText(getApplicationContext(), songName, Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
//                    Toast.makeText(getContext(), "Success retrieving data", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Error retrieving data", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Error in database!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        adapter.startListening();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RecommendationsViewActivity.this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recommendationsRecycler.getContext(),
                ((LinearLayoutManager) layoutManager).getOrientation());
        recommendationsRecycler.addItemDecoration(dividerItemDecoration);
        recommendationsRecycler.setAdapter(adapter);
    }
}