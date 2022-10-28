package com.example.tuneinn;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;

public class PlaylistSelectionAdapter extends RecyclerView.Adapter<PlaylistSelectionAdapter.ViewHolder>{
    ArrayList<Playlist> playlists;
    Context context;

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView playlistName;
        ViewHolder(View itemView){
            super(itemView);
            playlistName= itemView.findViewById(R.id.playlist_name);
        }
    }

    public PlaylistSelectionAdapter(ArrayList<Playlist> playlists, Context context) {
        this.playlists = playlists;
        this.context = context;
    }

    @NonNull
    @Override
    public PlaylistSelectionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.playlist_recycler,parent,false);
        return new PlaylistSelectionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistSelectionAdapter.ViewHolder holder, int position) {
        Playlist playlist= playlists.get(position);
        ArrayList<Song> songs = playlist.getSongs();
        holder.playlistName.setText(playlist.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //PlaylistInfo.currentPlaylistPosition= holder.getAdapterPosition();
                //RecyclerView songListRecyclerView = findViewById(R.id.songs_recycler_view);
                /*MusicPlayer.getInstance().reset();
                SongPosition.currentSongPosition = position;
                Intent intent = new Intent(context, MusicPlayerActivity.class);
                intent.putExtra("ABC", mySongs);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);*/
                Song song= SongPosition.allSongs.get(SongPosition.selectedSongToAdd);
                PlaylistInfo.allPlaylists.get(holder.getAdapterPosition()).addSong(song);
                SongPosition.selectedSongToAdd= -1;
                Toast.makeText(context, "Added to playlist "+playlist.getName(), Toast.LENGTH_SHORT).show();

                SharedPreferences sharedPreferences= context.getSharedPreferences("Playlists Details",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor= sharedPreferences.edit();
                Gson gson = new Gson();
                String json = gson.toJson(PlaylistInfo.allPlaylists);
                editor.putString("Created Playlists", json);
                editor.commit();

                Intent intent= new Intent(context,MusicActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }
}
