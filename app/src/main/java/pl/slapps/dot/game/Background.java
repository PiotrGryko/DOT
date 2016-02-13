package pl.slapps.dot.game;


import android.graphics.Color;
import android.opengl.GLES20;
import android.util.Log;

import pl.slapps.dot.MainActivity;
import pl.slapps.dot.SurfaceRenderer;
import pl.slapps.dot.drawing.Sprite;

public class Background extends Sprite {

    private String TAG = Background.class.getName();







    private int textures[] = new int[1];


    private Game view;
    //private Bitmap bitmap;

    private float moveX = 0;
    private float moveY = 0;
    private float a;
    private float r;
    private float g;
    private float b;
    public String backgroundcolor;

    /** Size of the normal data in elements. */

    static final int COORDS_PER_VERTEX = 3;






    float color[] = { 0.0f, 0.0f, 0.0f, 1.0f

    };


    public void setColor(String color) {

        try {
            int intColor = Color.parseColor(color);

            backgroundcolor = color;
            a = (float) Color.alpha(intColor) / 255;
            r = (float) Color.red(intColor) / 255;
            g = (float) Color.green(intColor) / 255;
            b = (float) Color.blue(intColor) / 255;
            Log.d("XXX","color setted "+color);
            refreashColor();
        } catch (Throwable t) {
            Log.d(TAG, "background color  null " + color);
        }
    }

    public Background(Game view, String colorString) {
        super(MainActivity.screenWidth / 2, MainActivity.screenHeight / 2, MainActivity.screenWidth, MainActivity.screenHeight);
        this.view = view;

        setColor(colorString);



        //GLES20.glClearColor(r, g, b, 0);



    }

    public void refreashColor()
    {
        //GLES20.glClearColor(r, g, b, 0.0f);
        //color = new float[]{ r,g, b, 0.0f };
        color = new float[]{
                // Front face (red)

                r, g, b, a,


             //   1.0f, 0.0f, 0.0f, 1.0f,
             //   1.0f, 0.0f, 0.0f, 1.0f,



        };


        //Log.d("XXX","background color refreashed "+r +" "+g +" "+b );


    }


    public void setMove(float x, float y) {
        moveX += x;
        moveY += y;
    }




    public void drawGl2(float[] mvpMatrix)
    {


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
