package pl.slapps.dot.game;

import android.graphics.Color;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import pl.slapps.dot.SurfaceRenderer;
import pl.slapps.dot.drawing.Util;
import pl.slapps.dot.model.Config;


public class Explosion {
    public String TAG = Explosion.class.getName();
    public float speed = 20;

    public float x;
    public float y;
    private Random random;
    private Game view;
    private long lifeTime = 0;
    private long time = 0;
    public float a,r, g, b;
    public float size;
    //private Text points;

    private static FloatBuffer bufferedVertex;
    private static FloatBuffer bufferedVertexTwo;
    private static FloatBuffer currentBuffer;
    private FloatBuffer usedBuffer;
    private static int count=40;


    // private FloatBuffer lPos;



    static final int COORDS_PER_VERTEX = 3;



    float color[] = {0.0f, 0.0f, 0.0f, 1.0f};


    private ArrayList<Particle> particles;
    private Config config;

    public static void initBuffers()
    {
        int bufferSize = count * COORDS_PER_VERTEX * 6 * 4;

        bufferedVertex = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder()).asFloatBuffer();
        bufferedVertexTwo = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder()).asFloatBuffer();
    }

    public Explosion(Game view, float x, float y, long time, float speed, int shipSize, Config config) {

        this.config=config;
        random = new Random();
        float colorTreshold = random.nextFloat();
        String finalColor = Util.calculateColorsSwitch(config.colors.colorExplosionStart, config.colors.colorExplosionEnd, colorTreshold);
        int intColor = Color.parseColor(finalColor);


        a = (float) Color.alpha(intColor) / 255;
        r = (float) Color.red(intColor) / 255;
        g = (float) Color.green(intColor) / 255;
        b = (float) Color.blue(intColor) / 255;
/*
        intColor = Color.parseColor(config.colors.colorExplosionEnd);
        float a_end = (float) Color.alpha(intColor) / 255;
        float r_end = (float) Color.red(intColor) / 255;
        float g_end = (float) Color.green(intColor) / 255;
        float b_end = (float) Color.blue(intColor) / 255;
        random = new Random();
*/

        this.view = view;
        this.x = Float.valueOf(x);
        this.y = Float.valueOf(y);
        this.time = time;
        this.size = (float) shipSize / 1.5f;
/*
        r = (float) Math.random() * (Math.abs(r_end - r_start)) + r_start;
        g = (float) Math.random() * (Math.abs(g_end - g_start)) + g_start;
        b = (float) Math.random() * (Math.abs(b_end - b_start)) + b_start;
        //points = new Text(generator,"100",x,y,50,50);

*/

        //  lPos = ByteBuffer.allocateDirect(4 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();


        color = new float[]{
                r, g, b, a,
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
        GLES20.glUseProgram(view.mProgram);

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
        SurfaceRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and generator transformation
        GLES20.glUniformMatrix4fv(view.mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        SurfaceRenderer.checkGlError("glUniformMatrix4fv");

        GLES20.glDrawArrays(GL10.GL_TRIANGLES, 0, count * 6);

        // Draw the square
        // GLES20.glDrawElements(
        //         GLES20.GL_TRIANGLES, indices.length,
        //        GLES20.GL_UNSIGNED_SHORT, bufferedIndices);

        // Disable vertex array

        if(usedBuffer==bufferedVertex) {
            GLES20.glUniform3f(view.mExplosionLightOnePosHandle, x, y, 0.0f);
            GLES20.glUniform1f(view.mExplosionLightOneDistanceHandle, config.settings.explosionOneLightDistance);
            GLES20.glUniform1f(view.mExplosionLightOneShinningHandle, config.settings.explosionOneLightShinning - getProgress()*config.settings.explosionOneLightShinning);
            GLES20.glUniform4fv(view.mExplosionLightOneColorHandle, 1, color, 0);
        }
        else
        {
            GLES20.glUniform3f(view.mExplosionLightTwoPosHandle, x, y, 0.0f);
            GLES20.glUniform1f(view.mExplosionLightTwoDistanceHandle, config.settings.explosionTwoLightDistance);
            GLES20.glUniform1f(view.mExplosionLightTwoShinningHandle, config.settings.explosionTwoLightShinning  - getProgress()*config.settings.explosionTwoLightShinning);
            GLES20.glUniform4fv(view.mExplosionLightTwoColorHandle, 1, color, 0);
        }
        GLES20.glDisableVertexAttribArray(view.mPositionHandle);





    }


}

