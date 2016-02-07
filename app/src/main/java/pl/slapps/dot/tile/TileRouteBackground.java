package pl.slapps.dot.tile;

import android.graphics.Color;
import android.opengl.GLES20;

import javax.microedition.khronos.opengles.GL10;

import pl.slapps.dot.game.GameView;
import pl.slapps.dot.game.Sprite;

/**
 * Created by piotr on 20.10.15.
 */
public class TileRouteBackground extends Sprite {

    private String TAG = TileRouteBackground.class.getName();


    private float angle;
    public float r = 0.0f;
    public float g = 0.0f;
    public float b = 0.0f;
    public float a = 0.0f;

    public boolean prepareToDie;

    GameView view;


    static final int COORDS_PER_VERTEX = 2;




    private int mPositionHandle;
    private int mColorHandle;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    private int mMVPMatrixHandle;

    float color[] = { 0.0f, 0.0f, 0.0f, 1.0f };


    public void setColor(String colorString) {
        if (colorString == null)
            colorString = "#B8B8B8";
        int intColor = Color.parseColor(colorString);
        a = (float) Color.alpha(intColor) / 255;
        r = (float) Color.red(intColor) / 255;
        g = (float) Color.green(intColor) / 255;
        b = (float) Color.blue(intColor) / 255;


        color = new float[]{ r,g,b,a };

    }


    public TileRouteBackground(float centerX, float centerY, float width,
                               float height, String color, GameView view) {

        super(centerX, centerY, width, height);
        this.view=view;
        setColor(color);


    }


    public void update() {
        super.update();
    }

    public void draw(GL10 gl) {


        gl.glLoadIdentity();



        gl.glColor4f(r, g, b, a);
        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, bufferedVertex);
        gl.glDrawElements(GL10.GL_TRIANGLES, indices.length,
                GL10.GL_UNSIGNED_SHORT, bufferedIndices);


        //gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);


    }


    public void drawGl2(float[] mvpMatrix)
    {


        // Add program to OpenGL environment
        GLES20.glUseProgram(view.mCurrentProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(view.mCurrentProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, bufferedVertex);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(view.mCurrentProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(view.mCurrentProgram, "uMVPMatrix");
        GameView.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        GameView.checkGlError("glUniformMatrix4fv");

        // Draw the square
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, indices.length,
                GLES20.GL_UNSIGNED_SHORT, bufferedIndices);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);




    }


}
