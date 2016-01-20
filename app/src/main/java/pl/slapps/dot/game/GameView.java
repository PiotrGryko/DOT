package pl.slapps.dot.game;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import pl.slapps.dot.GeneratorLayout;
import pl.slapps.dot.MainActivity;
import pl.slapps.dot.R;
import pl.slapps.dot.animation.Explosion;
import pl.slapps.dot.model.Route;
import pl.slapps.dot.model.Stage;
import pl.slapps.dot.tile.TileRoute;


public class GameView extends GLSurfaceView implements GLSurfaceView.Renderer {

    private String TAG = "GameView";

    private MainSprite mainSprite;
    private Background background;
    private Maze maze;
    public MainActivity context;

    public int screenWidth;
    public int screenHeight;
    public float dotSize;
    private Generator generator;
    private Handler handler = new Handler();

    //private String explosionColorStart;
    ///private String explosionColorEnd;

    private Stage currentStage;
    private Explosion currentExplosion;



    //public String shipColor = "#000000";
    private boolean isRunnig = false;

    public void setRunnig(boolean isRunnig) {
        this.isRunnig = isRunnig;

        if (!isRunnig && generator != null)
            context.clearStageState();


    }

    public void loadStageData(Stage stage) {


        //explosionColorEnd=stage.colorExplosionEnd;
        //explosionColorStart=stage.colorExplosionStart;
        //shipColor=stage.colorShip;
        generator = null;
        background = null;
        background = new Background(this, stage.colorBackground);

        maze = null;
        maze = new Maze(this, stage);
        currentStage = stage;

        resetDot();
    }

    public void initGenerator(int widht, int height) {

        generator = null;
        generator = new Generator(this, widht, height);

        context.drawerContent.addView(generator.getLayout().onCreateView(generator));
        context.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    private ArrayList<Explosion> explosions = new ArrayList<Explosion>();


    private void init(MainActivity context) {
        this.setRenderer(this);
        this.context = context;


        screenWidth = this.getResources().getDisplayMetrics().widthPixels;
        screenHeight = this.getResources().getDisplayMetrics().heightPixels;


        dotSize = screenWidth / 20;


    }

    public GameView(MainActivity context) {
        super(context);
        init(context);


    }

    public GameView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init((MainActivity) context);


    }


