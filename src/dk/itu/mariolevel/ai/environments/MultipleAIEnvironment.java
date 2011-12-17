package dk.itu.mariolevel.ai.environments;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import competition.gic2010.gameplay.grammaticalbehaviors.GEBT_Mario.GEBT_MarioAgent;

import dk.itu.mariolevel.ai.GeneralizerEnemies;
import dk.itu.mariolevel.ai.GeneralizerLevelScene;
import dk.itu.mariolevel.ai.agents.Agent;
import dk.itu.mariolevel.ai.agents.ForwardAgent;
import dk.itu.mariolevel.ai.agents.HumanKeyboardAgent;
import dk.itu.mariolevel.ai.agents.RandomAgent;
import dk.itu.mariolevel.engine.CameraHandler;
import dk.itu.mariolevel.engine.MarioTracker;
import dk.itu.mariolevel.engine.level.Level;
import dk.itu.mariolevel.engine.level.LevelGenerator;
import dk.itu.mariolevel.engine.scene.AIScene;
import dk.itu.mariolevel.engine.scene.RenderScene;
import dk.itu.mariolevel.engine.sprites.Sprite;

public class MultipleAIEnvironment implements Environment {
	public static final int AI_SET_ALL = 0;
	public static final int AI_SET_SIMPLE = 1;
	public static final int AI_SET_COMPLEX = 2;
	public static final int AI_SET_PLAYER = 3;
	
	private HashMap<Agent, AIScene> aiPairs;
	
	private int[] marioEgoPos = new int[]{9,9};
	private int receptiveFieldHeight = 22; // standard value
	private int receptiveFieldWidth = 22; // standard value
	
	private HashMap<Agent, byte[][]> aiSceneZ;
	private HashMap<Agent, byte[][]> enemiesZ;
	private HashMap<Agent, byte[][]> mergedZZ;

	private HashMap<Agent, int[]> serializedAiScene;
	private HashMap<Agent, int[]> serializedEnemies;
	private HashMap<Agent, int[]> serializedMergedObservation;
	
	private RenderScene renderScene;
	
	private AIScene playScene;
	private HumanKeyboardAgent playAgent;
	
	private Level level;
	
	private boolean left, right, speedScroll;
	
	private boolean politeReset;
	
	private int currentAISet, nextAISet;
	
	public MultipleAIEnvironment() {	
		// Generate the level
		level = LevelGenerator.createEditorLevel(100, 15);

		aiPairs = new HashMap<Agent, AIScene>();
	}

	public void toggleKey(int keyCode, boolean isPressed){
		
		if(currentAISet == AI_SET_PLAYER) {
			playAgent.toggleKey(keyCode, isPressed);
		}
		else {
			if (keyCode == KeyEvent.VK_LEFT)
				left = isPressed;
			if (keyCode == KeyEvent.VK_RIGHT)
				right = isPressed;
			if (keyCode == KeyEvent.VK_SHIFT)
				speedScroll = isPressed;
		}
	}
	
	public void changeAISet(int set) {
		nextAISet = set;
		politeReset = true;
	}
	
	public boolean canEdit() {
		return currentAISet == AI_SET_SIMPLE;
	}
	
	private void initializeAISet() {

		aiPairs.clear();
		
		if(currentAISet == AI_SET_COMPLEX || currentAISet == AI_SET_ALL) {
			addAgent(new GEBT_MarioAgent());
//			addAgent(new AStarAgent());
		}
		
		if(currentAISet == AI_SET_SIMPLE || currentAISet == AI_SET_ALL) {
    	    addAgent(new ForwardAgent());
    	    addAgent(new RandomAgent());
		}
		
		if(currentAISet == AI_SET_PLAYER) {
        	playAgent = new HumanKeyboardAgent();
        	playAgent.reset();
        	
        	playScene = new AIScene(new Level(level));
        	playScene.reset();
        	
        	CameraHandler.getInstance().setFollowMario(playScene.mario);
		}
		
		if(currentAISet != AI_SET_PLAYER) {
			renderScene = new RenderScene(new Level(level));
            
            CameraHandler.getInstance().setFollowMario(null);
		}
	}
	
	public void changeLevel(Level level) {
		this.level = level;
		this.reset();
	}
	
	public Level getLevel() {
		return level;
	}
	
	public Level getLevelToRender() {
		if(currentAISet == AI_SET_PLAYER)
			return playScene.level;
			
		return level;
	}
	
	private void addTracing() {
		MarioTracker.getInstance().removeAllTracing();
		
		if(currentAISet == AI_SET_PLAYER) {
			MarioTracker.getInstance().addTracing(playScene);
		}
		else {
			for(AIScene aiScene : aiPairs.values()) 
				MarioTracker.getInstance().addTracing(aiScene);
		}
	}
	
	public String getAgentName(AIScene levelScene) {
		if(levelScene == null) return "";
		
		
		
		for(Agent agent : aiPairs.keySet()) {
			if(aiPairs.get(agent).equals(levelScene)) {
				return agent.getName();
			}
		}
		
		if(levelScene.equals(playScene)) {
			return playAgent.getName();
		}
		
		return "";
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
		
		if(currentAISet == AI_SET_PLAYER) {
			playScene.tick();
			playScene.performAction(playAgent.getAction());
		}
		else {
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
		
		MarioTracker.getInstance().tick();
	}

	@Override
	public void reset() {
		politeReset = true;
	}
	
	public void actualReset() {
        serializedAiScene = new HashMap<Agent, int[]>();
        serializedEnemies = new HashMap<Agent, int[]>();
        serializedMergedObservation = new HashMap<Agent, int[]>();
        
        aiSceneZ = new HashMap<Agent, byte[][]>();
        enemiesZ = new HashMap<Agent, byte[][]>();
        mergedZZ = new HashMap<Agent, byte[][]>();

        currentAISet = nextAISet;
        
        initializeAISet();
        
        addTracing();
	}
	
	public List<Sprite> getSprites()
	{
		ArrayList<Sprite> returnSprites = new ArrayList<Sprite>();
		
		if(currentAISet == AI_SET_PLAYER) {
			returnSprites.addAll(playScene.sprites);
		}
		else {
			returnSprites.addAll(renderScene.sprites);		
			
			for(AIScene aiscene : aiPairs.values()) {
				returnSprites.add(aiscene.mario);
			}
		}
		
	    return returnSprites;
	}
	
	public int getTick()
	{
		if(currentAISet == AI_SET_PLAYER)
			return playScene.tickCount;

		return renderScene.tickCount;
	}
	
	public void addAgent(Agent agent) {
		// Each agent gets it's own copy of a aiscene
		AIScene aiScene = null;
		
		aiScene = new AIScene(new Level(level));
		
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
	public int[] getMarioEgoPos() {
	    return marioEgoPos;
	}

	@Override
	public int getTimeSpent(Agent agent) {
		if(aiPairs.containsKey(agent))
			return aiPairs.get(agent).getTimeSpent();
		
		return 0;
	}
}
