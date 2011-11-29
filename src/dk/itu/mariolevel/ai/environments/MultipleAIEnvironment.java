package dk.itu.mariolevel.ai.environments;


import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import dk.itu.mariolevel.ai.GeneralizerEnemies;
import dk.itu.mariolevel.ai.GeneralizerLevelScene;
import dk.itu.mariolevel.ai.SystemOfValues;
import dk.itu.mariolevel.ai.agents.Agent;
import dk.itu.mariolevel.ai.agents.ForwardAgent;
import dk.itu.mariolevel.ai.agents.RandomAgent;
import dk.itu.mariolevel.engine.Replayer;
import dk.itu.mariolevel.engine.level.Level;
import dk.itu.mariolevel.engine.level.LevelGenerator;
import dk.itu.mariolevel.engine.scene.LevelScene;
import dk.itu.mariolevel.engine.scene.RenderScene;
import dk.itu.mariolevel.engine.sprites.Mario;
import dk.itu.mariolevel.engine.sprites.Sprite;

public class MultipleAIEnvironment implements Environment {

	public static final int TICKS_PER_SECOND = 24;
	
	public static SystemOfValues IntermediateRewardsSystemOfValues = new SystemOfValues();
	
	private HashMap<Agent, LevelScene> aiPairs;
	
	private int[] marioEgoPos = new int[]{9,9};
	private int receptiveFieldHeight = 19; // to be setup via MarioAIOptions
	private int receptiveFieldWidth = 19; // to be setup via MarioAIOptions
	
	private HashMap<Agent, byte[][]> levelSceneZ;
	//private byte[][] levelSceneZ;     // memory is allocated in reset
	private HashMap<Agent, byte[][]> enemiesZ;
	//private byte[][] enemiesZ;      // memory is allocated in reset
	
	private HashMap<Agent, byte[][]> mergedZZ;
	//private byte[][] mergedZZ;      // memory is allocated in reset

	private HashMap<Agent, int[]> serializedLevelScene;
	//private int[] serializedLevelScene;   // memory is allocated in reset
	private HashMap<Agent, int[]> serializedEnemies;
	//private int[] serializedEnemies;      // memory is allocated in reset
	private HashMap<Agent, int[]> serializedMergedObservation;
	//private int[] serializedMergedObservation; // memory is allocated in reset
	
	//private RenderScene renderScene;
	
	public Level level;
	
	private GraphicsConfiguration graphicsConfiguration;
	
	public MultipleAIEnvironment(GraphicsConfiguration graphicsConfiguration) {	
		this.graphicsConfiguration = graphicsConfiguration;
		
		// Generate the level
		level = LevelGenerator.createLevel(320, 15, new Random().nextLong(),0,0);
		
		//renderScene = new RenderScene(level);
		
		aiPairs = new HashMap<Agent, LevelScene>();
	}

	
	@Override
	public void tick() {
		//renderScene.tick();
		
		// Tick each level scene
		for(LevelScene levelScene : aiPairs.values()) {
			levelScene.tick();
		}
		
		// for each agent
		// Integrate observation
		for(Agent agent : aiPairs.keySet()) {
			agent.integrateObservation(this);
		}
		
		// for each      agent / levelscene
		//          get action / perform action
		for(Entry<Agent, LevelScene> pair : aiPairs.entrySet()) {
			pair.getValue().performAction(pair.getKey().getAction());
		}
	}

	
	@Override
	public void reset() {
        serializedLevelScene = new HashMap<Agent, int[]>();
        serializedEnemies = new HashMap<Agent, int[]>();
        serializedMergedObservation = new HashMap<Agent, int[]>();
        
        levelSceneZ = new HashMap<Agent, byte[][]>();
        enemiesZ = new HashMap<Agent, byte[][]>();
        mergedZZ = new HashMap<Agent, byte[][]>();
        
        //renderScene.reset();
        
        aiPairs.clear();
//		for(LevelScene levelScene : aiPairs.values()) {
//			levelScene.reset();
//		}
        
	    // Add test agent
	    addAgent(new ForwardAgent());
	    addAgent(new RandomAgent());
	}
	
	private Agent followAgent;
	
	public Mario getMarioToFollow()
	{
		Mario mario = aiPairs.get(followAgent).mario;
		
		//renderScene.mario = mario;
		
		return mario;
	}
	
	public List<Sprite> getSprites()
	{
		ArrayList<Sprite> returnSprites = new ArrayList<Sprite>();
		
		//returnSprites.addAll(renderScene.sprites);		
		
		//LevelScene blug = null;
		for(LevelScene levelScene : aiPairs.values()) {
			returnSprites.addAll(levelScene.sprites);
		}
	
		for(LevelScene levelScene : aiPairs.values()) {
			returnSprites.add(levelScene.mario);
		}
		//returnSprites.addAll(blug.sprites);
		
	    return returnSprites;
	}
	