    public void moveToNextLvl() {
        //resetDot();
        //maze.clearRoutes();
        context.getSoundsManager().playFinishSound();
        //isRunnig = false;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                context.moveToNextStage();

            }
        }, 1000);


        //  onPause();

    }


    public Maze getMaze() {
        return maze;
    }

    public Background getGameBackground() {
        return background;
    }


    public void addExplosion(Explosion e) {
        //if(explosions.size()==0)
        //    toggleColors();
        explosions.add(e);
        currentExplosion = e;
    }

    public void removeExplosion(Explosion e) {
        explosions.remove(e);
        if (e == currentExplosion)
            currentExplosion = null;
        //if(explosions.size()==0)
        //    toggleColors();
        // Log.d(TAG, Integer.toString(explosions.size()));
    }


    private void update() {
        if (mainSprite != null)
            mainSprite.update();
        for (int i = 0; i < explosions.size(); i++) {
            explosions.get(i).update(System.currentTimeMillis());
        }

    }

    @Override
    public void onDrawFrame(GL10 gl) {


        // TODO Auto-generated method stub
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        if (background != null)
            background.draw(gl);


        if (generator != null && isRunnig)
            generator.draw(gl);
        else if (maze != null && mainSprite != null && isRunnig) {


            maze.draw(gl);


            mainSprite.draw(gl);

            for (int i = 0; i < explosions.size(); i++) {
                explosions.get(i).draw(gl);
            }
            if (currentExplosion != null)
                toggleColors(currentExplosion.getProgress());


        }
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

        update();

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        if (height == 0)
            height = 1;

        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();

        gl.glOrthof(0, width, height, 0, -1, 1);
        gl.glViewport(0, 0, width, height);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //	points.loadTexture(gl);
        //	gl.glEnable(GL10.GL_TEXTURE_2D);
        //background.loadTexture(gl);



        //gl.glClearDepthf(0);
      //  gl.glEnable(GL10.GL_DEPTH_TEST);
        //gl.glEnable(GL10.GL_LIGHTING);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
       // gl.glEnable(GL10.GL_COLOR_MATERIAL);
       // gl.glEnable(GL10.GL_LIGHT0);
        //gl.glEnable(GL10.GL_LIGHT1);
        //gl.glLightModelf(GL10.GL_LIGHT_MODEL_TWO_SIDE);


        Log.d(TAG,"light enabled ");


    }

    public void startDot() {

        if (maze != null) {
            TileRoute r = maze.getCurrentRouteObject(mainSprite.centerX, mainSprite.centerY);

            Log.d(TAG, "start dot " + r);

            if (r != null) {
                Route.Movement movement = r.getDirection();

                context.getSoundsManager().playMoveSound();

                setDotMovement(movement);
            }
        }
    }

    private void setDotMovement(Route.Movement movement) {

        if (movement == null)
            return;

        switch (movement) {

            case BOTTOMRIGHT:
            case TOPRIGHT:
            case LEFTRIGHT:
                mainSprite.setMove(mainSprite.spriteSpeed, 0);
                break;
            case BOTTOMLEFT:
            case TOPLEFT:
            case RIGHTLEFT:
                mainSprite.setMove(-mainSprite.spriteSpeed, 0);
                break;
            case LEFTTOP:
            case RIGHTTOP:
            case BOTTOMTOP:

                mainSprite.setMove(0, -mainSprite.spriteSpeed);
                break;
            case LEFTBOTTOM:
            case RIGHTBOTTOM:
            case TOPBOTTOM:

                mainSprite.setMove(0, mainSprite.spriteSpeed);
                break;


        }

    }

    public void stopDot() {
        mainSprite.setMove(0, 0);
    }

    public void resetDot() {

        if (maze.width > maze.height)
            dotSize = maze.height * 3 / 10;
        else
            dotSize = maze.width * 3 / 10;
        mainSprite = null;
        float tile_width = screenWidth / maze.horizontalSize;
        TileRoute startRoute = maze.getStartRoute();
        float startX = startRoute.topX + startRoute.width / 2;
        float startY = startRoute.topY + startRoute.height / 2;
        mainSprite = new MainSprite(this, startX, startY,
                (int) dotSize, (int) dotSize, currentStage.colorShip);

        stopDot();
    }

    public void destroyDot() {
        mainSprite = null;
        TileRoute startRoute = maze.getStartRoute();
        float startX = startRoute.topX + startRoute.width / 2;
        float startY = startRoute.topY + startRoute.height / 2;
        mainSprite = new MainSprite(this, startX, startY,
                0, 0, currentStage.colorShip);

        stopDot();
    }

    public Explosion explodeDot(boolean sound) {
        Explosion e = new Explosion(this, 50, mainSprite.centerX, mainSprite.centerY, System.currentTimeMillis(), mainSprite.spriteSpeed, (int) dotSize, currentStage.colorExplosionStart, currentStage.colorExplosionEnd);

        //view.resetDot();
        addExplosion(e);
        if (sound)
            context.getSoundsManager().playCrashSound();
        return e;
    }

    public void crashDot(boolean sound) {


        Explosion e = explodeDot(sound);


        dotSize = e.size;

        mainSprite = null;

        mainSprite = new MainSprite(this, e.x, e.y,
                (int) dotSize, (int) dotSize, currentStage.colorShip);
        mainSprite.setPrepareToDie(true);
        //mainSprite.r = e.r;
        //mainSprite.g = e.g;
        //mainSprite.b = e.b;
        TileRoute current = maze.getCurrentRouteObject(mainSprite.centerX, mainSprite.centerY);

        if (current != null)

            setDotMovement(current.getDirection());


        //  stopDot();
    }


    public Route.Movement getNextMove(TileRoute startRoute) {


        if (mainSprite.lastChangeRoute != null) {
            int lastRoute = maze.routes.indexOf(mainSprite.lastChangeRoute);
            int currentRoute = maze.routes.indexOf(startRoute);

            if (currentRoute > lastRoute) {
                mainSprite.lastChangeRoute = startRoute;
                return startRoute.next;
            } else if (lastRoute < maze.routes.size() - 1) {
                mainSprite.lastChangeRoute = maze.routes.get(lastRoute + 1);
                return mainSprite.lastChangeRoute.next;

            }
        } else {
            mainSprite.lastChangeRoute = startRoute;
            return startRoute.next;
        }
        return startRoute.next;
    }


    public String estimateColors(String colorStart, String colorEnd, float progress) {
        int startColor = Color.parseColor(colorStart);
        int endColor = Color.parseColor(colorEnd);

        if (progress < 0)
            progress = 0;
        if (progress > 1)
            progress = 1;

        int startRed;
        int endRed;

        if (Color.red(startColor) > Color.red(endColor)) {
            startRed = Color.red(startColor);
            endRed = Color.red(endColor);
        } else {
            startRed = Color.red(endColor);
            endRed = Color.red(startColor);
        }

        int startGreen;
        int endGreen;

        if (Color.green(startColor) > Color.green(endColor)) {
            startGreen = Color.green(startColor);
            endGreen = Color.green(endColor);
        } else {
            startGreen = Color.green(endColor);
            endGreen = Color.green(startColor);
        }

        int startBlue;
        int endBlue;

        if (Color.blue(startColor) > Color.blue(endColor)) {
            startBlue = Color.blue(startColor);
            endBlue = Color.blue(endColor);
        } else {
            startBlue = Color.blue(endColor);
            endBlue = Color.blue(startColor);
        }


        float redDiff = endRed - startRed;
        float greenDiff = endGreen - startGreen;
        float blueDiff = endBlue - startBlue;

        int r = (int) (progress * redDiff + startRed);
        int g = (int) (progress * greenDiff + startGreen);
        int b = (int) (progress * blueDiff + startBlue);

        int returnColor = Color.rgb(r, g, b);
        return "#" + Integer.toHexString(returnColor);
    }

    public void toggleColors(float progress) {

        //background.setColor(maze.getPath().backgroundColor);
        //maze.getPath().setColor(backgroundColor);

        Log.d(TAG, "toggle colors " + progress);

        background.setColor(estimateColors(currentStage.colorRoute, currentStage.colorBackground, progress));
        maze.getPath().setColor(estimateColors(currentStage.colorRoute, currentStage.colorBackground, 1 - progress));

    }

    // touch events

    public boolean onTouchEvent(MotionEvent event) {


        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN: {


                if (generator != null && isRunnig) {
                    return generator.onTouch(event);
                }

                if (mainSprite != null && isRunnig) {
                    if (!mainSprite.isMoving()) {

                        startDot();

                    } else {

                        TileRoute current = maze.getCurrentRouteObject(mainSprite.centerX, mainSprite.centerY);
                        if (current != null) {

                            if (!current.sound.equals(""))
                                context.getSoundsManager().playRawFile(current.sound);
                            else
                                context.getSoundsManager().playMoveSound();
                            setDotMovement(getNextMove(current));
                            //setDotMovement(current.next);
                        }


                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP: {

                break;
            }

        }

        return true;

    }

}
