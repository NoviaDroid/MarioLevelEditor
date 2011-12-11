package dk.itu.mariolevel.ai.environments;


import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import dk.itu.mariolevel.ai.GeneralizerEnemies;
import dk.itu.mariolevel.ai.GeneralizerLevelScene;
import dk.itu.mariolevel.ai.SystemOfValues;
import dk.itu.mariolevel.ai.agents.Agent;
import dk.itu.mariolevel.ai.agents.ForwardAgent;
import dk.itu.mariolevel.ai.agents.RandomAgent;
import dk.itu.mariolevel.engine.CameraHandler;
import dk.itu.mariolevel.engine.Replayer;
import dk.itu.mariolevel.engine.level.Level;
import dk.itu.mariolevel.engine.level.LevelGenerator;
import dk.itu.mariolevel.engine.scene.AIScene;
import dk.itu.mariolevel.engine.scene.RenderScene;
import dk.itu.mariolevel.engine.sprites.Sprite;

public class MultipleAIEnvironment implements Environment {

	public static final int TICKS_PER_SECOND = 24;
	
	public static SystemOfValues IntermediateRewardsSystemOfValues = new SystemOfValues();
	
	private HashMap<Agent, AIScene> aiPairs;
	
	private int[] marioEgoPos = new int[]{9,9};
	private int receptiveFieldHeight = 19; // standard value
	private int receptiveFieldWidth = 19; // standard value
	
	private HashMap<Agent, byte[][]> aiSceneZ;
	private HashMap<Agent, byte[][]> enemiesZ;
	private HashMap<Agent, byte[][]> mergedZZ;

	private HashMap<Agent, int[]> serializedAiScene;
	private HashMap<Agent, int[]> serializedEnemies;
	private HashMap<Agent, int[]> serializedMergedObservation;
	
	private RenderScene renderScene;
	
	public Level level;
	
	private boolean left, right, speedScroll;
	
	public MultipleAIEnvironment() {	
		// Generate the level
//		level = LevelGenerator.createLevel(320, 15, new Random().nextLong(),0,0);
		
		level = LevelGenerator.createEditorLevel(320, 15);
//		try {
//			level = Level.load(new ObjectInputStream(new FileInputStream("test.lvl")));
//		} catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		renderScene = new RenderScene(new Level(level));
		
		aiPairs = new HashMap<Agent, AIScene>();
	}

	public void toggleKey(int keyCode, boolean isPressed){
		if (keyCode == KeyEvent.VK_LEFT)
			left = isPressed;
		if (keyCode == KeyEvent.VK_RIGHT)
			right = isPressed;
		if (keyCode == KeyEvent.VK_SHIFT)
			speedScroll = isPressed;
	}
	
	@Override
	public void tick() {
		if(politeReset) {
			politeReset = false;
			actualReset();
		}
		
		if(right) {
			CameraHandler.getInstance().moveCamera(speedScroll ? 100 : 20, 0);
		}
		if(left) {
			CameraHandler.getInstance().moveCamera(speedScroll ? -100 : -20, 0);
		}
		
		renderScene.tick();
		
		// Tick each level scene
		for(AIScene aiScene : aiPairs.values()) {
			aiScene.tick();
		}
		
		// for each agent
		// Integrate observation
		for(Agent agent : aiPairs.keySet()) {
			agent.integrateObservation(this);
		}
		
		// for each      agent / aiscene
		//          get action / perform action
		for(Entry<Agent, AIScene> pair : aiPairs.entrySet()) {
			pair.getValue().performAction(pair.getKey().getAction());
		}
	}

	private boolean politeReset;
	
	@Override
	public void reset() {
		politeReset = true;
	}
	
	private void actualReset() {
        serializedAiScene = new HashMap<Agent, int[]>();
        serializedEnemies = new HashMap<Agent, int[]>();
        serializedMergedObservation = new HashMap<Agent, int[]>();
        
        aiSceneZ = new HashMap<Agent, byte[][]>();
        enemiesZ = new HashMap<Agent, byte[][]>();
        mergedZZ = new HashMap<Agent, byte[][]>();
        
        renderScene.level = new Level(level);
        renderScene.reset();
        
        aiPairs.clear();
//		for(LevelScene levelScene : aiPairs.values()) {
//			levelScene.reset();
//		}
        
	    // Add test agent
	    addAgent(new ForwardAgent());
	    addAgent(new RandomAgent());
	}
	
