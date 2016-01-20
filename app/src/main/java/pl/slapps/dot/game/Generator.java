package pl.slapps.dot.game;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import pl.slapps.dot.DAO;
import pl.slapps.dot.GeneratorLayout;
import pl.slapps.dot.model.Route;
import pl.slapps.dot.model.Stage;
import pl.slapps.dot.model.World;
import pl.slapps.dot.tile.TileRoute;
import pl.slapps.dot.tile.TileRouteFinish;
import pl.slapps.dot.tile.TileRouteStart;

/**
 * Created by piotr on 18.10.15.
 */
public class Generator {

    private String TAG = Generator.class.getName();


    public ArrayList<TileRoute> tiles;


    public int gridX;
    public int gridY;
    public GameView view;

    public String _id;

    public String name = "generated stage";
    public String description = "generated desc";

    public String backgroundColor;
    public String fillColor;

    public String dotColor = "#000000";
    public String routeColor = "#8b2323";
    public String blockColor = "#8b2323";

    public String explosionStartColor = "#000000";
    public String explosionEndColor = "#FFFFFF";

    public String backgrounSound = "";
    public String pressSound = "";
    public String crashSound = "";
    public String finishSound = "";

    public String routeSound = "";

    public TileRoute mCurrentSelectedRoad;
    private GeneratorLayout layout;


