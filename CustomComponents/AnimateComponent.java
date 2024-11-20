package RoboZero.MiniSprite.CustomComponents;

import android.graphics.Canvas;

import RoboZero.MiniSprite.Component;
import RoboZero.MiniSprite.CustomTransforms.MiniSprite;
import RoboZero.MiniSprite.MiniSpriteSurface;
import RoboZero.MiniSprite.Transform2D;

public class AnimateComponent extends Component {
    private float timeUntilNextFrame = 0;
    private float elapsedTime;
    private long lastTime;

    public AnimateComponent(float secondsUntilNextFrame){
        this.timeUntilNextFrame = secondsUntilNextFrame;
        lastTime = System.currentTimeMillis();
    }

    @Override
    public void start(Transform2D ownerTransform, Canvas canvas, MiniSpriteSurface miniSpriteSurface) {

    }

    @Override
    public void update(Transform2D ownerTransform, Canvas canvas, MiniSpriteSurface miniSpriteSurface) {
        if (!(ownerTransform instanceof MiniSprite)) return;

        MiniSprite sprite = (MiniSprite) ownerTransform;

        long newTime = System.currentTimeMillis();
        elapsedTime += (newTime - lastTime)/1000f;
        lastTime = newTime;

        if(elapsedTime > timeUntilNextFrame){
            sprite.nextSlice();
            elapsedTime = 0;
        }
    }

    @Override
    public Component createCopy() {
        return new AnimateComponent(timeUntilNextFrame);
    }
}
