package MiniSprite.CustomComponents;

import android.graphics.Canvas;

import java.util.function.Function;

import MiniSprite.Component;
import MiniSprite.MiniSpriteSurface;
import MiniSprite.Transform2D;

public class FallingComponent extends Component {
    Transform2D canvasTransform;
    int width;
    int height;

    int minFallSpeed;
    int maxFallSpeed;
    int fallSpeed;
    int minHeightMultiplier;
    int maxHeightMultiplier;
    Function<Transform2D, Boolean> onCollide;

    public FallingComponent(int minFallSpeed, int maxFallSpeed, int minHeightMultiplier, int maxHeightMultiplier, Function<Transform2D, Boolean> onSpriteCollide){
        this.minFallSpeed = minFallSpeed;
        this.maxFallSpeed = maxFallSpeed;
        this.minHeightMultiplier = minHeightMultiplier;
        this.maxHeightMultiplier = maxHeightMultiplier;
        this.onCollide = onSpriteCollide;
    }

    @Override
    public void start(Transform2D ownerTransform, Canvas canvas, MiniSpriteSurface miniSpriteSurface) {
        this.canvasTransform = ownerTransform;
        this.width = canvas.getWidth();
        this.height = canvas.getHeight();

        reset();
    }

    @Override
    public void update(Transform2D ownerTransform, Canvas canvas, MiniSpriteSurface miniSpriteSurface){
        ownerTransform.PositionY += fallSpeed;
        if(ownerTransform.PositionY > height){
            reset();
        }
        ownerTransform.RotationAngleDegrees += fallSpeed;

        for(Transform2D allTransform: miniSpriteSurface.AllTransforms){
            if(ownerTransform != allTransform && allTransform.isCollidingWith(ownerTransform)){
                boolean reset = onCollide.apply(allTransform);
                if(reset)
                    reset();
            }
        }
    }

    @Override
    public Component createCopy() {
        return new FallingComponent(minFallSpeed, maxFallSpeed, minHeightMultiplier, maxHeightMultiplier, onCollide);
    }

    public void reset(){
        canvasTransform.RotationAngleDegrees = (int)(Math.random() * 90) + 10;
        canvasTransform.PositionY = -canvasTransform.Height * ((int)(Math.random() * (maxHeightMultiplier - minHeightMultiplier)) + minHeightMultiplier);
        canvasTransform.PositionX = (int)(Math.random() * (width - canvasTransform.Width));
        fallSpeed = (int)(Math.random() * (maxFallSpeed - minFallSpeed) + minFallSpeed);
    }
}
