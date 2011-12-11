package dk.itu.mariolevel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import dk.itu.mariolevel.editor.EditorComponent;
import dk.itu.mariolevel.editor.PlayComponent;

public class MarioPanel extends JPanel {
	private static final long serialVersionUID = 8118679016668905717L;
	
	private JFrame parentFrame;
	
	private PlayComponent playComponent;
	private EditorComponent editorComponent;
	
	private boolean editing;
	
	public MarioPanel(JFrame parentFrame) {
		this.parentFrame = parentFrame;
		
		this.setLayout(new BorderLayout());
		
    	playComponent = new PlayComponent(640, 480);	
    	editorComponent = new EditorComponent(200);
    	
    	editorComponent.setTilePickListener(playComponent);
        
        setBackground(Color.BLACK);
        
        updateComponents();
	}
	
	public void toggleEditing() {
		editing = !editing;
		
		updateComponents();
	}
	
	public boolean isEditing() {
		return editing;
	}
	
	private void updateComponents() {
		this.removeAll();
		
        add(playComponent, BorderLayout.CENTER);

        if(editing) {
            JScrollPane scroll = new JScrollPane(editorComponent, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scroll.getVerticalScrollBar().setUnitIncrement(16);
            add(scroll, BorderLayout.EAST);
        }
        
        updateSize();
	}
	
	private void updateSize() {
		int height = PlayComponent.COMPONENT_HEIGHT*2;
		int width = PlayComponent.COMPONENT_WIDTH*2 + (editing ? editorComponent.getWidth() : 0);
		
		Dimension size = new Dimension(width, height);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		
		parentFrame.pack();
	}
	
	@Override
	public void addNotify() {
		super.addNotify();
		
		updateSize();
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		parentFrame.setLocation((screenSize.width-parentFrame.getWidth())/2, (screenSize.height-parentFrame.getHeight())/2);
	}
	
	public void start() {
		playComponent.start();
	}
}
