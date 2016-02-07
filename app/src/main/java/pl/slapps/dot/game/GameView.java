package pl.slapps.dot.game;

import java.util.ArrayList;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import pl.slapps.dot.MainActivity;
import pl.slapps.dot.animation.Explosion;
import pl.slapps.dot.model.Route;
import pl.slapps.dot.model.Stage;
import pl.slapps.dot.tile.TileRoute;


public class GameView extends GLSurfaceView implements GLSurfaceView.Renderer {

    private static String TAG = GameView.class.getName();


    private MainSprite mainSprite;
    private Background background;
    private Maze maze;
    public MainActivity context;
    public int screenWidth;
    public int screenHeight;
    public float dotSize;
    private Generator generator;
    private Handler handler = new Handler();

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    public final float[] mMVPMatrix = new float[16];
    public final float[] mProjectionMatrix = new float[16];
    public final float[] mViewMatrix = new float[16];

    private float[] mModelMatrix = new float[16];

    private int mLightPosHandle;
    private int mLightShinningHandle;
    private int mLightDistanceHandle;
    public int mPositionHandle;
    public int mColorHandle;
    public int mMVPMatrixHandle;

    private boolean startMonitoring = false;

    private ArrayList<Explosion> explosions = new ArrayList<Explosion>();


    public static final String generatorVertexShader =
            "uniform mat4 uMVPMatrix;      " +
                    // A constant representing the combined model/view/projection matrix
                    "attribute vec4 vPosition;     " +
                    // Per-vertex position information we will pass in.
                    "attribute vec4 vColor;        " +
                    // Per-vertex color information we will pass in.
                    "varying vec3 v_Position;       " +
                    // This will be passed into the fragment shader.
                    "varying vec4 v_Color;          " +
                    // This will be passed into the fragment shader.
                    // The entry point for our vertex shader.
                    "void main()" +
                    "{" +
                    // Transform the vertex into eye space.
                    "    v_Position = vec3(vPosition);" +
                    // Pass through the color.
                    "    v_Color = vColor;" +
                    // gl_Position is a special variable used to store the final position.
                    // Multiply the vertex by the matrix to get the final point in normalized screen coordinates. +
                    "    gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    public static final String generatorFragmentShader =
            "precision mediump float;       // Set the default precision to medium. We don't need as high of a\n" +
                    "                               // precision in the fragment shader.\n" +
                    "uniform vec3 u_LightPos;       // The position of the light in eye space.\n" +
                    "varying vec3 v_Position;       // Interpolated position for this fragment.\n" +
                    "varying vec4 v_Color;          // This is the color from the vertex shader interpolated across the\n" +
                    "                               // triangle per fragment.\n" +

                    "uniform float lightShinning;" +
                    "uniform float lightDistance;" +

                    "// The entry point for our fragment shader.\n" +
                    "void main()\n" +
                    "{\n" +
                    "    // Will be used for attenuation.\n" +

                    "    // Add attenuation.\n" +
                    "    // Multiply the color by the diffuse illumination level to get final output color.\n" +
                    "    gl_FragColor = v_Color;" +
                    "}";


    final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;      " +
                    // A constant representing the combined model/view/projection matrix
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
                "vec3 Color;" +
                "};"+

                "uniform LightSource lights[1];"+

            "precision mediump float;       // Set the default precision to medium. We don't need as high of a\n" +
                    "                               // precision in the fragment shader.\n" +
                    "uniform vec3 u_LightPos;       // The position of the light in eye space.\n" +
                    "varying vec3 v_Position;       // Interpolated position for this fragment.\n" +
                    "uniform vec4 vColor;          // This is the color from the vertex shader interpolated across the\n" +
                    "                               // triangle per fragment.\n" +

                    "uniform float lightShinning;" +
                    "uniform float lightDistance;" +

                    "// The entry point for our fragment shader.\n" +
                    "void main()\n" +
                    "{\n" +
                    "    // Will be used for attenuation.\n" +
                    "    float distance = length(u_LightPos - v_Position);\n" +

                    "    // Add attenuation.\n" +
                    "   float diffuse = lightShinning * (1.0 / (1.0 + (0.00007 * distance * distance*lightDistance)));\n" +

                //"    float diffuse = lightShinning * (1.0 / (1.0 + (0.00007 * distance * distance*lightDistance)));\n" +
                    "    // Multiply the color by the diffuse illumination level to get final output color.\n" +
                    "    gl_FragColor = vColor*diffuse;" +
                    "}";


    private int mProgram;
    private int mGeneratorProgram;
    public int mCurrentProgram;


    //private String explosionColorStart;
    ///private String explosionColorEnd;

    private Stage currentStage;
    private Explosion currentExplosion;


    //public String shipColor = "#000000";
    private boolean isRunnig = false;


    public ArrayList<Explosion> getExplosions() {
        return explosions;
    }

