package cz.Empatix.Entity.Enemies;

import cz.Empatix.AudioManager.AudioManager;
import cz.Empatix.AudioManager.Soundtrack;
import cz.Empatix.Entity.Animation;
import cz.Empatix.Entity.Enemies.Projectiles.KingSlimebullet;
import cz.Empatix.Entity.Enemy;
import cz.Empatix.Entity.Player;
import cz.Empatix.Gamestates.Singleplayer.InGame;
import cz.Empatix.Java.Loader;
import cz.Empatix.Java.Random;
import cz.Empatix.Render.Graphics.Model.ModelManager;
import cz.Empatix.Render.Graphics.Shaders.ShaderManager;
import cz.Empatix.Render.Graphics.Sprites.Sprite;
import cz.Empatix.Render.Graphics.Sprites.SpritesheetManager;
import cz.Empatix.Render.Hud.HealthBar;
import cz.Empatix.Render.RoomObjects.Chest;
import cz.Empatix.Render.RoomObjects.DestroyableObject;
import cz.Empatix.Render.RoomObjects.RoomObject;
import cz.Empatix.Render.TileMap;
import org.joml.Vector3f;

import java.util.ArrayList;

public class KingSlime extends Enemy {

    private static final int IDLE = 0;
    private static final int DEAD = 1;

    private boolean shootready;
    private boolean disableDraw;
    private long shootCooldown;

    private long shootCooldownCircle;


    private int angle;
    private boolean chestCreated;

    private final ArrayList<KingSlimebullet> bullets;
    private transient HealthBar healthBar;

    private long directionChangeCooldown;
    private boolean invertDirection;

    public static void load(){
        Loader.loadImage("Textures\\Sprites\\Enemies\\slimeking.tga");
    }
    public KingSlime(TileMap tm, Player player) {
        super(tm,player);

        moveSpeed = 0.6f;
        maxSpeed = 1.6f;
        stopSpeed = 0.5f;

        width = 64;
        height = 48;
        cwidth = 64;
        cheight = 48;
        scale = 5;

        health = maxHealth = (int)(90*(1+(Math.pow(tm.getFloor(),1.5)*0.12)));
        damage = 1;

        type = melee;
        facingRight = true;
        bullets=new ArrayList<>(100);

        spriteSheetCols = 6;
        spriteSheetRows = 2;

        // try to find spritesheet if it was created once
        spritesheet = SpritesheetManager.getSpritesheet("Textures\\Sprites\\Enemies\\slimeking.tga");

        // creating a new spritesheet
        if (spritesheet == null){
            spritesheet = SpritesheetManager.createSpritesheet("Textures\\Sprites\\Enemies\\slimeking.tga");
            Sprite[] sprites = new Sprite[4];
            for(int i = 0; i < sprites.length; i++) {
                Sprite sprite = new Sprite(5,i,0,32,24,spriteSheetRows,spriteSheetCols);
                sprites[i] = sprite;

            }
            spritesheet.addSprites(sprites);

            sprites = new Sprite[6];
            for(int i = 0; i < sprites.length; i++) {
                Sprite sprite = new Sprite(5,i,1,32,24,spriteSheetRows,spriteSheetCols);
                sprites[i] = sprite;


            }
            spritesheet.addSprites(sprites);

        }
        vboVertices = ModelManager.getModel(width,height);
        if (vboVertices == -1){
            vboVertices = ModelManager.createModel(width,height);
        }

        animation = new Animation();
        animation.setFrames(spritesheet.getSprites(IDLE));
        animation.setDelay(175);

        shader = ShaderManager.getShader("shaders\\shader");
        if (shader == null){
            shader = ShaderManager.createShader("shaders\\shader");
        }
        // because of scaling image by 2x
        width *= scale;
        height *= scale;
        cwidth *= scale;
        cheight *= scale;

        angle=0;
        chestCreated=false;

        healthBar = new HealthBar("Textures\\bosshealthbar",new Vector3f(960,1000,0),7,49,3);
        healthBar.initHealth(health,maxHealth);

        createShadow();

        directionChangeCooldown = System.currentTimeMillis()- InGame.deltaPauseTime();
        invertDirection = false;

    }
    // multiplayer
    public KingSlime(TileMap tm, Player[] player) {
        super(tm,player);
        if(tm.isServerSide()){
            moveSpeed = 0.6f;
            maxSpeed = 1.6f;
            stopSpeed = 0.5f;

            width = 64;
            height = 48;
            cwidth = 64;
            cheight = 48;
            scale = 5;

            animation = new Animation(4);
            animation.setDelay(175);

            health = maxHealth = (int)(90*(1+(Math.pow(tm.getFloor(),1.5)*0.12)));
            damage = 1;

            type = melee;
            facingRight = true;
            bullets=new ArrayList<>(100);

            // because of scaling image by 2x
            width *= scale;
            height *= scale;
            cwidth *= scale;
            cheight *= scale;

            angle=0;
            chestCreated=false;

            directionChangeCooldown = System.currentTimeMillis()- InGame.deltaPauseTime();
            invertDirection = false;
        } else  {
            moveSpeed = 0.6f;
            maxSpeed = 1.6f;
            stopSpeed = 0.5f;

            width = 64;
            height = 48;
            cwidth = 64;
            cheight = 48;
            scale = 5;

            health = maxHealth = (int)(90*(1+(Math.pow(tm.getFloor(),1.5)*0.12)));
            damage = 1;

            type = melee;
            facingRight = true;
            bullets=new ArrayList<>(100);

            spriteSheetCols = 6;
            spriteSheetRows = 2;

            // try to find spritesheet if it was created once
            spritesheet = SpritesheetManager.getSpritesheet("Textures\\Sprites\\Enemies\\slimeking.tga");

            // creating a new spritesheet
            if (spritesheet == null){
                spritesheet = SpritesheetManager.createSpritesheet("Textures\\Sprites\\Enemies\\slimeking.tga");
                Sprite[] sprites = new Sprite[4];
                for(int i = 0; i < sprites.length; i++) {
                    Sprite sprite = new Sprite(5,i,0,32,24,spriteSheetRows,spriteSheetCols);
                    sprites[i] = sprite;

                }
                spritesheet.addSprites(sprites);

                sprites = new Sprite[6];
                for(int i = 0; i < sprites.length; i++) {
                    Sprite sprite = new Sprite(5,i,1,32,24,spriteSheetRows,spriteSheetCols);
                    sprites[i] = sprite;


                }
                spritesheet.addSprites(sprites);

            }
            vboVertices = ModelManager.getModel(width,height);
            if (vboVertices == -1){
                vboVertices = ModelManager.createModel(width,height);
            }

            animation = new Animation();
            animation.setFrames(spritesheet.getSprites(IDLE));
            animation.setDelay(175);

            shader = ShaderManager.getShader("shaders\\shader");
            if (shader == null){
                shader = ShaderManager.createShader("shaders\\shader");
            }
            // because of scaling image by 2x
            width *= scale;
            height *= scale;
            cwidth *= scale;
            cheight *= scale;

            angle=0;
            chestCreated=false;

            healthBar = new HealthBar("Textures\\bosshealthbar",new Vector3f(960,1000,0),7,49,3);
            healthBar.initHealth(health,maxHealth);

            createShadow();

            directionChangeCooldown = System.currentTimeMillis()- InGame.deltaPauseTime();
            invertDirection = false;
        }

    }

