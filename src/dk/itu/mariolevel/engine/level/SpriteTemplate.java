package dk.itu.mariolevel.engine.level;

import java.io.Serializable;

import dk.itu.mariolevel.engine.scene.LevelScene;
import dk.itu.mariolevel.engine.sprites.Enemy;
import dk.itu.mariolevel.engine.sprites.FlowerEnemy;
import dk.itu.mariolevel.engine.sprites.Sprite;

public class SpriteTemplate implements Serializable
{
	private static final long serialVersionUID = -1354910977314834331L;
	
	public transient int lastVisibleTick = -1;
    public transient Sprite sprite;
    public transient boolean isDead = false;

    private boolean winged;
    
    public int type;
    
    public SpriteTemplate(int type)
    {
        this.type = type;
        switch (type)
        {
            case Sprite.KIND_GOOMBA:
            case Sprite.KIND_GREEN_KOOPA:
            case Sprite.KIND_RED_KOOPA:
            case Sprite.KIND_SPIKY:
            case Sprite.KIND_BULLET_BILL:
            case Sprite.KIND_PRINCESS:
            case Sprite.KIND_ENEMY_FLOWER:
                this.winged = false;
                break;
            case Sprite.KIND_GOOMBA_WINGED:
            case Sprite.KIND_GREEN_KOOPA_WINGED:
            case Sprite.KIND_RED_KOOPA_WINGED:
            case Sprite.KIND_SPIKY_WINGED:
            case Sprite.KIND_WAVE_GOOMBA:
                this.winged = true;
                break;
        }
    }
    
    public void spawn(LevelScene world, int x, int y, int dir)
    {
        if (isDead) return;

        if (type == Sprite.KIND_ENEMY_FLOWER)
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