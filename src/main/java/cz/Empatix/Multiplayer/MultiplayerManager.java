package cz.Empatix.Multiplayer;

import cz.Empatix.Gamestates.GameStateManager;

public class MultiplayerManager {

    private static MultiplayerManager multiplayerManager;

    public PacketHolder packetHolder;
    public GameClient client;
    public GameServer server;

    private boolean host;
    private String username;
    private int idConnection;

    public static boolean multiplayer = false;

    public static final int TICKS = 60;

    public static MultiplayerManager getInstance(){ return multiplayerManager;}
    public MultiplayerManager(boolean host, GameStateManager gsm, String ip) {
        multiplayerManager = this;
        multiplayer = true;
        packetHolder = new PacketHolder();
        this.host = host;
        if(host) {
            server = new GameServer();
        }

        if(client == null)client = new GameClient(gsm,ip);

    }
    public MultiplayerManager(){
        multiplayerManager = this;

        client = new GameClient();
    }

    public String getUsername() {
        return username;
    }

    public boolean isHost() {
        return host;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isNotConnected(){
        return !client.getClient().isConnected();
    }

    public void close(){
        if(!multiplayer) return;
        multiplayer = false;
        if(isHost()) {
            server.close();
            server = null;
        }
        client.close();
        client = null;
        packetHolder = null;
    }

    public void setIdConnection(int id) {
        idConnection = id;
    }

    /**
     * returns connection id of this current player, useful for receiving packets from server
     * @return connectin id from KryoNet
     */
    public int getIdConnection() {
        return idConnection;
    }
}
