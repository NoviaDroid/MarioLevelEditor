package dk.itu.mariolevel.engine.scene;

import dk.itu.mariolevel.engine.level.Level;
import dk.itu.mariolevel.engine.sprites.CoinAnim;
import dk.itu.mariolevel.engine.sprites.FireFlower;
import dk.itu.mariolevel.engine.sprites.Fireball;
import dk.itu.mariolevel.engine.sprites.Mario;
import dk.itu.mariolevel.engine.sprites.Mushroom;
import dk.itu.mariolevel.engine.sprites.Particle;
import dk.itu.mariolevel.engine.sprites.Shell;
import dk.itu.mariolevel.engine.sprites.Sprite;
import dk.itu.mariolevel.engine.sprites.SpriteContext;

public abstract class LevelScene extends Scene implements SpriteContext{

	public Mario mario;
	public Level level;

	public abstract void checkShellCollide(Shell shell);
	
    public abstract void checkFireballCollide(Fireball fireball);
    
	public abstract void bump(int x, int y, boolean canBreakBricks);

    public abstract void bumpInto(int x, int y);
}
