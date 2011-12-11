package dk.itu.mariolevel.engine.sprites;

import dk.itu.mariolevel.engine.Art;
import dk.itu.mariolevel.engine.scene.AIScene;


public class FireFlower extends Sprite
{
    int height = 24;

    private AIScene world;
    public int facing;

    public boolean avoidCliffs = false;
    private int life;

    public FireFlower(AIScene world, int x, int y)
    {
        kind = KIND_FIRE_FLOWER;
        sheet = Art.items;

        this.x = x;
        this.y = y;
        this.world = world;
        xPicO = 8;
        yPicO = 15;

        xPic = 1;
        yPic = 0;
        height = 12;
        facing = 1;
        wPic  = hPic = 16;
        life = 0;
    }

    public void collideCheck()
    {
        float xMarioD = world.mario.x - x;
        float yMarioD = world.mario.y - y;

        if (xMarioD > -16 && xMarioD < 16)
        {
            if (yMarioD > -height && yMarioD < world.mario.height)
            {
                world.mario.getFlower();
                spriteContext.removeSprite(this);
            }
        }
    }

    public void move()
    {
        if (life<9)
        {
            layer = 0;
            y--;
            life++;
            return;
        }
    }
}