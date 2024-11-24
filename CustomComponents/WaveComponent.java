package MiniSprite.CustomComponents;

import android.graphics.Canvas;
import android.util.Log;

import java.util.ArrayList;
import java.util.function.Supplier;

import MiniSprite.Component;
import MiniSprite.MiniSpriteSurface;
import MiniSprite.Transform2D;

public class WaveComponent extends Component {
    public int WaveAmplitude;
    public int WavelengthPx;
    public int WavePeriodMillis;
    public float SpriteScaleMultiplierX;
    public float SpriteScaleMultiplierY;

    private final Supplier<Transform2D> waveFollowerSupplier;
    private final int totalWaveWidth;
    private final int totalWaveHeight;
    private final int sortingLayer;
    private final int offsetWidthPercent;
    private final int offsetHeightPercent;

    private final ArrayList<WaveFollowerComponent> waveFollowerComponents = new ArrayList<>();


    public WaveComponent(Supplier<Transform2D> waveFollowerSupplier,
                         int offsetWidthPercent,
                         int offsetHeightPercent,
                         int totalWaveWidth,
                         int totalWaveHeight,
                         int waveAmplitude,
                         int wavelengthPx,
                         int wavePeriodMillis,
                         float spriteScaleMultiplierX,
                         float spriteScaleMultiplierY,
                         int sortingLayer
    ){
        this.waveFollowerSupplier = waveFollowerSupplier;
        this.totalWaveWidth = totalWaveWidth;
        this.totalWaveHeight = totalWaveHeight;
        this.offsetWidthPercent = offsetWidthPercent;
        this.offsetHeightPercent = offsetHeightPercent;
        this.WaveAmplitude = waveAmplitude;
        this.WavelengthPx = wavelengthPx;
        this.WavePeriodMillis = wavePeriodMillis;
        this.SpriteScaleMultiplierX = spriteScaleMultiplierX;
        this.SpriteScaleMultiplierY = spriteScaleMultiplierY;
        this.sortingLayer = sortingLayer;
    }

    // TODO: Next - Sprite copy
    // Supplier functions
    @Override
    public void start(Transform2D ownerTransform, Canvas canvas, MiniSpriteSurface miniSpriteSurface) {
        int waveFollowerWidth = totalWaveWidth / 20;;
        int waveFollowerHeight = totalWaveHeight / 30;
        int waveFollowerCount = (int)(5f * ((float) totalWaveWidth) / waveFollowerWidth);

        for(int i = 0; i < waveFollowerCount; i++){
            double waveWidthPercentage = ((double) i) / (waveFollowerCount - 1);
            int relativeXPositionWave = (int)((waveWidthPercentage - 0.5f) * totalWaveWidth);

            WaveFollowerComponent waveFollowerComponent = new WaveFollowerComponent(
                    relativeXPositionWave,
                    WaveAmplitude,
                    WavelengthPx,
                    WavePeriodMillis
            );

            Transform2D waveFollower = waveFollowerSupplier.get();
            waveFollower.Parent = ownerTransform;
            waveFollower.PositionX = relativeXPositionWave;
            waveFollower.PositionY = 0;
            waveFollower.Width = waveFollowerWidth;
            waveFollower.Height = waveFollowerHeight;
            waveFollower.Components.add(waveFollowerComponent);

            waveFollowerComponents.add(waveFollowerComponent);
            miniSpriteSurface.AllTransforms.instantiateTransform(waveFollower, sortingLayer);
            Log.d("MiniSpriteWaveComponent", "Added wave follower at x position " + waveFollower.PositionX);
        }
    }

    @Override
    public void update(Transform2D ownerTransform, Canvas canvas, MiniSpriteSurface miniSpriteSurface) {
        for (WaveFollowerComponent follower : waveFollowerComponents) {
            follower.amplitude = WaveAmplitude;
            follower.wavelengthPx = WavelengthPx;
            follower.periodMillis = WavePeriodMillis;
            follower.spriteXScale = SpriteScaleMultiplierX;
            follower.spriteYScale = SpriteScaleMultiplierY;
        }
        Log.d("MiniSpriteWaveComponent", "Updating wave component");
    }

    @Override
    public Component createCopy() {
        return new WaveComponent(
                waveFollowerSupplier,
                offsetWidthPercent,
                offsetHeightPercent,
                totalWaveWidth,
                totalWaveHeight,
                WaveAmplitude,
                WavelengthPx,
                WavePeriodMillis,
                SpriteScaleMultiplierX,
                SpriteScaleMultiplierY,
                sortingLayer
        );
    }
}