	public List<Sprite> getSprites()
	{
		ArrayList<Sprite> returnSprites = new ArrayList<Sprite>();
		
		returnSprites.addAll(renderScene.sprites);		
		
		for(AIScene aiscene : aiPairs.values()) {
			returnSprites.add(aiscene.mario);
		}
		
	    return returnSprites;
	}
	
	public int getTick()
	{
	    return renderScene.tickCount;
	}
	
	public void addAgent(Agent agent) {
		// Each agent gets it's own copy of a aiscene
		AIScene aiScene = new AIScene(new Level(level));
		aiScene.reset();
		
		aiPairs.put(agent, aiScene);

		agent.reset();
		agent.setObservationDetails(receptiveFieldWidth, receptiveFieldHeight,marioEgoPos[0],marioEgoPos[1]);
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
	public byte[][] getMergedObservationZZ(Agent agent, int ZaiScene, int ZLevelEnemies) {
		if(!aiPairs.containsKey(agent)) return null;
		
		AIScene aiScene = aiPairs.get(agent);
		
		int mCol = marioEgoPos[1];
		int mRow = marioEgoPos[0];
		
		byte[][] mergedZZ = new byte[receptiveFieldHeight][receptiveFieldWidth];
		
		for (int y = aiScene.mario.mapY - mRow, row = 0; y <= aiScene.mario.mapY + (receptiveFieldHeight - mRow - 1); y++, row++)
		{
			for (int x = aiScene.mario.mapX - mCol, col = 0; x <= aiScene.mario.mapX + (receptiveFieldWidth - mCol - 1); x++, col++)
			{
				if (x >= 0 && x < aiScene.level.xExit && y >= 0 && y < aiScene.level.height)
					mergedZZ[row][col] = GeneralizerLevelScene.ZLevelGeneralization(aiScene.level.map[x][y], ZaiScene);	
				else
					mergedZZ[row][col] = 0;
		    }
		}
		    
	    for (Sprite sprite : aiScene.sprites)
	    {
	        if (sprite.isDead() || sprite.kind == aiScene.mario.kind)
	            continue;
	        if (sprite.mapX >= 0 &&
	                sprite.mapX >= aiScene.mario.mapX - mCol &&
	                sprite.mapX <= aiScene.mario.mapX + (receptiveFieldWidth - mCol - 1) &&
	                sprite.mapY >= 0 &&
	                sprite.mapY >= aiScene.mario.mapY - mRow &&
	                sprite.mapY <= aiScene.mario.mapY + (receptiveFieldHeight - mRow - 1) &&
	                sprite.kind != Sprite.KIND_PRINCESS)
	        {
	            int row = sprite.mapY - aiScene.mario.mapY + mRow;
	            int col = sprite.mapX - aiScene.mario.mapX + mCol;
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
		
		AIScene aiScene = aiPairs.get(agent);
		
		if(!mergedZZ.containsKey(agent))
			this.mergedZZ.put(agent, new byte[receptiveFieldHeight][receptiveFieldWidth]);
		
		byte[][] mergedZZ = this.mergedZZ.get(agent);
		
	    int mCol = marioEgoPos[1];
	    int mRow = marioEgoPos[0];
	    
	    byte[][] aiSceneZ = new byte[receptiveFieldHeight][receptiveFieldWidth];
	    
	    for (int y = aiScene.mario.mapY - mRow, row = 0; y <= aiScene.mario.mapY + (receptiveFieldHeight - mRow - 1); y++, row++)
	    {
	        for (int x = aiScene.mario.mapX - mCol, col = 0; x <= aiScene.mario.mapX + (receptiveFieldWidth - mCol - 1); x++, col++)
	        {
	            if (x >= 0 && x < aiScene.level.length && y >= 0 && y < aiScene.level.height)
	            {
	                mergedZZ[row][col] = aiSceneZ[row][col] = GeneralizerLevelScene.ZLevelGeneralization(aiScene.level.map[x][y], ZLevel);
	            } else
	            {
	                mergedZZ[row][col] = aiSceneZ[row][col] = 0;
	            }
	        }
	    }
	    
	    this.aiSceneZ.put(agent, aiSceneZ);
	    
	    return aiSceneZ;
	}

	@Override
	public byte[][] getEnemiesObservationZ(Agent agent, int ZLevel) {
		if(!aiPairs.containsKey(agent)) return null;
		
		AIScene aiScene = aiPairs.get(agent);
		
		if(!mergedZZ.containsKey(agent))
			this.mergedZZ.put(agent, new byte[receptiveFieldHeight][receptiveFieldWidth]);
		
		byte[][] mergedZZ = this.mergedZZ.get(agent);
		
	    int marioEgoCol = marioEgoPos[1];
	    int marioEgoRow = marioEgoPos[0];
	    
	    byte[][] enemiesZ = new byte[receptiveFieldHeight][receptiveFieldWidth];
	    
	    for (int w = 0; w < enemiesZ.length; w++)
	        for (int h = 0; h < enemiesZ[0].length; h++)
	            enemiesZ[w][h] = 0;
	    for (Sprite sprite : aiScene.sprites)
	    {
	        if (sprite.isDead() || sprite.kind == aiScene.mario.kind)
	            continue;
	        if (sprite.mapX >= 0 &&
	                sprite.mapX >= aiScene.mario.mapX - marioEgoCol &&
	                sprite.mapX <= aiScene.mario.mapX + (receptiveFieldWidth - marioEgoCol - 1) &&
	                sprite.mapY >= 0 &&
	                sprite.mapY >= aiScene.mario.mapY - marioEgoRow &&
	                sprite.mapY <= aiScene.mario.mapY + (receptiveFieldHeight - marioEgoRow - 1) &&
	                sprite.kind != Sprite.KIND_PRINCESS)
	        {
	            int row = sprite.mapY - aiScene.mario.mapY + marioEgoRow;
	            int col = sprite.mapX - aiScene.mario.mapX + marioEgoCol;

	            mergedZZ[row][col] = enemiesZ[row][col] = GeneralizerEnemies.ZLevelGeneralization(sprite.kind, ZLevel);
	        }
	    }
	    
	    this.enemiesZ.put(agent, enemiesZ);
	    
	    return enemiesZ;
	}

	@Override
	public int[] getSerializedFullObservationZZ(Agent agent, int ZaiScene, int ZLevelEnemies) {
	    int[] obs = new int[receptiveFieldHeight * receptiveFieldWidth * 2 + 11]; // 11 is a size of the MarioState array

	    int receptiveFieldSize = receptiveFieldWidth * receptiveFieldHeight;

	    System.arraycopy(getSerializedLevelSceneObservationZ(agent, ZaiScene), 0, obs, 0, receptiveFieldSize);
	    System.arraycopy(getSerializedEnemiesObservationZ(agent, ZaiScene), 0, obs, receptiveFieldSize, receptiveFieldSize);
	    System.arraycopy(getMarioState(agent), 0, obs, receptiveFieldSize * 2, 11);

	    return obs;
	}

	@Override
	public int[] getSerializedLevelSceneObservationZ(Agent agent, int ZAiScene) {
		if(!aiPairs.containsKey(agent)) return null;
		
	    byte[][] aiScene = this.getLevelSceneObservationZ(agent, ZAiScene);
	    
	    int[] serializedAiScene = new int[receptiveFieldHeight * receptiveFieldWidth];
	    
	    for (int i = 0; i < serializedAiScene.length; ++i)
	    {
	        final int i1 = i / receptiveFieldWidth;
	        final int i2 = i % receptiveFieldWidth;
	        serializedAiScene[i] = (int) aiScene[i1][i2];
	    }
	    
	    this.serializedAiScene.put(agent, serializedAiScene);
	    
	    return serializedAiScene;
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
	public int[] getSerializedMergedObservationZZ(Agent agent, int ZaiScene, int ZLevelEnemies) {
		if(!aiPairs.containsKey(agent)) return null;
		
		int[] serializedMergedObservation = new int[receptiveFieldHeight * receptiveFieldWidth];
		
	    // serialization into arrays of primitive types to speed up the data transfer.
	    byte[][] merged = this.getMergedObservationZZ(agent, ZaiScene, ZLevelEnemies);
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
