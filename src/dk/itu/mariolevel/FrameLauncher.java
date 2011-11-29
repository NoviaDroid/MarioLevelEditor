package dk.itu.mariolevel;

import java.awt.*;
import javax.swing.*;

import dk.itu.mariolevel.ai.environments.MultipleAIEnvironment;
import dk.itu.mariolevel.editor.EditorComponent;
import dk.itu.mariolevel.engine.MarioComponent;

public class FrameLauncher
{
    public static void main(String[] args)
    {
    	// Create environment which then has the component.
    	
    	EditorComponent component = new EditorComponent(640, 480);	
    	
//        MarioComponent mario = new MarioComponent(640, 480, false);
        JFrame frame = new JFrame("Mario Test");
        frame.setContentPane(component);
        frame.pack();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((screenSize.width-frame.getWidth())/2, (screenSize.height-frame.getHeight())/2);
        
        frame.setVisible(true);
        
        component.start();
//        frame.addKeyListener(mario);
//        frame.addFocusListener(mario);
    }
}