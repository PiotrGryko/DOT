package pl.slapps.dot.layout;

import android.app.Dialog;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import pl.slapps.dot.DAO;
import pl.slapps.dot.MainActivity;
import pl.slapps.dot.R;
import pl.slapps.dot.SurfaceRenderer;
import pl.slapps.dot.adapter.AdapterStages;
import pl.slapps.dot.adapter.AdapterWorlds;
import pl.slapps.dot.model.Stage;
import pl.slapps.dot.model.World;

/**
 * Created by piotr on 09.12.15.
 */
public class MainMenu {


    private String TAG = MainMenu.class.getName();


    private TextView tvName;
    private TextView tvDesc;

    public ImageView btnSettings;
    public LinearLayout layoutMenu;
    public LinearLayout layoutBtns;
    public LinearLayout layoutHeader;
    public TextView tvHeader;
    public LinearLayout menuBkg;


    private ImageButton btnPlay;
    private ImageButton btnExit;
    private ImageButton btnGenerate;
    private ImageButton btnStages;
    private ImageButton btnOnline;

    private AnimationMainMenu animationMainMenu;

    public AnimationMainMenu getAnimationMainMenu()
    {
        return animationMainMenu;
    }

    public void disableButtons() {
        btnPlay.setEnabled(false);
        btnExit.setEnabled(false);
        btnGenerate.setEnabled(false);
        btnStages.setEnabled(false);
        btnOnline.setEnabled(false);
    }

    public void enableButtons() {
        btnPlay.setEnabled(true);
        btnExit.setEnabled(true);
        btnGenerate.setEnabled(true);
        btnStages.setEnabled(true);
        btnOnline.setEnabled(true);
    }

    private MainActivity context;
    private SurfaceRenderer game;

    public SurfaceRenderer getGame()
    {
        return game;
    }

    public MainMenu(MainActivity context, SurfaceRenderer game) {
        this.context = context;
        this.game = game;


    }