	public int getTick()
	{
		for(LevelScene levelScene : aiPairs.values()) {
			return levelScene.tickCount;
		
		}
		return 0;
		
	    //return renderScene.tickCount;
	}
	
	public void addAgent(Agent agent) {
		// Each agent gets it's own copy of a levelscene
		LevelScene levelScene = new LevelScene(graphicsConfiguration, null, level);
		levelScene.reset();
		
		aiPairs.put(agent, levelScene);
		
		//renderScene.mario = levelScene.mario;
		
		agent.reset();
		agent.setObservationDetails(receptiveFieldWidth, receptiveFieldHeight,marioEgoPos[0],marioEgoPos[1]);
		
		followAgent = agent;
	}
	
	@Override
	public void resetDefault() {
		reset();
	}

	@Override
	public void reset(String setUpOptions) {
		reset();
	}

	@Override
	public float[] getMarioFloatPos(Agent agent) {		
		if(aiPairs.containsKey(agent))
			return aiPairs.get(agent).getMarioFloatPos();
		
		return null;
	}

	@Override
	public int getMarioMode(Agent agent) {
		if(aiPairs.containsKey(agent))
			return aiPairs.get(agent).getMarioMode();
		
		return 0;
	}

	@Override
	public float[] getEnemiesFloatPos(Agent agent) {
		if(aiPairs.containsKey(agent))
			return aiPairs.get(agent).getEnemiesFloatPos();
		
		return null;
	}

	@Override
	public boolean isMarioOnGround(Agent agent) {
		if(aiPairs.containsKey(agent))
			return aiPairs.get(agent).isMarioOnGround();
		
		return false;
	}

	@Override
	public boolean isMarioAbleToJump(Agent agent) {
		if(aiPairs.containsKey(agent))
			return aiPairs.get(agent).isMarioAbleToJump();
		
		return false;
	}

	@Override
	public boolean isMarioCarrying(Agent agent) {
		if(aiPairs.containsKey(agent))
			return aiPairs.get(agent).isMarioCarrying();
		
		return false;
	}

	@Override
	public boolean isMarioAbleToShoot(Agent agent) {
		if(aiPairs.containsKey(agent))
			return aiPairs.get(agent).isMarioAbleToShoot();
		
		return false;
	}

	@Override
	public int getReceptiveFieldWidth() {
		return receptiveFieldWidth;
	}

	@Override
	public int getReceptiveFieldHeight() {
		return receptiveFieldHeight;
	}

	@Override
	public byte[][] getMergedObservationZZ(Agent agent, int ZLevelScene, int ZLevelEnemies) {
		if(!aiPairs.containsKey(agent)) return null;
		
		LevelScene levelScene = aiPairs.get(agent);
		
		int mCol = marioEgoPos[1];
		int mRow = marioEgoPos[0];
		
		byte[][] mergedZZ = new byte[receptiveFieldHeight][receptiveFieldWidth];
		
		for (int y = levelScene.mario.mapY - mRow, row = 0; y <= levelScene.mario.mapY + (receptiveFieldHeight - mRow - 1); y++, row++)
		{
			for (int x = levelScene.mario.mapX - mCol, col = 0; x <= levelScene.mario.mapX + (receptiveFieldWidth - mCol - 1); x++, col++)
			{
				if (x >= 0 && x < levelScene.level.xExit && y >= 0 && y < levelScene.level.height)
					mergedZZ[row][col] = GeneralizerLevelScene.ZLevelGeneralization(levelScene.level.map[x][y], ZLevelScene);	
				else
					mergedZZ[row][col] = 0;
		    }
		}
		    
	    for (Sprite sprite : levelScene.sprites)
	    {
	        if (sprite.isDead() || sprite.kind == levelScene.mario.kind)
	            continue;
	        if (sprite.mapX >= 0 &&
	                sprite.mapX >= levelScene.mario.mapX - mCol &&
	                sprite.mapX <= levelScene.mario.mapX + (receptiveFieldWidth - mCol - 1) &&
	                sprite.mapY >= 0 &&
	                sprite.mapY >= levelScene.mario.mapY - mRow &&
	                sprite.mapY <= levelScene.mario.mapY + (receptiveFieldHeight - mRow - 1) &&
	                sprite.kind != Sprite.KIND_PRINCESS)
	        {
	            int row = sprite.mapY - levelScene.mario.mapY + mRow;
	            int col = sprite.mapX - levelScene.mario.mapX + mCol;
	            byte tmp = GeneralizerEnemies.ZLevelGeneralization(sprite.kind, ZLevelEnemies);
	            if (tmp != Sprite.KIND_NONE)
	                mergedZZ[row][col] = tmp;
	        }
	    }
	    
	    this.mergedZZ.put(agent, mergedZZ);
	    
	    return mergedZZ;
	}

