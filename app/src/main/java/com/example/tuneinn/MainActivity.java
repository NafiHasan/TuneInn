package com.example.tuneinn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DatabaseReference mRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {        //
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadSharedPreferenceData();
        PartyInfo.init();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference().child("Users");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mUser != null){
                    mRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                startActivity(new Intent(MainActivity.this, HomeBNB.class));
                                finish();
                            }else {
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }, 1000);

    }

    private void loadSharedPreferenceData() {
        SharedPreferences sharedPreferences= getSharedPreferences("Playlists Details", Context.MODE_PRIVATE);
        if(sharedPreferences.contains("Created Playlists")){
            Gson gson = new Gson();
            String json = sharedPreferences.getString("Created Playlists", "");
            //PlaylistInfo.allPlaylists = gson.fromJson(json, (Type) Playlist.class);
            Type type = new TypeToken<ArrayList< Playlist >>() {}.getType();
            PlaylistInfo.allPlaylists= new Gson().fromJson(json, type);
        }
        else {
            PlaylistInfo.allPlaylists= new ArrayList<>();
        }
    }

}