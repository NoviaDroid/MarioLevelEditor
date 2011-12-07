package dk.itu.mariolevel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import dk.itu.mariolevel.editor.EditorComponent;
import dk.itu.mariolevel.editor.PlayComponent;

public class FrameLauncher
{
    public static void main(String[] args)
    {
    	// Create environment which then has the component.
    	
    	PlayComponent playComponent = new PlayComponent(640, 480);	
    	EditorComponent editorComponent = new EditorComponent(200);
    	
    	editorComponent.setTilePickListener(playComponent);
    	
        JFrame frame = new JFrame("Mario Test");
        
        Container bluh = frame.getContentPane();
        
        bluh.setLayout(new BorderLayout());
        
        bluh.add(playComponent, BorderLayout.WEST);

        
        JScrollPane scroll = new JScrollPane(editorComponent, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        bluh.add(scroll, BorderLayout.EAST);
        
        bluh.setBackground(Color.BLACK);
        
        frame.setContentPane(bluh);
//        
        Dimension size = new Dimension(850, 480);
        frame. setPreferredSize(size);
        frame.setMinimumSize(size);
        frame.setMaximumSize(size);
//        
//        frame.pack();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((screenSize.width-frame.getWidth())/2, (screenSize.height-frame.getHeight())/2);
        
        frame.setVisible(true);
        
        playComponent.start();
    }
}