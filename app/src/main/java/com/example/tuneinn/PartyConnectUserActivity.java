package com.example.tuneinn;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class PartyConnectUserActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

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
    }

    WifiP2pManager.ConnectionInfoListener connectionInfoListener= new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            final InetAddress groupOwnerAddress= wifiP2pInfo.groupOwnerAddress;
            if(wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner){
                pb.setText("Host");
            }
            else if(wifiP2pInfo.groupFormed){
                pb.setText("Receiver");
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
        Socket socket;
        ServerSocket serverSocket;

        @Override
        public void run(){
            try{
                serverSocket= new ServerSocket(8888);
                socket= serverSocket.accept();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public class ClientClass extends Thread{
        Socket socket;
        String hostAdd;

        public ClientClass(InetAddress hostAddress){
            hostAdd= hostAddress.getHostAddress();
            socket= new Socket();
        }

        @Override
        public void run(){
            try {
                socket.connect(new InetSocketAddress(hostAdd,8888),500);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
