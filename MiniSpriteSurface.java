package RoboZero.MiniSprite;

import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import RoboZero.MiniSprite.Internal.Animator;
import RoboZero.MiniSprite.Internal.DrawnTransformCollection;
import RoboZero.MiniSprite.Internal.IDrawable;
import RoboZero.MiniSprite.Internal.PackageUtilities;

/**
 * The core of MiniSprite
 * Instead of having Activity inherit Surface holder, can create instance of this.
 * Runs a new Animate thread, calling all CanvasTransform.draw in TransformCollection.
 * Before and after drawing, will automatically call all Listener methods.
 */
public class MiniSpriteSurface implements SurfaceHolder.Callback, IDrawable {
    public final DrawnTransformCollection AllTransforms = new DrawnTransformCollection();
    public final SurfaceView SurfaceView;

    private SurfaceHolder holder;
    private final Animator animator;
    private final List<IMiniSpriteSurfaceListener> listeners = new ArrayList<>();

    public MiniSpriteSurface(SurfaceView surfaceView){
        surfaceView.getHolder().addCallback(this);
        SurfaceView = surfaceView;

        animator = new Animator(this);
        animator.start();
    }

    /**
     *
     * @param listener
     */
    public void addListener(IMiniSpriteSurfaceListener listener){
        listeners.add(listener);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        PackageUtilities.LOG_BUILDER.append("Surface Created");
        PackageUtilities.logBuilderSingle();
        holder = surfaceHolder;

        draw();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        PackageUtilities.LOG_BUILDER.append("Surface Changed");
        PackageUtilities.logBuilderSingle();
        holder = surfaceHolder;
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        holder = null;
        animator.finish();
    }

    @Override
    public void draw() {
        if(holder == null) return;

        Canvas c = holder.lockCanvas();

        for (IMiniSpriteSurfaceListener listener : listeners) {
            listener.onPreUpdate(this, c);
        }

        start(c);
        update(c);

        for (IMiniSpriteSurfaceListener listener : listeners) {
            listener.onPreDrawSprites(this, c);
        }

        for(Transform2D canvasTransform : AllTransforms){
            canvasTransform.draw(c);
        }

        for (IMiniSpriteSurfaceListener listener : listeners) {
            listener.onPostDrawSprites(this, c);
        }

        holder.unlockCanvasAndPost(c);
    }

    private void start(Canvas canvas){
        for(Transform2D transform : AllTransforms){
            transform.start(canvas, this);
        }
    }

    private void update(Canvas canvas){
        for(Transform2D transform : AllTransforms){
            transform.update(canvas, this);
        }
    }
}
