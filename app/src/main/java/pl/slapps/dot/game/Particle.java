package pl.slapps.dot.game;

import android.opengl.GLES20;
import android.os.SystemClock;
import android.util.Log;

import java.nio.FloatBuffer;

import pl.slapps.dot.SurfaceRenderer;
import pl.slapps.dot.drawing.Quad;
import pl.slapps.dot.drawing.Wall;


public class Particle extends Quad {


    private Maze fence;
    public long lifeTime = 2000;
    private long creationTime;
    float startX;
    float startY;

    public float centerX; // used to track sprite position
    public float centerY;
    public float moveX; // used to sprite translation
    public float moveY;
    public float width;
    public float height;

    public float startWidth;
    public float startHeight;
    public FloatBuffer explosionBufferedVertex;
    public int bufferStartPosition;
    public int index;


    public float x; // used to increas/decreas sprite position
    public float y;


    private long LAST_FRAME;
    private final long NANO = 1000000;
    private final float SECOND = 1000 * NANO;
    private final float DEFAULT_FRAME = SECOND/60;

    private Wall.Type lastCollision;


    public Particle(float centerX, float centerY, int width,
                    int height, Maze fence) {

        super(centerX, centerY, width, height);
        this.centerX = centerX;
        this.centerY = centerY;
        this.width = startWidth = width;
        this.height = startHeight = height;
        this.creationTime = System.currentTimeMillis();
        this.lifeTime = (long) (Math.random() * 2000);
        this.fence = fence;





    }

    public void initVertexArray(float centerX, float centerY, float width,
                                float height)
    {
        vertices = new float[]{centerX,centerY,0};
    }
  
    public void initDrawing(FloatBuffer bufferedVertex, int index) {
        explosionBufferedVertex = bufferedVertex;
        this.index=index;
        initVertexArray(centerX, centerY, width, height);
        bufferStartPosition = index * vertices.length;
        explosionBufferedVertex.position(bufferStartPosition);
        explosionBufferedVertex.put(vertices);


    }

    public void setMove(float x, float y) {
        this.startX = this.x = x;
        this.startY = this.y = y;
    }

    public void update(long current) {


        long elapsedTime = current - creationTime;

        if(elapsedTime>lifeTime)
        {
            vertices[2]=1;
            explosionBufferedVertex.position(bufferStartPosition);
            explosionBufferedVertex.put(vertices);
            explosionBufferedVertex.position(0);
            return;
        }

        /*
        float ratio =0;
        if(LAST_FRAME==0)
            ratio = 1;
        else
        {
            long diff = System.nanoTime()-LAST_FRAME;
            ratio=diff/DEFAULT_FRAME;

           // Log.d("HHH","update "+ratio+" "+diff);

        }
*/
        float measuredX = x;//*ratio;
        float measuredY = y;//*ratio;

        moveX += measuredX;
        moveY += measuredY;
        centerX += measuredX;
        centerY += measuredY;


        bottomLeft.update(measuredX, measuredY);
        bottomRight.update(measuredX, measuredY);
        topLeft.update(measuredX, measuredY);
        topRight.update(measuredX, measuredY);
        initVertexArray(centerX, centerY, width, height);

        explosionBufferedVertex.position(bufferStartPosition);
        explosionBufferedVertex.put(vertices);
        explosionBufferedVertex.position(0);

        LAST_FRAME = System.nanoTime();

        this.x = startX - ((float) elapsedTime / (float) lifeTime) * startX;
        this.y = startY - ((float) elapsedTime / (float) lifeTime) * startY;

        Wall.Type collision = fence.checkCollision(centerX, centerY, width);
        if (collision != null && collision!=lastCollision) {
            lastCollision=collision;


            switch (collision) {
                case LEFT:
                case RIGHT:
                    this.x = -x;
                    this.startX = -startX;
                    break;
                case TOP:
                case BOTTOM:
                    this.y = -y;
                    this.startY = -startY;
                    break;
            }
        }





    }



}
