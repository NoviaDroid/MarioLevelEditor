package dk.itu.mariolevel.engine.scene;

import dk.itu.mariolevel.engine.sprites.Mario;
import dk.itu.mariolevel.engine.sprites.Sprite;



public abstract class Scene
{
    public static boolean[] keys = new boolean[16];

    public void toggleKey(int key, boolean isPressed)
    {
        keys[key] = isPressed;
    }

    public abstract void tick();
}