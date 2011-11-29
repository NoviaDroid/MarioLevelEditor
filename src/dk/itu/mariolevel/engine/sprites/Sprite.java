package dk.itu.mariolevel.engine.sprites;

import java.awt.Graphics;
import java.awt.Image;

import dk.itu.mariolevel.engine.level.SpriteTemplate;

public class Sprite
{
	public static final int KIND_NONE = 0;
	public static final int KIND_MARIO = -31;
	public static final int KIND_GOOMBA = 80;
	public static final int KIND_GOOMBA_WINGED = 95;
	public static final int KIND_RED_KOOPA = 82;
	public static final int KIND_RED_KOOPA_WINGED = 97;
	public static final int KIND_GREEN_KOOPA = 81;
	public static final int KIND_GREEN_KOOPA_WINGED = 96;
	public static final int KIND_BULLET_BILL = 84;
	public static final int KIND_SPIKY = 93;
	public static final int KIND_SPIKY_WINGED = 99;
//	    public static final int KIND_ENEMY_FLOWER = 11;
	public static final int KIND_ENEMY_FLOWER = 91;
	public static final int KIND_WAVE_GOOMBA = 98; // TODO: !H!: same
	public static final int KIND_SHELL = 13;
	public static final int KIND_MUSHROOM = 2;
	public static final int KIND_GREEN_MUSHROOM = 9;
	public static final int KIND_PRINCESS = 49;
	public static final int KIND_FIRE_FLOWER = 3;
	public static final int KIND_PARTICLE = 21;
	public static final int KIND_SPARCLE = 22;
	public static final int KIND_COIN_ANIM = 1;
	public static final int KIND_FIREBALL = 25;
	
	public static final int KIND_UNDEF = -42;
	
    public static SpriteContext spriteContext;
    public byte kind = KIND_UNDEF;
    
    public float xOld, yOld, x, y, xa, ya;
    
    public int mapX, mapY;
    
    public int xPic, yPic;
    public int wPic = 32;
    public int hPic = 32;
    public int xPicO, yPicO;
    public boolean xFlipPic = false;
    public boolean yFlipPic = false;
    public Image[][] sheet;
    public boolean visible = true;
    
    public int layer = 1;

    public SpriteTemplate spriteTemplate;
    
    public void move()
    {
        x+=xa;
        y+=ya;
    }
    
    public void render(Graphics og)
    {
        if (!visible) return;
        
        int xPixel = (int) x - xPicO;
        int yPixel = (int) y - yPicO;

        og.drawImage(sheet[xPic][yPic],
                xPixel + (xFlipPic ? wPic : 0),
                yPixel + (yFlipPic ? hPic : 0),
                xFlipPic ? -wPic : wPic,
                yFlipPic ? -hPic : hPic, null);
    }

    public final void tick()
    {
        xOld = x;
        yOld = y;
        move();
        mapY = (int) (y / 16);
        mapX = (int) (x / 16);
    }

    public final void tickNoMove()
    {
        xOld = x;
        yOld = y;
    }

    public float getX(float alpha)
    {
        return (xOld+(x-xOld)*alpha)-xPicO;
    }

    public float getY(float alpha)
    {
        return (yOld+(y-yOld)*alpha)-yPicO;
    }

    public void collideCheck()
    {
    }

    public void bumpCheck(int xTile, int yTile)
    {
    }

    public boolean shellCollideCheck(Shell shell)
    {
        return false;
    }

    public void release(Mario mario)
    {
    }

    public boolean fireballCollideCheck(Fireball fireball)
    {
        return false;
    }
    
    public boolean isDead()
    {
        return spriteTemplate != null && spriteTemplate.isDead;
    }
}