    @Override
    public void update() {
        setMapPosition();
        healthBar.update(health,maxHealth);
        if(isSpawning()) return;
        // update animation
        animation.update();
        if(isDead() && animation.hasPlayedOnce()){
            disableDraw = true;
            if(!chestCreated){
                chestCreated=true;

                Chest chest = new Chest(tileMap);
                chest.setPosition(position.x,position.y);
                chest.enableDropArtefact();
                tileMap.addObject(chest);

                tileMap.addLadder();
            }
        }

        for(int i = 0;i<bullets.size();i++){
            KingSlimebullet slimebullet = bullets.get(i);
            slimebullet.update();
            for(Player p : player){
                if(p != null){
                    if(slimebullet.intersects(p) && !p.isFlinching() && !p.isDead()){
                        slimebullet.setHit();
                        p.hit(1);
                    }
                }
            }
            if(slimebullet.shouldRemove()) {
                bullets.remove(i);
                i--;
            }
            for(RoomObject object: tileMap.getRoomMapObjects()){
                if(object instanceof DestroyableObject) {
                    if (slimebullet.intersects(object) && !slimebullet.isHit() && !((DestroyableObject) object).isDestroyed()) {
                        slimebullet.setHit();
                        ((DestroyableObject) object).setHit(1);
                    }
                } else if(object.collision && slimebullet.intersects(object)){
                    slimebullet.setHit();
                }
            }
        }

        if(dead) return;

        if((float)health/maxHealth <= 0.5 && System.currentTimeMillis()-shootCooldownCircle- InGame.deltaPauseTime() > 2500){
            shootCooldownCircle = System.currentTimeMillis()- InGame.deltaPauseTime();
            for (int i = 0; i < 5; ) {
                double inaccuracy = 0.055 * i;

                int index = theClosestPlayerIndex();

                KingSlimebullet slimebullet = new KingSlimebullet(
                        tileMap,
                        px[index]-position.x,
                        py[index]-position.y,
                        inaccuracy
                );
                slimebullet.setPosition(position.x, position.y);
                bullets.add(slimebullet);
                if (i >= 0) i++;
                else i--;
                i = -i;
            }
        }

        if(!shootready && System.currentTimeMillis()-shootCooldown- InGame.deltaPauseTime() > 50){
            shootready = true;
            shootCooldown = System.currentTimeMillis()- InGame.deltaPauseTime();
        }
        else if(shootready) {
            shootready = false;


            float offset = Random.nextInt(3) * 0.1f;


            // FLOOR 3 change between time change direction
            if(tileMap.getFloor() >= 2 && System.currentTimeMillis() - directionChangeCooldown - InGame.deltaPauseTime() > 7500){
                directionChangeCooldown = System.currentTimeMillis()- InGame.deltaPauseTime();
                invertDirection = !invertDirection;
            }
            if(invertDirection){
                angle -= 7;
            } else {
                angle += 7;
            }
            if(angle >= 360){
                angle-=360;
            }
            if(angle < 0){
                angle+=360;
            }
            KingSlimebullet slimebullet = new KingSlimebullet(
                    tileMap,
                    Math.sin(Math.toRadians(angle)),
                    Math.sin(Math.toRadians(angle-90)),
                    offset+0.05 *(1-Random.nextInt(2)*2)
            );
            slimebullet.setPosition(position.x, position.y);
            bullets.add(slimebullet);

            slimebullet = new KingSlimebullet(
                    tileMap,
                    Math.sin(Math.toRadians(angle-180)),
                    Math.sin(Math.toRadians(angle-270)),
                    offset+0.05 *(1-Random.nextInt(2)*2)
            );
            slimebullet.setPosition(position.x, position.y);
            bullets.add(slimebullet);


        }
        super.update();
        movePacket();

    }

