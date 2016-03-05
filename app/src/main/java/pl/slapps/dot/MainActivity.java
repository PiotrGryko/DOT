package pl.slapps.dot;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;


import io.fabric.sdk.android.Fabric;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import pl.slapps.dot.layout.AnimationShow;
import pl.slapps.dot.layout.MainMenu;
import pl.slapps.dot.layout.AnimationScoreLayout;
import pl.slapps.dot.model.Stage;
import pl.slapps.dot.model.World;

public class MainActivity extends Activity {

    //////////tmp
    private TextView tvMax;
    private TextView tvMin;
    private TextView tvCur;


    private String TAG = "MainActivity";
    private SurfaceRenderer surfaceRenderer;
    public RelativeLayout gameHolder;
    public RelativeLayout rootLayout;
    private View mockView;
    private ImageView btnSettings;
    private ImageView btnPlay;
    private ImageView btnPalette;
    private ImageView btnLights;
    private ImageView btnSounds;

    private LinearLayout layoutButtons;


    public final String WORLDS_FILE = "worlds.json";


    private SoundsManager soundsManager;

    private SharedPreferences preferences;
    public ArrayList<Stage> stages;

    private int currentStage = 0;
    private int unlockedStage = 0;

    private Handler handler = new Handler();


    public InterstitialAd mInterstitialAd;
    public AdView mAdView;

    public MainMenu mainMenu;
    public AnimationScoreLayout scoreLayout;

    public DrawerLayout drawer;
    public LinearLayout drawerContent;

    public static float screenWidth;
    public static float screenHeight;


    public String android_id;

    public static ArrayList<String> sounds;

    //////////////////////////////////////////////////////////////////////////////////////////////


    private class DownloadFile extends AsyncTask<String, Integer, String> {

        private JSONObject o;

        public DownloadFile(JSONObject object) {
            this.o = object;
        }

        @Override
        protected String doInBackground(String... arg) {
            int count;
            try {

                String path = o.has("path") ? o.getString("path") : null;
                String name = o.has("originalname") ? o.getString("originalname") : null;

                if (path == null) {
                    Log.d("aaa", "path null");
                    return null;
                }

                File f = new File(MainActivity.this.getCacheDir() + "/" + name);

                if (f.exists()) {
                    Log.d("aaa", "file exist");
                    return null;
                }

                URL url = new URL(DAO.url_files + path);
                URLConnection conexion = url.openConnection();
                conexion.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conexion.getContentLength();

                // downlod the file
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(f.getPath());

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    Log.d("aaa", "progress " + (total * 100 / lenghtOfFile));
                    //publishProgress((int) (total * 100 / lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
                Log.d("aaa", "file saved");
                listCatche();
            } catch (Exception e) {
                Log.d("aaa", e.toString());
            }
            return null;
        }

    }

