package cz.Empatix.Entity.ItemDrops;

import cz.Empatix.Entity.Animation;
import cz.Empatix.Gamestates.Singleplayer.InGame;
import cz.Empatix.Java.Loader;
import cz.Empatix.Java.Random;
import cz.Empatix.Render.Graphics.Model.ModelManager;
import cz.Empatix.Render.Graphics.Shaders.ShaderManager;
import cz.Empatix.Render.Graphics.Sprites.Sprite;
import cz.Empatix.Render.Graphics.Sprites.SpritesheetManager;
import cz.Empatix.Render.Postprocessing.Lightning.LightManager;
import cz.Empatix.Render.TileMap;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class PistolAmmo extends ItemDrop {
    public static void load(){
        Loader.loadImage("Textures\\pistol_bullet.tga");
    }
    public PistolAmmo(TileMap tm){
        super(tm);
        if(tm.isServerSide()){
            type = PISTOLAMMO;
            canDespawn = true;
            liveTime = System.currentTimeMillis()-InGame.deltaPauseTime();
            pickedUp = false;

            width = 16;
            height = 45;
            cwidth = 16;
            cheight = 45;
            scale = 1;
            facingRight = true;

            amount = Random.nextInt(5) + 3;

            // because of scaling image by 3x
            width *= scale;
            height *= scale;
            cwidth *= scale;
            cheight *= scale;

            stopSpeed = 0.35f;
        } else {
            type = PISTOLAMMO;
            canDespawn = true;
            liveTime = System.currentTimeMillis()-InGame.deltaPauseTime();
            pickedUp = false;

            width = 16;
            height = 45;
            cwidth = 16;
            cheight = 45;
            scale = 1;
            facingRight = true;

            amount = Random.nextInt(5) + 3;

            // try to find spritesheet if it was created once
            spritesheet = SpritesheetManager.getSpritesheet("Textures\\pistol_bullet.tga");

            // creating a new spritesheet
            if (spritesheet == null){
                spritesheet = SpritesheetManager.createSpritesheet("Textures\\pistol_bullet.tga");
                Sprite[] sprites = new Sprite[1];
                float[] texCoords =
                        {
                                0,0,

                                0,1,

                                1,1,

                                1,0
                        };
                Sprite sprite = new Sprite(texCoords);
                sprites[0] = sprite;
                spritesheet.addSprites(sprites);
            }
            vboVertices = ModelManager.getModel(width,height);
            if (vboVertices == -1){
                vboVertices = ModelManager.createModel(width,height);
            }

            animation = new Animation();
            animation.setFrames(spritesheet.getSprites(0));
            animation.setDelay(-1);

            shader = ShaderManager.getShader("shaders\\shader");
            if (shader == null){
                shader = ShaderManager.createShader("shaders\\shader");
            }

            // because of scaling image by 3x
            width *= scale;
            height *= scale;
            cwidth *= scale;
            cheight *= scale;

            light = LightManager.createLight(new Vector3f(1.0f,0.8274f,0.0f),new Vector2f(0,0),1.25f,this);

            stopSpeed = 0.35f;
        }

    }

    @Override
    public void loadSave() {
        width = 16;
        height = 45;

        // try to find spritesheet if it was created once
        spritesheet = SpritesheetManager.getSpritesheet("Textures\\pistol_bullet.tga");

        // creating a new spritesheet
        if (spritesheet == null){
            spritesheet = SpritesheetManager.createSpritesheet("Textures\\pistol_bullet.tga");
            Sprite[] sprites = new Sprite[1];
            float[] texCoords =
                    {
                            0,0,

                            0,1,

                            1,1,

                            1,0
                    };
            Sprite sprite = new Sprite(texCoords);
            sprites[0] = sprite;
            spritesheet.addSprites(sprites);
        }
        vboVertices = ModelManager.getModel(width,height);
        if (vboVertices == -1){
            vboVertices = ModelManager.createModel(width,height);
        }

        animation = new Animation();
        animation.setFrames(spritesheet.getSprites(0));
        animation.setDelay(-1);

        shader = ShaderManager.getShader("shaders\\shader");
        if (shader == null){
            shader = ShaderManager.createShader("shaders\\shader");
        }

        // because of scaling image by 3x
        width *= scale;
        height *= scale;

        light = LightManager.createLight(new Vector3f(1.0f,0.8274f,0.0f),new Vector2f(position.x, position.y),1.25f,this);
    }

    public void update(){
        super.update();
        checkTileMapCollision();
        setPosition(temp.x, temp.y);

        long timeNow = System.currentTimeMillis();
        float time = (float)(timeNow - liveTime - InGame.deltaPauseTime())/1000;
        if(time >= 30 && canDespawn){
            pickedUp = true;
            remove();
        } else if(time >= 25 && canDespawn){
            flinching = true;
        }
    }
    public void draw(){

        setMapPosition();

        long timeNow = System.currentTimeMillis() - InGame.deltaPauseTime();
        if(flinching){
            if((timeNow - liveTime) / 10 % 2 == 0) return;
        }
        super.draw();
    }
}
