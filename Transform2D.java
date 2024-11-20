package RoboZero.MiniSprite;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;

import java.util.ArrayList;

import RoboZero.MiniSprite.Internal.PackageUtilities;

/**
 * A Transform is a name for anything with a position and orientation in space (you transform it's position)
 * Allows Canvas item to be positioned and rotated in world space
 * Or relative to a parent transform (think a player moving with a platform)
 */
public class Transform2D {
    public String Name;
    public Transform2D Parent;
    public int PositionX;
    public int PositionY;
    public int Width;
    public int Height;
    public float RotationAngleDegrees;
    public ArrayList<Component> Components;
    public boolean DebugMode;

    protected boolean started;

    protected Transform2D(Builder<?> builder){
        Name = builder.name;
        Parent = builder.parent;
        PositionX = builder.positionX;
        PositionY = builder.positionY;
        Width = builder.width;
        Height = builder.height;
        RotationAngleDegrees = builder.rotationAngleDegrees;
        DebugMode = builder.debugMode;
        Components = builder.components;
    }

    /**
     * Deep copy components with their tryCopy functions.
     * @param another
     */
    public Transform2D(Transform2D another){
        Name = another.Name;
        Parent = another.Parent;
        PositionX = another.PositionX;
        PositionY = another.PositionY;
        Width = another.Width;
        Height = another.Height;
        RotationAngleDegrees = another.RotationAngleDegrees;

        Components = new ArrayList<>();
        for (Component component : another.Components){
            Components.add(component.createCopy());
        }
    }

    /**
     * Safely creates a Transform2D.
     * Can be extended!
     * More info: https://stackoverflow.com/questions/17164375/subclassing-a-java-builder-class
     */
    public static class Builder<T extends Builder<T>> {
        private String name;
        private Transform2D parent;
        private int positionX;
        private int positionY;
        private int width;
        private int height;
        private float rotationAngleDegrees;
        private boolean debugMode;
        private final ArrayList<Component> components = new ArrayList<>();

        public Builder() {}

        /**
         * Set a custom name, useful for debugging and
         * searching for specific objects.
         * Also can search by name when iterating Transform collections.
         * @param name Name you want to see when debugging.
         * @return Self for builder pattern.
         */
        public T withName(String name){
            this.name = name;
            return (T) this;
        }

        /**
         * Position on screen in pixels when drawing.
         * Canvas starts at top left (0, 0). Bottom is positive (x, y).
         * Calculated before rotation.
         * Can be global or local position, local if Parent != null
         * @param x Position on x axis
         * @param y Position on y axis
         * @return Self for builder pattern,
         */
        public T withPositionXY(int x, int y){
            this.positionX = x;
            this.positionY = y;
            return (T) this;
        }

        /**
         * Width and height on screen when drawing.
         * Defined and used by custom Transforms.
         * No inherent use when manipulating Canvas.
         * TODO: Allow for scale.
         * @param width Width in pixels
         * @param height Height in pixels
         * @return Self for builder pattern.
         */
        public T withDimensions(int width, int height){
            this.width = width;
            this.height = height;
            return (T) this;
        }

        /**
         * When you want a Transform to move and rotate
         * using another Transform as a basis, set the basis Transform as a parent.
         * In Unity, this is effectively setting a child in the hierarchy.
         * @param parent Other transform. When drawn, position and rotation will include parent's values.
         * @return Self for builder pattern.
         */
        public T withParent(Transform2D parent){
            this.parent = parent;
            return (T) this;
        }

        /**
         * A normal basis has 3 vectors in 3D space, where you can rotate around each one.
         * In 2D, only uses z-axis rotation from 0-360 degrees.
         * 0 is orientated with z-axis direction up.
         * @param rotationAngleDegrees Degrees of rotation on z-axis starting up rotating counter clockwise.
         * @return Self for Builder pattern.
         */
        public T withRotationAngleDegrees(int rotationAngleDegrees){
            this.rotationAngleDegrees = rotationAngleDegrees;
            return (T) this;
        }

        /**
         * Useful debug tool. Draw circles at where transform is supposed to be.
         * Green circle = Position XY.
         * Red circle with rectangle = Rotation Angle 2D
         * @param debugMode True if you want to see drawn circles indicating position and rotation.
         * @return Self for Builder pattern.
         */
        public T withDebugMode(boolean debugMode){
            this.debugMode = debugMode;
            return (T) this;
        }