    public Generator(final GameView view, int width, int gridY) {
        //this.elements=elements;
        this.view = view;
        this.view.context.mainMenu.btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GeneratorDialog.showGeneratorMenuDialog(Generator.this);
                view.context.toggleMenu();
            }
        });

        initGrid(width, gridY);

        Log.d(TAG, "generator loaded " + this.tiles.size());
        layout = new GeneratorLayout(view.context, tiles.get(tiles.size() / 2));


    }

    public GeneratorLayout getLayout() {
        return layout;
    }

    public void initGrid(int width, int height) {
        this.gridX = width;
        this.gridY = height;


        this.tiles = new ArrayList<>();
        this.backgroundColor = this.fillColor = view.getGameBackground().backgroundcolor;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                this.tiles.add(new TileRoute(view.screenWidth, view.screenHeight, width, height, j, i, "LEFT", "RIGHT", Route.Type.TILE));

            }
        }
    }

    public TileRoute findTile(int x, int y) {
        for (int i = 0; i < tiles.size(); i++) {
            TileRoute t = tiles.get(i);
            if (t.horizontalPos == x && t.verticalPos == y)
                return t;
        }
        return null;
    }

    public void startRouteConfiguration() {
        ArrayList<TileRoute> routes = new ArrayList<>();
        TileRoute startRoute = null;
        TileRoute nextRoute = null;
        Route.Direction from = null;
        for (int i = 0; i < tiles.size(); i++) {
            TileRoute t = tiles.get(i);
            if (t.getType() == Route.Type.START) {
                startRoute = t;
                break;
            }
        }

        if (startRoute != null) {
            Route.Movement movement = startRoute.getDirection();
            switch (movement) {
                case LEFTRIGHT:
                    from = Route.Direction.LEFT;
                    nextRoute = findTile(startRoute.horizontalPos + 1, startRoute.verticalPos);
                    break;

                case RIGHTLEFT:
                    from = Route.Direction.RIGHT;
                    nextRoute = findTile(startRoute.horizontalPos - 1, startRoute.verticalPos);
                    break;
                case BOTTOMTOP:
                    from = Route.Direction.BOTTOM;
                    nextRoute = findTile(startRoute.horizontalPos, startRoute.verticalPos - 1);
                    break;
                case TOPBOTTOM:
                    from = Route.Direction.TOP;
                    nextRoute = findTile(startRoute.horizontalPos, startRoute.verticalPos + 1);
                    break;


            }
            Log.d(TAG, "config started ");
            Log.d(TAG, "route configured from:" + startRoute.from.name() + " to: " + startRoute.to.name() + " movement: " + startRoute.getDirection().name() + " type: " + startRoute.getType().name());

            configRoute(nextRoute, from, routes);


            Log.d(TAG, "config end  " + routes.size());

            for (int i = 0; i < routes.size(); i++) {
                routes.get(i).next = getNextMove(i, routes);

            }

            Log.d(TAG, "next moves set   " + routes.size());

            for (int i = 0; i < routes.size(); i++) {
                Log.d(TAG, "next move : " + i + " " + routes.get(i).next);
            }


        }


    }

    public ArrayList<TileRoute> getPath() {
        ArrayList<TileRoute> path = new ArrayList<>();
        TileRoute startRoute = getStartRoute();


        while (startRoute != null) {
            if (startRoute.getType() == Route.Type.TILE)
                break;
            path.add(startRoute);
            if (startRoute.getType() == Route.Type.FINISH)
                break;
            Route.Movement movement = startRoute.getDirection();
            switch (movement) {
                case LEFTRIGHT:
                case TOPRIGHT:
                case BOTTOMRIGHT:
                    startRoute = findTile(startRoute.horizontalPos + 1, startRoute.verticalPos);
                    break;

                case RIGHTLEFT:
                case TOPLEFT:
                case BOTTOMLEFT:
                    startRoute = findTile(startRoute.horizontalPos - 1, startRoute.verticalPos);
                    break;
                case BOTTOMTOP:
                case RIGHTTOP:
                case LEFTTOP:
                    startRoute = findTile(startRoute.horizontalPos, startRoute.verticalPos - 1);
                    break;
                case TOPBOTTOM:
                case LEFTBOTTOM:
                case RIGHTBOTTOM:
                    startRoute = findTile(startRoute.horizontalPos, startRoute.verticalPos + 1);
                    break;
                default:
                    startRoute = null;


            }
        }
        for (int i = 0; i < tiles.size(); i++) {
            if (tiles.get(i).getType() == Route.Type.ROUTE || tiles.get(i).getType() == Route.Type.FINISH || tiles.get(i).getType() == Route.Type.START) {
                if (!path.contains(tiles.get(i)))
                    path.add(tiles.get(i));
            }
        }

        return path;
    }

    private void configRoute(TileRoute route, Route.Direction target_from, ArrayList<TileRoute> routes) {
        //route.from=from;


        Route.Movement t = route.getDirection();
        Route.Direction target_to = route.to;
        int targetX = route.horizontalPos;
        int targetY = route.verticalPos;

        Route.Direction nextFrom = target_from;

        //Log.d(TAG, "config route from: " + target_from);

        switch (target_from) {
            case LEFT:

                switch (t) {
                    case RIGHTLEFT:
                    case LEFTRIGHT:
                        target_to = Route.Direction.RIGHT;
                        nextFrom = Route.Direction.LEFT;
                        targetX++;
                        break;
                    case BOTTOMLEFT:
                    case LEFTBOTTOM:
                        target_to = Route.Direction.BOTTOM;
                        nextFrom = Route.Direction.TOP;
                        targetY++;
                        break;
                    case TOPLEFT:
                    case LEFTTOP:
                        targetY--;
                        nextFrom = Route.Direction.BOTTOM;
                        target_to = Route.Direction.TOP;
                        break;

                }
                break;
            case RIGHT:
                switch (t) {
                    case LEFTRIGHT:
                    case RIGHTLEFT:
                        targetX--;

                        nextFrom = Route.Direction.RIGHT;
                        target_to = Route.Direction.LEFT;
                        break;
                    case BOTTOMRIGHT:
                    case RIGHTBOTTOM:
                        targetY++;

                        nextFrom = Route.Direction.TOP;
                        target_to = Route.Direction.BOTTOM;
                        break;
                    case TOPRIGHT:
                    case RIGHTTOP:
                        targetY--;

                        nextFrom = Route.Direction.BOTTOM;
                        target_to = Route.Direction.TOP;
                        break;

                }
                break;
            case TOP:

                switch (t) {
                    case BOTTOMTOP:
                    case TOPBOTTOM:
                        targetY++;
                        nextFrom = Route.Direction.TOP;
                        target_to = Route.Direction.BOTTOM;
                        break;
                    case LEFTTOP:
                    case TOPLEFT:
                        targetX--;
                        nextFrom = Route.Direction.RIGHT;
                        target_to = Route.Direction.LEFT;
                        break;
                    case RIGHTTOP:
                    case TOPRIGHT:
                        targetX++;
                        nextFrom = Route.Direction.LEFT;
                        target_to = Route.Direction.RIGHT;
                        break;

                }
                break;
            case BOTTOM:
                switch (t) {
                    case TOPBOTTOM:
                    case BOTTOMTOP:
                        nextFrom = Route.Direction.BOTTOM;
                        target_to = Route.Direction.TOP;
                        targetY--;

                        break;
                    case LEFTBOTTOM:
                    case BOTTOMLEFT:
                        nextFrom = Route.Direction.RIGHT;
                        target_to = Route.Direction.LEFT;
                        targetX--;

                        break;
                    case RIGHTBOTTOM:
                    case BOTTOMRIGHT:
                        nextFrom = Route.Direction.LEFT;
                        target_to = Route.Direction.RIGHT;
                        targetX++;

                        break;

                }
                break;


        }
        route.from = target_from;
        route.to = target_to;
        routes.add(route);
        Log.d(TAG, "route configured from:" + target_from + " to: " + target_to + " movement: " + route.getDirection().name() + " type: " + route.getType().name());

        TileRoute nextRoute = findTile(targetX, targetY);

        if (route.getType() == Route.Type.FINISH)
            return;

        if (nextRoute != null && nextRoute.getType() != Route.Type.TILE && nextRoute != route) {
            configRoute(nextRoute, nextFrom, routes);
        }


    }

    public Route.Movement getNextMove(int start, ArrayList<TileRoute> routes) {


        for (int i = start; i < routes.size(); i++) {

            Route.Movement type = routes.get(i).getDirection();
            if (type != Route.Movement.LEFTRIGHT && type != Route.Movement.RIGHTLEFT && type != Route.Movement.BOTTOMTOP && type != Route.Movement.TOPBOTTOM)
                return type;
            else {
                continue;
            }

        }
        return null;
    }


    public TileRoute getStartRoute() {
        for (int i = 0; i < tiles.size(); i++) {
            TileRoute t = tiles.get(i);
            if (t.getType() == Route.Type.START) {
                return t;

            }

        }
        return null;
    }

    public void refreashMaze() {
        view.getGameBackground().setColor(backgroundColor);

        for (int i = 0; i < tiles.size(); i++) {
            TileRoute t = tiles.get(i);
            if (t.getType() != Route.Type.TILE && t.getType() != Route.Type.BLOCK && t.getType() != Route.Type.FILL) {
                t.setRouteColor(routeColor);

            }

        }
    }


    public void saveMaze() {


        startRouteConfiguration();

        JSONObject output = new JSONObject();


        try {
            output.put("name", name);

            output.put("description", description);
            output.put("y_max", gridY);
            output.put("x_max", gridX);

            JSONObject colors = new JSONObject();
            colors.put("ship", dotColor);
            colors.put("background", backgroundColor);
            colors.put("explosion_start", explosionStartColor);
            colors.put("explosion_end", explosionEndColor);
            colors.put("route", routeColor);
            output.put("colors", colors);

            JSONObject sounds = new JSONObject();
            sounds.put("background", backgrounSound);
            sounds.put("press", pressSound);
            sounds.put("crash", crashSound);
            sounds.put("finish", finishSound);
            output.put("sounds", sounds);

            Log.d(TAG, output.toString());


            JSONArray route = new JSONArray();
            for (int i = 0; i < tiles.size(); i++) {
                TileRoute t = tiles.get(i);
                if (t.getType() != Route.Type.TILE) {
                    JSONObject step = new JSONObject();
                    step.put("type", t.getType().name());
                    if (t.next != null)
                        step.put("next", t.next.name());

                    step.put("x", t.horizontalPos);
                    step.put("y", t.verticalPos);
                    step.put("from", t.from);
                    step.put("to", t.to);
                    step.put("background_color", t.backgroundColor);
                    step.put("ratio",t.speedRatio);
                    if (t.sound != null)
                        step.put("sound", t.sound);


                    route.put(step);
                }

            }
            output.put("route", route);
            output.put("world_id", layout.getCurrentWorld().id);

            //Stages.stages.add(output.toString());

            /*

            World currentWorld = layout.getCurrentWorld();
            ArrayList<Stage> stages = currentWorld.stages;
            if (stages == null)
                stages = new ArrayList<>();


            stages.add(Stage.valueOf(output));
            currentWorld.stages=stages;
            //layout.addWorld(currentWorld);
            layout.updateWorld();

            Toast.makeText(view.context, "Stage saved!", Toast.LENGTH_LONG).show();
*/

            DAO.addStage(view.context, output, new Response.Listener() {
                @Override
                public void onResponse(Object response) {
                    Log.d(TAG, response.toString());
                    Toast.makeText(view.context, "Stage saved!", Toast.LENGTH_LONG).show();
                }
            }, _id);
            int maxLogSize = 1000;


            for (int i = 0; i <= output.toString().length() / maxLogSize; i++) {
                int start = i * maxLogSize;
                int end = (i + 1) * maxLogSize;
                end = end > output.toString().length() ? output.toString().length() : end;
                Log.v(TAG, output.toString().substring(start, end));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void loadWorld(World currentWorld)
    {
        backgrounSound=currentWorld.soundBackground;
        crashSound=currentWorld.soundCrash;
        pressSound=currentWorld.soundPress;
        finishSound=currentWorld.soundFinish;
        backgroundColor=currentWorld.colorBackground;
        dotColor=currentWorld.colorShip;
        explosionEndColor=currentWorld.colorExplosionEnd;
        explosionStartColor=currentWorld.colorExplosionStart;
        routeColor=currentWorld.colorRoute;
        Log.d(TAG,"load world "+backgroundColor);
        Log.d(TAG,"load world json "+currentWorld.toJson().toString());


        view.getGameBackground().setColor(backgroundColor);

    }

    public void loadRoute(Stage maze) {
        //this.elements=elements;


        tiles.clear();

        for (int i = 0; i < gridY; i++) {
            for (int j = 0; j < gridX; j++) {
                this.tiles.add(new TileRoute(view.screenWidth, view.screenHeight, gridX, gridY, j, i, "LEFT", "RIGHT", Route.Type.TILE));

            }
        }


        gridX = maze.xMax;

        gridY = maze.yMax;

        _id = maze.id;

        initGrid((int) gridX, (int) gridY);


        routeColor = maze.colorRoute;
        backgroundColor = maze.colorBackground;
        dotColor = maze.colorShip;
        explosionStartColor = maze.colorExplosionStart;

        explosionEndColor = maze.colorExplosionEnd;

        backgrounSound = maze.sounBackground;
        pressSound = maze.soundPress;
        crashSound = maze.soundCrash;
        finishSound = maze.soundFinish;


        for (int i = 0; i < maze.routes.size(); i++) {
            Route element = maze.routes.get(i);

            Route.Type t = element.type;

            TileRoute tile = findTile(element.x, element.y);

            switch (t) {

                case FINISH:
                    tiles.remove(tile);
                    this.tiles.add(new TileRouteFinish(view.screenWidth, view.screenHeight, gridX, gridY, view, element));
                    break;

                case START:
                    tiles.remove(tile);
                    this.tiles.add(new TileRouteStart(view.screenWidth, view.screenHeight, gridX, gridY, element));
                    break;
                default:
                    tiles.remove(tile);
                    this.tiles.add(new TileRoute(view.screenWidth, view.screenHeight, gridX, gridY, element));
                    break;

            }
        }

        refreashMaze();
        Log.d(TAG, "stage loaded ");


    }


    public boolean onTouch(MotionEvent event) {


        Log.d(TAG, "ont touch " + event.getX());

        for (int i = 0; i < tiles.size(); i++) {
            TileRoute t = tiles.get(i);
            if (t.contains(event.getX(), event.getY())) {
                //showGeneratorMenuDialog(t);
                //GeneratorDialog.showPathDialog(this, t, null);
                layout.setCurrentTile(t);

                return true;
            }
        }


        return true;
    }

    public boolean contains(float x, float y) {
        for (int i = 0; i < tiles.size(); i++) {
            TileRoute element = tiles.get(i);

            if (element.contains(x, y))
                return true;

        }
        return false;
    }


    public void draw(GL10 gl) {
        if (tiles == null)
            return;
        for (int i = 0; i < tiles.size(); i++) {

            if (tiles.size()>i && tiles.get(i) != null)
                tiles.get(i).draw(gl);
        }

    }

}
