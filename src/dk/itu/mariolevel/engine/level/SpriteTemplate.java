package dk.itu.mariolevel.engine.level;

import dk.itu.mariolevel.engine.scene.PlayableScene;
import dk.itu.mariolevel.engine.sprites.Enemy;
import dk.itu.mariolevel.engine.sprites.FlowerEnemy;
import dk.itu.mariolevel.engine.sprites.Sprite;

public class SpriteTemplate
{
    public int lastVisibleTick = -1;
    public Sprite sprite;
    public boolean isDead = false;
    private boolean winged;
    
    private int type;
    
    public SpriteTemplate(int type, boolean winged)
    {
        this.type = type;
        this.winged = winged;
    }
    
    public void spawn(PlayableScene world, int x, int y, int dir)
    {
        if (isDead) return;

        if (type==Enemy.ENEMY_FLOWER)
        {
            sprite = new FlowerEnemy(world, x*16+15, y*16+24);
        }
        else
        {
            sprite = new Enemy(world, x*16+8, y*16+15, dir, type, winged);
        }
        sprite.spriteTemplate = this;
        world.addSprite(sprite);
    }
}