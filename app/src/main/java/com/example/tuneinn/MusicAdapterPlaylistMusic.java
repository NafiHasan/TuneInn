package com.example.tuneinn;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;

public class MusicAdapterPlaylistMusic extends RecyclerView.Adapter<MusicAdapterPlaylistMusic.ViewHolder> implements MoveItemCallback.ItemTouchHelperContract
{
    ArrayList<Song> mySongs;
    Context context;
    private final DragListener mAdapter;
    ImageButton optionsButton;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener
    {
        TextView musicFileName;
        ImageView albumArt;
        ImageView handleButton;
        View row;

        ViewHolder(View itemView)
        {
            super(itemView);
            musicFileName = itemView.findViewById(R.id.music_file_name_playlist_recycler);
            albumArt = itemView.findViewById(R.id.music_image_playlist_recycler);
            row = itemView;
            handleButton = itemView.findViewById(R.id.drag_handle_playlist_recycler);
            optionsButton = itemView.findViewById(R.id.playlist_music_options_button);
            optionsButton.setOnClickListener(this);
        }

        public void onClick(View view) {
            showPopUpMenu(view);
        }

        private void showPopUpMenu(View view)
        {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.inflate(R.menu.playlist_song_popup_menu);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId())
            {
                case R.id.popup_delete_song_from_playlist_button:
                    //delete song from playlist
                    return true;

                default:
                    return false;
            }

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
        return new MusicAdapterPlaylistMusic.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = mySongs.get(position);
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
                SongPosition.currentSongPosition = position;
                Intent intent = new Intent(context, MusicPlayerActivity.class);
                intent.putExtra("ABC", mySongs);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
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
            }
        }

        else
        {
            for(int i = fromPosition; i > toPosition; i--)
            {
                Collections.swap(mySongs, i , i-1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelected(RVAdapterH.MyViewHolder myViewHolder) {

    }

    @Override
    public void onRowClear(RVAdapterH.MyViewHolder myViewHolder) {

    }
}
