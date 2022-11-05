package com.example.tuneinn;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    ArrayList<Playlist> playlists;
    Context context;
    ImageButton optionsButton;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener{
        TextView playlistName;
        ViewHolder(View itemView){
            super(itemView);
            playlistName= itemView.findViewById(R.id.playlist_name);
            optionsButton = itemView.findViewById(R.id.playlist_options_button);
            optionsButton.setOnClickListener(this);
        }

        public void onClick(View view) {
            showPopUpMenu(view);
        }

        private void showPopUpMenu(View view)
        {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.inflate(R.menu.playlist_popup_menu);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId())
            {
                case R.id.popup_delete_playlist_button:
                    //delete playlist
                    return true;

                default:
                    return false;
            }

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
