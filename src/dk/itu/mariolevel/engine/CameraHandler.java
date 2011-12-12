package dk.itu.mariolevel.engine;

import java.awt.Point;

import dk.itu.mariolevel.engine.sprites.Mario;

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
	
	public int width, height;
	
	private Mario followMario;
	
	public void tick() {
		if(followMario != null){
			camX = (int) (followMario.x - width/2);
			camY = 0;
			
			if(camX + width > maxX)
				camX = maxX - width;
	    	
	    	if(camX < minX) camX = minX;
		}
	}
	
	public void setFollowMario(Mario mario) {
		followMario = mario;
	}
	
	public Mario getFollowMario() {
		return followMario;
	}
	
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
	
	public void setScreenSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public Point mousePointToTile(Point mousePos) {
    	int xTile = (mousePos.x + (camX*2)) / 32;
		int yTile = (mousePos.y + (camY*2)) / 32;
		
		return new Point(xTile, yTile);
	}
}
