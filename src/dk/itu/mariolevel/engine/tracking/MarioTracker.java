package dk.itu.mariolevel.engine.tracking;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import dk.itu.mariolevel.engine.scene.LevelScene;
import dk.itu.mariolevel.engine.sprites.Mario;

public class MarioTracker {
	private final static int POS_TRACE_INTERVAL = 1;
	
	private static MarioTracker _instance;
	
	public static MarioTracker getInstance() {
		if(_instance == null)
			_instance = new MarioTracker();
		
		return _instance;
	}
	
	private HashMap<LevelScene, TraceHolder> traceMap;
	
	private int tick;
	
	private boolean trace;
	
	private MarioTracker() {
		traceMap = new HashMap<LevelScene, TraceHolder>();
	}
	
	public void addTracing(LevelScene levelScene) {
		if(!traceMap.containsKey(levelScene)) {
			traceMap.put(levelScene, new TraceHolder());
		}
	}
	
	public void removeTracing(LevelScene levelScene) {
		traceMap.remove(levelScene);
	}
	
	public void removeAllTracing() {
		traceMap = new HashMap<LevelScene, TraceHolder>();
		tick = 0;
	}
	
	public void toggleShowTracing() {
		trace = !trace;
	}
	
	public boolean showTracing() {
		return trace;
	}
	
	public List<TraceHolder> getTracings() {
		ArrayList<TraceHolder> returnList = new ArrayList<TraceHolder>();
		
		returnList.addAll(traceMap.values());
		
		return returnList;
	}
	
	public ArrayList<FinishPoint> getTraceAndFinish(boolean deathsOnly) {
		ArrayList<FinishPoint> returnList = new ArrayList<FinishPoint>();
		
		for(TraceHolder holder : traceMap.values()) {
			returnList.addAll(holder.getTraceAndFinish(deathsOnly));
		}

		return returnList;
	}
	
	public void tick() {
		tick++;
		
		for(Entry<LevelScene, TraceHolder> pair : traceMap.entrySet()) {
			Mario mario = pair.getKey().mario;
			TraceHolder traceHolder = pair.getValue();
			
			if((mario.getStatus() == Mario.STATUS_DEAD && mario.deathTime == 1) || mario.getStatus() == Mario.STATUS_WIN)
				traceHolder.addFinish(new Point(mario.xDeathPos, mario.yDeathPos), mario.getStatus() == Mario.STATUS_WIN);
			else if(mario.getStatus() == Mario.STATUS_RUNNING && tick % POS_TRACE_INTERVAL == 0)
				traceHolder.addToTrack(new Point((int) mario.x, (int) (mario.y - 7)));
		}
	}
}