package pl.slapps.dot.game;

import android.opengl.GLES20;
import android.util.Log;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;



import javax.microedition.khronos.opengles.GL10;

/**
 * Created by piotr on 14.10.15.
 */
public class Wall {


    private float vert[];
    private FloatBuffer bufferedVertex;

    public String TAG = Wall.class.getName();

    public enum Type {
        LEFT, RIGHT, TOP, BOTTOM;
    }

    public Junction start;
    public Junction end;
    public Type type;


    private int mPositionHandle;
    private int mColorHandle;
    static final int COORDS_PER_VERTEX = 3;

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    private int mMVPMatrixHandle;
    private GameView view;
    private final FloatBuffer mWallColor;



    public float color[] = { 0.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 0.0f, 1.0f,};

    public static float[] concatenate(float[] a, float[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        float[] c = (float[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

    public Wall(Junction start, Junction end, Type type,GameView view) {
        this.start = start;
        this.end = end;
        this.type = type;
        this.view=view;

        if(view==null)
            Log.d("XXX","wall constructor view is null "+type.name());


        float[] tab = new float[2 * 3];
        //float[] tabRight = new float[cornersRight.size() * 3];

        tab = concatenate(tab, start.getCoords());
        tab = concatenate(tab, end.getCoords());


        vert = tab;

        ByteBuffer bytes = ByteBuffer.allocateDirect(vert.length * 4);
        bytes.order(ByteOrder.nativeOrder());

        bufferedVertex = bytes.asFloatBuffer();
        bufferedVertex.put(vert);
        bufferedVertex.position(0);


        mWallColor = ByteBuffer.allocateDirect(color.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mWallColor.put(color).position(0);


    }

    public float[] getVert()
    {
        return vert;
    }


    public void draw(GL10 gl) {


        gl.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);


        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, bufferedVertex);
        gl.glDrawArrays(GL10.GL_LINES, 0, this.vert.length / 3);


    }




    public void drawGl2(float[] mvpMatrix)
    {

        if(view==null)
            Log.d("XXX","view is null");


        //Log.d("XXX","wall view: "+view);
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
        mColorHandle = GLES20.glGetAttribLocation(view.mCurrentProgram, "vColor");
        GLES20.glEnableVertexAttribArray(mColorHandle);
        // Pass in the color information
        GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false,
                0, mWallColor);


        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(view.mCurrentProgram, "uMVPMatrix");
        GameView.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        GameView.checkGlError("glUniformMatrix4fv");
        GLES20.glDrawArrays(GL10.GL_LINES, 0, this.vert.length / 3);

        // Draw the square
        // GLES20.glDrawElements(
        //         GLES20.GL_TRIANGLES, indices.length,
        //        GLES20.GL_UNSIGNED_SHORT, bufferedIndices);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);




    }



    public Type checkCollision(float x, float y, float width) {
        boolean result = false;

        float left = x - width;
        float right = x + width;
        float top = y - width;
        float bottom = y + width;

        switch (type) {
            case TOP:

                if (top <= start.y && top <= end.y && right >= start.x && left <= end.x)
                    result = true;
                break;
            case BOTTOM:
                if (bottom >= start.y && bottom >= end.y && right >= start.x && left <= end.x)
                    result = true;
                break;
            case LEFT:
                if (left <= start.x && left <= end.x && top <= start.y && bottom >= end.y)
                    result = true;
                break;
            case RIGHT:
                if (right >= start.x && right >= end.x && top <= start.y && bottom >= end.y)
                    result = true;
                break;
            default:
                result = false;
                break;

        }

        if (result) {
            //Log.d(TAG, type.name() + " hitted! "+start.x +" "+start.y +" ");

            return type;
        } else
            return null;

    }
}
