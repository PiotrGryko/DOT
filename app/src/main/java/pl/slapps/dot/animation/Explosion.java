package pl.slapps.dot.animation;

import android.graphics.Color;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import pl.slapps.dot.game.GameView;


public class Explosion {
    public String TAG = Explosion.class.getName();
    public float speed = 20;

    public float x;
    public float y;
    private Random random;
    private GameView view;
    private long lifeTime = 1500;
    private long time = 0;
    public float r, g, b;
    public float size;
    //private Text points;
    private int bufferSize;

    private FloatBuffer bufferedVertex;


    private ArrayList<Particle> particles;
    private int count;

    public Explosion(GameView view, int count, float x, float y, long time, float speed, int shipSize, String color_start, String color_end) {


        int intColor = Color.parseColor(color_start);
        float a_start = (float) Color.alpha(intColor) / 255;
        float r_start = (float) Color.red(intColor) / 255;
        float g_start = (float) Color.green(intColor) / 255;
        float b_start = (float) Color.blue(intColor) / 255;

        intColor = Color.parseColor(color_end);
        float a_end = (float) Color.alpha(intColor) / 255;
        float r_end = (float) Color.red(intColor) / 255;
        float g_end = (float) Color.green(intColor) / 255;
        float b_end = (float) Color.blue(intColor) / 255;


        random = new Random();
        this.view = view;
        this.x = x;
        this.y = y;
        this.time = time;
        this.size = (float) shipSize / 1.5f;

        r = (float) Math.random() * (Math.abs(r_end - r_start)) + r_start;
        g = (float) Math.random() * (Math.abs(g_end - g_start)) + g_start;
        b = (float) Math.random() * (Math.abs(b_end - b_start)) + b_start;
        //points = new Text(view,"100",x,y,50,50);

        this.count=count;
        bufferSize = count*2*6*4;

        ByteBuffer bytes = ByteBuffer.allocateDirect(bufferSize);
        bytes.order(ByteOrder.nativeOrder());

        bufferedVertex = bytes.asFloatBuffer();

        particles = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            //if(r.nextBoolean())

            int partSize = random.nextInt((int) size);

            Particle p = new Particle(view, x, y, partSize, partSize, view.getMaze(), time, this);
            p.setMove(randomSign((float) Math.random() * speed * 1.5f),
                    randomSign((float) Math.random() * speed * 1.5f));
            p.initDrawing(bufferedVertex,i);
            particles.add(p);
        }
        bufferedVertex.position(0);


    }

    public float randomSign(float number) {
        if (random.nextBoolean())
            return number;
        else
            return -number;

    }

    public void removeParticle(Particle particle) {
        particles.remove(particle);


        if (particles.size() == 0)
            view.removeExplosion(this);
    }

    public void update(long current)
    {
        for (int i = 0; i < particles.size(); i++)
            particles.get(i).update(current);
    }


    public void draw(GL10 gl) {

        gl.glLoadIdentity();


        gl.glColor4f(r, g, b, 0.0f);

        //gl.glTranslatef(moveX, moveY, 0);

        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, bufferedVertex);
        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, count*3);
        //for (int i = 0; i < particles.size(); i++)
        ///    particles.get(i).draw(gl, r, g, b);

        //points.draw(gl);


    }
}

