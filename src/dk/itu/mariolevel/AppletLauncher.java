package dk.itu.mariolevel;

import javax.swing.JApplet;

import dk.itu.mariolevel.editor.PlayComponent;


public class AppletLauncher extends JApplet
{
    private static final long serialVersionUID = -2238077255106243788L;

    private PlayComponent mario;
    private boolean started = false;

    public void init()
    {
    }

    public void start()
    {
        if (!started)
        {
            started = true;
            mario = new PlayComponent(getWidth(), getHeight());
            setContentPane(mario);
            setFocusable(false);
            mario.setFocusCycleRoot(true);

            mario.start();
        }
    }

    public void stop()
    {
        if (started)
        {
            started = false;
            mario.stop();
        }
    }
}