package dk.itu.mariolevel.editor.tileselector;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.VolatileImage;
import java.io.IOException;

import javax.swing.JComponent;

import dk.itu.mariolevel.editor.EditorComponent;
import dk.itu.mariolevel.engine.Art;
import dk.itu.mariolevel.engine.Scale2x;
import dk.itu.mariolevel.engine.level.Level;

public class TileGroup extends JComponent  implements MouseListener {
	private static final long serialVersionUID = -4748861593538807170L;
	
	public VolatileImage thisVolatileImage;
	public Graphics thisVolatileImageGraphics;
	private Scale2x scale2x;
	
	private String headerPic;
	private Image headerImage;
	
	public int width, height;
	
	private byte[] tiles;
	
	private int tilesPerRow, rows;
	
	private int tileX, tileY;
	
	public byte pickedTile;
		
	private boolean open;
	
	private EditorComponent parent;
	
	public TileGroup(String headerPic, byte[] tiles, int width, EditorComponent parent) {
		addMouseListener(this);
		
		this.setEnabled(true);
		
		this.headerPic = headerPic;
		this.tiles = tiles;
		this.width = width;
		this.parent = parent;
		this.height = 25;
		
		Dimension size = new Dimension(width, height-5);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		
		tileX = tileY = -1;
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		g.drawImage(headerImage, 0, 0, null);
		
		if(open) {
			this.render(thisVolatileImageGraphics);
			g.drawImage(scale2x.scale(thisVolatileImage), 0, 20, null);
		}	
	}
	
	private void render(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		
		for(int y = 0; y < rows; y++) {
			for(int x = 0; x < tilesPerRow; x++) {
				
				if(x+(y*tilesPerRow) >= tiles.length) continue;
				
				byte tileByte = tiles[x+(y*tilesPerRow)];
				 
				Image drawImage = null;
				
				int drawX = x * 18 + 2;
				int drawY = y * 18 + 2;
				
				if(tileByte == Level.SPECIAL_BLOCK_START) {
					drawImage = Art.specialBlockStart;
//					g.drawImage(Art.specialBlockStart, drawX, drawY, null);
				}
				else if(tileByte == Level.SPECIAL_BLOCK_END) {
					drawImage = Art.specialBlockEnd;
//					g.drawImage(Art.specialBlockEnd, drawX, drawY, null);
				}
				else if(tileByte == Level.SPECIAL_BLOCK_GOOMBA) {
					drawImage = Art.enemies[0][2];
					drawY -= 16;
				}
				else if(tileByte == Level.SPECIAL_BLOCK_GREEN_KOOPA) {
					drawImage = Art.enemies[4][1];
					drawY -= 16;
				}
				else if(tileByte == Level.SPECIAL_BLOCK_RED_KOOPA) {
					drawImage = Art.enemies[4][0];
					drawY -= 16;
				}
				else if(tileByte == Level.SPECIAL_BLOCK_FLOWER) {
					drawImage = Art.enemies[0][6];
					drawY -= 10;
				}
				else {
					int xPickedTile = (tileByte & 0xff) % 16;
					int yPickedTile = (tileByte & 0xff) / 16;
					
					drawImage = Art.level[xPickedTile][yPickedTile];
//					g.drawImage(Art.level[xPickedTile][yPickedTile], drawX, y*18+2, null);
				}

				if(drawImage != null)
					g.drawImage(drawImage, drawX, drawY, null);
			}
		}
		
		if(tileX != -1 && tileY != -1) {
			g.setColor(Color.WHITE);
			g.drawRect(tileX*18, tileY*18, 19, 19);
		}
	}
	
	public void toggleTiles() {
		open = !open;
		
		height = 25 + (open ? rows * 40 : 0);
		
		Dimension size = new Dimension(width, height-5);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		
		parent.revalidate();
	}
	
	@Override
	public void addNotify() {
		super.addNotify();
		
		try {
			headerImage = Art.getImage(getGraphicsConfiguration(), "res/" + headerPic + ".png");
		} catch (IOException e) {
			headerImage = createImage(width, 20);
		}

        scale2x = new Scale2x(width / 2, tiles.length * 18);
        
        thisVolatileImage = createVolatileImage(width / 2, tiles.length * 18);
		thisVolatileImageGraphics = thisVolatileImage.getGraphics();
		
		tilesPerRow = width/32 - 1;
		rows = (tiles.length / tilesPerRow + (tiles.length%tilesPerRow != 0 ? 1 : 0));
	}
	
	public void deselectTile() {
		tileX = -1;
		tileY = -1;
		repaint();
	}
	
	public void setTilePicked(int tileX, int tileY) {
		int tilePos = tileX + (tileY*tilesPerRow);
		
		if(tilePos >= tiles.length) return;
		
		this.tileX = tileX;
		this.tileY = tileY;
		
		pickedTile = tiles[tilePos];
		
		parent.setTilePicked(this);
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getX() < width && e.getY() < 20)
			toggleTiles();
		else {
			int tileX = (e.getX()+2)/36;
			int tileY = (e.getY()-18)/36;
			
			
			setTilePicked(tileX, tileY);			
		}
		
		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
}
