package pl.slapps.dot;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.unity3d.ads.android.IUnityAdsListener;
import com.unity3d.ads.android.UnityAds;


import java.io.IOException;
import java.io.InputStream;


import pl.slapps.dot.gui.AnimationShow;
import pl.slapps.dot.gui.fragment.LayoutMainMenu;
import pl.slapps.dot.gui.AnimationScoreLayout;
import pl.slapps.dot.model.Route;
import pl.slapps.dot.model.Stage;

public class MainActivity extends FragmentActivity {


    private String TAG = "MainActivity";
    //private LinearLayout fragmentContainer;
    public SurfaceRenderer surfaceRenderer;
    public RelativeLayout gameHolder;
    public RelativeLayout rootLayout;
    private Stage CURRENT_STAGE;


    //  private SoundsManager soundsManager;

    private SharedPreferences preferences;

    private int currentStage = 0;
    private int unlockedStage = 0;

    private Handler handler = new Handler();


    public InterstitialAd mInterstitialAd;
    public RewardedVideoAd mRewardedVideoAd;
    //public AdView mAdView;


    public AnimationScoreLayout scoreLayout;


    // public DrawerLayout drawer;
    // public LinearLayout drawerContent;

    public static float screenWidth;
    public static float screenHeight;


    public String android_id;

    private ActivityLoader activityLoader;
    private ActivityControls activityControls;

    private LayoutMainMenu layoutMainMenu;


    //IInAppBillingService mService;


