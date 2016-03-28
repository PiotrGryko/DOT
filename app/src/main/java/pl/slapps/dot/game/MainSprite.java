package pl.slapps.dot.game;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import pl.slapps.dot.R;
import pl.slapps.dot.SurfaceRenderer;
import pl.slapps.dot.drawing.Quad;
import pl.slapps.dot.drawing.Sprite;
import pl.slapps.dot.drawing.Util;
import pl.slapps.dot.drawing.Wall;
import pl.slapps.dot.generator.builder.TileRoute;
import pl.slapps.dot.generator.builder.TileRouteFinish;
import pl.slapps.dot.generator.widget.RouteScoreCounter;
import pl.slapps.dot.model.Config;
import pl.slapps.dot.model.Route;


public class MainSprite extends Sprite {


    private String TAG = MainSprite.class.getName();
    private Maze fence;
    private Config config;
    public boolean prepareToDie;

    private Game game;
    public TileRoute lastChangeRoute;
    public TileRoute currentTile;
    private float lightDistance;
    private float lightShinning;
    public float spriteSpeed = 0;

    public boolean booster = true;


    static final int COORDS_PER_VERTEX = 3;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex


    float color[] = {0.0f, 0.0f, 0.0f, 1.0f};


    public void setPrepareToDie(boolean prepareToDie) {
        this.prepareToDie = prepareToDie;
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
        fence = view.maze;

        spriteSpeed = view.context.getResources().getDimension(R.dimen.speed);

        configure(config);


    }


    public void configure(Config config) {
        this.config = config;

        if (!config.settings.switchDotColor) {
            color = Util.parseColor(config.colors.colorShip);
        } else {
            color = Util.parseColor(config.colors.colorSwitchDotStart);

        }


        if (!config.settings.switchDotLightDistance)
            lightDistance = config.settings.dotLightDistance;
        else
            lightDistance = config.settings.dotLightDistanceStart;


        lightShinning = config.settings.dotLightShinning;

    }


    public void update() {
        super.update();


        //TileRoute collision = fence.checkRouteCollision(centerX, centerY, width / 2);
        TileRoute tmpCurrent = fence.getCurrentRouteObject(centerX, centerY);
        Wall.Type collision = tmpCurrent.checkCollision(centerX, centerY, width / 2);

        fence.checkCoinCollision(this);


        if (currentTile != tmpCurrent) {

            currentTile = tmpCurrent;

            float speedRatio = 1;
            if (booster) {
                if (tmpCurrent.type == Route.Type.FINISH || tmpCurrent.type == Route.Type.START) {

                    speedRatio=1;
                } else if (tmpCurrent.getDirection() == Route.Movement.LEFTRIGHT || tmpCurrent.getDirection() == Route.Movement.TOPBOTTOM) {
                    speedRatio=1.5f;
                } else {
                    speedRatio=0.5f;
                }
            } else {

                speedRatio = (float) currentTile.speedRatio;
            }

            if (x > 0)
                x = spriteSpeed * speedRatio;
            if (x < 0)
                x = -spriteSpeed * speedRatio;
            if (y > 0)
                y = spriteSpeed * speedRatio;
            if (y < 0)
                y = -spriteSpeed * speedRatio;


        }

        if (collision != null) {
            if (currentTile.getType() == Route.Type.FINISH) {
                game.explodeDot(false);
                game.destroyDot();
                if (game.getPreview())
                    game.resetDot();
                else
                    game.gameView.moveToNextLvl();
                //    game.resetDot();

            } else if (!prepareToDie) {
                game.crashDot(true);
                //    game.toggleColors();
            } else {
                game.explodeDot(true);

                if (new Random().nextFloat() > 0.99f)
                    game.context.showAdv();

                game.resetDot();
                //   game.toggleColors();
            }

        }

    }


    public void drawGl2(float[] mvpMatrix) {

        // get handle to vertex shader's vPosition member
        //mPositionHandle = GLES20.glGetAttribLocation(game.mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(game.mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                game.mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, bufferedVertex);


        // mColorHandle = GLES20.glGetUniformLocation(game.mProgram, "vColor");
        // Pass in the color information
        // Set color for drawing the triangle
        GLES20.glUniform4fv(game.mColorHandle, 1, color, 0);


        // get handle to shape's transformation matrix
        //  mMVPMatrixHandle = GLES20.glGetUniformLocation(game.mProgram, "uMVPMatrix");
        //  SurfaceRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and game transformation
        GLES20.glUniformMatrix4fv(game.mMVPMatrixHandle, 1, false, mvpMatrix, 0);
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
        GLES20.glDisableVertexAttribArray(game.mPositionHandle);

    }


}
