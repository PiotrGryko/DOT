package pl.slapps.dot.drawing;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import javax.microedition.khronos.opengles.GL10;

import pl.slapps.dot.model.Wall;
import pl.slapps.dot.route.Route;

/**
 * Created by piotr on 02.11.15.
 */
public class Fence {

    private String TAG = Fence.class.getName();

    private float vert[];
    private FloatBuffer bufferedVertex;
    private ArrayList<Wall> walls;

    public Fence(ArrayList<Route> routes, String color) {
        walls = new ArrayList<>();
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

        //float[] tabRight = new float[cornersRight.size() * 3];
        for (int i = 0; i < walls.size(); i++) {
            int start = i * 2;
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

        Log.d(TAG, "finished fence " + Arrays.toString(vert));

        ByteBuffer bytes = ByteBuffer.allocateDirect(vert.length * 4);
        bytes.order(ByteOrder.nativeOrder());

        bufferedVertex = bytes.asFloatBuffer();
        bufferedVertex.put(vert);


        bufferedVertex.position(0);


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
                Log.d(TAG,"BREAK!");
                break;
            }
        }


        Log.d(TAG, "walls sorted " + walls.size() + " " + output.size());
        walls = output;

        /*
        for (int i = 0; i < walls.size(); i++) {
            Log.d(TAG, walls.get(i).start.print() + " $$ " + walls.get(i).end.print());
        }
*/
    }


    private Wall findNextWall(Wall w) {
        for (int i = 0; i < walls.size(); i++) {
            Wall next = walls.get(i);
            if (w == next) {
                //Log.d(TAG, "duplicate");
                continue;
            }
            if (w.end.equals(next.start)) {
                //Log.d(TAG, "ok " + w.start.print() + " " + w.end.print());
                //Log.d(TAG, "returned: " + next.start.print() + " " + next.end.print());

                return next;
            } else if (w.end.equals(next.end)) {
                //Log.d(TAG, "switch");

                next.end = next.start;
                next.start = w.end;
                return next;
            }

        }
        //Log.d(TAG,"find next wall returns null");
        return null;
    }


    public void draw(GL10 gl) {


        gl.glColor4f(0.0f, 0.0f, 0.0f, 0.0f);


        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, bufferedVertex);
        gl.glDrawArrays(GL10.GL_LINE_LOOP, 0, this.vert.length / 2);


    }
}
