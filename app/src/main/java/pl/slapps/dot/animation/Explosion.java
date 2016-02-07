package pl.slapps.dot.animation;

import android.graphics.Color;
import android.opengl.GLES20;
import android.util.Log;

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
    private long lifeTime = 0;
    private long time = 0;
    public float r, g, b;
    public float size;
    //private Text points;

    private static FloatBuffer bufferedVertex;
    private static FloatBuffer bufferedVertexTwo;
    private static FloatBuffer currentBuffer;
    private FloatBuffer usedBuffer;
    private static int count=20;


    // private FloatBuffer lPos;

    private float lightRange = 100f;


    static final int COORDS_PER_VERTEX = 3;



    float color[] = {0.0f, 0.0f, 0.0f, 1.0f};


    private ArrayList<Particle> particles;

    public static void initBuffers()
    {
        int bufferSize = count * COORDS_PER_VERTEX * 6 * 4;

        bufferedVertex = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder()).asFloatBuffer();
        bufferedVertexTwo = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }

    public Explosion(GameView view, float x, float y, long time, float speed, int shipSize, String color_start, String color_end) {


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



        //  lPos = ByteBuffer.allocateDirect(4 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();


        color = new float[]{
                r, g, b, 1.0f,
              };


        if(currentBuffer!=bufferedVertexTwo) {
            currentBuffer = bufferedVertexTwo;
        }else {
            currentBuffer = bufferedVertex;

        }
        usedBuffer=currentBuffer;
        usedBuffer.position(0);
        particles = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            //if(r.nextBoolean())
            int partSize = random.nextInt((int) size);

            Particle p = new Particle(view, x, y, partSize, partSize, view.getMaze(), time, this);

            if (p.lifeTime > lifeTime)
                lifeTime = p.lifeTime;
            p.setMove(randomSign((float) Math.random() * speed * 1.5f),
                    randomSign((float) Math.random() * speed * 1.5f));
            p.initDrawing(usedBuffer, i);
            particles.add(p);
        }
        usedBuffer.position(0);




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

    public void update(long current) {
        for (int i = 0; i < particles.size(); i++)
            particles.get(i).update(current);

        //lPos.position(0);
        /// lPos.put(new float[]{x, y, lightRange-lightRange*getProgress(), 0.0f});
        //lPos.position(0);
    }

    public float getProgress() {
        long elapsedTime = System.currentTimeMillis() - time;
        float progress = (float) elapsedTime / (float) lifeTime;
        return progress;
    }


    public void drawGl2(float[] mvpMatrix) {


        // Add program to OpenGL environment
        GLES20.glUseProgram(view.mCurrentProgram);

        // get handle to vertex shader's vPosition member

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(view.mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                view.mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                0, usedBuffer);


        GLES20.glUniform4fv(view.mColorHandle, 1, color, 0);


        // get handle to shape's transformation matrix
        GameView.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(view.mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        GameView.checkGlError("glUniformMatrix4fv");

        GLES20.glDrawArrays(GL10.GL_TRIANGLES, 0, count * 6);

        // Draw the square
        // GLES20.glDrawElements(
        //         GLES20.GL_TRIANGLES, indices.length,
        //        GLES20.GL_UNSIGNED_SHORT, bufferedIndices);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(view.mPositionHandle);


    }


}

