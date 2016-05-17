package pl.slapps.dot.generator.gui;

import android.app.Dialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import pl.slapps.dot.MainActivity;
import pl.slapps.dot.R;
import pl.slapps.dot.SoundsService;
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

        Log.d("aaa", generator.getConfig().sounds.toJson().toString());
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



        btnPlayBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vie) {
                if (generator.getConfig().sounds.soundBackground.trim().equals("")) {
                    Toast.makeText(generator.view.context, "Select file first", Toast.LENGTH_LONG).show();
                    return;
                }
                MainActivity.sendAction(SoundsService.ACTION_BACKGROUND, null);

                //  btnPlayBackground.play(SoundsService.getSoundsManager().parseSound(generator.getConfig().sounds.soundBackground));
            }
        });
        btnPlayPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vie) {
                if (generator.getConfig().sounds.soundPress.trim().equals("")) {
                    Toast.makeText(generator.view.context, "Select file first", Toast.LENGTH_LONG).show();
                    return;
                }
                MainActivity.sendAction(SoundsService.ACTION_PRESS, null);

                //     btnPlayPress.play(SoundsService.getSoundsManager().parseSound(generator.getConfig().sounds.soundPress));


            }
        });
        btnPlayCrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vie) {
                if (generator.getConfig().sounds.soundCrash.trim().equals("")) {
                    Toast.makeText(generator.view.context, "Select file first", Toast.LENGTH_LONG).show();
                    return;
                }
                MainActivity.sendAction(SoundsService.ACTION_CRASH, null);

                //       btnPlayCrash.play(SoundsService.getSoundsManager().parseSound(generator.getConfig().sounds.soundCrash));


            }
        });
        btnPlayCrashTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vie) {
                if (generator.getConfig().sounds.soundCrashTwo.trim().equals("")) {
                    Toast.makeText(generator.view.context, "Select file first", Toast.LENGTH_LONG).show();
                    return;
                }
                MainActivity.sendAction(SoundsService.ACTION_CRASH, null);

                //    btnPlayCrashTwo.play(SoundsService.getSoundsManager().parseSound(generator.getConfig().sounds.soundCrashTwo));


            }
        });
        btnPlayFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vie) {
                if (generator.getConfig().sounds.soundFinish.trim().equals("")) {
                    Toast.makeText(generator.view.context, "Select file first", Toast.LENGTH_LONG).show();
                    return;
                }
                MainActivity.sendAction(SoundsService.ACTION_FINISH, null);

                //     btnPlayFinish.play(SoundsService.getSoundsManager().parseSound(generator.getConfig().sounds.soundFinish));


            }
        });


        baseBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                final Dialog dialogChooseSound = new Dialog(generator.view.context);
                dialogChooseSound.requestWindowFeature(Window.FEATURE_NO_TITLE);
                final View chooseView = LayoutInflater.from(generator.view.context).inflate(R.layout.dialog_stages, null);
                ListView lv = (ListView) chooseView.findViewById(R.id.lv);
                lv.setAdapter(new AdapterSounds(generator.view.context, generator.view.context.getActivityLoader().listSoundsFromAssets(null)
                        , new AdapterSounds.OnSoundClickListener() {
                    @Override
                    public void onClick(String sound) {
                        generator.getConfig().sounds.soundBackground = sound;
                        btnSoundBackground.setText(generator.getConfig().sounds.soundBackground);
                        generator.refreashMaze();

                        MainActivity.sendAction(SoundsService.ACTION_CONFIG, generator.getConfig().sounds);

                        //      SoundsService.getSoundsManager().playBackgroundSound();
                        MainActivity.sendAction(SoundsService.ACTION_BACKGROUND, null);
                        dialogChooseSound.dismiss();
                    }
                }));


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
                lv.setAdapter(new AdapterSounds(generator.view.context, generator.view.context.getActivityLoader().listSoundsFromAssets(null),
                        new AdapterSounds.OnSoundClickListener() {
                            @Override
                            public void onClick(String sound) {
                                generator.getConfig().sounds.soundPress = sound;
                                btnSoundPress.setText(generator.getConfig().sounds.soundPress);
                                generator.refreashMaze();
                                MainActivity.sendAction(SoundsService.ACTION_CONFIG, generator.getConfig().sounds);

                                dialogChooseSound.dismiss();
                            }
                        }));

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
                lv.setAdapter(new AdapterSounds(generator.view.context, generator.view.context.getActivityLoader().listSoundsFromAssets(null), new AdapterSounds.OnSoundClickListener() {
                    @Override
                    public void onClick(String sound) {
                        generator.getConfig().sounds.soundCrash = sound;
                        btnSoundCrash.setText(generator.getConfig().sounds.soundCrash);
                        generator.refreashMaze();
                        MainActivity.sendAction(SoundsService.ACTION_CONFIG, generator.getConfig().sounds);

                        dialogChooseSound.dismiss();
                    }
                }));

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
                lv.setAdapter(new AdapterSounds(generator.view.context, generator.view.context.getActivityLoader().listSoundsFromAssets(null), new AdapterSounds.OnSoundClickListener() {
                    @Override
                    public void onClick(String sound) {
                        generator.getConfig().sounds.soundCrashTwo = sound;
                        btnSoundCrashTwo.setText(generator.getConfig().sounds.soundCrashTwo);
                        generator.refreashMaze();
                        MainActivity.sendAction(SoundsService.ACTION_CONFIG, generator.getConfig().sounds);

                        dialogChooseSound.dismiss();
                    }
                }));

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
                lv.setAdapter(new AdapterSounds(generator.view.context, generator.view.context.getActivityLoader().listSoundsFromAssets(null), new AdapterSounds.OnSoundClickListener() {
                    @Override
                    public void onClick(String sound) {
                        generator.getConfig().sounds.soundFinish = sound;
                        btnSoundFinish.setText(generator.getConfig().sounds.soundFinish);
                        generator.refreashMaze();
                        MainActivity.sendAction(SoundsService.ACTION_CONFIG, generator.getConfig().sounds);

                        dialogChooseSound.dismiss();
                    }
                }));

                dialogChooseSound.setContentView(chooseView);
                dialogChooseSound.show();

            }
        });


    }

}