    public void draw() {

        for(KingSlimebullet bullet : bullets){
            bullet.draw();
        }
        if(!disableDraw){
            super.draw();
        }


    }
    public void drawHud(){
        healthBar.draw();
    }
    @Override
    public void hit(int damage) {
        if(dead || isSpawning()) return;
        lastTimeDamaged=System.currentTimeMillis()- InGame.deltaPauseTime();
        health -= damage;
        if(health < 0) health = 0;
        if(health == 0){
            if(tileMap.isServerSide()){
                animation = new Animation(6);
                animation.setDelay(85);
            } else {
                animation.setFrames(spritesheet.getSprites(DEAD));
                animation.setDelay(85);
            }
            speed.x = 0;
            speed.y = 0;
            dead = true;

            AudioManager.playSoundtrack(Soundtrack.IDLE);

            // deleting all projectiles after death
            for(KingSlimebullet slimebullet : bullets){
                slimebullet.setHit();
            }
        }
    }
    @Override
    public boolean shouldRemove(){
        return animation.hasPlayedOnce() && isDead() && bullets.size()==0;
    }

    @Override
    public void drawShadow() {
        if(!disableDraw) drawShadow(11f);
    }

    @Override
    public void loadSave() {
        super.loadSave();
        width = 64;
        height = 48;

        // try to find spritesheet if it was created once
        spritesheet = SpritesheetManager.getSpritesheet("Textures\\Sprites\\Enemies\\slimeking.tga");

        // creating a new spritesheet
        if (spritesheet == null){
            spritesheet = SpritesheetManager.createSpritesheet("Textures\\Sprites\\Enemies\\slimeking.tga");
            Sprite[] sprites = new Sprite[4];
            for(int i = 0; i < sprites.length; i++) {
                Sprite sprite = new Sprite(5,i,0,32,24,spriteSheetRows,spriteSheetCols);
                sprites[i] = sprite;

            }
            spritesheet.addSprites(sprites);

            sprites = new Sprite[6];
            for(int i = 0; i < sprites.length; i++) {
                Sprite sprite = new Sprite(5,i,1,32,24,spriteSheetRows,spriteSheetCols);
                sprites[i] = sprite;


            }
            spritesheet.addSprites(sprites);

        }
        vboVertices = ModelManager.getModel(width,height);
        if (vboVertices == -1){
            vboVertices = ModelManager.createModel(width,height);
        }

        animation = new Animation();
        animation.setFrames(spritesheet.getSprites(IDLE));
        animation.setDelay(175);

        shader = ShaderManager.getShader("shaders\\shader");
        if (shader == null){
            shader = ShaderManager.createShader("shaders\\shader");
        }
        // because of scaling image by 2x
        width *= scale;
        height *= scale;

        healthBar = new HealthBar("Textures\\bosshealthbar",new Vector3f(960,1000,0),7,49,3);
        healthBar.initHealth(health,maxHealth);

        createShadow();

        for(KingSlimebullet kingSlimebullet : bullets){
            kingSlimebullet.loadSave();
        }

    }
}
