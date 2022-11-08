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
import com.example.tuneinn.friendsPackage.SongInfo;
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

import org.w3c.dom.ls.LSOutput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class RecommendationsViewActivity extends AppCompatActivity {


    private RecyclerView recommendationsRecycler;
    private TextView song_name;


    FirebaseRecyclerOptions<SongInfo> options;
    FirebaseRecyclerOptions<User> options2;
    FirebaseRecyclerAdapter<SongInfo, RecSongsViewHolder> adapter;

    private DatabaseReference mUserRef, mRecRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations_view);

        setTitle("Song Recommendations");

//        song_name = findViewById(R.id.song_name);
        recommendationsRecycler = findViewById(R.id.recommendationsRecycler);
        recommendationsRecycler.setLayoutManager(new LinearLayoutManager(this));

        String userID = getIntent().getStringExtra("userID");
        String curUser = getIntent().getStringExtra("curUser");

        // database variables

        mUserRef = FirebaseDatabase.getInstance().getReference("Users");
        mRecRef = FirebaseDatabase.getInstance().getReference().child("Recommendations");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        LoadFriends("", userID, curUser);

    }
    private void LoadFriends(String s, String user1, String user2) {
        HashMap<String, Integer> check = new HashMap<String, Integer>();
        Query query = mRecRef.child(user1).child(user2).orderByChild("SongName").startAt(s.toLowerCase()).endAt(s.toLowerCase() + "\uf8ff");
        options = new FirebaseRecyclerOptions.Builder<SongInfo>().setQuery(query, SongInfo.class).build();
        adapter = new FirebaseRecyclerAdapter<SongInfo, RecSongsViewHolder>(options) {
            @NonNull
            @Override
            public RecSongsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommended_song_view, parent, false);
                return new RecSongsViewHolder(view);
            }
            @Override
            protected void onBindViewHolder(@NonNull RecSongsViewHolder holder, int position, @NonNull SongInfo model) {
                if(!Objects.equals(check.get(model.getSongName()), (Integer) 1)){
                    holder.song_name.setText(model.getSongName());
                    check.put(model.getSongName(), 1);
                }
                else {
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }
            }
        };

        adapter.startListening();
        LinearLayoutManager layoutManager = new LinearLayoutManager(RecommendationsViewActivity.this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recommendationsRecycler.getContext(),
                layoutManager.getOrientation());
        recommendationsRecycler.addItemDecoration(dividerItemDecoration);
        recommendationsRecycler.setAdapter(adapter);
    }
}