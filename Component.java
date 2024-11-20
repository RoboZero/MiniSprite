package RoboZero.MiniSprite;

import android.graphics.Canvas;

/**
 * Add components to Transforms to update that transform over time.
 */
public abstract class Component {
    /**
     * Start is called once when added to MiniSpriteSurface canvas.
     * @param ownerTransform Transform added to that has list of components run in order.
     * @param canvas Locked Canvas. Use to get width, height, and other attributes.
     * @param miniSpriteSurface Surface transform belongs to, use to manipulate other transforms using GetComponent<Component>()
     */
    public abstract void start(Transform2D ownerTransform, Canvas canvas, MiniSpriteSurface miniSpriteSurface);
    public abstract void update(Transform2D ownerTransform, Canvas canvas, MiniSpriteSurface miniSpriteSurface);
    public abstract Component createCopy();
}
