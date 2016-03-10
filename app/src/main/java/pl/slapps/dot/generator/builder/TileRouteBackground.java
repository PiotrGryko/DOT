package pl.slapps.dot.generator.builder;

import android.opengl.GLES20;

import pl.slapps.dot.SurfaceRenderer;
import pl.slapps.dot.drawing.Sprite;
import pl.slapps.dot.drawing.Util;
import pl.slapps.dot.generator.Generator;
import pl.slapps.dot.model.Config;

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

    public Generator generator;


    static final int COORDS_PER_VERTEX = 3;


    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    float color[] = {0.0f, 0.0f, 0.0f, 1.0f};


    public void configure(Config config) {
        String colorString = config.colors.colorRoute;

        if (config.settings.switchRouteColors)

            colorString = config.colors.colorSwitchRouteStart;

        color = Util.parseColor(colorString);

    }

    public TileRouteBackground(float centerX, float centerY, float width,
                               float height) {

        super(centerX, centerY, width, height, false);


    }

    public TileRouteBackground(float centerX, float centerY, float width,
                               float height, Generator generator) {

        super(centerX, centerY, width, height, true);
        this.generator = generator;
        configure(generator.getConfig());


    }


    public void update() {
        super.update();
    }


    public void drawGl2(float[] mvpMatrix) {


        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(generator.mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                generator.mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, bufferedVertex);

        // get handle to fragment shader's vColor member

        // Set color for drawing the triangle
        GLES20.glUniform4fv(generator.mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        SurfaceRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and generator transformation
        GLES20.glUniformMatrix4fv(generator.mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        SurfaceRenderer.checkGlError("glUniformMatrix4fv");

        // Draw the square
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, indices.length,
                GLES20.GL_UNSIGNED_SHORT, bufferedIndices);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(generator.mPositionHandle);


    }


}
