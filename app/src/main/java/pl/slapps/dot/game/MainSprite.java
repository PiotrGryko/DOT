package pl.slapps.dot.game;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import pl.slapps.dot.R;
import pl.slapps.dot.SurfaceRenderer;
import pl.slapps.dot.drawing.Sprite;
import pl.slapps.dot.drawing.Util;
import pl.slapps.dot.generator.builder.TileRoute;
import pl.slapps.dot.generator.builder.TileRouteFinish;
import pl.slapps.dot.generator.widget.RouteScoreCounter;
import pl.slapps.dot.model.Config;
import pl.slapps.dot.model.Route;


public class MainSprite extends Sprite {


    public interface OnProgressListener {
        public void onProgressChanged(float value);
    }


    private OnProgressListener listener;

    public void setOnProgressListener(OnProgressListener listener) {
        this.listener = listener;
    }


    private String TAG = MainSprite.class.getName();
    private Maze fence;


    private Config config;


    public boolean prepareToDie;

    private Game game;

    public TileRoute lastChangeRoute;
    public TileRoute currentTile;
    private RouteScoreCounter scoreCounter;

    private float movingProgres = 0;
    private float totalScore =0;

    private float lightDistance;
    private float lightShinning;


    public float spriteSpeed = 0;
    private FloatBuffer lPos = ByteBuffer.allocateDirect(4 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();


    static final int COORDS_PER_VERTEX = 3;


    private int mPositionHandle;
    private int mColorHandle;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    private int mMVPMatrixHandle;


    float color[] = {0.0f, 0.0f, 0.0f, 1.0f};


    public void setPrepareToDie(boolean prepareToDie) {
        this.prepareToDie = prepareToDie;
    }


    public void setInitialProgress(float value) {
        movingProgres += value;
    }

    public boolean isMoving() {
        if (x != 0 || y != 0) {
            return true;
        } else
            return false;
    }

    public MainSprite(Game view, float centerX, float centerY, int width,
                      int height, Config config) {

        super(centerX, centerY, width, height, true);
        this.game = view;
        fence = view.getMaze();

        spriteSpeed = view.context.getResources().getDimension(R.dimen.speed);

        configure(config);


    }

    public void onProgressChanged(float value) {
        if (config.settings.switchDotLightDistance) {

            lightDistance = config.settings.dotLightDistanceStart + (config.settings.dotLightDistanceEnd - config.settings.dotLightDistanceStart) * value;
        }

        if (config.settings.switchDotColor) {
            String finalColor = Util.calculateColorsSwitch(config.colors.colorSwitchDotStart, config.colors.colorSwitchDotEnd, value);
            color = Util.parseColor(finalColor);
        }

    }

    public void configure(Config config) {
        this.config = config;

        if (!config.settings.switchDotColor) {
            color = Util.parseColor(config.colors.colorShip);
        } else {
            color = Util.parseColor(config.colors.colorSwitchDotStart);

        }

        Log.d(TAG,"configure dot");

        if (!config.settings.switchDotLightDistance)
            lightDistance = config.settings.dotLightDistance;
        else
            lightDistance = config.settings.dotLightDistanceStart;



        lightShinning = config.settings.dotLightShinning;

    }


    public void update() {
        super.update();

        //if(moveX!=0 || moveY!=0)

        movingProgres += Math.abs(this.x) + Math.abs(this.y);
        if (movingProgres != 0 && movingProgres < fence.getMazeLength()) {
            if (listener != null) {
                listener.onProgressChanged(movingProgres / fence.getMazeLength());


            }
            //Log.d("aaa", "moving progress " + movingProgres);
        }
        //fence.setMove(-x, -y);
        //background.setMove(-x, -y);

        this.bufferedVertex.position(0);
        this.bufferedVertex.put(this.quad.vertices);
        this.bufferedVertex.position(0);


        lPos.position(0);
        lPos.put(new float[]{this.getCenterX(), this.getCenterY(), 100.0f, 1.0f});
        lPos.position(0);


        TileRoute collision = fence.checkRouteCollision(centerX, centerY, width / 2);
        TileRoute tmpCurrent = fence.getCurrentRouteObject(centerX, centerY);

        fence.checkCoinCollision(this);


        if (tmpCurrent != null && currentTile != tmpCurrent) {

            if (scoreCounter != null) {
                scoreCounter.setExitCoords(centerX, centerY);
                totalScore += scoreCounter.estimateScore();
                Log.d("aaa", "calculate score " + totalScore);
            }

            if (scoreCounter == null)
                scoreCounter = new RouteScoreCounter(tmpCurrent);
            else
                scoreCounter.setRoute(tmpCurrent);

            scoreCounter.setEnterCoords(centerX, centerY);

            if (tmpCurrent.getType() == Route.Type.FINISH) {
                scoreCounter.setExitCoords(centerX, centerY);
                totalScore += scoreCounter.estimateScore();

                Log.d("aaa", "calculate score " + totalScore);
            }


            currentTile = tmpCurrent;


            if (x > 0)
                x = spriteSpeed * (float) currentTile.speedRatio;

            if (x < 0)
                x = -spriteSpeed * (float) currentTile.speedRatio;

            if (y > 0)
                y = spriteSpeed * (float) currentTile.speedRatio;
            if (y < 0)
                y = -spriteSpeed * (float) currentTile.speedRatio;


        }

        if (collision != null) {
            if (collision instanceof TileRouteFinish && currentTile.getType() == Route.Type.FINISH) {
                game.explodeDot(false);
                game.destroyDot();
                if (game.getPreview())
                    game.resetDot();
                else
                    game.gameView.moveToNextLvl((totalScore/fence.routes.size())*100);
                //    game.resetDot();

            } else if (!prepareToDie) {
                game.crashDot(true);
                //    game.toggleColors();
            } else {
                game.explodeDot(true);

                if(new Random().nextFloat()>0.9f)
                    game.context.showAdv();

                game.resetDot();
                //   game.toggleColors();
            }

        }

    }

    public void drawGl2(float[] mvpMatrix) {


        // Add program to OpenGL environment
        GLES20.glUseProgram(game.mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(game.mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, bufferedVertex);


        mColorHandle = GLES20.glGetUniformLocation(game.mProgram, "vColor");
        // Pass in the color information
        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);


        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(game.mProgram, "uMVPMatrix");
        SurfaceRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and game transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        SurfaceRenderer.checkGlError("glUniformMatrix4fv");


        GLES20.glUniform3f(game.mDotLightPosHandle, getCenterX(), getCenterY(), 0.0f);
        GLES20.glUniform1f(game.mDotLightDistanceHandle, lightDistance);
        GLES20.glUniform1f(game.mDotLightShinningHandle, lightShinning);
        GLES20.glUniform4fv(game.mDotLightColorHandle, 1, color, 0);


        // Draw the square
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, indices.length,
                GLES20.GL_UNSIGNED_SHORT, bufferedIndices);


        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);


    }


}
