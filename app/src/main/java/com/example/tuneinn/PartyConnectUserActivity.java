package com.example.tuneinn;

import static com.example.tuneinn.BottomPlayerFragment.albumArt;
import static com.example.tuneinn.BottomPlayerFragment.playPauseButton;
import static com.example.tuneinn.BottomPlayerFragment.songFileName;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.LogRecord;

public class PartyConnectUserActivity extends AppCompatActivity {
    static final int READ_HOST_SONGS = 1;

    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    BroadcastReceiver receiver;
    IntentFilter intentFilter;
    ArrayList<WifiP2pDevice> peers= new ArrayList<android.net.wifi.p2p.WifiP2pDevice>();
    ArrayList<String> peersDeviceNames= new ArrayList<>();
    ArrayList<WifiP2pDevice> peersDevices= new ArrayList<>();

    WifiP2pManager.PeerListListener peerListListener;

    RecyclerView recyclerView;
    //Button pb;
    TextView statusText;

    Socket socket;

    ServerClass serverClass;
    ClientClass clientClass;
//    SendReceive sendReceive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.party_connect);

        PartyInfo.partyLan= PartyConnectUserActivity.this;

        // set title of the page
        setTitle("Party");

//        pb= findViewById(R.id.playlistButton);
        statusText= findViewById(R.id.partyConnectStatusText);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);

        recyclerView= findViewById(R.id.addUserPartyRecycler);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        serverClass= new ServerClass();
        serverClass.start();


        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(PartyConnectUserActivity.this, "Discovery succeeded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(PartyConnectUserActivity.this, "Discovery failed", Toast.LENGTH_SHORT).show();
            }
        });

        peerListListener = new WifiP2pManager.PeerListListener(){
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peerList) {
                if(!peerList.getDeviceList().equals(peers)){
                    peers.clear();
                    peersDevices.clear();
                    peersDeviceNames.clear();
                    peers.addAll(peerList.getDeviceList());

                    for(WifiP2pDevice peer: peerList.getDeviceList()){
                        peersDevices.add(peer);
                        peersDeviceNames.add(peer.deviceName);
                    }

                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclerView.setAdapter(new PartyConnectUserAdapter(peersDevices, getApplicationContext(), PartyConnectUserActivity.this ));
                }

                if(peerList.getDeviceList().size()==0){
                    Toast.makeText(PartyConnectUserActivity.this, "No devices available", Toast.LENGTH_SHORT).show();
                }
            }
        };

        PartyInfo.handler = new Handler();

        PartyInfo.handler.postDelayed(runnable,2000);
    }

    public final Runnable runnable = new Runnable()
    {
        public void run()

        {
            //Toast.makeText(refresh.this,"in runnable",Toast.LENGTH_SHORT).show();
            if(PartyInfo.isPlayer){
                ExecutorService executor= Executors.newSingleThreadExecutor();
                PartyInfo.hostSongs= SongPosition.allSongs;
                PartyInfo.isPlayer= true;
                Gson gson = new Gson();
                String json = gson.toJson(SongPosition.allSongs);
                json += "1";
                String msg= json;
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if(msg != null && PartyInfo.isHost && serverClass!=null){
                            serverClass.write(msg.getBytes());
                        }
                        else if(msg != null && !PartyInfo.isHost && clientClass!=null){
                            clientClass.write(msg.getBytes());
                        }
                    }
                });
            }
            if(PartyInfo.isPlayer){
                ExecutorService executor= Executors.newSingleThreadExecutor();
                PartyInfo.hostSongs= SongPosition.allSongs;
                PartyInfo.isPlayer= true;
                Gson gson = new Gson();
                String json = gson.toJson(PartyInfo.songs);
                json += "9";
                String msg= json;
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if(msg != null && PartyInfo.isHost && serverClass!=null){
                            serverClass.write(msg.getBytes());
                        }
                        else if(msg != null && !PartyInfo.isHost && clientClass!=null){
                            clientClass.write(msg.getBytes());
                        }
                    }
                });
            }
            PartyInfo.handler.postDelayed(runnable, 2000);
        }

    };


    WifiP2pManager.ConnectionInfoListener connectionInfoListener= new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            final InetAddress groupOwnerAddress= wifiP2pInfo.groupOwnerAddress;
            if(wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner){

                statusText.setText("Connection Status: Host");
                PartyInfo.isHost= true;
                PartyInfo.isConnected=true;
                PartyInfo.hostSongs= SongPosition.allSongs;

                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else if(wifiP2pInfo.groupFormed){
                clientClass= new ClientClass(groupOwnerAddress);
                clientClass.start();
                statusText.setText("Connection Status: Client");
                PartyInfo.isHost= false;
                PartyInfo.isConnected= true;

                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    public class ServerClass extends Thread{
        ServerSocket serverSocket;
        InputStream inputStream;
        OutputStream outputStream;

        public void write(byte[] bytes){
            try {
                if (outputStream==null){
                    if(socket==null)return;
                    outputStream= socket.getOutputStream();
                    //Toast.makeText(PartyConnectUserActivity.this, "!!!", Toast.LENGTH_SHORT).show();
                }
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run(){
            try{
                serverSocket= new ServerSocket(8888);
                socket= serverSocket.accept();
                inputStream= socket.getInputStream();
                outputStream= socket.getOutputStream();
            } catch (IOException e){
                e.printStackTrace();
            }

            ExecutorService executor= Executors.newSingleThreadExecutor();
            Handler handler= new Handler(Looper.getMainLooper());

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    byte[] buffer= new byte[1024];
                    int bytes;

                    while (socket != null){
                        try {
                            if(inputStream==null)break;
                            bytes= inputStream.read(buffer);
                            if(bytes>0){
                                int finalBytes= bytes;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        String tmpMsg= new String(buffer,0,finalBytes);
                                        //Toast.makeText(PartyConnectUserActivity.this, tmpMsg, Toast.LENGTH_SHORT).show();
                                        Gson gson= new Gson();
                                        String json= tmpMsg;
                                        char check= json.charAt(json.length()-1);
                                        if(check=='1') {
                                            json = json.substring(0, json.length() - 1);
                                            Type type = new TypeToken<ArrayList<Song>>() {
                                            }.getType();
                                            PartyInfo.hostSongs = new Gson().fromJson(json, type);
                                            PartyInfo.isPlayer=false;
                                        }
                                        else if(check=='2'){
                                            json = json.substring(0, json.length() - 1);
                                            Song song= gson.fromJson(json,Song.class);
                                            //Toast.makeText(PartyConnectUserActivity.this, song.getTitle(), Toast.LENGTH_SHORT).show();
                                            PartyInfo.addSong(song);
                                        }
                                        else if(check=='3'){
                                            json = json.substring(0,json.length()-1);
                                            Integer position= Integer.valueOf(json);
                                            MusicPlayer.getInstance().reset();
                                            SongPosition.currentSongPosition = position;
                                            Intent intent = new Intent(getApplicationContext(), MusicPlayerActivity.class);
                                            intent.putExtra("ABC", PartyInfo.songs);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                        }
                                        else if(check=='9'){
                                            json = json.substring(0, json.length() - 1);
                                            Type type = new TypeToken<ArrayList<Song>>() {
                                            }.getType();
                                            PartyInfo.songs = new Gson().fromJson(json, type);
                                            PartyInfo.isPlayer=false;
                                        }
                                        else if(check=='?'){
                                            json = json.substring(0,json.length()-1);
                                            MediaPlayer musicPlayer = MusicPlayer.getInstance();
                                            if(musicPlayer.isPlaying())musicPlayer.pause();
                                            else musicPlayer.start();

                                        }
                                        else if(check=='!'){
                                            json = json.substring(0,json.length()-1);
                                            MediaPlayer musicPlayer = MusicPlayer.getInstance();
                                            if(musicPlayer.isPlaying())musicPlayer.pause();
                                            else musicPlayer.start();

                                        }
                                        else if(check=='^'){
                                            playNextSong();
                                        }
                                        else if(check=='*'){
                                            playPrevSong();
                                        }
                                    }
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
    }

    public class ClientClass extends Thread{
        String hostAdd;
        InputStream inputStream;
        OutputStream outputStream;

        public ClientClass(InetAddress hostAddress){
            hostAdd= hostAddress.getHostAddress();
            socket= new Socket();
            //Toast.makeText(PartyConnectUserActivity.this, "Client class created", Toast.LENGTH_SHORT).show();
        }

        public void write(byte[] bytes){
            try {
                if(outputStream==null){
                    if(socket==null)return;
                    outputStream= socket.getOutputStream();
                    //Toast.makeText(PartyConnectUserActivity.this, "!!!", Toast.LENGTH_SHORT).show();
                }
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run(){
            try {
                socket.connect(new InetSocketAddress(hostAdd,8888),500);
                //Toast.makeText(PartyConnectUserActivity.this, "Socket connected to "+hostAdd, Toast.LENGTH_SHORT).show();
                inputStream= socket.getInputStream();
                outputStream= socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            ExecutorService executor= Executors.newSingleThreadExecutor();
            Handler handler= new Handler(Looper.getMainLooper());

            executor.execute(new Runnable() {
                @Override
                public void run() {
                    byte[] buffer= new byte[1024];
                    int bytes;

                    while (socket != null){
                        try {
                            if(inputStream==null)break;
                            bytes= inputStream.read(buffer);
                            if(bytes>0){
                                int finalbytes= bytes;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        String tmpMsg= new String(buffer,0,finalbytes);
                                        //Toast.makeText(PartyConnectUserActivity.this, tmpMsg, Toast.LENGTH_SHORT).show();
                                        Gson gson= new Gson();
                                        String json= tmpMsg;
                                        char check= json.charAt(json.length()-1);
                                        //Toast.makeText(PartyConnectUserActivity.this, "The check is "+check, Toast.LENGTH_SHORT).show();
                                        if(check=='1') {
                                            json = json.substring(0, json.length() - 1);
                                            Type type = new TypeToken<ArrayList<Song>>() {
                                            }.getType();
                                            PartyInfo.hostSongs = new Gson().fromJson(json, type);
                                            PartyInfo.isPlayer=false;
                                        }
                                        else if(check=='2'){
                                            json = json.substring(0, json.length() - 1);
                                            Song song= gson.fromJson(json,(Type)Song.class);
                                            //Toast.makeText(PartyConnectUserActivity.this, song.getTitle(), Toast.LENGTH_SHORT).show();
                                            PartyInfo.addSong(song);
                                        }
                                        else if(check=='3'){
                                            json = json.substring(0,json.length()-1);
                                            Integer position= Integer.valueOf(json);
                                            MusicPlayer.getInstance().reset();
                                            SongPosition.currentSongPosition = position;
                                            Intent intent = new Intent(getApplicationContext(), MusicPlayerActivity.class);
                                            intent.putExtra("ABC", PartyInfo.songs);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                        }
                                        else if(check=='9'){
                                            json = json.substring(0, json.length() - 1);
                                            Type type = new TypeToken<ArrayList<Song>>() {
                                            }.getType();
                                            PartyInfo.songs = new Gson().fromJson(json, type);
                                            PartyInfo.isPlayer=false;
                                        }
                                        else if(check=='?'){
                                            json = json.substring(0,json.length()-1);
                                            MediaPlayer musicPlayer = MusicPlayer.getInstance();
                                            musicPlayer.start();

                                        }
                                        else if(check=='!'){
                                            json = json.substring(0,json.length()-1);
                                            MediaPlayer musicPlayer = MusicPlayer.getInstance();
                                            if(musicPlayer.isPlaying())musicPlayer.pause();
                                            else musicPlayer.start();

                                        }
                                        else if(check=='^'){
                                            playNextSong();
                                        }
                                        else if(check=='*'){
                                            playPrevSong();
                                        }
                                        else if(check=='4'){
                                            json = json.substring(0,json.length()-1);
                                            Song song= gson.fromJson(json,(Type)Song.class);
                                            File file= new File(song.getData());
                                            ContentResolver cr = getContentResolver();
                                            InputStream inputStream1=new InputStream() {
                                                @Override
                                                public int read() throws IOException {
                                                    return 0;
                                                }
                                            };
                                            try {
                                                inputStream1 = cr.openInputStream(Uri.parse(song.getData()));
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                            }
                                            int len=0;
                                            while (true) {
                                                try {
                                                    if (!((len = inputStream1.read(buffer)) != -1))
                                                        break;
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                try {
                                                    outputStream.write(buffer, 0, len);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                        else {
                                            final File f = new File(Environment.getExternalStorageDirectory() + "/"
                                                    + getPackageName() + "/tuneinnshared-" + System.currentTimeMillis()
                                                    + ".mp3");

                                            File dirs = new File(f.getParent());
                                            if (!dirs.exists())
                                                dirs.mkdirs();
                                            try {
                                                f.createNewFile();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            try {
                                                copyInputStreamToFile(inputStream, f);
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

    }

    private void playNextSong()
    {
        if(SongPosition.currentSongList.size() == 0)return;
        else if(SongPosition.currentSongPosition  == SongPosition.currentSongList.size() - 1)
        {
            return;
        }
        else
        {
            SongPosition.currentSongPosition += 1;
            MediaPlayer musicPlayer= MusicPlayer.getInstance();
            musicPlayer.reset();
            SongPosition.currentlyPLayingSong = SongPosition.currentSongList.get(SongPosition.currentSongPosition);
            songFileName.setText(SongPosition.currentlyPLayingSong.getTitle());
            try {
                musicPlayer.setDataSource(SongPosition.currentlyPLayingSong.getData());
                musicPlayer.prepare();
                musicPlayer.start();
                playPauseButton.setImageResource(R.drawable.pause_circle);


            } catch (IOException e) {

            }

           /* byte[] arts = getAlbumArt(SongPosition.currentlyPLayingSong.getData());
            SongPosition.currentArt = arts;

            if(arts != null)
            {
                Glide.with(this).asBitmap().load(arts).into(albumArt);
                //Glide.with(this).asBitmap().load(SongPosition.currentArt).into(BottomPlayerFragment.albumArt);
                SongPosition.currentArt = arts;
            }

            else
            {
                Glide.with(this).load(R.drawable.ic_baseline_music_note_24).into(albumArt);
                //Glide.with(this).load(R.drawable.ic_baseline_music_note_24).into(BottomPlayerFragment.albumArt);
                SongPosition.currentArt = null;
            }*/

        }
    }

    private byte[] getAlbumArt(String uri)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] albumArt = retriever.getEmbeddedPicture();
        retriever.release();
        return albumArt;
    }

    private void playPrevSong()
    {
        if(SongPosition.currentSongList.size() == 0)return;
        else if(SongPosition.currentSongPosition == 0)
        {
            return;
        }
        else
        {
            SongPosition.currentSongPosition -= 1;
            MediaPlayer musicPlayer= MusicPlayer.getInstance();
            musicPlayer.reset();
            SongPosition.currentlyPLayingSong = SongPosition.currentSongList.get(SongPosition.currentSongPosition);
            songFileName.setText(SongPosition.currentlyPLayingSong.getTitle());
            try {
                musicPlayer.setDataSource(SongPosition.currentlyPLayingSong.getData());
                musicPlayer.prepare();
                musicPlayer.start();
                playPauseButton.setImageResource(R.drawable.pause_circle);


            } catch (IOException e) {

            }

           /* byte[] arts = getAlbumArt(SongPosition.currentlyPLayingSong.getData());
            SongPosition.currentArt = arts;

            if(arts != null)
            {
                Glide.with(this).asBitmap().load(arts).into(albumArt);
                //Glide.with(this).asBitmap().load(SongPosition.currentArt).into(BottomPlayerFragment.albumArt);
                SongPosition.currentArt = arts;
            }

            else
            {
                Glide.with(this).load(R.drawable.ic_baseline_music_note_24).into(albumArt);
                //Glide.with(this).load(R.drawable.ic_baseline_music_note_24).into(BottomPlayerFragment.albumArt);
                SongPosition.currentArt = null;
            }*/



        }
    }

    public static void copyInputStreamToFile(InputStream inputStream, File file)
            throws IOException {

        // append = false
        try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
            int read;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }

    }
}
