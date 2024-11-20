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
        a. Implement IMiniSpriteSurfaceListener on your Activity 
        b. Add listener to MiniSprite surface
        c. Update transforms onPreUpdate
    4. Create Transforms (MiniSprite, MiniText, etc. ) and add components
        a. Use builder pattern .with for useful settings
        b. Use builder pattern .addComponent to add multiple components at once
    5. Add all Transforms that will be updated to MiniSpriteSurface.TransformCollection
        a. Can create transforms in onPreUpdate once (boolean check)

You're done!
All transforms will update based on components
Move groups of transforms in onPreUpdate
It's an extension of a Surface, doesn't interfere with anything else.

### Example
Ex. 

MiniSpriteSurface miniSpriteSurface;
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
                         .withComponent(myComponent) // Components to be updated by MiniSpriteSurface
                         .withParent(myParentTransform) // Follow transform position and rotation, position now in local space
                         .withSliceDimensions(2, 2) // Indicates my_drawable is a 2x2 spritesheet. Can set Slice Index to show correct sprite. 
                         .build(),
                2 // Sorting layer. Set higher to have MiniSpriteSurface draw above other sprites
        );

    Transform2D titleLeft = new MiniText.Builder() // Can create text that can use same transform parents and components. No UI anchoring yet. 
                .withName("Title")
                .withTextValue("My Titel")
                .withPaint(titleTextPaint)
                .withPositionXY(myXPosition, myYPosition)
                .withParent(myParentTransform)
                .build();

    miniSpriteSurface.AllTransforms.instantiateTransform(
                titleRight,
                4
        );

    created = true;
    ... 
}

@Override
public void onPreDrawSprites(MiniSpriteSurface miniSpriteSurface, Canvas canvas) {
    ...
    ...
}

@Override
public void onPostDrawSprites(MiniSpriteSurface miniSpriteSurface, Canvas canvas) {
    ...
    Update any stored components. 
    myComponent.myValue = newValue;
    ...
}

}
