package RoboZero.MiniSprite.CustomTransforms;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import RoboZero.MiniSprite.Internal.PackageUtilities;
import RoboZero.MiniSprite.Transform2D;

/**
 * Draw sprite with drawable.
 * Implicitly sliced using drawable width/height and slice column and row information (1x1 by default).
 * Ex.
 * Drawable is 128x128 pixels and 4 rows 4 columns = slice into 16 32x32 bit images
 * Index 0 is top left, index 15 is bottom right.
 * SetSliceIndex wraps any value higher than 15 back to 0.
 *
 */
public class MiniSprite extends Transform2D {
    public Paint Paint;

    private final int drawableId;
    private final Resources resources;
    private Bitmap scaledBitmap;

    private final int sliceCount;
    private int sliceColumnLength = 1;
    private int sliceRowLength = 1;
    private int sliceIndex;
    private int widthPerSlice;
    private int heightPerSlice;

    private MiniSprite(Builder builder) {
        super(builder);
        drawableId = builder.drawableId;
        resources = builder.resources;
        Width = builder.width;
        Height = builder.height;
        Paint = builder.paint;
        sliceColumnLength = builder.sliceColumnLength;
        sliceRowLength = builder.sliceRowLength;

        if(builder.bitmap != null)
            scaledBitmap = builder.bitmap;
        else if(builder.downscale)
            downscaleBitmap(builder.width, builder.height);
        else
            decodeBitmap();

        sliceCount = sliceColumnLength * sliceRowLength;
        setSliceIndex(builder.sliceIndex);
    }

    /**
     * Safely creates a MiniSprite. Nifty!
     * More info on the pattern: <a href="https://stackoverflow.com/questions/17164375/subclassing-a-java-builder-class">...</a>
     */
    public static class Builder extends Transform2D.Builder<Builder> {
        private int drawableId;
        private Resources resources;
        private Bitmap bitmap = null;

        private Paint paint;
        private int width;
        private int height;
        private boolean downscale = false;
        private int sliceIndex;
        private int sliceColumnLength = 1;
        private int sliceRowLength = 1;

        /**
         * Creates bitmap using drawable and resources
         * @param drawableId R.drawable.my_resource
         * @param resources Activity's getResources(), or any resource collection.
         */
        public Builder(int drawableId, Resources resources) {
            this.drawableId = drawableId;
            this.resources = resources;
        }

        public Builder(Bitmap bitmap){
            this.bitmap = bitmap;
        }

        /**
         * @param paint Paint used when drawing sprite on canvas
         * @return Self for builder pattern.
         */
        public Builder withPaint(Paint paint){
            this.paint = paint;
            return this;
        }

        /**
         * Width and height on screen when drawing.
         * @param width Width in pixels
         * @param height Height in pixels
         * @return Self for builder pattern/
         */
        public Builder withDimensions(int width, int height){
            this.width = width;
            this.height = height;
            return this;
        }

        /**
         * Downscales bitmap.
         * Recommended if dimensions won't get bigger over lifespan to reduce bitmap size.
         * @param downscale
         * @return
         */
        public Builder withDownscaleFlag(boolean downscale){
            this.downscale = downscale;
            return this;
        }

        /**
         * The drawable can be a spritesheet with fixed row/column size.
         * Implicitly sliced using drawable width/height and slice column and row information (1x1 by default).
         * Ex.
         * Drawable is 128x128 pixels and 4 rows 4 columns = slice into 16 32x32 bit images
         * Index 0 is top left, index 15 is bottom right.
         * setSliceIndex wraps any value higher than 15 back to 0.
         * @param sliceColumnLength Number of columns in your spritesheet.
         * @param sliceRowLength Number of rows in your spritesheet.
         * @return Self for builder pattern.
         */
        public Builder withSliceDimensions(int sliceColumnLength, int sliceRowLength){
            this.sliceColumnLength = sliceColumnLength;
            this.sliceRowLength = sliceRowLength;
            return this;
        }

        /**
         * Sets slice index. Wraps any value higher than slice count (columns * rows) back to 0.
         * See with slice dimensions for more info.
         * @param sliceIndex Which slice of spritesheet to display
         * @return Self for builder pattern.
         */
        public Builder withSliceIndex(int sliceIndex){
            this.sliceIndex = sliceIndex;
            return this;
        }

