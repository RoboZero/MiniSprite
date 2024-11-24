### Summary
MiniSprite
Tiny package made for Wearable & Mobile Apps class
Made for midterm Fidget Phone.
Based on Unity component architecture

### Reasons to use
You are using SurfaceView
You want to easily move, rotate, and set a local-space parent on the SurfaceView = Transform
You want to animate a sprite on SurfaceView = MiniSprite, Transform
You want to share capabilities (e.g. falling sprite, player sprite) using components

### How to Use:

Activity OnCreate
1. Get SurfaceView in Activity
2. Create MiniSpriteSurface with SurfaceView and store reference.
3. If you need to update transforms in MiniSpriteSurface update loop
- Implement IMiniSpriteSurfaceListener on your Activity
- Add listener to MiniSprite surface
- Update transforms onPreUpdate
4. Create Transforms (MiniSprite, MiniText, etc. ) and add components
- Use builder pattern .with for useful settings
- Use builder pattern .addComponent to add multiple components at once
5. Add all Transforms that will be updated to MiniSpriteSurface.TransformCollection
- Can create transforms in onPreUpdate once (boolean check)

You're done!
All transforms will update based on components
Move groups of transforms in onPreUpdate
It's an extension of a Surface, doesn't interfere with anything else.

### Example 1
```MiniSpriteSurface miniSpriteSurface;
MyComponent myComponent;

MyActivity extends AppCompatActivity implements IMiniSpriteSurfaceListener {
...
    
    // Don't delete Sprite Surface, will clean up Animate/Draw thread.
    MiniSpriteSurface miniSpriteSurface;
    
    // Store any reference to Transforms, MiniSprites, etc. you want to update later.
    MiniSprite wave;
    
    ...
    
    protected void onCreate(Bundle savedInstanceState) {
        ...
        SurfaceView mySurface = findViewById(R.id.surfaceView);
        miniSpriteSurface = new MiniSpriteSurface(mySurface);
        miniSpriteSurface.addListener(this)
        ...
    }
    
    // I like instantiating in onPreUpdate to set size with the canvas scale
    boolean created = false
    @Override
    public void onPreUpdate(MiniSpriteSurface miniSpriteSurface, Canvas canvas) {
        ...
    
        myComponent = new MyComponent(myArgs...);
            
        miniSpriteSurface.AllTransforms.instantiateTransform( // Instantiate any transform so components are updated
                     new MiniSprite.Builder(R.drawable.my_drawable, getResources()) // Create sprite. Sprite can be sliced if in grid
                             .withName("My Name") // Name for logging
                             .withDimensions(myWidth, myHeight) //  Width and Height of sprite
                             .withPaint(myPaint) // Sprite can use a paint
                             .withPositionXY(myXPosition, myYPosition) // Position either in world or local space depending on parent.
                             .withRotationAngleDegrees(degrees0to360) // Rotation around center point of transform
                             .withAnchorOffsetXY(myXAnchorOffset, myYAnchorOffset) // After position and rotation set around center, translate (rotate around different axis)
                             .withComponent(myComponent) // Components to be updated by MiniSpriteSurface
                             .withParent(myParentTransform) // Follow transform position and rotation, position now in local space
                             .withSliceDimensions(2, 2) // Indicates my_drawable is a 2x2 spritesheet. Can set Slice Index to show correct sprite. 
                             .build(),
                    2 // Sorting layer. Set higher to have MiniSpriteSurface draw above other sprites
            );
    
        Transform2D titleLeft = new MiniText.Builder() // Can create text that can use same transform parents and components. No UI anchoring yet. 
                    .withName("Title")
                    .withTextValue("My Title")
                    .withPaint(titleTextPaint)
                    .withPositionXY(myXPosition, myYPosition)
                    .withParent(myParentTransform)
                    .build();
    
        miniSpriteSurface.AllTransforms.instantiateTransform(
                    titleRight,
                    mySortingLayerInt // Value to set order of when transform is drawn. Anything drawn before (lower value layer) will be covered by anything drawn after (higher value layer)
            );
    
        created = true;
        ... 
    }
    
    @Override
    public void onPreDrawSprites(MiniSpriteSurface miniSpriteSurface, Canvas canvas) {
        ...
        canvas.drawColor(getResources().getColor(R.color.black)); // Optionally clear canvas of previously drawn sprites. 
        ...
    }
    
    @Override
    public void onPostDrawSprites(MiniSpriteSurface miniSpriteSurface, Canvas canvas) {
        ...
        Update any stored components. 
        myComponent.myValue = newValue;
        ...
    }

}```
