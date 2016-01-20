package pl.slapps.dot.tile;

import android.graphics.Color;

import javax.microedition.khronos.opengles.GL10;

import pl.slapps.dot.game.Sprite;

/**
 * Created by piotr on 20.10.15.
 */
public class TileRouteBackground extends Sprite {

    private String TAG = TileRouteBackground.class.getName();


    private float angle;
    public float r = 0.0f;
    public float g = 0.0f;
    public float b = 0.0f;
    public float a = 0.0f;

    public boolean prepareToDie;


    public void setColor(String color) {
        if (color == null)
            color = "#B8B8B8";
        int intColor = Color.parseColor(color);
        a = (float) Color.alpha(intColor) / 255;
        r = (float) Color.red(intColor) / 255;
        g = (float) Color.green(intColor) / 255;
        b = (float) Color.blue(intColor) / 255;
    }


    public TileRouteBackground(float centerX, float centerY, float width,
                               float height, String color) {

        super(centerX, centerY, width, height);
        setColor(color);


    }


    public void update() {
        super.update();
    }

    public void draw(GL10 gl) {


        gl.glLoadIdentity();



        gl.glColor4f(r, g, b, a);
        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, bufferedVertex);
        gl.glDrawElements(GL10.GL_TRIANGLES, indices.length,
                GL10.GL_UNSIGNED_SHORT, bufferedIndices);


        //gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);


    }

}