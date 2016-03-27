package pl.slapps.dot.generator.gui;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import pl.slapps.dot.R;
import pl.slapps.dot.generator.Generator;
import pl.slapps.dot.generator.widget.NumberPickerTextView;

/**
 * Created by piotr on 15/02/16.
 */
public class GeneratorLayoutLights {

    private String TAG = GeneratorLayoutLights.class.getName();
    private View layoutLights;
    private Generator generator;
    private GeneratorLayout generatorLayout;

    private NumberPickerTextView dotLightShinning;
    private NumberPickerTextView dotLightDistance;

    private NumberPickerTextView explosionOneLightShinning;
    private NumberPickerTextView explosionOneLightDistance;

    private NumberPickerTextView explosionTwoLightShinning;
    private NumberPickerTextView explosionTwoLightDistance;

    private SeekBar sbDotShinning;
    private SeekBar sbDotDistance;
    private SeekBar sbExplosionShinning;
    private SeekBar sbExplosionDistance;


    public View getLayout() {
        return layoutLights;
    }

    public void refreashLayout() {
        dotLightShinning.putValue(generator.getConfig().settings.dotLightShinning);
        dotLightDistance.putValue(generator.getConfig().settings.dotLightDistance);

        explosionOneLightDistance.putValue(generator.getConfig().settings.explosionOneLightDistance);
        explosionOneLightShinning.putValue(generator.getConfig().settings.explosionOneLightShinning);

        explosionTwoLightDistance.putValue(generator.getConfig().settings.explosionTwoLightDistance);
        explosionTwoLightShinning.putValue(generator.getConfig().settings.explosionTwoLightShinning);


        sbDotDistance.setProgress((int) (100 * generator.getConfig().settings.dotLightDistance));
        sbDotShinning.setProgress((int) (100 * generator.getConfig().settings.dotLightShinning));
        sbExplosionDistance.setProgress((int) (100 * generator.getConfig().settings.explosionOneLightDistance));
        sbExplosionShinning.setProgress((int) (100 * generator.getConfig().settings.explosionOneLightShinning));


    }

    public void initLayout(final GeneratorLayout generatorLayout) {
        this.generatorLayout = generatorLayout;
        this.generator = generatorLayout.generator;


        layoutLights = LayoutInflater.from(generator.view.context).inflate(R.layout.layout_generator_lights, null);


        dotLightShinning = (NumberPickerTextView) layoutLights.findViewById(R.id.et_dot_shinning);
        dotLightDistance = (NumberPickerTextView) layoutLights.findViewById(R.id.et_dot_distance);

        explosionOneLightShinning = (NumberPickerTextView) layoutLights.findViewById(R.id.et_explosion_one_shinning);
        explosionOneLightDistance = (NumberPickerTextView) layoutLights.findViewById(R.id.et_explosion_one_distance);

        explosionTwoLightShinning = (NumberPickerTextView) layoutLights.findViewById(R.id.et_explosion_two_shinning);
        explosionTwoLightDistance = (NumberPickerTextView) layoutLights.findViewById(R.id.et_explosion_two_distance);


        sbDotShinning = (SeekBar) layoutLights.findViewById(R.id.sb_dot_shining);
        sbDotDistance = (SeekBar) layoutLights.findViewById(R.id.sb_dot_distance);
        sbExplosionDistance = (SeekBar) layoutLights.findViewById(R.id.sb_explosion_start_distance);
        sbExplosionShinning = (SeekBar) layoutLights.findViewById(R.id.sb_explosion_start_shinning);

        sbDotShinning.setMax(250);
        sbDotDistance.setMax(250);
        sbExplosionShinning.setMax(250);
        sbExplosionDistance.setMax(250);


        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                float value = (float) i / 100.0f;

                if (seekBar == sbDotDistance) {
                    generator.getConfig().settings.dotLightDistance = value;
                    generator.refreashMaze();
                } else if (seekBar == sbDotShinning) {
                    generator.getConfig().settings.dotLightShinning = value;
                    generator.refreashMaze();
                } else if (seekBar == sbExplosionDistance) {
                    generator.getConfig().settings.explosionOneLightDistance = value;
                    generator.getConfig().settings.explosionTwoLightDistance = value;
                    generator.refreashMaze();
                } else if (seekBar == sbExplosionShinning) {
                    generator.getConfig().settings.explosionOneLightShinning = value;
                    generator.getConfig().settings.explosionTwoLightShinning = value;
                    generator.refreashMaze();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };

        sbDotShinning.setOnSeekBarChangeListener(listener);
        sbDotDistance.setOnSeekBarChangeListener(listener);
        sbExplosionDistance.setOnSeekBarChangeListener(listener);
        sbExplosionShinning.setOnSeekBarChangeListener(listener);


        dotLightShinning.setListener(new NumberPickerTextView.OnLabelValueChanged() {
            @Override
            public void onValueChanged(float value) {
                generator.getConfig().settings.dotLightShinning = value;
                generator.refreashMaze();


            }
        });
        dotLightDistance.setListener(new NumberPickerTextView.OnLabelValueChanged() {
            @Override
            public void onValueChanged(float value) {
                generator.getConfig().settings.dotLightDistance = value;
                generator.refreashMaze();


            }
        });
        explosionOneLightDistance.setListener(new NumberPickerTextView.OnLabelValueChanged() {
            @Override
            public void onValueChanged(float value) {
                generator.getConfig().settings.explosionOneLightDistance = value;
                generator.refreashMaze();


            }
        });

        explosionOneLightShinning.setListener(new NumberPickerTextView.OnLabelValueChanged() {
            @Override
            public void onValueChanged(float value) {
                generator.getConfig().settings.explosionOneLightShinning = value;
                generator.refreashMaze();


            }
        });

        explosionTwoLightDistance.setListener(new NumberPickerTextView.OnLabelValueChanged() {
            @Override
            public void onValueChanged(float value) {
                generator.getConfig().settings.explosionTwoLightDistance = value;
                generator.refreashMaze();


            }
        });

        explosionTwoLightShinning.setListener(new NumberPickerTextView.OnLabelValueChanged() {
            @Override
            public void onValueChanged(float value) {
                generator.getConfig().settings.explosionTwoLightShinning = value;
                generator.refreashMaze();


            }
        });


    }


}

