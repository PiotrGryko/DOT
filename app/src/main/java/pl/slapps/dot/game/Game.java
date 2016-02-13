package pl.slapps.dot.game;

import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;

import pl.slapps.dot.MainActivity;
import pl.slapps.dot.SurfaceRenderer;
import pl.slapps.dot.model.Route;
import pl.slapps.dot.model.Stage;
import pl.slapps.dot.generator.TileRoute;

/**
 * Created by piotr on 10/02/16.
 */
public class Game {


    private String TAG = Game.class.getName();

    public Game(MainActivity context, SurfaceRenderer gameView)
    {
        this.context=context;
        this.gameView=gameView;

        dotSize = gameView.screenWidth / 20;

    }

    public MainActivity context;
    public SurfaceRenderer gameView;



    private MainSprite mainSprite;
    private Background background;
    private Maze maze;

    private ArrayList<Explosion> explosions = new ArrayList<Explosion>();
    private Explosion currentExplosion;
    private Stage currentStage;

    public float dotSize;


    private int mDotLightPosHandle;
    private int mDotLightShinningHandle;
    private int mDotLightDistanceHandle;


    public int mExplosionLightOnePosHandle;
    public int mExplosionLightOneShinningHandle;
    public int mExplosionLightOneDistanceHandle;
    public int mExplosionLightOneColorHandle;


    public int mExplosionLightTwoPosHandle;
    public int mExplosionLightTwoShinningHandle;
    public int mExplosionLightTwoDistanceHandle;
    public int mExplosionLightTwoColorHandle;


    public int mPositionHandle;
    public int mColorHandle;
    public int mMVPMatrixHandle;

    public int mProgram;


    private boolean isRunnig = true;



    final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;      " +
                    // A constant representing the combined model/generator/projection matrix
                    "attribute vec4 vPosition;     " +
                    // Per-vertex position information we will pass in.

                    // Per-vertex color information we will pass in.
                    "varying vec3 v_Position;       " +
                    // This will be passed into the fragment shader.
                    // This will be passed into the fragment shader.


                    // The entry point for our vertex shader.
                    "void main()" +
                    "{" +
                    // Transform the vertex into eye space.
                    "    v_Position = vec3(vPosition);" +
                    // Pass through the color.

                    // gl_Position is a special variable used to store the final position.
                    // Multiply the vertex by the matrix to get the final point in normalized screen coordinates. +
                    "    gl_Position = uMVPMatrix * vPosition;" +
                    "}";


    public final String fragmentShaderCode =
            "struct LightSource" +
                    "{" +
                    "vec3 u_LightPos;" +
                    "float lightShinning;" +
                    "float lightDistance;" +
                    "vec4 lightColor;" +
                    "};" +

                    "uniform LightSource lights[3];" +

                    "precision mediump float;       // Set the default precision to medium. We don't need as high of a\n" +
                    "                               // precision in the fragment shader.\n" +
                    "varying vec3 v_Position;       // Interpolated position for this fragment.\n" +
                    "uniform vec4 vColor;          // This is the color from the vertex shader interpolated across the\n" +
                    "                               // triangle per fragment.\n" +


                    "// The entry point for our fragment shader.\n" +
                    "void main()\n" +
                    "{" +


                    "   float dotDistance = length(lights[0].u_LightPos - v_Position);\n" +
                    "   float dotDiffuse =  lights[0].lightShinning * (1.0 / (1.0 + (0.00007 * dotDistance * dotDistance* lights[0].lightDistance)));\n" +

                    "   float explosionOneDistance = length(lights[1].u_LightPos - v_Position);\n" +
                    "   float explosionOneDiffuse =  lights[1].lightShinning * (1.0 / (1.0 + (0.00007 * explosionOneDistance * explosionOneDistance* lights[1].lightDistance)));\n" +
                    "   vec4 explosionOneResult = explosionOneDiffuse * lights[1].lightColor;"+

