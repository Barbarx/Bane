package cz.Empatix.Render.Hud.ProgressNPC.GunUpgrades;


import cz.Empatix.Gamestates.GameStateManager;
import cz.Empatix.Render.Text.TextRender;
import org.joml.Vector3f;

public class GrenadelauncherUpgrade extends UpgradeBar {
    public GrenadelauncherUpgrade(int row){
        super("Textures\\grenadelauncher.tga",2,row);
        info = new WeaponInfo();
        info.maxAmmo = 36;
        info.maxMagazineAmmo = 6;
        info.maxDamage = 7;
        info.minDamage = 4;
        info.firerate = 1f/0.550f;
        info.name = "Grenade Launcher";

        int numUpgrades = GameStateManager.getDb().getValueUpgrade("grenadelauncher","upgrades");
        this.numUpgrades = numUpgrades;


        UpgradeSideBar bar = new UpgradeSideBar(sideBars.size(),"grenadelauncher");
        String[] text = new String[]{"Increase maximum capacity of ammo by 8"};
        bar.setText(text);
        bar.setType(UpgradeSideBar.AMMOUPGRADE);
        bar.setPrice(20);
        sideBars.add(bar);
        if(numUpgrades > 0){
            bar.setBought(true);
            numUpgrades--;
            info.maxAmmo+=8;
        }

        bar = new UpgradeSideBar(sideBars.size(),"grenadelauncher");
        text = new String[]{"Increase magazine capacity by 3"};
        bar.setText(text);
        bar.setType(UpgradeSideBar.AMMOUPGRADE);
        bar.setPrice(40);
        sideBars.add(bar);
        if(numUpgrades > 0){
            bar.setBought(true);
            numUpgrades--;
            info.maxMagazineAmmo+=3;
        }

        bar = new UpgradeSideBar(sideBars.size(),"grenadelauncher");
        text = new String[]{"Increase damage by 2"};
        bar.setText(text);
        bar.setType(UpgradeSideBar.DAMAGEUPGRADE);
        bar.setPrice(80);
        sideBars.add(bar);
        if(numUpgrades > 0){
            bar.setBought(true);
            numUpgrades--;
            info.maxDamage+=2;
            info.minDamage+=2;
        }

        bar = new UpgradeSideBar(sideBars.size(),"grenadelauncher");
        text = new String[]{"Your explosions can cause critical hits"};
        bar.setText(text);
        bar.setType(UpgradeSideBar.DAMAGEUPGRADE);
        bar.setPrice(140);
        sideBars.add(bar);
        if(numUpgrades > 0){
            bar.setBought(true);
            numUpgrades--;
            info.crit_hits=true;
        }
    }
    @Override
    public void drawStats() {
        super.drawStats();
        textRender[5].draw(info.name,new Vector3f(TextRender.getHorizontalCenter(202,496,info.name,2),325,0),2, new Vector3f(0.78f,0.737f,0.027f));

        textRender[0].draw("Min. damage: " +info.minDamage,new Vector3f(295,400,0),1, new Vector3f(0.686f,0.4f,0.258f));
        textRender[1].draw("Max. damage: " +info.maxDamage,new Vector3f(295,430,0),1, new Vector3f(0.686f,0.4f,0.258f));
        textRender[2].draw("Fire rate: " +String.format("%.2f",info.firerate),new Vector3f(295,460,0),1, new Vector3f(0.686f,0.4f,0.258f));
        textRender[3].draw("Max ammo: " +info.maxMagazineAmmo+"/"+info.maxAmmo,new Vector3f(295,490,0),1, new Vector3f(0.686f,0.4f,0.258f));
        textRender[4].draw("Critical hits: " +info.areCritical_hits_enabled(),new Vector3f(295,520,0),1, new Vector3f(0.686f,0.4f,0.258f));

    }

    @Override
    public void updateStats() {
        int numUpgrades = GameStateManager.getDb().getValueUpgrade("grenadelauncher","upgrades");
        this.numUpgrades = numUpgrades;

        if(numUpgrades == 1){
            info.maxAmmo+=4;
        }
        if(numUpgrades == 2){
            info.maxMagazineAmmo+=3;
        }
        if(numUpgrades == 3){
            info.maxDamage+=2;
            info.minDamage+=2;
        }
        if(numUpgrades == 4){
            info.crit_hits=true;
        }
    }
}
