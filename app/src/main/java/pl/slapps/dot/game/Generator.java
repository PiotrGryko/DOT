package pl.slapps.dot.game;

import android.app.Dialog;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import pl.slapps.dot.DAO;
import pl.slapps.dot.R;
import pl.slapps.dot.route.Route;
import pl.slapps.dot.route.RouteFinish;
import pl.slapps.dot.route.RouteStart;
import yuku.ambilwarna.AmbilWarnaDialog;

/**
 * Created by piotr on 18.10.15.
 */
public class Generator {

    private String TAG = Generator.class.getName();


    ArrayList<Route> tiles;


    public float width;
    public float height;
    private GameView view;

    private String _id;

    private String name="generated stage";
    private String description = "generated desc";

    private String backgroundColor;
    private String fillColor;

    private String dotColor = "#000000";
    private String routeColor = "#8b2323";
    private String blockColor = "#8b2323";

    private String explosionStartColor = "#000000";
    private String explosionEndColor = "#FFFFFF";

    private Route mCurrentSelectedRoad;


    public Generator(GameView view, int width, int height) {
        //this.elements=elements;
        this.view = view;

        initGrid(width, height);

        Log.d(TAG, "generator loaded " + this.tiles.size());


    }

    private void initGrid(int width, int height) {
        this.width = width;
        this.height = height;


        this.tiles = new ArrayList<>();
        this.backgroundColor = this.fillColor = view.getGameBackground().backgroundcolor;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                this.tiles.add(new Route(view.screenWidth, view.screenHeight, width, height, j, i, "LEFT", "RIGHT", Route.Type.TILE));

            }
        }
    }

    private Route findTile(int x, int y) {
        for (int i = 0; i < tiles.size(); i++) {
            Route t = tiles.get(i);
            if (t.horizontalPos == x && t.verticalPos == y)
                return t;
        }
        return null;
    }

    private void startRouteConfiguration() {
        ArrayList<Route> routes = new ArrayList<>();
        Route startRoute = null;
        Route nextRoute = null;
        Route.Direction from = null;
        for (int i = 0; i < tiles.size(); i++) {
            Route t = tiles.get(i);
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

    private void configRoute(Route route, Route.Direction target_from, ArrayList<Route> routes) {
        //route.from=from;

        Route.Movement t = route.getDirection();
        Route.Direction target_to = route.to;
        int targetX = route.horizontalPos;
        int targetY = route.verticalPos;

        Route.Direction nextFrom = target_from;

        Log.d(TAG, "config route from: " + target_from);

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
        Log.d(TAG, "route configured " + target_from + " " + target_to + " ");

        Route nextRoute = findTile(targetX, targetY);


        if (nextRoute != null && nextRoute.getType() != Route.Type.TILE && nextRoute != route) {
            configRoute(nextRoute, nextFrom, routes);
        }


    }

    public Route.Movement getNextMove(int start, ArrayList<Route> routes) {


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

    private void loadMaze() {
        final Dialog stages = new Dialog(view.context);
        stages.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View v = LayoutInflater.from(view.context).inflate(R.layout.dialog_stages, null);
        ListView listView = (ListView) v.findViewById(R.id.lv);
        ArrayList<String> entries = new ArrayList<String>();
        for (int i = 0; i < view.context.stages.length(); i++) {
            entries.add(Integer.toString(i));
        }
        listView.setAdapter(new ArrayAdapter<String>(view.context, android.R.layout.simple_list_item_1, entries));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int i, long l) {
                try {
                    loadRoute(view.context.stages.getJSONObject(i));

                    stages.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        stages.setContentView(v);
        stages.show();
    }

    private void loadOnlineMaze() {


        DAO.getStages(view.context, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                Log.d(TAG, response.toString());
                JSONObject out = null;
                try {
                    out = new JSONObject(response.toString());

                    JSONObject api = out.has("api") ? out.getJSONObject("api") : new JSONObject();
                    final JSONArray results = api.has("results") ? api.getJSONArray("results") : new JSONArray();

                    final Dialog stages = new Dialog(view.context);
                    stages.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

                    View v = LayoutInflater.from(view.context).inflate(R.layout.dialog_stages, null);
                    ListView listView = (ListView) v.findViewById(R.id.lv);
                    ArrayList<String> entries = new ArrayList<String>();
                    for (int i = 0; i < results.length(); i++) {
                        entries.add(Integer.toString(i));
                    }
                    listView.setAdapter(new ArrayAdapter<String>(view.context, android.R.layout.simple_list_item_1, entries));

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            try {
                                loadRoute(results.getJSONObject(i));

                                stages.dismiss();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    stages.setContentView(v);
                    stages.show();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


    }


    private void refreashMaze() {
        view.getGameBackground().setColor(backgroundColor);

        for (int i = 0; i < tiles.size(); i++) {
            Route t = tiles.get(i);
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
            output.put("y_max", height);
            output.put("x_max", width);

            JSONObject colors = new JSONObject();
            colors.put("ship", dotColor);
            colors.put("background", backgroundColor);
            colors.put("explosion_start", explosionStartColor);
            colors.put("explosion_end", explosionEndColor);
            colors.put("route", routeColor);
            output.put("colors", colors);


            JSONArray route = new JSONArray();
            for (int i = 0; i < tiles.size(); i++) {
                Route t = tiles.get(i);
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

                    route.put(step);
                }

            }
            output.put("route", route);

            //Stages.stages.add(output.toString());

            DAO.addStage(view.context, output, new Response.Listener() {
                @Override
                public void onResponse(Object response) {
                    Log.d(TAG, response.toString());
                    Toast.makeText(view.context, "Stage saved!", Toast.LENGTH_LONG).show();
                }
            },_id);
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

    private void showGridSizeDialog() {
        View v = LayoutInflater.from(view.context).inflate(R.layout.dialog_generator_grid_size, null);
        final EditText etWidth = (EditText) v.findViewById(R.id.et_width);
        final EditText etHeight = (EditText) v.findViewById(R.id.et_height);
        Button btnSave = (Button) v.findViewById(R.id.btn_init_grid);

        final Dialog dialog = new Dialog(view.context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textWidht = etWidth.getText().toString();
                String textHeight = etHeight.getText().toString();

                if (textWidht.trim().equals("")) {
                    Toast.makeText(view.context, "width is required", Toast.LENGTH_LONG).show();
                    return;
                }
                if (textHeight.trim().equals("")) {
                    Toast.makeText(view.context, "height is required", Toast.LENGTH_LONG).show();
                    return;
                }

                initGrid(Integer.parseInt(textWidht), Integer.parseInt(textHeight));
                dialog.dismiss();
            }
        });

        dialog.setContentView(v);
        dialog.show();

    }

    private void showStringsDialog() {
        View v = LayoutInflater.from(view.context).inflate(R.layout.dialog_generator_strings, null);
        final EditText etName = (EditText) v.findViewById(R.id.et_name);
        final EditText etDesc = (EditText) v.findViewById(R.id.et_desc);
        Button btnSave = (Button) v.findViewById(R.id.btn_ok);

        final Dialog dialog = new Dialog(view.context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textName = etName.getText().toString();
                String textDesc = etDesc.getText().toString();

                if (textName.trim().equals("")) {
                    Toast.makeText(view.context, "width is required", Toast.LENGTH_LONG).show();
                    return;
                }
                if (textDesc.trim().equals("")) {
                    Toast.makeText(view.context, "height is required", Toast.LENGTH_LONG).show();
                    return;
                }

                name=textName;
                description=textDesc;
                //initGrid(Integer.parseInt(textWidht), Integer.parseInt(textHeight));
                dialog.dismiss();
            }
        });

        dialog.setContentView(v);
        dialog.show();

    }

    private void showOptionsDialog(final Route tile) {

        View layout = LayoutInflater.from(view.context).inflate(R.layout.dialog_generator_options, null);
        ImageView tvBottomLeft = (ImageView) layout.findViewById(R.id.tv_bottomleft);
        ImageView tvBottomRight = (ImageView) layout.findViewById(R.id.tv_bottomright);
        ImageView tvBottomTop = (ImageView) layout.findViewById(R.id.tv_bottomtop);

        ImageView tvLeftTop = (ImageView) layout.findViewById(R.id.tv_lefttop);
        ImageView tvLeftRight = (ImageView) layout.findViewById(R.id.tv_leftright);

        ImageView tvRightTop = (ImageView) layout.findViewById(R.id.tv_righttop);


        ImageView tvStartTop = (ImageView) layout.findViewById(R.id.tv_starttop);
        ImageView tvStartBottom = (ImageView) layout.findViewById(R.id.tv_startbottom);
        ImageView tvStartRight = (ImageView) layout.findViewById(R.id.tv_startright);
        ImageView tvStartLeft = (ImageView) layout.findViewById(R.id.tv_startleft);

        ImageView tvFinishTop = (ImageView) layout.findViewById(R.id.tv_finishtop);
        ImageView tvFinishBottom = (ImageView) layout.findViewById(R.id.tv_finishbottom);
        ImageView tvFinishLeft = (ImageView) layout.findViewById(R.id.tv_finishleft);
        ImageView tvFinishRight = (ImageView) layout.findViewById(R.id.tv_finishright);

        ImageView tvFill = (ImageView) layout.findViewById(R.id.tv_fill);

        ImageView tvBlock = (ImageView) layout.findViewById(R.id.tv_block);

        final LinearLayout tvBlockColor = (LinearLayout) layout.findViewById(R.id.tv_block_color);
        final LinearLayout tvFillColor = (LinearLayout) layout.findViewById(R.id.tv_fill_color);

        final LinearLayout tvBackgroundColor = (LinearLayout) layout.findViewById(R.id.tv_background_color);
        final LinearLayout tvRouteColor = (LinearLayout) layout.findViewById(R.id.tv_route_color);
        final LinearLayout tvDotColor = (LinearLayout) layout.findViewById(R.id.tv_dot_color);
        final LinearLayout tvExplosionStartColor = (LinearLayout) layout.findViewById(R.id.tv_explosion_start_color);
        final LinearLayout tvExplosionEndColor = (LinearLayout) layout.findViewById(R.id.tv_explosion_end_color);

        View colorBlock = layout.findViewById(R.id.color_block);
        View colorFill = layout.findViewById(R.id.color_fill);

        View colorBackground = layout.findViewById(R.id.color_background);
        View colorRoute = layout.findViewById(R.id.color_route);
        View colorDot = layout.findViewById(R.id.color_dot);
        View colorExplosionStart = layout.findViewById(R.id.color_explosion_start);
        View colorExplosionEnd = layout.findViewById(R.id.color_explosion_end);

        colorFill.setBackgroundColor(Color.parseColor(fillColor));

        colorBlock.setBackgroundColor(Color.parseColor(blockColor));
        colorBackground.setBackgroundColor(Color.parseColor(backgroundColor));
        colorRoute.setBackgroundColor(Color.parseColor(routeColor));
        colorDot.setBackgroundColor(Color.parseColor(dotColor));
        colorExplosionStart.setBackgroundColor(Color.parseColor(explosionStartColor));
        colorExplosionEnd.setBackgroundColor(Color.parseColor(explosionEndColor));


        TextView tvBlank = (TextView) layout.findViewById(R.id.tv_blank);
        TextView tvSave = (TextView) layout.findViewById(R.id.tv_save);
        TextView tvLoad = (TextView) layout.findViewById(R.id.tv_load);
        TextView tvLoadOnline = (TextView) layout.findViewById(R.id.tv_load_online);
        TextView tvDeleteOnline = (TextView) layout.findViewById(R.id.tv_delete_online);
        TextView tvSetName = (TextView) layout.findViewById(R.id.tv_set_name);

        TextView tvGridSize = (TextView) layout.findViewById(R.id.tv_grid_size);


        final Dialog d = new Dialog(view.context);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int id = v.getId();

                Route r = null;
                switch (id) {
                    case R.id.tv_leftright: {
                        r = new Route(view.screenWidth, view.screenHeight, width, height, tile.horizontalPos, tile.verticalPos, Route.Direction.LEFT.name(), Route.Direction.RIGHT.name(), Route.Type.ROUTE);
                        break;
                    }
                    case R.id.tv_lefttop: {
                        r = new Route(view.screenWidth, view.screenHeight, width, height, tile.horizontalPos, tile.verticalPos, Route.Direction.LEFT.name(), Route.Direction.TOP.name(), Route.Type.ROUTE);

                        break;
                    }

                    case R.id.tv_righttop: {
                        r = new Route(view.screenWidth, view.screenHeight, width, height, tile.horizontalPos, tile.verticalPos, Route.Direction.RIGHT.name(), Route.Direction.TOP.name(), Route.Type.ROUTE);

                        break;
                    }


                    case R.id.tv_bottomleft: {
                        r = new Route(view.screenWidth, view.screenHeight, width, height, tile.horizontalPos, tile.verticalPos, Route.Direction.BOTTOM.name(), Route.Direction.LEFT.name(), Route.Type.ROUTE);

                        break;
                    }
                    case R.id.tv_bottomright: {
                        r = new Route(view.screenWidth, view.screenHeight, width, height, tile.horizontalPos, tile.verticalPos, Route.Direction.BOTTOM.name(), Route.Direction.RIGHT.name(), Route.Type.ROUTE);

                        break;
                    }
                    case R.id.tv_bottomtop: {

                        r = new Route(view.screenWidth, view.screenHeight, width, height, tile.horizontalPos, tile.verticalPos, Route.Direction.BOTTOM.name(), Route.Direction.TOP.name(), Route.Type.ROUTE);

                        break;
                    }


                    case R.id.tv_blank: {
                        r = new Route(view.screenWidth, view.screenHeight, width, height, tile.horizontalPos, tile.verticalPos, Route.Direction.TOP.name(), Route.Direction.RIGHT.name(), Route.Type.TILE);
                        break;
                    }
                    case R.id.tv_block: {
                        r = new Route(view.screenWidth, view.screenHeight, width, height, tile.horizontalPos, tile.verticalPos, Route.Direction.TOP.name(), Route.Direction.RIGHT.name(), Route.Type.BLOCK, blockColor);
                        break;
                    }

                    case R.id.tv_fill: {
                        r = new Route(view.screenWidth, view.screenHeight, width, height, tile.horizontalPos, tile.verticalPos, Route.Direction.TOP.name(), Route.Direction.RIGHT.name(), Route.Type.FILL, fillColor);
                        break;
                    }

                    case R.id.tv_finishtop: {
                        r = new RouteFinish(view.screenWidth, view.screenHeight, width, height, tile.horizontalPos, tile.verticalPos, view, Route.Direction.BOTTOM.name(), Route.Direction.TOP.name());
                        break;
                    }
                    case R.id.tv_finishbottom: {
                        r = new RouteFinish(view.screenWidth, view.screenHeight, width, height, tile.horizontalPos, tile.verticalPos, view, Route.Direction.TOP.name(), Route.Direction.BOTTOM.name());
                        break;
                    }
                    case R.id.tv_finishleft: {
                        r = new RouteFinish(view.screenWidth, view.screenHeight, width, height, tile.horizontalPos, tile.verticalPos, view, Route.Direction.RIGHT.name(), Route.Direction.LEFT.name());
                        break;
                    }
                    case R.id.tv_finishright: {
                        r = new RouteFinish(view.screenWidth, view.screenHeight, width, height, tile.horizontalPos, tile.verticalPos, view, Route.Direction.LEFT.name(), Route.Direction.RIGHT.name());
                        break;
                    }


                    case R.id.tv_starttop: {
                        r = new RouteStart(view.screenWidth, view.screenHeight, width, height, tile.horizontalPos, tile.verticalPos, Route.Direction.TOP.name(), Route.Direction.BOTTOM.name());

                        break;
                    }
                    case R.id.tv_startbottom: {
                        r = new RouteStart(view.screenWidth, view.screenHeight, width, height, tile.horizontalPos, tile.verticalPos, Route.Direction.BOTTOM.name(), Route.Direction.TOP.name());

                        break;
                    }
                    case R.id.tv_startleft: {

                        r = new RouteStart(view.screenWidth, view.screenHeight, width, height, tile.horizontalPos, tile.verticalPos, Route.Direction.LEFT.name(), Route.Direction.RIGHT.name());
                        break;
                    }
                    case R.id.tv_startright: {

                        r = new RouteStart(view.screenWidth, view.screenHeight, width, height, tile.horizontalPos, tile.verticalPos, Route.Direction.RIGHT.name(), Route.Direction.LEFT.name());
                        break;
                    }

                   /*


                    case R.id.tv_finish: {
                        r = new RouteFinish(view.screenWidth, view.screenHeight, width, height, tile.horizontalPos, tile.verticalPos, view);
                        tiles.remove(tile);
                        tiles.add(r);
                        d.dismiss();
                        break;
                    }


                    case R.id.tv_start: {
                        Route r = new RouteStart(view.screenWidth, view.screenHeight, width, height, tile.horizontalPos, tile.verticalPos);
                        tiles.remove(tile);
                        tiles.add(r);
                        d.dismiss();
                        break;
                    }
                    */
                    case R.id.tv_save: {
                        saveMaze();
                        break;
                    }

                    case R.id.tv_load: {
                        loadMaze();
                        break;
                    }

                    case R.id.tv_load_online: {
                        loadOnlineMaze();
                        break;
                    }

                    case R.id.tv_delete_online: {


                        if(_id == null )
                        {
                            Toast.makeText(view.context,"First you have to load online stage",Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            DAO.removeStage(view.context, new Response.Listener() {
                                @Override
                                public void onResponse(Object response) {

                                    Log.d(TAG,"Stage removed ");
                                    Toast.makeText(view.context,"Stage removed",Toast.LENGTH_LONG).show();
                                }
                            },_id);
                        }
                        //deleteOnlineMaze();
                        break;
                    }
                    case R.id.tv_grid_size: {
                        showGridSizeDialog();
                        break;
                    }

                    case R.id.tv_set_name: {
                        showStringsDialog();
                        break;
                    }

                    case R.id.tv_block_color: {


                        AmbilWarnaDialog dialog = new AmbilWarnaDialog(view.context, Color.parseColor(blockColor), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                            @Override
                            public void onOk(AmbilWarnaDialog dialog, int color) {
                                // color is the color selected by the user.

                                tvBlockColor.setBackgroundColor(color);
                                blockColor = "#" + Integer.toHexString(color);

                                refreashMaze();
                                dialog.getDialog().dismiss();
                            }

                            @Override
                            public void onCancel(AmbilWarnaDialog dialog) {
                                // cancel was selected by the user
                            }
                        });

                        dialog.show();

                        break;
                    }

                    case R.id.tv_fill_color: {


                        AmbilWarnaDialog dialog = new AmbilWarnaDialog(view.context, Color.parseColor(fillColor), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                            @Override
                            public void onOk(AmbilWarnaDialog dialog, int color) {
                                // color is the color selected by the user.

                                tvFillColor.setBackgroundColor(color);
                                fillColor = "#" + Integer.toHexString(color);

                                refreashMaze();
                                dialog.getDialog().dismiss();
                            }

                            @Override
                            public void onCancel(AmbilWarnaDialog dialog) {
                                // cancel was selected by the user
                            }
                        });

                        dialog.show();

                        break;
                    }
                    case R.id.tv_background_color: {


                        AmbilWarnaDialog dialog = new AmbilWarnaDialog(view.context, Color.parseColor(backgroundColor), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                            @Override
                            public void onOk(AmbilWarnaDialog dialog, int color) {
                                // color is the color selected by the user.

                                tvBackgroundColor.setBackgroundColor(color);
                                backgroundColor = "#" + Integer.toHexString(color);


                                refreashMaze();
                                dialog.getDialog().dismiss();
                            }

                            @Override
                            public void onCancel(AmbilWarnaDialog dialog) {
                                // cancel was selected by the user
                            }
                        });

                        dialog.show();
                        break;
                    }
                    case R.id.tv_route_color: {


                        AmbilWarnaDialog dialog = new AmbilWarnaDialog(view.context, Color.parseColor(routeColor), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                            @Override
                            public void onOk(AmbilWarnaDialog dialog, int color) {
                                // color is the color selected by the user.

                                tvRouteColor.setBackgroundColor(color);
                                routeColor = "#" + Integer.toHexString(color);

                                refreashMaze();
                                dialog.getDialog().dismiss();
                            }

                            @Override
                            public void onCancel(AmbilWarnaDialog dialog) {
                                // cancel was selected by the user
                            }
                        });

                        dialog.show();
                        break;
                    }
                    case R.id.tv_dot_color: {


                        AmbilWarnaDialog dialog = new AmbilWarnaDialog(view.context, Color.parseColor(dotColor), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                            @Override
                            public void onOk(AmbilWarnaDialog dialog, int color) {
                                // color is the color selected by the user.

                                tvDotColor.setBackgroundColor(color);
                                dotColor = "#" + Integer.toHexString(color);


                                refreashMaze();
                                dialog.getDialog().dismiss();
                            }

                            @Override
                            public void onCancel(AmbilWarnaDialog dialog) {
                                // cancel was selected by the user
                            }
                        });

                        dialog.show();
                        break;
                    }
                    case R.id.tv_explosion_start_color: {


                        AmbilWarnaDialog dialog = new AmbilWarnaDialog(view.context, Color.BLUE, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                            @Override
                            public void onOk(AmbilWarnaDialog dialog, int color) {
                                // color is the color selected by the user.

                                tvExplosionStartColor.setBackgroundColor(color);
                                explosionStartColor = "#" + Integer.toHexString(color);


                                refreashMaze();
                                dialog.getDialog().dismiss();
                            }

                            @Override
                            public void onCancel(AmbilWarnaDialog dialog) {
                                // cancel was selected by the user
                            }
                        });

                        dialog.show();
                        break;
                    }
                    case R.id.tv_explosion_end_color: {


                        AmbilWarnaDialog dialog = new AmbilWarnaDialog(view.context, Color.parseColor(explosionEndColor), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                            @Override
                            public void onOk(AmbilWarnaDialog dialog, int color) {
                                // color is the color selected by the user.

                                tvExplosionEndColor.setBackgroundColor(color);
                                explosionEndColor = "#" + Integer.toHexString(color);


                                refreashMaze();
                                dialog.getDialog().dismiss();
                            }

                            @Override
                            public void onCancel(AmbilWarnaDialog dialog) {
                                // cancel was selected by the user
                            }
                        });

                        dialog.show();


                        break;
                    }


                }
                Route nextTile = null;

                if (r != null) {
                    switch (r.getType()) {
                        case BLOCK:
                            r.setRouteColor(blockColor);
                            break;
                        case ROUTE:
                            r.setRouteColor(routeColor);
                            break;
                        case FILL:
                            r.setRouteColor(fillColor);
                            break;


                    }

                    tiles.remove(tile);

                    tiles.add(r);

                    startRouteConfiguration();


                    Route.Movement movement = r.getDirection();
                    switch (movement) {
                        case LEFTRIGHT:
                        case BOTTOMRIGHT:
                        case TOPRIGHT:
                            nextTile = findTile(r.horizontalPos + 1, r.verticalPos);
                            break;
                        case RIGHTLEFT:
                        case BOTTOMLEFT:
                        case TOPLEFT:
                            nextTile = findTile(r.horizontalPos - 1, r.verticalPos);
                            break;
                        case BOTTOMTOP:
                        case RIGHTTOP:
                        case LEFTTOP:
                            nextTile = findTile(r.horizontalPos, r.verticalPos - 1);
                            break;
                        case LEFTBOTTOM:
                        case RIGHTBOTTOM:
                        case TOPBOTTOM:
                            nextTile = findTile(r.horizontalPos, r.verticalPos + 1);
                            break;
                    }
                }
                d.dismiss();

                if (nextTile != null)
                    showOptionsDialog(nextTile);
            }
        };

        tvBottomLeft.setOnClickListener(listener);
        tvBottomRight.setOnClickListener(listener);
        tvBottomTop.setOnClickListener(listener);

        tvRightTop.setOnClickListener(listener);

        tvLeftRight.setOnClickListener(listener);
        tvLeftTop.setOnClickListener(listener);


        tvBlank.setOnClickListener(listener);

        tvFinishBottom.setOnClickListener(listener);
        tvFinishLeft.setOnClickListener(listener);
        tvFinishRight.setOnClickListener(listener);
        tvFinishTop.setOnClickListener(listener);


        tvStartBottom.setOnClickListener(listener);
        tvStartLeft.setOnClickListener(listener);
        tvStartRight.setOnClickListener(listener);
        tvStartTop.setOnClickListener(listener);
        tvBlock.setOnClickListener(listener);
        tvBlockColor.setOnClickListener(listener);

        tvFill.setOnClickListener(listener);
        tvFillColor.setOnClickListener(listener);
        tvBackgroundColor.setOnClickListener(listener);
        tvRouteColor.setOnClickListener(listener);
        tvDotColor.setOnClickListener(listener);
        tvExplosionStartColor.setOnClickListener(listener);
        tvExplosionEndColor.setOnClickListener(listener);


        tvSave.setOnClickListener(listener);
        tvLoad.setOnClickListener(listener);
        tvLoadOnline.setOnClickListener(listener);
        tvDeleteOnline.setOnClickListener(listener);
        tvSetName.setOnClickListener(listener);
        tvGridSize.setOnClickListener(listener);


        d.setContentView(layout);
        d.show();


    }

    public void loadRoute(JSONObject maze) {
        //this.elements=elements;


        tiles.clear();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                this.tiles.add(new Route(view.screenWidth, view.screenHeight, width, height, j, i, "LEFT", "RIGHT", Route.Type.TILE));

            }
        }

        try {


            width = maze.has("x_max") ? maze.getInt("x_max") : 0;

            height = maze.has("y_max") ? maze.getInt("y_max") : 0;

            _id = maze.has("_id")?maze.getString("_id"):null;

            initGrid((int)width,(int)height);

            JSONObject colors = maze.has("colors") ? maze.getJSONObject("colors") : new JSONObject();

            routeColor = colors.has("route") ? colors.getString("route") : routeColor;
            backgroundColor = colors.has("background") ? colors.getString("background") : backgroundColor;
            dotColor = colors.has("ship") ? colors.getString("ship") : dotColor;
            explosionStartColor = colors.has("explosion_start") ? colors.getString("explosion_start") : "#FF0000";

            explosionEndColor = colors.has("explosion_end") ? colors.getString("explosion_end") : "#700BCB";


            JSONArray jsonRoutes = maze.has("route") ? maze.getJSONArray("route") : new JSONArray();

            for (int i = 0; i < jsonRoutes.length(); i++) {
                JSONObject element = jsonRoutes.getJSONObject(i);
                //String type = element.has("type") ? element.getString("type") : "";
                int horizontal_position = element.has("x") ? element.getInt("x") : 0;
                int vertical_position = element.has("y") ? element.getInt("y") : 0;
                String from = element.has("from") ? element.getString("from") : "";
                String to = element.has("to") ? element.getString("to") : "";
                String type = element.has("type") ? element.getString("type") : "ROUTE";

                Route.Type t = Route.Type.valueOf(type);

                Route tile = findTile(horizontal_position, vertical_position);

                switch (t) {

                    case FINISH:
                        tiles.remove(tile);
                        this.tiles.add(new RouteFinish(view.screenWidth, view.screenHeight, width, height, view, element, routeColor));
                        break;

                    case START:
                        tiles.remove(tile);
                        this.tiles.add(new RouteStart(view.screenWidth, view.screenHeight, width, height, element, routeColor));
                        break;
                    default:
                        tiles.remove(tile);
                        this.tiles.add(new Route(view.screenWidth, view.screenHeight, width, height, element, routeColor));
                        break;

                }
            }

            refreashMaze();
            Log.d(TAG, "stage loaded ");
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    public boolean onTouch(MotionEvent event) {


        Log.d(TAG, "ont touch " + event.getX());

        for (int i = 0; i < tiles.size(); i++) {
            Route t = tiles.get(i);
            if (t.contains(event.getX(), event.getY())) {
                showOptionsDialog(t);
                return true;
            }
        }


        return true;
    }

    public boolean contains(float x, float y) {
        for (int i = 0; i < tiles.size(); i++) {
            Route element = tiles.get(i);

            if (element.contains(x, y))
                return true;

        }
        return false;
    }


    public void draw(GL10 gl) {
        for (int i = 0; i < tiles.size(); i++) {

            tiles.get(i).draw(gl);
        }

    }

}
