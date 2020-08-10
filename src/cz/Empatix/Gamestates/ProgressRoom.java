package cz.Empatix.Gamestates;

import cz.Empatix.Entity.Player;
import cz.Empatix.Main.Settings;
import cz.Empatix.Render.Camera;
import cz.Empatix.Render.Graphics.Framebuffer;
import cz.Empatix.Render.Hud.Image;
import cz.Empatix.Render.Postprocessing.Lightning.LightManager;
import cz.Empatix.Render.Text.TextRender;
import cz.Empatix.Render.TileMap;
import org.joml.Vector3f;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class ProgressRoom extends GameState {

    private Player player;
    private TileMap tileMap;

    private float mouseX;
    private float mouseY;

    private Framebuffer objectsFramebuffer;

    private LightManager lightManager;

    private cz.Empatix.Render.Hud.Image coin;

    private static boolean enterGame;

    ProgressRoom(GameStateManager gsm){
        this.gsm = gsm;
    }

    @Override
    void init() {

        objectsFramebuffer = new Framebuffer();
        lightManager = new LightManager();

        // Tile map
        tileMap = new TileMap(64);
        tileMap.loadTiles("Textures\\tileset64.tga");

        // player
        // create player object
        player = new Player(tileMap);

        // generate map + create objects which needs item manager & gun manager created
        tileMap.loadProgressRoom();
        // move player to starter room
        player.setPosition(tileMap.getPlayerStartX(), tileMap.getPlayerStartY());
        tileMap.setPosition(
                Camera.getWIDTH() / 2f - player.getX(),
                Camera.getHEIGHT() / 2f - player.getY()
        );
        // make camera move smoothly
        tileMap.setTween(0.1);

        coin = new Image("Textures\\coin.tga",new Vector3f(75,1000,0),1.5f);

    }

    @Override
    void draw() {
        objectsFramebuffer.bindFBO();
        // clear framebuffer
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);

        tileMap.draw();

        tileMap.preDrawObjects();

        player.draw();

        // draw enemies
        tileMap.drawObjects();

        objectsFramebuffer.unbindFBO();

        lightManager.draw(objectsFramebuffer);

        coin.draw();
        TextRender.renderText(""+player.getCoins(),new Vector3f(170,1019,0),3,new Vector3f(1.0f,0.847f,0.0f));


    }
    @Override
    void update() {
        if(enterGame){
            enterGame=false;
            gsm.setState(GameStateManager.INGAME);
            return;
        }
        // loc of mouse
        final Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
        mouseX = mouseLoc.x* Settings.scaleMouseX();
        mouseY = mouseLoc.y*Settings.scaleMouseY();

        // mouse location-moving direction of mouse of tilemap
        tileMap.setPosition(
                tileMap.getX()-(mouseX-960)/30,
                tileMap.getY()-(mouseY- 540)/30);

        // updating player
        // updating tilemap by player position
        tileMap.setPosition(
                Camera.getWIDTH() / 2f - player.getX(),
                Camera.getHEIGHT() / 2f - player.getY()
        );
        tileMap.updateObjects();
        player.update();
        lightManager.update();
    }
    @Override
    void keyPressed(int k) {
        player.keyPressed(k);
        tileMap.keyPressed(k,player);
    }

    @Override
    void keyReleased(int k) {
        player.keyReleased(k);
    }

    @Override
    void mousePressed(int button) {

    }

    @Override
    void mouseReleased(int button) {

    }

    @Override
    void mouseScroll(double x, double y) {

    }

    public static void EnterGame() {
        ProgressRoom.enterGame = true;
    }
}
