package dk.itu.mariolevel.engine.scene;

public abstract class Scene
{
    public static boolean[] keys = new boolean[16];

    public void toggleKey(int key, boolean isPressed)
    {
        keys[key] = isPressed;
    }

    public abstract void tick();
}