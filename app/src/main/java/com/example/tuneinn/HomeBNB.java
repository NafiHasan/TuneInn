package com.example.tuneinn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class HomeBNB extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    FrameLayout frameLayout;

    // storing info about current user
    public static CurrentUser currentUser = new CurrentUser();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_bnb);

        bottomNavigationView = findViewById(R.id.homeBNB);
        frameLayout = findViewById(R.id.homeBNBFrameLayout);

        MusicFragment musicFragment = new MusicFragment();
        PartyFragment partyFragment = new PartyFragment();
        FriendsFragment friendsFragment = new FriendsFragment();
        ProfileFragment profileFragment = new ProfileFragment();

        SongPosition.currentSongList = new ArrayList<>();

        setFragment(musicFragment);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.musicBNB:
                        setFragment(musicFragment);
                        return true;

                    case R.id.profileBNB:
                        setFragment(profileFragment);
                        return true;
                    case R.id.partyBNB:
                        setFragment(partyFragment);
                        return true;
                    case R.id.friendsBNB:
                        setFragment(friendsFragment);
                        return true;
                }
                return false;
            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction  = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.homeBNBFrameLayout, fragment);
        fragmentTransaction.commit();
    }
}