    private void loadSounds() {


        DAO.getSounds(this, new Response.Listener() {
            @Override
            public void onResponse(Object response) {


                Log.d(TAG, response.toString());
                JSONObject object = null;
                try {
                    object = new JSONObject(response.toString());

                    object = object.has("api") ? object.getJSONObject("api") : object;

                    JSONArray array = object.has("results") ? object.getJSONArray("results") : new JSONArray();

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject o = array.getJSONObject(i);

                        new DownloadFile(o).execute();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d(TAG, error.toString());
            }
        });


    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static ArrayList<String> listRaw() {
        ArrayList<String> files = new ArrayList<>();
        Field[] fields = R.raw.class.getFields();
        for (int count = 0; count < fields.length; count++) {

            files.add(fields[count].getName());

        }
        for (int i = 0; i < sounds.size(); i++) {
            files.add(sounds.get(i));
        }
        files.add("");
        return files;
    }

    public void listCatche() {
        sounds = new ArrayList<>();

        File f = new File(getCacheDir().getPath());
        File[] files1 = f.listFiles();

        for (int i = 0; i < files1.length; i++) {
            if (files1[i].getAbsolutePath().endsWith("mp3"))
                sounds.add(files1[i].getAbsolutePath());
        }


    }

    public void setCurrent(final float value) {
        handler.post(new Runnable() {
            @Override
            public void run() {


                tvCur.setText(Integer.toString((int) value));


            }
        });
    }


    public void resetLogs() {
        tvMax.setText(Integer.toString(0));
        tvCur.setText(Integer.toString(0));
        tvMin.setText(Integer.toString(0));
    }

    public void setMax(final long value) {
        handler.post(new Runnable() {
            @Override
            public void run() {


                String current = tvMax.getText().toString();

                try {
                    float v = Long.parseLong(current);
                    if (value > v)
                        tvMax.setText(Long.toString(value));
                } catch (Throwable t) {
                    tvMax.setText(Long.toString(value));

                }

            }
        });
    }

    public void setMin(final long value) {
        handler.post(new Runnable() {
            @Override
            public void run() {


                String current = tvMin.getText().toString();

                try {
                    float v = Long.parseLong(current);
                    if (value < v)
                        tvMin.setText(Long.toString(value));
                } catch (Throwable t) {
                    tvMin.setText(Long.toString(value));

                }

            }
        });
    }

    public View getButtonSettings() {
        return btnSettings;
    }

    public View getMockView() {
        return mockView;
    }

    public ImageView getButtonPlay() {
        return btnPlay;
    }

    public ImageView getButtonColours() {
        return btnPalette;
    }

    public ImageView getButtonSounds() {
        return btnSounds;
    }

    public ImageView getButtonLights() {
        return btnLights;
    }

    public LinearLayout getLayoutButtons() {
        return layoutButtons;
    }


    private void initGeneratorButtons() {
        btnSettings = (ImageView) findViewById(R.id.btn_settings);
        btnSettings.setVisibility(View.GONE);

        btnPlay = (ImageView) findViewById(R.id.btn_play);
        btnPlay.setVisibility(View.GONE);

        btnPalette = (ImageView) findViewById(R.id.btn_colours);
        btnPalette.setVisibility(View.GONE);

        btnLights = (ImageView) findViewById(R.id.btn_lights);
        btnLights.setVisibility(View.GONE);

        btnSounds = (ImageView) findViewById(R.id.btn_sounds);
        btnSounds.setVisibility(View.GONE);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());


        listCatche();
        loadSounds();

        android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        screenHeight = this.getResources().getDisplayMetrics().heightPixels;
        screenWidth = this.getResources().getDisplayMetrics().widthPixels;
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        listRaw();

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    this.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d(TAG, Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, e.toString());

        } catch (NoSuchAlgorithmException e) {
            Log.d(TAG, e.toString());
        }

        soundsManager = new SoundsManager(this);

        loadStagesFile();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentStage = unlockedStage = preferences.getInt("current_stage", 0);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.main_activity);

        tvMax = (TextView) findViewById(R.id.tv_max_time);
        tvMin = (TextView) findViewById(R.id.tv_min_time);
        tvCur = (TextView) findViewById(R.id.tv_current_time);

        initGeneratorButtons();

        surfaceRenderer = (SurfaceRenderer) findViewById(R.id.game);
        gameHolder = (RelativeLayout) findViewById(R.id.game_holder);
        rootLayout = (RelativeLayout) findViewById(R.id.root_layout);

        mockView = findViewById(R.id.mock_view);
        layoutButtons = (LinearLayout) findViewById(R.id.layout_buttons);

        gameHolder.removeView(mockView);
        rootLayout.removeView(layoutButtons);


        mainMenu = new MainMenu(this, surfaceRenderer);
        scoreLayout = new AnimationScoreLayout(surfaceRenderer);
        drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawerContent = (LinearLayout) findViewById(R.id.drawer_content);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mainMenu.init();
        surfaceRenderer.init(this);
        scoreLayout.initLayout(this);
        //initMainMenu();


        loadStage(stages.get(currentStage));


