package pl.slapps.dot;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;


import io.fabric.sdk.android.Fabric;

import pl.slapps.dot.gui.AnimationShow;
import pl.slapps.dot.gui.fragment.FragmentMainMenu;
import pl.slapps.dot.gui.AnimationScoreLayout;
import pl.slapps.dot.model.Stage;

public class MainActivity extends FragmentActivity {



    private String TAG = "MainActivity";
    private LinearLayout fragmentContainer;
    public SurfaceRenderer surfaceRenderer;
    public RelativeLayout gameHolder;
    public RelativeLayout rootLayout;
    private View mockView;
    private Stage CURRENT_STAGE;




    private SoundsManager soundsManager;

    private SharedPreferences preferences;

    private int currentStage = 0;
    private int unlockedStage = 0;

    private Handler handler = new Handler();


    public Handler getHandler()
    {return handler;}
    public InterstitialAd mInterstitialAd;
    public AdView mAdView;

    //public MainMenu mainMenu;
    public AnimationScoreLayout scoreLayout;



    public DrawerLayout drawer;
    public LinearLayout drawerContent;

    public static float screenWidth;
    public static float screenHeight;


    public String android_id;

    private ActivityLoader activityLoader;
    private ActivityControls activityControls;

    //////////////////////////////////////////////////////////////////////////////////////////////

    public Stage getCurrentStage()
    {
        return CURRENT_STAGE;
    }

    public ActivityLoader getActivityLoader()
    {
        return activityLoader;
    }
    public ActivityControls getActivityControls()
    {
        return activityControls;
    }
    public SoundsManager getSoundsManager() {
        return soundsManager;
    }



    public View getMockView() {
        return mockView;
    }





    private void setupFragment()
    {
        if(fragmentContainer.getParent()==null)
            gameHolder.addView(fragmentContainer);
        FragmentManager fm= getSupportFragmentManager();
        fm.beginTransaction().add(R.id.fragments_container,new FragmentMainMenu()).commitAllowingStateLoss();


        //  FragmentManager fm = getFragmentManager();

    }

    public Fragment getCurrentFragment()
    {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragments_container);
        return fragment;
    }

    public void removeCurrentFragment()
    {
        Fragment f = getCurrentFragment();
        if(f!=null) {
            getSupportFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();
            Log.d("aaa", "fragment removed");
            if(fragmentContainer.getParent()!=null)
                gameHolder.removeView(fragmentContainer);

        }

        Log.d("aaa","current fragment not removed");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        activityLoader = new ActivityLoader(this);

        activityLoader.listCatche();
        activityLoader.loadSounds();
        activityLoader.listRaw();
        activityLoader.loadStagesFile();



        android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        screenHeight = this.getResources().getDisplayMetrics().heightPixels;
        screenWidth = this.getResources().getDisplayMetrics().widthPixels;
        FacebookSdk.sdkInitialize(this.getApplicationContext());


        soundsManager = new SoundsManager(this);


        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentStage = unlockedStage = preferences.getInt("current_stage", 0);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.main_activity);


        activityControls = new ActivityControls(this,handler);
        activityControls.initLayout();
        //initGeneratorButtons();

        surfaceRenderer = (SurfaceRenderer) findViewById(R.id.game);
        gameHolder = (RelativeLayout) findViewById(R.id.game_holder);
        rootLayout = (RelativeLayout) findViewById(R.id.root_layout);
        fragmentContainer = (LinearLayout)findViewById(R.id.fragments_container);
        mockView = (View)findViewById(R.id.mock_view);

        gameHolder.removeView(mockView);

        rootLayout.removeView(activityControls.getLayoutButtons());


        //mainMenu = new MainMenu(this, surfaceRenderer);
        scoreLayout = new AnimationScoreLayout(surfaceRenderer);
        drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawerContent = (LinearLayout) findViewById(R.id.drawer_content);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        //setupFragment();

        //mainMenu.init();
        surfaceRenderer.init(this);
        scoreLayout.initLayout(this);
        //initMainMenu();

        loadStage(activityLoader.stages.get(currentStage));



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



        setupFragment();


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

        surfaceRenderer.loadStageData(activityLoader.stages.get(currentStage));

    }

    public void loadStage(final Stage stage) {

        surfaceRenderer.setRunnig(false);
        try {


            //soundsManager.configure(stage.config.sounds);
            //soundsManager.playBackgroundSound();

            if(getCurrentFragment() instanceof FragmentMainMenu)
            {
                Log.d("aaa","fragment configured");
                FragmentMainMenu fm = (FragmentMainMenu)getCurrentFragment();
                fm.setColor(stage.config);
                final int color = Color.parseColor(stage.config.colors.colorBackground);
                int c = Color.argb(100, Color.red(color), Color.green(color), Color.blue(color));
                //fm.getBackground().setBackgroundColor(c);
                fm.loadStage(stage);

            }
            Log.d("aaa","stage loaded");

            scoreLayout.config(stage);


            surfaceRenderer.loadStageData(stage);


        } catch (Throwable e) {
            e.printStackTrace();
        }
        CURRENT_STAGE = stage;
    }

    public void toggleMenu() {
        if (drawer.isDrawerOpen(drawerContent))
            drawer.closeDrawers();
        else
            drawer.openDrawer(drawerContent);
    }

    public void moveToNextStage(final String points) {


        currentStage++;
        if (currentStage >= activityLoader.stages.size())
            currentStage = 0;


        int savedStage = preferences.getInt("current_stage", 0);
        if (savedStage < currentStage) {
            unlockedStage = currentStage;
            preferences.edit().putInt("current_stage", unlockedStage).apply();
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d("Zzz", "load stage " + currentStage + " " + activityLoader.stages.size());
                //mainMenu.getAnimationMainMenu().showMe;


                scoreLayout.showScore(points, new AnimationShow.OnAnimationListener() {
                    @Override
                    public void onAnimationEnd() {


                        Log.d("zzz", "move to next stage");
                        //mainMenu.playStage(false);

                        loadStage(activityLoader.stages.get(currentStage));
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





    public void onBackPressed() {
        Log.d("zzz", "on back pressed");

       // mainMenu.clearAnimation();

        if (surfaceRenderer.onBackPressed()) {

            if(getCurrentFragment()==null){

                Log.d("aaa","current fragment not null");
                setupFragment();
            //if (mainMenu.getLayout().getParent() == null) {
                //if (mainMenu.layoutMenu.getVisibility() == View.GONE) {
                surfaceRenderer.setRunnig(false);
                //mainMenu.getAnimationMainMenu().showMenu();
                gameHolder.removeView(mockView);
                rootLayout.removeView(activityControls.getLayoutButtons());

                Log.d("zzz", "mock view added ");
                mAdView.setVisibility(View.VISIBLE);


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
