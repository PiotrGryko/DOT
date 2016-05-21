package pl.slapps.dot.drawing;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import pl.slapps.dot.drawing.Quad;


public class Sprite {

    public float centerX; // used to track sprite position
    public float centerY;
    public float moveX; // used to sprite translation
    public float moveY;
    public float width;
    public float height;
    public float vertices[];
    public short indices[];
    public FloatBuffer bufferedVertex;
    public ShortBuffer bufferedIndices;

    public Quad quad;
    public long LAST_FRAME;
    private final long NANO = 1000000;
    private final float SECOND = 1000 * NANO;
    private final float DEFAULT_FRAME = SECOND / 60;


    public float x; // used to increas/decreas sprite position
    public float y;


    public Sprite(float centerX, float centerY, float width,
                  float height, boolean init) {

        setup(centerX, centerY, width, height);
        if (init)
            initBuffers();

    }

    public void setup(float centerX, float centerY, float width,
                      float height) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.width = width;
        this.height = height;

        quad = new Quad(centerX, centerY, width, height);
    }

    public void initBuffers() {
        vertices = quad.vertices;
        indices = quad.indices;



        ByteBuffer bytes = ByteBuffer.allocateDirect(vertices.length * 4);
        bytes.order(ByteOrder.nativeOrder());

        bufferedVertex = bytes.asFloatBuffer();
        bufferedVertex.put(vertices);
        bufferedVertex.position(0);

        bytes = ByteBuffer.allocateDirect(indices.length * 2);
        bytes.order(ByteOrder.nativeOrder());

        bufferedIndices = bytes.asShortBuffer();
        bufferedIndices.put(indices);
        bufferedIndices.position(0);
    }


    public float[] getVertices() {
        return quad.vertices;
    }

    public short[] getIndices() {
        return quad.indices;
    }


    public void setMove(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getMoveX() {
        return x;
    }

    public float getMoveY() {
        return y;
    }

    public void update(float ratio) {

            ratio=ratio/16.6f;
        if(ratio>2)
            ratio=2;

        moveX += x * ratio;
        moveY += y * ratio;
        centerX += x * ratio;
        centerY += y * ratio;

        quad.update(x * ratio, y * ratio);
        quad.initSharedVerticles();
        this.bufferedVertex.position(0);
        this.bufferedVertex.put(quad.vertices);
        this.bufferedVertex.position(0);
        LAST_FRAME = System.nanoTime();
    }

    public float getCenterX() {
        return centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public boolean cointains(Verticle v) {
        if (v.x <= centerX + width / 2
                && v.x >= centerX - width / 2
                && v.y <= centerY + height / 2
                && v.y >= centerY - height / 2)
            return true;

        return false;
    }

    public boolean cointains(float x, float y) {
        if (x <= centerX + width / 2
                && x >= centerX - width / 2
                && y <= centerY + height / 2
                && y >= centerY - height / 2)
            return true;

        return false;
    }

}
