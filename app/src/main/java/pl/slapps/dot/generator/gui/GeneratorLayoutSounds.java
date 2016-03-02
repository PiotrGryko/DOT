package pl.slapps.dot.generator.gui;

import android.app.Dialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
        //else
        //    btnSoundCrash.setText(SoundsManager.DEFAULT_CRASH);
        if (!generator.getConfig().sounds.soundFinish.trim().equals(""))
            btnSoundFinish.setText(generator.getConfig().sounds.soundFinish);
        //else
        //    btnSoundFinish.setText(SoundsManager.DEFAULT_FINISH);

    }

    public void initLayout(GeneratorLayout generatorLayout) {
        this.generatorLayout = generatorLayout;
        this.generator = generatorLayout.generator;

        layoutSounds = LayoutInflater.from(this.generator.view.context).inflate(R.layout.layout_generator_sounds, null);

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
                if (generator.getConfig().sounds.soundBackground.trim().equals("")) {
                    Toast.makeText(generator.view.context, "Select file first", Toast.LENGTH_LONG).show();
                    return;
                }

                generator.view.context.getSoundsManager().playRawFile(generator.getConfig().sounds.soundBackground);
            }
        });
        btnPlayPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vie) {
                if (generator.getConfig().sounds.soundPress.trim().equals("")) {
                    Toast.makeText(generator.view.context, "Select file first", Toast.LENGTH_LONG).show();
                    return;
                }
                generator.view.context.getSoundsManager().playRawFile(generator.getConfig().sounds.soundPress);

            }
        });
        btnPlayCrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vie) {
                if (generator.getConfig().sounds.soundCrash.trim().equals("")) {
                    Toast.makeText(generator.view.context, "Select file first", Toast.LENGTH_LONG).show();
                    return;
                }
                generator.view.context.getSoundsManager().playRawFile(generator.getConfig().sounds.soundCrash);

            }
        });
        btnPlayFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vie) {
                if (generator.getConfig().sounds.soundFinish.trim().equals("")) {
                    Toast.makeText(generator.view.context, "Select file first", Toast.LENGTH_LONG).show();
                    return;
                }
                generator.view.context.getSoundsManager().playRawFile(generator.getConfig().sounds.soundFinish);

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
                        generator.getConfig().sounds.soundBackground = adapterView.getItemAtPosition(i).toString();
                        btnSoundBackground.setText(generator.getConfig().sounds.soundBackground);
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
                        generator.getConfig().sounds.soundPress = adapterView.getItemAtPosition(i).toString();
                        btnSoundPress.setText(generator.getConfig().sounds.soundPress);

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
                        generator.getConfig().sounds.soundCrash = adapterView.getItemAtPosition(i).toString();
                        btnSoundCrash.setText(generator.getConfig().sounds.soundCrash);

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
                        generator.getConfig().sounds.soundFinish = adapterView.getItemAtPosition(i).toString();
                        btnSoundFinish.setText(generator.getConfig().sounds.soundFinish);

                        dialogChooseSound.dismiss();
                    }
                });
                dialogChooseSound.setContentView(chooseView);
                dialogChooseSound.show();

            }
        });


    }

}
