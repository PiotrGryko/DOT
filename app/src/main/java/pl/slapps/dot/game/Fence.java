package pl.slapps.dot.game;

import android.opengl.GLES20;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import pl.slapps.dot.SurfaceRenderer;
import pl.slapps.dot.drawing.Util;
import pl.slapps.dot.drawing.Wall;
import pl.slapps.dot.generator.builder.TileRoute;
import pl.slapps.dot.model.Config;

/**
 * Created by piotr on 02.11.15.
 */
public class Fence {


    private float vert[];
    private FloatBuffer bufferedVertex;
    private ArrayList<Wall> walls;


    static final int COORDS_PER_VERTEX = 2;
    private Game game;


    float color[] = {0.0f, 0.0f, 0.0f, 1.0f};



    public void configure(Config config)
    {
        this.color= Util.parseColor(config.colors.colorFence);

    }


    public Fence(ArrayList<TileRoute> routes,  Game game, Config config) {
        this.game= game;
        walls = new ArrayList<>();
        configure(config);
        ArrayList<Wall> tmp = new ArrayList<>();

        for (int i = 0; i < routes.size(); i++) {
            tmp.addAll(routes.get(i).getWalls());
        }
        for (int i = 0; i < tmp.size(); i++) {
            Wall e = tmp.get(i);
            walls.add(new Wall(e.start, e.end, e.type));
        }

        sortWalls();
        ArrayList<Float> coords = new ArrayList<>();



        for (int i = 0; i < walls.size(); i++) {
            Wall w = walls.get(i);
            if (i < walls.size() - 1) {
                Wall next = walls.get(i + 1);
                if (next.end.x == w.start.x || next.end.y == w.start.y)
                    continue;
            }

            coords.add(w.end.x);
            coords.add(w.end.y);

        }
        float[] tab = new float[coords.size()];

        for (int i = 0; i < coords.size(); i++) {

            tab[i] = coords.get(i);

        }


        vert = tab;


        ByteBuffer bytes = ByteBuffer.allocateDirect(vert.length * 4)
                .order(ByteOrder.nativeOrder());

        bufferedVertex = bytes.asFloatBuffer();
        bufferedVertex.put(vert).position(0);





    }

    private void sortWalls() {
        if (walls.size() == 0)
            return;


        ArrayList<Wall> output = new ArrayList<>();
        Wall startWall = walls.get(0);
        Wall firstWall = startWall;


        output.add(startWall);
        while ((startWall = findNextWall(startWall)) != null) {


            output.add(startWall);
            if (startWall.end.equals(firstWall.start)) {
                break;
            }
        }


        walls = output;


    }


    private Wall findNextWall(Wall w) {
        for (int i = 0; i < walls.size(); i++) {
            Wall next = walls.get(i);
            if (w == next) {
                continue;
            }
            if (w.end.equals(next.start)) {

                return next;
            } else if (w.end.equals(next.end)) {

                next.end = next.start;
                next.start = w.end;
                return next;
            }

        }
        return null;
    }


    public void drawGl2(float[] mvpMatrix) {


        // Add program to OpenGL environment
        GLES20.glUseProgram(game.mProgram);

        // get handle to vertex shader's vPosition member

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(game.mPositionHandle);
        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                game.mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                0, bufferedVertex);


        // get handle to fragment shader's vColor member
        // Pass in the color information
        // Set color for drawing the triangle
        GLES20.glUniform4fv(game.mColorHandle, 1, color, 0);


        // Apply the projection and generator transformation
        GLES20.glUniformMatrix4fv(game.mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        SurfaceRenderer.checkGlError("glUniformMatrix4fv");


        GLES20.glDrawArrays(GL10.GL_LINE_LOOP, 0, this.vert.length / 2);

        // Draw the square
        // GLES20.glDrawElements(
        //         GLES20.GL_TRIANGLES, indices.length,
        //        GLES20.GL_UNSIGNED_SHORT, bufferedIndices);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(game.mPositionHandle);


    }



}
