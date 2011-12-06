package dk.itu.mariolevel.engine;

import java.awt.Point;

public class CameraHandler {
	private static CameraHandler _instance;
	
	public static CameraHandler getInstance() {
		if(_instance == null) _instance = new CameraHandler();
		
		return _instance;
	}
	
	private CameraHandler() {
		camX = camY = 0;
	}
	
	private int camX, camY;
	
	private int minX, maxX, minY, maxY;
	
	private int width, height;
	
	public void moveCamera(int x, int y) {
		camX += x;
		camY += y;
		
		if(camX + width > maxX)
			camX = maxX - width;
    	
    	if(camX < minX) camX = minX;
    	
    	if(camY + height > maxY)
    		camY = maxY - height;
    	
    	if(camY < minY) camY = minY;
	}
	
	public Point getCameraPosition() {
		return new Point(camX, camY);
	}
	
	public void setLimits(int minX, int maxX, int minY, int maxY) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}
	
	public void setScreenSize(int width, int height, boolean scalex2) {
		this.width = width * (scalex2 ? 2 : 1);
		this.height = height * (scalex2 ? 2 : 1);
	}
}