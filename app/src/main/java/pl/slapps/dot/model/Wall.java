package pl.slapps.dot.model;

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

    public static float[] concatenate(float[] a, float[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        float[] c = (float[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

    public Wall(Junction start, Junction end, Type type) {
        this.start = start;
        this.end = end;
        this.type = type;


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

    public float[] getVert()
    {
        return vert;
    }


    public void draw(GL10 gl) {


        gl.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);


        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, bufferedVertex);
        gl.glDrawArrays(GL10.GL_LINES, 0, this.vert.length / 3);


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