        public MiniSprite build() {
            return new MiniSprite(this);
        }
    }

    @Override
    public void relativeDraw(Canvas canvas) {
        int sourceSpriteLeft = (sliceIndex) * (widthPerSlice);
        int sourceSpriteTop = (sliceIndex % sliceRowLength) * heightPerSlice;
        int sourceSpriteRight = sourceSpriteLeft + widthPerSlice;
        int sourceSpriteBottom = sourceSpriteTop + heightPerSlice;

        PackageUtilities.LOG_BUILDER.append("Source Sprite: (L:").append(sourceSpriteLeft).append(", T:").append(sourceSpriteTop).append(", R:").append(sourceSpriteRight).append(", B:").append(sourceSpriteBottom).append(")");

        PackageUtilities.logContinuous("Drawing sprite");

        canvas.drawBitmap(scaledBitmap,
                new Rect(sourceSpriteLeft, sourceSpriteTop, sourceSpriteRight, sourceSpriteBottom),
                new Rect(-Width/2, -Height/2, Width/2, Height/2),
                Paint);
    }

    public int getSliceIndex() { return sliceIndex; }
    public int getSliceCount() { return sliceCount; }
    public void setSliceIndex(int sliceIndex) { this.sliceIndex = sliceIndex % sliceCount; }
    public void nextSlice(){ setSliceIndex(sliceIndex + 1); }

    /**
     * Get sprite's bitmap that was either assigned or built from resources.
     * Manipulating this bitmaps' size may cause slicing error.
     * Recommended to reuse when creating many similar sprites.
     * @return Sprite's bitmap.
     */
    public Bitmap getBitmap() { return scaledBitmap; }

    /**
     * Decode bitmap based on resources and drawableId
     */
    public void decodeBitmap(){
        PackageUtilities.LOG_BUILDER.append("Decoding bitmap\n");

        Bitmap bitmap = BitmapFactory.decodeResource(resources, drawableId);
        PackageUtilities.LOG_BUILDER.append("Decoded resource ").append(resources.getResourceName(drawableId)).append(" of scale (W:").append(bitmap.getWidth()).append(", H: ").append(bitmap.getHeight()).append(")\n");
        PackageUtilities.logBuilderSingle();
        this.widthPerSlice = (int)((float) bitmap.getWidth() / sliceColumnLength);
        this.heightPerSlice = (int)((float) bitmap.getHeight() / sliceRowLength);
        this.scaledBitmap = bitmap;
    }

    /**
     * If rescaling sprite, may want to rescale bitmap if much smaller to improve performance.
     * @param bitmapWidth New x pixel size of drawable itself. Downsize -> only applies if below current x size.
     * @param bitmapHeight New y pixel size of drawable itself. Downsize -> only applies if below current y size.
     */
    public void downscaleBitmap(int bitmapWidth, int bitmapHeight){
        PackageUtilities.LOG_BUILDER.append("Downscaling bitmap\n");

        Bitmap bitmap = BitmapFactory.decodeResource(resources, drawableId);
        PackageUtilities.LOG_BUILDER.append("Decoded resource ").append(resources.getResourceName(drawableId)).append(" of scale (W:").append(bitmap.getWidth()).append(", H: ").append(bitmap.getHeight()).append(")\n");

        if(bitmapWidth * bitmapHeight < bitmap.getWidth() * bitmap.getHeight()){
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmapWidth, bitmapHeight,false);
            PackageUtilities.LOG_BUILDER.append("Created scaled bitmap").append(" of scale (W:").append(bitmap.getWidth()).append(", H: ").append(bitmap.getHeight()).append(")\n");
        }

        this.widthPerSlice = (int)((float) bitmap.getWidth() / sliceColumnLength);
        this.heightPerSlice = (int)((float) bitmap.getHeight() / sliceRowLength);
        PackageUtilities.LOG_BUILDER.append("Pixels per slice with Columns: ").append(sliceColumnLength).append(" Rows: ").append(sliceRowLength).append(" is W:").append(widthPerSlice).append(", H:").append(heightPerSlice).append(")\n");

        this.scaledBitmap = bitmap;
        PackageUtilities.logBuilderSingle();
    }
}
