package dk.itu.mariolevel.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import dk.itu.mariolevel.MarioPanel;
import dk.itu.mariolevel.ai.environments.MultipleAIEnvironment;

public class MenuComponent extends JPanel implements ActionListener{
	private static final long serialVersionUID = -7392809893837906533L;
	
	private static final String LOAD_COMMAND = "load";
	private static final String SAVE_COMMAND = "save";
	private static final String SIMPLE_AI_COMMAND = "simple_ai";
	private static final String COMPLEX_AI_COMMAND = "complex_ai";
	private static final String ALL_AI_COMMAND = "all_ai";
	private static final String PLAYER_COMMAND = "player";
	private static final String RATE_COMMAND = "rate";
	
	public int width, height;
	
	private JRadioButton b3, b4, b5, b6;
	private ButtonGroup group;
	
	private JButton b7;
	
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
		
		add(b2);
		
		b3 = new JRadioButton("All AI");
		b3.setActionCommand(ALL_AI_COMMAND);
		b3.addActionListener(this);
		b3.setBackground(Color.BLACK);
		b3.setForeground(Color.WHITE);
		
		b4 = new JRadioButton("Simple AI");
		b4.setActionCommand(SIMPLE_AI_COMMAND);
		b4.addActionListener(this);
		b4.setBackground(Color.BLACK);
		b4.setForeground(Color.WHITE);
		
		b5 = new JRadioButton("Complex AI");
		b5.setActionCommand(COMPLEX_AI_COMMAND);
		b5.addActionListener(this);
		b5.setBackground(Color.BLACK);
		b5.setForeground(Color.WHITE);
		
		b6 = new JRadioButton("Player");
		b6.setActionCommand(PLAYER_COMMAND);
		b6.addActionListener(this);
		b6.setBackground(Color.BLACK);
		b6.setForeground(Color.WHITE);
		
		group = new ButtonGroup();
		
		group.add(b3);
		group.add(b4);
		group.add(b5);
		group.add(b6);

		group.setSelected(b6.getModel(), true);
		
		JPanel panel = new JPanel();
		
		panel.add(b3);
		panel.add(b4);
		panel.add(b5);
		panel.add(b6);
		
		panel.setBackground(Color.BLACK);
		
		add(panel);
		
		b7 = new JButton("Make suggestion");
		
		b7.setActionCommand(RATE_COMMAND);
		b7.addActionListener(this);
		
		add(b7);
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
		else if(RATE_COMMAND.equals(e.getActionCommand())) {
			getMarioPanel().rateLevel();
		}
		else {
			if(ALL_AI_COMMAND.equals(e.getActionCommand())) {
				getMarioPanel().changeAISet(MultipleAIEnvironment.AI_SET_ALL);
			}
			else if(SIMPLE_AI_COMMAND.equals(e.getActionCommand())) {
				getMarioPanel().changeAISet(MultipleAIEnvironment.AI_SET_SIMPLE);			
			}
			else if(COMPLEX_AI_COMMAND.equals(e.getActionCommand())) {
				getMarioPanel().changeAISet(MultipleAIEnvironment.AI_SET_COMPLEX);
			}
			else if(PLAYER_COMMAND.equals(e.getActionCommand())) {
				getMarioPanel().changeAISet(MultipleAIEnvironment.AI_SET_PLAYER);
			}
			
			b7.setEnabled(!PLAYER_COMMAND.equals(e.getActionCommand()));
		}
		
		getMarioPanel().returnFocusToGame();
	}
	
	public void changeAvailableAISets(boolean editing) {
		if(editing) {
			group.setSelected(b4.getModel(), true);
			
			b3.setEnabled(false);
			b5.setEnabled(false);
			b6.setEnabled(false);
		}
		else {
			b3.setEnabled(true);
			b5.setEnabled(true);
			b6.setEnabled(true);
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
		
//		getMarioPanel().returnFocusToGame();
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
           	return fc.getSelectedFile().getPath();
        }
		
		return null;
	}
}
