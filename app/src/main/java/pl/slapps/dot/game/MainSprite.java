package pl.slapps.dot.game;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import pl.slapps.dot.R;
import pl.slapps.dot.SurfaceRenderer;
import pl.slapps.dot.drawing.Sprite;
import pl.slapps.dot.drawing.Util;
import pl.slapps.dot.generator.TileRoute;
import pl.slapps.dot.generator.TileRouteFinish;
import pl.slapps.dot.model.Config;


public class MainSprite extends Sprite {

    private String TAG = MainSprite.class.getName();
    private Maze fence;


    private float angle;


    public boolean prepareToDie;

    private Game game;

    public TileRoute lastChangeRoute;
    public TileRoute currentTile;

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

    public void setRotation(float angle) {
        this.angle = angle;
    }

    public boolean isMoving() {
        if (x != 0 || y != 0) {
            return true;
        } else
            return false;
    }

    public MainSprite(Game view, float centerX, float centerY, int width,
                      int height, Config config) {

        super(centerX, centerY, width, height,true);
        this.game = view;
        fence = view.getMaze();


        spriteSpeed = view.context.getResources().getDimension(R.dimen.speed);
        color = Util.parseColor(config.colors.colorShip);





    }


    public void update() {
        super.update();
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

        if (tmpCurrent != null && currentTile != tmpCurrent) {
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
            if (collision instanceof TileRouteFinish) {
                game.explodeDot(false);
                game.destroyDot();
            } else if (!prepareToDie) {
                game.crashDot(true);
                //    game.toggleColors();
            } else {
                game.explodeDot(true);

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
        GLES20.glUniform1f(game.mDotLightDistanceHandle, 0.3f);
        GLES20.glUniform1f(game.mDotLightShinningHandle, 1.0f);
        GLES20.glUniform4fv(game.mDotLightColorHandle, 1, color, 0);



        // Draw the square
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, indices.length,
                GLES20.GL_UNSIGNED_SHORT, bufferedIndices);





        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);


    }


}
