package cz.Empatix.Guns;

import cz.Empatix.AudioManager.AudioManager;
import cz.Empatix.Entity.Enemy;
import cz.Empatix.Gamestates.GameStateManager;
import cz.Empatix.Gamestates.InGame;
import cz.Empatix.Java.Random;
import cz.Empatix.Render.Damageindicator.DamageIndicator;
import cz.Empatix.Render.Hud.Image;
import cz.Empatix.Render.RoomObjects.DestroyableObject;
import cz.Empatix.Render.RoomObjects.RoomObject;
import cz.Empatix.Render.Text.TextRender;
import cz.Empatix.Render.TileMap;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;

public class Luger extends Weapon {
    // audio
    private final int[] soundShoot;
    private final int soundEmptyShoot;
    private final int soundReload;

    private int dots;

    private ArrayList<Bullet> bullets;

    private int bonusShots;
    private float chanceBonusShots;
    private boolean bonusShotsAntiConsume; // bonus shots do not consume ammo

    Luger(TileMap tm){
        super(tm);
        mindamage = 1;
        maxdamage = 2;
        inaccuracy = 0.8f;
        maxAmmo = 120;
        maxMagazineAmmo = 9;
        currentAmmo = maxAmmo;
        currentMagazineAmmo = maxMagazineAmmo;
        type = 1;
        bullets = new ArrayList<>();
        // shooting
        soundShoot = new int[2];
        soundShoot[0] = AudioManager.loadSound("guns\\shootpistol_1.ogg");
        soundShoot[1] = AudioManager.loadSound("guns\\shootpistol_2.ogg");
        // shooting without ammo
        soundEmptyShoot = AudioManager.loadSound("guns\\emptyshoot.ogg");
        soundReload = AudioManager.loadSound("guns\\reloadpistol.ogg");

        weaponHud = new Image("Textures\\lahti.tga",new Vector3f(1600,975,0),2f);
        weaponAmmo = new Image("Textures\\pistol_bullet.tga",new Vector3f(1810,975,0),1f);

        bonusShots = 0;
        chanceBonusShots = 0.2f;

        int numUpgrades = GameStateManager.getDb().getValueUpgrade("luger","upgrades");
        numUpgrades = 100;
        if(numUpgrades >= 1){
            maxdamage++;
            mindamage++;
        }
        if(numUpgrades >= 2){
            chanceBonusShots+=0.1f;
        }
        if(numUpgrades >= 3){
            bonusShotsAntiConsume = true;
        }
        if(numUpgrades >= 4){
            criticalHits = true;
        }
    }

    @Override
    public void reload() {
        if (!reloading && currentAmmo != 0 && currentMagazineAmmo != maxMagazineAmmo){
            reloadDelay = System.currentTimeMillis() - InGame.deltaPauseTime();
            reloadsource.play(soundReload);
            reloading = true;

            dots = 0;
        }
    }

    @Override
    public void shot(float x,float y,float px,float py) {
        long delta = System.currentTimeMillis() - delay - InGame.deltaPauseTime();
        if(bonusShots > 0 && delta > 250-bonusShots*16.6 && delta < 250){
            double inaccuracy = 0;
            inaccuracy = 0.055 * delta / (250-bonusShots*16.6) * (Random.nextInt(2) * 2 - 1);
            Bullet bullet = new Bullet(tm, x, y, inaccuracy,30);
            bullet.setPosition(px, py);
            bullet.setDamage(3);
            if(criticalHits){
                if(Math.random() > 0.9){
                    bullet.setDamage(6);
                    bullet.setCritical(true);
                }
            }
            bullets.add(bullet);
            GunsManager.bulletShooted++;

            bonusShots--;
        }
        if(isShooting()) {
            if (currentMagazineAmmo != 0) {
                if (reloading) return;
                // delta - time between shoots
                // InGame.deltaPauseTime(); returns delayed time because of pause time
                if (delta > 250) {
                    double inaccuracy = 0;
                    if (delta < 400) {
                        inaccuracy = 0.055 * 400 / delta * (Random.nextInt(2) * 2 - 1);
                    }
                    delay = System.currentTimeMillis() - InGame.deltaPauseTime();
                    Bullet bullet = new Bullet(tm, x, y, inaccuracy,30);
                    bullet.setPosition(px, py);
                    int damage = Random.nextInt(maxdamage+1-mindamage) + mindamage;
                    if(criticalHits){
                        if(Math.random() > 0.9){
                            damage*=2;
                            bullet.setCritical(true);
                        }
                    }
                    bullet.setDamage(damage);
                    bullets.add(bullet);
                    currentMagazineAmmo--;
                    GunsManager.bulletShooted++;
                    source.play(soundShoot[Random.nextInt(2)]);
                    while(Math.random() > 1-chanceBonusShots && currentMagazineAmmo != 0){
                        bonusShots++;
                        if(!bonusShotsAntiConsume)currentMagazineAmmo--;
                    }
                }
            } else if (currentAmmo != 0) {
                reload();
            } else {
                source.play(soundEmptyShoot);
            }
            setShooting(false);

        }
    }

