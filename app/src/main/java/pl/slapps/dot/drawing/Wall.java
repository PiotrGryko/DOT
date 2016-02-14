package pl.slapps.dot.drawing;

import android.net.Uri;
import android.opengl.GLES20;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;



import javax.microedition.khronos.opengles.GL10;

import pl.slapps.dot.SurfaceRenderer;
import pl.slapps.dot.generator.Generator;
import pl.slapps.dot.model.Config;

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



    static final int COORDS_PER_VERTEX = 3;

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    private Generator generator;




    public float color[] = { 0.0f, 0.0f, 0.0f, 1.0f};

    public static float[] concatenate(float[] a, float[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        float[] c = (float[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

    public Wall(Junction start, Junction end, Type type)
    {
        this.start = start;
        this.end = end;
        this.type = type;
    }

    public Wall(Junction start, Junction end, Type type, Generator generator) {
        this.start = start;
        this.end = end;
        this.type = type;

        this.generator=generator;



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





    }

    public void configure(Config config)
    {
        this.color = Util.parseColor(config.colors.colorFence);
    }
    public float[] getColor() {return color;}

    public float[] getVert()
    {
        return vert;
    }






    public void drawGl2(float[] mvpMatrix)
    {




        // get handle to vertex shader's vPosition member
        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(generator.mPositionHandle);
        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                generator.mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, bufferedVertex);




        GLES20.glUniform4fv(generator.mColorHandle, 1, color, 0);


        // get handle to shape's transformation matrix
        SurfaceRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and generator transformation
        GLES20.glUniformMatrix4fv(generator.mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        SurfaceRenderer.checkGlError("glUniformMatrix4fv");
        GLES20.glDrawArrays(GL10.GL_LINES, 0, this.vert.length / 3);

        // Draw the square
        // GLES20.glDrawElements(
        //         GLES20.GL_TRIANGLES, indices.length,
        //        GLES20.GL_UNSIGNED_SHORT, bufferedIndices);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(generator.mPositionHandle);




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
