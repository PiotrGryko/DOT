package pl.slapps.dot.game;

import android.net.Uri;
import android.opengl.GLES20;

import android.util.Log;
import android.view.MotionEvent;

import pl.slapps.dot.MainActivity;
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


    public int mProgram;
    public int mPointProgram;


    private boolean isPreview = false;
    private boolean isPaused = true;


    public float colorFilter[] = {0.0f, 0.0f, 0.0f, 1.0f};


    public void setPreview(boolean preview) {
        this.isPreview = preview;
    }

    public boolean getPreview() {
        return isPreview;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;

    }

    public boolean getPaused() {
        return isPaused;
    }

    // Define a simple shader program for our point.


    final String pointVertexShader =
            "uniform mat4 uMVPMatrix;      "
                    + "attribute vec4 vPosition;     "
                    + "attribute float vSize;     "
                    + "void main()                    "
                    + "{                              "

                    + "   gl_Position = uMVPMatrix   "
                    + "               * vPosition;   "
                    + "   gl_PointSize = vSize;         "
                    + "}                              ";

    final String pointFragmentShader =
            "precision mediump float;       " +
                    "uniform lowp vec4 vColor;          // This is the color from the vertex shader interpolated across the\n"

                    + "void main()                    "
                    + "{                              "
                    + "   gl_FragColor = vColor;      "
                    + "}                              ";
