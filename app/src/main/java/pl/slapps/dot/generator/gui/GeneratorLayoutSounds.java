package pl.slapps.dot.generator.gui;

import android.app.Dialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pl.slapps.dot.DAO;
import pl.slapps.dot.MainActivity;
import pl.slapps.dot.R;
import pl.slapps.dot.SoundsManager;
import pl.slapps.dot.adapter.AdapterSounds;
import pl.slapps.dot.generator.Generator;
import pl.slapps.dot.generator.widget.PlayButton;

/**
 * Created by piotr on 14/02/16.
 */
public class GeneratorLayoutSounds {

    private String TAG = GeneratorLayoutSounds.class.getName();
    private View layoutSounds;
    private Generator generator;
    private GeneratorLayout generatorLayout;

    /*
   sounds layout elements
    */
    private TextView btnSoundBackground;
    private TextView btnSoundPress;
    private TextView btnSoundCrash;
    private TextView btnSoundCrashTwo;

    private TextView btnSoundFinish;

    private LinearLayout baseBackground;
    private LinearLayout basePress;
    private LinearLayout baseCrash;
    private LinearLayout baseCrashTwo;
    private LinearLayout baseFinish;

    private PlayButton btnPlayBackground;
    private PlayButton btnPlayPress;
    private PlayButton btnPlayCrash;
    private PlayButton btnPlayCrashTwo;
    private PlayButton btnPlayFinish;

    private CheckBox cbOverlap;

    public View getLayout() {
        return layoutSounds;
    }




    public void refreashLayout() {

        Log.d("aaa",generator.getConfig().sounds.toJson().toString());
        if (!generator.getConfig().sounds.soundBackground.trim().equals(""))
            btnSoundBackground.setText(generator.getConfig().sounds.soundBackground);

        if (!generator.getConfig().sounds.soundPress.trim().equals(""))
            btnSoundPress.setText(generator.getConfig().sounds.soundPress);
        //else
        //    btnSoundPress.setText(SoundsManager.DEFAULT_PRESS);
        if (!generator.getConfig().sounds.soundCrash.trim().equals(""))
            btnSoundCrash.setText(generator.getConfig().sounds.soundCrash);

        if (!generator.getConfig().sounds.soundCrashTwo.trim().equals(""))
            btnSoundCrashTwo.setText(generator.getConfig().sounds.soundCrashTwo);
        //else
        //    btnSoundCrash.setText(SoundsManager.DEFAULT_CRASH);
        if (!generator.getConfig().sounds.soundFinish.trim().equals(""))
            btnSoundFinish.setText(generator.getConfig().sounds.soundFinish);
        //else
        //    btnSoundFinish.setText(SoundsManager.DEFAULT_FINISH);

        cbOverlap.setChecked(generator.getConfig().sounds.overlap);

    }

