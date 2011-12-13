package dk.itu.mariolevel.engine;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

public class TraceHolder {
	private final static int MAX_TRACES = 5;
	
	private int currentTrace;
	private ArrayList<ArrayList<Point>> traces;
	private HashMap<Point, Boolean> finishMap;
	
	public TraceHolder() {
		traces = new ArrayList<ArrayList<Point>>();
		finishMap = new HashMap<Point, Boolean>();
	}
	
	public void addToTrack(Point point) {
		if(currentTrace <= traces.size())
			traces.add(new ArrayList<Point>());
		
		// Avoid additional points the same place
		int traceSize = traces.get(currentTrace).size();
		
		Point lastPoint = null;
		
		if(traceSize > 0)
			lastPoint = traces.get(currentTrace).get(traceSize-1);
		
		if(lastPoint != point)
			traces.get(currentTrace).add(point);
	}
	
	public void addFinish(Point point, boolean win) {
		currentTrace++;
		
		if(currentTrace == MAX_TRACES -1) {
			traces.remove(0);
			finishMap.remove(0);
			
			currentTrace--;
		}
		
		finishMap.put(point, win);
	}
	
	public void clearTracks() {
		currentTrace = 0;
		
		traces.clear();
		finishMap.clear();
	}
}
