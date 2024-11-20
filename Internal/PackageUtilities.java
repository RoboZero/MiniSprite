package RoboZero.MiniSprite.Internal;

import android.graphics.Paint;
import android.util.Log;

import androidx.annotation.ColorInt;

public class PackageUtilities {
    public static String PACKAGE_NAME = "MiniSprite";
    public static String LOG_TAG = PACKAGE_NAME + ".Single";
    public static String LOG_CONTINUOUS_TAG = PACKAGE_NAME + ".MiniSprite.Continuous";

    public static StringBuilder LOG_BUILDER = new StringBuilder();

    private static Paint DEBUG_PAINT;
    private static Paint DEBUG_OUTLINE_PAINT;

    public static void logBuilderSingle(){
        Log.d(LOG_TAG, LOG_BUILDER.toString());
        LOG_BUILDER.setLength(0);
    }

    public static void logContinuous(String message){
        Log.d(LOG_CONTINUOUS_TAG, message);
    }

    public static Paint getDebugPaint(@ColorInt int color){
        if(DEBUG_PAINT == null){
            DEBUG_PAINT = new Paint();
            DEBUG_PAINT.setStyle(Paint.Style.FILL);
        }

        DEBUG_PAINT.setColor(color);

        return DEBUG_PAINT;
    }

    public static Paint getDebugOutlinePaint(@ColorInt int color){
        if(DEBUG_OUTLINE_PAINT == null){
            DEBUG_OUTLINE_PAINT = new Paint();
            DEBUG_OUTLINE_PAINT.setStyle(Paint.Style.STROKE);
            DEBUG_OUTLINE_PAINT.setStrokeWidth(5);
        }

        DEBUG_OUTLINE_PAINT.setColor(color);

        return DEBUG_OUTLINE_PAINT;
    }

    public static void LogBuilderContinuous(){
        Log.d(LOG_CONTINUOUS_TAG, LOG_BUILDER.toString());
        LOG_BUILDER.setLength(0);
    }
}
