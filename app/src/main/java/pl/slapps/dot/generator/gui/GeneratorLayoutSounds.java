package pl.slapps.dot.generator.gui;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import pl.slapps.dot.MainActivity;
import pl.slapps.dot.R;
import pl.slapps.dot.SoundsManager;
import pl.slapps.dot.generator.Generator;

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
    private TextView btnSoundFinish;
    private ImageView btnPlayBackground;
    private ImageView btnPlayPress;
    private ImageView btnPlayCrash;
    private ImageView btnPlayFinish;

    public View getLayout()
    {
        return layoutSounds;
    }



    public void refreashLayout() {
        if (!generator.backgrounSound.trim().equals(""))
            btnSoundBackground.setText(generator.backgrounSound);

        if (!generator.pressSound.trim().equals(""))
            btnSoundPress.setText(generator.pressSound);
        else
            btnSoundPress.setText(SoundsManager.DEFAULT_PRESS);
        if (!generator.crashSound.trim().equals(""))
            btnSoundCrash.setText(generator.crashSound);
        else
            btnSoundCrash.setText(SoundsManager.DEFAULT_CRASH);
        if (!generator.finishSound.trim().equals(""))
            btnSoundFinish.setText(generator.finishSound);
        else
            btnSoundFinish.setText(SoundsManager.DEFAULT_FINISH);

    }

    public void initLayout(GeneratorLayout generatorLayout) {
        this.generatorLayout=generatorLayout;
        this.generator=generatorLayout.generator;

        layoutSounds = LayoutInflater.from(this.generator.view.context).inflate(R.layout.layout_generator_sounds,null);

        btnSoundBackground = (TextView) layoutSounds.findViewById(R.id.btn_choose_background);
        btnSoundPress = (TextView) layoutSounds.findViewById(R.id.btn_choose_press);
        btnSoundCrash = (TextView) layoutSounds.findViewById(R.id.btn_choose_crash);
        btnSoundFinish = (TextView) layoutSounds.findViewById(R.id.btn_choose_finish);

        btnPlayBackground = (ImageView) layoutSounds.findViewById(R.id.btn_play_background);
        btnPlayPress = (ImageView) layoutSounds.findViewById(R.id.btn_play_press);
        btnPlayCrash = (ImageView) layoutSounds.findViewById(R.id.btn_play_crash);
        btnPlayFinish = (ImageView) layoutSounds.findViewById(R.id.btn_play_finish);

        refreashLayout();

        btnPlayBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vie) {
                if (generator.backgrounSound.trim().equals("")) {
                    Toast.makeText(generator.view.context, "Select file first", Toast.LENGTH_LONG).show();
                    return;
                }

                generator.view.context.getSoundsManager().playRawFile(generator.backgrounSound);
            }
        });
        btnPlayPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vie) {
                if (generator.pressSound.trim().equals("")) {
                    Toast.makeText(generator.view.context, "Select file first", Toast.LENGTH_LONG).show();
                    return;
                }
                generator.view.context.getSoundsManager().playRawFile(generator.pressSound);

            }
        });
        btnPlayCrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vie) {
                if (generator.crashSound.trim().equals("")) {
                    Toast.makeText(generator.view.context, "Select file first", Toast.LENGTH_LONG).show();
                    return;
                }
                generator.view.context.getSoundsManager().playRawFile(generator.crashSound);

            }
        });
        btnPlayFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vie) {
                if (generator.finishSound.trim().equals("")) {
                    Toast.makeText(generator.view.context, "Select file first", Toast.LENGTH_LONG).show();
                    return;
                }
                generator.view.context.getSoundsManager().playRawFile(generator.finishSound);

            }
        });


        btnSoundBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                final Dialog dialogChooseSound = new Dialog(generator.view.context);
                dialogChooseSound.requestWindowFeature(Window.FEATURE_NO_TITLE);
                final View chooseView = LayoutInflater.from(generator.view.context).inflate(R.layout.dialog_stages, null);
                ListView lv = (ListView) chooseView.findViewById(R.id.lv);
                lv.setAdapter(new ArrayAdapter<String>(generator.view.context, android.R.layout.simple_list_item_1, MainActivity.listRaw()));

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        generator.backgrounSound = adapterView.getItemAtPosition(i).toString();
                        btnSoundBackground.setText(generator.backgrounSound);
                        dialogChooseSound.dismiss();
                    }
                });
                dialogChooseSound.setContentView(chooseView);
                dialogChooseSound.show();

            }
        });

        btnSoundPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                final Dialog dialogChooseSound = new Dialog(generator.view.context);
                dialogChooseSound.requestWindowFeature(Window.FEATURE_NO_TITLE);
                View chooseView = LayoutInflater.from(generator.view.context).inflate(R.layout.dialog_stages, null);
                ListView lv = (ListView) chooseView.findViewById(R.id.lv);
                lv.setAdapter(new ArrayAdapter<String>(generator.view.context, android.R.layout.simple_list_item_1, MainActivity.listRaw()));
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        generator.pressSound = adapterView.getItemAtPosition(i).toString();
                        btnSoundPress.setText(generator.pressSound);

                        dialogChooseSound.dismiss();
                    }
                });
                dialogChooseSound.setContentView(chooseView);
                dialogChooseSound.show();

            }
        });

        btnSoundCrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                final Dialog dialogChooseSound = new Dialog(generator.view.context);
                dialogChooseSound.requestWindowFeature(Window.FEATURE_NO_TITLE);
                View chooseView = LayoutInflater.from(generator.view.context).inflate(R.layout.dialog_stages, null);
                ListView lv = (ListView) chooseView.findViewById(R.id.lv);
                lv.setAdapter(new ArrayAdapter<String>(generator.view.context, android.R.layout.simple_list_item_1, MainActivity.listRaw()));
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        generator.crashSound = adapterView.getItemAtPosition(i).toString();
                        btnSoundCrash.setText(generator.crashSound);

                        dialogChooseSound.dismiss();
                    }
                });
                dialogChooseSound.setContentView(chooseView);
                dialogChooseSound.show();

            }
        });

        btnSoundFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                final Dialog dialogChooseSound = new Dialog(generator.view.context);
                dialogChooseSound.requestWindowFeature(Window.FEATURE_NO_TITLE);
                View chooseView = LayoutInflater.from(generator.view.context).inflate(R.layout.dialog_stages, null);
                ListView lv = (ListView) chooseView.findViewById(R.id.lv);
                lv.setAdapter(new ArrayAdapter<String>(generator.view.context, android.R.layout.simple_list_item_1, MainActivity.listRaw()));
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        generator.finishSound = adapterView.getItemAtPosition(i).toString();
                        btnSoundFinish.setText(generator.finishSound);

                        dialogChooseSound.dismiss();
                    }
                });
                dialogChooseSound.setContentView(chooseView);
                dialogChooseSound.show();

            }
        });


    }

}
