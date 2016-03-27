package pl.slapps.dot.gui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import pl.slapps.dot.DAO;
import pl.slapps.dot.MainActivity;
import pl.slapps.dot.R;
import pl.slapps.dot.SurfaceRenderer;
import pl.slapps.dot.adapter.AdapterStages;
import pl.slapps.dot.adapter.AdapterWorlds;
import pl.slapps.dot.gui.AnimationMainMenu;
import pl.slapps.dot.model.Config;
import pl.slapps.dot.model.Stage;
import pl.slapps.dot.model.World;

/**
 * Created by piotr on 05/03/16.
 */
public class FragmentMainMenu extends Fragment {


    //private MainMenu mainMenu;

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {

        this.context = (MainActivity)getActivity();
        this.game = context.surfaceRenderer;
        //mainMenu = new MainMenu(context,context.surfaceRenderer);



        View v = init();

        return v;

    }

    private String TAG = FragmentMainMenu.class.getName();

    public View layout;

    private TextView tvName;
    private TextView tvDesc;

    private LinearLayout layoutMenu;
    private LinearLayout layoutBtns;
    private LinearLayout layoutHeader;
    //private TextView tvHeader;
    //private LinearLayout menuBkg;


    private ImageButton btnPlay;
    //private ImageButton btnExit;
    private ImageButton btnGenerate;
    private ImageButton btnStages;
    private ImageButton btnOnline;
    private ImageButton btnShuffle;

    private LinearLayout layoutPurchase;
    private ImageButton btnSkipStage;
    private ImageButton btnDisableAds;


    private ProgressBar bar;

    private String currentColor = null;

    private AnimationMainMenu animationMainMenu;

    public LinearLayout getLayoutMenu() {
        return layoutMenu;
    }

    public LinearLayout getLayoutBtns() {
        return layoutBtns;
    }

    public LinearLayout getLayoutHeader() {
        return layoutHeader;
    }



    public ImageView getStartButton() {
        return btnPlay;
    }

    public LinearLayout getBackground() {
        return layoutMenu;
    }

    public View getLayout() {
        return layout;
    }


    public void clearAnimation() {
        layoutMenu.clearAnimation();
    }

    public AnimationMainMenu getAnimationMainMenu() {
        return animationMainMenu;
    }

    public void setColor(Config config) {
        String colorString = config.colors.colorRoute;
        if (currentColor == null) {


//            final int color = Color.parseColor(colorString);
//            int c = Color.argb(100, Color.red(color), Color.green(color), Color.blue(color));

            currentColor = colorString;

            animationMainMenu.setColor("#ffffff", colorString);

            //getBackground().setBackgroundColor(c);
        } else {

            animationMainMenu.setColor(currentColor, colorString);
            currentColor = colorString;

        }
    }

    public void disableButtons() {
        btnPlay.setEnabled(false);
        //btnExit.setEnabled(false);
        btnGenerate.setEnabled(false);
        btnStages.setEnabled(false);
        btnOnline.setEnabled(false);
        btnShuffle.setEnabled(false);

    }

    public void enableButtons() {
        btnPlay.setEnabled(true);
        //btnExit.setEnabled(true);
        btnGenerate.setEnabled(true);
        btnStages.setEnabled(true);
        btnOnline.setEnabled(true);
        btnShuffle.setEnabled(true);

    }

    public MainActivity context;
    private SurfaceRenderer game;

    public SurfaceRenderer getGame() {
        return game;
    }



    public void displayError() {

        bar.setVisibility(View.GONE);
        tvName.setText("No internet connection");

    }

    public void loadStage(Stage stage, int currentStage) {

        final int color = Color.parseColor(stage.config.colors.colorShip);


        //layoutMenu.clearAnimation();
        tvName.setTextColor(color);
        tvDesc.setTextColor(color);

        btnPlay.setColorFilter(color);
        btnGenerate.setColorFilter(color);

        //tvName.setText(stage.name);
        //tvDesc.setText(stage.description);
        tvName.setText("#"+currentStage);
        //layoutMenu.setVisibility(View.VISIBLE);
        bar.setVisibility(View.GONE);
        //layoutHeader.setVisibility(View.VISIBLE);
        btnPlay.setVisibility(View.VISIBLE);
        layoutPurchase.setVisibility(View.VISIBLE);
        btnGenerate.setVisibility(View.VISIBLE);

    }


