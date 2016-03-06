package pl.slapps.dot.generator.gui;

import android.app.Dialog;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
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
import pl.slapps.dot.adapter.AdapterStages;
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
    //private LinearLayout layoutRutesBase;
    private LinearLayout layoutSoundsBase;
    private LinearLayout layoutStringsBase;
    private LinearLayout layoutWorldBase;
    private LinearLayout layoutChooseWorldBase;
    private LinearLayout layoutPreviewBase;
    private LinearLayout layoutLightsBase;
    private LinearLayout layoutEffectsBase;


    /*
    drawer Main buttons
     */
    private LinearLayout toggleConstruct;
    private LinearLayout toggleColors;
    private LinearLayout toggleGridSize;
    private LinearLayout toggleConfig;
    private LinearLayout toggleSounds;
    private LinearLayout toggleString;
    private LinearLayout toggleWorld;
    private LinearLayout togglePreview;
    private LinearLayout toggleLights;
    private LinearLayout toggleEffects;

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
    private World currentWorld;


    private GeneratorLayoutWorlds layoutWorlds;
    private GeneratorLayoutGrid layoutGrid;
    private GeneratorLayoutColors layoutColors;
    private GeneratorLayoutConfig layoutConfig;
    //private GeneratorLayoutPath layoutRoutes;
    private GeneratorLayoutSounds layoutSounds;
    private GeneratorLayoutStrings layoutStrings;
    private GeneratorLayoutWorldsList layoutWorldsList;
    private GeneratorLayoutPreview layoutPreview;
    private GeneratorLayoutLights layoutLights;
    private GeneratorLayoutEffects layoutEffects;

    public GeneratorLayoutConstruct layoutConstruct;


    public GeneratorLayout(MainActivity context, Generator generator, TileRoute tile) {
        this.context = context;
        this.tile = tile;
        this.generator = generator;


        this.layoutWorlds = new GeneratorLayoutWorlds();
        this.layoutGrid = new GeneratorLayoutGrid();
        this.layoutColors = new GeneratorLayoutColors();
        this.layoutConfig = new GeneratorLayoutConfig();
        //this.layoutRoutes = new GeneratorLayoutPath();
        this.layoutSounds = new GeneratorLayoutSounds();
        this.layoutStrings = new GeneratorLayoutStrings();
        this.layoutWorldsList = new GeneratorLayoutWorldsList();
        this.layoutConstruct = new GeneratorLayoutConstruct();
        this.layoutPreview = new GeneratorLayoutPreview();
        this.layoutLights = new GeneratorLayoutLights();
        this.layoutEffects = new GeneratorLayoutEffects();

    }

    public World getCurrentWorld() {
        return currentWorld;
    }

    public void setCurrentWorld(World world) {
        currentWorld = world;
        loadWorld(currentWorld);

        refreshControlls();
    }


    private void loadRoute(Stage stage) {

        generator.loadRoute(stage);

        layoutGrid.refreashLayout();
        layoutColors.refreashLayout();
        layoutSounds.refreashLayout();
        layoutStrings.refreashLayout(stage);
        layoutLights.refreashLayout();
        layoutEffects.refreashLayout();



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

                    final Dialog dialogStages = new Dialog(context);
                    dialogStages.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

                    View v = LayoutInflater.from(context).inflate(R.layout.dialog_worlds, null);

                    GridView gridView = (GridView) v.findViewById(R.id.grid_view);


                    ArrayList<Stage> entries = new ArrayList<Stage>();
                    for (int i = 0; i < results.length(); i++) {
                        entries.add(Stage.valueOf(results.getJSONObject(i)));
                    }
                    gridView.setAdapter(new AdapterStages(context, entries));

                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            try {
                                loadRoute(Stage.valueOf(results.getJSONObject(i)));
                                generator.startRouteConfiguration();

                                ArrayList<TileRoute> currentRoutes = generator.getPath();
                                layoutConstruct.refreashLayout(currentRoutes);
                                dialogStages.dismiss();
                                context.drawer.closeDrawer(context.drawerContent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    dialogStages.setContentView(v);
                    dialogStages.show();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }, currentWorld.id);


    }

    public void loadMaze() {
        final Dialog stages = new Dialog(context);
        stages.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        View v = LayoutInflater.from(context).inflate(R.layout.dialog_stages, null);
        ListView listView = (ListView) v.findViewById(R.id.lv);
        ArrayList<String> entries = new ArrayList<String>();
        for (int i = 0; i < context.getActivityLoader().stages.size(); i++) {
            entries.add(Integer.toString(i));
        }
        listView.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, entries));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int i, long l) {

                loadRoute(context.getActivityLoader().stages.get(i));
                generator._id = null;
                generator.startRouteConfiguration();

                ArrayList<TileRoute> currentRoutes = generator.getPath();
                layoutConstruct.refreashLayout(currentRoutes);
                stages.dismiss();


            }
        });
        stages.setContentView(v);
        stages.show();
    }


    public void setCurrentTile(TileRoute tile) {
        if (this.tile != null)
            this.tile.setCurrentTile(false);
        this.tile = tile;
        this.tile.setCurrentTile(true);
        //this.tile.getWalls().get(0).getColor()[0]=1.0f;

        this.oldTile = null;
        generator.getPathPopup().refreashLayout();
        //layoutRoutes.refreashLayout();
        //context.drawer.openDrawer(context.drawerContent);
        //if (layoutRoutes.getLayout().getParent() == null) {
        //    layoutRutesBase.addView(layoutRoutes.getLayout());
        //    toggleRoutes.setSelected(true);

        //}


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

    public void refreshControlls() {


        layoutSounds.refreashLayout();
        layoutGrid.refreashLayout();
        layoutColors.refreashLayout();
        generator.getPathPopup().refreashLayout();
        layoutLights.refreashLayout();
        layoutEffects.refreashLayout();

    }


    public void showPreviewControlls() {
        //layoutGridBase.setVisibility(View.GONE);
        //layoutColorsBase.setVisibility(View.VISIBLE);
        layoutConfigBase.setVerticalGravity(View.GONE);
        //layoutRutesBase.setVisibility(View.GONE);
        //layoutSoundsBase.setVisibility(View.VISIBLE);
        //layoutLightsBase.setVisibility(View.VISIBLE);
        layoutEffectsBase.setVisibility(View.VISIBLE);

        layoutStringsBase.setVisibility(View.GONE);
        layoutConfigBase.setVisibility(View.GONE);
        layoutConstructBase.setVisibility(View.VISIBLE);
        toggleSave.setVisibility(View.VISIBLE);
        //layoutPreviewBase.setVisibility(View.VISIBLE);
        toggleWorld.setVisibility(View.GONE);
        layoutChooseWorldBase.setVisibility(View.GONE);
    }

    public void showGeneratorConstrolls() {
        toggleWorld.setVisibility(View.GONE);
        layoutChooseWorldBase.setVisibility(View.GONE);

        //layoutGridBase.setVisibility(View.VISIBLE);
        //layoutColorsBase.setVisibility(View.VISIBLE);
        //layoutSoundsBase.setVisibility(View.VISIBLE);
        layoutStringsBase.setVisibility(View.VISIBLE);
        layoutConfigBase.setVisibility(View.VISIBLE);
        layoutConstructBase.setVisibility(View.VISIBLE);
        toggleSave.setVisibility(View.VISIBLE);
        //layoutPreviewBase.setVisibility(View.VISIBLE);
        //layoutLightsBase.setVisibility(View.VISIBLE);
        layoutEffectsBase.setVisibility(View.VISIBLE);

    }

    public void showWorldControlls() {
        toggleWorld.setVisibility(View.VISIBLE);
        layoutChooseWorldBase.setVisibility(View.VISIBLE);

        //layoutGridBase.setVisibility(View.GONE);
        //layoutColorsBase.setVisibility(View.GONE);
        layoutConfigBase.setVerticalGravity(View.GONE);
        //layoutRutesBase.setVisibility(View.VISIBLE);
        //layoutSoundsBase.setVisibility(View.GONE);
        layoutStringsBase.setVisibility(View.GONE);
        layoutConfigBase.setVisibility(View.GONE);
        layoutConstructBase.setVisibility(View.GONE);
        toggleSave.setVisibility(View.GONE);
        //layoutPreviewBase.setVisibility(View.GONE);
        ///layoutLightsBase.setVisibility(View.GONE);
        layoutEffectsBase.setVisibility(View.GONE);

    }

    public void loadWorld(World world) {


        if (world == null) {
            showWorldControlls();
            return;
        }

        showGeneratorConstrolls();
        generator.loadWorld(world);

        this.tile.setCurrentTile(true);
        this.generator.getPathPopup().showPath(this.tile.centerX, this.tile.centerY);
        context.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        //context.drawer.closeDrawer(context.drawerContent);
    }


    public View onCreateView() {


        View v = LayoutInflater.from(context).inflate(R.layout.layout_generator, null);

        layoutWorlds.initLayout(this);
        layoutGrid.initLayout(this);
        layoutColors.initLayout(this);
        layoutConfig.initLayout(this);
        layoutSounds.initLayout(this);
        layoutStrings.initLayout(this);
        layoutWorldsList.initLayout(this);
        layoutConstruct.initLayout(this);
        layoutPreview.initLayout(this);
        layoutLights.initLayout(this);
        layoutEffects.initLayout(this);


        layoutGridBase = (LinearLayout) v.findViewById(R.id.layout_grid_size_base);
        toggleGridSize = (LinearLayout) v.findViewById(R.id.toggle_grid_size);

        layoutChooseWorldBase = (LinearLayout) v.findViewById(R.id.layout_choose_world_base);
        toggleChooseWorld = (LinearLayout) v.findViewById(R.id.toggle_choose_world);


        layoutColorsBase = (LinearLayout) v.findViewById(R.id.layout_colors_base);
        toggleColors = (LinearLayout) v.findViewById(R.id.toggle_colors);

        layoutConfigBase = (LinearLayout) v.findViewById(R.id.layout_config_base);
        toggleConfig = (LinearLayout) v.findViewById(R.id.toggle_config);


        layoutSoundsBase = (LinearLayout) v.findViewById(R.id.layout_sounds_base);
        toggleSounds = (LinearLayout) v.findViewById(R.id.toggle_sounds);

        layoutStringsBase = (LinearLayout) v.findViewById(R.id.layout_strings_base);
        toggleString = (LinearLayout) v.findViewById(R.id.toggle_strings);

        layoutWorldBase = (LinearLayout) v.findViewById(R.id.layout_world_base);
        toggleWorld = (LinearLayout) v.findViewById(R.id.toggle_world);

        layoutConstructBase = (LinearLayout) v.findViewById(R.id.layout_construct_base);
        toggleConstruct = (LinearLayout) v.findViewById(R.id.toggle_construct);

        layoutPreviewBase = (LinearLayout) v.findViewById(R.id.layout_preview_base);
        togglePreview = (LinearLayout) v.findViewById(R.id.toggle_preview);

        layoutLightsBase = (LinearLayout) v.findViewById(R.id.layout_lights_base);
        toggleLights = (LinearLayout) v.findViewById(R.id.toggle_lights);


        layoutEffectsBase = (LinearLayout) v.findViewById(R.id.layout_effects_base);
        toggleEffects = (LinearLayout) v.findViewById(R.id.toggle_effects);

        toggleSave = (LinearLayout) v.findViewById(R.id.toggle_save);


        showWorldControlls();

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

                    generator.startRouteConfiguration();
                    ArrayList<TileRoute> currentRoutes = generator.getPath();
                    layoutConstruct.refreashLayout(currentRoutes);


                    layoutConstructBase.addView(layoutConstruct.getLayout());
                    toggleConstruct.setSelected(true);

                } else {
                    layoutConstructBase.removeView(layoutConstruct.getLayout());
                    toggleConstruct.setSelected(false);

                }
            }
        });

        togglePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutPreview.getLayout().getParent() == null) {
                    layoutPreviewBase.addView(layoutPreview.getLayout());
                    togglePreview.setSelected(true);

                } else {
                    layoutPreviewBase.removeView(layoutPreview.getLayout());
                    togglePreview.setSelected(false);

                }
            }
        });

        toggleLights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutLights.getLayout().getParent() == null) {
                    layoutLightsBase.addView(layoutLights.getLayout());
                    toggleLights.setSelected(true);

                } else {
                    layoutLightsBase.removeView(layoutLights.getLayout());
                    toggleLights.setSelected(false);

                }
            }
        });


        toggleEffects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutEffects.getLayout().getParent() == null) {
                    layoutEffectsBase.addView(layoutEffects.getLayout());
                    toggleEffects.setSelected(true);

                } else {
                    layoutEffectsBase.removeView(layoutEffects.getLayout());
                    toggleEffects.setSelected(false);

                }
            }
        });

        toggleChooseWorld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (layoutWorldsList.getLayout().getParent() == null) {
                    toggleChooseWorld.setSelected(true);
                    layoutChooseWorldBase.addView(layoutWorldsList.getLayout());
                    toggleChooseWorld.setEnabled(false);
                    layoutWorldsList.buildWorldsList(new GeneratorLayoutWorldsList.OnListBuildedListener() {
                        @Override
                        public void onListBuilded(boolean flag) {
                            toggleChooseWorld.setEnabled(true);
                            if(!flag)
                                toggleChooseWorld.setSelected(false);
                        }
                    });
                } else {
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

        return v;
    }


}
