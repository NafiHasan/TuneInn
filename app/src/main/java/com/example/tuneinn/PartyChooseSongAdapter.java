package com.example.tuneinn;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PartyChooseSongAdapter extends RecyclerView.Adapter<PartyChooseSongAdapter.ViewHolder> {

    ArrayList<Song> songs;
    Context context;

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView songName;
        ViewHolder(View itemView){
            super(itemView);
            songName= itemView.findViewById(R.id.music_file_name);
        }
    }

    public PartyChooseSongAdapter(ArrayList<Song> songs, Context context) {
        this.songs = songs;
        this.context = context;
    }

    @NonNull
    @Override
    public PartyChooseSongAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.song_recycler,parent,false);
        return new PartyChooseSongAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PartyChooseSongAdapter.ViewHolder holder, int position) {
        Song song= songs.get(position);
        holder.songName.setText(song.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson= new Gson();
                String json = gson.toJson(song);
                json += "2";
                Toast.makeText(context, song.getTitle()+" to add", Toast.LENGTH_SHORT).show();
                PartyInfo.addSong(song);
                ExecutorService executor= Executors.newSingleThreadExecutor();
                String finalJson = json;
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if(PartyInfo.isHost){
                            PartyInfo.partyLan.serverClass.write(finalJson.getBytes());
                        }
                        else if(!PartyInfo.isHost){
                            PartyInfo.partyLan.clientClass.write(finalJson.getBytes());
                        }
                    }
                });

//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }


}
