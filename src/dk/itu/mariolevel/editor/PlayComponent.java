package dk.itu.mariolevel.editor;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.VolatileImage;
import java.util.List;

import javax.swing.JComponent;

import dk.itu.mariolevel.MarioPanel;
import dk.itu.mariolevel.ai.environments.MultipleAIEnvironment;
import dk.itu.mariolevel.engine.Art;
import dk.itu.mariolevel.engine.BgRenderer;
import dk.itu.mariolevel.engine.CameraHandler;
import dk.itu.mariolevel.engine.LevelRenderer;
import dk.itu.mariolevel.engine.Scale2x;
import dk.itu.mariolevel.engine.level.BgLevelGenerator;
import dk.itu.mariolevel.engine.level.Level;
import dk.itu.mariolevel.engine.sprites.Sprite;

public class PlayComponent extends JComponent implements Runnable, KeyListener, MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 5552300968020476470L;
	
	public static final int KEY_LEFT = 0;
    public static final int KEY_RIGHT = 1;
    public static final int KEY_DOWN = 2;
    public static final int KEY_UP = 3;
    public static final int KEY_JUMP = 4;
    public static final int KEY_SPEED = 5;
    
    public static final int COMPONENT_WIDTH = 320;
    public static final int COMPONENT_HEIGHT = 240;

    private boolean running = false;
    private int width, height;
    
    private GraphicsConfiguration graphicsConfiguration;

	public VolatileImage thisVolatileImage;
	public Graphics thisVolatileImageGraphics;
	public Graphics thisGraphics;
    
	private MultipleAIEnvironment environment;
	private LevelRenderer layer;
	private BgRenderer[] bgLayer = new BgRenderer[2];
	
	private long tm = System.currentTimeMillis();

	int delay = 1000/24;

	private Scale2x scale2x = new Scale2x(320, 240);

	private Point lastMousePos = new Point(-100, -100);
	
	int xCam, yCam;
	   
	private byte pickedTile;
	
    public PlayComponent(int width, int height){
    	addMouseListener(this);
    	addMouseMotionListener(this);
    	addKeyListener(this);

        this.setFocusable(true);
        this.setEnabled(true);
        this.width = width;
        this.height = height;

        Dimension size = new Dimension(width, height);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
    }

	@Override
	public void addNotify() {
		super.addNotify();
		
		requestFocus();
	}
    
	public void tick() {
		CameraHandler.getInstance().tick();
		
		xCam = CameraHandler.getInstance().getCameraPosition().x;
		yCam = CameraHandler.getInstance().getCameraPosition().y;
		
		// Environment tick
		environment.tick();
		
		// Draw tick
		this.render(thisVolatileImageGraphics);
		thisGraphics.drawImage(scale2x.scale(thisVolatileImage), 0, 0, null);
		
	    if (delay > 0)
	    {
	        try
	        {
	            tm += delay;
	            Thread.sleep(Math.max(0, tm - System.currentTimeMillis()));
	        } catch (InterruptedException ignored) {}
	    }
	}
	
	public void render(Graphics g) {
	    for (int i = 0; i < bgLayer.length; i++)
	    {
	        bgLayer[i].setCam(xCam, yCam);
	        bgLayer[i].render(g); //levelScene.
	    }
	    
	    g.translate(-xCam, -yCam);

	    List<Sprite> sprites = environment.getSprites();
	    
	    for (Sprite sprite : sprites)          // levelScene.
	        if (sprite.layer == 0) sprite.render(g);

	    g.translate(xCam, yCam);

	    layer.renderBehaviors = getMarioPanel().isEditing();
	    		
	    layer.setCam(xCam, yCam);
	    layer.render(g, environment.getTick() /*levelScene.paused ? 0 : */);
	    layer.renderExit0(g);
	    
	    g.translate(-xCam, -yCam);

	    for (Sprite sprite : sprites)  // Mario, creatures
	        if (sprite.layer == 1) sprite.render(g);

	    g.translate(xCam, yCam);
	    		
	    layer.renderExit1(g);
	    		
	    if(getMarioPanel().isEditing()) {
		    // Start block
		    int xEnterPos = environment.getLevel().xEnter * 16;
		    int yEnterPos = environment.getLevel().yEnter * 16;
		    
		    g.drawImage(Art.specialBlockStart, xEnterPos-xCam, yEnterPos-yCam, null);
		    
		    // End block
		    int xEndPos = environment.getLevel().xExit * 16;
		    int yEndPos = environment.getLevel().yExit * 16;
		    
		    g.drawImage(Art.specialBlockEnd, xEndPos-xCam, yEndPos-yCam, null);
	    	
	    	// Draw tile marker
			Point tile = CameraHandler.getInstance().mousePointToTile(lastMousePos);
			
			int relativeTilePositionX = (tile.x * 16) - xCam;
			int relativeTilePositionY = (tile.y * 16) - yCam;
			
		    g.drawRect(relativeTilePositionX, relativeTilePositionY, 16, 16);
	    }
	}

	public void start() {
	    if (!running)
        {
            running = true;
            new Thread(this, "Game Thread").start();
        }
	}
	
	public void stop() {
		running = false;
	}
	
	
	private MarioPanel getMarioPanel() {
		return (MarioPanel) getParent();
	}
	
	@Override
	public void run() {
		graphicsConfiguration = getGraphicsConfiguration();

        thisVolatileImage = createVolatileImage(COMPONENT_WIDTH, COMPONENT_HEIGHT);
        thisGraphics = getGraphics();
        thisVolatileImageGraphics = thisVolatileImage.getGraphics();
        
        environment = new MultipleAIEnvironment();
        
        environment.actualReset();
        
        layer = new LevelRenderer(environment.getLevelToRender(), graphicsConfiguration, this.width, this.height);
        for (int i = 0; i < bgLayer.length; i++)
        {
            int scrollSpeed = 4 >> i;
            int w = ((environment.getLevel().length * 16) - COMPONENT_WIDTH) / scrollSpeed + COMPONENT_WIDTH;
            int h = ((environment.getLevel().height * 16) - COMPONENT_HEIGHT) / scrollSpeed + COMPONENT_HEIGHT;
            
            Level bgLevel = BgLevelGenerator.createLevel(w / 32 + 1, h / 32 + 1, i == 0, 0);
            
            bgLayer[i] = new BgRenderer(bgLevel, graphicsConfiguration, COMPONENT_WIDTH, COMPONENT_HEIGHT, scrollSpeed);
        }       
        
        CameraHandler.getInstance().setLimits(0, environment.getLevel().length * 16, 0, 0);
        CameraHandler.getInstance().setScreenSize(COMPONENT_WIDTH, COMPONENT_HEIGHT);
        
        // Stupid hack to fix weird frame packing
        getMarioPanel().toggleEditing();
        getMarioPanel().toggleEditing();
        // Stupid hack to fix weird frame packing
        
		while(running) {
			tick();
		}
	}
	
	public void changeLevel(Level level) {
		layer.setLevel(level);
		environment.changeLevel(level);
	}
	
	public Level getLevel() {
		return environment.getLevel();
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		lastMousePos = new Point(-100, -100);
	}

	@Override
	public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 && getMarioPanel().isEditing()) {
        	Point bluh = CameraHandler.getInstance().mousePointToTile(lastMousePos);
        	environment.getLevel().setBlock(bluh.x, bluh.y, pickedTile);
        }
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		if(getMarioPanel().isEditing()) {
			environment.reset();
			layer.setLevel(environment.getLevelToRender());
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		toggleKey(arg0.getKeyCode(), true);
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		toggleKey(arg0.getKeyCode(), false);
	}

	private void toggleKey(int keyCode, boolean isPressed){
		environment.toggleKey(keyCode, isPressed);
		
		if(keyCode == KeyEvent.VK_E && !isPressed) {
			getMarioPanel().toggleEditing();
			environment.reset(getMarioPanel().isEditing());
			environment.tick();
			layer.setLevel(environment.getLevelToRender());
		}
		
		if(keyCode == KeyEvent.VK_M && !isPressed) {
			getMarioPanel().toggleMenu();
		}
		
		if(keyCode == KeyEvent.VK_T && !isPressed) {
			environment.toggleTracing();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		lastMousePos = e.getPoint();

		if(getMarioPanel().isEditing()) {
			Point bluh = CameraHandler.getInstance().mousePointToTile(lastMousePos);
	    	environment.getLevel().setBlock(bluh.x, bluh.y, pickedTile);
		}
	}
	
	public void setPickedTile(byte tile) {
		pickedTile = tile;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		lastMousePos = e.getPoint();
	}
	
	@Override
	public void keyTyped(KeyEvent arg0) {}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {}

	@Override
	public void mouseEntered(MouseEvent arg0) {}
}
