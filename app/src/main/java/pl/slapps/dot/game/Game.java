package pl.slapps.dot.game;

import android.net.Uri;
import android.opengl.GLES20;

import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;

import pl.slapps.dot.MainActivity;
import pl.slapps.dot.R;
import pl.slapps.dot.Shaders;
import pl.slapps.dot.SoundsService;
import pl.slapps.dot.SurfaceRenderer;
import pl.slapps.dot.drawing.Util;
import pl.slapps.dot.model.Route;
import pl.slapps.dot.model.Stage;
import pl.slapps.dot.generator.builder.TileRoute;

/**
 * Created by piotr on 10/02/16.
 */
public class Game {


    public Game(SurfaceRenderer gameView) {
        //this.context = context;
        this.gameView = gameView;

        dotSize = gameView.screenWidth / 20;

    }

    //public MainActivity context;
    public SurfaceRenderer gameView;


    private MainSprite mainSprite;
    private Background background;
    public Maze maze;

    public ExplosionManager explosionManager;

    public Stage currentStage;

    public float dotSize;


    public int mDotLightPosHandle;
    public int mDotLightShinningHandle;
    public int mDotLightDistanceHandle;
    // public int mDotLightColorHandle;


    public int mPositionHandle;
    public int mColorHandle;
    public int mColorFilterHandle;
    public int mMVPMatrixHandle;

    public int mPointColorHandle;
    public int mPointPositionHandle;
    public int mPointSizeHandle;
    public int mPointMVPMatrixHandle;

    public int mTextureMVPMatrixHandle;

    public int mTextureUniformHandle;
    public int mTexturePositionHandle;


    /**
     * This will be used to pass in model texture coordinate information.
     */
    public int mTextureCoordinateHandle;


    public int mProgram;
    public int mPointProgram;
    public int mTextureProgram;


    private boolean isPreview = false;
    private boolean isPaused = true;
    private boolean isCrashed = true;


    public float colorFilter[] = {0.0f, 0.0f, 0.0f, 1.0f};

    public void onPause() {
        if (background != null)
            background.onPause();

        lastRealTimeMeasurement_ms=0;

    }

    public void onResume() {
        if (background != null)
            background.onResume();

        lastRealTimeMeasurement_ms=0;


    }


    public void setPreview(boolean preview) {
        this.isPreview = preview;
        lastRealTimeMeasurement_ms=0;
    }

    public boolean getPreview() {
        return isPreview;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
        //if(isPaused)
            lastRealTimeMeasurement_ms=0;


    }

    public boolean getPaused() {
        return isPaused;
    }

    // Define a simple shader program for our point.


