package pl.slapps.dot;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import pl.slapps.dot.game.Game;
import pl.slapps.dot.generator.Generator;
import pl.slapps.dot.model.Stage;


public class SurfaceRenderer extends GLSurfaceView implements GLSurfaceView.Renderer {

    private static String TAG = SurfaceRenderer.class.getName();


    public MainActivity context;
    public int screenWidth;
    public int screenHeight;
    private Stage currentStage;

    private Generator generator;
    private Game game;

    private Handler handler = new Handler();


    public final float[] mProjectionMatrix = new float[16];
    public final float[] mViewMatrix = new float[16];
    public final float[] mMVPMatrix = new float[16];
    public final float[] mModelMatrix = new float[16];


    //private String explosionColorStart;
    ///private String explosionColorEnd;


    //public String shipColor = "#000000";


    private boolean drawGenerator = false;
    private boolean startMonitoring = false;
    private boolean isRunning = false;
    private boolean isDrawing = true;
    public boolean isInitialized = false;


    public Game getGame() {
        return game;
    }

    public boolean onBackPressed() {
        if (generator.getPreview()) {
            generator.stopPreview();
            return false;
        }
        generator.getPathPopup().dissmissControls();
        generator.getPathPopup().dissmissPath();
        generator.getPathPopup().dissmissSounds();
        generator.getPathPopup().dissmissColours();
        generator.getPathPopup().dissmissLights();
        generator.getPathPopup().dissmissGrod();

        return true;
    }

    public int createAndLinkProgram(final int vertexShaderHandle, final int fragmentShaderHandle, final String[] attributes) {
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


    public SurfaceRenderer(MainActivity context) {
        super(context);
        setEGLContextClientVersion(2);
        this.setRenderer(this);
        this.context = context;



    }

    public SurfaceRenderer(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setEGLContextClientVersion(2);
        this.setRenderer(this);
        this.context = (MainActivity) context;



    }


    @Override
    public void onDrawFrame(GL10 gl) {

        long currentTime = System.currentTimeMillis();

        Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        if (isDrawing) {
            if (drawGenerator && generator != null)
                generator.onDraw(mMVPMatrix);
            else if (game != null)
                game.onDraw(mMVPMatrix);
        }

        long renderTime = System.currentTimeMillis() - currentTime;

        if (startMonitoring && isRunning) {
            context.getActivityControls().setMax(renderTime);
            context.getActivityControls().setMin(renderTime);
            //context.setCurrent(renderTime);
        }

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {


        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);


        Matrix.orthoM(mProjectionMatrix, 0, 0, width, height, 0, -1, 1);

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {


        // Set the background clear color to black.
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // Use culling to remove back faces.
        //GLES20.glEnable(GLES20.GL_CULL_FACE);


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

        // Set the generator matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // generator matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);


        Log.d("zzz", "init game elmenets ");

        game.initGameShaders();
        generator.initGeneratorShaders();

        if(!isInitialized) {
            game.initStage(currentStage);
            isInitialized = true;
        }

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

    public void setDrawing(boolean isDrawing)
    {
        this.isDrawing=isDrawing;
    }

    public void setInteracting(boolean interacting)
    {
        this.isRunning=interacting;
    }

    public void setRunnig(boolean isRunnig) {
        this.isRunning = isRunnig;
       /// this.isDrawing=isRunnig;

        if (!isRunnig) {
            context.getActivityControls().resetLogs();
        }
        if (!isRunnig && drawGenerator) {
            context.drawerContent.removeAllViews();

            context.clearStageState();


        }

    }

    public void loadStageData(Stage stage) {

        drawGenerator = false;
        currentStage = stage;
        game.initStage(stage);

    }


    public void initGenerator() {

        drawGenerator = true;
        context.drawerContent.addView(generator.getLayout().onCreateView());
        context.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        generator.reset();

        context.drawer.openDrawer(context.drawerContent);
        //context.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
    }


    public void init(MainActivity context) {


        screenWidth = this.getResources().getDisplayMetrics().widthPixels;
        screenHeight = this.getResources().getDisplayMetrics().heightPixels;

        game = new Game(context, this);
        generator = new Generator(this, 9, 15);


    }


    public void moveToNextLvl(final float points) {
        //resetDot();
        //maze.clearRoutes();
        context.getSoundsManager().playFinishSound();
        //isRunnig = false;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                context.moveToNextStage(Integer.toString((int)points)+"%");

            }
        }, 1000);


        //  onPause();

    }


    // touch events

    public boolean onTouchEvent(MotionEvent event) {


        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:{


                if (isRunning) {
                    if (drawGenerator && generator != null) {
                        return generator.onTouch(event);
                    } else if (game != null)
                        return game.onTouchEvent(event);
                }
                break;
            }


        }

        return true;

    }

}