    private static Messenger mService = null;
    private static boolean mBound;
    public static boolean mute;

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            mService = new Messenger(service);
            mBound = true;
            if (CURRENT_STAGE != null)
                sendAction(SoundsService.ACTION_CONFIG, CURRENT_STAGE.config.sounds);

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
            mService = null;
        }
    };


    public static void configServiceStage(Stage stage) {
        sendAction(SoundsService.ACTION_CONFIG, stage.config.sounds);

        for (int i = 0; i < stage.routes.size(); i++) {
            Route r = stage.routes.get(i);
            if (r.sound != null && !r.sound.trim().equals(""))
                transferMessage(SoundsService.ACTION_CONFIG_RAW, r.sound);
        }
    }

    public static void sendAction(int action, Object data) {
        if (!mBound) return;

        if (action != SoundsService.ACTION_MUTE && mute && action != SoundsService.ACTION_CONFIG)
            return;
        // Create and send a message to the service, using a supported 'what' value
        transferMessage(action, data);
    }

    private static void transferMessage(int action, Object data) {
        Message msg = Message.obtain(null, action, 0, 0, data);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void mute(boolean flag) {
        mute = flag;
        if (mute)
            sendAction(SoundsService.ACTION_MUTE, null);
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

    /*
    public GoogleBilling getActivityBilling() {
        return activityBilling;
    }
*/


    //  public SoundsManager getSoundsManager() {
    //      return soundsManager;
    //  }

    public Handler getHandler() {
        return handler;
    }


    private void setupMainMenu() {
      /*  if (fragmentContainer.getParent() == null)
            gameHolder.addView(fragmentContainer);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.fragments_container, new LayoutMainMenu()).commitAllowingStateLoss();
*/

        //  FragmentManager fm = getFragmentManager();
        layoutMainMenu = new LayoutMainMenu();
        gameHolder.addView(layoutMainMenu.onCreateView(this));

    }

    /*
        public Fragment getCurrentFragment() {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragments_container);
            return fragment;
        }
    */
    public void removeMainMenu() {

        if (layoutMainMenu.getLayout().getParent() != null)
            gameHolder.removeView(layoutMainMenu.getLayout());

        // surfaceRenderer.setGameLoop();
       /*
        Fragment f = getCurrentFragment();
        if (f != null) {
            getSupportFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();
            if (fragmentContainer.getParent() != null)
                gameHolder.removeView(fragmentContainer);

        }
*/

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        surfaceRenderer.stopGameThread();
        if (activityLoader != null)
            activityLoader.onDestroy();

        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }

    }

    /**
     * parse the CPU info to get the BogoMIPS.
     *
     * @return the BogoMIPS value as a String
     */
    public static String getBogoMipsFromCpuInfo() {
        String result = null;
        String cpuInfo = readCPUinfo();
        String[] cpuInfoArray = cpuInfo.split(":");
        for (int i = 0; i < cpuInfoArray.length; i++) {
            if (cpuInfoArray[i].contains("BogoMIPS")) {
                result = cpuInfoArray[i + 1];
                break;
            }
        }
        if (result != null) result = result.trim();
        return result;
    }

    /**
     * @return the CPU info.
     * @see {http://stackoverflow.com/a/3021088/3014036}
     */
    public static String readCPUinfo() {
        ProcessBuilder cmd;
        String result = "";
        InputStream in = null;
        try {
            String[] args = {"/system/bin/cat", "/proc/cpuinfo"};
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            in = process.getInputStream();
            byte[] re = new byte[1024];
            while (in.read(re) != -1) {
                System.out.println(new String(re));
                result = result + new String(re);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DAO.initRequestQueue(this);

        Log.d("GLTEST", readCPUinfo());

        Intent intent = new Intent(this, SoundsService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        activityLoader = new ActivityLoader(this, handler);

        activityLoader.listCatche();
        //activityLoader.loadSounds();
        //activityLoader.listSoundsFromAssets();


        android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        screenHeight = this.getResources().getDisplayMetrics().heightPixels;
        screenWidth = this.getResources().getDisplayMetrics().widthPixels;

        //   soundsManager = new SoundsManager(this);

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


        //rootLayout.removeView(activityControls.getLayoutButtons());


        //mainMenu = new MainMenu(this, surfaceRenderer);
        scoreLayout = new AnimationScoreLayout(surfaceRenderer);

        //  drawer = (DrawerLayout) findViewById(R.id.drawer);
        //   drawerContent = (LinearLayout) findViewById(R.id.drawer_content);
        //   drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        //setupFragment();

        //mainMenu.init();
        surfaceRenderer.init();
        scoreLayout.initLayout(this);
        setupMainMenu();

        //initMainMenu();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentStage = unlockedStage = preferences.getInt("current_stage", 0);

        Log.d("activityloader", "start loading stages");
        activityLoader.loadStagesFile(new ActivityLoader.OnStagesLoadingListener() {
            @Override
            public void onLoaded() {


                activityLoader.getStageAtIndex(currentStage, new ActivityLoader.OnStageLoadingListener() {
                    @Override
                    public void onLoaded(Stage stage) {
                        loadStage(stage);
                    }

                    @Override
                    public void onFailed() {

                        Log.d(TAG, "loading stage fialed! ");
                        currentStage = 0;
                        activityLoader.getStageAtIndex(currentStage, new ActivityLoader.OnStageLoadingListener() {
                            @Override
                            public void onLoaded(Stage stage) {
                                loadStage(stage);
                            }

                            @Override
                            public void onFailed() {
                                Toast.makeText(MainActivity.this, "??", Toast.LENGTH_LONG).show();

                            }
                        });
                    }
                });

            }

            @Override
            public void onFailed() {

                layoutMainMenu.displayError();

            }

            @Override
            public void onProgress(float progress) {
                Log.d("nnn", "on loading progress " + progress);

            }
        }, true);



        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-2117747061583571/7695580442");

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                surfaceRenderer.getGame().setPaused(false);
                //    mainMenu.headerHideAnimation.startAnimation(500);
                //    mainMenu.btnsHideAnimation.startAnimation(500);
                //mAdView.setVisibility(View.GONE);

            }
        });

        //requestNewInterstitial();


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

                Log.d("ADS","close");
                requestNewAward();

            }

            @Override
            public void onRewarded(RewardItem rewardItem) {


                Log.d("ADS","rewarded..");
                currentStage++;
                // if (currentStage == activityLoader.jsonStages.length())
                //     currentStage = 0;

                int savedStage = preferences.getInt("current_stage", 0);
                if (savedStage < currentStage) {
                    unlockedStage = currentStage;

                    preferences.edit().putInt("current_stage", unlockedStage).apply();
                }

                activityLoader.getStageAtIndex(currentStage, new ActivityLoader.OnStageLoadingListener() {
                    @Override
                    public void onLoaded(Stage stage) {
                        loadStage(stage);

                    }

                    @Override
                    public void onFailed() {

                    }

                });


            }

            @Override
            public void onRewardedVideoAdLeftApplication() {

            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
                Log.d("ADS","rewarded failed");

            }
        });

        //requestNewAward();

    }

    public boolean showAdv() {
        Log.d(TAG, "showinga ads...");

            Log.d(TAG, "add is loaded");

            handler.post(new Runnable() {
                @Override
                public void run() {

                    if (mInterstitialAd.isLoaded()) {

                        mInterstitialAd.show();

                    }
                }
            });
            return true;
  
    }

    private void show_award()
    {

        if(mRewardedVideoAd.isLoaded()) {
            Log.d("ADS","show rewarded ad");
            mRewardedVideoAd.show();

        }
        else
            Log.d("ADS","rewarded not loaded");

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
        AlertDialog.Builder builder = null;

        if (Build.VERSION.SDK_INT >= 11)
            builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        else
            builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.setMessage("Skip to next stage?").setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                show_award();
                dialog.dismiss();


            }
        }).setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.show();


    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    private void requestNewAward()
    {
        Bundle extras = new Bundle();
        extras.putBoolean("_noRefresh", true);
        AdRequest adRequest = new AdRequest.Builder()
                .addNetworkExtrasBundle(com.google.ads.mediation.unity.UnityAdapter.class, extras)
                .build();
        mRewardedVideoAd.loadAd(getResources().getString(R.string.award_ad_unit_id), adRequest);
        Log.d("ads","loading reward..");
    }

    public void clearStageState()

    {

        activityLoader.getStageAtIndex(currentStage, new ActivityLoader.OnStageLoadingListener() {
            @Override
            public void onLoaded(Stage stage) {
                loadStage(stage);
            }

            @Override
            public void onFailed() {

            }
        });

        // surfaceRenderer.loadStageData(activityLoader.getStageAtIndex(currentStage));

    }

    public void loadStage(final Stage stage) {

        surfaceRenderer.setRunnig(false);
        try {


            //soundsManager.configure(stage.config.sounds);
            //soundsManager.playBackgroundSound();

            /*
            if (getCurrentFragment() instanceof LayoutMainMenu) {
                LayoutMainMenu fm = (LayoutMainMenu) getCurrentFragment();
                fm.setColor(stage.config);

                fm.loadStage(stage, currentStage);

            }
*/
            layoutMainMenu.loadStage(stage, currentStage);
            scoreLayout.config(stage, currentStage);


            surfaceRenderer.loadStageData(stage);


        } catch (Throwable e) {
            e.printStackTrace();
        }
        CURRENT_STAGE = stage;
        //   surfaceRenderer.setRunnig(true);
    }


    public void moveToNextStage() {

        currentStage++;
        //   if (currentStage >= activityLoader.jsonStages.length())
        //       currentStage = 0;


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

                        activityLoader.getStageAtIndex(currentStage, new ActivityLoader.OnStageLoadingListener() {
                            @Override
                            public void onLoaded(Stage stage) {
                                loadStage(stage);
                            }

                            @Override
                            public void onFailed() {
                                currentStage = 0;
                                activityLoader.getStageAtIndex(currentStage, new ActivityLoader.OnStageLoadingListener() {
                                    @Override
                                    public void onLoaded(Stage stage) {
                                        loadStage(stage);
                                    }

                                    @Override
                                    public void onFailed() {
                                        Toast.makeText(MainActivity.this, "??", Toast.LENGTH_LONG).show();

                                    }
                                });
                            }
                        });
                        // loadStage(activityLoader.getStageAtIndex(currentStage));
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

        //


        if (surfaceRenderer.onBackPressed()) {
            surfaceRenderer.drawGenerator = false;

            if (layoutMainMenu.getLayout().getParent() == null) {

                // surfaceRenderer.setRunnig(false);
                //  surfaceRenderer.setDrawing(false);
                setupMainMenu();


                //     drawerContent.removeAllViews();
                //     drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);


            } else {
                // mService.getSoundsManager().stopBackgroundPlayer();

                finish();
            }
        }

    }


    public void onResume() {

        super.onResume();

        surfaceRenderer.onResume();

        //UnityAds.changeActivity(this);


    }

    public void onPause() {

        super.onPause();
        surfaceRenderer.onPause();

    }

}
