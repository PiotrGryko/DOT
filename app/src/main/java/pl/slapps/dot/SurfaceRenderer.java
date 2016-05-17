package pl.slapps.dot;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
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


    public boolean drawGenerator = false;
    private boolean startMonitoring = false;
    private boolean isRunning = false;
    private boolean isDrawing = true;

    private Thread gameThread;





    public Game getGame() {
        return game;
    }

    public boolean onBackPressed() {
        if (generator.getPreview()) {
            generator.stopPreview();
            Log.d("ggg", "stop preview " + isDrawing + " " + isRunning + " " + drawGenerator);
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

    public int loadTexture(Bitmap bitmap) {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0) {


            // Read in the resource

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }

    public void unloadTexture() {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
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

            Log.e(TAG, "vertex shader info log: " + GLES20.glGetShaderInfoLog(vertexShaderHandle));
            Log.e(TAG, "fragment shader info log: " + GLES20.glGetShaderInfoLog(fragmentShaderHandle));

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
        this.setEGLConfigChooser(false);
        this.setRenderer(this);
        this.context = context;
        this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setGameLoop();

    }

    public SurfaceRenderer(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setEGLContextClientVersion(2);
        this.setEGLConfigChooser(false);
        this.setRenderer(this);
        this.context = (MainActivity) context;
        this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setGameLoop();

    }

    private void setGameLoop() {

        if (gameThread != null)
            return;

        Log.e("ccc", "start");
        setDrawing(true);
        //  setRunnig(true);

        gameThread = new Thread() {

            //long LAST_TIME;
            //long INTERVAL = 10 * 1000000;

            @Override
            public void run() {
                super.run();
                while (isDrawing) {


                    //if (System.nanoTime() - LAST_TIME > INTERVAL) {
                    if (!ondraw) {
                        requestRender();

                        //          LAST_TIME = System.nanoTime();
                    }

                }
            }
        };

        gameThread.start();
    }

    public void stopGameThread() {

        if (gameThread == null)
            return;
        setDrawing(false);
        //   setRunnig(false);

        gameThread.interrupt();
        //gameThread.stop();
        gameThread = null;

        Log.e("ccc", "stop");
    }

    public void onPause() {
        super.onPause();
        stopGameThread();
        if (game != null)
            game.onPause();


    }

    public void onResume() {
        super.onResume();
        setGameLoop();
        if(game!=null)
            game.onResume();

    }


    //long lastUpdateTime = 0;
    //int framesCount = 0;
    private boolean ondraw = false;

    @Override
    public void onDrawFrame(GL10 gl) {


        Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        if (isDrawing) {
            ondraw = true;
            /// long tmp = System.currentTimeMillis();

            if (drawGenerator && generator != null) {
                generator.onDraw(mMVPMatrix);
            } else if (game != null)
                game.onDraw(mMVPMatrix);


            //    long diff = System.currentTimeMillis() - tmp;
            //    if (diff > 3)
            //        Log.e("ccc", "render time =" + diff);


            /*
            if (startMonitoring) {
                if (System.currentTimeMillis() - lastUpdateTime > 1000 && framesCount > 0) {
                    //  Log.d("ccc","frames "+framesCount);

                    context.getActivityControls().setMax(framesCount);
                    lastUpdateTime = System.currentTimeMillis();


                    framesCount = 0;
                    // Log.d("ccc","monitor time ="+(System.nanoTime()-tmp)/1000000);

                } else {
                    framesCount += 1;
                }


            }
            */
            ondraw = false;
        }

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
/*
        Log.d("GLTEST", "GL_RENDERER = " + gl.glGetString(GL10.GL_RENDERER));
        Log.d("GLTEST", "GL_VENDOR = " + gl.glGetString(GL10.GL_VENDOR));
        Log.d("GLTEST", "GL_VERSION = " + gl.glGetString(GL10.GL_VERSION));
        Log.i("GLTEST", "GL_EXTENSIONS = " + gl.glGetString(GL10.GL_EXTENSIONS));
*/
        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);


        Matrix.orthoM(mProjectionMatrix, 0, 0, width, height, 0, -1, 1);

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {


        // Set the background clear color to black.
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        GLES20.glEnable (GLES20.GL_BLEND);
        GLES20.glBlendFunc (GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

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


        game.initGameShaders();
        generator.initGeneratorShaders();

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

        checkGlError("glCompileShader");

        return shader;
    }

    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     * <p/>
     * <pre>
     * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     * <p/>
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

    public void setDrawing(boolean isDrawing) {
        this.isDrawing = isDrawing;
    }

    public void setInteracting(boolean interacting) {
        this.isRunning = interacting;
    }

    public void setRunnig(boolean isRunnig) {
        this.isRunning = isRunnig;

        if (!isRunnig) {
            context.getActivityControls().resetLogs();
        }
        if (!isRunnig && drawGenerator) {
            //    context.drawerContent.removeAllViews();
            context.clearStageState();
        }

    }

    public void loadStageData(Stage stage) {

        currentStage = stage;
        game.initStage(stage);

    }


    public void initGenerator() {

        drawGenerator = true;

        generator.initMenu();
        //generator.getLayout().onCreateView();
        // context.drawerContent.addView(generator.getLayout().onCreateView());
        // context.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        generator.reset();

        //    context.drawer.openDrawer(context.drawerContent);
    }


    public void init() {


        screenWidth = this.getResources().getDisplayMetrics().widthPixels;
        screenHeight = this.getResources().getDisplayMetrics().heightPixels;

        game = new Game(this);
        generator = new Generator(this, 9, 15);


    }


    public void moveToNextLvl() {

        MainActivity.sendAction(SoundsService.ACTION_FINISH, null);
        //SoundsService.getSoundsManager().playFinishSound();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                context.moveToNextStage();
            }
        }, 1000);


    }


    // touch events

    public boolean onTouchEvent(MotionEvent event) {


        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP: {


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