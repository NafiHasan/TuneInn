package com.example.tuneinn;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder>
{
    ArrayList<Song> mySongs;
    Context context;
    Boolean isHost;

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView musicFileName;
        ImageView albumArt;
        ViewHolder(View itemView)
        {
            super(itemView);
            musicFileName = itemView.findViewById(R.id.music_file_name);
            albumArt = itemView.findViewById(R.id.music_image);
        }
    }

    public MusicAdapter(ArrayList<Song> mySongs, Context context) {
        this.mySongs = mySongs;
        this.context = context;
        this.isHost= true;
    }

    public MusicAdapter(ArrayList<Song> mySongs,Context context,Boolean isHost) {
        this.mySongs = mySongs;
        this.context= context;
        this.isHost= isHost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.song_recycler,parent,false);
        return new MusicAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = mySongs.get(position);
        holder.musicFileName.setText(song.getTitle());

        /*byte[] albumArts = getAlbumArt(song.getData());

        if(albumArts != null)
        {
            Glide.with(context).asBitmap().load(albumArts).into(holder.albumArt);
        }

        else
        {
            Glide.with(context).load(R.drawable.ic_baseline_music_note_24).into(holder.albumArt);
        }*/
        if(isHost) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MusicPlayer.getInstance().reset();
                    SongPosition.currentSongPosition = holder.getAdapterPosition();
                    Intent intent = new Intent(context, MusicPlayerActivity.class);
                    intent.putExtra("ABC", mySongs);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    SongPosition.selectedSongToAdd = holder.getAdapterPosition();
                    Intent intent = new Intent(context, PlaylistSelectionActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    return false;
                }
            });
        }
        else {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SongPosition.currentSongPosition = holder.getAdapterPosition();
                    String json= String.valueOf(SongPosition.currentSongPosition);
                    json += "3";
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
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mySongs.size();
    }

    /*private byte[] getAlbumArt(String uri)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] albumArt = retriever.getEmbeddedPicture();
        retriever.release();
        return albumArt;
    }*/
}