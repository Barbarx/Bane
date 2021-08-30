package cz.Empatix.Multiplayer;

import cz.Empatix.Gamestates.GameStateManager;
import cz.Empatix.Gamestates.Multiplayer.MultiplayerManager;
import cz.Empatix.Multiplayer.Packets.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class GameServer extends Thread {
    private DatagramSocket socket;
    private GameStateManager gsm;

    private List<PlayerMP> connectedPlayers;

    public GameServer(GameStateManager gsm){
        this.gsm = gsm;
        try {
            this.socket = new DatagramSocket(23333);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        connectedPlayers = new ArrayList<>();

        System.out.println("HOSTING SERVER");
    }

    @Override
    public void run() {
        while(MultiplayerManager.multiplayer){
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data,data.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                continue;
            }
            parseSocket(packet.getData(),packet.getAddress(),packet.getPort());

        }
        socket.close();
    }

    private void parseSocket(byte[] data, InetAddress adress, int port) {
        String message = new String(data).trim();
        Packet.PacketType type = Packet.lookupPacket(message.substring(0,2));
        Packet packet;
        switch (type) {
            case INVALID: { break; }
            case DISCONNECT: {
                packet = new Packet01Disconnect(data);
                System.out.println("[" + adress.getHostAddress() + ":" + port + "]" + ((Packet01Disconnect)(packet)).getUsername()+" has disconnected..");

                removeConnection((Packet01Disconnect) packet);
                break;
            }
            case LOGIN : {
                packet = new Packet00Login(data);
                System.out.println("[" + adress.getHostAddress() + ":" + port + "]" + ((Packet00Login)(packet)).getUsername()+" has connected..");

                PlayerMP playerMP = new PlayerMP(null, adress, port,((Packet00Login)(packet)).getUsername());
                playerMP.remove();
                // removing light source
                addConnection(playerMP, (Packet00Login) packet);

                break;
            }
            case MOVE:{
                packet = new Packet02Move(data);
                handleMove((Packet02Move) packet);
                //System.out.println("[" + adress.getHostAddress() + ":" + port + "]" + ((Packet02Move)(packet)).getUsername()+" has moved to X: "+((Packet02Move) packet).getX()+" Y:"+ ((Packet02Move) packet).getY());

                break;
            }
            case ENTERREADY:{
                packet = new Packet03EnterReady(data);

                System.out.println("[" + adress.getHostAddress() + ":" + port + "]" + "is ready ("+((Packet03EnterReady) packet).getState()+")");

                PlayerMP player = getPlayerMP(((Packet03EnterReady)packet).getUsername());
                if(player != null){
                    packet.writeData(this);
                }

                break;
            }
        }
    }

    public void removeConnection(Packet01Disconnect packet) {
        PlayerMP player = getPlayerMP(packet.getUsername());
        connectedPlayers.remove(player);
        packet.writeData(this);

    }

    public PlayerMP getPlayerMP(String username){
        for(PlayerMP playerMP : connectedPlayers){
            if(playerMP.getUsername().equals(username)) return playerMP;
        }
        return null;
    }

    public void addConnection(PlayerMP player, Packet00Login packet) {
        boolean alreadyConnected = false;
        for(PlayerMP p: this.connectedPlayers){
            // host will be always AlreadyConnected
            if(player.getUsername().equalsIgnoreCase(p.getUsername())){
                if(p.ipAdress == null){
                    p.ipAdress = player.getIpAdress();
                }
                if(p.port == -1){
                    p.port = player.port;
                }
                alreadyConnected = true;
            } else {
                // sending packet of new player to other players
                sendData(packet.getData(),p.ipAdress,p.port);

                // sending packet of other players to new player
                packet = new Packet00Login(p.getUsername());
                sendData(packet.getData(), player.ipAdress,player.port);
            }
        }
        if(!alreadyConnected){
            connectedPlayers.add(player);
        }
    }

    private void handleMove(Packet02Move packet){
        PlayerMP player = getPlayerMP(packet.getUsername());
        if(player != null){
            packet.writeData(this);
        }
    }

    public void sendData(byte[] data, InetAddress ipAdress,int port){
        DatagramPacket packet = new DatagramPacket(data,data.length,ipAdress,port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendDataToAllClients(byte[] data) {
        for(PlayerMP p : connectedPlayers){
            sendData(data,p.getIpAdress(),p.getPort());
        }
    }
}
