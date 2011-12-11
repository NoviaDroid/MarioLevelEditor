package dk.itu.mariolevel.engine;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import dk.itu.mariolevel.engine.res.ResourcesManager;


public class Art
{
    public static final int SAMPLE_BREAK_BLOCK = 0;
    public static final int SAMPLE_GET_COIN = 1;
    public static final int SAMPLE_MARIO_JUMP = 2;
    public static final int SAMPLE_MARIO_STOMP = 3;
    public static final int SAMPLE_MARIO_KICK = 4;
    public static final int SAMPLE_MARIO_POWER_UP = 5;
    public static final int SAMPLE_MARIO_POWER_DOWN = 6;
    public static final int SAMPLE_MARIO_DEATH = 7;
    public static final int SAMPLE_ITEM_SPROUT = 8;
    public static final int SAMPLE_CANNON_FIRE = 9;
    public static final int SAMPLE_SHELL_BUMP = 10;
    public static final int SAMPLE_LEVEL_EXIT = 11;
    public static final int SAMPLE_MARIO_1UP = 12;
    public static final int SAMPLE_MARIO_FIREBALL = 13;

    public static Image[][] mario;
    public static Image[][] smallMario;
    public static Image[][] fireMario;
    public static Image[][] enemies;
    public static Image[][] items;
    public static Image[][] level;
    public static Image[][] particles;
    public static Image[][] font;
    public static Image[][] bg;

    public static Image specialBlockStart;
    public static Image specialBlockEnd;
    
    private static final String PREFIX="res";
    
    private static boolean successfullInit;
    
    public static void init(GraphicsConfiguration gc)
    {
    	if(successfullInit) return;
    	
        try
        {
            mario = cutImage(gc, PREFIX+"/mariosheet.png", 32, 32);
            smallMario = cutImage(gc, PREFIX+"/smallmariosheet.png", 16, 16);
            fireMario = cutImage(gc, PREFIX+"/firemariosheet.png", 32, 32);
            enemies = cutImage(gc, PREFIX+"/enemysheet.png", 16, 32);
            items = cutImage(gc,PREFIX+ "/itemsheet.png", 16, 16);
            level = cutImage(gc, PREFIX+"/mapsheet.png", 16, 16);
            particles = cutImage(gc, PREFIX+"/particlesheet.png", 8, 8);
            bg = cutImage(gc,PREFIX+ "/bgsheet.png", 32, 32);
            font = cutImage(gc, PREFIX+"/font.gif", 8, 8);
            
            specialBlockStart = getImage(gc, PREFIX+"/special_block_start.png");
            specialBlockEnd = getImage(gc, PREFIX+"/special_block_end.png");
            
            successfullInit = true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static Image getImage(GraphicsConfiguration gc, String imageName) throws IOException
    {
        InputStream p=ResourcesManager.class.getResourceAsStream(imageName);
        BufferedImage source = ImageIO.read(p);
        Image image = gc.createCompatibleImage(source.getWidth(), source.getHeight(), Transparency.BITMASK);
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setComposite(AlphaComposite.Src);
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return image;
    }

    private static Image[][] cutImage(GraphicsConfiguration gc, String imageName, int xSize, int ySize) throws IOException
    {
        Image source = getImage(gc, imageName);
        Image[][] images = new Image[source.getWidth(null) / xSize][source.getHeight(null) / ySize];
        for (int x = 0; x < source.getWidth(null) / xSize; x++)
        {
            for (int y = 0; y < source.getHeight(null) / ySize; y++)
            {
                Image image = gc.createCompatibleImage(xSize, ySize, Transparency.BITMASK);
                Graphics2D g = (Graphics2D) image.getGraphics();
                g.setComposite(AlphaComposite.Src);
                g.drawImage(source, -x * xSize, -y * ySize, null);
                g.dispose();
                images[x][y] = image;
            }
        }

        return images;
    }
}
