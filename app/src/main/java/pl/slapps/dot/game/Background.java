package pl.slapps.dot.game;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import pl.slapps.dot.MainActivity;
import pl.slapps.dot.R;
import pl.slapps.dot.SurfaceRenderer;
import pl.slapps.dot.drawing.Sprite;
import pl.slapps.dot.drawing.Util;
import pl.slapps.dot.model.Config;

public class Background extends Sprite {

    private final static String TAG = Background.class.getName();


    private Game view;


    /**
     * Size of the normal data in elements.
     */

    static final int COORDS_PER_VERTEX = 3;
    private Config config;
    private boolean switchColors;
    private FloatBuffer textureBuffer;
    //public FloatBuffer colorBuffer;

    public int mTextureDataHandle;
    public String currentTexture;
    private boolean shouldInit = false;
    private boolean useTexture = false;
    //private boolean initialized = false;


    float color[] = {0.0f, 0.0f, 0.0f, 1.0f

    };


    float texture[] = {0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            1.0f, 0.0f};


    public void configure(Config config) {
        this.config = config;

        //if (!config.settings.switchBackgroundColors) {
        color = Util.parseColor(config.colors.colorBackground);
/*

        float[] tmp = new float[0];
        tmp = Util.summArrays(tmp, color);
        tmp = Util.summArrays(tmp, color);
        tmp = Util.summArrays(tmp, color);
        tmp = Util.summArrays(tmp, color);

        color = tmp;

        if (initialized) {
            colorBuffer.position(0);
            colorBuffer.put(color);
            colorBuffer.position(0);
        }
*/

        String filename = config.settings.backgroundFile;
        if (filename == null || filename.trim().equals("")) {
            useTexture = false;


            if (mTextureDataHandle != 0)
                view.gameView.unloadTexture();
        } else {
            useTexture = true;
            initTexture(filename);

        }
    }


    public void initTexture(String filename) {

        if (currentTexture != null && currentTexture.equals(filename))
            return;
        currentTexture = filename;
        shouldInit = true;

    }


    public void onPause() {
        view.gameView.unloadTexture();
        mTextureDataHandle = 0;
    }

    public void onResume() {
        shouldInit = true;
    }