    public void loadStage(Stage stage) {

        final int color = Color.parseColor(stage.config.colors.colorRoute);


        //layoutMenu.clearAnimation();
        tvName.setTextColor(color);
        tvDesc.setTextColor(color);
        tvName.setText(stage.name);
        tvDesc.setText(stage.description);
        //layoutMenu.setVisibility(View.VISIBLE);



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


    public void init() {

        animationMainMenu = new AnimationMainMenu(this);
        btnSettings = (ImageView) context.findViewById(R.id.btn_settings);
        layoutMenu = (LinearLayout) context.findViewById(R.id.layout_menu);
        layoutBtns = (LinearLayout) context.findViewById(R.id.layout_btns);
        layoutHeader = (LinearLayout) context.findViewById(R.id.layout_header);
        tvHeader = (TextView) context.findViewById(R.id.tv_header);
        btnSettings.setVisibility(View.GONE);
        menuBkg = (LinearLayout) context.findViewById(R.id.menu_bkg);



        tvName = (TextView) context.findViewById(R.id.tv_lvl);
        tvDesc = (TextView) context.findViewById(R.id.tv_desc);

        btnExit = (ImageButton) context.findViewById(R.id.btn_exit);
        btnPlay = (ImageButton) context.findViewById(R.id.btn_play);
        btnGenerate = (ImageButton) context.findViewById(R.id.btn_generate);
        btnStages = (ImageButton) context.findViewById(R.id.btn_stages);
        btnOnline = (ImageButton) context.findViewById(R.id.btn_online);

        animationMainMenu.init();
        LoginButton loginButton = (LoginButton) context.findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");
        // If using in a fragment
        //loginButton.setFragment(this);
        // Other app specific specialization

        // Callback registration
        CallbackManager callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code

                Log.d(TAG, "on success");
            }

            @Override
            public void onCancel() {
                // App code
                Log.d(TAG, "on cancel");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d(TAG, "on error");
            }
        });


        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                context.mAdView.setVisibility(View.GONE);
                animationMainMenu.hideMenu();

            }
        });

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.getSoundsManager().stopBackgroundPlayer();
                game.initGenerator();
                game.setRunnig(true);
                btnSettings.setVisibility(View.VISIBLE);
                layoutMenu.setVisibility(View.GONE);
                //menuHideAnimation.startAnimation(500);
                context.mAdView.setVisibility(View.GONE);


            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.onBackPressed();
            }
        });

        btnStages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog worldsDialog = new Dialog(context);
                worldsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

                View v = LayoutInflater.from(context).inflate(R.layout.dialog_worlds, null);

                GridView gridView = (GridView) v.findViewById(R.id.grid_view);


                final ArrayList<World> worlds = new ArrayList<World>();
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(loadWorlds());

                    for (int i = 0; i < jsonArray.length(); i++) {
                        worlds.add(World.valueOf(jsonArray.getJSONObject(i)));
                    }
                    gridView.setAdapter(new AdapterWorlds(context, worlds));

                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int j, long l) {
                            Log.d(TAG, "selected world " + j);
                            Log.d(TAG, "worlds stages  " + worlds.get(j).stages.size());
                            final World world = worlds.get(j);

                            final Dialog stagesDialog = new Dialog(context);
                            stagesDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            View v = LayoutInflater.from(context).inflate(R.layout.dialog_worlds, null);

                            GridView gridView = (GridView) v.findViewById(R.id.grid_view);


                            gridView.setAdapter(new AdapterStages(context, world.stages));
                            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                    context.loadStage(world.stages.get(i));
                                    stagesDialog.dismiss();
                                    worldsDialog.dismiss();
                                }
                            });
                            stagesDialog.setContentView(v);
                            stagesDialog.show();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                /*
                ListView listView = (ListView) v.findViewById(R.id.lv);

                ArrayList<JSONObject> entries = new ArrayList<JSONObject>();

                for (int i = 0; i <= unlockedStage; i++) {
                    try {
                        entries.add(stages.getJSONObject(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                listView.setAdapter(new AdapterStages(MainActivity.this, entries));

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View generator, int i, long l) {
                        currentStage = i;
                        try {
                            loadStage(stages.getJSONObject(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        stagesDialog.dismiss();
                    }
                });
                */
                worldsDialog.setContentView(v);
                worldsDialog.show();
            }
        });


        btnOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "button online pressed");

                DAO.getWorlds(context, new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        Log.d(TAG, "online stages fetched");

                        final Dialog worldsDialog = new Dialog(context);
                        worldsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

                        View v = LayoutInflater.from(context).inflate(R.layout.dialog_worlds, null);

                        GridView gridView = (GridView) v.findViewById(R.id.grid_view);


                        final ArrayList<World> worlds = new ArrayList<World>();
                        try {

                            JSONObject res = new JSONObject(response.toString());

                            JSONObject api = res.has("api") ? res.getJSONObject("api") : new JSONObject();
                            JSONArray jsonArray = api.has("results") ? api.getJSONArray("results") : new JSONArray();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                worlds.add(World.valueOf(jsonArray.getJSONObject(i)));
                            }
                            gridView.setAdapter(new AdapterWorlds(context, worlds));

                            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int j, long l) {
                                    Log.d(TAG, "selected world " + j);
                                    Log.d(TAG, "worlds stages  " + worlds.get(j).stages.size());
                                    final World world = worlds.get(j);

                                    final Dialog stagesDialog = new Dialog(context);
                                    stagesDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    View v = LayoutInflater.from(context).inflate(R.layout.dialog_worlds, null);

                                    GridView gridView = (GridView) v.findViewById(R.id.grid_view);


                                    gridView.setAdapter(new AdapterStages(context, world.stages));
                                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                            context.loadStage(world.stages.get(i));
                                            stagesDialog.dismiss();
                                            worldsDialog.dismiss();
                                        }
                                    });
                                    stagesDialog.setContentView(v);
                                    stagesDialog.show();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        worldsDialog.setContentView(v);
                        worldsDialog.show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "error fetching online stages " + error.getMessage());
                        Log.d(TAG, error.toString());
                    }
                }, true);


            }
        });


    }





}