        //soundsManager.playBackgroundBirds();


        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                //    mainMenu.headerHideAnimation.startAnimation(500);
                //    mainMenu.btnsHideAnimation.startAnimation(500);
                //mAdView.setVisibility(View.GONE);

            }
        });

        requestNewInterstitial();


    }

    public void showAdv() {
        handler.post(new Runnable() {
            @Override
            public void run() {


                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }

            }
        });
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    public void clearStageState()

    {

        surfaceRenderer.loadStageData(stages.get(currentStage));

    }

    public void loadStage(final Stage stage) {

        surfaceRenderer.setRunnig(false);
        try {


            //soundsManager.configure(stage.config.sounds);
            //soundsManager.playBackgroundSound();

            mainMenu.setColor(stage.config);
            //final int color = Color.parseColor(stage.config.colors.colorBackground);
            //int c = Color.argb(100, Color.red(color), Color.green(color), Color.blue(color));
            //mainMenu.getBackground().setBackgroundColor(c);
            scoreLayout.config(stage);
            mainMenu.loadStage(stage);


            surfaceRenderer.loadStageData(stage);


        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void toggleMenu() {
        if (drawer.isDrawerOpen(drawerContent))
            drawer.closeDrawers();
        else
            drawer.openDrawer(drawerContent);
    }

    public void moveToNextStage(final String points) {


        currentStage++;
        if (currentStage >= stages.size())
            currentStage = 0;


        int savedStage = preferences.getInt("current_stage", 0);
        if (savedStage < currentStage) {
            unlockedStage = currentStage;
            preferences.edit().putInt("current_stage", unlockedStage).apply();
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d("Zzz", "load stage " + currentStage + " " + stages.size());
                //mainMenu.getAnimationMainMenu().showMe;


                scoreLayout.showScore(points, new AnimationShow.OnAnimationListener() {
                    @Override
                    public void onAnimationEnd() {


                        Log.d("zzz", "move to next stage");
                        //mainMenu.playStage(false);

                        loadStage(stages.get(currentStage));
                        //surfaceRenderer.setRunnig(true);
                        surfaceRenderer.setDrawing(true);


                    }

                    @Override
                    public void onAnimationStart() {

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
                    .open(WORLDS_FILE, Context.MODE_WORLD_READABLE);
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
            ArrayList<World> worlds = new ArrayList<>();
            JSONObject jsonData = new JSONObject(returnString.toString());
            JSONObject api = jsonData.has("api") ? jsonData.getJSONObject("api") : new JSONObject();

            JSONArray jsonArray = api.has("results") ? api.getJSONArray("results") : new JSONArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                worlds.add(World.valueOf(jsonArray.getJSONObject(i)));
            }


            stages = new ArrayList<>();
            for (World w : worlds) {
                stages.addAll(w.stages);
            }

            Log.d(TAG, "stages loaded " + stages.size());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }

    }


    public void onBackPressed() {
        Log.d("zzz", "on back pressd");

        mainMenu.clearAnimation();

        if (surfaceRenderer.onBackPressed()) {
            if (mainMenu.getLayout().getParent() == null) {
                //if (mainMenu.layoutMenu.getVisibility() == View.GONE) {
                surfaceRenderer.setRunnig(false);
                mainMenu.getAnimationMainMenu().showMenu();
                gameHolder.removeView(mockView);
                rootLayout.removeView(layoutButtons);

                Log.d("zzz", "mock view added ");
                mAdView.setVisibility(View.VISIBLE);

                btnSettings.setVisibility(View.GONE);
                btnPalette.setVisibility(View.GONE);
                btnSounds.setVisibility(View.GONE);
                btnLights.setVisibility(View.GONE);
                btnPlay.setVisibility(View.GONE);

                drawerContent.removeAllViews();
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);


            } else {
                soundsManager.stopBackgroundPlayer();

                finish();
            }
        }
    }


    public void onResume() {

        super.onResume();
        surfaceRenderer.onResume();
    }

    public void onPause() {

        super.onPause();
        surfaceRenderer.onPause();

    }

}