    public void initLayout(final GeneratorLayout generatorLayout) {
        this.generatorLayout = generatorLayout;
        this.generator = generatorLayout.generator;

        layoutSounds = LayoutInflater.from(this.generator.view.context).inflate(R.layout.layout_generator_sounds, null);

        btnSoundBackground = (TextView) layoutSounds.findViewById(R.id.btn_choose_background);
        btnSoundPress = (TextView) layoutSounds.findViewById(R.id.btn_choose_press);
        btnSoundCrash = (TextView) layoutSounds.findViewById(R.id.btn_choose_crash);
        btnSoundCrashTwo = (TextView) layoutSounds.findViewById(R.id.btn_choose_crash_two);
        btnSoundFinish = (TextView) layoutSounds.findViewById(R.id.btn_choose_finish);

        baseBackground = (LinearLayout) layoutSounds.findViewById(R.id.base_background);
        basePress = (LinearLayout) layoutSounds.findViewById(R.id.base_press);
        baseCrash = (LinearLayout) layoutSounds.findViewById(R.id.base_crash);
        baseCrashTwo = (LinearLayout) layoutSounds.findViewById(R.id.base_crash_two);
        baseFinish = (LinearLayout) layoutSounds.findViewById(R.id.base_finish);

        btnPlayBackground = (PlayButton) layoutSounds.findViewById(R.id.btn_play_background);
        btnPlayPress = (PlayButton) layoutSounds.findViewById(R.id.btn_play_press);
        btnPlayCrash = (PlayButton) layoutSounds.findViewById(R.id.btn_play_crash);
        btnPlayCrashTwo = (PlayButton) layoutSounds.findViewById(R.id.btn_play_crash_two);
        btnPlayFinish = (PlayButton) layoutSounds.findViewById(R.id.btn_play_finish);

        cbOverlap = (CheckBox) layoutSounds.findViewById(R.id.cb_overlap);


        refreashLayout();

        cbOverlap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                generator.getConfig().sounds.overlap=b;
                generator.refreashMaze();
            }
        });

        btnPlayBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vie) {
                if (generator.getConfig().sounds.soundBackground.trim().equals("")) {
                    Toast.makeText(generator.view.context, "Select file first", Toast.LENGTH_LONG).show();
                    return;
                }

                btnPlayBackground.play(generator.view.context.getSoundsManager().parseSound(generator.getConfig().sounds.soundBackground));
            }
        });
        btnPlayPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vie) {
                if (generator.getConfig().sounds.soundPress.trim().equals("")) {
                    Toast.makeText(generator.view.context, "Select file first", Toast.LENGTH_LONG).show();
                    return;
                }
                btnPlayPress.play(generator.view.context.getSoundsManager().parseSound(generator.getConfig().sounds.soundPress));


            }
        });
        btnPlayCrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vie) {
                if (generator.getConfig().sounds.soundCrash.trim().equals("")) {
                    Toast.makeText(generator.view.context, "Select file first", Toast.LENGTH_LONG).show();
                    return;
                }
                btnPlayCrash.play(generator.view.context.getSoundsManager().parseSound(generator.getConfig().sounds.soundCrash));


            }
        });
        btnPlayCrashTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vie) {
                if (generator.getConfig().sounds.soundCrashTwo.trim().equals("")) {
                    Toast.makeText(generator.view.context, "Select file first", Toast.LENGTH_LONG).show();
                    return;
                }
                btnPlayCrashTwo.play(generator.view.context.getSoundsManager().parseSound(generator.getConfig().sounds.soundCrashTwo));


            }
        });
        btnPlayFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vie) {
                if (generator.getConfig().sounds.soundFinish.trim().equals("")) {
                    Toast.makeText(generator.view.context, "Select file first", Toast.LENGTH_LONG).show();
                    return;
                }
                btnPlayFinish.play(generator.view.context.getSoundsManager().parseSound(generator.getConfig().sounds.soundFinish));


            }
        });


        baseBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                final Dialog dialogChooseSound = new Dialog(generator.view.context);
                dialogChooseSound.requestWindowFeature(Window.FEATURE_NO_TITLE);
                final View chooseView = LayoutInflater.from(generator.view.context).inflate(R.layout.dialog_stages, null);
                ListView lv = (ListView) chooseView.findViewById(R.id.lv);
                lv.setAdapter(new AdapterSounds(generator.view.context, generator.view.context.getActivityLoader().listRaw()));

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        generator.getConfig().sounds.soundBackground = adapterView.getItemAtPosition(i).toString();
                        btnSoundBackground.setText(generator.getConfig().sounds.soundBackground);
                        generator.refreashMaze();
                        generator.view.context.getSoundsManager().playBackgroundSound();

                        dialogChooseSound.dismiss();
                    }
                });
                dialogChooseSound.setContentView(chooseView);
                dialogChooseSound.show();

            }
        });

        basePress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                final Dialog dialogChooseSound = new Dialog(generator.view.context);
                dialogChooseSound.requestWindowFeature(Window.FEATURE_NO_TITLE);
                View chooseView = LayoutInflater.from(generator.view.context).inflate(R.layout.dialog_stages, null);
                ListView lv = (ListView) chooseView.findViewById(R.id.lv);
                lv.setAdapter(new AdapterSounds(generator.view.context, generator.view.context.getActivityLoader().listRaw()));
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        generator.getConfig().sounds.soundPress = adapterView.getItemAtPosition(i).toString();
                        btnSoundPress.setText(generator.getConfig().sounds.soundPress);
                        generator.refreashMaze();

                        dialogChooseSound.dismiss();
                    }
                });
                dialogChooseSound.setContentView(chooseView);
                dialogChooseSound.show();

            }
        });

        baseCrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                final Dialog dialogChooseSound = new Dialog(generator.view.context);
                dialogChooseSound.requestWindowFeature(Window.FEATURE_NO_TITLE);
                View chooseView = LayoutInflater.from(generator.view.context).inflate(R.layout.dialog_stages, null);
                ListView lv = (ListView) chooseView.findViewById(R.id.lv);
                lv.setAdapter(new AdapterSounds(generator.view.context, generator.view.context.getActivityLoader().listRaw()));
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        generator.getConfig().sounds.soundCrash = adapterView.getItemAtPosition(i).toString();
                        btnSoundCrash.setText(generator.getConfig().sounds.soundCrash);
                        generator.refreashMaze();

                        dialogChooseSound.dismiss();
                    }
                });
                dialogChooseSound.setContentView(chooseView);
                dialogChooseSound.show();

            }
        });

        baseCrashTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                final Dialog dialogChooseSound = new Dialog(generator.view.context);
                dialogChooseSound.requestWindowFeature(Window.FEATURE_NO_TITLE);
                View chooseView = LayoutInflater.from(generator.view.context).inflate(R.layout.dialog_stages, null);
                ListView lv = (ListView) chooseView.findViewById(R.id.lv);
                lv.setAdapter(new AdapterSounds(generator.view.context, generator.view.context.getActivityLoader().listRaw()));
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        generator.getConfig().sounds.soundCrashTwo = adapterView.getItemAtPosition(i).toString();
                        btnSoundCrashTwo.setText(generator.getConfig().sounds.soundCrashTwo);
                        generator.refreashMaze();

                        dialogChooseSound.dismiss();
                    }
                });
                dialogChooseSound.setContentView(chooseView);
                dialogChooseSound.show();

            }
        });


        baseFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                final Dialog dialogChooseSound = new Dialog(generator.view.context);
                dialogChooseSound.requestWindowFeature(Window.FEATURE_NO_TITLE);
                View chooseView = LayoutInflater.from(generator.view.context).inflate(R.layout.dialog_stages, null);
                ListView lv = (ListView) chooseView.findViewById(R.id.lv);
                lv.setAdapter(new AdapterSounds(generator.view.context, generator.view.context.getActivityLoader().listRaw()));
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        generator.getConfig().sounds.soundFinish = adapterView.getItemAtPosition(i).toString();
                        btnSoundFinish.setText(generator.getConfig().sounds.soundFinish);
                        generator.refreashMaze();

                        dialogChooseSound.dismiss();
                    }
                });
                dialogChooseSound.setContentView(chooseView);
                dialogChooseSound.show();

            }
        });


    }

}
