package dk.itu.mariolevel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import dk.itu.mariolevel.editor.EditorComponent;
import dk.itu.mariolevel.editor.MenuComponent;
import dk.itu.mariolevel.editor.PlayComponent;
import dk.itu.mariolevel.engine.Art;
import dk.itu.mariolevel.engine.CameraHandler;
import dk.itu.mariolevel.engine.level.Level;
import dk.itu.mariolevel.engine.tracking.FinishPoint;
import dk.itu.mariolevel.engine.tracking.MarioTracker;

public class MarioPanel extends JPanel {
	private static final long serialVersionUID = 8118679016668905717L;
	
//	private static final int EDITOR_BUTTON = 1;
	
	private JFrame parentFrame;
	
	private PlayComponent playComponent;
	private EditorComponent editorComponent;
	private MenuComponent menuComponent;
	
	private boolean showEdit;
	private boolean showMenu;
	
	public MarioPanel(JFrame parentFrame) {
		this.parentFrame = parentFrame;
		
		this.setLayout(new BorderLayout());
		
    	playComponent = new PlayComponent(640, 480);	
    	editorComponent = new EditorComponent(200);
    	menuComponent = new MenuComponent(640);
    	
    	editorComponent.setTilePickListener(playComponent);
        
        setBackground(Color.BLACK);
        
        updateComponents();
	}
	
	public void toggleEditing() {
		showEdit = !showEdit;
		
		menuComponent.changeAvailableAISets(showEdit);
		
		updateComponents();
	}
	
	public void toggleMenu() {
		showMenu = !showMenu;
		
		updateComponents();
	}
	
	public boolean isEditing() {
		return showEdit;
	}
	
	public void changeAISet(int aiSet) {
		playComponent.changeAISet(aiSet);
	}
	
	private void updateComponents() {
		this.removeAll();
		
        add(playComponent, BorderLayout.CENTER);

        if(showEdit) {
            JScrollPane scroll = new JScrollPane(editorComponent, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scroll.getVerticalScrollBar().setUnitIncrement(16);
            scroll.setBorder(null);
            add(scroll, BorderLayout.EAST);
        }
        
        if(showMenu) {
        	add(menuComponent, BorderLayout.SOUTH);
        }
        
        updateSize();
	}
	
	private void updateSize() {
		int height = PlayComponent.COMPONENT_HEIGHT*2 + (showMenu ? menuComponent.height : 0);
		int width = PlayComponent.COMPONENT_WIDTH*2 + (showEdit ? editorComponent.getWidth() : 0);
		
		Dimension size = new Dimension(width, height);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		
		parentFrame.pack();
	}
	
	@Override
	public void addNotify() {
		super.addNotify();
		
		Art.init(getGraphicsConfiguration());
		
		updateSize();
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		parentFrame.setLocation((screenSize.width-parentFrame.getWidth())/2, (screenSize.height-parentFrame.getHeight())/2);
	}
	
	public void start() {
		playComponent.start();
	}
	
	public void saveLevel(String path) {
		Level level = playComponent.getLevel();
		
		try {
			Level.save(level, new ObjectOutputStream(new FileOutputStream(path)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadLevel(String path) {
		Level level = null;
		
		String failMessage = null;
		
		try {
			level = Level.load(new ObjectInputStream(new FileInputStream(path)));
		} catch (ClassNotFoundException e) {
			failMessage = "Something went wrong!";
		} catch (FileNotFoundException e) {
			failMessage = "Something went wrong!";
		} catch (IOException e) {
			failMessage = "You picked a file that isn't a level.";
		}
		
		if(failMessage != null) {
			JOptionPane.showMessageDialog(this,
				    failMessage,
				    "Level load fail",
				    JOptionPane.ERROR_MESSAGE);
		}
		else if(level != null) {
			playComponent.changeLevel(level);
		}
	}

	public void returnFocusToGame() {
		playComponent.requestFocus();
	}

	private int threshold = 50;
	private int minDeaths = 5;
	
	public void rateLevel() {
		ArrayList<FinishPoint> finishMap = MarioTracker.getInstance().getTraceAndFinish(true);

		if(finishMap.size() < minDeaths) {
			JOptionPane.showMessageDialog(this, "Need more deaths to give a proper estimate.", "Death to all agents", JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			HashMap<Integer, Integer> deathMap = new HashMap<Integer, Integer>();
			
			// Get only deaths:
			for(FinishPoint point : finishMap) {
				int mapX =  ((int)(point.finish.x+threshold/2.0) / threshold) * threshold;
				
				if(!deathMap.containsKey(mapX)) 
					deathMap.put(mapX, 0);
				
				deathMap.put(mapX, deathMap.get(mapX) + 1);
			}
			
			int mostDeath = -1;
			
			for(Entry<Integer, Integer> pair : deathMap.entrySet()) {
				if(mostDeath == -1 || pair.getValue() > deathMap.get(mostDeath))
					mostDeath = pair.getKey();
			}
			
			CameraHandler.getInstance().moveCameraToX(mostDeath-CameraHandler.getInstance().width/2);
			
			JOptionPane.showMessageDialog(this, "A lot of agents seem to die here.", "Death to all agents", JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
