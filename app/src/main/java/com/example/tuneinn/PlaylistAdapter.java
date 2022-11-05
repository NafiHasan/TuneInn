package com.example.tuneinn;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    ArrayList<Playlist> playlists;
    Context context;


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView playlistName;
        ImageButton optionsButton;
        ViewHolder(View itemView){
            super(itemView);
            playlistName= itemView.findViewById(R.id.playlist_name);
            optionsButton = itemView.findViewById(R.id.playlist_options_button);
        }


    }

    public PlaylistAdapter(ArrayList<Playlist> playlists, Context context) {
        this.playlists = playlists;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.playlist_recycler,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Playlist playlist= playlists.get(position);
        ArrayList<Song> songs = playlist.getSongs();
        holder.playlistName.setText(playlist.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaylistInfo.currentPlaylistPosition= holder.getAdapterPosition();
                Intent intent = new Intent(context, MusicActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        holder.optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index= holder.getAdapterPosition();
                PlaylistInfo.allPlaylists.remove(index);
                SharedPreferences sharedPreferences= context.getSharedPreferences("Playlists Details", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor= sharedPreferences.edit();
                Gson gson = new Gson();
                String json = gson.toJson(PlaylistInfo.allPlaylists);
                editor.putString("Created Playlists", json);
                editor.commit();

                if(PlaylistInfo.currentPlaylistPosition == SongPosition.playlistNo)
                {
                    SongPosition.currentSongList = SongPosition.allSongs;
                    SongPosition.listType = -1;
                    SongPosition.currentSongPosition = 0;
                    SongPosition.currentlyPLayingSong = SongPosition.currentSongList.get(0);

                    ((Activity)context).finish();
                    context.startActivity(((Activity) context).getIntent());
                }

                notifyItemRemoved(index);
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }


}
