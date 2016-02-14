package pl.slapps.dot.game;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.util.Log;

import pl.slapps.dot.drawing.Wall;
import pl.slapps.dot.model.Config;
import pl.slapps.dot.model.Route;
import pl.slapps.dot.model.Stage;
import pl.slapps.dot.generator.TileRoute;
import pl.slapps.dot.generator.TileRouteFinish;
import pl.slapps.dot.generator.TileRouteStart;

public class Maze {

    private String TAG = Maze.class.getName();


    ArrayList<TileRoute> elements;
    ArrayList<TileRoute> routes;
    private Game game;

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

    private Config config;


    public Path getPath() {
        return path;
    }

    public Fence getFence() {
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


    public Maze(Game game, Stage stage) {
        //this.elements=elements;

        Log.d(TAG, "maze created");


        this.game = game;

        horizontalSize = stage.xMax;

        verticalSize = stage.yMax;

        this.elements = new ArrayList<>();
        this.routes = new ArrayList<>();


        float vertRatio = (float) defaultVerticalSize / (float) verticalSize;
        float horizRatio = (float) defaultHorizontalSize / (float) horizontalSize;

        // float ratio = vertRatio;
        // if (horizRatio < ratio)
        //    ratio = horizRatio;

        // spriteSpeed = generator.spriteSpeed * ratio;


        this.width = game.gameView.screenWidth / horizontalSize;
        this.height = game.gameView.screenHeight / verticalSize;


        for (int i = 0; i < stage.routes.size(); i++) {

            Route element = stage.routes.get(i);

            TileRoute r = null;

            switch (element.type) {

                case FINISH:

                    r = new TileRouteFinish(game.gameView.screenWidth, game.gameView.screenHeight, horizontalSize, verticalSize, element);
                    this.elements.add(r);
                    break;

                case START:
                    r = new TileRouteStart(game.gameView.screenWidth, game.gameView.screenHeight, horizontalSize, verticalSize, element);

                    this.elements.add(r);
                    break;
                default:
                    r = new TileRoute(game.gameView.screenWidth, game.gameView.screenHeight, horizontalSize, verticalSize, element);

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
        path = new Path(routes,  game,stage.config);
        fence = new Fence(routes, game,stage.config);


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


    public void drawGL20(float[] mvpMatrix) {
        path.drawGl2(mvpMatrix);
        fence.drawGl2(mvpMatrix);



        /*
        for (int i = 0; i < elements.size(); i++) {

            if (elements.get(i) != null)
                elements.get(i).draw(gl);
        }
*/

    }



}
