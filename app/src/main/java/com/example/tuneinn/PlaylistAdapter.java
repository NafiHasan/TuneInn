package com.example.tuneinn;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    ArrayList<Playlist> playlists;
    Context context;

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView playlistName;
        ViewHolder(View itemView){
            super(itemView);
            playlistName= itemView.findViewById(R.id.playlist_name);
        }
    }

    public PlaylistAdapter(ArrayList<Playlist> playlists, Context context) {
        this.playlists = playlists;
        this.context = context;
    }

    @NonNull
    @Override
    public PlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.playlist_recycler,parent,false);
        return new PlaylistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.ViewHolder holder, int position) {
        Playlist playlist= playlists.get(position);
        ArrayList<Song> songs = playlist.getSongs();
        holder.playlistName.setText(playlist.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaylistInfo.currentPlaylistPosition= holder.getAdapterPosition();
                Intent intent = new Intent(context, MusicActivity.class);
                //intent.putExtra("ABC", mySongs);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                //RecyclerView songListRecyclerView = findViewById(R.id.songs_recycler_view);
                /*MusicPlayer.getInstance().reset();
                SongPosition.currentSongPosition = position;
                Intent intent = new Intent(context, MusicPlayerActivity.class);
                intent.putExtra("ABC", mySongs);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }


}
