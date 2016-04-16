package pl.slapps.dot.game;

import android.graphics.Color;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import pl.slapps.dot.SurfaceRenderer;
import pl.slapps.dot.drawing.Util;
import pl.slapps.dot.model.Config;


public class ExplosionManager {


    private Game view;
    private Random random;
    private Config config;
    public float size;


    private int EXPLOSIONS_CURRENT_COUNT = 0;
    static final Explosion[] explosions = new Explosion[2];
    static final int PARTICLES_MAX = 80;
    static final int BYTES_PER_FLOAT = 4;
    static final int COORDS_PER_VERTEX = 3;
    static final int PARTICLES_PER_EXPLOSION = 40;


    static final int BUFFER_SIZE = PARTICLES_MAX * COORDS_PER_VERTEX * BYTES_PER_FLOAT;

    private final FloatBuffer bufferedVertex = ByteBuffer.allocateDirect(BUFFER_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer();
    private final FloatBuffer bufferedSizesVertex = ByteBuffer.allocateDirect(PARTICLES_MAX * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();


    class Explosion {

        private int START_POSITION;
        private final float color[] = {0.0f, 0.0f, 0.0f, 1.0f};
        private final Particle[] particles = new Particle[PARTICLES_PER_EXPLOSION];
        private int EXPLOSION_INDEX = 0;
        private float x;
        private float y;
        private long creationTime;
        private long elapsedTime;
        private final long LIFE_TIME = 2000;
        public float a, r, g, b;


        private int mExplosionLightPosHandle;
        private int mExplosionLightShinningHandle;
        private int mExplosionLightDistanceHandle;
        private int mExplosionLightColorHandle;

        public Explosion(float x, float y, float speed, int index) {

            this.x = x;
            this.y = y;
            EXPLOSION_INDEX = index;
            creationTime = System.currentTimeMillis();
            random = new Random();
            float colorTreshold = random.nextFloat();
            String finalColor = Util.calculateColorsSwitch(config.colors.colorExplosionStart, config.colors.colorExplosionEnd, colorTreshold);
            int intColor = Color.parseColor(finalColor);


            a = (float) Color.alpha(intColor) / 255;
            r = (float) Color.red(intColor) / 255;
            g = (float) Color.green(intColor) / 255;
            b = (float) Color.blue(intColor) / 255;


            color[0] = r;
            color[1] = g;
            color[2] = b;
            color[3] = a;


            START_POSITION = index * PARTICLES_PER_EXPLOSION;
            if (START_POSITION > 0)
                START_POSITION--;


            int bufferPosition = START_POSITION;


            for (int i = 0; i < PARTICLES_PER_EXPLOSION; i++) {

                int partSize = random.nextInt((int) size);

                Particle p = new Particle(x, y, partSize, partSize, view.maze);

                p.setMove(randomSign((float) Math.random() * speed * 1.5f),
                        randomSign((float) Math.random() * speed * 1.5f));
                p.initDrawing(bufferedVertex, bufferPosition);
                bufferedSizesVertex.position(bufferPosition);
                bufferedSizesVertex.put(partSize);
                particles[i] = p;
                bufferPosition++;

            }
            bufferedSizesVertex.position(0);
            bufferedVertex.position(0);
        }


        public void update(long current) {
            for (int i = 0; i < particles.length; i++) {
                if (particles[i] != null)
                    particles[i].update(current);
            }
            elapsedTime = current - creationTime;


            //REMOVE EXPLOSION
            if (elapsedTime > LIFE_TIME) {
                Log.d("YYY", "Explosion remove " + EXPLOSION_INDEX);

                for (int i = 0; i < PARTICLES_PER_EXPLOSION; i++) {
                    particles[i] = null;
                }


                GLES20.glUniform3f(mExplosionLightPosHandle, x, y, 0.0f);
                GLES20.glUniform1f(mExplosionLightDistanceHandle, 0);
                GLES20.glUniform1f(mExplosionLightShinningHandle, 0);
                GLES20.glUniform4fv(mExplosionLightColorHandle, 1, color, 0);



                explosions[EXPLOSION_INDEX] = null;
                if (EXPLOSIONS_CURRENT_COUNT > 0)
                    EXPLOSIONS_CURRENT_COUNT--;
                return;
            }

        }


        public void drawGl2(float[] mvpMatrix) {

            // Add program to OpenGL environment
            GLES20.glUseProgram(view.mPointProgram);

            // get handle to vertex shader's vPosition member

            // Enable a handle to the triangle vertices
            GLES20.glEnableVertexAttribArray(view.mPointPositionHandle);


            // Prepare the triangle coordinate data
            GLES20.glVertexAttribPointer(
                    view.mPointPositionHandle, COORDS_PER_VERTEX,
                    GLES20.GL_FLOAT, false,
                    0, bufferedVertex);


            GLES20.glEnableVertexAttribArray(view.mPointSizeHandle);

            GLES20.glVertexAttribPointer(
                    view.mPointSizeHandle, 1,
                    GLES20.GL_FLOAT, false,
                    0, bufferedSizesVertex);

            GLES20.glUniform4fv(view.mPointColorHandle, 1, color, 0);

            // get handle to shape's transformation matrix
            // SurfaceRenderer.checkGlError("glGetUniformLocation");

            // Apply the projection and generator transformation
            GLES20.glUniformMatrix4fv(view.mPointMVPMatrixHandle, 1, false, mvpMatrix, 0);
            SurfaceRenderer.checkGlError("glUniformMatrix4fv");

            GLES20.glDrawArrays(GL10.GL_POINTS, START_POSITION, PARTICLES_PER_EXPLOSION);

            GLES20.glUseProgram(view.mProgram);


            switch (EXPLOSION_INDEX) {
                case 0: {
                    mExplosionLightPosHandle = GLES20.glGetUniformLocation(view.mProgram, "lights[0].u_LightPos");
                    mExplosionLightDistanceHandle = GLES20.glGetUniformLocation(view.mProgram, "lights[0].lightDistance");
                    mExplosionLightShinningHandle = GLES20.glGetUniformLocation(view.mProgram, "lights[0].lightShinning");
                    mExplosionLightColorHandle = GLES20.glGetUniformLocation(view.mProgram, "lights[0].lightColor");
                    break;


                }

                case 1: {
                    mExplosionLightPosHandle = GLES20.glGetUniformLocation(view.mProgram, "lights[1].u_LightPos");
                    mExplosionLightDistanceHandle = GLES20.glGetUniformLocation(view.mProgram, "lights[1].lightDistance");
                    mExplosionLightShinningHandle = GLES20.glGetUniformLocation(view.mProgram, "lights[1].lightShinning");
                    mExplosionLightColorHandle = GLES20.glGetUniformLocation(view.mProgram, "lights[1].lightColor");
                    break;
                }
            /*
                case 2: {
                    mExplosionLightPosHandle = GLES20.glGetUniformLocation(view.mProgram, "lights[2].u_LightPos");
                    mExplosionLightDistanceHandle = GLES20.glGetUniformLocation(view.mProgram, "lights[2].lightDistance");
                    mExplosionLightShinningHandle = GLES20.glGetUniformLocation(view.mProgram, "lights[2].lightShinning");
                    mExplosionLightColorHandle = GLES20.glGetUniformLocation(view.mProgram, "lights[2].lightColor");
                    break;
                }
                case 3: {
                    mExplosionLightPosHandle = GLES20.glGetUniformLocation(view.mProgram, "lights[3].u_LightPos");
                    mExplosionLightDistanceHandle = GLES20.glGetUniformLocation(view.mProgram, "lights[3].lightDistance");
                    mExplosionLightShinningHandle = GLES20.glGetUniformLocation(view.mProgram, "lights[3].lightShinning");
                    mExplosionLightColorHandle = GLES20.glGetUniformLocation(view.mProgram, "lights[3].lightColor");
                    break;
                }
*/

            }

            //  if(EXPLOSION_INDEX==0||EXPLOSION_INDEX==2) {

            GLES20.glUniform3f(mExplosionLightPosHandle, x, y, 0.0f);
            GLES20.glUniform1f(mExplosionLightDistanceHandle, config.settings.explosionOneLightDistance);
            GLES20.glUniform1f(mExplosionLightShinningHandle, config.settings.explosionOneLightShinning - (float) elapsedTime / (float) LIFE_TIME * config.settings.explosionOneLightShinning);
            GLES20.glUniform4fv(mExplosionLightColorHandle, 1, color, 0);

           /*
            }
            else
            {
                GLES20.glUniform3f(view.mExplosionLightTwoPosHandle, x, y, 0.0f);
                GLES20.glUniform1f(view.mExplosionLightTwoDistanceHandle, config.settings.explosionTwoLightDistance);
                GLES20.glUniform1f(view.mExplosionLightTwoShinningHandle, config.settings.explosionTwoLightShinning - (float) elapsedTime / (float) LIFE_TIME * config.settings.explosionTwoLightShinning);
                GLES20.glUniform4fv(view.mExplosionLightTwoColorHandle, 1, color, 0);
            }
*/
            GLES20.glDisableVertexAttribArray(view.mPointPositionHandle
            );

            GLES20.glDisableVertexAttribArray(view.mPointSizeHandle
            );

        }

    }


    public ExplosionManager(Game view, float shipSize, Config config) {
        this.size = shipSize / 2f;
        this.view = view;
        this.config = config;

    }

    public void configure(Config config) {
        this.config = config;
    }

    public void explode(float x, float y, float speed) {

        if (EXPLOSIONS_CURRENT_COUNT >= explosions.length)
            EXPLOSIONS_CURRENT_COUNT = 0;

        final int index = EXPLOSIONS_CURRENT_COUNT;
        explosions[index] = new Explosion(x, y, speed, index);
        EXPLOSIONS_CURRENT_COUNT++;


    }


    public float randomSign(float number) {
        if (random.nextBoolean())
            return number;
        else
            return -number;

    }


    public void update(long current) {
        for (int i = 0; i < explosions.length; i++)
            if (explosions[i] != null) {
                explosions[i].update(current);

            }


    }


    public void drawGl2(float[] mvpMatrix) {


        for (int i = 0; i < explosions.length; i++) {
            if (explosions[i] != null) {
                explosions[i].drawGl2(mvpMatrix);

            }
        }

    }


}

