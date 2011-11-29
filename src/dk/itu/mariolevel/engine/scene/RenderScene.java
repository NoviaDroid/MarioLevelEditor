package dk.itu.mariolevel.engine.scene;

import dk.itu.mariolevel.engine.level.Level;
import dk.itu.mariolevel.engine.level.SpriteTemplate;
import dk.itu.mariolevel.engine.sprites.BulletBill;
import dk.itu.mariolevel.engine.sprites.CoinAnim;
import dk.itu.mariolevel.engine.sprites.FireFlower;
import dk.itu.mariolevel.engine.sprites.Fireball;
import dk.itu.mariolevel.engine.sprites.Mario;
import dk.itu.mariolevel.engine.sprites.Mushroom;
import dk.itu.mariolevel.engine.sprites.Particle;
import dk.itu.mariolevel.engine.sprites.Shell;
import dk.itu.mariolevel.engine.sprites.Sparkle;
import dk.itu.mariolevel.engine.sprites.Sprite;

public class RenderScene extends PlayableScene {
	

	public RenderScene(Level level) {
		super(level);
		
		super.reset();
	}

	@Override
	public void reset() {
		//super.reset();

		sprites.remove(mario);
	}
	
	@Override
	public void tick() {
        xCamO = xCam;
        yCamO = yCam;

        if (startTime > 0)
        {
            startTime++;
        }

        float targetXCam = mario.x - 160;

        xCam = targetXCam;

        if (xCam < 0) xCam = 0;
        if (xCam > level.width * 16 - 320) xCam = level.width * 16 - 320;
        
        fireballsOnScreen = 0;

        for (Sprite sprite : sprites)
        {
            if (sprite != mario)
            {
                float xd = sprite.x - xCam;
                float yd = sprite.y - yCam;
                if (xd < -64 || xd > 320 + 64 || yd < -64 || yd > 240 + 64)
                {
                    removeSprite(sprite);
                }
                else
                {
                    if (sprite instanceof Fireball)
                    {
                        fireballsOnScreen++;
                    }
                }
            }
        }

        tickCount++;
        level.tick();

        for (int x = (int) xCam / 16 - 1; x <= (int) (xCam + this.width) / 16 + 1; x++)
            for (int y = (int) yCam / 16 - 1; y <= (int) (yCam + this.height) / 16 + 1; y++)
            {
                int dir = 0;

                if (x * 16 + 8 > mario.x + 16) dir = -1;
                if (x * 16 + 8 < mario.x - 16) dir = 1;

                SpriteTemplate st = level.getSpriteTemplate(x, y);

                if (st != null)
                {
                    if (st.lastVisibleTick != tickCount - 1)
                    {
                        if (st.sprite == null || !sprites.contains(st.sprite))
                        {
                            st.spawn(this, x, y, dir);
                        }
                    }

                    st.lastVisibleTick = tickCount;
                }

                if (dir != 0)
                {
                    byte b = level.getBlock(x, y);
                    if (((Level.TILE_BEHAVIORS[b & 0xff]) & Level.BIT_ANIMATED) > 0)
                    {
                        if ((b % 16) / 4 == 3 && b / 16 == 0)
                        {
                            if ((tickCount - x * 2) % 100 == 0)
                            {
                                for (int i = 0; i < 8; i++)
                                {
                                    addSprite(new Sparkle(x * 16 + 8, y * 16 + (int) (Math.random() * 16), (float) Math.random() * dir, 0, 0, 1, 5));
                                }
                                addSprite(new BulletBill(this, x * 16 + 8 + dir * 8, y * 16 + 15, dir));
                            }
                        }
                    }
                }
            }


        for (Sprite sprite : sprites)
        {
        	if(sprite instanceof Mario) continue;
            sprite.tick();
        }

        sprites.addAll(0, spritesToAdd);
        sprites.removeAll(spritesToRemove);
        spritesToAdd.clear();
        spritesToRemove.clear();
	}
	
	public void bump(int x, int y, boolean canBreakBricks)
    {
        
    }

    public void bumpInto(int x, int y)
    {
        
    }
}
