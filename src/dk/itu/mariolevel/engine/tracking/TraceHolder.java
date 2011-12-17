package dk.itu.mariolevel.engine.tracking;

import java.awt.Point;
import java.util.ArrayList;

public class TraceHolder {
//	private final static int MAX_TRACES = 10;
	private final static int MAX_POS_TRACE = 200;
	
//	private int currentTrace;
	private ArrayList<ArrayList<Point>> traces;
	private ArrayList<FinishPoint> finishMap;
	
	public TraceHolder() {
		traces = new ArrayList<ArrayList<Point>>();
		finishMap = new ArrayList<FinishPoint>();
		
		traces.add(new ArrayList<Point>());
	}
	
	public void addToTrack(Point point) {
//		if(currentTrace >= traces.size())
//			traces.add(new ArrayList<Point>());
//		
		int currentTrace = traces.size()-1;
		
		// Avoid additional points the same place
		int traceSize = traces.get(traces.size()-1).size();
		
		Point lastPoint = null;
		
		if(traceSize > 0)
			lastPoint = traces.get(currentTrace).get(traceSize-1);
		
		if(lastPoint != point) {
			traces.get(currentTrace).add(point);
			
			// Trim traces
			if(traces.get(currentTrace).size() > MAX_POS_TRACE) traces.get(currentTrace).remove(0);
		}	
	}
	
	public ArrayList<Point> getRecordedTraces() {
		return traces.get(traces.size()-1);
	}
	
	public ArrayList<FinishPoint> getTraceAndFinish(boolean deathsOnly) {
		ArrayList<FinishPoint> returnList = new ArrayList<FinishPoint>();
		
		for(int i = 0; i < finishMap.size(); i++) {
			if(!deathsOnly || !finishMap.get(i).win) {
				returnList.add(finishMap.get(i));
			}
		}
		
		return returnList;
	}
	
	public void addFinish(Point point, boolean win) {
//		currentTrace++;
//		
//		if(currentTrace == MAX_TRACES -1) {
//			traces.remove(0);
//			finishMap.remove(0);
//			
//			currentTrace--;
//		}
//		
		
		System.out.println(point);
		traces.add(new ArrayList<Point>());
		finishMap.add(new FinishPoint(point, win));
	}
}