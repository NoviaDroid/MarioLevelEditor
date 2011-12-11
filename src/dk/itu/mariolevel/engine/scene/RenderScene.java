package dk.itu.mariolevel.engine.scene;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dk.itu.mariolevel.engine.CameraHandler;
import dk.itu.mariolevel.engine.level.Level;
import dk.itu.mariolevel.engine.level.SpriteTemplate;
import dk.itu.mariolevel.engine.res.ResourcesManager;
import dk.itu.mariolevel.engine.sprites.BulletBill;
import dk.itu.mariolevel.engine.sprites.Fireball;
import dk.itu.mariolevel.engine.sprites.Mario;
import dk.itu.mariolevel.engine.sprites.Shell;
import dk.itu.mariolevel.engine.sprites.Sparkle;
import dk.itu.mariolevel.engine.sprites.Sprite;

public class RenderScene extends LevelScene{
	
    public List<Sprite> sprites = new ArrayList<Sprite>();
    protected List<Sprite> spritesToAdd = new ArrayList<Sprite>();
    protected List<Sprite> spritesToRemove = new ArrayList<Sprite>();

    public float xCam, yCam;
    
    public int startTime = 0;
    
    public int tickCount;
    
    private boolean politeReset;
    
	public RenderScene(Level level) {
		this.mario = new Mario(32, 32);
		
		this.level = level;
		
		try
        {
            Level.loadBehaviors(new DataInputStream(ResourcesManager.class.getResourceAsStream("res/tiles.dat")));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(0);
        }
	}

	public void reset() {
        sprites.clear();
        level.reset();
        
        tickCount = 0;
	}
	
	public void politeReset() {
		politeReset = true;
	}
	
	@Override
	public void tick() {
		if(politeReset) {
			politeReset = false;
			reset();
		}
		
        if (startTime > 0)
        	startTime++;

        xCam = CameraHandler.getInstance().getCameraPosition().x;
        yCam = CameraHandler.getInstance().getCameraPosition().y;

        for (Sprite sprite : sprites)
        {
        	float xd = sprite.x - xCam;
            float yd = sprite.y - yCam;
            if (xd < -64 || xd > 320 + 64 || yd < -64 || yd > 240 + 64)
            {
                removeSprite(sprite);
            }
        }

        tickCount++;
        level.tick();

        for (int x = (int) xCam / 16 - 1; x <= (int) (xCam + CameraHandler.getInstance().width) / 16 + 1; x++)
            for (int y = (int) yCam / 16 - 1; y <= (int) (yCam + CameraHandler.getInstance().height) / 16 + 1; y++)
            {
                int dir = -1;

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
            sprite.tick();
        }

        sprites.addAll(0, spritesToAdd);
        sprites.removeAll(spritesToRemove);
        spritesToAdd.clear();
        spritesToRemove.clear();
        
        if(tickCount > 50) politeReset = true;
	}
	

	@Override
    public void addSprite(Sprite sprite)
    {
        spritesToAdd.add(sprite);
        
//        System.out.println("Add sprite: " + sprite.getNameByKind(sprite.kind) + ", " + sprites.size());
        
        sprite.tick();
    }

    @Override
    public void removeSprite(Sprite sprite)
    {
        spritesToRemove.add(sprite);
    }
	
    @Override
	public void bump(int x, int y, boolean canBreakBricks){}

	@Override
    public void bumpInto(int x, int y){}

	@Override
	public void checkShellCollide(Shell shell) {}
	
	@Override
	public void checkFireballCollide(Fireball fireball) {}
	@Override
	public String toString() {
		return "RenderScene";
	}
}
