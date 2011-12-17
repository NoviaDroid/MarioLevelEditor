package dk.itu.mariolevel.engine.tracking;

import java.awt.Point;

public class FinishPoint {
	public Point finish;
	public boolean win;
	
	public FinishPoint(Point finish, boolean win) {
		this.finish = finish;
		this.win = win;
	}
}
