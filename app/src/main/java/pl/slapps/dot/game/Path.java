package pl.slapps.dot.game;

import android.opengl.GLES20;
import android.util.Log;

import org.apache.commons.lang3.ArrayUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import pl.slapps.dot.SurfaceRenderer;
import pl.slapps.dot.drawing.Quad;
import pl.slapps.dot.drawing.Util;
import pl.slapps.dot.drawing.Wall;
import pl.slapps.dot.generator.builder.TileRoute;
import pl.slapps.dot.generator.builder.TileRouteBackground;
import pl.slapps.dot.model.Config;

/**
 * Created by piotr on 01.11.15.
 */
public class Path {


    public float[] roadVert;
    public short[] roadIndices;

    public FloatBuffer roadBufferedVertex;
    public ShortBuffer roadBufferedIndices;
    //public FloatBuffer colorBuffer;



    private String TAG = Path.class.getName();
    public float[] verticles;
    public short[] indices;
    private Game game;
    private Config config;
    static final int COORDS_PER_VERTEX = 3;
    float color[] = {0.0f, 0.0f, 0.0f, 1.0f};
    private ArrayList<Quad> quads; //list of quads ordered from beginning to end
    //boolean initialized=false;

    /*
    public Wall.Type checkCollision(Quad quad,float x, float y) {




        for(int i=0;i<quads.size();i++)
        {
            if(quads.get(i).contains(x,y))
            {
                Wall.Type result = Quad.checkChollision(quads.get(i), quad);
                if(result!=null) {
                    Log.d("hhh","check colision "+result.name());
                    return result;
                }
            }

        }

        return null;
    }


    public boolean detectCollision(Quad quad) {


        boolean leftTop = false;
        boolean rightTop = false;
        boolean leftBottom = false;
        boolean rightBottom = false;

        for (int i = 0; i < quads.size(); i++) {

            if (quads.get(i).contains(quad.left, quad.top))
                leftTop = true;
            if (quads.get(i).contains(quad.right, quad.top))
                rightTop = true;
            if (quads.get(i).contains(quad.left, quad.bottom))
                leftBottom = true;
            if (quads.get(i).contains(quad.right, quad.bottom))
                rightBottom = true;

        }

        if (!leftTop
                || !leftBottom
                || !rightBottom
                || !rightTop) {
            Log.d("ttt", "collision detected");
            return true;
        } else
            return false;
    }
*/

    public void onProgressChanged(float value) {
        if (config.settings.switchRouteColors) {
            String finalColor = Util.calculateColorsSwitch(config.colors.colorSwitchRouteStart, config.colors.colorSwitchRouteEnd, value);
            color = Util.parseColor(finalColor);
        }

    }

    public void configure(Config config) {
        this.config = config;

        String finalColor = config.colors.colorRoute;
        if (config.settings.switchRouteColors)
            finalColor = config.colors.colorSwitchRouteStart;
        color = Util.parseColor(finalColor);
/*
        buildColors();



        if(initialized) {
            colorBuffer.position(0);
            colorBuffer.put(color);
            colorBuffer.position(0);

           // Log.e("zzz", "configure " + ArrayUtils.toString(color));
        }
  */
    }


/*
    private void buildColors()
    {
        float[] out = new float[0];
        for(int i=0;i<quads.size();i++)
        {
            out = Util.summArrays(out,color);
            out = Util.summArrays(out,color);
            out = Util.summArrays(out,color);
            out = Util.summArrays(out,color);
        }
        color=out;

    }
*/
    public Path(ArrayList<TileRoute> routes, Game game, Config config) {
        this.game = game;
        this.config = config;
        ArrayList<TileRouteBackground> backgrounds = new ArrayList<>();
        ArrayList<Quad> data = new ArrayList<>();

        for (int i = 0; i < routes.size(); i++) {
            backgrounds.addAll(routes.get(i).getBackgrounds());
        }

        for (int i = 0; i < backgrounds.size(); i++) {
            data.add(backgrounds.get(i).quad);
        }

        this.quads = new ArrayList<>();
        verticles = data.get(0).vertices;
        indices = data.get(0).indices;


        for (int i = 0; i < data.size(); i++) {
            addQuad(data.get(i));
        }

        configure(config);


        verticles = new float[this.quads.size() * COORDS_PER_VERTEX * 4];
        indices = new short[this.quads.size() * 2 * 3];
  //      float[] colors = new float[this.quads.size()*4*4];


        //Building arrays

        for (int j = 0; j < this.quads.size(); j++) {
            int startVerticlesIndex = j * COORDS_PER_VERTEX * 4;
            int startIncentIndex = j * 6;



            Quad q = quads.get(j);
            //Log.d(TAG,"strt index "+startVerticlesIndex +" "+startIncentIndex);
            verticles[startVerticlesIndex++] = q.bottomLeft.x;
            verticles[startVerticlesIndex++] = q.bottomLeft.y;
            verticles[startVerticlesIndex++] = q.bottomLeft.z;

            verticles[startVerticlesIndex++] = q.topLeft.x;
            verticles[startVerticlesIndex++] = q.topLeft.y;
            verticles[startVerticlesIndex++] = q.topLeft.z;

            verticles[startVerticlesIndex++] = q.bottomRight.x;
            verticles[startVerticlesIndex++] = q.bottomRight.y;
            verticles[startVerticlesIndex++] = q.bottomRight.z;

            verticles[startVerticlesIndex++] = q.topRight.x;
            verticles[startVerticlesIndex++] = q.topRight.y;
            verticles[startVerticlesIndex++] = q.topRight.z;

            indices[startIncentIndex++] = q.bottomLeft.index;
            indices[startIncentIndex++] = q.topLeft.index;
            indices[startIncentIndex++] = q.bottomRight.index;

            indices[startIncentIndex++] = q.bottomRight.index;
            indices[startIncentIndex++] = q.topLeft.index;
            indices[startIncentIndex++] = q.topRight.index;


        }

        //buildColors();

        roadVert = verticles;
        roadIndices = indices;
        ByteBuffer bytes = ByteBuffer.allocateDirect(roadVert.length * 4);
        bytes.order(ByteOrder.nativeOrder());

        roadBufferedVertex = bytes.asFloatBuffer();
        roadBufferedVertex.put(roadVert);
        roadBufferedVertex.position(0);

        bytes = ByteBuffer.allocateDirect(roadIndices.length * 2);
        bytes.order(ByteOrder.nativeOrder());

        roadBufferedIndices = bytes.asShortBuffer();
        roadBufferedIndices.put(roadIndices);
        roadBufferedIndices.position(0);
/*
        ByteBuffer cb = ByteBuffer.allocateDirect(color.length * 4);
        cb.order(ByteOrder.nativeOrder());
        colorBuffer = cb.asFloatBuffer();
        colorBuffer.put(color);
        colorBuffer.position(0);

        initialized=true;
*/
    }


