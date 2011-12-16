package dk.itu.mariolevel.engine.scene;

import dk.itu.mariolevel.engine.level.Level;
import dk.itu.mariolevel.engine.sprites.Fireball;
import dk.itu.mariolevel.engine.sprites.Mario;
import dk.itu.mariolevel.engine.sprites.Shell;
import dk.itu.mariolevel.engine.sprites.SpriteContext;

public abstract class LevelScene implements SpriteContext
{
    public static boolean[] keys = new boolean[16];

	public Mario mario;
	public Level level;
	
    public void toggleKey(int key, boolean isPressed)
    {
        keys[key] = isPressed;
    }

    public abstract void tick();

	public abstract void checkShellCollide(Shell shell);
	
    public abstract void checkFireballCollide(Fireball fireball);
    
	public abstract void bump(int x, int y, boolean canBreakBricks);

    public abstract void bumpInto(int x, int y);
}
