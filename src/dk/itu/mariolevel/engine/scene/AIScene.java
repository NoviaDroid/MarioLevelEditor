package dk.itu.mariolevel.engine.scene;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dk.itu.mariolevel.engine.CameraHandler;
import dk.itu.mariolevel.engine.level.Level;
import dk.itu.mariolevel.engine.level.SpriteTemplate;
import dk.itu.mariolevel.engine.res.ResourcesManager;
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
import dk.itu.mariolevel.engine.sprites.SpriteContext;

public class AIScene extends LevelScene implements SpriteContext {
	public static final int cellSize = 16;	
	
    public List<Sprite> sprites = new ArrayList<Sprite>();
    protected List<Sprite> spritesToAdd = new ArrayList<Sprite>();
    protected List<Sprite> spritesToRemove = new ArrayList<Sprite>();
    
    public float xCam, yCam, xCamO, yCamO;
    
    public int tickCount;

    public int startTime = 0;
    private int timeLeft;

    private boolean politeReset;
    
    final private List<Float> enemiesFloatsList = new ArrayList<Float>();
    final private float[] marioFloatPos = new float[2];
    final private int[] marioState = new int[11];
    
    private Point marioInitialPos;
    private int bonusPoints = -1;

    public static int killedCreaturesTotal;
    public static int killedCreaturesByFireBall;
    public static int killedCreaturesByStomp;
    public static int killedCreaturesByShell;
    
    public int fireballsOnScreen = 0;

    List<Shell> shellsToCheck = new ArrayList<Shell>();
    List<Fireball> fireballsToCheck = new ArrayList<Fireball>();
 	
	public AIScene(Level level) {
        this.level = level;

        marioInitialPos = new Point(level.xEnter, level.yEnter);
        
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
	    killedCreaturesTotal = 0;
	    killedCreaturesByFireBall = 0;
	    killedCreaturesByStomp = 0;
	    killedCreaturesByShell = 0;
	    
	    bonusPoints = -1;
	    
	    // Default value

        sprites.clear();
	    
        level.reset();
            
        Mario oldMario = mario;
	    mario = new Mario(this);
	    sprites.add(mario);
	    
        if(CameraHandler.getInstance().getFollowMario() == oldMario) {
        	CameraHandler.getInstance().setFollowMario(mario);
        }
	       
	    startTime = 1;
	    timeLeft = 200*15;
	    
	    tickCount = 0;
	}
	
	public void politeReset() {
		politeReset = true;
	}
	
	@Override
	public void checkShellCollide(Shell shell)
    {
        shellsToCheck.add(shell);
    }
	
	@Override
    public void checkFireballCollide(Fireball fireball)
    {
        fireballsToCheck.add(fireball);
    }

	@Override
	public void tick() {
		if(politeReset) {
			politeReset = false;
			reset();
		}
		
		Sprite.spriteContext = this;
		
		timeLeft--;
        if (timeLeft==0)
        {
            mario.die();
        }
        xCamO = xCam;
        yCamO = yCam;

        if (startTime > 0)
        {
            startTime++;
        }

        float targetXCam = mario.x - 160;

        xCam = targetXCam;

        if (xCam < 0) xCam = 0;
        if (xCam > level.length * 16 - 320) xCam = level.length * 16 - 320;
        
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

        for (int x = (int) xCam / 16 - 1; x <= (int) (xCam + CameraHandler.getInstance().width) / 16 + 1; x++)
            for (int y = (int) yCam / 16 - 1; y <= (int) (yCam + CameraHandler.getInstance().height) / 16 + 1; y++)
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
            sprite.tick();
        }

        for (Sprite sprite : sprites)
        {
            sprite.collideCheck();
        }