                    "   float explosionTwoDistance = length(lights[2].u_LightPos - v_Position);\n" +
                    "   float explosionTwoDiffuse =  lights[2].lightShinning * (1.0 / (1.0 + (0.00007 * explosionTwoDistance * explosionTwoDistance* lights[2].lightDistance)));\n" +
                    "   vec4 explosionTwoResult = explosionTwoDiffuse * lights[2].lightColor;"+

                    "    // Multiply the color by the diffuse illumination level to get final output color.\n" +
                    "    gl_FragColor = vColor* dotDiffuse+explosionOneResult + explosionTwoResult;" +
                    "}";






    public ArrayList<Explosion> getExplosions() {
        return explosions;
    }


    public void initGameShaders() {


        // prepare shaders and OpenGL program
        int vertexShader = SurfaceRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = SurfaceRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);
        mProgram = gameView.createAndLinkProgram(vertexShader, fragmentShader,
                new String[]{"vPosition"});





        mDotLightPosHandle = GLES20.glGetUniformLocation(mProgram, "lights[0].u_LightPos");
        mDotLightDistanceHandle = GLES20.glGetUniformLocation(mProgram, "lights[0].lightDistance");
        mDotLightShinningHandle = GLES20.glGetUniformLocation(mProgram, "lights[0].lightShinning");


        mExplosionLightOnePosHandle = GLES20.glGetUniformLocation(mProgram, "lights[1].u_LightPos");
        mExplosionLightOneDistanceHandle = GLES20.glGetUniformLocation(mProgram, "lights[1].lightDistance");
        mExplosionLightOneShinningHandle = GLES20.glGetUniformLocation(mProgram, "lights[1].lightShinning");
        mExplosionLightOneColorHandle = GLES20.glGetUniformLocation(mProgram, "lights[1].lightColor");


        mExplosionLightTwoPosHandle = GLES20.glGetUniformLocation(mProgram, "lights[2].u_LightPos");
        mExplosionLightTwoDistanceHandle = GLES20.glGetUniformLocation(mProgram, "lights[2].lightDistance");
        mExplosionLightTwoShinningHandle = GLES20.glGetUniformLocation(mProgram, "lights[2].lightShinning");
        mExplosionLightTwoColorHandle = GLES20.glGetUniformLocation(mProgram, "lights[2].lightColor");


        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");


    }


    public void initStage(Stage stage) {
        currentStage=stage;
        background = null;
        Explosion.initBuffers();
        background = new Background(this, stage.colorBackground);

        maze = null;
        maze = new Maze(this, stage);

        resetDot();

    }


    public void onDraw(float [] mMVPMatrix)
    {





        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(mProgram);




        GLES20.glUniform3f(mDotLightPosHandle, mainSprite.getCenterX(), mainSprite.getCenterY(), 0.0f);
        GLES20.glUniform1f(mDotLightDistanceHandle, 0.5f);
        GLES20.glUniform1f(mDotLightShinningHandle, 1.0f);






        if (background != null)
            background.drawGl2(mMVPMatrix);



        if (maze != null && mainSprite != null && isRunnig) {

            maze.drawGL20(mMVPMatrix);


            mainSprite.drawGl2(mMVPMatrix);

            for (int i = 0; i < explosions.size(); i++) {
                explosions.get(i).drawGl2(mMVPMatrix);
            }
             //if (currentExplosion != null)
             //   toggleColors(currentExplosion.getProgress());


        }
        update();



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



    public boolean onTouchEvent(MotionEvent event) {


        Log.d("zzz","game on touch");
        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN: {



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



    public void stopDot() {
        mainSprite.setMove(0, 0);
    }

    public void resetDot() {

        if (maze.width > maze.height)
            dotSize = maze.height * 3 / 10;
        else
            dotSize = maze.width * 3 / 10;
        mainSprite = null;
        float tile_width = gameView.screenWidth / maze.horizontalSize;
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
        Explosion e = new Explosion(this, mainSprite.centerX, mainSprite.centerY, System.currentTimeMillis(), mainSprite.spriteSpeed, (int) dotSize, currentStage.colorExplosionStart, currentStage.colorExplosionEnd);

        //generator.resetDot();
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

}
