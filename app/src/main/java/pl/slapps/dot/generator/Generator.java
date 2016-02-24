package pl.slapps.dot.generator;

import android.graphics.Color;
import android.opengl.GLES20;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import pl.slapps.dot.DAO;
import pl.slapps.dot.SurfaceRenderer;
import pl.slapps.dot.generator.gui.GeneratorLayout;
import pl.slapps.dot.generator.widget.PathPopup;
import pl.slapps.dot.model.Config;
import pl.slapps.dot.model.Route;
import pl.slapps.dot.model.Stage;
import pl.slapps.dot.model.World;

/**
 * Created by piotr on 18.10.15.
 */
public class Generator {

    private String TAG = Generator.class.getName();


    public ArrayList<TileRoute> tiles;


    public int gridX;
    public int gridY;
    public String name = "generated stage";
    public String description = "generated desc";

    public SurfaceRenderer view;

    public String _id;

    private float a;
    private float r;
    private float g;
    private float b;

    private Config config;

    private boolean runPreview;

    private PathPopup pathPopup;

    public PathPopup getPathPopup() {
        return pathPopup;
    }

    public Config getConfig() {
        return config;
    }

    public void configure(Config config) {
        try {
            this.config = config;

            String color = config.colors.colorBackground;
            if (config.settings.switchBackgroundColors)
                color = config.colors.colorSwitchBackgroundStart;

            int intColor = Color.parseColor(color);

            a = (float) Color.alpha(intColor) / 255;
            r = (float) Color.red(intColor) / 255;
            g = (float) Color.green(intColor) / 255;
            b = (float) Color.blue(intColor) / 255;

        } catch (Throwable t) {
            Log.d(TAG, "background color  null ");
        }
    }


    public int mPositionHandle;
    public int mColorHandle;
    public int mMVPMatrixHandle;

    private GeneratorLayout layout;