    public String loadWorlds() {
        StringBuilder returnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = context.getResources().getAssets()
                    .open(context.getActivityLoader().WORLDS_FILE, Context.MODE_WORLD_READABLE);
            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line = "";
            while ((line = input.readLine()) != null) {
                returnString.append(line);
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            try {
                if (isr != null)
                    isr.close();
                if (fIn != null)
                    fIn.close();
                if (input != null)
                    input.close();
            } catch (Exception e2) {
                e2.getMessage();
            }
        }
        return returnString.toString();
    }


    private View init() {

        animationMainMenu = new AnimationMainMenu(this);

        layout = LayoutInflater.from(context).inflate(R.layout.fragment_main_menu, null);
        bar = (ProgressBar)layout.findViewById(R.id.bar);
        layoutMenu = (LinearLayout) layout.findViewById(R.id.layout_menu);
        layoutBtns = (LinearLayout) layout.findViewById(R.id.layout_btns);
        layoutHeader = (LinearLayout) layout.findViewById(R.id.layout_header);
        //tvHeader = (TextView) layout.findViewById(R.id.tv_header);


        layoutPurchase=(LinearLayout)layout.findViewById(R.id.layout_purchase);
        btnSkipStage=(ImageButton)layout.findViewById(R.id.skip_stage);
        btnDisableAds =(ImageButton)layout.findViewById(R.id.disable_ads);

        tvName = (TextView) layout.findViewById(R.id.tv_lvl);
        tvDesc = (TextView) layout.findViewById(R.id.tv_desc);

        //btnExit = (ImageButton) layout.findViewById(R.id.btn_exit);
        btnPlay = (ImageButton) layout.findViewById(R.id.btn_play);
        btnGenerate = (ImageButton) layout.findViewById(R.id.btn_generate);
        btnStages = (ImageButton) layout.findViewById(R.id.btn_stages);
        btnOnline = (ImageButton) layout.findViewById(R.id.btn_online);
        btnShuffle = (ImageButton) layout.findViewById(R.id.btn_shuffle);

        btnSkipStage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)FragmentMainMenu.this.getActivity()).getActivityBilling().buyGap();
            }
        });

        btnDisableAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) FragmentMainMenu.this.getActivity()).getActivityBilling().disableAds();
            }
        });

        //LoginButton loginButton = (LoginButton) layout.findViewById(R.id.login_button);

        //context.gameHolder.addView(layout);

        animationMainMenu.init();
        //loginButton.setReadPermissions("user_friends");
        // If using in a fragment
        //loginButton.setFragment(this);
        // Other app specific specialization
/*
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
*/

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




                ///context.gameHolder.removeView(layout);



                //context.gameHolder.removeView(layoutMenu);

                context.removeCurrentFragment();

                context.gameHolder.addView(context.getMockView());
                //context.rootLayout.addView(context.getActivityControls().getLayoutButtons());

                //menuHideAnimation.startAnimation(500);
                context.mAdView.setVisibility(View.GONE);


            }
        });
/*
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.onBackPressed();
            }
        });
*/
        btnStages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                Log.d(TAG, "online stages fetched");

                final Dialog worldsDialog = new Dialog(context);
                worldsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

                View v = LayoutInflater.from(context).inflate(R.layout.dialog_worlds, null);

                GridView gridView = (GridView) v.findViewById(R.id.grid_view);


                final ArrayList<World> worlds = new ArrayList<World>();
                try {

                    String response = loadWorlds();
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

                                    context.loadStage(world.stages.get(i),false);
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

                                            context.loadStage(world.stages.get(i),false);
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
        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                DAO.getRandomStage(context, new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {

                        Log.d(TAG, response.toString());
                        JSONObject object = null;
                        try {
                            object = new JSONObject(response.toString());
                            object = object.has("api") ? object.getJSONObject("api") : object;
                            object = object.has("doc") ? object.getJSONObject("doc") : object;

                            context.loadStage(Stage.valueOf(object),true);

                            context.mAdView.setVisibility(View.GONE);
                            animationMainMenu.hideMenu();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());

                    }
                }, context.android_id);

            }
        });
        if(context.getCurrentStage()!=null)
        context.loadStage(context.getCurrentStage(),false);



        return layout;
    }




}
