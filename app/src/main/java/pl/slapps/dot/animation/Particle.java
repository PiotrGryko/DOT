package pl.slapps.dot.animation;

import android.media.AsyncPlayer;
import android.media.AudioManager;
import android.net.Uri;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import pl.slapps.dot.drawing.Quad;
import pl.slapps.dot.game.Maze;
import pl.slapps.dot.game.Wall;
import pl.slapps.dot.game.GameView;


public class Particle extends Quad {


    private Maze fence;
    public long lifeTime = 1000;
    private long creationTime;
    private Explosion explosion;
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


    public float x; // used to increas/decreas sprite position
    public float y;


    private GameView view;

    private Wall.Type lastCollision;


    public Particle(GameView view, float centerX, float centerY, int width,
                    int height, Maze fence, long creationTime, Explosion explosion) {

        super(centerX, centerY, width, height);
        this.view = view;
        this.centerX = centerX;
        this.centerY = centerY;
        this.width = startWidth = width;
        this.height = startHeight = height;
        this.creationTime = creationTime;
        this.explosion = explosion;
        this.lifeTime = (long) (Math.random() * 2000);

        this.fence = fence;





    }

  
    public void initDrawing(FloatBuffer bufferedVertex, int index) {
        explosionBufferedVertex = bufferedVertex;
        initVertexArray(centerX, centerY, width, height);
        bufferStartPosition = index * vertices.length;


        // explosionBufferedVertex.put(vertices);
        explosionBufferedVertex.position(bufferStartPosition);
        explosionBufferedVertex.put(vertices);
        /*
        for(int i=0;i<vertices.length;i++)
        {
            explosionBufferedVertex.put(bufferStartPosition+i,vertices[i]);
        }
*/

    }

    public void setMove(float x, float y) {
        //super.setMove(x, y);
        this.startX = this.x = x;
        this.startY = this.y = y;
    }


    public void update(long current) {

        moveX += x;
        moveY += y;
        centerX += x;
        centerY += y;
        long elapsedTime = current - creationTime;
        if (elapsedTime > lifeTime) {
            width = 0;
            height = 0;
        }

        bottomLeft.update(x, y);
        bottomRight.update(x, y);
        topLeft.update(x, y);
        topRight.update(x, y);
        initVertexArray(centerX, centerY, width, height);
        //explosionBufferedVertex.put(vertices);
        explosionBufferedVertex.position(bufferStartPosition);
        explosionBufferedVertex.put(vertices);
        explosionBufferedVertex.position(0);

        /*
        for(int i=0;i<vertices.length;i++)
        {
            explosionBufferedVertex.put(bufferStartPosition+i,vertices[i]);
        }
*/


        if (elapsedTime > lifeTime) {
            explosion.removeParticle(this);
            return;
        }
        this.x = startX - ((float) elapsedTime / (float) lifeTime) * startX;
        this.y = startY - ((float) elapsedTime / (float) lifeTime) * startY;

        //this.gridX=startWidth-((float)elapsedTime/(float)lifeTime)*startWidth;
        //this.gridY=startHeight-((float)elapsedTime/(float)lifeTime)*startHeight;


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
            //playCollisionSound();
           // view.context.getSoundsManager().playRawFile("particle");
        }


    }


}
