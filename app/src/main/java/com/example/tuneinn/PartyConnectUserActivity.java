package com.example.tuneinn;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
    Button pb;

    Socket socket;

    ServerClass serverClass;
    ClientClass clientClass;
//    SendReceive sendReceive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        PartyInfo.partyLan= PartyConnectUserActivity.this;

        // set title of the page
        setTitle("Party");

        pb= findViewById(R.id.playlistButton);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);

        recyclerView= findViewById(R.id.songs_recycler_view);

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
        pb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        });
    }


    WifiP2pManager.ConnectionInfoListener connectionInfoListener= new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            final InetAddress groupOwnerAddress= wifiP2pInfo.groupOwnerAddress;
            if(wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner){

                pb.setText("Host");
                PartyInfo.isHost= true;
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
                pb.setText("Receiver");
                PartyInfo.isHost= false;

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
                    Toast.makeText(PartyConnectUserActivity.this, "!!!", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(PartyConnectUserActivity.this, tmpMsg, Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(PartyConnectUserActivity.this, song.getTitle(), Toast.LENGTH_SHORT).show();
                                            PartyInfo.addSong(song);
                                        }
                                        else if(check=='3'){
                                            json = json.substring(0,json.length()-1);
                                            Integer position= Integer.valueOf(json);
                                            MusicPlayer.getInstance().reset();
                                            SongPosition.currentSongPosition = position;
                                            Intent intent = new Intent(getApplicationContext(), MusicPlayerActivity.class);
                                            intent.putExtra("ABC", PartyInfo.songs);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
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
        }

        public void write(byte[] bytes){
            try {
                if(outputStream==null){
                    if(socket==null)return;
                    outputStream= socket.getOutputStream();
                    Toast.makeText(PartyConnectUserActivity.this, "!!!", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(PartyConnectUserActivity.this, tmpMsg, Toast.LENGTH_SHORT).show();
                                        Gson gson= new Gson();
                                        String json= tmpMsg;
                                        char check= json.charAt(json.length()-1);
                                        Toast.makeText(PartyConnectUserActivity.this, "The check is "+check, Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(PartyConnectUserActivity.this, song.getTitle(), Toast.LENGTH_SHORT).show();
                                            PartyInfo.addSong(song);
                                        }
                                        else if(check=='3'){
                                            json = json.substring(0,json.length()-1);
                                            Integer position= Integer.valueOf(json);
                                            MusicPlayer.getInstance().reset();
                                            SongPosition.currentSongPosition = position;
                                            Intent intent = new Intent(getApplicationContext(), MusicPlayerActivity.class);
                                            intent.putExtra("ABC", PartyInfo.songs);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
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
//
//    public class SendTask extends AsyncTask<Void,Void,Void> {
//        String message;
//
//        SendTask(String msg){
//            message=msg;
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            sendReceive.write(message.getBytes());
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void unused) {
//            super.onPostExecute(unused);
//        }
//    }
}
