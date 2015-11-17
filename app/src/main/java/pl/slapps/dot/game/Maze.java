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
import pl.slapps.dot.model.Wall;
import pl.slapps.dot.route.Route;
import pl.slapps.dot.route.RouteFinish;
import pl.slapps.dot.route.RouteStart;

public class Maze {

    private String TAG = Maze.class.getName();


    ArrayList<Route> elements;
    ArrayList<Route> routes;
    private GameView view;

    public int horizontalSize = 0;
    public int verticalSize = 0;
    public float width;
    public float height;
    public float spriteSpeed;

    public int defaultVerticalSize = 15;
    public int defaultHorizontalSize = 9;

    public float[] wallsVert;
    public FloatBuffer wallsBufferedVertex;

    private Path path;
    private Fence fence;


    public Route getStartRoute() {
        for (int i = 0; i < routes.size(); i++) {

            Route r = routes.get(i);
            if (r == null)
                continue;
            if (r.getType() == Route.Type.START)
                return r;
        }


        return null;
    }

    public void sortMaze() {
        ArrayList<Route> output = new ArrayList<>();
        Route start = getStartRoute();
        output.add(start);
        Route old = null;
        while ((start = findNextRoute(start)) != null) {

            output.add(start);
            if(start.getType()== Route.Type.FINISH)
                break;

        }
        Log.d(TAG, "maze sorted " + routes.size() + " " + output.size());

        routes = output;


    }

    private Route findNextRoute(Route t) {
        Route next = null;
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

    private Route findRoute(int x, int y) {
        for (int i = 0; i < routes.size(); i++) {
            Route t = routes.get(i);
            if (t.horizontalPos == x && t.verticalPos == y)
                return t;
        }
        return null;
    }


    public Maze(GameView view, JSONObject maze) {
        //this.elements=elements;

        Log.d(TAG, "maze created");

        try {
            this.view = view;

            horizontalSize = maze.has("x_max") ? maze.getInt("x_max") : 0;

            verticalSize = maze.has("y_max") ? maze.getInt("y_max") : 0;

            this.elements = new ArrayList<>();
            this.routes = new ArrayList<>();

            JSONObject colors = maze.has("colors") ? maze.getJSONObject("colors") : new JSONObject();
            String routeColor = colors.has("route") ? colors.getString("route") : "#C8C8C8";

            float vertRatio = (float) defaultVerticalSize / (float) verticalSize;
            float horizRatio = (float) defaultHorizontalSize / (float) horizontalSize;

            float ratio = vertRatio;
            if (horizRatio < ratio)
                ratio = horizRatio;

            spriteSpeed = view.spriteSpeed * ratio;


            this.width = view.screenWidth / horizontalSize;
            this.height = view.screenHeight / verticalSize;

            JSONArray jsonRoutes = maze.has("route") ? maze.getJSONArray("route") : new JSONArray();

            for (int i = 0; i < jsonRoutes.length(); i++) {
                JSONObject element = jsonRoutes.getJSONObject(i);
                String type = element.has("type") ? element.getString("type") : "ROUTE";

                Route.Type t = Route.Type.valueOf(type);

                Route r = null;

                switch (t) {

                    case FINISH:
                        r = new RouteFinish(view.screenWidth, view.screenHeight, horizontalSize, verticalSize, view, element, routeColor);
                        this.elements.add(r);
                        break;

                    case START:
                        r = new RouteStart(view.screenWidth, view.screenHeight, horizontalSize, verticalSize, element, routeColor);
                        this.elements.add(r);
                        break;
                    default:
                        r = new Route(view.screenWidth, view.screenHeight, horizontalSize, verticalSize, element, routeColor);
                        this.elements.add(r);

                        break;

                }
            }

            for (int i = 0; i < elements.size(); i++) {

                Route element = elements.get(i);
                if (element == null)
                    continue;
                if (element.getType() == Route.Type.ROUTE || element.getType() == Route.Type.START || element.getType() == Route.Type.FINISH)
                    routes.add(element);

            }
            sortMaze();
            Log.d(TAG, "elements loaded ");
            //initWallsDrawing();
            path = new Path(routes, routeColor);
            fence = new Fence(routes, routeColor);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    public Route getCurrentRouteObject(float x, float y) {

        for (int i = 0; i < routes.size(); i++) {

            Route r = routes.get(i);
            if (r != null && r.contains(x, y)) {

                return routes.get(i);
            }

        }

        return null;
    }


    public Wall.Type checkCollision(float x, float y, float width) {


        Route element = getCurrentRouteObject(x, y);
        if (element == null)
            return null;

        Wall.Type result = element.checkCollision(x, y, width);
        if (result != null)
            return result;


        return null;
    }

    public Route checkRouteCollision(float x, float y, float width) {
        Route element = getCurrentRouteObject(x, y);
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