	@Override
	public byte[][] getLevelSceneObservationZ(Agent agent, int ZLevel) {
		if(!aiPairs.containsKey(agent)) return null;
		
		LevelScene levelScene = aiPairs.get(agent);
		
		if(!mergedZZ.containsKey(agent))
			this.mergedZZ.put(agent, new byte[receptiveFieldHeight][receptiveFieldWidth]);
		
		byte[][] mergedZZ = this.mergedZZ.get(agent);
		
	    int mCol = marioEgoPos[1];
	    int mRow = marioEgoPos[0];
	    
	    byte[][] levelSceneZ = new byte[receptiveFieldHeight][receptiveFieldWidth];
	    
	    for (int y = levelScene.mario.mapY - mRow, row = 0; y <= levelScene.mario.mapY + (receptiveFieldHeight - mRow - 1); y++, row++)
	    {
	        for (int x = levelScene.mario.mapX - mCol, col = 0; x <= levelScene.mario.mapX + (receptiveFieldWidth - mCol - 1); x++, col++)
	        {
	            if (x >= 0 && x < levelScene.level.width && y >= 0 && y < levelScene.level.height)
	            {
	                mergedZZ[row][col] = levelSceneZ[row][col] = GeneralizerLevelScene.ZLevelGeneralization(levelScene.level.map[x][y], ZLevel);
	            } else
	            {
	                mergedZZ[row][col] = levelSceneZ[row][col] = 0;
	            }
	        }
	    }
	    
	    this.levelSceneZ.put(agent, levelSceneZ);
	    
	    return levelSceneZ;
	}

	@Override
	public byte[][] getEnemiesObservationZ(Agent agent, int ZLevel) {
		if(!aiPairs.containsKey(agent)) return null;
		
		LevelScene levelScene = aiPairs.get(agent);
		
		if(!mergedZZ.containsKey(agent))
			this.mergedZZ.put(agent, new byte[receptiveFieldHeight][receptiveFieldWidth]);
		
		byte[][] mergedZZ = this.mergedZZ.get(agent);
		
	    int marioEgoCol = marioEgoPos[1];
	    int marioEgoRow = marioEgoPos[0];
	    
	    byte[][] enemiesZ = new byte[receptiveFieldHeight][receptiveFieldWidth];
	    
	    for (int w = 0; w < enemiesZ.length; w++)
	        for (int h = 0; h < enemiesZ[0].length; h++)
	            enemiesZ[w][h] = 0;
	    for (Sprite sprite : levelScene.sprites)
	    {
	        if (sprite.isDead() || sprite.kind == levelScene.mario.kind)
	            continue;
	        if (sprite.mapX >= 0 &&
	                sprite.mapX >= levelScene.mario.mapX - marioEgoCol &&
	                sprite.mapX <= levelScene.mario.mapX + (receptiveFieldWidth - marioEgoCol - 1) &&
	                sprite.mapY >= 0 &&
	                sprite.mapY >= levelScene.mario.mapY - marioEgoRow &&
	                sprite.mapY <= levelScene.mario.mapY + (receptiveFieldHeight - marioEgoRow - 1) &&
	                sprite.kind != Sprite.KIND_PRINCESS)
	        {
	            int row = sprite.mapY - levelScene.mario.mapY + marioEgoRow;
	            int col = sprite.mapX - levelScene.mario.mapX + marioEgoCol;

	            mergedZZ[row][col] = enemiesZ[row][col] = GeneralizerEnemies.ZLevelGeneralization(sprite.kind, ZLevel);
	        }
	    }
	    
	    this.enemiesZ.put(agent, enemiesZ);
	    
	    return enemiesZ;
	}

	@Override
	public int[] getSerializedFullObservationZZ(Agent agent, int ZLevelScene, int ZLevelEnemies) {
	    int[] obs = new int[receptiveFieldHeight * receptiveFieldWidth * 2 + 11]; // 11 is a size of the MarioState array

	    int receptiveFieldSize = receptiveFieldWidth * receptiveFieldHeight;

	    System.arraycopy(getSerializedLevelSceneObservationZ(agent, ZLevelScene), 0, obs, 0, receptiveFieldSize);
	    System.arraycopy(getSerializedEnemiesObservationZ(agent, ZLevelScene), 0, obs, receptiveFieldSize, receptiveFieldSize);
	    System.arraycopy(getMarioState(agent), 0, obs, receptiveFieldSize * 2, 11);

	    return obs;
	}

