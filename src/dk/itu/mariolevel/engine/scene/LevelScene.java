package dk.itu.mariolevel.engine.scene;

import java.awt.GraphicsConfiguration;

import dk.itu.mariolevel.engine.MarioComponent;
import dk.itu.mariolevel.engine.level.Level;

public class LevelScene extends PlayableScene {

	public LevelScene(GraphicsConfiguration graphicsConfiguration,
			MarioComponent renderer, long seed, int levelDifficulty, int type) {
		super(seed, levelDifficulty, type);
	}
	
	public LevelScene(GraphicsConfiguration graphicsConfiguration, MarioComponent renderer, Level level) {
        super(level);
	}
}
