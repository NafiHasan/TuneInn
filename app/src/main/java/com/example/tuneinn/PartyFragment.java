package com.example.tuneinn;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Objects;

public class PartyFragment extends Fragment {
    View v;
    WifiManager wifiManager;


    public PartyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_party, container, false);

        Button createParty = v.findViewById(R.id.goToPartyPageButton);

        wifiManager= (WifiManager) requireContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        wifiManager= (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
// change previous line with this line if error occurs
        wifiManager.setWifiEnabled(false);
        wifiManager.setWifiEnabled(true);


        createParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireActivity().getApplicationContext(), PartyActivity.class));
            }
        });

        return v;
    }
}