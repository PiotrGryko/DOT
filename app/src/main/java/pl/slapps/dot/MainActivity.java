package pl.slapps.dot;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AsyncPlayer;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import pl.slapps.dot.adapter.AdapterStages;
import pl.slapps.dot.game.GameView;

public class MainActivity extends Activity {

    private String TAG = "MainActivity";
    private GameView game;


    private final String STAGES_FILE = "stages.json";
    private TextView tvName;
    private TextView tvDesc;

    private LinearLayout layoutMenu;
    private LinearLayout layoutBtns;
    private LinearLayout layoutHeader;
    private TextView tvHeader;
    private LinearLayout menuBkg;


    private ImageButton btnPlay;
    private ImageButton btnExit;
    private ImageButton btnGenerate;
    private ImageButton btnStages;
    private ImageButton btnOnline;

    private SoundsManager soundsManager;

    private SharedPreferences preferences;
    public JSONArray stages;

    private int currentStage = 0;
    private int unlockedStage = 0;

    private Handler handler = new Handler();

    private HideAnimation menuHideAnimation;
    private ShowAnimation menuShowAnimation;
    private HideAnimation headerHideAnimation;
    private HideAnimation btnsHideAnimation;
    private EntranceAnimation entranceAnimation;


    class HideAnimation {
        private View view;
        private Animation animation;

        public HideAnimation(final View view, Animation.AnimationListener listener) {
            this.view = view;
            animation = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    super.applyTransformation(interpolatedTime, t);
                    ViewCompat.setAlpha(view, 1 - interpolatedTime);
                }
            };

            animation.setDuration(300);
            if (listener != null)
                animation.setAnimationListener(listener);
            else {
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        view.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        }

        public void startAnimation() {
            view.startAnimation(animation);
        }

