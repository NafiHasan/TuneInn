package com.example.tuneinn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    Toolbar toolbar;

    DatabaseReference mUserRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    RecyclerView findFriendRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        findFriendRecycler = findViewById(R.id.findFriendRecycler);
        findFriendRecycler.setLayoutManager(new LinearLayoutManager(this));

        LoadUsers("");
    }

    private void LoadUsers(String s) {
        Query query = mUserRef.orderByChild("name").startAt(s).endAt(s + "\uf8ff");
        options = new FirebaseRecyclerOptions.Builder<User>().setQuery(query, User.class).build();
        adapter = new FirebaseRecyclerAdapter<User, FindFriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, int position, @NonNull User model) {
                User all = new User();
                if(model.URL != null)Picasso.get().load(model.URL).into(holder.userDP);
                holder.userName.setText(model.name);
                holder.userGenre.setText(model.genre);
            }

            @NonNull
            @Override
            public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_view, parent, false);
                return new FindFriendViewHolder(view);
            }
        };
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(FindFriendActivity.this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(findFriendRecycler.getContext(),
                ((LinearLayoutManager) layoutManager).getOrientation());
        findFriendRecycler.addItemDecoration(dividerItemDecoration);
        adapter.startListening();
        findFriendRecycler.setAdapter(adapter);

    }
}