	@Override
	public int[] getSerializedLevelSceneObservationZ(Agent agent, int ZLevelScene) {
		if(!aiPairs.containsKey(agent)) return null;
		
	    byte[][] levelScene = this.getLevelSceneObservationZ(agent, ZLevelScene);
	    
	    int[] serializedLevelScene = new int[receptiveFieldHeight * receptiveFieldWidth];
	    
	    for (int i = 0; i < serializedLevelScene.length; ++i)
	    {
	        final int i1 = i / receptiveFieldWidth;
	        final int i2 = i % receptiveFieldWidth;
	        serializedLevelScene[i] = (int) levelScene[i1][i2];
	    }
	    
	    this.serializedLevelScene.put(agent, serializedLevelScene);
	    
	    return serializedLevelScene;
	}

	@Override
	public int[] getSerializedEnemiesObservationZ(Agent agent, int ZLevelEnemies) {
		if(!aiPairs.containsKey(agent)) return null;
		
	    int[] serializedEnemies = new int[receptiveFieldHeight * receptiveFieldWidth];
	    
	    // serialization into arrays of primitive types to speed up the data transfer.
	    byte[][] enemies = this.getEnemiesObservationZ(agent, ZLevelEnemies);
	    for (int i = 0; i < serializedEnemies.length; ++i)
	        serializedEnemies[i] = (int) enemies[i / receptiveFieldWidth][i % receptiveFieldWidth];
	    
	    this.serializedEnemies.put(agent, serializedEnemies);
	    
	    return serializedEnemies;
	}

	@Override
	public int[] getSerializedMergedObservationZZ(Agent agent, int ZLevelScene, int ZLevelEnemies) {
		if(!aiPairs.containsKey(agent)) return null;
		
		int[] serializedMergedObservation = new int[receptiveFieldHeight * receptiveFieldWidth];
		
	    // serialization into arrays of primitive types to speed up the data transfer.
	    byte[][] merged = this.getMergedObservationZZ(agent, ZLevelScene, ZLevelEnemies);
	    for (int i = 0; i < serializedMergedObservation.length; ++i)
	        serializedMergedObservation[i] = (int) merged[i / receptiveFieldWidth][i % receptiveFieldWidth];
	    
	    this.serializedMergedObservation.put(agent, serializedMergedObservation);
	    
	    return serializedMergedObservation;
	}

	@Override
	public float[] getCreaturesFloatPos(Agent agent) {
		if(aiPairs.containsKey(agent))
			return aiPairs.get(agent).getCreaturesFloatPos();
		
		return null;
	}

	@Override
	public int getKillsTotal(Agent agent) {
		if(aiPairs.containsKey(agent))
			return aiPairs.get(agent).getKillsTotal();
		
		return 0;
	}

	@Override
	public int getKillsByFire(Agent agent) {
		if(aiPairs.containsKey(agent))
			return aiPairs.get(agent).getKillsByFire();
		
		return 0;
	}

	@Override
	public int getKillsByStomp(Agent agent) {
		if(aiPairs.containsKey(agent))
			return aiPairs.get(agent).getKillsByStomp();
		
		return 0;
	}

	@Override
	public int getKillsByShell(Agent agent) {
		if(aiPairs.containsKey(agent))
			return aiPairs.get(agent).getKillsByShell();
		
		return 0;
	}

	@Override
	public int getMarioStatus(Agent agent) {
		if(aiPairs.containsKey(agent))
			return aiPairs.get(agent).getMarioStatus();
		
		return 0;
	}

	@Override
	public int[] getMarioState(Agent agent) {
		if(aiPairs.containsKey(agent))
			return aiPairs.get(agent).getMarioState();
		
		return null;
	}

	@Override
	public void performAction(Agent agent, boolean[] action) {
		if(aiPairs.containsKey(agent))
			aiPairs.get(agent).performAction(action);
	}

	@Override
	public boolean isLevelFinished(Agent agent) {
		if(aiPairs.containsKey(agent))
			return aiPairs.get(agent).isLevelFinished();
		
		return false;
	}

	@Override
	public int[] getEvaluationInfoAsInts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEvaluationInfoAsString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAgent(Agent agent) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getIntermediateReward(Agent agent) {
		if(aiPairs.containsKey(agent))
			return aiPairs.get(agent).getBonusPoints();
		
		return 0;
	}

	@Override
	public int[] getMarioEgoPos() {
	    return marioEgoPos;
	}

	@Override
	public void closeRecorder() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setReplayer(Replayer recorder) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getTimeSpent(Agent agent) {
		if(aiPairs.containsKey(agent))
			return aiPairs.get(agent).getTimeSpent();
		
		return 0;
	}

	@Override
	public byte[][] getScreenCapture() {
		return null;
	}

	@Override
	public void saveLastRun(String filename) {

	}
}
