package com.example.tuneinn.friendsPackage;

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

import com.example.tuneinn.R;
import com.example.tuneinn.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class FindFriendActivity extends AppCompatActivity {

    FirebaseRecyclerOptions<User> options;
    FirebaseRecyclerAdapter<User, FindFriendViewHolder> adapter;

    DatabaseReference mUserRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    RecyclerView findFriendRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);

        // set title of the page
        setTitle("Find Friends");
        getSupportActionBar().setIcon(R.drawable.ic_baseline_search_24);



        // database variables
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        findFriendRecycler = findViewById(R.id.friendsRecycler);
        findFriendRecycler.setLayoutManager(new LinearLayoutManager(this));

        LoadUsers("");
    }

    private void LoadUsers(String s) {
        Query query = mUserRef.orderByChild("name").startAt(s).endAt(s + "\uf8ff");
        options = new FirebaseRecyclerOptions.Builder<User>().setQuery(query, User.class).build();
        adapter = new FirebaseRecyclerAdapter<User, FindFriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, int position, @NonNull User model) {
                // remove current user from the friend list
                if(mUser.getUid().equals(getRef(position).getKey().toString())){
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }
                else {
                    if(model.URL != null && !model.URL.equals(""))Picasso.get().load(model.URL).into(holder.userDP);
                    holder.userName.setText(model.name);
                    holder.userGenre.setText(model.genre);
                }

                // clicking user's profile
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(FindFriendActivity.this, FriendViewActivity.class);
                        intent.putExtra("userID", getRef(position).getKey().toString());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_view, parent, false);
                return new FindFriendViewHolder(view);
            }
        };
        LinearLayoutManager layoutManager = new LinearLayoutManager(FindFriendActivity.this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(findFriendRecycler.getContext(),
                layoutManager.getOrientation());
        findFriendRecycler.addItemDecoration(dividerItemDecoration);
        adapter.startListening();
        findFriendRecycler.setAdapter(adapter);

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
                LoadUsers(newText);
                return false;
            }
        });
        return true;
    }
}