    private int loadTexture(Context context, String filename) {

        currentTexture = filename;

        if (!filename.startsWith("backgrounds"))
            filename = "backgrounds/" + filename;

        if (mTextureDataHandle != 0)
            view.gameView.unloadTexture();

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = true;   // No pre-scaling
        InputStream fin = null;
        try {
            fin = context.getAssets().open(filename);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        final Bitmap bitmap = BitmapFactory.decodeStream(fin);
        float widht = bitmap.getWidth();
        float height = bitmap.getHeight();

        float targetWidht = view.gameView.getWidth() * 1.25f;
        float targetHeight = view.gameView.getHeight() * 1.25f;


        float ratioWidth = targetWidht / widht;
        float ratioHeight = targetHeight / height;

        float finalRatio = ratioWidth;
        if (ratioHeight > ratioWidth)
            finalRatio = ratioHeight;

        float finalWidth = finalRatio * widht;
        float finalHeight = finalRatio * height;

        // Bitmap b = Bitmap.createScaledBitmap(bitmap, (int) finalWidth, (int) finalHeight, true);


        setup(view.gameView.getWidth() / 2, view.gameView.getHeight() / 2, finalWidth, finalHeight);
        initBuffers();

        textureBuffer.position(0);
        textureBuffer.put(texture);
        textureBuffer.position(0);


        Log.e("GGG", "texture loaded targetheight=" + targetHeight +
                " targetnwidht=" + targetWidht +
                " finalwidth=" + finalWidth +
                " finalheight=" + finalHeight +
                " inputwidth=" + widht +
                " inputheight=" + height);

        int result = view.gameView.loadTexture(bitmap);
        bitmap.recycle();
        return result;

    }

    private float lastPositionX = 0;
    private float lastPositionY = 0;

    public void setPosition(float x, float y) {

        if (lastPositionX == x && lastPositionY == y)
            return;
        // int signX = x>MainActivity.screenWidth/2? 1:-1;
        // int signy = y>MainActivity.screenHeight/2? 1:-1;

        float surfaceW = view.gameView.getWidth();
        float surfaceH = view.gameView.getHeight();

        float diffX = x - surfaceW / 2;
        float diffY = y - surfaceH / 2;

        ///ratio....


        float ratioX = ((surfaceW - width) / 2) / (width / 2);
        float ratioY = ((surfaceH - height) / 2) / (height / 2);


        float newCenterX = surfaceW / 2 - diffX * ratioX;
        float newCenterY = surfaceH / 2 - diffY * ratioY;


        quad.update(centerX - newCenterX, centerY - newCenterY);

        quad.initSharedVerticles();
        this.bufferedVertex.position(0);
        this.bufferedVertex.put(quad.vertices);
        this.bufferedVertex.position(0);
        centerY = newCenterY;
        centerX = newCenterX;

    }


    public Background(Game view, Config config) {
        super(MainActivity.screenWidth / 2, MainActivity.screenHeight / 2, MainActivity.screenWidth, MainActivity.screenHeight, true);
        this.view = view;
        this.config = config;
        configure(config);


        ByteBuffer byteBuf = ByteBuffer.allocateDirect(texture.length * 4);
        byteBuf.order(ByteOrder.nativeOrder());
        textureBuffer = byteBuf.asFloatBuffer();
        textureBuffer.put(texture);
        textureBuffer.position(0);


        ByteBuffer cb = ByteBuffer.allocateDirect(color.length * 4);
        cb.order(ByteOrder.nativeOrder());
      /*
        colorBuffer = cb.asFloatBuffer();
        colorBuffer.put(color);
        colorBuffer.position(0);

        initialized = true;
*/
    }


    public void setMove(float x, float y) {
        moveX += x;
        moveY += y;
    }


    public void drawGl2(float[] mvpMatrix) {


        // get handle to fragment shader's vColor member
        // Pass in the color information
        // Set color for drawing the triangle
        //  GLES20.glUniform4fv(view.mColorHandle, 1, color, 0);


        // get handle to shape's transformation matrix
        //SurfaceRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and generator transformation
        //GLES20.glUniformMatrix4fv(view.mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        //SurfaceRenderer.checkGlError("glUniformMatrix4fv");


        if (useTexture) {
            GLES20.glUseProgram(view.mTextureProgram);
            GLES20.glUniformMatrix4fv(view.mTextureMVPMatrixHandle, 1, false, mvpMatrix, 0);


            // get handle to vertex shader's vPosition member
            // Enable a handle to the triangle vertices
            GLES20.glEnableVertexAttribArray(view.mTexturePositionHandle);
            // Prepare the triangle coordinate data
            GLES20.glVertexAttribPointer(
                    view.mTexturePositionHandle, COORDS_PER_VERTEX,
                    GLES20.GL_FLOAT, false,
                    0, bufferedVertex);


            if (shouldInit) {
                shouldInit = false;
                mTextureDataHandle = loadTexture(view.gameView.context, currentTexture);
            }


            //GLES20.glBindTexture();

            view.mTextureUniformHandle = GLES20.glGetUniformLocation(view.mTextureProgram, "u_Texture");
            view.mTextureCoordinateHandle = GLES20.glGetAttribLocation(view.mTextureProgram, "a_TexCoordinate");


            GLES20.glEnableVertexAttribArray(view.mTextureCoordinateHandle);
            // Prepare the triangle coordinate data
            GLES20.glVertexAttribPointer(
                    view.mTextureCoordinateHandle, 2,
                    GLES20.GL_FLOAT, false,
                    0, textureBuffer);
            // Set the active texture unit to texture unit 0.
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

            // Bind the texture to this unit.
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);

            // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
            GLES20.glUniform1i(view.mTextureUniformHandle, 0);
// Draw the square
            GLES20.glDrawElements(
                    GLES20.GL_TRIANGLES, indices.length,
                    GLES20.GL_UNSIGNED_SHORT, bufferedIndices);
            //GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

            // Disable vertex array
            GLES20.glDisable(GLES20.GL_TEXTURE_2D);
            GLES20.glDisableVertexAttribArray(view.mTexturePositionHandle);
            GLES20.glDisableVertexAttribArray(view.mTextureCoordinateHandle);


        }


        GLES20.glUseProgram(view.mProgram);
        GLES20.glUniformMatrix4fv(view.mMVPMatrixHandle, 1, false, mvpMatrix, 0);


        // get handle to vertex shader's vPosition member
        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(view.mPositionHandle);
        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                view.mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                0, bufferedVertex);

        //GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);


        //GLES20.glEnableVertexAttribArray(view.mColorHandle);

// Prepare the triangle coordinate data
        //GLES20.glVertexAttribPointer(view.mColorHandle, 4, GLES20.GL_FLOAT, false, 0, colorBuffer);
        // GLES20.glDisableVertexAttribArray(view.mColorHandle);
           GLES20.glUniform4fv(view.mColorHandle, 1, color, 0);
        // Draw the square
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, indices.length,
                GLES20.GL_UNSIGNED_SHORT, bufferedIndices);


        //GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

        // Disable vertex array
        //  GLES20.glDisable(GLES20.GL_TEXTURE_2D);
        //  GLES20.glDisableVertexAttribArray(view.mPositionHandle);
        //  GLES20.glDisableVertexAttribArray(view.mTextureCoordinateHandle);


    }


}