        /**
         * When transform is added to MiniSpriteSurface,
         * each added component will be called
         * with this Transform as the owner
         * @param component Component to add (recommended to make copies of components. Shared references can be unpredictable.
         * @return Self for fluent builder pattern.
         */
        public T withComponent(Component component){
            components.add(component);
            return (T) this;
        }

        public Transform2D build(){
            return new Transform2D(this);
        }
    }

    /**
     * Draw the transform on canvas
     * Manipulates canvas translation and rotation, then children's RelativeDraw is called
     * If debug mode enabled, draws circles
     * Green circle = Position XY.
     * Red circle with rectangle = Rotation Angle 2D
     * @param canvas Canvas to temporarily save, manipulate and draw on.
     */
    public void draw(Canvas canvas){
        int parentXPosition = 0;
        int parentYPosition = 0;
        float parentRotationAngle = 0;

        if(Parent != null){
            parentXPosition = Parent.PositionX;
            parentYPosition = Parent.PositionY;
            parentRotationAngle = Parent.RotationAngleDegrees;
        }

        canvas.save();

        canvas.translate(parentXPosition, parentYPosition);
        canvas.rotate(parentRotationAngle);
        // TODO: Consider and resize with parent scale
        canvas.translate(PositionX, PositionY);
        canvas.rotate(RotationAngleDegrees);

        PackageUtilities.logContinuous("Drawing transform: " + Name);
        relativeDraw(canvas);

        if(DebugMode){
            canvas.drawCircle(0, 0, 40, PackageUtilities.getDebugPaint(Color.RED));
            canvas.drawRect(new Rect(-10,-80,10,0), PackageUtilities.getDebugPaint(Color.RED));
            canvas.drawRect(new Rect(-Width /2, -Height /2, Width /2, Height /2), PackageUtilities.getDebugOutlinePaint(Color.RED));
            canvas.drawCircle(0, 0, 40, PackageUtilities.getDebugPaint(Color.GREEN));
        }

        PackageUtilities.LogBuilderContinuous();
        canvas.restore();
    }

    /**
     * Subclass Transform and override RelativeDraw.
     * Called by Draw, which already translates, rotates, and scales the canvas, considering the parent.
     * This means drawing at 0, 0 will have the correct location.
     * @param canvas Canvas to draw on. Ex. Sprite, Text, etc.
     */
    public void relativeDraw(Canvas canvas) {}

    /**
     * Called by MiniSpriteSurface's Animate thread.
     * Note: The transform itself only runs start once, not by component.
     * Add all components before adding to MiniSpriteSurface collection!
     * @param canvas Canvas that components will use.
     * @param miniSpriteSurface Surface that components will use.
     */
    public void start(Canvas canvas, MiniSpriteSurface miniSpriteSurface){
        if(started) return;

        for (Component component: Components) {
            component.start(this, canvas, miniSpriteSurface);
        }

        started = true;
    }

    /**
     * Called by MiniSpriteSurface's Animate thread.
     * Runs whenever animate ticks.
     * @param canvas Canvas that components will use.
     * @param miniSpriteSurface Surface that components will use.
     */
    public void update(Canvas canvas, MiniSpriteSurface miniSpriteSurface){
        for (Component component: Components) {
            component.update(this, canvas, miniSpriteSurface);
        }
    }

    /**
     * Retrieve previously added and stored component using class.
     * Ex. getComponent<T>(</T>FallingComponent.class)
     * @param type For all components, find first of type.
     * @return Component class you can cast. If doesn't exist, return null.
     * @param <T> Any possible component that can be started & updated.
     */
    public <T extends Component> T getComponent(Class<T> type){
        for(Component component : Components){
            if(component.getClass() == type){
                return (T) component;
            }
        }
        return null;
    }

    /**
     * Using position and dimensions (width, height), simple calculation if would be intersecting
     * @param other Transform with position and dimensions within space.
     * @return True if would be intersecting, false if not.
     */
    public boolean isCollidingWith(Transform2D other){
        return Math.abs(other.PositionX - PositionX) < other.Width &&
                Math.abs(other.PositionY - PositionY) < other.Height;
    }
}
