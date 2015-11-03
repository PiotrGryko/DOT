package pl.slapps.dot;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.media.AsyncPlayer;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pl.slapps.dot.view.GameView;

public class MainActivity extends Activity {

    private String TAG = "MainActivity";
    private GameView game;

    private Dialog dialogMenu;

    private TextView tvName;
    private TextView tvDesc;

    private Button btnPlay;
    private Button btnExit;
    private Button btnGenerate;
    private Button btnStages;
    private Button btnOnline;


    private Uri moveSound;
    private Uri crashSound;
    private Uri soundFinish;
    private Uri soundBackgroundBirds;
    private Uri soundBackgroundBeach;

    private Uri soundBackgroundWater;

    private Uri currentBackground;


    private AsyncPlayer asyncPlayer;
    private AsyncPlayer asyncPlayerBackground;

    // private MediaPlayer mediaPlayerBirds;


    // private LinearLayout optionsLayout;


    private int currentStage = 0;
    private Handler handler = new Handler();

    public void loadStage(JSONObject jsonStage) {
        //setContentView(R.layout.dialog_main_menu);

        try {
          /*
            if (stage >= Stages.stages.size()) {
                stage = currentStage = 0;
            }

            String stageData = Stages.stages.get(stage);


            final JSONObject jsonStage = new JSONObject(stageData);
           */
            final String name = jsonStage.has("name") ? jsonStage.getString("name") : "";
            final String desc = jsonStage.has("description") ? jsonStage.getString("description") : "";
            JSONObject colors = jsonStage.has("colors") ? jsonStage.getJSONObject("colors") : new JSONObject();
            final String backgroundColor = colors.has("background") ? colors.getString("background") : "#ff9999";


            // dialogMenu.setBackgroundColor(Color.parseColor(backgroundColor));


            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (!dialogMenu.isShowing())
                        dialogMenu.show();


                    tvName.setText(name);
                    tvDesc.setText(desc);

                }
            });

            game.loadStageData(jsonStage, backgroundColor);

            /*
            if (stage < 10 && currentBackground != soundBackgroundBirds) {
                playBackgroundBirds();
            } else if (stage >= 10 && stage < 20 && currentBackground != soundBackgroundBeach) {
                playBackgroundBeach();

            } else if (stage >= 20 && currentBackground != soundBackgroundWater) {
                playBackgroundWaves();
            }
*/
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void moveToNextStage() {

        currentStage++;
        try {
            loadStage(new JSONObject(Stages.stages.get(currentStage)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initMainMenu() {

        dialogMenu = new Dialog(this);
        dialogMenu.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogMenu.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //dialogMenu.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        View v = LayoutInflater.from(this).inflate(R.layout.dialog_main_menu, null);

        tvName = (TextView) v.findViewById(R.id.tv_lvl);
        tvDesc = (TextView) v.findViewById(R.id.tv_desc);

        btnExit = (Button) v.findViewById(R.id.btn_exit);
        btnPlay = (Button) v.findViewById(R.id.btn_play);
        btnGenerate = (Button) v.findViewById(R.id.btn_generate);
        btnStages = (Button) v.findViewById(R.id.btn_stages);
        btnOnline = (Button) v.findViewById(R.id.btn_online);

        //  optionsLayout = (LinearLayout) v.findViewById(R.id.layout_options);


        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  optionsLayout.setVisibility(View.GONE);
                dialogMenu.dismiss();
                game.setRunnig(true);
                //   game.onResume();
                //   game.resetDot();

            }
        });

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                game.initGenerator(9, 15);
                dialogMenu.dismiss();
                // optionsLayout.setVisibility(View.GONE);
                //   game.onResume();
                //   game.resetDot();

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
                final Dialog stages = new Dialog(MainActivity.this);
                stages.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

                View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_stages, null);
                ListView listView = (ListView) v.findViewById(R.id.lv);
                ArrayList<String> entries = new ArrayList<String>();
                for (int i = 0; i < Stages.stages.size(); i++) {
                    entries.add(Integer.toString(i));
                }
                listView.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, entries));

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        currentStage = i;
                        try {
                            loadStage(new JSONObject(Stages.stages.get(i)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        stages.dismiss();
                    }
                });
                stages.setContentView(v);
                stages.show();
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
                            ArrayList<String> entries = new ArrayList<String>();
                            for (int i = 0; i < results.length(); i++) {
                                entries.add(Integer.toString(i));
                            }
                            listView.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, entries));

                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    try {
                                        currentStage = i;
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


        dialogMenu.setContentView(v);
        dialogMenu.setCanceledOnTouchOutside(false);
        dialogMenu.setCancelable(false);
        dialogMenu.show();
    }

    public void playFinishSound() {
        asyncPlayer.play(this, soundFinish, false, AudioManager.STREAM_MUSIC);
        //  mediaPlayerMove.start();
    }

    public void playMoveSound() {
        asyncPlayer.play(this, moveSound, false, AudioManager.STREAM_MUSIC);
        //  mediaPlayerMove.start();
    }

    public void playCrashSound() {
        asyncPlayer.play(this, crashSound, false, AudioManager.STREAM_MUSIC);

        //  mediaPlayerCrash.start();
    }

    public void playBackgroundBirds() {
        currentBackground = soundBackgroundBirds;
        asyncPlayerBackground.stop();
        asyncPlayerBackground.play(this, soundBackgroundBirds, true, AudioManager.STREAM_MUSIC);

        //  mediaPlayerCrash.start();
    }

    public void playBackgroundBeach() {
        currentBackground = soundBackgroundBeach;
        asyncPlayerBackground.stop();
        asyncPlayerBackground.play(this, soundBackgroundBeach, true, AudioManager.STREAM_MUSIC);

        //  mediaPlayerCrash.start();
    }

    public void playBackgroundWaves() {
        currentBackground = soundBackgroundWater;
        asyncPlayerBackground.stop();
        asyncPlayerBackground.play(this, soundBackgroundWater, true, AudioManager.STREAM_MUSIC);

        //  mediaPlayerCrash.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        Stages.initStages();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        asyncPlayer = new AsyncPlayer("action");
        asyncPlayerBackground = new AsyncPlayer("background");

        Log.d(TAG, this.getPackageName());
        moveSound = Uri.parse("android.resource://" + this.getPackageName() + "/raw/click2");
        crashSound = Uri.parse("android.resource://" + this.getPackageName() + "/raw/click");
        soundFinish = Uri.parse("android.resource://" + this.getPackageName() + "/raw/finish");
        soundBackgroundBirds = Uri.parse("android.resource://" + this.getPackageName() + "/raw/birds");
        soundBackgroundWater = Uri.parse("android.resource://" + this.getPackageName() + "/raw/waves");
        soundBackgroundBeach = Uri.parse("android.resource://" + this.getPackageName() + "/raw/beach");

//  mediaPlayerBirds = MediaPlayer.create(getApplicationContext(), R.raw.birds);


        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        // making it full screen


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //	game.setLayerType(View.LAYER_TYPE_HARDWARE, null);


        //setContentView(R.layout.dialog_main_menu);
        //setContentView(R.layout.main_activity);
        //game = (GameView) findViewById(R.id.game);
        game = new GameView(this);
        setContentView(game);
        initMainMenu();

        try {
            loadStage(new JSONObject(Stages.stages.get(currentStage)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*
        mediaPlayerBirds.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayerBirds.setLooping(true);
                mediaPlayerBirds.start();

            }
        });
        mediaPlayerBirds.prepareAsync();

*/
        //
        // game.initGenerator(13,16);

        // asyncPlayer.play(this, birdsSound, false, AudioManager.STREAM_MUSIC);

        playBackgroundBirds();

    }

    public void onBackPressed() {

        if (!dialogMenu.isShowing())
            dialogMenu.show();
        else {
            asyncPlayerBackground.stop();

            dialogMenu.dismiss();
            finish();
        }

    }


//	public void onWindowFocusChanged(boolean flag)
//	{
    //	Log.d(TAG, Boolean.toString(game.isHardwareAccelerated()));
//	}

    public void onResume() {

        super.onResume();
        game.onResume();
    }

    public void onPause() {

        super.onPause();
        game.onPause();

    }

}
