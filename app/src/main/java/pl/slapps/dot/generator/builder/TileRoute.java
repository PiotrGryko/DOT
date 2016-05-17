package pl.slapps.dot.generator.builder;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

import pl.slapps.dot.generator.Generator;
import pl.slapps.dot.model.Route;

/**
 * Created by piotr on 13.10.15.
 */
public class TileRoute extends Tile {


    public Route.Movement next;
    public String backgroundColor;
    public String sound;
    public double speedRatio = 1;

    private boolean currentTile = false;
    private boolean currentHeadTile = false;
    public boolean drawCoin = false;
    public boolean visited = false;

    private final float scaleTime = 1f * 1000;
    private long scaleStartTime = 0;
    private float[] mModelMatrix = new float[16];
    private float[] mvpLocalMatrix = new float[16];
    private float scale = 1;
    private boolean isIncreasing = false;


    public void setCurrentTile(boolean flag) {
        currentTile = flag;
    }

    public void setCurrentHeadTile(boolean flag) {
        currentHeadTile = flag;
    }

    //public SurfaceRenderer generator;


    public TileRoute(float screenWidth, float screenHeight, float widthBlocksCount, float heightBlocksCount, int widthNumber, int heightNumber, Route.Direction from, Route.Direction to, Route.Type t, Generator generator) {
        super(screenWidth, screenHeight, widthBlocksCount, heightBlocksCount, widthNumber, heightNumber, from, to, t, generator);

    }


    public TileRoute(float screenWidth, float screenHeight, float widthBlocksCount, float heightBlocksCount, Route route, Generator generator) {
        super(screenWidth, screenHeight, widthBlocksCount, heightBlocksCount, route, generator);
        this.next = Route.Movement.valueOf(route.next.name());
        backgroundColor = route.backgroundColor;
        sound = route.sound;
        speedRatio = route.speedRatio;
        drawCoin = route.drawCoin;


    }

    public TileRoute(float screenWidth, float screenHeight, float widthBlocksCount, float heightBlocksCount, Route route) {
        super(screenWidth, screenHeight, widthBlocksCount, heightBlocksCount, route);
        this.next = Route.Movement.valueOf(route.next.name());
        backgroundColor = route.backgroundColor;
        sound = route.sound;
        speedRatio = route.speedRatio;
        drawCoin = route.drawCoin;


    }


    public void configRoute(TileRoute route) {
        this.sound = route.sound;
        this.speedRatio = route.speedRatio;
    }


    public void setScale(float scale) {
        this.scale = scale;
    }

    private void updateScale() {

        long currentTime = System.currentTimeMillis();

        float diff = 0;
        if ((scale >= 1 || scale <= 0)) {
            isIncreasing = !isIncreasing;
            scaleStartTime = currentTime;
            diff = 5;
        } else
            diff = currentTime - scaleStartTime;
        //if (diff == 0|| scaleStartTime==0)
        //    return;

        scale = diff / scaleTime;
        if (isIncreasing) {

            scale = 1 - scale;
        }


    }


    public void drawGL20(float[] mvpMatrix) {


        if (!isInitialized)
            return;


        if (currentTile || currentHeadTile) {
            Matrix.setIdentityM(mModelMatrix, 0);


            long time = SystemClock.uptimeMillis() % 10000L;
            float angleInDegrees = (360.0f / 5000.0f) * ((int) time);
            // Draw the triangle facing straight on.
            Matrix.translateM(mModelMatrix, 0, centerX, centerY, 0);

            if (currentHeadTile) {

                updateScale();
                Matrix.scaleM(mModelMatrix, 0, scale, scale, 1);
            } else {
                Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 0.0f, 0.0f, 1.0f);


            }
            Matrix.translateM(mModelMatrix, 0, -centerX, -centerY, 0);

            Matrix.multiplyMM(mvpLocalMatrix, 0, generator.view.mViewMatrix, 0, mModelMatrix, 0);
            // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
            // (which now contains model * view * projection).
            Matrix.multiplyMM(mvpLocalMatrix, 0, generator.view.mProjectionMatrix, 0, mvpLocalMatrix, 0);
            GLES20.glUniformMatrix4fv(generator.mMVPMatrixHandle, 1, false, mvpLocalMatrix, 0);

            if (backgroundPartOne != null)
                backgroundPartOne.drawGl2(mvpLocalMatrix);
            if (backgroundPartTwo != null)
                backgroundPartTwo.drawGl2(mvpLocalMatrix);


            for (int i = 0; i < walls.size(); i++) {
                walls.get(i).drawGl2(mvpLocalMatrix);
            }

            Matrix.setIdentityM(generator.view.mModelMatrix, 0);


        } else {
            GLES20.glUniformMatrix4fv(generator.mMVPMatrixHandle, 1, false, mvpMatrix, 0);


            super.drawGL20(mvpMatrix);
        }


    }


}