    public void initGameShaders() {

        int vertexPointShader = SurfaceRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER,
                Shaders.pointVertexShader);
        int fragmentPointShader = SurfaceRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                Shaders.pointFragmentShader);

        mPointProgram = gameView.createAndLinkProgram(vertexPointShader, fragmentPointShader,
                new String[]{"vPosition"});

        mPointSizeHandle = GLES20.glGetAttribLocation(mPointProgram, "vSize");
        mPointPositionHandle = GLES20.glGetAttribLocation(mPointProgram, "vPosition");
        mPointMVPMatrixHandle = GLES20.glGetUniformLocation(mPointProgram, "uMVPMatrix");
        mPointColorHandle = GLES20.glGetUniformLocation(mPointProgram, "vColor");

        ///////////////////////////////////////////////////////////////////////////////////////////
        // prepare shaders and OpenGL program
        int vertexShader = SurfaceRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER,
                Shaders.getVertexShaderCode());
        int fragmentShader = SurfaceRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                Shaders.getFragmentShaderCode());
        mProgram = gameView.createAndLinkProgram(vertexShader, fragmentShader,
                new String[]{"vPosition"});


        mDotLightPosHandle = GLES20.glGetUniformLocation(mProgram, "lights[0].u_LightPos");
        mDotLightDistanceHandle = GLES20.glGetUniformLocation(mProgram, "lights[0].lightDistance");
        mDotLightShinningHandle = GLES20.glGetUniformLocation(mProgram, "lights[0].lightShinning");
        //  mDotLightColorHandle = GLES20.glGetUniformLocation(mProgram, "lights[0].lightColor");


        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        //mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        mColorFilterHandle = GLES20.glGetUniformLocation(mProgram, "vColorFilter");

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
/////////////////////////////////////////////////////////////////////////////////////////


        int vertexTextureShader = SurfaceRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER,
                Shaders.textureVertexShader);
        int fragmentTextureShader = SurfaceRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                Shaders.textureFragmentShader);

        mTextureProgram = gameView.createAndLinkProgram(vertexTextureShader, fragmentTextureShader,
                new String[]{"vPosition", "a_TexCoordinate"});

        mTexturePositionHandle = GLES20.glGetAttribLocation(mTextureProgram, "vPosition");
        mTextureMVPMatrixHandle = GLES20.glGetUniformLocation(mTextureProgram, "uMVPMatrix");



        // background.loadTexture(gameView.context,R.drawable.bubles);


        //   if(!updateThread.isAlive())
        //   updateThread.start();
    }


    public void initStage(Stage stage) {

        //updateThread.interrupt();


        MainActivity.configServiceStage(stage);
        //MainActivity.sendAction(SoundsService.ACTION_CONFIG, stage.config.sounds);
        MainActivity.sendAction(SoundsService.ACTION_BACKGROUND, null);

//        SoundsService.getSoundsManager().configure(stage.config.sounds);
//        SoundsService.getSoundsManager().playBackgroundSound();
        colorFilter = Util.parseColor(stage.config.colors.colorFilter);


        currentStage = stage;
        background = null;
        // ExplosionManager.initBuffers();
        background = new Background(this, stage.config);


        maze = null;
        maze = new Maze(this, stage);

        if (maze.width > maze.height)
            dotSize = maze.height * 3 / 10;
        else
            dotSize = maze.width * 3 / 10;

        explosionManager = new ExplosionManager(this, dotSize, stage.config);

        resetDot();


        background.setPosition(mainSprite.centerX, mainSprite.centerY);

        //updateThread.start();
    }


    public void update(float ratio) {

        if (isPaused)
            return;

        // long tmp = System.currentTimeMillis();


        //   long current = System.nanoTime();
        //   float diff = (current - lastUpdate)/frame;
        //   if(lastUpdate>0 && diff>1.5f)
        //   Log.e("ccc", "update time =" + diff);
        //   lastUpdate=current;


        //Log.e("ccc","move "+ratio);
        if (!isCrashed && mainSprite != null)
            mainSprite.update(ratio);

        if (background != null && !isCrashed && mainSprite != null)
            background.setPosition(mainSprite.centerX, mainSprite.centerY);

        if (explosionManager != null)
            explosionManager.update(System.currentTimeMillis());


    }


    float smoothedDeltaRealTime_ms = 17.5f; // initial value, Optionally you can save the new computed value (will change with each hardware) in Preferences to optimize the first drawing frames
    float movAverageDeltaTime_ms = smoothedDeltaRealTime_ms; // mov Average start with default value
    long lastRealTimeMeasurement_ms; // temporal storage for last time measurement

    // smooth constant elements to play with
    static final float movAveragePeriod = 40; // #frames involved in average calc (suggested values 5-100)
    static final float smoothFactor = 0.1f; // adjusting ratio (suggested values 0.01-0.5)


    public void onDraw(float[] mMVPMatrix) {

        update(smoothedDeltaRealTime_ms);





        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        //BACKGROUND SETS MVP MATRIX!!!!!!! REMEMBER WHEN DISABLE
        if (background != null)
            background.drawGl2(mMVPMatrix);


        GLES20.glUniform4fv(mColorFilterHandle, 1, colorFilter, 0);

        if (maze != null)
            maze.drawGL20(mMVPMatrix);

        if (!isCrashed && mainSprite != null)
            mainSprite.drawGl2(mMVPMatrix);

        if (explosionManager != null)
            explosionManager.drawGl2(mMVPMatrix);


        // Moving average calc
        long currTimePick_ms = SystemClock.uptimeMillis();
        float realTimeElapsed_ms;
        if (lastRealTimeMeasurement_ms > 0) {
            realTimeElapsed_ms = (currTimePick_ms - lastRealTimeMeasurement_ms);
        } else {
            realTimeElapsed_ms = smoothedDeltaRealTime_ms; // just the first time

        }
        movAverageDeltaTime_ms = (realTimeElapsed_ms + movAverageDeltaTime_ms * (movAveragePeriod - 1)) / movAveragePeriod;

        // Calc a better aproximation for smooth stepTime
        smoothedDeltaRealTime_ms = smoothedDeltaRealTime_ms + (movAverageDeltaTime_ms - smoothedDeltaRealTime_ms) * smoothFactor;

        lastRealTimeMeasurement_ms = currTimePick_ms;



    }


    private void setDotMovement(Route.Movement movement) {

        float spriteSpeed =0;
        if (movement == null || mainSprite == null)
            return;
        else
            spriteSpeed=mainSprite.spriteSpeed;


        switch (movement) {

            case BOTTOMRIGHT:
            case TOPRIGHT:
            case LEFTRIGHT:
                mainSprite.setMove(spriteSpeed, 0);
                break;
            case BOTTOMLEFT:
            case TOPLEFT:
            case RIGHTLEFT:
                mainSprite.setMove(-spriteSpeed, 0);
                break;
            case LEFTTOP:
            case RIGHTTOP:
            case BOTTOMTOP:

                mainSprite.setMove(0, -spriteSpeed);
                break;
            case LEFTBOTTOM:
            case RIGHTBOTTOM:
            case TOPBOTTOM:

                mainSprite.setMove(0, spriteSpeed);
                break;


        }

    }


    public boolean onTouchEvent(MotionEvent event) {

        if (isPaused)
            isPaused = false;

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN: {


                if (mainSprite != null) {
                    if (!mainSprite.isMoving()) {

                        startDot();

                    } else {

                        TileRoute current = maze.getCurrentRouteObject(mainSprite.centerX, mainSprite.centerY);
                        if (current != null) {

                            if (current.sound.equals(""))
                                MainActivity.sendAction(SoundsService.ACTION_PRESS, null);
                            else
                                MainActivity.sendAction(SoundsService.ACTION_RAW, current.sound);
                            setDotMovement(getNextMove(current));
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


    public void configRoute(TileRoute route) {
        if (maze != null)
            maze.configRoute(route);
    }


    public void configure() {
        if (background != null)
            background.configure(currentStage.config);
        if (mainSprite != null)
            mainSprite.configure(currentStage.config);
        if (maze != null)
            maze.configure(currentStage.config);
        if (explosionManager != null) {
            explosionManager.configure(currentStage.config);
        }

        // MainActivity.configServiceStage(currentStage);
        //MainActivity.sendAction(SoundsService.ACTION_CONFIG, currentStage.config.sounds);

        // SoundsService.getSoundsManager().configure(currentStage.config.sounds);

        colorFilter = Util.parseColor(currentStage.config.colors.colorFilter);

    }

    public void configureSounds() {
        MainActivity.configServiceStage(currentStage);
    }


    public void startDot() {

        if (maze != null) {
            TileRoute r = maze.getCurrentRouteObject(mainSprite.centerX, mainSprite.centerY);


            if (r != null) {
                Route.Movement movement = r.getDirection();

                MainActivity.sendAction(SoundsService.ACTION_PRESS, null);


                setDotMovement(movement);
            }
        }
    }


    public void stopDot() {
        mainSprite.setMove(0, 0);
    }

    public void resetDot() {

        isCrashed = true;
        mainSprite = null;
        TileRoute startRoute = maze.getStartRoute();
        float startX = startRoute.topX + startRoute.width / 2;
        float startY = startRoute.topY + startRoute.height / 2;
        mainSprite = new MainSprite(this, startX, startY,
                (int) dotSize, (int) dotSize, currentStage.config);
        isCrashed = false;

        maze.initPoints();
        stopDot();

    }

    public void destroyDot() {
        isCrashed = true;
        mainSprite = null;


    }

    public ExplosionManager explodeDot(boolean sound) {
        explosionManager.explode(mainSprite.centerX, mainSprite.centerY, mainSprite.spriteSpeed);

        if(sound)
        MainActivity.sendAction(SoundsService.ACTION_CRASH, null);

        //if (sound)
        //    SoundsService.getSoundsManager().playCrashSound();
        return null;
    }


    public void crashDot(boolean sound) {


        explodeDot(sound);

        mainSprite = new MainSprite(this, mainSprite.centerX, mainSprite.centerY,
                (int) dotSize / 2, (int) dotSize / 2, currentStage.config);

        mainSprite.setPrepareToDie(true);

        TileRoute current = maze.getCurrentRouteObject(mainSprite.centerX, mainSprite.centerY);

        if (current != null)
            setDotMovement(current.getDirection());


    }


    public Route.Movement getNextMove(TileRoute startRoute) {


        if (mainSprite != null && mainSprite.lastChangeRoute != null) {
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
            if (mainSprite != null)
                mainSprite.lastChangeRoute = startRoute;
            return startRoute.next;
        }
        return startRoute.next;
    }

}
