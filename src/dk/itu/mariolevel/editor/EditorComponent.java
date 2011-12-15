package dk.itu.mariolevel.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import dk.itu.mariolevel.editor.tileselector.TileGroup;
import dk.itu.mariolevel.engine.level.Level;

public class EditorComponent extends JPanel {

	private static final long serialVersionUID = -2642655301800190064L;

	private int width;
	public int height;
	
	private PlayComponent tilePickListener;
	
	private List<TileGroup> children = new ArrayList<TileGroup>();
	
	public EditorComponent(int width) {
		setLayout(new FlowLayout());

        this.setEnabled(true);
        this.width = width;
        this.height = 480;

		Dimension size = new Dimension(width, height);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
        
        addTileGroups();
        
        setBackground(Color.BLACK);
	}
	
	private void addTileGroups() {
		addTileGroup(new TileGroup("ground", new byte[]{-128,-127,-126,0,0,-112,-111,-110,0,0,-96,-95,-94,0,0}, width, this));
		addTileGroup(new TileGroup("hill", new byte[]{-124,-123,-122,0,0,-108,-107,-106,0,0,-92,-91,-90,0,0}, width, this));
		addTileGroup(new TileGroup("blocks", new byte[]{32,20,4,16,12,9,0,0,0,0}, width, this));
		addTileGroup(new TileGroup("enemies", new byte[]{Level.SPECIAL_BLOCK_GOOMBA,Level.SPECIAL_BLOCK_FLOWER,14,0,0,Level.SPECIAL_BLOCK_RED_KOOPA,Level.SPECIAL_BLOCK_GREEN_KOOPA,30,0,0,0,0,46,0,0}, width, this));
		addTileGroup(new TileGroup("pipes", new byte[]{10,11,24,0,0,26,27,40,0,0,0,0,56,0,0}, width, this));
		addTileGroup(new TileGroup("special", new byte[]{Level.SPECIAL_BLOCK_START,Level.SPECIAL_BLOCK_END,Level.SPECIAL_BLOCK_MUSHROOM,0,0}, width, this));
		addTileGroup(new TileGroup("misc", new byte[]{80,81,82,0,0,96,97,98}, width, this));
		
		revalidate();
	}
	
	private void addTileGroup(TileGroup group) {
		children.add(group);
		add(group);
	}
	
	@Override
	public void revalidate() {
		super.revalidate();

		if(children != null) {
			int height = 0;
			
			for(TileGroup child : children)
				height += child.height;
			
			Dimension size = new Dimension(width, height);
			setPreferredSize(size);
			setMinimumSize(size);
			setMaximumSize(size);
		}
	}

	public void setTilePickListener(PlayComponent listener) {
		tilePickListener = listener;
	}
	
	public void setTilePicked(TileGroup tileChild) {
		for(TileGroup child : children)
			if(child != tileChild) child.deselectTile();

		if(tilePickListener != null) {
			tilePickListener.setPickedTile(tileChild.pickedTile);
		}
	}
	
	public int getWidth() {
		return width + 5;
	}
}
