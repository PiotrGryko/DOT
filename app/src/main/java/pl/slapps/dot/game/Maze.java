package pl.slapps.dot.game;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pl.slapps.dot.drawing.Fence;
import pl.slapps.dot.drawing.Path;
import pl.slapps.dot.model.Route;
import pl.slapps.dot.model.Stage;
import pl.slapps.dot.tile.TileRoute;
import pl.slapps.dot.tile.TileRouteFinish;
import pl.slapps.dot.tile.TileRouteStart;

public class Maze {

    private String TAG = Maze.class.getName();


    ArrayList<TileRoute> elements;
    ArrayList<TileRoute> routes;
    private GameView view;

    public int horizontalSize = 0;
    public int verticalSize = 0;
    public float width;
    public float height;
   // public float spriteSpeed;

    public int defaultVerticalSize = 15;
    public int defaultHorizontalSize = 9;

    public float[] wallsVert;
    public FloatBuffer wallsBufferedVertex;

    private Path path;
    private Fence fence;


    public Path getPath()
    {
        return path;
    }
    public Fence getFence()
    {
        return fence;
    }

    public TileRoute getStartRoute() {
        for (int i = 0; i < routes.size(); i++) {

            TileRoute r = routes.get(i);
            if (r == null)
                continue;
            if (r.getType() == Route.Type.START)
                return r;
        }


        return null;
    }

    public void sortMaze() {
        ArrayList<TileRoute> output = new ArrayList<>();
        TileRoute start = getStartRoute();
        output.add(start);
        TileRoute old = null;
        while ((start = findNextRoute(start)) != null) {

            output.add(start);
            if (start.getType() == Route.Type.FINISH)
                break;

        }
        Log.d(TAG, "maze sorted " + routes.size() + " " + output.size());

        routes = output;


    }

    private TileRoute findNextRoute(TileRoute t) {
        TileRoute next = null;
        switch (t.to) {
            case TOP:
                next = findRoute(t.horizontalPos, t.verticalPos - 1);
                break;
            case LEFT:
                next = findRoute(t.horizontalPos - 1, t.verticalPos);
                break;
            case RIGHT:
                next = findRoute(t.horizontalPos + 1, t.verticalPos);
                break;
            case BOTTOM:
                next = findRoute(t.horizontalPos, t.verticalPos + 1);
                break;
        }


        return next;
    }

    private TileRoute findRoute(int x, int y) {
        for (int i = 0; i < routes.size(); i++) {
            TileRoute t = routes.get(i);
            if (t.horizontalPos == x && t.verticalPos == y)
                return t;
        }
        return null;
    }


    public Maze(GameView view, Stage stage) {
        //this.elements=elements;

        Log.d(TAG, "maze created");


        this.view = view;

        horizontalSize = stage.xMax;

        verticalSize = stage.yMax;

        this.elements = new ArrayList<>();
        this.routes = new ArrayList<>();


        float vertRatio = (float) defaultVerticalSize / (float) verticalSize;
        float horizRatio = (float) defaultHorizontalSize / (float) horizontalSize;

       // float ratio = vertRatio;
       // if (horizRatio < ratio)
        //    ratio = horizRatio;

       // spriteSpeed = view.spriteSpeed * ratio;


        this.width = view.screenWidth / horizontalSize;
        this.height = view.screenHeight / verticalSize;


        for (int i = 0; i < stage.routes.size(); i++) {

            Route element = stage.routes.get(i);

            TileRoute r = null;

            switch (element.type) {

                case FINISH:

                    r = new TileRouteFinish(view.screenWidth, view.screenHeight, horizontalSize, verticalSize, view, element);
                    this.elements.add(r);
                    break;

                case START:
                    r = new TileRouteStart(view.screenWidth, view.screenHeight, horizontalSize, verticalSize, element);

                    this.elements.add(r);
                    break;
                default:
                    r = new TileRoute(view.screenWidth, view.screenHeight, horizontalSize, verticalSize, element);

                    this.elements.add(r);

                    break;

            }
        }

        for (int i = 0; i < elements.size(); i++) {

            TileRoute element = elements.get(i);
            if (element == null)
                continue;
            if (element.getType() == Route.Type.ROUTE || element.getType() == Route.Type.START || element.getType() == Route.Type.FINISH)
                routes.add(element);

        }
        sortMaze();
        Log.d(TAG, "elements loaded ");
        //initWallsDrawing();
        path = new Path(routes, stage.colorRoute);
        fence = new Fence(routes, "#000000");


    }


    public TileRoute getCurrentRouteObject(float x, float y) {

        for (int i = 0; i < routes.size(); i++) {

            TileRoute r = routes.get(i);
            if (r != null && r.contains(x, y)) {

                return routes.get(i);
            }

        }

        return null;
    }


    public Wall.Type checkCollision(float x, float y, float width) {


        TileRoute element = getCurrentRouteObject(x, y);
        if (element == null)
            return null;

        Wall.Type result = element.checkCollision(x, y, width);
        if (result != null)
            return result;


        return null;
    }

    public TileRoute checkRouteCollision(float x, float y, float width) {
        TileRoute element = getCurrentRouteObject(x, y);
        if (element == null)
            return null;
        Wall.Type result = element.checkCollision(x, y, width);

        if (result != null)
            return element;


        return null;
    }


    public void draw(GL10 gl) {
        path.draw(gl);

        fence.draw(gl);

        /*
        for (int i = 0; i < elements.size(); i++) {

            if (elements.get(i) != null)
                elements.get(i).draw(gl);
        }
*/

    }

}
