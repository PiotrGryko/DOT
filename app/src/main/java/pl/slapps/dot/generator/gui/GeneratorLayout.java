package pl.slapps.dot.generator.gui;

import android.app.Dialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import pl.slapps.dot.DAO;
import pl.slapps.dot.MainActivity;
import pl.slapps.dot.R;
import pl.slapps.dot.generator.Generator;
import pl.slapps.dot.generator.TileRoute;
import pl.slapps.dot.model.Stage;
import pl.slapps.dot.model.World;

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



    private MainActivity context;
    public Generator generator;

    /*
    currently selected tile and previously selected tile
     */
    public TileRoute tile;
    public TileRoute oldTile;

    /*
    currently setted world
     */
    public World currentWorld;


    private TextView tvCurrentX;
    private TextView tvCurrentY;
    private TextView tvElementsCountLabel;

    private GeneratorLayoutWorlds layoutWorlds;
    private GeneratorLayoutGrid layoutGrid;
    private GeneratorLayoutColors layoutColors;
    private GeneratorLayoutConfig layoutConfig;
    private GeneratorLayoutPath layoutRoutes;
    private GeneratorLayoutSounds layoutSounds;
    private GeneratorLayoutStrings layoutStrings;
    private GeneratorLayoutWorldsList layoutWorldsList;
    public GeneratorLayoutConstruct layoutConstruct;




    public GeneratorLayout(MainActivity context, TileRoute tile) {
        this.context = context;
        this.tile = tile;

        this.layoutWorlds = new GeneratorLayoutWorlds();
        this.layoutGrid=new GeneratorLayoutGrid();
        this.layoutColors = new GeneratorLayoutColors();
        this.layoutConfig=new GeneratorLayoutConfig();
        this.layoutRoutes = new GeneratorLayoutPath();
        this.layoutSounds=new GeneratorLayoutSounds();
        this.layoutStrings = new GeneratorLayoutStrings();
        this.layoutWorldsList = new GeneratorLayoutWorldsList();
        this.layoutConstruct=new GeneratorLayoutConstruct();
    }

    public World getCurrentWorld() {
        return currentWorld;
    }






    private void loadRoute(Stage stage) {

        generator.loadRoute(stage);

        layoutGrid.refreashLayout();
        layoutColors.refreashLayout();
        layoutSounds.refreashLayout();
        layoutStrings.refreashLayout(stage);


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
                                layoutConstruct.refreashLayout(currentRoutes);
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
                    layoutConstruct.refreashLayout(currentRoutes);
                    stages.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        stages.setContentView(v);
        stages.show();
    }








    public void refreashConstructLength(String label)
    {
        tvElementsCountLabel.setText(label);
    }


    public void refreashCurrentTileLabels()
    {
        tvCurrentX.setText("current x : " + this.tile.horizontalPos);
        tvCurrentY.setText("current y : " + this.tile.verticalPos);
    }


    public void setCurrentTile(TileRoute tile) {
        this.tile = tile;
        //this.tile.getWalls().get(0).getColor()[0]=1.0f;
        refreashCurrentTileLabels();

        this.oldTile = null;
        layoutRoutes.refreashLayout();
        context.drawer.openDrawer(context.drawerContent);
        if (layoutRoutes.getLayout().getParent() == null) {
            layoutRutesBase.addView(layoutRoutes.getLayout());
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

    public void refreashMainLayout() {


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

            layoutSounds.refreashLayout();
            layoutGrid.refreashLayout();
            layoutColors.refreashLayout();
            layoutRoutes.refreashLayout();


        }

    }


    public View onCreateView(final Generator generator) {

        this.generator = generator;

        View v = LayoutInflater.from(context).inflate(R.layout.layout_generator, null);

        layoutWorlds.initLayout(this);
        layoutGrid.initLayout(this);
        layoutColors.initLayout(this);
        layoutConfig.initLayout(this);
        layoutRoutes.initLayout(this);
        layoutSounds.initLayout(this);
        layoutStrings.initLayout(this);
        layoutWorldsList.initLayout(this);
        layoutConstruct.initLayout(this);

        tvCurrentX = (TextView) v.findViewById(R.id.tv_current_x);
        tvCurrentY = (TextView) v.findViewById(R.id.tv_current_y);

        tvElementsCountLabel = (TextView) v.findViewById(R.id.tv_construct_title);


        layoutGridBase = (LinearLayout) v.findViewById(R.id.layout_grid_size_base);
        toggleGridSize = (LinearLayout) v.findViewById(R.id.toggle_grid_size);

        layoutChooseWorldBase = (LinearLayout) v.findViewById(R.id.layout_choose_world_base);
        toggleChooseWorld = (LinearLayout) v.findViewById(R.id.toggle_choose_world);


        layoutColorsBase = (LinearLayout) v.findViewById(R.id.layout_colors_base);
        toggleColors = (LinearLayout) v.findViewById(R.id.toggle_colors);

        layoutConfigBase = (LinearLayout) v.findViewById(R.id.layout_config_base);
        toggleConfig = (LinearLayout) v.findViewById(R.id.toggle_config);

        layoutRutesBase = (LinearLayout) v.findViewById(R.id.layout_routes_base);
        toggleRoutes = (LinearLayout) v.findViewById(R.id.toggle_routes);

        layoutSoundsBase = (LinearLayout) v.findViewById(R.id.layout_sounds_base);
        toggleSounds = (LinearLayout) v.findViewById(R.id.toggle_sounds);

        layoutStringsBase = (LinearLayout) v.findViewById(R.id.layout_strings_base);
        toggleString = (LinearLayout) v.findViewById(R.id.toggle_strings);

        layoutWorldBase = (LinearLayout) v.findViewById(R.id.layout_world_base);
        toggleWorld = (LinearLayout) v.findViewById(R.id.toggle_world);

        layoutConstructBase = (LinearLayout) v.findViewById(R.id.layout_construct_base);
        toggleConstruct = (LinearLayout) v.findViewById(R.id.toggle_construct);

        toggleSave = (LinearLayout) v.findViewById(R.id.toggle_save);


        refreashMainLayout();

        toggleGridSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutGrid.getLayout().getParent() == null) {
                    layoutGridBase.addView(layoutGrid.getLayout());
                    toggleGridSize.setSelected(true);
                } else {
                    layoutGridBase.removeView(layoutGrid.getLayout());
                    toggleGridSize.setSelected(false);
                }
            }
        });

        toggleColors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutColors.getLayout().getParent() == null) {
                    layoutColorsBase.addView(layoutColors.getLayout());
                    toggleColors.setSelected(true);

                } else {
                    layoutColorsBase.removeView(layoutColors.getLayout());
                    toggleColors.setSelected(false);

                }
            }
        });

        toggleConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutConfig.getLayout().getParent() == null) {
                    layoutConfigBase.addView(layoutConfig.getLayout());
                    toggleConfig.setSelected(true);

                } else {
                    layoutConfigBase.removeView(layoutConfig.getLayout());
                    toggleConfig.setSelected(false);

                }
            }
        });

        toggleRoutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutRoutes.getLayout().getParent() == null) {
                    layoutRutesBase.addView(layoutRoutes.getLayout());
                    toggleRoutes.setSelected(true);

                } else {
                    layoutRutesBase.removeView(layoutRoutes.getLayout());
                    toggleRoutes.setSelected(false);

                }
            }
        });

        toggleSounds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutSounds.getLayout().getParent() == null) {
                    layoutSoundsBase.addView(layoutSounds.getLayout());
                    toggleSounds.setSelected(true);

                } else {
                    layoutSoundsBase.removeView(layoutSounds.getLayout());
                    toggleSounds.setSelected(false);

                }
            }
        });

        toggleString.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutStrings.getLayout().getParent() == null) {
                    layoutStringsBase.addView(layoutStrings.getLayout());
                    toggleString.setSelected(true);

                } else {
                    layoutStringsBase.removeView(layoutStrings.getLayout());
                    toggleString.setSelected(false);

                }
            }
        });

        toggleWorld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutWorlds.getLayout().getParent() == null) {
                    layoutWorldBase.addView(layoutWorlds.getLayout());
                    toggleWorld.setSelected(true);

                } else {
                    layoutWorldBase.removeView(layoutWorlds.getLayout());
                    toggleWorld.setSelected(false);

                }
            }
        });

        toggleConstruct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutConstruct.getLayout().getParent() == null) {
                    layoutConstructBase.addView(layoutConstruct.getLayout());
                    toggleConstruct.setSelected(true);

                } else {
                    layoutConstructBase.removeView(layoutConstruct.getLayout());
                    toggleConstruct.setSelected(false);

                }
            }
        });

        toggleChooseWorld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (layoutWorldsList.getLayout().getParent() == null) {
                    layoutWorldsList.buildWorldsList(new GeneratorLayoutWorldsList.OnListBuildedListener() {
                        @Override
                        public void onListBuilded() {
                            toggleChooseWorld.setSelected(true);
                            layoutChooseWorldBase.addView(layoutWorldsList.getLayout());
                        }
                    });
                }
                else
                {
                    toggleChooseWorld.setSelected(false);
                    layoutChooseWorldBase.removeView(layoutWorldsList.getLayout());

                }

            }
        });


        toggleSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generator.saveMaze();
            }
        });

        refreashCurrentTileLabels();
        return v;
    }



}
