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

public class RequestsActivity extends AppCompatActivity {

    FirebaseRecyclerOptions<Friends> options;
    FirebaseRecyclerAdapter<Friends, RequestViewHolder> adapter;

    DatabaseReference mUserRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    RecyclerView requestsRecycler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        // set title of the page
        setTitle("Friend Requests");
        getSupportActionBar().setIcon(R.drawable.ic_baseline_search_24);


        // database variables
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Received Requests");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        requestsRecycler = findViewById(R.id.requestsRecycler);
        requestsRecycler.setLayoutManager(new LinearLayoutManager(this));

        LoadRequests("");
    }

    private void LoadRequests(String s) {
        Query query = mUserRef.child(mUser.getUid()).orderByChild("name").startAt(s).endAt(s + "\uf8ff");
        options = new FirebaseRecyclerOptions.Builder<Friends>().setQuery(query, Friends.class).build();
        adapter = new FirebaseRecyclerAdapter<Friends, RequestViewHolder>(options) {

            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_view, parent, false);
                return new RequestViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull RequestViewHolder holder, int position, @NonNull Friends model) {
                if(model.getURL() != null && !model.getURL().equals(""))Picasso.get().load(model.getURL()).into(holder.userDP);
                if(model.getName() != null)holder.userName.setText(model.getName());
                if(model.getGenre() != null)holder.userGenre.setText(model.getGenre());

                // clicking user's profile
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(RequestsActivity.this, FriendViewActivity.class);
                        intent.putExtra("userID", getRef(position).getKey().toString());
                        startActivity(intent);
                    }
                });
            }
        };
        adapter.startListening();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RequestsActivity.this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requestsRecycler.getContext(),
                ((LinearLayoutManager) layoutManager).getOrientation());
        requestsRecycler.addItemDecoration(dividerItemDecoration);
        requestsRecycler.setAdapter(adapter);
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
                LoadRequests(newText);
                return false;
            }
        });
        return true;
    }
}