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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;


import io.fabric.sdk.android.Fabric;

import pl.slapps.dot.gui.AnimationShow;
import pl.slapps.dot.gui.fragment.FragmentMainMenu;
import pl.slapps.dot.gui.AnimationScoreLayout;
import pl.slapps.dot.model.Sounds;
import pl.slapps.dot.model.Stage;

public class MainActivity extends FragmentActivity {


    private String TAG = "MainActivity";
    private LinearLayout fragmentContainer;
    public SurfaceRenderer surfaceRenderer;
    public RelativeLayout gameHolder;
    public RelativeLayout rootLayout;
    private View mockView;
    private Stage CURRENT_STAGE;



  //  private SoundsManager soundsManager;

    private SharedPreferences preferences;

    private int currentStage = 0;
    private int unlockedStage = 0;

    private Handler handler = new Handler();


    public InterstitialAd mInterstitialAd;
    public RewardedVideoAd mRewardedVideoAd;

    public AnimationScoreLayout scoreLayout;


    public DrawerLayout drawer;
    public LinearLayout drawerContent;

    public static float screenWidth;
    public static float screenHeight;


    public String android_id;

    private ActivityLoader activityLoader;
    private ActivityControls activityControls;
    // private GoogleBilling activityBilling;
    private GoogleInvite activityInvite;


    //IInAppBillingService mService;



    private static Messenger mService = null;
    private static boolean mBound;
    public static boolean mute;

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            mService = new Messenger(service);
            mBound = true;
            if(CURRENT_STAGE!=null)
                sendAction(SoundsService.ACTION_CONFIG,CURRENT_STAGE.config.sounds);

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
            mService=null;
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //   activityBilling.handleActivityResult(requestCode, resultCode, data);
        activityInvite.onActivityResult(requestCode, resultCode, data);
        Log.d("RRR", "on activity result ");
    }

    public static void sendAction(int action,Object data)
    {
        if (!mBound) return;

        if(action!= SoundsService.ACTION_MUTE && mute && action!= SoundsService.ACTION_CONFIG)
            return;
        // Create and send a message to the service, using a supported 'what' value
        Message msg = Message.obtain(null, action, 0, 0,data);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void mute(boolean flag)
    {
        mute=flag;
        if(mute)
            sendAction(SoundsService.ACTION_MUTE,null);
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
    public GoogleInvite getActivityInvite() {
        return activityInvite;
    }

  //  public SoundsManager getSoundsManager() {
  //      return soundsManager;
  //  }

    public Handler getHandler() {
        return handler;
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



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (activityLoader != null)
            activityLoader.onDestroy();

        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        DAO.initRequestQueue(this);

        Intent intent = new Intent(this, SoundsService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        // if(Build.VERSION.SDK_INT>=11)
        // setTheme(android.R.style.Theme_Holo_Dialog);

        activityInvite = new GoogleInvite(this);

        activityInvite.receive();


        //  activityBilling = new GoogleBilling(this);
        //  activityBilling.setupBilling();
        activityLoader = new ActivityLoader(this, handler);

        activityLoader.listCatche();
        activityLoader.loadSounds();
        activityLoader.listRaw();


        android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        screenHeight = this.getResources().getDisplayMetrics().heightPixels;
        screenWidth = this.getResources().getDisplayMetrics().widthPixels;
        FacebookSdk.sdkInitialize(this.getApplicationContext());

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
        fragmentContainer = (LinearLayout) findViewById(R.id.fragments_container);
        mockView = (View) findViewById(R.id.mock_view);

        gameHolder.removeView(mockView);

        //rootLayout.removeView(activityControls.getLayoutButtons());


        //mainMenu = new MainMenu(this, surfaceRenderer);
        scoreLayout = new AnimationScoreLayout(surfaceRenderer);

        drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawerContent = (LinearLayout) findViewById(R.id.drawer_content);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        //setupFragment();

        //mainMenu.init();
        surfaceRenderer.init();
        scoreLayout.initLayout(this);
        //initMainMenu();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentStage = unlockedStage = preferences.getInt("current_stage", 0);

        activityLoader.loadStagesFile(new ActivityLoader.OnStagesLoadingListener() {
            @Override
            public void onLoaded() {
                Log.d(TAG, "on loaded current stage " + currentStage);
                if (currentStage < activityLoader.jsonStages.length())
                    loadStage(activityLoader.getStageAtIndex(currentStage));
                else {

                    if (activityLoader.jsonStages.length() == 0) {
                        Toast.makeText(MainActivity.this, "??", Toast.LENGTH_LONG).show();
                    } else {
                        currentStage = 0;
                        loadStage(activityLoader.getStageAtIndex(currentStage));
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

            @Override
            public void onProgress(float progress) {
                Log.d("nnn", "on loading progress " + progress);

            }
        }, false);


        //soundsManager.playBackgroundBirds();


        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

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
        AlertDialog.Builder builder = null;

        if (Build.VERSION.SDK_INT >= 11)
            builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT);
        else
            builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.setMessage("Skip to next stage?").setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentStage++;
                if (currentStage == activityLoader.jsonStages.length())
                    currentStage = 0;

                loadStage(activityLoader.getStageAtIndex(currentStage));
                dialog.dismiss();
                ;

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
                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    public void clearStageState()

    {


        surfaceRenderer.loadStageData(activityLoader.getStageAtIndex(currentStage));

    }

    public void loadStage(final Stage stage) {

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


    public void moveToNextStage() {

        currentStage++;
        if (currentStage >= activityLoader.jsonStages.length())
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

                        loadStage(activityLoader.getStageAtIndex(currentStage));
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
            if (getCurrentFragment() == null) {

                // surfaceRenderer.setRunnig(false);
                //  surfaceRenderer.setDrawing(false);
                setupFragment();
                gameHolder.removeView(mockView);


                drawerContent.removeAllViews();
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);


            } else {
               // mService.getSoundsManager().stopBackgroundPlayer();

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
