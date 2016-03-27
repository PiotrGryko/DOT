package pl.slapps.dot.game;

import android.opengl.GLES20;

import android.view.MotionEvent;

import pl.slapps.dot.MainActivity;
import pl.slapps.dot.SurfaceRenderer;
import pl.slapps.dot.model.Route;
import pl.slapps.dot.model.Stage;
import pl.slapps.dot.generator.builder.TileRoute;

/**
 * Created by piotr on 10/02/16.
 */
public class Game {


    public Game(MainActivity context, SurfaceRenderer gameView) {
        this.context = context;
        this.gameView = gameView;

        dotSize = gameView.screenWidth / 20;

    }

    public MainActivity context;
    public SurfaceRenderer gameView;


    private MainSprite mainSprite;
    private Background background;
    public Maze maze;

    private ExplosionManager explosionManager;

    public Stage currentStage;

    public float dotSize;


    public int mDotLightPosHandle;
    public int mDotLightShinningHandle;
    public int mDotLightDistanceHandle;
    public int mDotLightColorHandle;







    public int mPositionHandle;
    public int mColorHandle;
    public int mMVPMatrixHandle;

    public int mPointColorHandle;
    public int mPointPositionHandle;
    public int mPointSizeHandle;
    public int mPointMVPMatrixHandle;


    public int mProgram;
    public int mPointProgram;


    private boolean isPreview = false;

    public void setPreview(boolean preview) {
        this.isPreview = preview;
    }

    public boolean getPreview() {
        return isPreview;
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
                    "uniform vec4 vColor;          // This is the color from the vertex shader interpolated across the\n"

                    + "void main()                    "
                    + "{                              "
                    + "   gl_FragColor = vColor;      "
                    + "}                              ";


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
            "struct LightSource" +
                    "{" +
                    "vec3 u_LightPos;" +
                    "float lightShinning;" +
                    "float lightDistance;" +
                    "vec4 lightColor;" +
                    "};" +

                    "uniform LightSource lights[5];" +

                    "precision mediump float;       // Set the default precision to medium. We don't need as high of a\n" +
                    "                               // precision in the fragment shader.\n" +
                    "varying vec3 v_Position;       // Interpolated position for this fragment.\n" +
                    "uniform vec4 vColor;          // This is the color from the vertex shader interpolated across the\n" +
                    "                               // triangle per fragment.\n" +


                    "// The entry point for our fragment shader.\n" +
                    "void main()\n" +
                    "{" +


                    "   float dotDistance = length(lights[0].u_LightPos - v_Position);\n" +
                    "   float dotDiffuse =  lights[0].lightShinning * (1.0 / (1.0 + 0.00007 *( dotDistance * dotDistance* lights[0].lightDistance)));\n" +
                    "   vec4 dotResult = dotDiffuse * lights[0].lightColor;" +

                    "   float explosionOneDistance = length(lights[1].u_LightPos - v_Position);\n" +
                    "   float explosionOneDiffuse =  lights[1].lightShinning * (1.0 / (1.0 + 0.00007 *( explosionOneDistance * explosionOneDistance* lights[1].lightDistance)));\n" +
                    "   vec4 explosionOneResult = explosionOneDiffuse * lights[1].lightColor;" +

                    "   float explosionTwoDistance = length(lights[2].u_LightPos - v_Position);\n" +
                    "   float explosionTwoDiffuse =  lights[2].lightShinning * (1.0 / (1.0 + 0.00007 *( explosionTwoDistance * explosionTwoDistance* lights[2].lightDistance)));\n" +
                    "   vec4 explosionTwoResult = explosionTwoDiffuse * lights[2].lightColor;" +

                    "   float explosionThreeDistance = length(lights[3].u_LightPos - v_Position);\n" +
                    "   float explosionThreeDiffuse =  lights[3].lightShinning * (1.0 / (1.0 + 0.00007 *( explosionThreeDistance * explosionThreeDistance* lights[3].lightDistance)));\n" +
                    "   vec4 explosionThreeResult = explosionThreeDiffuse * lights[3].lightColor;" +

                    "   float explosionFourDistance = length(lights[4].u_LightPos - v_Position);\n" +
                    "   float explosionFourDiffuse =  lights[4].lightShinning * (1.0 / (1.0 + 0.00007 *( explosionFourDistance * explosionFourDistance* lights[4].lightDistance)));\n" +
                    "   vec4 explosionFourResult = explosionFourDiffuse * lights[4].lightColor;" +

                    "    // Multiply the color by the diffuse illumination level to get final output color.\n" +
                    "    gl_FragColor = vColor *dotDiffuse +explosionOneResult + explosionTwoResult +explosionThreeResult + explosionFourResult;" +
                    "}";


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
        mDotLightColorHandle = GLES20.glGetUniformLocation(mProgram, "lights[0].lightColor");







        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");


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


    }


    public void initStage(Stage stage) {

        context.getSoundsManager().configure(stage.config.sounds);
        context.getSoundsManager().playBackgroundSound();


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

    }


    public void onDraw(float[] mMVPMatrix) {


        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glUseProgram(mProgram);


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


        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN: {


                if (mainSprite != null) {
                    if (!mainSprite.isMoving()) {

                        startDot();

                    } else {

                        TileRoute current = maze.getCurrentRouteObject(mainSprite.centerX, mainSprite.centerY);
                        if (current != null) {

                            if (current.sound.equals(""))
                                context.getSoundsManager().playMoveSound();
                            else
                                context.getSoundsManager().playRawFile(current.sound);
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
        if(explosionManager !=null)
        {
            explosionManager.configure(currentStage.config);
        }

        context.getSoundsManager().configure(currentStage.config.sounds);
    }


    private void update() {
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

                context.getSoundsManager().playMoveSound();

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

        if (sound)
            context.getSoundsManager().playCrashSound();
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