/*


    final static String vertexShaderCode =
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


    final static String fragmentShaderCode =

            "precision lowp float;" +
                    "struct LightSource" +
                    "{" +
                    "lowp vec3 u_LightPos;" +
                    "lowp float lightShinning;" +
                    "lowp float lightDistance;" +
                    "lowp vec4 lightColor;" +
                    "};" +

                    "uniform LightSource lights[5];" +


                    "varying lowp vec3 v_Position;       // Interpolated position for this fragment.\n" +
                    "uniform lowp vec4 vColor;          // This is the color from the vertex shader interpolated across the\n" +
                    "                               // triangle per fragment.\n" +

                    "uniform lowp vec3 dotLightPos;" +
                    "uniform lowp float dotlightShinning;" +
                    "uniform lowp float dotlightDistance;" +

                    "// The entry point for our fragment shader.\n" +
                    "void main()\n" +
                    "{" +


                    "   lowp float dotDistance = length(dotLightPos - v_Position);\n" +
                    "   lowp float dotDiffuse =  dotlightShinning * (1.0 / (1.0 + 0.025 *( dotDistance * dotlightDistance)));\n" +

                    "   lowp float explosionOneDistance = length(lights[1].u_LightPos - v_Position);\n" +
                    "   lowp float explosionOneDiffuse =  lights[1].lightShinning * (1.0 / (1.0 + 0.025 *(  explosionOneDistance* lights[1].lightDistance)));\n" +
                    "   lowp vec4 explosionOneResult = explosionOneDiffuse * lights[1].lightColor;" +

                    "   lowp float explosionTwoDistance = length(lights[2].u_LightPos - v_Position);\n" +
                    "   lowp float explosionTwoDiffuse =  lights[2].lightShinning * (1.0 / (1.0 + 0.025 *( explosionTwoDistance* lights[2].lightDistance)));\n" +
                    "   lowp vec4 explosionTwoResult = explosionTwoDiffuse * lights[2].lightColor;" +

                    "   lowp float explosionThreeDistance = length(lights[3].u_LightPos - v_Position);\n" +
                    "   lowp float explosionThreeDiffuse =  lights[3].lightShinning * (1.0 / (1.0 + 0.025 *(  explosionThreeDistance* lights[3].lightDistance)));\n" +
                    "   lowp vec4 explosionThreeResult = explosionThreeDiffuse * lights[3].lightColor;" +

                    "   lowp float explosionFourDistance = length(lights[4].u_LightPos - v_Position);\n" +
                    "   lowp float explosionFourDiffuse =  lights[4].lightShinning * (1.0 / (1.0 + 0.025 *( explosionFourDistance* lights[4].lightDistance)));\n" +
                    "   lowp vec4 explosionFourResult = explosionFourDiffuse * lights[4].lightColor;" +

                    //"    // Multiply the color by the diffuse illumination level to get final output color.\n" +
                    "    gl_FragColor = vColor*dotDiffuse +explosionOneResult + explosionTwoResult +explosionThreeResult + explosionFourResult;" +
                    "}";




*/


    final static String vertexShaderCode =
            "uniform mat4 uMVPMatrix;      " +
                    "struct LightSource" +
                    "{" +
                    "lowp vec3 u_LightPos;" +
                    "lowp float lightShinning;" +
                    "lowp float lightDistance;" +
                    "lowp vec4 lightColor;" +
                    "};" +
                    "uniform lowp vec4 vColor;" +
                    "uniform lowp vec4 vColorFilter;" +

                    "varying lowp vec4 color;" +
                    "varying lowp vec4 explosionColor;" +


                    "uniform LightSource lights[2];" +
                    "varying vec3 v_Position;" +
                    "attribute vec4 vPosition;     " +

                    "void main()" +
                    "{" +

                    "v_Position = vec3(vPosition);" +


                    "   lowp float explosionOneDistance = length(lights[0].u_LightPos - v_Position);\n" +
                    "   lowp float explosionOneDiffuse =  lights[0].lightShinning * (1.0 / (1.0 + 0.015 *  explosionOneDistance* lights[0].lightDistance));\n" +
                    "   lowp vec4 explosionOneResult = explosionOneDiffuse * lights[0].lightColor;" +

                    "   lowp float explosionTwoDistance = length(lights[1].u_LightPos - v_Position);\n" +
                    "   lowp float explosionTwoDiffuse =  lights[1].lightShinning * (1.0 / (1.0 + 0.015 * explosionTwoDistance* lights[1].lightDistance));\n" +
                    "   lowp vec4 explosionTwoResult = explosionTwoDiffuse * lights[1].lightColor;" +

                    "explosionColor=explosionOneResult+explosionTwoResult;" +
                    "color = vColor+vColorFilter;" +

                    "    gl_Position = uMVPMatrix * vPosition;" +
                    "}";


    final static String fragmentShaderCode =

            "precision lowp float;" +
                    "varying lowp vec3 v_Position;" +
                    "uniform lowp vec3 dotLightPos;" +
                    "uniform lowp float dotlightShinning;" +
                    "uniform lowp float dotlightDistance;" +
                    "varying lowp vec4 color;" +
                    "varying lowp vec4 explosionColor;" +

                    "void main()" +
                    "{" +

                    "   lowp float dotDistance = length(dotLightPos - v_Position);" +
                    "   lowp float dotDiffuse =  dotlightShinning * (1.0 / (1.0 + 0.025 *( dotDistance * dotlightDistance)));" +

                    "    gl_FragColor = color*dotDiffuse+explosionColor;" +
                    "}";


    public void initGameShaders() {

        int vertexPointShader = SurfaceRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER,
                pointVertexShader);
        int fragmentPointShader = SurfaceRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                pointFragmentShader);

        mPointProgram = gameView.createAndLinkProgram(vertexPointShader, fragmentPointShader,
                new String[]{"vPosition"});

        mPointSizeHandle = GLES20.glGetAttribLocation(mPointProgram, "vSize");
        mPointPositionHandle = GLES20.glGetAttribLocation(mPointProgram, "vPosition");
        mPointMVPMatrixHandle = GLES20.glGetUniformLocation(mPointProgram, "uMVPMatrix");
        mPointColorHandle = GLES20.glGetUniformLocation(mPointProgram, "vColor");

        // prepare shaders and OpenGL program
        int vertexShader = SurfaceRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = SurfaceRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);
        mProgram = gameView.createAndLinkProgram(vertexShader, fragmentShader,
                new String[]{"vPosition"});


        mDotLightPosHandle = GLES20.glGetUniformLocation(mProgram, "dotLightPos");
        mDotLightDistanceHandle = GLES20.glGetUniformLocation(mProgram, "dotlightDistance");
        mDotLightShinningHandle = GLES20.glGetUniformLocation(mProgram, "dotlightShinning");
        //  mDotLightColorHandle = GLES20.glGetUniformLocation(mProgram, "lights[0].lightColor");


        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        mColorFilterHandle = GLES20.glGetUniformLocation(mProgram, "vColorFilter");

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");


        //   if(!updateThread.isAlive())
        //   updateThread.start();
    }


    public void initStage(Stage stage) {

        //updateThread.interrupt();


        MainActivity.sendAction(SoundsService.ACTION_CONFIG, stage.config.sounds);
        MainActivity.sendAction(SoundsService.ACTION_BACKGROUND, null);

//        SoundsService.getSoundsManager().configure(stage.config.sounds);
//        SoundsService.getSoundsManager().playBackgroundSound();
        colorFilter = Util.parseColor(stage.config.colors.colorFilter);


        currentStage = stage;
        background = null;
        // ExplosionManager.initBuffers();
        background = new Background(this, stage.config);

        Log.d("rrr", "init stage ");
        maze = null;
        maze = new Maze(this, stage);

        if (maze.width > maze.height)
            dotSize = maze.height * 3 / 10;
        else
            dotSize = maze.width * 3 / 10;

        explosionManager = new ExplosionManager(this, dotSize, stage.config);

        resetDot();

        //updateThread.start();
        Log.d("SSS", "Thread startd");
    }


    public void onDraw(float[] mMVPMatrix) {


        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glUseProgram(mProgram);

        GLES20.glUniform4fv(mColorFilterHandle, 1, colorFilter, 0);


        if (background != null)
            background.drawGl2(mMVPMatrix);


        if (maze != null)
            maze.drawGL20(mMVPMatrix);


        if (mainSprite != null)
            mainSprite.drawGl2(mMVPMatrix);


        if (explosionManager != null)
            explosionManager.drawGl2(mMVPMatrix);
        update();


    }


    private void setDotMovement(Route.Movement movement) {

        if (movement == null || mainSprite == null)
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

        MainActivity.sendAction(SoundsService.ACTION_CONFIG, currentStage.config.sounds);

        // SoundsService.getSoundsManager().configure(currentStage.config.sounds);

        colorFilter = Util.parseColor(currentStage.config.colors.colorFilter);

    }


    private void update() {

        if (isPaused)
            return;

        if (mainSprite != null)
            mainSprite.update();

        if (explosionManager != null)
            explosionManager.update(System.currentTimeMillis());

    }


    public void startDot() {

        if (maze != null) {
            TileRoute r = maze.getCurrentRouteObject(mainSprite.centerX, mainSprite.centerY);


            if (r != null) {
                Route.Movement movement = r.getDirection();

                MainActivity.sendAction(SoundsService.ACTION_PRESS, null);

//                SoundsService.getSoundsManager().playMoveSound();

                setDotMovement(movement);
            }
        }
    }


    public void stopDot() {
        mainSprite.setMove(0, 0);
    }

    public void resetDot() {


        mainSprite = null;
        TileRoute startRoute = maze.getStartRoute();
        float startX = startRoute.topX + startRoute.width / 2;
        float startY = startRoute.topY + startRoute.height / 2;
        mainSprite = new MainSprite(this, startX, startY,
                (int) dotSize, (int) dotSize, currentStage.config);


        maze.initPoints();
        stopDot();
    }

    public void destroyDot() {
        mainSprite = null;

    }

    public ExplosionManager explodeDot(boolean sound) {
        explosionManager.explode(mainSprite.centerX, mainSprite.centerY, mainSprite.spriteSpeed);

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
