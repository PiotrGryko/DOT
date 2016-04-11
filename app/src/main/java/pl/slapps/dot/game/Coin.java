package pl.slapps.dot.game;

import android.opengl.GLES20;
import android.util.Log;

import pl.slapps.dot.MainActivity;
import pl.slapps.dot.SurfaceRenderer;
import pl.slapps.dot.drawing.Sprite;
import pl.slapps.dot.drawing.Util;
import pl.slapps.dot.model.Config;

/**
 * Created by piotr on 03/03/16.
 */
public class Coin extends Sprite {



    private Game view;


    /**
     * Size of the normal data in elements.
     */

    static final int COORDS_PER_VERTEX = 3;


    private Config config;
    private float coinX;
    private float cointY;


    float color[] = {1.0f, 1.0f, 1.0f, 1.0f

    };

    public boolean checkColision(MainSprite mainSprite, Config config) {


        boolean flag = false;

        if (cointains(mainSprite.quad.topLeft)) {
            flag =  true;
        }
        else if (cointains(mainSprite.quad.topRight)) {
            flag =  true;
        }
        else if (cointains(mainSprite.quad.bottomLeft)) {
            flag =  true;
        }
        else if (cointains(mainSprite.quad.topRight)) {
            flag =  true;
        }

        if(flag)
        {
            mainSprite.booster=true;
            view.explosionManager.explode(coinX,cointY,mainSprite.spriteSpeed);
            mainSprite.startBooster();
            view.context.getSoundsManager().playCoinSound();
            //view.addExplosion(new ExplosionManager(view, mainSprite.centerX, mainSprite.centerY, System.currentTimeMillis(), mainSprite.spriteSpeed, (int)view.dotSize, config));
            //view.context.getSoundsManager().playCrashSound();
        }

        return flag;
    }


    public void configure(Config config) {
       /*
        Log.d(TAG, "configure background");
        this.config=config;
        Log.d(TAG,config.colors.colorBackground +"  "+config.colors.colorSwitchBackgroundStart);

        if (!config.settings.switchBackgroundColors) {
            color = Util.parseColor(config.colors.colorBackground);
        } else {
            switchColors = true;
            color = Util.parseColor(config.colors.colorSwitchBackgroundStart);

        }
    */
    }

    public Coin(Game view, float centerX, float centerY, float width, float height) {
        super(centerX, centerY, width, height, true);
        this.view = view;
        this.coinX=centerX;
        this.cointY=centerY;
        this.config = config;
        configure(config);


    }


    public void drawGl2(float[] mvpMatrix) {

        // Add program to OpenGL environment
        GLES20.glUseProgram(view.mProgram);


        // get handle to vertex shader's vPosition member
        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(view.mPositionHandle);
        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                view.mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                0, bufferedVertex);


        // get handle to fragment shader's vColor member
        // Pass in the color information
        // Set color for drawing the triangle
        GLES20.glUniform4fv(view.mColorHandle, 1, color, 0);


        // get handle to shape's transformation matrix
        SurfaceRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and generator transformation
        GLES20.glUniformMatrix4fv(view.mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        SurfaceRenderer.checkGlError("glUniformMatrix4fv");


        // Draw the square
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, indices.length,
                GLES20.GL_UNSIGNED_SHORT, bufferedIndices);

        //GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(view.mPositionHandle);


    }


}