        public void clearAnimation() {
            ViewCompat.setAlpha(view, 1);
        }
    }


    interface OnAnimationListener
    {
        public void onAnimationEnd();
        public void onAnimationStart();
    }
    class ShowAnimation {
        private View view;
        private Animation animation;
        private OnAnimationListener listener;



        public ShowAnimation(final View view) {
            this.view = view;
            animation = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    super.applyTransformation(interpolatedTime, t);
                    ViewCompat.setAlpha(view, interpolatedTime);
                }
            };
            animation.setDuration(150);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    if(listener!=null)
                        listener.onAnimationStart();

                    view.setVisibility(View.VISIBLE);

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                    if(listener!=null)
                        listener.onAnimationEnd();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        public void startAnimation(OnAnimationListener listener) {
            this.listener=listener;
            view.startAnimation(animation);
            //view.setVisibility(View.VISIBLE);
        }

    }

    class EntranceAnimation {
        private View view;
        private Animation animation;

        public EntranceAnimation(final View view, Animation.AnimationListener listener) {
            this.view = view;

            animation = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    super.applyTransformation(interpolatedTime, t);
                    ViewCompat.setScaleX(view, 1 + interpolatedTime / 3);
                    ViewCompat.setScaleY(view, 1 + interpolatedTime / 3);


                }
            };


            animation.setDuration(2000);


            if (listener != null)
                animation.setAnimationListener(listener);
        }

        public void startAnimation() {
            view.startAnimation(animation);
        }

        public void clearAnimation() {
            ViewCompat.setScaleX(view, 1);
            ViewCompat.setScaleY(view, 1);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        soundsManager = new SoundsManager(this);
        loadStagesFile();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentStage =unlockedStage= preferences.getInt("current_stage", 0);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.main_activity);
        game = (GameView) findViewById(R.id.game);
        layoutMenu = (LinearLayout) findViewById(R.id.layout_menu);
        layoutBtns = (LinearLayout) findViewById(R.id.layout_btns);
        layoutHeader = (LinearLayout) findViewById(R.id.layout_header);
        tvHeader = (TextView) findViewById(R.id.tv_header);
        menuHideAnimation = new HideAnimation(layoutMenu, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {


                layoutMenu.setVisibility(View.GONE);
                //menuHideAnimation.clearAnimation();
                entranceAnimation.clearAnimation();
                btnsHideAnimation.clearAnimation();
                headerHideAnimation.clearAnimation();

                game.setRunnig(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        menuShowAnimation = new ShowAnimation(layoutMenu);
        headerHideAnimation = new HideAnimation(tvHeader, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        entranceAnimation = new EntranceAnimation(layoutHeader, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                menuHideAnimation.startAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        btnsHideAnimation = new HideAnimation(layoutBtns, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                entranceAnimation.startAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        menuBkg = (LinearLayout) findViewById(R.id.menu_bkg);

        initMainMenu();


        try {
            loadStage(stages.getJSONObject(currentStage));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        soundsManager.playBackgroundBirds();

    }

    public void clearStageState()
    {
        try {
            game.loadStageData(stages.getJSONObject(currentStage));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loadStage(final JSONObject jsonStage) {

        game.setRunnig(false);
        try {

            final String name = jsonStage.has("name") ? jsonStage.getString("name") : "";
            final String desc = jsonStage.has("description") ? jsonStage.getString("description") : "";
            JSONObject colors = jsonStage.has("colors") ? jsonStage.getJSONObject("colors") : new JSONObject();
            final String backgroundColor = colors.has("background") ? colors.getString("background") : "#ff9999";

            final int color = Color.parseColor(backgroundColor);
            int c = Color.argb(100, Color.red(color), Color.green(color), Color.blue(color));
            menuBkg.setBackgroundColor(c);

            handler.post(new Runnable() {
                @Override
                public void run() {
                    layoutMenu.clearAnimation();
                    tvName.setTextColor(color);
                    tvDesc.setTextColor(color);
                    if (layoutMenu.getVisibility() == View.GONE)
                        menuShowAnimation.startAnimation(new OnAnimationListener() {
                            @Override
                            public void onAnimationEnd() {
                                game.setRunnig(true);
                            }

                            @Override
                            public void onAnimationStart() {

                            }
                        });

                    tvName.setText(name);
                    tvDesc.setText(desc);

                }
            });
            game.loadStageData(jsonStage);



        } catch (Throwable e) {
            e.printStackTrace();
        }
    }



    public void moveToNextStage() {


        currentStage++;
        if (currentStage >= stages.length())
            currentStage = stages.length() - 1;
        try {
            loadStage(stages.getJSONObject(currentStage));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int savedStage = preferences.getInt("current_stage", 0);
        if (savedStage < currentStage) {
            unlockedStage=currentStage;
            preferences.edit().putInt("current_stage", unlockedStage).apply();
        }
    }


    private void initMainMenu() {

        tvName = (TextView) findViewById(R.id.tv_lvl);
        tvDesc = (TextView) findViewById(R.id.tv_desc);

        btnExit = (ImageButton) findViewById(R.id.btn_exit);
        btnPlay = (ImageButton) findViewById(R.id.btn_play);
        btnGenerate = (ImageButton) findViewById(R.id.btn_generate);
        btnStages = (ImageButton) findViewById(R.id.btn_stages);
        btnOnline = (ImageButton) findViewById(R.id.btn_online);


        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                headerHideAnimation.startAnimation();
                btnsHideAnimation.startAnimation();


            }
        });

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                game.initGenerator(9, 15);
                menuHideAnimation.startAnimation();


            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnStages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog stagesDialog = new Dialog(MainActivity.this);
                stagesDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

                View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_stages, null);
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
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        currentStage = i;
                        try {
                            loadStage(stages.getJSONObject(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        stagesDialog.dismiss();
                    }
                });
                stagesDialog.setContentView(v);
                stagesDialog.show();
            }
        });

        btnOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btnOnline.setEnabled(false);

                DAO.getStages(MainActivity.this, new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        Log.d(TAG, response.toString());
                        JSONObject out = null;
                        try {
                            out = new JSONObject(response.toString());

                            JSONObject api = out.has("api") ? out.getJSONObject("api") : new JSONObject();
                            final JSONArray results = api.has("results") ? api.getJSONArray("results") : new JSONArray();

                            final Dialog stages = new Dialog(MainActivity.this);
                            stages.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

                            View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_stages, null);
                            ListView listView = (ListView) v.findViewById(R.id.lv);
                            ArrayList<JSONObject> entries = new ArrayList<JSONObject>();
                            for (int i = 0; i < results.length(); i++) {
                                entries.add(results.getJSONObject(i));
                            }
                            listView.setAdapter(new AdapterStages(MainActivity.this, entries));

                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    try {

                                        currentStage = unlockedStage-1;
                                        loadStage(results.getJSONObject(i));
                                        stages.dismiss();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            stages.setContentView(v);
                            stages.show();
                            btnOnline.setEnabled(true);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        btnOnline.setEnabled(true);

                    }
                });

            }
        });


    }

    public SoundsManager getSoundsManager() {
        return soundsManager;
    }

    public void loadStagesFile() {
        StringBuilder returnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = getResources().getAssets()
                    .open(STAGES_FILE, Context.MODE_WORLD_READABLE);
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

        try {
            JSONObject jsonData = new JSONObject(returnString.toString());
            JSONObject api = jsonData.has("api") ? jsonData.getJSONObject("api") : new JSONObject();
            stages = api.has("results") ? api.getJSONArray("results") : new JSONArray();
            Log.d(TAG, "stages loaded");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }

    }


    public void onBackPressed() {

        layoutMenu.clearAnimation();

        if (layoutMenu.getVisibility() == View.GONE) {
            game.setRunnig(false);
            menuShowAnimation.startAnimation(null);

        } else {
            soundsManager.stopBackgroundPlayer();

            finish();
        }

    }


    public void onResume() {

        super.onResume();
        game.onResume();
    }

    public void onPause() {

        super.onPause();
        game.onPause();

    }

}
