package pl.slapps.dot.animation;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import pl.slapps.dot.model.Maze;
import pl.slapps.dot.model.Sprite;
import pl.slapps.dot.model.Wall;
import pl.slapps.dot.view.GameView;


public class Particle extends Sprite {


    private Maze fence;
    private long lifeTime = 1000;
    private long creationTime;
    private Explosion explosion;
    float startX;
    float startY;


    public Particle(GameView view, float centerX, float centerY, int width,
                    int height, Maze fence, long creationTime, Explosion explosion) {

        super(centerX, centerY, width, height);
        this.creationTime = creationTime;
        this.explosion = explosion;
        this.lifeTime = (long) (Math.random() * 1500);

        this.fence = fence;


    }

    public void setMove(float x, float y)
    {
        super.setMove(x,y);
        this.startX=this.x;
        this.startY=this.y;
    }


    public void update(long current) {

        super.update();

        long elapsedTime =current - creationTime;
        if (elapsedTime > lifeTime) {
            explosion.removeParticle(this);
            return;
        }
        this.x = startX-((float)elapsedTime/(float)lifeTime)*startX;
        this.y = startY-((float)elapsedTime/(float)lifeTime)*startY;



        Wall.Type collision = fence.checkCollision(centerX, centerY, width);
        if (collision != null) {
            switch (collision) {
                case LEFT:
                case RIGHT:
                    this.x = -x;
                    this.startX=-startX;
                    break;
                case TOP:
                case BOTTOM:
                    this.y = -y;
                    this.startY=-startY;
                    break;
            }
        }


    }

    public void draw(GL10 gl, float r, float g, float b) {


        gl.glLoadIdentity();



        //gl.glColor4f((float) Math.random() * 255, (float) Math.random() * 255,
        //		(float) Math.random() * 255, 0.5f);

        //	gl.glco

        gl.glColor4f(r, g, b, 0.0f);

        gl.glTranslatef(moveX, moveY, 0);

        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, bufferedVertex);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);

    }

}
