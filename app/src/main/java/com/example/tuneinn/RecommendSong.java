package com.example.tuneinn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.HashMap;

public class RecommendSong extends AppCompatActivity {

    private RecyclerView friendsRecycler;


    FirebaseRecyclerOptions<Friends> options;
    FirebaseRecyclerAdapter<Friends, FriendsViewHolder> adapter;

    private DatabaseReference mUserRef, mFrndRef, mRecSongRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String songName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_song);

        // set title of the page
        setTitle("Recommend to a Friend");
        getSupportActionBar().setIcon(R.drawable.ic_baseline_search_24);

        songName = getIntent().getStringExtra("songName");

        friendsRecycler = findViewById(R.id.friendsRecycler);
        friendsRecycler.setLayoutManager(new LinearLayoutManager(this));


        // database variables

        mUserRef = FirebaseDatabase.getInstance().getReference("Users");
        mFrndRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        mRecSongRef = FirebaseDatabase.getInstance().getReference().child("Recommendations");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        LoadFriends("");
    }


    private void LoadFriends(String s) {
        Query query = mFrndRef.child(mUser.getUid()).orderByChild("name").startAt(s.toLowerCase()).endAt(s.toLowerCase() + "\uf8ff");
        options = new FirebaseRecyclerOptions.Builder<Friends>().setQuery(query, Friends.class).build();
        adapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {

            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_view, parent, false);
                return new FriendsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull FriendsViewHolder holder, int position, @NonNull Friends model) {
                String id = getRef(position).getKey().toString();
                // data
                final User[] userProfile = new User[1];
                mUserRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userProfile[0] = snapshot.getValue(User.class);
                        if(userProfile[0] != null){
                            if(userProfile[0].URL != null && !userProfile[0].URL.equals("")){
                                Picasso.get().load(userProfile[0].URL).into(holder.userDP);
                            }
                            if(userProfile[0].name != null)holder.userName.setText(userProfile[0].name);
                            if(userProfile[0].genre != null)holder.userGenre.setText(userProfile[0].genre);
//                            Toast.makeText(getApplicationContext(), "Success retrieving data", Toast.LENGTH_SHORT).show();
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
                // clicking user's profile
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    int cnt;
                    ArrayList<HashMap<String, Object>> getList;
                    @Override
                    public void onClick(View view) {
//                        Intent intent = new Intent(FriendListActivity.this, FriendViewActivity.class);
//                        intent.putExtra("userID", id);
//                        startActivity(intent);
                        HashMap hashMap = new HashMap();
                        hashMap.put("SongName", songName);

                        mRecSongRef.child(id).child(mUser.getUid()).push().updateChildren(hashMap).addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Recommended Song to " + userProfile[0].name, Toast.LENGTH_SHORT)
                                        .show();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Couldn't recommend!", Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });
                        Toast.makeText(getApplicationContext(), "Recommended Song to " + userProfile[0].name, Toast.LENGTH_SHORT)
                                .show();
                    }
                });
            }
        };





        adapter.startListening();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RecommendSong.this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(friendsRecycler.getContext(),
                ((LinearLayoutManager) layoutManager).getOrientation());
        friendsRecycler.addItemDecoration(dividerItemDecoration);
        friendsRecycler.setAdapter(adapter);
    }
    // search action for users
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                LoadFriends(newText);
                return false;
            }
        });
        return true;
    }
}