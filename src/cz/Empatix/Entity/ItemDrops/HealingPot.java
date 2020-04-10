package cz.Empatix.Entity.ItemDrops;

import cz.Empatix.Entity.Animation;
import cz.Empatix.Gamestates.InGame;
import cz.Empatix.Render.Graphics.Model.ModelManager;
import cz.Empatix.Render.Graphics.Shaders.ShaderManager;
import cz.Empatix.Render.Graphics.Sprites.Sprite;
import cz.Empatix.Render.Graphics.Sprites.SpritesheetManager;
import cz.Empatix.Render.Postprocessing.Lightning.LightManager;
import cz.Empatix.Render.TileMap;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class HealingPot extends ItemDrop{
    public HealingPot(TileMap tm){
        super(tm);
        type = HP;
        canDespawn = true;
        liveTime = System.currentTimeMillis();
        pickedUp = false;

        width = 30;
        height = 37;
        cwidth = 30;
        cheight = 46;
        scale = 1;
        facingRight = true;

        //amount = Random.nextInt(3) + 1;

        // try to find spritesheet if it was created once
        spritesheet = SpritesheetManager.getSpritesheet("Textures\\healingpot.tga");

        // creating a new spritesheet
        if (spritesheet == null){
            spritesheet = SpritesheetManager.createSpritesheet("Textures\\healingpot.tga");
            Sprite[] sprites = new Sprite[1];
            double[] texCoords =
                    {
                            0.,0.,

                            0.,1.,

                            1.,1.,

                            1.,0.
                    };
            Sprite sprite = new Sprite(texCoords);
            sprites[0] = sprite;
            spritesheet.addSprites(sprites);
        }
        vboVerticles = ModelManager.getModel(width,height);
        if (vboVerticles == -1){
            vboVerticles = ModelManager.createModel(width,height);
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
        cwidth *= scale*3;
        cheight *= scale*2;

        light = LightManager.createLight(new Vector3f(.0f,1.0f,.0f),new Vector2f(0,0),1.25f,this);

    }

    public void update(){
        animation.update();
        long timeNow = System.currentTimeMillis();
        if((float)(timeNow - liveTime - InGame.deltaPauseTime())/1000 > 30  && canDespawn){
            pickedUp = true;
            remove();
        }
    }
    public void draw(){

        setMapPosition();

        long timeNow = System.currentTimeMillis();
        if((float)(timeNow - liveTime - InGame.deltaPauseTime())/1000 > 25  && canDespawn){
            if((timeNow - liveTime-InGame.deltaPauseTime()) / 10 % 2 == 0) return;
        }
        super.draw();
    }
}
