package com.example.tuneinn;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;

public class MusicAdapterPlaylistMusic extends RecyclerView.Adapter<MusicAdapterPlaylistMusic.ViewHolder> implements MoveItemCallback.ItemTouchHelperContract
{
    ArrayList<Song> mySongs;
    Context context;
    private final DragListener mAdapter;
    ImageButton optionsButton;

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView musicFileName;
        ImageView albumArt;
        ImageView handleButton;
        ImageButton optionsButton;
        View row;

        ViewHolder(View itemView)
        {
            super(itemView);
            musicFileName = itemView.findViewById(R.id.music_file_name_playlist_recycler);
            albumArt = itemView.findViewById(R.id.music_image_playlist_recycler);
            row = itemView;
            handleButton = itemView.findViewById(R.id.drag_handle_playlist_recycler);
            optionsButton = itemView.findViewById(R.id.playlist_music_options_button);
        }
    }

    public MusicAdapterPlaylistMusic(ArrayList<Song> mySongs, Context context, DragListener Adapter) {
        this.mySongs = mySongs;
        this.context = context;
        mAdapter = Adapter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.song_recycler_playlist,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = mySongs.get(holder.getAdapterPosition());
        holder.musicFileName.setText(song.getTitle());

        byte[] albumArts = getAlbumArt(song.getData());

        if(albumArts != null)
        {
            Glide.with(context).asBitmap().load(albumArts).into(holder.albumArt);
        }

        else
        {
            Glide.with(context).load(R.drawable.ic_baseline_music_note_24).into(holder.albumArt);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayer.getInstance().reset();
                SongPosition.listType = 2;
                SongPosition.playlistNo = PlaylistInfo.currentPlaylistPosition;
                SongPosition.currentSongPosition = holder.getAdapterPosition();
                Intent intent = new Intent(context, MusicPlayerActivity.class);
                intent.putExtra("ABC", mySongs);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        holder.optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index= holder.getAdapterPosition();
                Toast.makeText(context, index + " " + PlaylistInfo.currentPlaylistPosition, Toast.LENGTH_SHORT).show();
                PlaylistInfo.allPlaylists.get(PlaylistInfo.currentPlaylistPosition).songs.remove(index);
                SharedPreferences sharedPreferences= context.getSharedPreferences("Playlists Details", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor= sharedPreferences.edit();
                Gson gson = new Gson();
                String json = gson.toJson(PlaylistInfo.allPlaylists);
                editor.putString("Created Playlists", json);
                editor.commit();

                if(PlaylistInfo.currentPlaylistPosition == SongPosition.playlistNo)
                {
                    SongPosition.currentSongList = PlaylistInfo.allPlaylists.get(PlaylistInfo.currentPlaylistPosition).songs;

                    if(index == SongPosition.currentSongPosition)
                    {
                        SongPosition.currentlyPLayingSong = null;
                        SongPosition.currentSongName = "No Song Playing";
                        SongPosition.currentArt = null;

                        ((Activity)context).finish();
                        context.startActivity(((Activity) context).getIntent());

                        SongPosition.currentSongPosition = -1;
                    }
                }

                if(index < SongPosition.currentSongPosition)SongPosition.currentSongPosition -=1;

                notifyItemRemoved(index);
            }
        });

        /*holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                SongPosition.selectedSongToAdd= holder.getAdapterPosition();
                Intent intent= new Intent(context,PlaylistSelectionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return false;
            }
        });*/

        holder.handleButton.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN)
            {
                mAdapter.requestDrag(holder);
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return mySongs.size();
    }

    private byte[] getAlbumArt(String uri)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] albumArt = retriever.getEmbeddedPicture();
        retriever.release();
        return albumArt;
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if(fromPosition < toPosition)
        {
            for(int i = fromPosition; i < toPosition; i++)
            {
                Collections.swap(mySongs, i, i+1);
                //Collections.swap(PlaylistInfo.allPlaylists.get(PlaylistInfo.currentPlaylistPosition).songs,i,i+1);
                //Log.i("AB", String.valueOf(PlaylistInfo.currentPlaylistPosition));
                if(PlaylistInfo.currentPlaylistPosition == SongPosition.playlistNo)Collections.swap(SongPosition.currentSongList, i, i+1);
            }
        }

        else
        {
            for(int i = fromPosition; i > toPosition; i--)
            {
                Collections.swap(mySongs, i , i-1);
                //Collections.swap(PlaylistInfo.allPlaylists.get(PlaylistInfo.currentPlaylistPosition).songs,i,i-1);
                if(PlaylistInfo.currentPlaylistPosition == SongPosition.playlistNo)Collections.swap(SongPosition.currentSongList, i, i-1);
            }
        }
        if(PlaylistInfo.currentPlaylistPosition == SongPosition.playlistNo)
        {
            Log.i("ABC", String.valueOf(SongPosition.currentSongPosition) + " " + String.valueOf(fromPosition) + " " + String.valueOf(toPosition));
            if(SongPosition.currentSongPosition == fromPosition)SongPosition.currentSongPosition= toPosition;
            else if(SongPosition.currentSongPosition == toPosition)SongPosition.currentSongPosition = fromPosition;
        }
        Log.i("ABC", String.valueOf(SongPosition.currentSongPosition));
        SharedPreferences sharedPreferences= context.getSharedPreferences("Playlists Details", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(PlaylistInfo.allPlaylists);
        editor.putString("Created Playlists", json);
        editor.commit();
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelected(RVAdapterH.MyViewHolder myViewHolder) {

    }

    @Override
    public void onRowClear(RVAdapterH.MyViewHolder myViewHolder) {

    }
}
