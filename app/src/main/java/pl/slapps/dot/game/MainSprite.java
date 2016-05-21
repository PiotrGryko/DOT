package pl.slapps.dot.game;

import android.opengl.GLES20;
import android.util.Log;

import org.apache.commons.lang3.ArrayUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import pl.slapps.dot.MainActivity;
import pl.slapps.dot.R;
import pl.slapps.dot.SoundsService;
import pl.slapps.dot.SurfaceRenderer;
import pl.slapps.dot.drawing.Quad;
import pl.slapps.dot.drawing.Sprite;
import pl.slapps.dot.drawing.Util;
import pl.slapps.dot.drawing.Wall;
import pl.slapps.dot.generator.builder.TileRoute;
import pl.slapps.dot.generator.builder.TileRouteFinish;
import pl.slapps.dot.generator.widget.RouteScoreCounter;
import pl.slapps.dot.model.Config;
import pl.slapps.dot.model.Route;


public class MainSprite {


    private String TAG = MainSprite.class.getName();
    private Maze fence;
    private Config config;
    public boolean prepareToDie;

    private Game game;
    public TileRoute lastChangeRoute;
    public TileRoute currentTile;
    public float lightDistance;
    public float lightShinning;
    public float spriteSpeed = 0;
    public float defaultSpeed = 0;

    public boolean booster = false;

    private long BOOSTER_START_TIME;

    private static final long BOOSTER_TIME = 10 * 1000;
    private static final int BYTES_PER_FLOAT = 4;
    private static final int COORDS_PER_VERTEX = 3;
    private static final int BUFFER_SIZE = COORDS_PER_VERTEX * BYTES_PER_FLOAT;

    private final FloatBuffer bufferedSizesVertex;
    private final FloatBuffer bufferedVertex;
    private float[] vertices;



    public float color[] = {0.0f, 0.0f, 0.0f, 1.0f};
    public float centerX;
    public float centerY;
    public float x; //movement x
    public float y; //movement y
    public float dotSize; //sprite size


    public void startBooster() {
        BOOSTER_START_TIME = System.currentTimeMillis();
        booster = true;

        if (game != null)
            this.game.colorFilter = Util.parseColor("#0011FF");
    }

    private void stopBooster() {
        if (booster) {
            booster = false;
            this.game.colorFilter = Util.parseColor(config.colors.colorFilter);
        }
    }

    public void setPrepareToDie(boolean prepareToDie) {
        this.prepareToDie = prepareToDie;
    }


    public boolean isMoving() {
        if (x != 0 || y != 0) {
            return true;
        } else
            return false;
    }

    public void setMove(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public MainSprite(Game view, float centerX, float centerY, int width,
                      int height, Config config) {


        this.centerX=centerX;
        this.centerY=centerY;
        this.dotSize=width;
        this.game = view;
        fence = view.maze;


        bufferedSizesVertex = ByteBuffer.allocateDirect(BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        bufferedVertex = ByteBuffer.allocateDirect(BUFFER_SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertices = new float[]{centerX,centerY,0};

        bufferedVertex.position(0);
        bufferedVertex.put(vertices);
        bufferedVertex.position(0);
        bufferedSizesVertex.position(0);
        bufferedSizesVertex.put(dotSize);
        bufferedSizesVertex.position(0);

        if (MainActivity.screenHeight < MainActivity.screenWidth)
            defaultSpeed = MainActivity.screenHeight / 120;
        else
            defaultSpeed = MainActivity.screenWidth / 120;

        spriteSpeed = defaultSpeed;
        configure(config);


    }


    public void configure(Config config) {
        this.config = config;

        if (config.settings.speedRatio != 0)
            spriteSpeed = defaultSpeed * config.settings.speedRatio;

        if (!config.settings.switchDotColor) {
            color = Util.parseColor(config.colors.colorShip);
        } else {
            color = Util.parseColor(config.colors.colorSwitchDotStart);

        }

        if (!config.settings.switchDotLightDistance)
            lightDistance = config.settings.dotLightDistance;
        else
            lightDistance = config.settings.dotLightDistanceStart;


        lightShinning = config.settings.dotLightShinning;

    }


    public void update(float ratio) {

        ratio=ratio/16.6f;
        if(ratio>2)
            ratio=2;

        centerX += x * ratio;
        centerY += y * ratio;

        vertices = new float[]{centerX,centerY,0};

        bufferedVertex.position(0);
        bufferedVertex.put(vertices);
        bufferedVertex.position(0);


        if (booster) {
            if (System.currentTimeMillis() - BOOSTER_START_TIME > BOOSTER_TIME) {
                stopBooster();

            }
        }

        TileRoute tmpCurrent = null;

        if (currentTile == null || !currentTile.contains(centerX, centerY))
            tmpCurrent = fence.getCurrentRouteObject(centerX, centerY);
        else
            tmpCurrent = currentTile;

        Wall.Type collision = null;
        if (tmpCurrent != null)
            collision = tmpCurrent.checkCollision(centerX, centerY,dotSize / 2);

        fence.checkCoinCollision(this);


        if (tmpCurrent != null && currentTile != tmpCurrent) {

            currentTile = tmpCurrent;

            float speedRatio = 1;
            if (booster) {
                if (tmpCurrent.type == Route.Type.FINISH || tmpCurrent.type == Route.Type.START) {

                    speedRatio = 1;
                } else if (tmpCurrent.getDirection() == Route.Movement.LEFTRIGHT || tmpCurrent.getDirection() == Route.Movement.TOPBOTTOM || tmpCurrent.getDirection() == Route.Movement.BOTTOMTOP || tmpCurrent.getDirection() == Route.Movement.RIGHTLEFT) {
                    speedRatio = 1.5f;
                } else {
                    speedRatio = 0.5f;
                }
            } else {

                speedRatio = (float) currentTile.speedRatio;
            }


            if (x > 0)
                x = spriteSpeed * speedRatio;
            if (x < 0)
                x = -spriteSpeed * speedRatio;
            if (y > 0)
                y = spriteSpeed * speedRatio;
            if (y < 0)
                y = -spriteSpeed * speedRatio;


        }

        if (collision != null) {
            stopBooster();
            if (currentTile.getType() == Route.Type.FINISH) {
                game.explodeDot(false);
                game.destroyDot();
                if (game.getPreview()) {
                    game.resetDot();
                    MainActivity.sendAction(SoundsService.ACTION_FINISH, null);

                } else
                    game.gameView.moveToNextLvl();

            } else if (!prepareToDie) {
                game.crashDot(true);
            } else {
                game.explodeDot(true);


                if (new Random().nextFloat() > 0.75f) {

                    if(game.gameView.context.showAdv())
                        game.setPaused(true);


                }

                game.resetDot();
            }

        }


    }


    public void drawGl2(float[] mvpMatrix) {

        GLES20.glUseProgram(game.mPointProgram);

        // get handle to vertex shader's vPosition member

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(game.mPointPositionHandle);


        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                game.mPointPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                0, bufferedVertex);


        GLES20.glEnableVertexAttribArray(game.mPointSizeHandle);

        GLES20.glVertexAttribPointer(
                game.mPointSizeHandle, 1,
                GLES20.GL_FLOAT, false,
                0, bufferedSizesVertex);

        GLES20.glUniform4fv(game.mPointColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        // SurfaceRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and generator transformation
        GLES20.glUniformMatrix4fv(game.mPointMVPMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glDrawArrays(GL10.GL_POINTS, 0, 1);



        GLES20.glDisableVertexAttribArray(game.mPointPositionHandle
        );

        GLES20.glDisableVertexAttribArray(game.mPointSizeHandle
        );


    }


}