    @Override
    public void drawAmmo() {
        for (Bullet bullet : bullets) {
            bullet.draw();
        }
    }

    @Override
    public void draw() {
        if(reloading){
            StringBuilder builder = new StringBuilder();
            for(int i = 0;i<=dots;i++) builder.append(".");
            TextRender.renderText(builder.toString(),new Vector3f(1820,985,0),6,new Vector3f(0.886f,0.6f,0.458f));

        } else {
            TextRender.renderText(currentMagazineAmmo+"/"+currentAmmo,new Vector3f(1750,985,0),2,new Vector3f(0.886f,0.6f,0.458f));
        }
        weaponHud.draw();
        weaponAmmo.draw();

    }

    @Override
    public void update() {
        float time = (float)(System.currentTimeMillis()-reloadDelay-InGame.deltaPauseTime())/1000;
        dots = (int)((time / 0.7f) / 0.2f);
        if(reloading && time > 0.7f) {
            if (currentAmmo - maxMagazineAmmo+currentMagazineAmmo < 0) {
                currentMagazineAmmo = currentAmmo;
                currentAmmo = 0;
            } else {
                currentAmmo -= maxMagazineAmmo - currentMagazineAmmo;
                currentMagazineAmmo = maxMagazineAmmo;
            }
            reloading = false;
        }
    }

    @Override
    public void updateAmmo() {
        for(int i = 0; i < bullets.size(); i++) {
            bullets.get(i).update();
            if(bullets.get(i).shouldRemove()) {
                bullets.remove(i);
                i--;
            }
        }
    }

    @Override
    public void checkCollisions(ArrayList<Enemy> enemies) {
        ArrayList<RoomObject> objects = tm.getRoomMapObjects();
        A: for(Bullet bullet:bullets){
            for(Enemy enemy:enemies){
                if (bullet.intersects(enemy) && !bullet.isHit() && !enemy.isDead() && !enemy.isSpawning()) {
                    enemy.hit(bullet.getDamage());
                    int cwidth = enemy.getCwidth();
                    int cheight = enemy.getCheight();
                    int x = -cwidth/4+Random.nextInt(cwidth/2);
                    if(bullet.isCritical()){
                        DamageIndicator.addCriticalDamageShow(bullet.getDamage(),(int)enemy.getX()-x,(int)enemy.getY()-cheight/3
                                ,new Vector2f(-x/25f,-1f));
                    } else {
                        DamageIndicator.addDamageShow(bullet.getDamage(),(int)enemy.getX()-x,(int)enemy.getY()-cheight/3
                                ,new Vector2f(-x/25f,-1f));
                    }
                    bullet.playEnemyHit();
                    bullet.setHit();
                    GunsManager.hitBullets++;
                    continue A;
                }
            }
            for(RoomObject object: objects){
                if(object instanceof DestroyableObject) {
                    if (bullet.intersects(object) && !bullet.isHit() && !((DestroyableObject) object).isDestroyed()) {
                        bullet.playEnemyHit();
                        bullet.setHit();
                        ((DestroyableObject) object).setHit(bullet.getDamage());
                        continue A;
                    }
                }
            }
        }
    }
}