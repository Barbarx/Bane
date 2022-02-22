package cz.Empatix.Entity.Enemies.Projectiles;

import cz.Empatix.Entity.Animation;
import cz.Empatix.Entity.MapObject;
import cz.Empatix.Gamestates.Multiplayer.MultiplayerManager;
import cz.Empatix.Java.Loader;
import cz.Empatix.Render.Graphics.Model.ModelManager;
import cz.Empatix.Render.Graphics.Shaders.ShaderManager;
import cz.Empatix.Render.Graphics.Sprites.Sprite;
import cz.Empatix.Render.Graphics.Sprites.SpritesheetManager;
import cz.Empatix.Render.Postprocessing.Lightning.LightManager;
import cz.Empatix.Render.TileMap;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class RedSlimebullet extends MapObject {
    public static void load(){
        Loader.loadImage("Textures\\Sprites\\Enemies\\redslimebullet.tga");
    }
    // SPRITE VARS
    private final static int sprites = 0;
    private final static int hitSprites = 1;

    // BASIC VARS
    private boolean hit;
    private boolean remove;

    public RedSlimebullet(TileMap tm, double x, double y, double inaccuracy) {
        super(tm);
        if(tm.isServerSide()){
            facingRight = true;

            width = 16;
            height = 16;

            cwidth = 16;
            cheight = 16;

            scale = 2;

            // load sprites
            spriteSheetCols = 4;
            spriteSheetRows = 2;

            double atan = Math.atan2(y,x) + inaccuracy;
            // 30 - speed of bullet
            speed.x = (float)(Math.cos(atan) * 17.5);
            speed.y = (float)(Math.sin(atan) * 17.5);

            animation = new Animation(4);
            animation.setDelay(70);

            // because of scaling image by 2x
            width *= 2;
            height *= 2;
            cwidth *= 2;
            cheight *= 2;
        } else {
            facingRight = true;

            width = 16;
            height = 16;

            cwidth = 16;
            cheight = 16;

            scale = 2;

            // load sprites
            spriteSheetCols = 4;
            spriteSheetRows = 2;

            double atan = Math.atan2(y,x) + inaccuracy;
            // 30 - speed of bullet
            speed.x = (float)(Math.cos(atan) * 17.5);
            speed.y = (float)(Math.sin(atan) * 17.5);

            // try to find spritesheet if it was created once
            spritesheet = SpritesheetManager.getSpritesheet("Textures\\Sprites\\Enemies\\redslimebullet.tga");

            // creating a new spritesheet
            if (spritesheet == null){
                spritesheet = SpritesheetManager.createSpritesheet("Textures\\Sprites\\Enemies\\redslimebullet.tga");

                Sprite[] images = new Sprite[4];

                for(int i = 0; i < images.length; i++) {
                    float[] texCoords =
                            {
                                    (float)i/spriteSheetCols,0,

                                    (float)i/spriteSheetCols,1.0f/spriteSheetRows,

                                    (i+1.0f)/spriteSheetCols,1.0f/spriteSheetRows,

                                    (i+1.0f)/spriteSheetCols,0
                            };
                    Sprite sprite = new Sprite(texCoords);

                    images[i] = sprite;

                }
                spritesheet.addSprites(images);

                images = new Sprite[3];
                for(int i = 0; i < images.length; i++) {
                    float[] texCoords =
                            {
                                    (float)i/spriteSheetCols,1.0f/spriteSheetRows,

                                    (float) i/spriteSheetCols,1,

                                    (i+1.0f)/spriteSheetCols,1,

                                    (i+1.0f)/spriteSheetCols,1.0f/spriteSheetRows
                            };
                    Sprite sprite = new Sprite(texCoords);

                    images[i] = sprite;

                }
                spritesheet.addSprites(images);
            }

            vboVertices = ModelManager.getModel(width,height);
            if (vboVertices == -1){
                vboVertices = ModelManager.createModel(width,height);
            }

            animation = new Animation();
            animation.setFrames(spritesheet.getSprites(sprites));
            animation.setDelay(70);

            shader = ShaderManager.getShader("shaders\\shader");
            if (shader == null){
                shader = ShaderManager.createShader("shaders\\shader");
            }

            // because of scaling image by 2x
            width *= 2;
            height *= 2;
            cwidth *= 2;
            cheight *= 2;

            light = LightManager.createLight(new Vector3f(0.980f, 0.839f, 0.078f), new Vector2f((float)x+xmap,(float)y+ymap), 1.25f,this);
        }
    }

    public void setHit() {
        if(hit) return;
        hit = true;
        if(!tileMap.isServerSide()){
            animation.setFrames(spritesheet.getSprites(hitSprites));
            animation.setDelay(70);
        } else {
            animation = new Animation(3);
            animation.setDelay(70);
        }
        speed.x = 0;
        speed.y = 0;
    }

    public boolean shouldRemove() { return remove; }

    public void update() {
        // server side update
        setMapPosition();
        animation.update();
        if(tileMap.isServerSide()){
            checkTileMapCollision();
            setPosition(temp.x, temp.y);
            if((speed.x == 0 || speed.y == 0) && !hit) {
                setHit();
            }
            if(hit) {
                if (animation.hasPlayedOnce()){
                    remove = true;
                }
            }
        } else {
            if(!MultiplayerManager.multiplayer) {
                checkTileMapCollision();
                setPosition(temp.x, temp.y);
            }
            if((speed.x == 0 || speed.y == 0) && !hit) {
                setHit();
            }
            if(hit) {
                if (animation.hasPlayedOnce()){
                    remove = true;
                    light.remove();
                } else {
                    // decrease intensity every time we use next sprite of hitBullet
                    light.setIntensity(1-0.5f*animation.getIndexOfFrame());
                }
            }
        }

    }

    public void draw() {
        if (animation.hasPlayedOnce() && hit) return;
        super.draw();

    }
    public boolean isHit() {return hit;}

    public void loadSave(){
        width = 16;
        height = 16;

        // try to find spritesheet if it was created once
        spritesheet = SpritesheetManager.getSpritesheet("Textures\\Sprites\\Enemies\\slimebullet.tga");

        // creating a new spritesheet
        if (spritesheet == null){
            spritesheet = SpritesheetManager.createSpritesheet("Textures\\Sprites\\Enemies\\slimebullet.tga");

            Sprite[] images = new Sprite[4];

            for(int i = 0; i < images.length; i++) {
                float[] texCoords =
                        {
                                (float)i/spriteSheetCols,0,

                                (float)i/spriteSheetCols,1.0f/spriteSheetRows,

                                (i+1.0f)/spriteSheetCols,1.0f/spriteSheetRows,

                                (i+1.0f)/spriteSheetCols,0
                        };
                Sprite sprite = new Sprite(texCoords);

                images[i] = sprite;

            }
            spritesheet.addSprites(images);

            images = new Sprite[3];
            for(int i = 0; i < images.length; i++) {
                float[] texCoords =
                        {
                                (float)i/spriteSheetCols,1.0f/spriteSheetRows,

                                (float) i/spriteSheetCols,1,

                                (i+1.0f)/spriteSheetCols,1,

                                (i+1.0f)/spriteSheetCols,1.0f/spriteSheetRows
                        };
                Sprite sprite = new Sprite(texCoords);

                images[i] = sprite;

            }
            spritesheet.addSprites(images);
        }

        vboVertices = ModelManager.getModel(width,height);
        if (vboVertices == -1){
            vboVertices = ModelManager.createModel(width,height);
        }

        animation = new Animation();
        animation.setFrames(spritesheet.getSprites(sprites));
        animation.setDelay(70);

        shader = ShaderManager.getShader("shaders\\shader");
        if (shader == null){
            shader = ShaderManager.createShader("shaders\\shader");
        }

        // because of scaling image by 2x
        width *= 2;
        height *= 2;

        light = LightManager.createLight(new Vector3f(0.980f, 0.839f, 0.078f), new Vector2f(position.x+xmap,position.y+ymap), 1.25f,this);

    }
}