    private void addQuad(Quad q) {
        for (int i = 0; i < quads.size(); i++) {
            Quad e = quads.get(i);
            if (e.compareTop(q)) {
                q.topLeft.index = e.topLeft.index;
                q.topRight.index = e.topRight.index;
                e.topLeft = q.topLeft;
                e.topRight = q.topRight;
                return;
            }
            if (e.compareRight(q)) {
                q.bottomRight.index = e.bottomRight.index;
                q.topRight.index = e.topRight.index;
                e.topRight = q.topRight;
                e.bottomRight = q.bottomRight;
                return;
            }

            if (e.compareLeft(q)) {
                q.bottomLeft.index = e.bottomLeft.index;
                q.topLeft.index = e.topLeft.index;
                e.topLeft = q.topLeft;
                e.bottomLeft = q.bottomLeft;
                return;
            }
            if (e.compareBottom(q)) {
                q.bottomRight.index = e.bottomRight.index;
                q.bottomLeft.index = e.bottomLeft.index;
                e.bottomRight = q.bottomRight;
                e.bottomLeft = q.bottomLeft;
                return;
            }

        }


        int startIndex = quads.size() * 4;
        q.bottomRight.index += startIndex;
        q.bottomLeft.index += startIndex;
        q.topRight.index += startIndex;
        q.topLeft.index += startIndex;


        quads.add(q);

    }


    public void drawGl2(float[] mvpMatrix) {


        // Add program to OpenGL environment
       // GLES20.glUseProgram(game.mProgram);


        // get handle to vertex shader's vPosition member

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(game.mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                game.mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                0, roadBufferedVertex);


        // get handle to fragment shader's vColor member
        // Pass in the color information
        // Set color for drawing the triangle
        //GLES20.glUniform4fv(game.mColorHandle, 1, color, 0);


        //GLES20.glEnableVertexAttribArray(game.mColorHandle);
        GLES20.glUniform4fv(game.mColorHandle, 1, color, 0);
// Prepare the triangle coordinate data
       // GLES20.glVertexAttribPointer(game.mColorHandle, 4, GLES20.GL_FLOAT, false, 0, colorBuffer);
        //GLES20.glDisableVertexAttribArray(game.mColorHandle);

        // Apply the projection and generator transformation
       // GLES20.glUniformMatrix4fv(game.mMVPMatrixHandle, 1, false, mvpMatrix, 0);
       // SurfaceRenderer.checkGlError("glUniformMatrix4fv");

        // Draw the square
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, indices.length,
                GLES20.GL_UNSIGNED_SHORT, roadBufferedIndices);

        // Disable vertex array
       GLES20.glDisableVertexAttribArray(game.mPositionHandle);


    }


}
