package MiniSprite.Internal;

public class Animator extends Thread {
    private static final int SLEEP_TIME = 50;

    IDrawable drawableActivity;
    boolean is_running=false;

    public Animator(IDrawable drawableActivity){
        this.drawableActivity = drawableActivity;
    }

    public void run(){
        is_running = true;

        while(is_running){
            drawableActivity.draw();

            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void finish(){
        is_running = false;
    }
}
