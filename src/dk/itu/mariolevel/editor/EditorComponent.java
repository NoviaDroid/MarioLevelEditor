package dk.itu.mariolevel.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.VolatileImage;

import javax.swing.JComponent;

import dk.itu.mariolevel.engine.Art;
import dk.itu.mariolevel.engine.Scale2x;

public class EditorComponent extends JComponent implements KeyListener, FocusListener, MouseListener {

	private static final long serialVersionUID = -2642655301800190064L;

	private int width, height;
	
	private byte[] validTiles = new byte[]{0,16,20,4,32,9,10,11,27,26,-128,-127,-126,-124,-123,-122};

	public VolatileImage thisVolatileImage;
	public Graphics thisVolatileImageGraphics;
	private Scale2x scale2x;
	
	public byte pickedTile;
	
	private PlayComponent tilePickListener;
	
	private int tilesPerRow;
	
	private int tileX, tileY;
	
	public EditorComponent(int width) {
    	addFocusListener(this);
    	addMouseListener(this);
    	addKeyListener(this);

        this.setFocusable(true);
        this.setEnabled(true);
        this.width = width;
        this.height = 480;
        
        Dimension size = new Dimension(width, height);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        
        this.setFocusable(true);
	}
	
	public void setTilePickListener(PlayComponent listener) {
		tilePickListener = listener;
	}

	public void setTilePicked(int tileX, int tileY) {
		int tilePos = tileX + (tileY*tilesPerRow);
		
		if(tilePos >= validTiles.length) return;
		
		this.tileX = tileX;
		this.tileY = tileY;
		
		pickedTile = validTiles[tilePos];
		
		if(tilePickListener != null) {
			tilePickListener.setPickedTile(pickedTile);
		}
	}
	
	@Override
	public void addNotify() {
		super.addNotify();
		
		Art.init(getGraphicsConfiguration());

        scale2x = new Scale2x(width / 2, 1500);
        
        thisVolatileImage = createVolatileImage(width / 2, 1500);
		thisVolatileImageGraphics = thisVolatileImage.getGraphics();
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		this.render(thisVolatileImageGraphics);
	   
		g.drawImage(scale2x.scale(thisVolatileImage), 0, 0, null);
	}
	
	public void render(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);

		tilesPerRow = width/32 -1;
		
		for(int y = 0; y < (validTiles.length / tilesPerRow + (validTiles.length%tilesPerRow != 0 ? 1 : 0)); y++) {
			for(int x = 0; x < tilesPerRow; x++) {
				if(x+(y*tilesPerRow) >= validTiles.length) continue;
				 byte tileByte = validTiles[x+(y*tilesPerRow)];
				 
				 int xPickedTile = (tileByte & 0xff) % 16;
				 int yPickedTile = (tileByte & 0xff) / 16;
				 
				 g.drawImage(Art.level[xPickedTile][yPickedTile], x*18+2, y*18+2, null);
			}
		}
		
		g.setColor(Color.BLACK);
		g.drawRect(tileX*18, tileY*18, 19, 19);
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		int tileX = (e.getX()+2)/36;
		int tileY = (e.getY()+2)/36;
		
		setTilePicked(tileX, tileY);
		repaint();
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
