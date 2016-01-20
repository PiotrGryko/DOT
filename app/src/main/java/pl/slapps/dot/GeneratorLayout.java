package pl.slapps.dot;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import pl.slapps.dot.adapter.AdapterWorlds;
import pl.slapps.dot.game.Generator;
import pl.slapps.dot.model.Route;
import pl.slapps.dot.model.Stage;
import pl.slapps.dot.model.World;
import pl.slapps.dot.tile.TileRoute;
import pl.slapps.dot.tile.TileRouteFinish;
import pl.slapps.dot.tile.TileRouteStart;
import yuku.ambilwarna.AmbilWarnaDialog;

/**
 * Created by piotr on 21.12.15.
 */
public class GeneratorLayout {

    private String TAG = GeneratorLayout.class.getName();

    /*
    drawer Main buttons bases
     */
    private LinearLayout layoutConstructBase;
    private LinearLayout layoutGridBase;
    private LinearLayout layoutColorsBase;
    private LinearLayout layoutConfigBase;
    private LinearLayout layoutRutesBase;
    private LinearLayout layoutSoundsBase;
    private LinearLayout layoutStringsBase;
    private LinearLayout layoutWorldBase;
    private LinearLayout layoutChooseWorldBase;

    /*
    drawer Main buttons
     */
    private LinearLayout toggleConstruct;
    private LinearLayout toggleColors;
    private LinearLayout toggleGridSize;
    private LinearLayout toggleConfig;
    private LinearLayout toggleRoutes;
    private LinearLayout toggleSounds;
    private LinearLayout toggleString;
    private LinearLayout toggleWorld;
    private LinearLayout toggleChooseWorld;
    private LinearLayout toggleSave;
    /*
    drawer toggable layouts
     */
    private LinearLayout layoutConstruct;
    private LinearLayout layoutGridsize;
    private LinearLayout layoutcolors;
    private LinearLayout layoutConfig;
    private LinearLayout layoutRoutes;
    private LinearLayout layoutSounds;
    private LinearLayout layoutStrings;
    private LinearLayout layoutWorld;
    private LinearLayout layoutWorldsList;

    /*
    path buttons
     */
    private ImageView tvBottomLeft;
    private ImageView tvBottomRight;
    private ImageView tvBottomTop;
    private ImageView tvLeftTop;
    private ImageView tvLeftRight;
    private ImageView tvRightTop;

    private ImageView tvStartTop;
    private ImageView tvStartBottom;
    private ImageView tvStartRight;
    private ImageView tvStartLeft;

    private ImageView tvFinishTop;
    private ImageView tvFinishBottom;
    private ImageView tvFinishLeft;
    private ImageView tvFinishRight;


    private MainActivity context;
    private Generator generator;

    /*
    currently selected tile and previously selected tile
     */
    private TileRoute tile;
    private TileRoute oldTile;

    /*
    currently setted world
     */
    private World currentWorld;



    /*
    layout elements
     */

    /*
   Path layout elements
    */
    private TextView tvElementsCountLabel;
    private LinearLayout layoutProgress;
    private TextView tvCurrentX;
    private TextView tvCurrentY;

    /*
    gridsize layout elements
     */
    private EditText etWidth;
    private EditText etHeight;
    /*
    strings layout elements
     */
    private EditText etName;
    private EditText etDesc;

    /*
    sounds layout elements
     */
    private TextView btnSoundBackground;
    private TextView btnSoundPress;
    private TextView btnSoundCrash;
    private TextView btnSoundFinish;
    private ImageView btnPlayBackground;
    private ImageView btnPlayPress;
    private ImageView btnPlayCrash;
    private ImageView btnPlayFinish;
    /*
    colors layout elements
     */
    private View colorBackground;
    private View colorRoute;
    private View colorDot;
    private View colorExplosionStart;
    private View colorExplosionEnd;

    public GeneratorLayout(MainActivity context, TileRoute tile) {
        this.context = context;
        this.tile = tile;
    }

    public World getCurrentWorld() {
        return currentWorld;
    }

    private void refreashStringsLayout(Stage stage) {
        etName.setText(stage.name);
        etDesc.setText(stage.description);
    }

