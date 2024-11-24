package MiniSprite;

import android.graphics.Canvas;

/**
 * CanvasTransformSurface has an Update loop which draws transforms.
 * Implement this on your Activity to manipulate transforms each animate call.
 */
public interface IMiniSpriteSurfaceListener {
    void onPreUpdate(MiniSpriteSurface miniSpriteSurface, Canvas canvas);
    void onPreDrawSprites(MiniSpriteSurface miniSpriteSurface, Canvas canvas);
    void onPostDrawSprites(MiniSpriteSurface miniSpriteSurface, Canvas canvas);
}