    public Generator(final SurfaceRenderer view, int width, int gridY) {
        //this.elements=elements;
        this.config = new Config();
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


        layout = new GeneratorLayout(view.context, this, tiles.get(10));

        this.pathPopup = new PathPopup(this);

        this.view.context.drawer.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                pathPopup.onDrawerSlide(slideOffset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                pathPopup.dissmiss();
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });


    }

    public void reset() {
        //int currentX = this.getLayout().tile.horizontalPos;
        //int currentY = this.getLayout().tile.verticalPos;
        view.getGame().setPreview(false);
        getPathPopup().dissmiss();
        initGrid(9, 15);
        configure(new Config());
        getLayout().setCurrentWorld(null);

        getLayout().tile = tiles.get(10);
        //getLayout().tile.setCurrentTile(true);

    }


    public static final String generatorVertexShader =
            "uniform mat4 uMVPMatrix;      " +
                    // A constant representing the combined model/generator/projection matrix
                    "attribute vec4 vPosition;     " +
                    // Per-vertex position information we will pass in.
                    // Per-vertex color information we will pass in.
                    // This will be passed into the fragment shader.
                    // This will be passed into the fragment shader.
                    // The entry point for our vertex shader.
                    "void main()" +
                    "{" +
                    // Transform the vertex into eye space.
                    // Pass through the color.

                    // gl_Position is a special variable used to store the final position.
                    // Multiply the vertex by the matrix to get the final point in normalized screen coordinates. +
                    "    gl_Position = uMVPMatrix * vPosition;" +
                    "}";

    public static final String generatorFragmentShader =


            "precision mediump float;       // Set the default precision to medium. We don't need as high of a\n" +
                    "                               // precision in the fragment shader.\n" +
                    "uniform vec4 vColor;          // This is the color from the vertex shader interpolated across the\n" +
                    "                               // triangle per fragment.\n" +


                    "// The entry point for our fragment shader.\n" +
                    "void main()\n" +
                    "{" +


                    "    gl_FragColor = vColor;" +
                    "}";


    private int mGeneratorProgram;

    public void initGeneratorShaders() {


        int genVertexShader = SurfaceRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER,
                generatorVertexShader);
        int genFragmentShader = SurfaceRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                generatorFragmentShader);

        mGeneratorProgram = view.createAndLinkProgram(genVertexShader, genFragmentShader,
                new String[]{"vPosition"});


        mPositionHandle = GLES20.glGetAttribLocation(mGeneratorProgram, "vPosition");
        mColorHandle = GLES20.glGetUniformLocation(mGeneratorProgram, "vColor");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mGeneratorProgram, "uMVPMatrix");

        configure(config);


    }


    public GeneratorLayout getLayout() {
        return layout;
    }

    public void initGrid(int width, int height) {
        this.gridX = width;
        this.gridY = height;


        this.tiles = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                this.tiles.add(new TileRoute(view.screenWidth, view.screenHeight, width, height, j, i, "LEFT", "RIGHT", Route.Type.TILE, this));

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

        Log.d("zzz", "refreash maze");
        configure(config);

        if (runPreview) {
            view.getGame().getCurrentStage().config = config;
            view.getGame().configure();
        } else {

            for (int i = 0; i < tiles.size(); i++) {
                TileRoute t = tiles.get(i);
                if (t.getType() != Route.Type.TILE && t.getType() != Route.Type.BLOCK && t.getType() != Route.Type.FILL) {
                    t.configure(config);

                }

            }
        }


    }


    private JSONObject dumpMaze() {
        startRouteConfiguration();

        JSONObject output = new JSONObject();


        try {
            output.put("name", name);

            output.put("description", description);
            output.put("y_max", gridY);
            output.put("x_max", gridX);


            output.put("colors", config.colors.toJson());
            output.put("sounds", config.sounds.toJson());
            output.put("settings", config.settings.toJson());


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
                    step.put("ratio", t.speedRatio);
                    if (t.sound != null)
                        step.put("sound", t.sound);


                    route.put(step);
                }

            }
            output.put("route", route);
            output.put("world_id", layout.getCurrentWorld().id);
            int maxLogSize = 1000;

            for (int i = 0; i <= output.toString().length() / maxLogSize; i++) {
                int start = i * maxLogSize;
                int end = (i + 1) * maxLogSize;
                end = end > output.toString().length() ? output.toString().length() : end;
                Log.v(TAG, output.toString().substring(start, end));
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }
        return output;
    }

    public void saveMaze() {


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

            Toast.makeText(generator.context, "Stage saved!", Toast.LENGTH_LONG).show();
*/

        DAO.addStage(view.context, dumpMaze(), new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                Log.d(TAG, response.toString());
                Toast.makeText(view.context, "Stage saved!", Toast.LENGTH_LONG).show();
            }
        }, _id);


    }

    public void loadWorld(World currentWorld) {


        config.sounds.soundBackground = currentWorld.soundBackground;
        config.sounds.soundCrash = currentWorld.soundCrash;
        config.sounds.soundPress = currentWorld.soundPress;
        config.sounds.soundFinish = currentWorld.soundFinish;
        config.colors.colorBackground = currentWorld.colorBackground;
        config.colors.colorShip = currentWorld.colorShip;
        config.colors.colorExplosionEnd = currentWorld.colorExplosionEnd;
        config.colors.colorExplosionStart = currentWorld.colorExplosionStart;
        config.colors.colorRoute = currentWorld.colorRoute;


        configure(config);

    }

    public void loadRoute(Stage maze) {
        //this.elements=elements;


        tiles.clear();

        for (int i = 0; i < gridY; i++) {
            for (int j = 0; j < gridX; j++) {
                this.tiles.add(new TileRoute(view.screenWidth, view.screenHeight, gridX, gridY, j, i, "LEFT", "RIGHT", Route.Type.TILE, this));

            }
        }


        gridX = maze.xMax;
        gridY = maze.yMax;

        _id = maze.id;

        initGrid((int) gridX, (int) gridY);

        config = maze.config;


        for (int i = 0; i < maze.routes.size(); i++) {
            Route element = maze.routes.get(i);

            Route.Type t = element.type;

            TileRoute tile = findTile(element.x, element.y);

            switch (t) {

                case FINISH:
                    tiles.remove(tile);
                    this.tiles.add(new TileRouteFinish(view.screenWidth, view.screenHeight, gridX, gridY, element, this));
                    break;

                case START:
                    tiles.remove(tile);
                    this.tiles.add(new TileRouteStart(view.screenWidth, view.screenHeight, gridX, gridY, element, this));
                    break;
                default:
                    tiles.remove(tile);
                    this.tiles.add(new TileRoute(view.screenWidth, view.screenHeight, gridX, gridY, element, this));
                    break;

            }
        }

        refreashMaze();
        Log.d(TAG, "stage loaded ");


    }


    public boolean onTouch(MotionEvent event) {

        if (getLayout().getCurrentWorld() == null)
            return true;

        if (runPreview) {
            view.getGame().onTouchEvent(event);
        } else {
            float x = event.getX();
            float y = event.getY();


            for (int i = 0; i < tiles.size(); i++) {
                TileRoute t = tiles.get(i);
                if (t.contains(x, y)) {
                    layout.setCurrentTile(t);

                    pathPopup.show(event.getX(), event.getY());

                    return true;
                }
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

    public boolean getPreview() {
        return runPreview;
    }


    public void startPreview() {
        view.getGame().setPreview(true);
        view.getGame().initStage(Stage.valueOf(dumpMaze()));
        //view.context.drawer.closeDrawer(view.context.drawerContent);
        runPreview = true;
        getLayout().showPreviewControlls();

    }

    public void stopPreview() {
        view.getGame().setPreview(false);
        runPreview = false;
        refreashMaze();
        getLayout().showGeneratorConstrolls();


    }

    public void onDraw(float[] mMVPMatrix) {


        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);


        if (!runPreview) {
            GLES20.glClearColor(r, g, b, a);
            GLES20.glUseProgram(mGeneratorProgram);

            if (tiles == null)
                return;
            for (int i = 0; i < tiles.size(); i++) {

                if (tiles.size() > i && tiles.get(i) != null)
                    tiles.get(i).drawGL20(mMVPMatrix);
            }
        } else {
            view.getGame().onDraw(mMVPMatrix);
        }
    }


}
