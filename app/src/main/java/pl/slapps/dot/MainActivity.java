package pl.slapps.dot;

import android.content.Intent;
import android.net.Uri;
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
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;


import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

import pl.slapps.dot.billing.util.IabHelper;
import pl.slapps.dot.billing.util.IabResult;
import pl.slapps.dot.billing.util.Purchase;
import pl.slapps.dot.drawing.Util;
import pl.slapps.dot.gui.AnimationShow;
import pl.slapps.dot.gui.fragment.AnimationRandomLayout;
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


    public Handler getHandler() {
        return handler;
    }

    public InterstitialAd mInterstitialAd;
    public AdView mAdView;
    public RewardedVideoAd mRewardedVideoAd;

    //public MainMenu mainMenu;
    public AnimationScoreLayout scoreLayout;
    public AnimationRandomLayout randomLayout;


    public DrawerLayout drawer;
    public LinearLayout drawerContent;

    public static float screenWidth;
    public static float screenHeight;


    public String android_id;

    private ActivityLoader activityLoader;
    private ActivityControls activityControls;
    private GoogleBilling activityBilling;
    private GoogleInvite activityInvite;


    //IInAppBillingService mService;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        activityBilling.handleActivityResult(requestCode, resultCode, data);
        activityInvite.onActivityResult(requestCode, resultCode, data);
        Log.d("RRR", "on activity result ");
    }


    //////////////////////////////////////////////////////////////////////////////////////////////

    public Stage getCurrentStage() {
        return CURRENT_STAGE;
    }

    public ActivityLoader getActivityLoader() {
        return activityLoader;
    }

    public ActivityControls getActivityControls() {
        return activityControls;
    }

    public GoogleBilling getActivityBilling() {
        return activityBilling;
    }

    public GoogleInvite getActivityInvite() {
        return activityInvite;
    }

    public SoundsManager getSoundsManager() {
        return soundsManager;
    }


    public View getMockView() {
        return mockView;
    }


    private void setupFragment() {
        if (fragmentContainer.getParent() == null)
            gameHolder.addView(fragmentContainer);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.fragments_container, new FragmentMainMenu()).commitAllowingStateLoss();


        //  FragmentManager fm = getFragmentManager();

    }

    public Fragment getCurrentFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragments_container);
        return fragment;
    }

    public void removeCurrentFragment() {
        Fragment f = getCurrentFragment();
        if (f != null) {
            getSupportFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();
            if (fragmentContainer.getParent() != null)
                gameHolder.removeView(fragmentContainer);

        }


    }

    /*
        @Override
        public void onDestroy() {
            super.onDestroy();
            if (mService != null) {
                unbindService(mServiceConn);
            }
        }
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        DAO.initRequestQueue(this);

        activityInvite = new GoogleInvite(this);

        activityInvite.receive();


        activityBilling = new GoogleBilling(this);
        activityBilling.setupBilling();
        activityLoader = new ActivityLoader(this);

        activityLoader.listCatche();
        activityLoader.loadSounds();
        activityLoader.listRaw();


        android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        screenHeight = this.getResources().getDisplayMetrics().heightPixels;
        screenWidth = this.getResources().getDisplayMetrics().widthPixels;
        FacebookSdk.sdkInitialize(this.getApplicationContext());


        soundsManager = new SoundsManager(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.main_activity);


        activityControls = new ActivityControls(this, handler);
        activityControls.initLayout();
        //initGeneratorButtons();

        surfaceRenderer = (SurfaceRenderer) findViewById(R.id.game);
        gameHolder = (RelativeLayout) findViewById(R.id.game_holder);
        rootLayout = (RelativeLayout) findViewById(R.id.root_layout);
        fragmentContainer = (LinearLayout) findViewById(R.id.fragments_container);
        mockView = (View) findViewById(R.id.mock_view);

        gameHolder.removeView(mockView);

        //rootLayout.removeView(activityControls.getLayoutButtons());


        //mainMenu = new MainMenu(this, surfaceRenderer);
        scoreLayout = new AnimationScoreLayout(surfaceRenderer);
        randomLayout = new AnimationRandomLayout(surfaceRenderer);

        drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawerContent = (LinearLayout) findViewById(R.id.drawer_content);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        //setupFragment();

        //mainMenu.init();
        surfaceRenderer.init(this);
        scoreLayout.initLayout(this);
        randomLayout.initLayout(this);
        //initMainMenu();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentStage = unlockedStage = preferences.getInt("current_stage", 0);

        activityLoader.loadStagesFile(new ActivityLoader.OnStagesLoadingListener() {
            @Override
            public void onLoaded() {
                if (currentStage < activityLoader.stages.size())
                    loadStage(activityLoader.stages.get(currentStage), false);
                else {

                    if (activityLoader.stages.size() == 0) {
                        Toast.makeText(MainActivity.this, "??", Toast.LENGTH_LONG).show();
                    } else {
                        currentStage = 0;
                        loadStage(activityLoader.stages.get(currentStage), false);
                    }
                }
            }

            @Override
            public void onFailed() {
                if (getCurrentFragment() instanceof FragmentMainMenu) {
                    FragmentMainMenu fm = (FragmentMainMenu) getCurrentFragment();

                    fm.displayError();

                }
            }
        });


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



        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {

            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {

            }

            @Override
            public void onRewarded(RewardItem rewardItem) {

            }

            @Override
            public void onRewardedVideoAdLeftApplication() {

            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {

            }
        });


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

    public void showVideoAdv() {

        /*
        Bundle extras = new Bundle();
        extras.putBoolean("_noRefresh", true);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("E94C03F2EE6C70812C2298399AAE3483")
                .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                .build();
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/1033173712", adRequest);
        mRewardedVideoAd.show();
 */
        currentStage++;
        if(currentStage==activityLoader.stages.size())
            currentStage=0;

        loadStage(activityLoader.stages.get(currentStage), false);


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

    public void loadStage(final Stage stage, boolean shuffle) {

        this.isRandomStage = shuffle;
        surfaceRenderer.setRunnig(false);
        try {


            //soundsManager.configure(stage.config.sounds);
            //soundsManager.playBackgroundSound();

            if (getCurrentFragment() instanceof FragmentMainMenu) {
                FragmentMainMenu fm = (FragmentMainMenu) getCurrentFragment();
                fm.setColor(stage.config);

                fm.loadStage(stage, currentStage);

            }

            scoreLayout.config(stage, currentStage);
            randomLayout.config(stage);


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

    public void showRandom() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                //Log.d("Zzz", "load stage " + currentStage + " " + activityLoader.stages.size());
                //mainMenu.getAnimationMainMenu().showMe;


                randomLayout.showLayout(new AnimationShow.OnAnimationListener() {
                    @Override
                    public void onAnimationEnd() {


                        //  Log.d("zzz", "move to next stage");
                        //mainMenu.playStage(false);

                        //loadStage(activityLoader.stages.get(currentStage));
                        //surfaceRenderer.setRunnig(true);
                        // surfaceRenderer.setDrawing(true);


                    }

                    @Override
                    public void onAnimationStart() {

                    }
                });
            }
        });
    }

    private boolean isRandomStage;

    public void moveToNextStage() {

        if (isRandomStage) {

            showRandom();
        } else {
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
                    //mainMenu.getAnimationMainMenu().showMe;


                    scoreLayout.showScore(new AnimationShow.OnAnimationListener() {
                        @Override
                        public void onAnimationEnd() {


                            //mainMenu.playStage(false);

                            loadStage(activityLoader.stages.get(currentStage), false);
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
    }


    public void onBackPressed() {


        if (isRandomStage && randomLayout.getLayout().getParent() == null) {
            randomLayout.showLayout(null);
        } else if (surfaceRenderer.onBackPressed()) {
            if (getCurrentFragment() == null) {
                randomLayout.hide();
                setupFragment();
                surfaceRenderer.setRunnig(false);
                gameHolder.removeView(mockView);
                mAdView.setVisibility(View.VISIBLE);


                drawerContent.removeAllViews();
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                isRandomStage = false;


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