        for (Shell shell : shellsToCheck)
        {
            for (Sprite sprite : sprites)
            {
                if (sprite != shell && !shell.dead)
                {
                    if (sprite.shellCollideCheck(shell))
                    {
                        if (mario.carried == shell && !shell.dead)
                        {
                            mario.carried = null;
                            shell.die();
                            ++killedCreaturesTotal;
                        }
                    }
                }
            }
        }
        shellsToCheck.clear();

        for (Fireball fireball : fireballsToCheck)
        {
            for (Sprite sprite : sprites)
            {
                if (sprite != fireball && !fireball.dead)
                {
                    if (sprite.fireballCollideCheck(fireball))
                    {
                        fireball.die();
                    }
                }
            }
        }
        fireballsToCheck.clear();

        sprites.addAll(0, spritesToAdd);
        sprites.removeAll(spritesToRemove);
        spritesToAdd.clear();
        spritesToRemove.clear();
	}

	
	@Override
    public void addSprite(Sprite sprite)
    {
        spritesToAdd.add(sprite);
        sprite.tick();
    }

    @Override
    public void removeSprite(Sprite sprite)
    {
        spritesToRemove.add(sprite);
    }

    public float getX(float alpha)
    {
        int xCam = (int) (mario.xOld + (mario.x - mario.xOld) * alpha) - 160;
        if (xCam < 0) xCam = 0;

        return xCam + 160;
    }

    public float getY(float alpha)
    {
        return 0;
    }

    @Override
	public void bump(int x, int y, boolean canBreakBricks)
    {
        byte block = level.getBlock(x, y);

        if ((Level.TILE_BEHAVIORS[block & 0xff] & Level.BIT_BUMPABLE) > 0)
        {
            if (block == 1)
                mario.getHiddenBlock();
        	
            bumpInto(x, y - 1);
            level.setBlock(x, y, (byte) 4);
            level.setBlockData(x, y, (byte) 4);

            if (((Level.TILE_BEHAVIORS[block & 0xff]) & Level.BIT_SPECIAL) > 0)
            {
                if (!Mario.large)
                {
                    addSprite(new Mushroom(this, x * 16 + 8, y * 16 + 8, true));
                }
                else
                {
                    addSprite(new FireFlower(this, x * 16 + 8, y * 16 + 8));
                }
            }
            else
            {
                mario.getCoin();
                addSprite(new CoinAnim(x, y));
            }
        }

        if ((Level.TILE_BEHAVIORS[block & 0xff] & Level.BIT_BREAKABLE) > 0)
        {
            bumpInto(x, y - 1);
            if (canBreakBricks)
            {
                level.setBlock(x, y, (byte) 0);
                for (int xx = 0; xx < 2; xx++)
                    for (int yy = 0; yy < 2; yy++)
                        addSprite(new Particle(x * 16 + xx * 8 + 4, y * 16 + yy * 8 + 4, (xx * 2 - 1) * 4, (yy * 2 - 1) * 4 - 8));
            }
            else
            {
                level.setBlockData(x, y, (byte) 4);
            }
        }
    }

    @Override
    public void bumpInto(int x, int y)
    {
        byte block = level.getBlock(x, y);
        if (((Level.TILE_BEHAVIORS[block & 0xff]) & Level.BIT_PICKUPABLE) > 0)
        {
            mario.getCoin();
            level.setBlock(x, y, (byte) 0);
            addSprite(new CoinAnim(x, y + 1));
        }

        for (Sprite sprite : sprites)
        {
            sprite.bumpCheck(x, y);
        }
    }
    
    public float[] getMarioFloatPos()
    {
        marioFloatPos[0] = this.mario.x;
        marioFloatPos[1] = this.mario.y;
        return marioFloatPos;
    }
    
    public Point getMarioInitialPos() {
    	return marioInitialPos;
    }
    
    public int getMarioMode() { 
    	return mario.getMode(); 
    }
    
    public boolean isMarioOnGround() { 
    	return mario.isOnGround(); 
    }
    
    public boolean isMarioAbleToJump() {
    	return mario.mayJump();  
    }
    
    public boolean isMarioCarrying() { 
    	return mario.carried != null; 
    }
    
    public boolean isMarioAbleToShoot() {
        return mario.isAbleToShoot();
    }
    
    public int getMarioStatus() {
        return mario.getStatus();
    }
    
    public int getKillsTotal()
    {
        return killedCreaturesTotal;
    }

    public int getKillsByFire()
    {
        return killedCreaturesByFireBall;
    }

    public int getKillsByStomp()
    {
        return killedCreaturesByStomp;
    }

    public int getKillsByShell()
    {
        return killedCreaturesByShell;
    }
    
    public int[] getMarioState()
    {
        marioState[0] = this.getMarioStatus();
        marioState[1] = this.getMarioMode();
        marioState[2] = this.isMarioOnGround() ? 1 : 0;
        marioState[3] = this.isMarioAbleToJump() ? 1 : 0;
        marioState[4] = this.isMarioAbleToShoot() ? 1 : 0;
        marioState[5] = this.isMarioCarrying() ? 1 : 0;
        marioState[6] = this.getKillsTotal();
        marioState[7] = this.getKillsByFire();
        marioState[8] = this.getKillsByStomp();
        marioState[9] = this.getKillsByShell();
        marioState[10] = this.getTimeLeft();
        return marioState;
    }
    
    public int getTimeSpent() { 
    	return startTime / 15; 
    }
    
    public int getTimeLeft() { 
    	return timeLeft / 15; 
    }
    
    public boolean isLevelFinished() {
        return (mario.getStatus() != Mario.STATUS_RUNNING);
    }

    
	public void performAction(boolean[] action)
	{
	    // might look ugly , but arrayCopy is not necessary here:
	    this.mario.keys = action;
	}
    
	public int getBonusPoints()
	{
	    return bonusPoints;
	}

	public void setBonusPoints(final int bonusPoints)
	{
	    this.bonusPoints = bonusPoints;
	}

	public void appendBonusPoints(final int bonusPoints)
	{
	    this.bonusPoints += bonusPoints;
	}
	
    public float[] getCreaturesFloatPos()
    {
        float[] enemies = this.getEnemiesFloatPos();
        float ret[] = new float[enemies.length + 2];
        System.arraycopy(this.getMarioFloatPos(), 0, ret, 0, 2);
        System.arraycopy(enemies, 0, ret, 2, enemies.length);
        return ret;
    }
    
    public float[] getEnemiesFloatPos()
    {
        enemiesFloatsList.clear();
        for (Sprite sprite : sprites)
        {
            // TODO:[M]: add unit tests for getEnemiesFloatPos involving all kinds of creatures
            if (sprite.isDead()) continue;
            switch (sprite.kind)
            {
                case Sprite.KIND_GOOMBA:
                case Sprite.KIND_BULLET_BILL:
                case Sprite.KIND_ENEMY_FLOWER:
                case Sprite.KIND_GOOMBA_WINGED:
                case Sprite.KIND_GREEN_KOOPA:
                case Sprite.KIND_GREEN_KOOPA_WINGED:
                case Sprite.KIND_RED_KOOPA:
                case Sprite.KIND_RED_KOOPA_WINGED:
                case Sprite.KIND_SPIKY:
                case Sprite.KIND_SPIKY_WINGED:
                case Sprite.KIND_SHELL:
                {
                    enemiesFloatsList.add((float) sprite.kind);
                    enemiesFloatsList.add(sprite.x - mario.x);
                    enemiesFloatsList.add(sprite.y - mario.y);
                }
            }
        }

        float[] enemiesFloatsPosArray = new float[enemiesFloatsList.size()];

        int i = 0;
        for (Float F : enemiesFloatsList)
            enemiesFloatsPosArray[i++] = F;

        return enemiesFloatsPosArray;
    }

    @Override
    public String toString() {
    	return "AIScene";
    }
}
