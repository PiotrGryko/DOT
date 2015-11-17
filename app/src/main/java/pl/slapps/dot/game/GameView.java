package pl.slapps.dot.game;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import org.json.JSONException;
import org.json.JSONObject;

import pl.slapps.dot.MainActivity;
import pl.slapps.dot.R;
import pl.slapps.dot.animation.Explosion;
import pl.slapps.dot.model.Background;
import pl.slapps.dot.model.MainSprite;
import pl.slapps.dot.route.Route;


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

    private String explosionColorStart;
    private String explosionColorEnd;
    private String backgroundColor;


    public float spriteSpeed = this.getResources().getDimension(R.dimen.speed);

    public String shipColor = "#000000";
    private boolean isRunnig = false;

    public void setRunnig(boolean isRunnig) {
        this.isRunnig = isRunnig;

        if(!isRunnig && generator!=null)
            context.clearStageState();



    }

    public void loadStageData(JSONObject jsonStage) {
        try {

            JSONObject colors = jsonStage.has("colors") ? jsonStage.getJSONObject("colors") : new JSONObject();

            explosionColorStart = colors.has("explosion_start") ? colors.getString("explosion_start") : "#FF0000";
            backgroundColor = colors.has("background") ? colors.getString("background") : "#ff9999";

            explosionColorEnd = colors.has("explosion_end") ? colors.getString("explosion_end") : "#700BCB";
            shipColor = colors.has("ship") ? colors.getString("ship") : "#000000";

        } catch (JSONException e) {
            e.printStackTrace();
        }

        generator = null;
        background = null;
        background = new Background(this, backgroundColor);

        maze = null;
        maze = new Maze(this, jsonStage);


        resetDot();
    }

    public void initGenerator(int widht, int height) {

        generator = null;
        generator = new Generator(this, widht, height);
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
        explosions.add(e);
    }

    public void removeExplosion(Explosion e) {
        explosions.remove(e);
        // Log.d(TAG, Integer.toString(explosions.size()));
    }


    private void update()
    {
        if(mainSprite!=null)
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



        if (generator != null  && isRunnig)
            generator.draw(gl);
        else if (maze != null  && mainSprite!=null  && isRunnig) {


            maze.draw(gl);


            mainSprite.draw(gl);

            for (int i = 0; i < explosions.size(); i++) {
                explosions.get(i).draw(gl);
            }


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


    }

    public void startDot() {

        if (maze != null) {
            Route r = maze.getCurrentRouteObject(mainSprite.centerX, mainSprite.centerY);

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
                mainSprite.setMove(maze.spriteSpeed, 0);
                break;
            case BOTTOMLEFT:
            case TOPLEFT:
            case RIGHTLEFT:
                mainSprite.setMove(-maze.spriteSpeed, 0);
                break;
            case LEFTTOP:
            case RIGHTTOP:
            case BOTTOMTOP:

                mainSprite.setMove(0, -maze.spriteSpeed);
                break;
            case LEFTBOTTOM:
            case RIGHTBOTTOM:
            case TOPBOTTOM:

                mainSprite.setMove(0, maze.spriteSpeed);
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
        Route startRoute = maze.getStartRoute();
        float startX = startRoute.topX + startRoute.width / 2;
        float startY = startRoute.topY + startRoute.height / 2;
        mainSprite = new MainSprite(this, startX, startY,
                (int) dotSize, (int) dotSize, shipColor);

        stopDot();
    }

    public void destroyDot() {
        mainSprite = null;
        Route startRoute = maze.getStartRoute();
        float startX = startRoute.topX + startRoute.width / 2;
        float startY = startRoute.topY + startRoute.height / 2;
        mainSprite = new MainSprite(this, startX, startY,
                0, 0, shipColor);

        stopDot();
    }

    public Explosion explodeDot(boolean sound) {
        Explosion e = new Explosion(this, 50, mainSprite.centerX, mainSprite.centerY, System.currentTimeMillis(), spriteSpeed, (int) dotSize, explosionColorStart, explosionColorEnd);

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
                (int) dotSize, (int) dotSize, shipColor);
        mainSprite.setPrepareToDie(true);
        //mainSprite.r = e.r;
        //mainSprite.g = e.g;
        //mainSprite.b = e.b;
        Route current = maze.getCurrentRouteObject(mainSprite.centerX, mainSprite.centerY);

        if (current != null)
            setDotMovement(current.getDirection());


        //  stopDot();
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

                        Route current = maze.getCurrentRouteObject(mainSprite.centerX, mainSprite.centerY);
                        if (current != null) {
                            context.getSoundsManager().playMoveSound();

                            setDotMovement(current.next);
                        }


                    }
                }
                break;
            }

        }

        return true;

    }

}
