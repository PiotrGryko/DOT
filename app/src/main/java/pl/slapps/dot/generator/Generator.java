package pl.slapps.dot.generator;

import android.app.Dialog;
import android.graphics.Color;
import android.opengl.GLES20;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pl.slapps.dot.DAO;
import pl.slapps.dot.MainActivity;
import pl.slapps.dot.SoundsService;
import pl.slapps.dot.SurfaceRenderer;
import pl.slapps.dot.generator.builder.PathBuilderDraw;
import pl.slapps.dot.generator.builder.PathBuilderPopup;
import pl.slapps.dot.generator.builder.TileRoute;
import pl.slapps.dot.generator.builder.TileRouteFinish;
import pl.slapps.dot.generator.builder.TileRouteManager;
import pl.slapps.dot.generator.builder.TileRouteStart;
import pl.slapps.dot.generator.gui.GeneratorLayout;
import pl.slapps.dot.generator.widget.PopupLayoutFactory;
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

    private TileRouteManager tileRouteManager;


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

    private PopupLayoutFactory popupFactory;
    private PathBuilderPopup pathBuilderPopup;
    private PathBuilderDraw pathBuilderDraw;





    public PopupLayoutFactory getPathPopup() {
        return popupFactory;
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

    public TileRouteManager getTileRouteManager()
    {
        return tileRouteManager;
    }

    public PathBuilderPopup getPathBuilderPopup()
    {
        return pathBuilderPopup;
    }

    public PathBuilderDraw getPathBuilderDraw()
    {
        return pathBuilderDraw;
    }
    public Generator(final SurfaceRenderer view, int width, int gridY) {
        //this.elements=elements;
        this.config = new Config();
        this.view = view;

        tileRouteManager = new TileRouteManager(this);
        pathBuilderDraw =new PathBuilderDraw(this);

        pathBuilderPopup =new PathBuilderPopup(this);
        initGrid(width, gridY);

        Log.d(TAG, "generator loaded " + this.tiles.size());


        layout = new GeneratorLayout(view.context, this, tiles.get(10));

        this.popupFactory = new PopupLayoutFactory(this);




/*
        this.view.context.drawer.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                pathPopup.onDrawerSlide(slideOffset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                pathPopup.dissmissPath();
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
*/
    }




    public void reset() {
        //int currentX = this.getLayout().tile.horizontalPos;
        //int currentY = this.getLayout().tile.verticalPos;
        view.getGame().setPreview(false);
        getPathPopup().dissmissPath();
        initGrid(9, 15);
        configure(new Config());
        getLayout().setCurrentWorld(null);
        _id=null;
        getLayout().tile = tiles.get(10);
        getPathPopup().showControls();

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
                this.tiles.add(new TileRoute(view.screenWidth, view.screenHeight, width, height, j, i, Route.Direction.LEFT, Route.Direction.RIGHT, Route.Type.TILE, this));

            }
        }
    }


    public void resizeGrid(int width, int height) {

        int widthDiff = width-this.gridX;
        int heightDiff = height - this.gridY-height;




        this.gridX = width;
        this.gridY = height;


        this.tiles = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                this.tiles.add(new TileRoute(view.screenWidth, view.screenHeight, width, height, j, i, Route.Direction.LEFT, Route.Direction.RIGHT, Route.Type.TILE, this));

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

    public TileRoute findTileByCoords(float x, float y) {
        for (int i = 0; i < tiles.size(); i++) {
            TileRoute t = tiles.get(i);
            if (t.contains(x,y))
                return t;
        }
        return null;
    }




    public TileRoute getFinishRoute() {
        for (int i = 0; i < tiles.size(); i++) {
            TileRoute t = tiles.get(i);
            if (t.getType() == Route.Type.FINISH) {
                return t;

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

    public void refreashSounds()
    {
        view.getGame().configureSounds();
    }
    public void refreashMaze() {

        Log.d("zzz", "refreash maze " );
        configure(config);

        if (runPreview) {
            view.getGame().currentStage.config = config;
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

    private Dialog dialog;
    public void initMenu()
    {

        dialog = new Dialog(view.context);
        dialog.setTitle("Settings");
        dialog.setContentView(getLayout().onCreateView());
    }

    public void showMenu()
    {


        dialog.show();

    }


    private JSONObject dumpMaze() {
        pathBuilderPopup.startRouteConfiguration();


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
                    step.put("draw_coin", t.drawCoin);
                    if (t.sound != null)
                        step.put("sound", t.sound);


                    route.put(step);
                }

            }
            output.put("route", route);
            if(layout.getCurrentWorld()!=null)
            output.put("world_id", layout.getCurrentWorld().id);
            else
                output.put("world_id", null);

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

        Log.d(TAG,"maze dumped, stage id: "+_id);
        return output;
    }

    public void shareMaze()
    {
        if(_id==null || _id.trim().equals("")) {
            DAO.addStage(dumpMaze(), new Response.Listener() {
                @Override
                public void onResponse(Object response) {
                    Log.d(TAG, response.toString());

                    JSONObject object = null;
                    try {
                        object = new JSONObject(response.toString());

                        object = object.has("api") ? object.getJSONObject("api") : object;
                        object = object.has("doc") ? object.getJSONObject("doc") : object;
                        String id = object.has("_id") ? object.getString("_id") : "";
                        view.context.getActivityInvite().invite(id);
                        Toast.makeText(view.context, "Stage shared!", Toast.LENGTH_LONG).show();
                        _id = id;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }, _id);
        }
        else
        {
            view.context.getActivityInvite().invite(_id);

        }
    }

    public interface OnSaveListener
    {
        public void onSaved();
        public void onFailed();
        public JSONObject onDumped(JSONObject data);

    }
    public void saveMaze(final OnSaveListener listener) {




        JSONObject data = dumpMaze();

        if(listener!=null) {

            JSONObject tmp = listener.onDumped(data);
            if(tmp!=null)
                data=tmp;
        }

        DAO.addStage(data, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                Log.d(TAG, response.toString());


                Toast.makeText(view.context, "Stage saved!", Toast.LENGTH_LONG).show();
                if (listener != null)
                    listener.onSaved();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(listener!=null)
                listener.onFailed();
            }
        },_id);


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

        if(getPreview())
            stopPreview();


        tiles.clear();

        for (int i = 0; i < gridY; i++) {
            for (int j = 0; j < gridX; j++) {
                this.tiles.add(new TileRoute(view.screenWidth, view.screenHeight, gridX, gridY, j, i, Route.Direction.LEFT, Route.Direction.RIGHT, Route.Type.TILE, this));

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

      //  if (getLayout().getCurrentWorld() == null)
      //      return true;

        if (runPreview) {
            view.getGame().onTouchEvent(event);
        } else {

            pathBuilderDraw.onTouch(event);
            pathBuilderPopup.onTouch(event);
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


        if(getStartRoute() == null)
            return;


        view.getGame().setPreview(true);
        view.getGame().initStage(Stage.valueOf(dumpMaze()));
        //view.context.drawer.closeDrawer(view.context.drawerContent);
        runPreview = true;
        popupFactory.dissmissPath();
        //getLayout().showPreviewControlls();

        view.context.getActivityControls().resetLogs();

    }

    public void stopPreview() {
        MainActivity.sendAction(SoundsService.ACTION_MUTE,null);
        view.getGame().setPreview(false);
        runPreview = false;
        refreashMaze();
      //  getLayout().showGeneratorConstrolls();


    }

    public void onDraw(float[] mMVPMatrix) {


        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);


        if (!runPreview) {
            GLES20.glClearColor(r, g, b, a);
            GLES20.glUseProgram(mGeneratorProgram);

            if (tiles == null)
                return;

            GLES20.glEnableVertexAttribArray(mPositionHandle);
            GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);


            for (int i = 0; i < tiles.size(); i++) {

                if (tiles.size() > i && tiles.get(i) != null)
                    tiles.get(i).drawGL20(mMVPMatrix);
            }

            GLES20.glDisableVertexAttribArray(mPositionHandle);

        } else {
            view.getGame().onDraw(mMVPMatrix);
        }
    }


}
