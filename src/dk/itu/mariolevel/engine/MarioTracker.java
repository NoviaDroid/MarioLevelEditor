package dk.itu.mariolevel.engine;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map.Entry;

import dk.itu.mariolevel.engine.scene.LevelScene;
import dk.itu.mariolevel.engine.sprites.Mario;

public class MarioTracker {
	private final static int POS_TRACE_INTERVAL = 20;
	
	private static MarioTracker _instance;
	
	public static MarioTracker getInstance() {
		if(_instance == null)
			_instance = new MarioTracker();
		
		return _instance;
	}
	
	private HashMap<LevelScene, TraceHolder> traceMap;
	
	private int tick;
	
	private MarioTracker() {
		traceMap = new HashMap<LevelScene, TraceHolder>();
	}
	
	public void addTracing(LevelScene levelScene) {
		if(!traceMap.containsKey(levelScene)) {
			traceMap.put(levelScene, new TraceHolder());
		}
	}
	
	public void clearTracing() {
		traceMap = new HashMap<LevelScene, TraceHolder>();
		tick = 0;
	}
	
	public void tick() {
		tick++;
		
		for(Entry<LevelScene, TraceHolder> pair : traceMap.entrySet()) {
			Mario mario = pair.getKey().mario;
			TraceHolder traceHolder = pair.getValue();
			
			if(mario.getStatus() == Mario.STATUS_DEAD || mario.getStatus() == Mario.STATUS_WIN) {
				traceHolder.addFinish(new Point(mario.xDeathPos, mario.yDeathPos), mario.getStatus() == Mario.STATUS_WIN);
				
				System.out.println(mario.getStatus() == Mario.STATUS_WIN ? "Win" : "Death");
			}
			else {
				if(tick % POS_TRACE_INTERVAL == 0) {
					traceHolder.addToTrack(new Point((int)mario.x, (int)mario.y));
					
					System.out.println("Tracked pos: " + (int)mario.x + ", " + (int)mario.y);
				}
			}
		}
	}
}