    private void initStringsLayout(View v) {
        etName = (EditText) v.findViewById(R.id.et_name);
        etDesc = (EditText) v.findViewById(R.id.et_desc);
        ImageView btnSave = (ImageView) v.findViewById(R.id.btn_ok);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textName = etName.getText().toString();
                String textDesc = etDesc.getText().toString();


                generator.name = textName;
                generator.description = textDesc;
            }
        });


    }

    private void refreashSoundsLayout() {
        if (!generator.backgrounSound.trim().equals(""))
            btnSoundBackground.setText(generator.backgrounSound);

        if (!generator.pressSound.trim().equals(""))
            btnSoundPress.setText(generator.pressSound);
        else
            btnSoundPress.setText(SoundsManager.DEFAULT_PRESS);
        if (!generator.crashSound.trim().equals(""))
            btnSoundCrash.setText(generator.crashSound);
        else
            btnSoundCrash.setText(SoundsManager.DEFAULT_CRASH);
        if (!generator.finishSound.trim().equals(""))
            btnSoundFinish.setText(generator.finishSound);
        else
            btnSoundFinish.setText(SoundsManager.DEFAULT_FINISH);

    }

    private void initSoundsLayout(View v) {
        btnSoundBackground = (TextView) v.findViewById(R.id.btn_choose_background);
        btnSoundPress = (TextView) v.findViewById(R.id.btn_choose_press);
        btnSoundCrash = (TextView) v.findViewById(R.id.btn_choose_crash);
        btnSoundFinish = (TextView) v.findViewById(R.id.btn_choose_finish);

        btnPlayBackground = (ImageView) v.findViewById(R.id.btn_play_background);
        btnPlayPress = (ImageView) v.findViewById(R.id.btn_play_press);
        btnPlayCrash = (ImageView) v.findViewById(R.id.btn_play_crash);
        btnPlayFinish = (ImageView) v.findViewById(R.id.btn_play_finish);

        refreashSoundsLayout();

        btnPlayBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vie) {
                if (generator.backgrounSound.trim().equals("")) {
                    Toast.makeText(generator.view.context, "Select file first", Toast.LENGTH_LONG).show();
                    return;
                }

                generator.view.context.getSoundsManager().playRawFile(generator.backgrounSound);
            }
        });
        btnPlayPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vie) {
                if (generator.pressSound.trim().equals("")) {
                    Toast.makeText(generator.view.context, "Select file first", Toast.LENGTH_LONG).show();
                    return;
                }
                generator.view.context.getSoundsManager().playRawFile(generator.pressSound);

            }
        });
        btnPlayCrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vie) {
                if (generator.crashSound.trim().equals("")) {
                    Toast.makeText(generator.view.context, "Select file first", Toast.LENGTH_LONG).show();
                    return;
                }
                generator.view.context.getSoundsManager().playRawFile(generator.crashSound);

            }
        });
        btnPlayFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vie) {
                if (generator.finishSound.trim().equals("")) {
                    Toast.makeText(generator.view.context, "Select file first", Toast.LENGTH_LONG).show();
                    return;
                }
                generator.view.context.getSoundsManager().playRawFile(generator.finishSound);

            }
        });


        btnSoundBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                final Dialog dialogChooseSound = new Dialog(generator.view.context);
                dialogChooseSound.requestWindowFeature(Window.FEATURE_NO_TITLE);
                final View chooseView = LayoutInflater.from(generator.view.context).inflate(R.layout.dialog_stages, null);
                ListView lv = (ListView) chooseView.findViewById(R.id.lv);
                lv.setAdapter(new ArrayAdapter<String>(generator.view.context, android.R.layout.simple_list_item_1, MainActivity.listRaw()));

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        generator.backgrounSound = adapterView.getItemAtPosition(i).toString();
                        btnSoundBackground.setText(generator.backgrounSound);
                        dialogChooseSound.dismiss();
                    }
                });
                dialogChooseSound.setContentView(chooseView);
                dialogChooseSound.show();

            }
        });

        btnSoundPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                final Dialog dialogChooseSound = new Dialog(generator.view.context);
                dialogChooseSound.requestWindowFeature(Window.FEATURE_NO_TITLE);
                View chooseView = LayoutInflater.from(generator.view.context).inflate(R.layout.dialog_stages, null);
                ListView lv = (ListView) chooseView.findViewById(R.id.lv);
                lv.setAdapter(new ArrayAdapter<String>(generator.view.context, android.R.layout.simple_list_item_1, MainActivity.listRaw()));
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        generator.pressSound = adapterView.getItemAtPosition(i).toString();
                        btnSoundPress.setText(generator.pressSound);

                        dialogChooseSound.dismiss();
                    }
                });
                dialogChooseSound.setContentView(chooseView);
                dialogChooseSound.show();

            }
        });

        btnSoundCrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                final Dialog dialogChooseSound = new Dialog(generator.view.context);
                dialogChooseSound.requestWindowFeature(Window.FEATURE_NO_TITLE);
                View chooseView = LayoutInflater.from(generator.view.context).inflate(R.layout.dialog_stages, null);
                ListView lv = (ListView) chooseView.findViewById(R.id.lv);
                lv.setAdapter(new ArrayAdapter<String>(generator.view.context, android.R.layout.simple_list_item_1, MainActivity.listRaw()));
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        generator.crashSound = adapterView.getItemAtPosition(i).toString();
                        btnSoundCrash.setText(generator.crashSound);

                        dialogChooseSound.dismiss();
                    }
                });
                dialogChooseSound.setContentView(chooseView);
                dialogChooseSound.show();

            }
        });

        btnSoundFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                final Dialog dialogChooseSound = new Dialog(generator.view.context);
                dialogChooseSound.requestWindowFeature(Window.FEATURE_NO_TITLE);
                View chooseView = LayoutInflater.from(generator.view.context).inflate(R.layout.dialog_stages, null);
                ListView lv = (ListView) chooseView.findViewById(R.id.lv);
                lv.setAdapter(new ArrayAdapter<String>(generator.view.context, android.R.layout.simple_list_item_1, MainActivity.listRaw()));
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        generator.finishSound = adapterView.getItemAtPosition(i).toString();
                        btnSoundFinish.setText(generator.finishSound);

                        dialogChooseSound.dismiss();
                    }
                });
                dialogChooseSound.setContentView(chooseView);
                dialogChooseSound.show();

            }
        });


    }


    private void refreashPathLayout() {

        tvBottomLeft.setVisibility(View.VISIBLE);
        tvBottomRight.setVisibility(View.VISIBLE);
        tvBottomTop.setVisibility(View.VISIBLE);

        tvLeftTop.setVisibility(View.VISIBLE);
        tvLeftRight.setVisibility(View.VISIBLE);

        tvRightTop.setVisibility(View.VISIBLE);


        tvStartTop.setVisibility(View.VISIBLE);
        tvStartBottom.setVisibility(View.VISIBLE);
        tvStartRight.setVisibility(View.VISIBLE);
        tvStartLeft.setVisibility(View.VISIBLE);

        tvFinishTop.setVisibility(View.VISIBLE);
        tvFinishBottom.setVisibility(View.VISIBLE);
        tvFinishLeft.setVisibility(View.VISIBLE);
        tvFinishRight.setVisibility(View.VISIBLE);
        //tvBlank.setVisibility(View.VISIBLE);

        if (generator.getStartRoute() == null) {
            tvBottomLeft.setVisibility(View.GONE);
            tvBottomRight.setVisibility(View.GONE);
            tvBottomTop.setVisibility(View.GONE);

            tvLeftTop.setVisibility(View.GONE);
            tvLeftRight.setVisibility(View.GONE);
            tvRightTop.setVisibility(View.GONE);

            tvFinishBottom.setVisibility(View.GONE);
            tvFinishTop.setVisibility(View.GONE);
            tvFinishLeft.setVisibility(View.GONE);
            tvFinishRight.setVisibility(View.GONE);
        }

        if (oldTile != null) {
            tvStartBottom.setVisibility(View.GONE);
            tvStartLeft.setVisibility(View.GONE);
            tvStartRight.setVisibility(View.GONE);
            tvStartTop.setVisibility(View.GONE);

            Log.d(TAG, "TEST " + oldTile.getDirection());
            switch (oldTile.getDirection()) {
                case LEFTRIGHT:
                case TOPRIGHT:
                case BOTTOMRIGHT: {
                    tvBottomRight.setVisibility(View.GONE);
                    tvBottomTop.setVisibility(View.GONE);
                    tvRightTop.setVisibility(View.GONE);
                    tvFinishBottom.setVisibility(View.GONE);
                    tvFinishTop.setVisibility(View.GONE);
                    tvFinishLeft.setVisibility(View.GONE);

                    break;
                }
                case RIGHTLEFT:
                case TOPLEFT:
                case BOTTOMLEFT: {
                    tvBottomLeft.setVisibility(View.GONE);
                    tvBottomTop.setVisibility(View.GONE);
                    tvLeftTop.setVisibility(View.GONE);
                    tvFinishBottom.setVisibility(View.GONE);
                    tvFinishTop.setVisibility(View.GONE);
                    tvFinishRight.setVisibility(View.GONE);
                    break;


                }

                case BOTTOMTOP:
                case LEFTTOP:
                case RIGHTTOP: {
                    tvLeftTop.setVisibility(View.GONE);
                    tvLeftRight.setVisibility(View.GONE);
                    tvRightTop.setVisibility(View.GONE);
                    tvFinishBottom.setVisibility(View.GONE);
                    tvFinishLeft.setVisibility(View.GONE);
                    tvFinishRight.setVisibility(View.GONE);
                    break;


                }
                case TOPBOTTOM:
                case LEFTBOTTOM:
                case RIGHTBOTTOM: {
                    tvBottomLeft.setVisibility(View.GONE);
                    tvBottomRight.setVisibility(View.GONE);
                    tvLeftRight.setVisibility(View.GONE);
                    tvFinishTop.setVisibility(View.GONE);
                    tvFinishLeft.setVisibility(View.GONE);
                    tvFinishRight.setVisibility(View.GONE);
                    break;


                }

            }
        }

    }

    public void initPathLayout(View v) {

        tvBottomLeft = (ImageView) v.findViewById(R.id.tv_bottomleft);
        tvBottomRight = (ImageView) v.findViewById(R.id.tv_bottomright);
        tvBottomTop = (ImageView) v.findViewById(R.id.tv_bottomtop);

        tvLeftTop = (ImageView) v.findViewById(R.id.tv_lefttop);
        tvLeftRight = (ImageView) v.findViewById(R.id.tv_leftright);

        tvRightTop = (ImageView) v.findViewById(R.id.tv_righttop);


        tvStartTop = (ImageView) v.findViewById(R.id.tv_starttop);
        tvStartBottom = (ImageView) v.findViewById(R.id.tv_startbottom);
        tvStartRight = (ImageView) v.findViewById(R.id.tv_startright);
        tvStartLeft = (ImageView) v.findViewById(R.id.tv_startleft);

        tvFinishTop = (ImageView) v.findViewById(R.id.tv_finishtop);
        tvFinishBottom = (ImageView) v.findViewById(R.id.tv_finishbottom);
        tvFinishLeft = (ImageView) v.findViewById(R.id.tv_finishleft);
        tvFinishRight = (ImageView) v.findViewById(R.id.tv_finishright);
        //  tvBlank = (ImageView) v.findViewById(R.id.tv_blank);


        refreashPathLayout();

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                context.getSoundsManager().playRawFile("construct");

                final int id = vi.getId();
                TileRoute r = null;

                switch (id) {
                    case R.id.tv_leftright: {
                        r = new TileRoute(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, tile.horizontalPos, tile.verticalPos, Route.Direction.LEFT.name(), Route.Direction.RIGHT.name(), Route.Type.ROUTE);
                        break;
                    }
                    case R.id.tv_lefttop: {
                        r = new TileRoute(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, tile.horizontalPos, tile.verticalPos, Route.Direction.LEFT.name(), Route.Direction.TOP.name(), Route.Type.ROUTE);

                        break;
                    }

                    case R.id.tv_righttop: {
                        r = new TileRoute(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, tile.horizontalPos, tile.verticalPos, Route.Direction.RIGHT.name(), Route.Direction.TOP.name(), Route.Type.ROUTE);

                        break;
                    }


                    case R.id.tv_bottomleft: {
                        r = new TileRoute(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, tile.horizontalPos, tile.verticalPos, Route.Direction.BOTTOM.name(), Route.Direction.LEFT.name(), Route.Type.ROUTE);

                        break;
                    }
                    case R.id.tv_bottomright: {
                        r = new TileRoute(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, tile.horizontalPos, tile.verticalPos, Route.Direction.BOTTOM.name(), Route.Direction.RIGHT.name(), Route.Type.ROUTE);

                        break;
                    }
                    case R.id.tv_bottomtop: {

                        r = new TileRoute(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, tile.horizontalPos, tile.verticalPos, Route.Direction.BOTTOM.name(), Route.Direction.TOP.name(), Route.Type.ROUTE);

                        break;
                    }


                    case R.id.tv_blank: {
                        r = new TileRoute(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, tile.horizontalPos, tile.verticalPos, Route.Direction.TOP.name(), Route.Direction.RIGHT.name(), Route.Type.TILE);
                        break;
                    }


                    case R.id.tv_finishtop: {
                        r = new TileRouteFinish(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, tile.horizontalPos, tile.verticalPos, generator.view, Route.Direction.BOTTOM.name(), Route.Direction.TOP.name());
                        break;
                    }
                    case R.id.tv_finishbottom: {
                        r = new TileRouteFinish(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, tile.horizontalPos, tile.verticalPos, generator.view, Route.Direction.TOP.name(), Route.Direction.BOTTOM.name());
                        break;
                    }
                    case R.id.tv_finishleft: {
                        r = new TileRouteFinish(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, tile.horizontalPos, tile.verticalPos, generator.view, Route.Direction.RIGHT.name(), Route.Direction.LEFT.name());
                        break;
                    }
                    case R.id.tv_finishright: {
                        r = new TileRouteFinish(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, tile.horizontalPos, tile.verticalPos, generator.view, Route.Direction.LEFT.name(), Route.Direction.RIGHT.name());
                        break;
                    }


                    case R.id.tv_starttop: {
                        r = new TileRouteStart(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, tile.horizontalPos, tile.verticalPos, Route.Direction.TOP.name(), Route.Direction.BOTTOM.name());

                        break;
                    }
                    case R.id.tv_startbottom: {
                        r = new TileRouteStart(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, tile.horizontalPos, tile.verticalPos, Route.Direction.BOTTOM.name(), Route.Direction.TOP.name());

                        break;
                    }
                    case R.id.tv_startleft: {

                        r = new TileRouteStart(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, tile.horizontalPos, tile.verticalPos, Route.Direction.LEFT.name(), Route.Direction.RIGHT.name());
                        break;
                    }
                    case R.id.tv_startright: {

                        r = new TileRouteStart(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, tile.horizontalPos, tile.verticalPos, Route.Direction.RIGHT.name(), Route.Direction.LEFT.name());
                        break;
                    }


                }
                TileRoute nextTile = null;

                if (r != null) {
                    if (!generator.routeSound.equals(""))
                        r.sound = generator.routeSound;
                    switch (r.getType()) {
                        case BLOCK:
                            r.setRouteColor(generator.blockColor);
                            break;
                        case ROUTE:
                            r.setRouteColor(generator.routeColor);
                            break;
                        case START:
                            r.setRouteColor(generator.routeColor);
                            break;
                        case FINISH:
                            r.setRouteColor(generator.routeColor);
                            break;
                        case FILL:
                            r.setRouteColor(generator.fillColor);
                            break;


                    }

                    generator.tiles.remove(tile);

                    generator.tiles.add(r);


                    //generator.startRouteConfiguration();
                    generator.startRouteConfiguration();
                    ArrayList<TileRoute> currentRoutes = generator.getPath();
                    refreashConstructLayout(currentRoutes);

                    Route.Movement movement = r.getDirection();
                    switch (movement) {
                        case LEFTRIGHT:
                        case BOTTOMRIGHT:
                        case TOPRIGHT:
                            nextTile = generator.findTile(r.horizontalPos + 1, r.verticalPos);
                            break;
                        case RIGHTLEFT:
                        case BOTTOMLEFT:
                        case TOPLEFT:
                            nextTile = generator.findTile(r.horizontalPos - 1, r.verticalPos);
                            break;
                        case BOTTOMTOP:
                        case RIGHTTOP:
                        case LEFTTOP:
                            nextTile = generator.findTile(r.horizontalPos, r.verticalPos - 1);
                            break;
                        case LEFTBOTTOM:
                        case RIGHTBOTTOM:
                        case TOPBOTTOM:
                            nextTile = generator.findTile(r.horizontalPos, r.verticalPos + 1);
                            break;
                    }


                    if (nextTile != null) {
                        tile = nextTile;
                        oldTile = r;
                        refreashPathLayout();
                        tvCurrentX.setText("current x : " + tile.horizontalPos);
                        tvCurrentY.setText("current y : " + tile.verticalPos);
                    }
                    //      initPathLayout(generator, nextTile, r);
                }
            }
        };
        tvBottomLeft.setOnClickListener(listener);
        tvBottomRight.setOnClickListener(listener);
        tvBottomTop.setOnClickListener(listener);

        tvRightTop.setOnClickListener(listener);

        tvLeftRight.setOnClickListener(listener);
        tvLeftTop.setOnClickListener(listener);


        //  tvBlank.setOnClickListener(listener);

        tvFinishBottom.setOnClickListener(listener);
        tvFinishLeft.setOnClickListener(listener);
        tvFinishRight.setOnClickListener(listener);
        tvFinishTop.setOnClickListener(listener);


        tvStartBottom.setOnClickListener(listener);
        tvStartLeft.setOnClickListener(listener);
        tvStartRight.setOnClickListener(listener);
        tvStartTop.setOnClickListener(listener);


    }

    private void loadRoute(Stage stage) {

        generator.loadRoute(stage);

        refreashGridSizeLayout();
        refreashSoundsLayout();
        refreashColorsLayout();
        refreashStringsLayout(stage);

    }

    public void loadOnlineMaze() {


        DAO.getStages(context, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                Log.d(TAG, response.toString());
                JSONObject out = null;
                try {
                    out = new JSONObject(response.toString());

                    JSONObject api = out.has("api") ? out.getJSONObject("api") : new JSONObject();
                    final JSONArray results = api.has("results") ? api.getJSONArray("results") : new JSONArray();

                    final Dialog stages = new Dialog(context);
                    stages.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

                    View v = LayoutInflater.from(context).inflate(R.layout.dialog_stages, null);
                    ListView listView = (ListView) v.findViewById(R.id.lv);
                    ArrayList<String> entries = new ArrayList<String>();
                    for (int i = 0; i < results.length(); i++) {
                        entries.add(Integer.toString(i));
                    }
                    listView.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, entries));

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            try {
                                loadRoute(Stage.valueOf(results.getJSONObject(i)));
                                generator.startRouteConfiguration();

                                ArrayList<TileRoute> currentRoutes = generator.getPath();
                                refreashConstructLayout(currentRoutes);
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

    public void loadMaze() {
        final Dialog stages = new Dialog(context);
        stages.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View v = LayoutInflater.from(context).inflate(R.layout.dialog_stages, null);
        ListView listView = (ListView) v.findViewById(R.id.lv);
        ArrayList<String> entries = new ArrayList<String>();
        for (int i = 0; i < context.stages.length(); i++) {
            entries.add(Integer.toString(i));
        }
        listView.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, entries));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int i, long l) {
                try {
                    loadRoute(Stage.valueOf(context.stages.getJSONObject(i)));
                    generator._id = null;
                    generator.startRouteConfiguration();

                    ArrayList<TileRoute> currentRoutes = generator.getPath();
                    refreashConstructLayout(currentRoutes);
                    stages.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        stages.setContentView(v);
        stages.show();
    }


    private void initConfigLayout(View v) {


        TextView tvLoad = (TextView) v.findViewById(R.id.tv_load);
        TextView tvLoadOnline = (TextView) v.findViewById(R.id.tv_load_online);
        TextView tvDeleteOnline = (TextView) v.findViewById(R.id.tv_delete_online);


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int id = v.getId();

                switch (id) {


                    case R.id.tv_load: {
                        loadMaze();


                        break;
                    }

                    case R.id.tv_load_online: {
                        loadOnlineMaze();
                        break;
                    }



                    case R.id.tv_delete_online: {


                        if (generator._id == null) {
                            Toast.makeText(generator.view.context, "First you have to load online stage", Toast.LENGTH_LONG).show();
                        } else {

                            AlertDialog dialog = new AlertDialog.Builder(context).setMessage("Remove stage?").setNegativeButton("no", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialogInterface, int i) {
                                    DAO.removeStage(generator.view.context, new Response.Listener() {
                                        @Override
                                        public void onResponse(Object response) {

                                            Log.d(TAG, "Stage removed ");
                                            Toast.makeText(generator.view.context, "Stage removed", Toast.LENGTH_LONG).show();
                                            generator._id = null;
                                            dialogInterface.dismiss();
                                        }
                                    }, generator._id);
                                }
                            }).show();


                        }
                        //deleteOnlineMaze();
                        break;
                    }


                }


            }
        };


        tvLoad.setOnClickListener(listener);
        tvLoadOnline.setOnClickListener(listener);
        tvDeleteOnline.setOnClickListener(listener);


    }


    private View getProgressRow(final TileRoute route, int index) {
        View row = LayoutInflater.from(context).inflate(R.layout.row_path_element, null);
        ImageView imgRoute = (ImageView) row.findViewById(R.id.img_route);
        ImageView imgTrash = (ImageView) row.findViewById(R.id.img_trash);
        TextView tvCount = (TextView) row.findViewById(R.id.tv_count);
        TextView tvMove = (TextView) row.findViewById(R.id.tv_movement);
        EditText etSpeedRatio = (EditText) row.findViewById(R.id.et_speed_ratio);
        final LinearLayout layoutBase = (LinearLayout) row.findViewById(R.id.layout_base);
        final LinearLayout layoutDetails = (LinearLayout) row.findViewById(R.id.layout_details);


        final TextView btnSound = (TextView) row.findViewById(R.id.btn_choose_sound);
        ImageView btnPlay = (ImageView) row.findViewById(R.id.btn_play_sound);


        if (route.sound != null && !route.sound.equals(""))
            btnSound.setText(route.sound);


        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vie) {
                if (route.sound.trim().equals("")) {
                    Toast.makeText(generator.view.context, "Select file first", Toast.LENGTH_LONG).show();
                    return;
                }

                generator.view.context.getSoundsManager().playRawFile(route.sound);
            }
        });


        btnSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                final Dialog dialogChooseSound = new Dialog(generator.view.context);
                dialogChooseSound.requestWindowFeature(Window.FEATURE_NO_TITLE);
                final View chooseView = LayoutInflater.from(generator.view.context).inflate(R.layout.dialog_stages, null);
                ListView lv = (ListView) chooseView.findViewById(R.id.lv);
                lv.setAdapter(new ArrayAdapter<String>(generator.view.context, android.R.layout.simple_list_item_1, MainActivity.listRaw()));

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        route.sound = adapterView.getItemAtPosition(i).toString();
                        btnSound.setText(route.sound);
                        dialogChooseSound.dismiss();
                    }
                });
                dialogChooseSound.setContentView(chooseView);
                dialogChooseSound.show();

            }
        });


        layoutBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutDetails.getVisibility() == View.GONE) {
                    layoutBase.setSelected(true);
                    layoutDetails.setVisibility(View.VISIBLE);
                } else {
                    layoutBase.setSelected(false);

                    layoutDetails.setVisibility(View.GONE);
                }
            }
        });

        TextView tvType = (TextView) row.findViewById(R.id.tv_type);
        TextView tvX = (TextView) row.findViewById(R.id.tv_x);
        TextView tvY = (TextView) row.findViewById(R.id.tv_y);

        etSpeedRatio.setText(Double.toString(route.speedRatio));
        etSpeedRatio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                try {
                    route.speedRatio = Double.parseDouble(editable.toString());

                } catch (Throwable t) {
                    route.speedRatio = 1;

                }
            }
        });
        tvType.setText(route.type.name());
        tvX.setText(Integer.toString(route.horizontalPos));
        tvY.setText(Integer.toString(route.verticalPos));
        tvCount.setText("#" + index);
        imgRoute.setImageDrawable(getRouteDrawable(route));
        tvMove.setText(route.getDirection().name());
        imgTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.getSoundsManager().playRawFile("construct");

                TileRoute r = new TileRoute(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, route.horizontalPos, route.verticalPos, Route.Direction.TOP.name(), Route.Direction.RIGHT.name(), Route.Type.TILE);
                generator.tiles.remove(route);
                generator.tiles.add(r);
                ArrayList<TileRoute> currentRoutes = generator.getPath();
                refreashConstructLayout(currentRoutes);

            }
        });

        return row;
    }

    private Drawable getRouteDrawable(TileRoute route) {
        Drawable d = null;

        switch (route.getDirection()) {
            case LEFTBOTTOM:
            case BOTTOMLEFT:
                d = context.getResources().getDrawable(R.drawable.arrow_bottom_left);
                break;
            case LEFTTOP:
            case TOPLEFT:
                d = context.getResources().getDrawable(R.drawable.arrow_top_left);
                break;
            case LEFTRIGHT:
            case RIGHTLEFT:
                d = context.getResources().getDrawable(R.drawable.arrow_horizontal);
                break;
            case RIGHTBOTTOM:
            case BOTTOMRIGHT:
                d = context.getResources().getDrawable(R.drawable.arrow_bottom_right);
                break;
            case RIGHTTOP:
            case TOPRIGHT:
                d = context.getResources().getDrawable(R.drawable.arrow_top_right);
                break;
            case TOPBOTTOM:
            case BOTTOMTOP:
                d = context.getResources().getDrawable(R.drawable.arrow_vertical);
                break;


        }
        return d;
    }


    private void refreashConstructLayout(ArrayList<TileRoute> currentRoutes) {

        tvElementsCountLabel.setText(Integer.toString(currentRoutes.size()) + " elements");

        layoutProgress.removeAllViews();
        for (int i = 0; i < currentRoutes.size(); i++) {
            layoutProgress.addView(getProgressRow(currentRoutes.get(i), i));
        }


    }

    private void initConstructLayout(View v) {


        tvElementsCountLabel = (TextView) v.findViewById(R.id.tv_construct_title);
        layoutProgress = (LinearLayout) v.findViewById(R.id.layout_progress_base);


    }

    private void initWorldsLayout(View v) {

        final World tmpWorld = new World();

        final ImageView btnSaveWorld = (ImageView) v.findViewById(R.id.btn_save_world);
        final EditText etWorldName = (EditText) v.findViewById(R.id.et_world_name);
        final LinearLayout tvBackgroundColor = (LinearLayout) v.findViewById(R.id.tv_world_background_color);
        final LinearLayout tvRouteColor = (LinearLayout) v.findViewById(R.id.tv_world_route_color);
        final LinearLayout tvDotColor = (LinearLayout) v.findViewById(R.id.tv_dot_world_color);
        final LinearLayout tvExplosionStartColor = (LinearLayout) v.findViewById(R.id.tv_world_explosion_start_color);
        final LinearLayout tvExplosionEndColor = (LinearLayout) v.findViewById(R.id.tv_world_explosion_end_color);


        final View colorBackground = v.findViewById(R.id.color_world_background);
        final View colorRoute = v.findViewById(R.id.color_world_route);
        final View colorDot = v.findViewById(R.id.color_world_dot);
        final View colorExplosionStart = v.findViewById(R.id.color_world_explosion_start);
        final View colorExplosionEnd = v.findViewById(R.id.color_world_explosion_end);


        colorBackground.setBackgroundColor(Color.parseColor(generator.backgroundColor));
        colorRoute.setBackgroundColor(Color.parseColor(generator.routeColor));
        colorDot.setBackgroundColor(Color.parseColor(generator.dotColor));
        colorExplosionStart.setBackgroundColor(Color.parseColor(generator.explosionStartColor));
        colorExplosionEnd.setBackgroundColor(Color.parseColor(generator.explosionEndColor));


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View vi) {

                int id = vi.getId();
                switch (id) {


                    case R.id.tv_world_background_color: {


                        AmbilWarnaDialog dialog = new AmbilWarnaDialog(generator.view.context, Color.parseColor(generator.backgroundColor), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                            @Override
                            public void onOk(AmbilWarnaDialog dialog, int color) {
                                // color is the color selected by the user.

                                colorBackground.setBackgroundColor(color);
                                tmpWorld.colorBackground = "#" + Integer.toHexString(color);


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
                    case R.id.tv_world_route_color: {


                        AmbilWarnaDialog dialog = new AmbilWarnaDialog(generator.view.context, Color.parseColor(generator.routeColor), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                            @Override
                            public void onOk(AmbilWarnaDialog dialog, int color) {
                                // color is the color selected by the user.

                                colorRoute.setBackgroundColor(color);
                                tmpWorld.colorRoute = "#" + Integer.toHexString(color);

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
                    case R.id.tv_dot_world_color: {


                        AmbilWarnaDialog dialog = new AmbilWarnaDialog(generator.view.context, Color.parseColor(generator.dotColor), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                            @Override
                            public void onOk(AmbilWarnaDialog dialog, int color) {
                                // color is the color selected by the user.

                                colorDot.setBackgroundColor(color);
                                tmpWorld.colorShip = "#" + Integer.toHexString(color);

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
                    case R.id.tv_world_explosion_start_color: {


                        AmbilWarnaDialog dialog = new AmbilWarnaDialog(generator.view.context, Color.BLUE, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                            @Override
                            public void onOk(AmbilWarnaDialog dialog, int color) {
                                // color is the color selected by the user.

                                colorExplosionStart.setBackgroundColor(color);
                                tmpWorld.colorExplosionStart = "#" + Integer.toHexString(color);

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
                    case R.id.tv_world_explosion_end_color: {


                        AmbilWarnaDialog dialog = new AmbilWarnaDialog(generator.view.context, Color.parseColor(generator.explosionEndColor), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                            @Override
                            public void onOk(AmbilWarnaDialog dialog, int color) {
                                // color is the color selected by the user.

                                colorExplosionEnd.setBackgroundColor(color);
                                tmpWorld.colorExplosionEnd = "#" + Integer.toHexString(color);

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
            }
        };


        tvBackgroundColor.setOnClickListener(listener);
        tvRouteColor.setOnClickListener(listener);
        tvDotColor.setOnClickListener(listener);
        tvExplosionStartColor.setOnClickListener(listener);
        tvExplosionEndColor.setOnClickListener(listener);


        final TextView btnSoundBackground = (TextView) v.findViewById(R.id.btn_choose_world_background);
        final TextView btnSoundPress = (TextView) v.findViewById(R.id.btn_choose_world_press);
        final TextView btnSoundCrash = (TextView) v.findViewById(R.id.btn_choose_world_crash);
        final TextView btnSoundFinish = (TextView) v.findViewById(R.id.btn_choose_world_finish);

        ImageView btnPlayBackground = (ImageView) v.findViewById(R.id.btn_play_world_background);
        ImageView btnPlayPress = (ImageView) v.findViewById(R.id.btn_play_world_press);
        ImageView btnPlayCrash = (ImageView) v.findViewById(R.id.btn_play_world_crash);
        ImageView btnPlayFinish = (ImageView) v.findViewById(R.id.btn_play_world_finish);

        if (!tmpWorld.soundBackground.equals(""))
            btnSoundBackground.setText(tmpWorld.soundBackground);
        if (!tmpWorld.soundPress.equals(""))
            btnSoundPress.setText(tmpWorld.soundPress);
        if (!tmpWorld.soundCrash.equals(""))
            btnSoundBackground.setText(tmpWorld.soundCrash);
        if (!tmpWorld.soundFinish.equals(""))
            btnSoundFinish.setText(tmpWorld.soundFinish);


        btnPlayBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vie) {
                if (tmpWorld.soundBackground.trim().equals("")) {
                    Toast.makeText(generator.view.context, "Select file first", Toast.LENGTH_LONG).show();
                    return;
                }

                generator.view.context.getSoundsManager().playRawFile(tmpWorld.soundBackground);
            }
        });
        btnPlayPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vie) {
                if (tmpWorld.soundPress.trim().equals("")) {
                    Toast.makeText(generator.view.context, "Select file first", Toast.LENGTH_LONG).show();
                    return;
                }
                generator.view.context.getSoundsManager().playRawFile(tmpWorld.soundPress);

            }
        });
        btnPlayCrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vie) {
                if (tmpWorld.soundCrash.trim().equals("")) {
                    Toast.makeText(generator.view.context, "Select file first", Toast.LENGTH_LONG).show();
                    return;
                }
                generator.view.context.getSoundsManager().playRawFile(tmpWorld.soundCrash);

            }
        });
        btnPlayFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vie) {
                if (tmpWorld.soundFinish.trim().equals("")) {
                    Toast.makeText(generator.view.context, "Select file first", Toast.LENGTH_LONG).show();
                    return;
                }
                generator.view.context.getSoundsManager().playRawFile(tmpWorld.soundFinish);

            }
        });


        btnSoundBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                final Dialog dialogChooseSound = new Dialog(generator.view.context);
                dialogChooseSound.requestWindowFeature(Window.FEATURE_NO_TITLE);
                final View chooseView = LayoutInflater.from(generator.view.context).inflate(R.layout.dialog_stages, null);
                ListView lv = (ListView) chooseView.findViewById(R.id.lv);
                lv.setAdapter(new ArrayAdapter<String>(generator.view.context, android.R.layout.simple_list_item_1, MainActivity.listRaw()));

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        tmpWorld.soundBackground = adapterView.getItemAtPosition(i).toString();
                        btnSoundBackground.setText(tmpWorld.soundBackground);
                        dialogChooseSound.dismiss();
                    }
                });
                dialogChooseSound.setContentView(chooseView);
                dialogChooseSound.show();

            }
        });

        btnSoundPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                final Dialog dialogChooseSound = new Dialog(generator.view.context);
                dialogChooseSound.requestWindowFeature(Window.FEATURE_NO_TITLE);
                View chooseView = LayoutInflater.from(generator.view.context).inflate(R.layout.dialog_stages, null);
                ListView lv = (ListView) chooseView.findViewById(R.id.lv);
                lv.setAdapter(new ArrayAdapter<String>(generator.view.context, android.R.layout.simple_list_item_1, MainActivity.listRaw()));
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        tmpWorld.soundPress = adapterView.getItemAtPosition(i).toString();
                        btnSoundPress.setText(tmpWorld.soundPress);

                        dialogChooseSound.dismiss();
                    }
                });
                dialogChooseSound.setContentView(chooseView);
                dialogChooseSound.show();

            }
        });

        btnSoundCrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                final Dialog dialogChooseSound = new Dialog(generator.view.context);
                dialogChooseSound.requestWindowFeature(Window.FEATURE_NO_TITLE);
                View chooseView = LayoutInflater.from(generator.view.context).inflate(R.layout.dialog_stages, null);
                ListView lv = (ListView) chooseView.findViewById(R.id.lv);
                lv.setAdapter(new ArrayAdapter<String>(generator.view.context, android.R.layout.simple_list_item_1, MainActivity.listRaw()));
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        tmpWorld.soundCrash = adapterView.getItemAtPosition(i).toString();
                        btnSoundCrash.setText(tmpWorld.soundCrash);

                        dialogChooseSound.dismiss();
                    }
                });
                dialogChooseSound.setContentView(chooseView);
                dialogChooseSound.show();

            }
        });

        btnSoundFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                final Dialog dialogChooseSound = new Dialog(generator.view.context);
                dialogChooseSound.requestWindowFeature(Window.FEATURE_NO_TITLE);
                View chooseView = LayoutInflater.from(generator.view.context).inflate(R.layout.dialog_stages, null);
                ListView lv = (ListView) chooseView.findViewById(R.id.lv);
                lv.setAdapter(new ArrayAdapter<String>(generator.view.context, android.R.layout.simple_list_item_1, MainActivity.listRaw()));
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        tmpWorld.soundFinish = adapterView.getItemAtPosition(i).toString();
                        btnSoundFinish.setText(tmpWorld.soundFinish);

                        dialogChooseSound.dismiss();
                    }
                });
                dialogChooseSound.setContentView(chooseView);
                dialogChooseSound.show();

            }
        });


        btnSaveWorld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etWorldName.getText().toString().trim().equals("")) {
                    Toast.makeText(context, "set world name", Toast.LENGTH_LONG).show();
                    return;
                }
                if (tmpWorld.soundBackground.equals("")) {
                    Toast.makeText(context, "choose background sound", Toast.LENGTH_LONG).show();
                    return;
                }
                if (tmpWorld.soundPress.equals("")) {
                    Toast.makeText(context, "choose press sound", Toast.LENGTH_LONG).show();
                    return;
                }
                if (tmpWorld.soundCrash.equals("")) {
                    Toast.makeText(context, "choose crash sound", Toast.LENGTH_LONG).show();
                    return;
                }
                if (tmpWorld.soundFinish.equals("")) {
                    Toast.makeText(context, "choose finish sound", Toast.LENGTH_LONG).show();
                    return;
                }
                tmpWorld.name = etWorldName.getText().toString();
                //tmpWorld.id=Long.toString(System.currentTimeMillis());
                //addWorld(tmpWorld);

                DAO.addWorld(generator.view.context, tmpWorld.toJson(), new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        Log.d(TAG, response.toString());
                        Toast.makeText(generator.view.context, "World created", Toast.LENGTH_LONG).show();
                    }
                }, null);

            }
        });


    }

    private void refreashColorsLayout() {
        colorBackground.setBackgroundColor(Color.parseColor(generator.backgroundColor));
        colorRoute.setBackgroundColor(Color.parseColor(generator.routeColor));
        colorDot.setBackgroundColor(Color.parseColor(generator.dotColor));
        colorExplosionStart.setBackgroundColor(Color.parseColor(generator.explosionStartColor));
        colorExplosionEnd.setBackgroundColor(Color.parseColor(generator.explosionEndColor));
    }

    private void initColorsLayout(View v) {


        final LinearLayout tvBackgroundColor = (LinearLayout) v.findViewById(R.id.tv_background_color);
        final LinearLayout tvRouteColor = (LinearLayout) v.findViewById(R.id.tv_route_color);
        final LinearLayout tvDotColor = (LinearLayout) v.findViewById(R.id.tv_dot_color);
        final LinearLayout tvExplosionStartColor = (LinearLayout) v.findViewById(R.id.tv_explosion_start_color);
        final LinearLayout tvExplosionEndColor = (LinearLayout) v.findViewById(R.id.tv_explosion_end_color);


        colorBackground = v.findViewById(R.id.color_background);
        colorRoute = v.findViewById(R.id.color_route);
        colorDot = v.findViewById(R.id.color_dot);
        colorExplosionStart = v.findViewById(R.id.color_explosion_start);
        colorExplosionEnd = v.findViewById(R.id.color_explosion_end);


        refreashColorsLayout();


        tvBackgroundColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(generator.view.context, Color.parseColor(generator.backgroundColor), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        // color is the color selected by the user.

                        colorBackground.setBackgroundColor(color);
                        generator.backgroundColor = "#" + Integer.toHexString(color);


                        generator.refreashMaze();
                        dialog.getDialog().dismiss();
                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // cancel was selected by the user
                    }
                });

                dialog.show();
            }
        });
        tvRouteColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(generator.view.context, Color.parseColor(generator.routeColor), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        // color is the color selected by the user.

                        colorRoute.setBackgroundColor(color);
                        generator.routeColor = "#" + Integer.toHexString(color);

                        generator.refreashMaze();
                        dialog.getDialog().dismiss();
                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // cancel was selected by the user
                    }
                });

                dialog.show();
            }
        });
        tvDotColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(generator.view.context, Color.parseColor(generator.dotColor), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        // color is the color selected by the user.

                        colorDot.setBackgroundColor(color);
                        generator.dotColor = "#" + Integer.toHexString(color);


                        generator.refreashMaze();
                        dialog.getDialog().dismiss();
                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // cancel was selected by the user
                    }
                });

                dialog.show();
            }
        });
        tvExplosionStartColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(generator.view.context, Color.BLUE, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        // color is the color selected by the user.

                        colorExplosionStart.setBackgroundColor(color);
                        generator.explosionStartColor = "#" + Integer.toHexString(color);


                        generator.refreashMaze();
                        dialog.getDialog().dismiss();
                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // cancel was selected by the user
                    }
                });

                dialog.show();
            }
        });
        tvExplosionEndColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AmbilWarnaDialog dialog = new AmbilWarnaDialog(generator.view.context, Color.parseColor(generator.explosionEndColor), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        // color is the color selected by the user.

                        colorExplosionEnd.setBackgroundColor(color);
                        generator.explosionEndColor = "#" + Integer.toHexString(color);


                        generator.refreashMaze();
                        dialog.getDialog().dismiss();
                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // cancel was selected by the user
                    }
                });

                dialog.show();
            }
        });


    }

    private void refreashGridSizeLayout() {
        etWidth.setText(Integer.toString(generator.gridX));
        etHeight.setText(Integer.toString(generator.gridY));
    }

    private void initGridSizeLayout(View v) {
        Button btnSave = (Button) v.findViewById(R.id.btn_save_grid);

        etWidth = (EditText) v.findViewById(R.id.et_width);
        etHeight = (EditText) v.findViewById(R.id.et_height);

        refreashGridSizeLayout();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textWidht = etWidth.getText().toString();
                String textHeight = etHeight.getText().toString();

                if (textWidht.trim().equals("")) {
                    Toast.makeText(generator.view.context, "gridX is required", Toast.LENGTH_LONG).show();
                    return;
                }
                if (textHeight.trim().equals("")) {
                    Toast.makeText(generator.view.context, "gridY is required", Toast.LENGTH_LONG).show();
                    return;
                }

                generator.initGrid(Integer.parseInt(textWidht), Integer.parseInt(textHeight));
                refreashGridSizeLayout();
            }
        });


    }

    public void setCurrentTile(TileRoute tile) {
        this.tile = tile;
        tvCurrentX.setText("current x : " + this.tile.horizontalPos);
        tvCurrentY.setText("current y : " + this.tile.verticalPos);

        this.oldTile = null;
        refreashPathLayout();
        context.drawer.openDrawer(context.drawerContent);
        if (layoutRoutes.getParent() == null) {
            layoutRutesBase.addView(layoutRoutes);
            toggleRoutes.setSelected(true);

        }


    }

    public void deleteWorlds() {
        File file = new File(context.getExternalCacheDir(), "worlds.txt");
        if (file.exists()) {
            file.delete();
            Log.d(TAG, "worlds file deleted!");
            Toast.makeText(context, "worlds file deleted!", Toast.LENGTH_LONG).show();
        }
    }

    public void updateWorld() {
        try {
            JSONArray savedWorlds = new JSONArray(loadWorlds());
            JSONArray newWorlds = new JSONArray();
            for (int i = 0; i < savedWorlds.length(); i++) {
                World w = World.valueOf(savedWorlds.getJSONObject(i));
                if (w.id.equals(currentWorld.id)) {
                    newWorlds.put(currentWorld.toJson());
                } else
                    newWorlds.put(savedWorlds.getJSONObject(i));


            }

            deleteWorlds();
            File file = new File(context.getExternalCacheDir(), "worlds.txt");

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(newWorlds.toString().getBytes());
            fos.close();

        } catch (Throwable e) {
            e.printStackTrace();
        }

    }

    public String loadWorlds() {

        //deleteWorlds();
        File file = new File(context.getExternalCacheDir(), "worlds.txt");


        //Read text from file
        StringBuilder text = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            Log.d(TAG, "error " + e.toString());
            //You'll need to add proper error handling here
        }

        if (!text.toString().trim().equals(""))
            return text.toString();
        else
            return "[]";
    }


    public void addWorld(World w) {
        String data = loadWorlds();
        JSONArray worlds = null;
        try {
            worlds = new JSONArray(data);

            worlds.put(w.toJson());

            File file = new File(context.getExternalCacheDir(), "worlds.txt");

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(worlds.toString().getBytes());
            fos.close();
            Log.d(TAG, "world added " + worlds.toString());
            Toast.makeText(context, "world added ", Toast.LENGTH_LONG).show();

        } catch (Throwable e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }
    }

    private void refreashMainLayout() {


        Log.d(TAG, "Refreash maina layout");

        if (currentWorld == null) {
            layoutGridBase.setVisibility(View.GONE);
            layoutColorsBase.setVisibility(View.GONE);
            layoutConfigBase.setVerticalGravity(View.GONE);
            layoutRutesBase.setVisibility(View.GONE);
            layoutSoundsBase.setVisibility(View.GONE);
            layoutStringsBase.setVisibility(View.GONE);
            layoutConfigBase.setVisibility(View.GONE);
            layoutConstructBase.setVisibility(View.GONE);
            toggleSave.setVisibility(View.GONE);


        } else {

            //generator.loadWorld(currentWorld);
            //refreashColorsLayout();
            //refreashSoundsLayout();

            layoutGridBase.setVisibility(View.VISIBLE);
            layoutColorsBase.setVisibility(View.VISIBLE);
            layoutConfigBase.setVerticalGravity(View.VISIBLE);
            layoutRutesBase.setVisibility(View.VISIBLE);
            layoutSoundsBase.setVisibility(View.VISIBLE);
            layoutStringsBase.setVisibility(View.VISIBLE);
            layoutConfigBase.setVisibility(View.VISIBLE);
            layoutConstructBase.setVisibility(View.VISIBLE);
            toggleSave.setVisibility(View.VISIBLE);
            toggleWorld.setVisibility(View.GONE);
            layoutChooseWorldBase.setVisibility(View.GONE);

            generator.loadWorld(currentWorld);
            refreashColorsLayout();
            refreashSoundsLayout();
            refreashGridSizeLayout();
            refreashPathLayout();


        }

    }


    public View onCreateView(final Generator generator) {

        this.generator = generator;

        View v = LayoutInflater.from(context).inflate(R.layout.layout_generator, null);

        initGridSizeLayout(v);
        initColorsLayout(v);
        initConfigLayout(v);
        initPathLayout(v);
        initSoundsLayout(v);
        initStringsLayout(v);
        initConstructLayout(v);
        initWorldsLayout(v);

        tvCurrentX = (TextView) v.findViewById(R.id.tv_current_x);
        tvCurrentY = (TextView) v.findViewById(R.id.tv_current_y);


        layoutGridBase = (LinearLayout) v.findViewById(R.id.layout_grid_size_base);
        toggleGridSize = (LinearLayout) v.findViewById(R.id.toggle_grid_size);
        layoutGridsize = (LinearLayout) v.findViewById(R.id.layout_grid_size);

        layoutChooseWorldBase = (LinearLayout) v.findViewById(R.id.layout_choose_world_base);
        toggleChooseWorld = (LinearLayout) v.findViewById(R.id.toggle_choose_world);
        layoutWorldsList = (LinearLayout) v.findViewById(R.id.layout_worlds_list);


        layoutColorsBase = (LinearLayout) v.findViewById(R.id.layout_colors_base);
        toggleColors = (LinearLayout) v.findViewById(R.id.toggle_colors);
        layoutcolors = (LinearLayout) v.findViewById(R.id.layout_colors);

        layoutConfigBase = (LinearLayout) v.findViewById(R.id.layout_config_base);
        toggleConfig = (LinearLayout) v.findViewById(R.id.toggle_config);
        layoutConfig = (LinearLayout) v.findViewById(R.id.layout_config);

        layoutRutesBase = (LinearLayout) v.findViewById(R.id.layout_routes_base);
        toggleRoutes = (LinearLayout) v.findViewById(R.id.toggle_routes);
        layoutRoutes = (LinearLayout) v.findViewById(R.id.layout_routes);

        layoutSoundsBase = (LinearLayout) v.findViewById(R.id.layout_sounds_base);
        toggleSounds = (LinearLayout) v.findViewById(R.id.toggle_sounds);
        layoutSounds = (LinearLayout) v.findViewById(R.id.layout_sounds);

        layoutStringsBase = (LinearLayout) v.findViewById(R.id.layout_strings_base);
        toggleString = (LinearLayout) v.findViewById(R.id.toggle_strings);
        layoutStrings = (LinearLayout) v.findViewById(R.id.layout_strings);

        layoutWorldBase = (LinearLayout) v.findViewById(R.id.layout_world_base);
        toggleWorld = (LinearLayout) v.findViewById(R.id.toggle_world);
        layoutWorld = (LinearLayout) v.findViewById(R.id.layout_world);

        layoutConstructBase = (LinearLayout) v.findViewById(R.id.layout_construct_base);
        toggleConstruct = (LinearLayout) v.findViewById(R.id.toggle_construct);
        layoutConstruct = (LinearLayout) v.findViewById(R.id.layout_construct);

        toggleSave = (LinearLayout) v.findViewById(R.id.toggle_save);


        layoutGridBase.removeView(layoutGridsize);
        layoutColorsBase.removeView(layoutcolors);
        layoutConfigBase.removeView(layoutConfig);
        layoutRutesBase.removeView(layoutRoutes);
        layoutSoundsBase.removeView(layoutSounds);
        layoutStringsBase.removeView(layoutStrings);
        layoutWorldBase.removeView(layoutWorld);
        layoutConstructBase.removeView(layoutConstruct);
        layoutChooseWorldBase.removeView(layoutWorldsList);

        refreashMainLayout();

        toggleGridSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutGridsize.getParent() == null) {
                    layoutGridBase.addView(layoutGridsize);
                    toggleGridSize.setSelected(true);
                } else {
                    layoutGridBase.removeView(layoutGridsize);
                    toggleGridSize.setSelected(false);
                }
            }
        });

        toggleColors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutcolors.getParent() == null) {
                    layoutColorsBase.addView(layoutcolors);
                    toggleColors.setSelected(true);

                } else {
                    layoutColorsBase.removeView(layoutcolors);
                    toggleColors.setSelected(false);

                }
            }
        });

        toggleConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutConfig.getParent() == null) {
                    layoutConfigBase.addView(layoutConfig);
                    toggleConfig.setSelected(true);

                } else {
                    layoutConfigBase.removeView(layoutConfig);
                    toggleConfig.setSelected(false);

                }
            }
        });

        toggleRoutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutRoutes.getParent() == null) {
                    layoutRutesBase.addView(layoutRoutes);
                    toggleRoutes.setSelected(true);

                } else {
                    layoutRutesBase.removeView(layoutRoutes);
                    toggleRoutes.setSelected(false);

                }
            }
        });

        toggleSounds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutSounds.getParent() == null) {
                    layoutSoundsBase.addView(layoutSounds);
                    toggleSounds.setSelected(true);

                } else {
                    layoutSoundsBase.removeView(layoutSounds);
                    toggleSounds.setSelected(false);

                }
            }
        });

        toggleString.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutStrings.getParent() == null) {
                    layoutStringsBase.addView(layoutStrings);
                    toggleString.setSelected(true);

                } else {
                    layoutStringsBase.removeView(layoutStrings);
                    toggleString.setSelected(false);

                }
            }
        });

        toggleWorld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutWorld.getParent() == null) {
                    layoutWorldBase.addView(layoutWorld);
                    toggleWorld.setSelected(true);

                } else {
                    layoutWorldBase.removeView(layoutWorld);
                    toggleWorld.setSelected(false);

                }
            }
        });

        toggleConstruct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutConstruct.getParent() == null) {
                    layoutConstructBase.addView(layoutConstruct);
                    toggleConstruct.setSelected(true);

                } else {
                    layoutConstructBase.removeView(layoutConstruct);
                    toggleConstruct.setSelected(false);

                }
            }
        });

        toggleChooseWorld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (layoutWorldsList.getParent() == null) {
                    DAO.getWorlds(context, new Response.Listener() {
                        @Override
                        public void onResponse(Object response) {
                            Log.d(TAG, "worlds response");
                            Log.d(TAG, response.toString());

                            JSONObject res = null;
                            try {
                                res = new JSONObject(response.toString());

                                JSONObject api = res.has("api") ? res.getJSONObject("api") : new JSONObject();
                                JSONArray jsonArray = api.has("results") ? api.getJSONArray("results") : new JSONArray();


                                layoutWorldsList.removeAllViews();


                                for (int i = 0; i < jsonArray.length(); i++) {
                                    final World w = World.valueOf(jsonArray.getJSONObject(i));
                                    View v = getWorldRow(w, i);

                                    layoutWorldsList.addView(v);
                                    v.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            currentWorld = w;
                                            refreashMainLayout();

                                        }
                                    });

                                }


                                toggleChooseWorld.setSelected(true);
                                layoutChooseWorldBase.addView(layoutWorldsList);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "get worlds error ");
                            Log.d(TAG, error.toString());
                        }
                    }, false);
                }
                else
                {
                    toggleChooseWorld.setSelected(false);
                    layoutChooseWorldBase.removeView(layoutWorldsList);

                }

            }
        });


        toggleSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generator.saveMaze();
            }
        });

        tvCurrentX.setText("current x : " + this.tile.horizontalPos);
        tvCurrentY.setText("current y : " + this.tile.verticalPos);

        return v;
    }

    private View getWorldRow(final World world, int index) {
        final View row = LayoutInflater.from(context).inflate(R.layout.row_world, null);

        TextView tvWorldCount = (TextView) row.findViewById(R.id.tv_world_number);
        TextView tvWorldName = (TextView) row.findViewById(R.id.tv_world_name);
        TextView tvWorldStages = (TextView) row.findViewById(R.id.tv_stages_count);
        ImageView btnTrash = (ImageView) row.findViewById(R.id.btn_trash);


        tvWorldCount.setText("#" + index);
        tvWorldName.setTextColor(Color.parseColor(world.colorBackground));
        tvWorldName.setText(world.name);
        tvWorldStages.setText("Stages count: " + world.stages.size());
        btnTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog dialog = new AlertDialog.Builder(context).setMessage("Remove world?").setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        DAO.removeWorld(context, new Response.Listener() {
                            @Override
                            public void onResponse(Object response) {
                                layoutWorldsList.removeView(row);
                                dialogInterface.dismiss();

                            }
                        }, world.id);
                    }
                }).create();

                dialog.show();
            }
        });


        return row;
    }


}
