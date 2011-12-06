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

import javax.swing.JComponent;

import dk.itu.mariolevel.ai.environments.MultipleAIEnvironment;
import dk.itu.mariolevel.engine.Art;
import dk.itu.mariolevel.engine.BgRenderer;
import dk.itu.mariolevel.engine.CameraHandler;
import dk.itu.mariolevel.engine.LevelRenderer;
import dk.itu.mariolevel.engine.level.BgLevelGenerator;
import dk.itu.mariolevel.engine.level.Level;

public class EditorComponent extends JComponent implements KeyListener, FocusListener, MouseListener {

	private static final long serialVersionUID = -2642655301800190064L;

	private int width, height;
	
	public EditorComponent(int width, int height) {
    	addFocusListener(this);
    	addMouseListener(this);
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
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		g.setColor(Color.GREEN);
		g.fillRect(0, 0, width, height);
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
//		System.out.println("ARGH: " + arg0.getX() + ", " + arg0.getY());
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
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
