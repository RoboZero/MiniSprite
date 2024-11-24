package MiniSprite.CustomComponents;

import android.graphics.Canvas;
import android.util.Log;

import MiniSprite.Component;
import MiniSprite.MiniSpriteSurface;
import MiniSprite.Transform2D;

public class WaveFollowerComponent extends Component {
    private static final double TAU = 2 * Math.PI;

    public int spriteStartYPosition;
    public int spriteStartWidth;
    public int spriteStartHeight;
    public int waveXPosition;
    public int amplitude;
    public int wavelengthPx; // Distance it takes for wave to reset.
    public int periodMillis; // Time it takes for wave to reset
    public float spriteXScale;
    public float spriteYScale;

    private double time;

    public WaveFollowerComponent(
            int waveXPosition,
            int amplitude,
            int wavelengthPx,
            int periodMillis
    ){
        this.waveXPosition = waveXPosition;
        this.amplitude = amplitude;
        this.wavelengthPx = wavelengthPx;
        this.periodMillis = periodMillis;
    }

    @Override
    public void start(Transform2D ownerTransform, Canvas canvas, MiniSpriteSurface miniSpriteSurface) {
        spriteStartYPosition = ownerTransform.PositionY;
        spriteStartWidth = ownerTransform.Width;
        spriteStartHeight = ownerTransform.Height;
        Log.d("MiniSpriteWaveComponent", "Started wave follower " + ownerTransform.Name);
    }

    @Override
    public void update(Transform2D ownerTransform, Canvas canvas, MiniSpriteSurface miniSpriteSurface) {
        time = System.currentTimeMillis();

        ownerTransform.PositionY = spriteStartYPosition + (int)(calculateWaveHeight(waveXPosition, time));
        ownerTransform.Width = (int)(spriteStartWidth * spriteXScale);
        ownerTransform.Height = (int)(spriteStartHeight * spriteYScale);
    }

    @Override
    public Component createCopy() {
        WaveFollowerComponent component = new WaveFollowerComponent(
                waveXPosition,
                amplitude,
                wavelengthPx,
                periodMillis
        );

        component.spriteStartYPosition = spriteStartYPosition;
        component.spriteStartWidth = spriteStartWidth;
        component.spriteStartHeight = spriteStartHeight;
        component.waveXPosition = waveXPosition;
        component.amplitude = amplitude;
        component.wavelengthPx = wavelengthPx;
        component.periodMillis = periodMillis;
        component.spriteXScale = spriteXScale;
        component.spriteYScale = spriteYScale;

        return component;
    }

    private double calculateWaveHeight(int position, double elapsedTimeMs){
        return amplitude * Math.sin((TAU / wavelengthPx) * position - (TAU / periodMillis) * elapsedTimeMs);
    }
}