    public static int createAndLinkProgram(final int vertexShaderHandle, final int fragmentShaderHandle, final String[] attributes) {
        int programHandle = GLES20.glCreateProgram();

        if (programHandle != 0) {
            // Bind the vertex shader to the program.
            GLES20.glAttachShader(programHandle, vertexShaderHandle);

            // Bind the fragment shader to the program.
            GLES20.glAttachShader(programHandle, fragmentShaderHandle);

            // Bind attributes
            if (attributes != null) {
                final int size = attributes.length;
                for (int i = 0; i < size; i++) {
                    GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
                }
            }

            // Link the two shaders together into a program.
            GLES20.glLinkProgram(programHandle);

            // Get the link status.
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

            // If the link failed, delete the program.
            if (linkStatus[0] == 0) {
                Log.e(TAG, "Error compiling program: " + GLES20.glGetProgramInfoLog(programHandle));
                GLES20.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }

        if (programHandle == 0) {
            throw new RuntimeException("Error creating program.");
        }

        return programHandle;
    }

    private void initGL20Shaders() {


        // prepare shaders and OpenGL program
        int vertexShader = GameView.loadShader(
                GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = GameView.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);
        mProgram = createAndLinkProgram(vertexShader, fragmentShader,
                new String[]{"vPosition"});


        int genVertexShader = GameView.loadShader(
                GLES20.GL_VERTEX_SHADER,
                generatorVertexShader);
        int genFragmentShader = GameView.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                generatorFragmentShader);

        mGeneratorProgram = createAndLinkProgram(genVertexShader, genFragmentShader,
                new String[]{"vPosition", "vColor"});


        mLightPosHandle = GLES20.glGetUniformLocation(mProgram, "u_LightPos");
        mLightDistanceHandle = GLES20.glGetUniformLocation(mProgram, "lightDistance");
        mLightShinningHandle = GLES20.glGetUniformLocation(mProgram, "lightShinning");

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");



    }

    public GameView(MainActivity context) {
        super(context);
        setEGLContextClientVersion(2);
        this.setRenderer(this);
        this.context = context;


    }

    public GameView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setEGLContextClientVersion(2);
        this.setRenderer(this);
        this.context = (MainActivity) context;


    }


    @Override
    public void onDrawFrame(GL10 gl) {

        long currentTime = System.currentTimeMillis();


        if (generator != null && isRunnig && mCurrentProgram != mGeneratorProgram) {
            mCurrentProgram = mGeneratorProgram;
            Log.d("XXX", "generator program setted ");
        } else if (generator == null && mCurrentProgram != mProgram) {
            mCurrentProgram = mProgram;
        }


        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glUseProgram(mCurrentProgram);

        Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);


        GLES20.glUniform3f(mLightPosHandle, mainSprite.getCenterX(), mainSprite.getCenterY(), 0.0f);
        GLES20.glUniform1f(mLightDistanceHandle, 0.1f);
        GLES20.glUniform1f(mLightShinningHandle, 1.0f);


        if (background != null)
            background.drawGl2(mMVPMatrix);


        if (generator != null && isRunnig)
            generator.drawGL20(mMVPMatrix);
        else if (maze != null && mainSprite != null && isRunnig) {

            maze.drawGL20(mMVPMatrix);


            mainSprite.drawGl2(mMVPMatrix);

            for (int i = 0; i < explosions.size(); i++) {
                explosions.get(i).drawGl2(mMVPMatrix);
            }
            if (currentExplosion != null)
                toggleColors(currentExplosion.getProgress());


        }
        update();


        long renderTime = System.currentTimeMillis() - currentTime;

        if (startMonitoring) {
            context.setMax(renderTime);
            context.setMin(renderTime);
            context.setCurrent(renderTime);
        }

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {


        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);

        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
        Matrix.orthoM(mProjectionMatrix, 0, 0, MainActivity.screenWidth, 0, MainActivity.screenHeight, -1, 1);

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {


        // Set the background clear color to black.
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Use culling to remove back faces.
        GLES20.glEnable(GLES20.GL_CULL_FACE);


        // Position the eye in front of the origin.
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = -0.5f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = -5.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);


        init(context);
        initGlStage();
        background.refreashColor();
        initGL20Shaders();


        startMonitoring = true;

    }


    /**
     * Utility method for compiling a OpenGL shader.
     * <p/>
     * <p><strong>Note:</strong> When developing shaders, use the checkGlError()
     * method to debug shader coding errors.</p>
     *
     * @param type       - Vertex or fragment shader type.
     * @param shaderCode - String containing the shader code.
     * @return - Returns an id for the shader.
     */
    public static int loadShader(int type, String shaderCode) {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     * <p/>
     * <pre>
     * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }


    public void setRunnig(boolean isRunnig) {
        this.isRunnig = isRunnig;

        if (!isRunnig && generator != null)
            context.clearStageState();


    }

    public void loadStageData(Stage stage) {


        currentStage = stage;


    }

    public void initGlStage() {
        if (currentStage == null)
            return;

        generator = null;
        background = null;
        Explosion.initBuffers();
        background = new Background(this, currentStage.colorBackground);

        maze = null;
        maze = new Maze(this, currentStage);

        resetDot();

    }

    public void initGenerator(int widht, int height) {

        generator = null;
        generator = new Generator(this, widht, height);

        context.drawerContent.addView(generator.getLayout().onCreateView(generator));
        context.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }


    private void init(MainActivity context) {


        screenWidth = this.getResources().getDisplayMetrics().widthPixels;
        screenHeight = this.getResources().getDisplayMetrics().heightPixels;


        dotSize = screenWidth / 20;


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
        Explosion e = new Explosion(this, mainSprite.centerX, mainSprite.centerY, System.currentTimeMillis(), mainSprite.spriteSpeed, (int) dotSize, currentStage.colorExplosionStart, currentStage.colorExplosionEnd);

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