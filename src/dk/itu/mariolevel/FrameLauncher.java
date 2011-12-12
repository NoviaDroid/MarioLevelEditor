package dk.itu.mariolevel;

import javax.swing.JFrame;

public class FrameLauncher
{
    public static void main(String[] args)
    {
    	JFrame frame = new JFrame("Mario");
    	MarioPanel panel = new MarioPanel(frame);
    	frame.setContentPane(panel);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        
        
        frame.setVisible(true);
        
        panel.start();
    }
}