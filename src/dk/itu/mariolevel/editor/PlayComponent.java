package dk.itu.mariolevel.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.VolatileImage;
import java.util.List;

import javax.swing.JComponent;

import dk.itu.mariolevel.ai.environments.MultipleAIEnvironment;
import dk.itu.mariolevel.engine.Art;
import dk.itu.mariolevel.engine.BgRenderer;
import dk.itu.mariolevel.engine.CameraHandler;
import dk.itu.mariolevel.engine.LevelRenderer;
import dk.itu.mariolevel.engine.Scale2x;
import dk.itu.mariolevel.engine.level.BgLevelGenerator;
import dk.itu.mariolevel.engine.level.Level;
import dk.itu.mariolevel.engine.sprites.Mario;
import dk.itu.mariolevel.engine.sprites.Sprite;

public class PlayComponent extends JComponent implements Runnable, KeyListener, FocusListener, MouseListener, MouseMotionListener {
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
	private long tm0;
	int delay = 1000/24;

	private Scale2x scale2x = new Scale2x(320, 240);
	
	private boolean left, right;
	
	private boolean editing = true;
	private Point lastMousePos = new Point(-1, -1);
	
	int xCam, yCam;
	   
    public PlayComponent(int width, int height){
    	addFocusListener(this);
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

        this.setFocusable(true);
    }
    
	public void tick() {
		if(right) {		
			CameraHandler.getInstance().moveCamera(20, 0);
			
		}
		if(left) {
			CameraHandler.getInstance().moveCamera(-20, 0);
		}
		
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

	    layer.setCam(xCam, yCam);
	    layer.render(g, environment.getTick() /*levelScene.paused ? 0 : */);
	    
	    g.translate(-xCam, -yCam);

	    for (Sprite sprite : sprites)  // Mario, creatures
	        if (sprite.layer == 1) sprite.render(g);

	    g.translate(xCam, yCam);
	    		
	    if(editing) {
		    // If editing, draw tile marking and picked tile (alphaed)
	    	int xTile = (lastMousePos.x + (xCam*2)) / 32;
			int yTile = (lastMousePos.y + (yCam*2)) / 32;

			int relativeTilePositionX = (xTile * 16) - xCam;
			int relativeTilePositionY = (yTile * 16) - yCam;
			
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
	
	@Override
	public void run() {
		graphicsConfiguration = getGraphicsConfiguration();

		Art.init(graphicsConfiguration);
		
        thisVolatileImage = createVolatileImage(COMPONENT_WIDTH, COMPONENT_HEIGHT);
        thisGraphics = getGraphics();
        thisVolatileImageGraphics = thisVolatileImage.getGraphics();
        
        environment = new MultipleAIEnvironment();
        
        environment.reset();
        
        layer = new LevelRenderer(environment.level, graphicsConfiguration, this.width, this.height);
        for (int i = 0; i < bgLayer.length; i++)
        {
            int scrollSpeed = 4 >> i;
            int w = ((environment.level.length * 16) - COMPONENT_WIDTH) / scrollSpeed + COMPONENT_WIDTH;
            int h = ((environment.level.height * 16) - COMPONENT_HEIGHT) / scrollSpeed + COMPONENT_HEIGHT;
            
            // TODO: Fix level type being the same
            Level bgLevel = BgLevelGenerator.createLevel(w / 32 + 1, h / 32 + 1, i == 0, 0);
            
            bgLayer[i] = new BgRenderer(bgLevel, graphicsConfiguration, COMPONENT_WIDTH, COMPONENT_HEIGHT, scrollSpeed);
        }       
        
        CameraHandler.getInstance().setLimits(0, environment.level.length * 16, 0, 0);
        CameraHandler.getInstance().setScreenSize(COMPONENT_WIDTH, COMPONENT_HEIGHT, false);
        
		while(running) {
			tick();
		}
	}
	
	public void reset() {
	    tm = System.currentTimeMillis();
	    this.tm0 = tm;
	}
    
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		lastMousePos = new Point(-1, -1);
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		toggleKey(arg0.getKeyCode(), true);
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		toggleKey(arg0.getKeyCode(), false);
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	 private void toggleKey(int keyCode, boolean isPressed)
    {
        if (keyCode == KeyEvent.VK_LEFT)
        {
        	left = isPressed;
        }
        if (keyCode == KeyEvent.VK_RIGHT)
        {
        	right = isPressed;
        }
        if (keyCode == KeyEvent.VK_DOWN)
        {
        }
        if (keyCode == KeyEvent.VK_UP)
        {
        }
    }

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		lastMousePos = e.getPoint();
	}
}
