package pl.slapps.dot;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.facebook.FacebookSdk;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import pl.slapps.dot.game.GameView;
import pl.slapps.dot.model.Stage;

public class MainActivity extends Activity {

    private String TAG = "MainActivity";
    private GameView game;


    private final String STAGES_FILE = "stages.json";


    private SoundsManager soundsManager;

    private SharedPreferences preferences;
    public JSONArray stages;

    private int currentStage = 0;
    private int unlockedStage = 0;

    private Handler handler = new Handler();


    public InterstitialAd mInterstitialAd;
    public AdView mAdView;

    public MainMenu mainMenu;

    public DrawerLayout drawer;
    public LinearLayout drawerContent;


    public static ArrayList<String> listRaw() {
        ArrayList<String> files = new ArrayList<>();
        Field[] fields = R.raw.class.getFields();
        for (int count = 0; count < fields.length; count++) {

            files.add(fields[count].getName());

        }
        return files;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        game = (GameView) findViewById(R.id.game);

        mainMenu = new MainMenu(this, game);

        mainMenu.initMenu();
        //initMainMenu();


        try {
            loadStage(Stage.valueOf(stages.getJSONObject(currentStage)));
        } catch (JSONException e) {
            e.printStackTrace();
        }


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
                mainMenu.headerHideAnimation.startAnimation();
                mainMenu.btnsHideAnimation.startAnimation();
                mAdView.setVisibility(View.GONE);

            }
        });

        requestNewInterstitial();


        drawer = (DrawerLayout)findViewById(R.id.drawer);
        drawerContent = (LinearLayout)findViewById(R.id.drawer_content);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);


    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    public void clearStageState()

    {
        try {
            game.loadStageData(Stage.valueOf(stages.getJSONObject(currentStage)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loadStage(final Stage stage) {

        game.setRunnig(false);
        try {





            soundsManager.configure(stage.sounds);
            soundsManager.playBackgroundSound();


            final int color = Color.parseColor(stage.colorBackground);
            int c = Color.argb(100, Color.red(color), Color.green(color), Color.blue(color));
            mainMenu.menuBkg.setBackgroundColor(c);

            handler.post(new Runnable() {
                @Override
                public void run() {
                    mainMenu.loadStage(stage);

                }
            });
            game.loadStageData(stage);


        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void toggleMenu()
    {
        if(drawer.isDrawerOpen(drawerContent))
            drawer.closeDrawers();
        else
            drawer.openDrawer(drawerContent);
    }

    public void moveToNextStage() {


        currentStage++;
        if (currentStage >= stages.length())
            currentStage = stages.length() - 1;
        try {
            loadStage(Stage.valueOf(stages.getJSONObject(currentStage)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int savedStage = preferences.getInt("current_stage", 0);
        if (savedStage < currentStage) {
            unlockedStage = currentStage;
            preferences.edit().putInt("current_stage", unlockedStage).apply();
        }
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

        mainMenu.layoutMenu.clearAnimation();

        if (mainMenu.layoutMenu.getVisibility() == View.GONE) {
            game.setRunnig(false);
            mainMenu.menuShowAnimation.startAnimation(null);
            mAdView.setVisibility(View.VISIBLE);
            mainMenu.btnSettings.setVisibility(View.GONE);

            drawerContent.removeAllViews();
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);



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
