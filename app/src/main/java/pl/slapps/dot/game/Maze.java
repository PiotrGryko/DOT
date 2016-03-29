package pl.slapps.dot.game;

import java.util.ArrayList;

import android.util.Log;

import pl.slapps.dot.drawing.Quad;
import pl.slapps.dot.drawing.Wall;
import pl.slapps.dot.model.Config;
import pl.slapps.dot.model.Route;
import pl.slapps.dot.model.Stage;
import pl.slapps.dot.generator.builder.TileRoute;
import pl.slapps.dot.generator.builder.TileRouteFinish;
import pl.slapps.dot.generator.builder.TileRouteStart;

public class Maze {

    private String TAG = Maze.class.getName();


    ArrayList<TileRoute> elements;
    ArrayList<TileRoute> routes;
    private Game game;

    public int horizontalSize = 0;
    public int verticalSize = 0;
    public float width;
    public float height;


    private Path path;
    private Fence fence;
    private ArrayList<Coin> coins;
    private Config config;



    public void configure(Config config) {
        this.config = config;
        path.configure(config);
        fence.configure(config);

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
        while ((start = findNextRoute(start)) != null) {

            Log.d("rrr","sort maze ....");
            output.add(start);
            if (start.getType() == Route.Type.FINISH)
                break;

        }

        routes = output;

        initPoints();

    }

    public void initPoints() {
        coins.clear();
        for (int i = 0; i < routes.size(); i++) {
            TileRoute r = routes.get(i);
            if (r.drawCoin) {
                Coin coin = new Coin(game, r.centerX, r.centerY, game.dotSize, game.dotSize);
                coins.add(coin);
            }
        }
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

        config = stage.config;
        this.game = game;
        coins = new ArrayList<>();

        horizontalSize = stage.xMax;
        verticalSize = stage.yMax;

        this.elements = new ArrayList<>();
        this.routes = new ArrayList<>();
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
        path = new Path(routes, game, stage.config);

        fence = new Fence(routes, game, stage.config);



    }


    private TileRoute findTile(int x, int y) {
        for (int i = 0; i < routes.size(); i++) {
            TileRoute t = routes.get(i);
            if (t.horizontalPos == x && t.verticalPos == y)
                return t;
        }
        return null;
    }

    public void configRoute(TileRoute route) {
        TileRoute t = findTile(route.horizontalPos, route.verticalPos);
        if (t != null)
            t.configRoute(route);
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

    public Coin checkCoinCollision(MainSprite sprite) {
        for (Coin c : coins) {
            if (c.checkColision(sprite, config)) {
                coins.remove(c);
                return c;
            }
        }
        return null;
    }

    public Wall.Type checkCollision(float x, float y, float width) {

        TileRoute element = getCurrentRouteObject(x, y);
        if (element == null)
            return null;
        Wall.Type result = element.checkCollision(x, y, width);


        return result;
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

        for (Coin c : coins) {
            c.drawGl2(mvpMatrix);
        }

    }


}
