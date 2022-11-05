package com.example.tuneinn;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PartyConnectUserAdapter extends RecyclerView.Adapter<PartyConnectUserAdapter.ViewHolder> {
    ArrayList<WifiP2pDevice> peers;
    Context context;
    PartyConnectUserActivity partyConnectUserActivity;

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView musicFileName;
        ViewHolder(View itemView)
        {
            super(itemView);
            musicFileName = itemView.findViewById(R.id.music_file_name_choose_song);
        }
    }

    public PartyConnectUserAdapter(ArrayList<WifiP2pDevice> peers, Context context, PartyConnectUserActivity partyConnectUserActivity) {
        this.peers = peers;
        this.context = context;
        this.partyConnectUserActivity = partyConnectUserActivity;
    }

    @NonNull
    @Override
    public PartyConnectUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.song_recycler_choose_song,parent,false);
        return new PartyConnectUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WifiP2pDevice wifiP2pDevice= peers.get(position);
        holder.musicFileName.setText(wifiP2pDevice.deviceName);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final  WifiP2pDevice device= peers.get(holder.getAdapterPosition());
                WifiP2pConfig config= new WifiP2pConfig();

                config.deviceAddress= device.deviceAddress;
                partyConnectUserActivity.manager.connect(partyConnectUserActivity.channel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(context, "Connexted to "+device.deviceName, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i) {
                        Toast.makeText(context, "Failed to connect to "+device.deviceName, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    @Override
    public int getItemCount() {
        return peers.size();
    }
}
