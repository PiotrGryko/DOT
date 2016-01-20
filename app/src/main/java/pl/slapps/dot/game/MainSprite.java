package pl.slapps.dot.game;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import pl.slapps.dot.R;
import pl.slapps.dot.tile.TileRoute;
import pl.slapps.dot.tile.TileRouteFinish;


public class MainSprite extends Sprite {

    private String TAG = MainSprite.class.getName();
    private Maze fence;
    private Background background;

    private float angle;
    public float r = 0.0f;
    public float g = 0.0f;
    public float b = 0.0f;

    public boolean prepareToDie;

    private GameView view;

    public TileRoute lastChangeRoute;
    public TileRoute currentTile;

    public float spriteSpeed = 0;

    private FloatBuffer lPos = ByteBuffer.allocateDirect(4 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();



    public void setPrepareToDie(boolean prepareToDie) {
        this.prepareToDie = prepareToDie;
    }

    public void setRotation(float angle) {
        this.angle = angle;
    }

    public boolean isMoving() {
        if (x != 0 || y != 0) {
            return true;
        } else
            return false;
    }

    public MainSprite(GameView view, float centerX, float centerY, int width,
                      int height, String color) {

        super(centerX, centerY, width, height);
        this.view = view;
        fence = view.getMaze();
        background = view.getGameBackground();

        int intColor = Color.parseColor(color);
        r = (float) Color.red(intColor) / 255;
        g = (float) Color.green(intColor) / 255;
        b = (float) Color.blue(intColor) / 255;
        spriteSpeed = view.context.getResources().getDimension(R.dimen.speed);


    }

    public void loadGLTexture(GL10 gl, Context context) {

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.enemy);

        // generate one texture pointer

        // gl.glGenTextures(1, textures, 0);

        // ...and bind it to our array

        // gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

        // create nearest filtered texture

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_NEAREST);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_LINEAR);

        // Use Android GLUtils to specify a two-dimensional texture image from
        // our bitmap

        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);

        // Clean up

        bitmap.recycle();

    }

    public void update() {
        super.update();
        //fence.setMove(-x, -y);
        //background.setMove(-x, -y);

        this.bufferedVertex.position(0);
        this.bufferedVertex.put(this.quad.vertices);
        this.bufferedVertex.position(0);



        lPos.position(0);
        lPos.put(new float[]{this.getCenterX(), this.getCenterY(), 100.0f, 1.0f});
        lPos.position(0);


        TileRoute collision = fence.checkRouteCollision(centerX, centerY, width / 2);
        TileRoute tmpCurrent = fence.getCurrentRouteObject(centerX, centerY);

        if (tmpCurrent != null && currentTile != tmpCurrent) {
            currentTile = tmpCurrent;
            if (x > 0)
                x = spriteSpeed * (float) currentTile.speedRatio;

            if (x < 0)
                x = -spriteSpeed * (float) currentTile.speedRatio;

            if (y > 0)
                y = spriteSpeed * (float) currentTile.speedRatio;
            if (y < 0)
                y = -spriteSpeed * (float) currentTile.speedRatio;


        }

        if (collision != null) {
            if (collision instanceof TileRouteFinish) {
                view.explodeDot(false);
                view.destroyDot();
            } else if (!prepareToDie) {
                view.crashDot(true);
                //    view.toggleColors();
            } else {
                view.explodeDot(true);

                view.resetDot();
                //   view.toggleColors();
            }

        }

    }

    public void draw(GL10 gl) {


        //gl.glRotatef(angle, centerX, centerY, 0);

        gl.glLoadIdentity();




        //    gl.glTranslatef(centerX, centerY, 0);
        //    gl.glRotatef(angle, 0, 0, 1);
        //   gl.glTranslatef(-centerX, -centerY, 0);

        //  gl.glTranslatef(moveX, moveY, 0);

        gl.glColor4f(r, g, b, 0.0f);
        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, bufferedVertex);

        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 2);











//lights








       // gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lPos);



    }

}
