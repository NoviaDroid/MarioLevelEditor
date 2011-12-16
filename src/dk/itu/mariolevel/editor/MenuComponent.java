package dk.itu.mariolevel.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import dk.itu.mariolevel.MarioPanel;

public class MenuComponent extends JPanel implements ActionListener{
	private static final long serialVersionUID = -7392809893837906533L;
	
	private static final String LOAD_COMMAND = "load";
	private static final String SAVE_COMMAND = "save";
	
	public int width, height;
	
	public MenuComponent(int width) {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		
		this.width = width;
		this.height = 50;
		
		Dimension size = new Dimension(width, height);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		
		addButtons();
		
		setBackground(Color.BLACK);
	}
	
	private void addButtons() {
		JButton b1 = new JButton("Load");
		
		b1.setActionCommand(LOAD_COMMAND);
		b1.addActionListener(this);

		add(b1);
		
		JButton b2 = new JButton("Save");
		
		b2.setActionCommand(SAVE_COMMAND);
		b2.addActionListener(this);
//		
		add(b2);
	}
	
	private MarioPanel getMarioPanel() {
		return (MarioPanel) getParent();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(LOAD_COMMAND.equals(e.getActionCommand())) {
			String path = showFileDialog(true);
			
			if(path != null) {
				getMarioPanel().loadLevel(path);
			}
		}
		else if(SAVE_COMMAND.equals(e.getActionCommand())) {
			String path = showFileDialog(false);

			if(path != null) {
				getMarioPanel().saveLevel(path);
			}
		}
	}
	
	private String showFileDialog(boolean open) {
		final JFileChooser fc = new JFileChooser();
		
		int returnVal = -1;
		
		if(open)  {
			returnVal = fc.showOpenDialog(getMarioPanel());
		}
		else {
			returnVal = fc.showSaveDialog(getMarioPanel());
		}
		
		getMarioPanel().returnFocusToGame();
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
           	return fc.getSelectedFile().getPath();
        }
		
		return null;
	}
}
