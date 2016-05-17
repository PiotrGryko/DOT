package pl.slapps.dot.generator.gui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import java.util.Random;

import pl.slapps.dot.DAO;
import pl.slapps.dot.R;
import pl.slapps.dot.generator.Generator;
import pl.slapps.dot.generator.auto.AutoGenerator;
import pl.slapps.dot.generator.widget.NumberPickerTextView;

/**
 * Created by piotr on 08/04/16.
 */
public class GeneratorLayoutRandom {

    private String TAG = GeneratorLayout.class.getName();
    private View layoutConfig;
    private Generator generator;
    private GeneratorLayout generatorLayout;
    private AutoGenerator autoGenerator;

    public View getLayout() {
        return layoutConfig;
    }

    public void initLayout(GeneratorLayout generatorLayout) {
        this.generatorLayout = generatorLayout;
        this.generator = generatorLayout.generator;
        autoGenerator = new AutoGenerator(generator);


        layoutConfig = LayoutInflater.from(generator.view.context).inflate(R.layout.layout_generator_random, null);
        final NumberPickerTextView stagesCount = (NumberPickerTextView)layoutConfig.findViewById(R.id.count_stages);
        final NumberPickerTextView widhtCount = (NumberPickerTextView)layoutConfig.findViewById(R.id.count_width);
        final NumberPickerTextView heightCount = (NumberPickerTextView)layoutConfig.findViewById(R.id.count_height);

        stagesCount.setInteger(true);
        widhtCount.setInteger(true);
        heightCount.setInteger(true);
        widhtCount.setmMinValue(2);
        heightCount.setmMinValue(2);
        stagesCount.setmMinValue(1);

        widhtCount.putValue(2);
        heightCount.putValue(2);
        stagesCount.putValue(1);

        final CheckBox cbWidhtRandom = (CheckBox)layoutConfig.findViewById(R.id.cb_width_random);
        final CheckBox cbHeightRandom = (CheckBox)layoutConfig.findViewById(R.id.cb_height_random);


        final RadioButton cb50 = (RadioButton)layoutConfig.findViewById(R.id.cb_50);
        final RadioButton cb70 = (RadioButton)layoutConfig.findViewById(R.id.cb_70);
        final RadioButton cb85 = (RadioButton)layoutConfig.findViewById(R.id.cb_85);
        final RadioButton cb100 = (RadioButton)layoutConfig.findViewById(R.id.cb_100);

        final SeekBar speed = (SeekBar)layoutConfig.findViewById(R.id.sb_speed);
        speed.setProgress((int)(100*generator.getConfig().settings.speedRatio));

        speed.setMax(200);
        speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                generator.getConfig().settings.speedRatio=((float)progress)/100.0f;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        TextView tvGenerateRandom = (TextView) layoutConfig.findViewById(R.id.tv_generate_random);
        TextView tvGenerateWorldStages = (TextView) layoutConfig.findViewById(R.id.tv_generate_world_stages);


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int id = v.getId();

                switch (id) {


                    case R.id.tv_generate_random: {

                        autoGenerator.generateRandomStage();
                        break;
                    }

                    case R.id.tv_generate_world_stages: {

                        int stages = (int)stagesCount.getValue();
                        int width = (int)widhtCount.getValue();
                        int height =(int)heightCount.getValue();
                        if(cbWidhtRandom.isChecked())
                            width=1+new Random().nextInt(30);
                        if(cbHeightRandom.isChecked())
                            height=1+new Random().nextInt(30);

                        float percent = 0.8f;
                        if(cb50.isChecked())
                            percent = 0.5f;
                        else if(cb70.isChecked())
                            percent=0.7f;
                        else if(cb85.isChecked())
                            percent= 0.8f;
                        else if(cb100.isChecked())
                            percent=1.0f;


                        autoGenerator.generateStagesSet(stages,width,height,percent);
                        break;
                    }


                }


            }
        };

        tvGenerateRandom.setOnClickListener(listener);
        tvGenerateWorldStages.setOnClickListener(listener);


    }

}
