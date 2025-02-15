package cz.Empatix.Render.Hud.ProgressNPC.GunUpgrades;

import cz.Empatix.Database.Database;
import cz.Empatix.Entity.Player;
import cz.Empatix.Gamestates.GameStateManager;
import cz.Empatix.Render.Alerts.AlertManager;
import cz.Empatix.Render.Hud.Image;
import cz.Empatix.Render.Hud.MenuBar;
import cz.Empatix.Render.Text.TextRender;
import cz.Empatix.Utility.Loader;
import org.joml.Vector3f;

public class UpgradeSideBar {
    private Image sideBar;
    private String[] text;
    private Vector3f pos;

    private int price;

    private int type;
    private String nameWeapon;

    final static int AMMOUPGRADE = 0;
    final static int ACCURACYUPGRADE = 1;
    final static int DAMAGEUPGRADE = 2;
    final static int OTHERUPGRADE = 3;

    private MenuBar buyButton;
    private MenuBar lockedButton;
    private MenuBar boughtButton;
    private Image coinImage;

    private boolean bought;

    private TextRender renderText;
    private long alertCooldown;

    UpgradeSideBar(int row, String nameWeapon){
        this.nameWeapon = nameWeapon;
        pos = new Vector3f(1122.5f,245+row*130,0);
        buyButton = new MenuBar("Textures\\Menu\\menu_bar.tga",new Vector3f(pos.x+215,pos.y+30,pos.z),0.5f,200,100,true);
        buyButton.setClick(false);
        sideBar = new Image("Textures\\ProgressRoom\\upgrademenu-guns-sidebar.tga",pos,1.5f);
        coinImage = new Image("Textures\\coin.tga",new Vector3f(pos.x+115,pos.y+30,pos.z),1f);
        boughtButton = new MenuBar("Textures\\ProgressRoom\\bought-bar.tga",new Vector3f(pos.x+215,pos.y+30,pos.z),0.5f,200,100,false);
        lockedButton = new MenuBar("Textures\\ProgressRoom\\lockedbar.tga",new Vector3f(pos.x+215,pos.y+30,pos.z),0.5f,200,100,false);

        renderText = new TextRender();
    }
    public void draw(boolean locked){
        sideBar.draw();
        if(text != null){
            for(int i = 0; i< text.length; i++){
                renderText.draw(text[i],new Vector3f(pos.x-135,pos.y-40+i*20,0),1,new Vector3f(0.686f,0.4f,0.258f));
            }
        }
        if(bought){
            boughtButton.draw();
        }else if (locked){
            lockedButton.draw();
        }
        else {
            buyButton.draw();
            renderText.draw("Buy",new Vector3f(TextRender.getHorizontalCenter(1290,1385,"Buy",2),pos.y+40,pos.z),2,new Vector3f(0.686f,0.4f,0.258f));
            renderText.draw(""+price,new Vector3f(pos.x+50,pos.y+42.5f,pos.z),2,new Vector3f(1.0f,0.947f,0.2f));
            coinImage.draw();

        }

    }

    public void setText(String[] text) {
        this.text = text;
    }

    public Vector3f getPosition() {
        return pos;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void mouseClick(float x, float y, Player p){
        if(buyButton.intersects(x,y)){
            if(p.getCoins() >= price && !isBought()){
                p.removeCoins(price);
                Database database = GameStateManager.getDb();
                int numUpgrades = database.getValueUpgrade(nameWeapon,"upgrades");
                database.setValueUpgrade(nameWeapon,numUpgrades+1);
                database.setValue("money",database.getValue("money","general")-price);
                bought = true;
            } else if (!isBought()){
                if(System.currentTimeMillis() - alertCooldown > 2000){
                    alertCooldown = System.currentTimeMillis();
                    AlertManager.add(AlertManager.WARNING,"You don't have enough coins");
                }
            }
        }
    }
    public void mouseHover(float x, float y){
        buyButton.setClick(false);
        if(buyButton.intersects(x,y)){
            buyButton.setClick(true);
        }
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isBought() {
        return bought;
    }

    public void setBought(boolean bought) {
        this.bought = bought;
    }

    public int getPrice() {
        return price;
    }

}
