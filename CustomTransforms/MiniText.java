package RoboZero.MiniSprite.CustomTransforms;

import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.annotation.NonNull;

import RoboZero.MiniSprite.Transform2D;

/**
 * Wrap text within transform - allows tracking parent transform and adding components.
 * Note: Text size depends on Paint (e.g. paint.setTextSize(value);)
 */
public class MiniText extends Transform2D {
    public String TextValue;
    public Paint Paint;

    private MiniText(Builder builder){
        super(builder);
        TextValue = builder.textValue;
        Paint = builder.paint;
    }

    public MiniText(MiniText another){
        super(another);
        TextValue = another.TextValue;
        Paint = another.Paint;
    }

    public static class Builder extends Transform2D.Builder<MiniText.Builder> {
        private String textValue;
        private Paint paint;

        public Builder() {}

        /**
         * Set string of text to be displayed when drawn.
         * @param textValue Non-null string to display.
         * @return Self for builder pattern.
         */
        public Builder withTextValue(@NonNull String textValue){
            this.textValue = textValue;
            return this;
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
         * Note: Text size depends on Paint (e.g. paint.setTextSize(value);).
         * Width and height do not currently affect text.
         * @param width Width in pixels
         * @param height Height in pixels
         * @return Self for builder pattern
         */
        public Builder withDimensions(int width, int height){
            super.withDimensions(width, height);
            return this;
        }

        public MiniText build() {
            return new MiniText(this);
        }
    }

    // TODO: Anchoring and layouting within canvas
    @Override
    public void relativeDraw(Canvas canvas) {
        canvas.drawText(TextValue, 0,0 , Paint);
    }
}
