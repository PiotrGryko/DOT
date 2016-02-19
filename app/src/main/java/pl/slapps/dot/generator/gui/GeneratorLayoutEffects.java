package pl.slapps.dot.generator.gui;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import pl.slapps.dot.R;
import pl.slapps.dot.generator.Generator;
import pl.slapps.dot.generator.widget.NumberPickerTextView;

/**
 * Created by piotr on 15/02/16.
 */
public class GeneratorLayoutEffects {

    private String TAG = GeneratorLayoutEffects.class.getName();
    private View layoutEffects;
    private Generator generator;
    private GeneratorLayout generatorLayout;

    private NumberPickerTextView dotLightShinning;
    private NumberPickerTextView dotLightDistance;

    private NumberPickerTextView explosionOneLightShinning;
    private NumberPickerTextView explosionOneLightDistance;

    private NumberPickerTextView explosionTwoLightShinning;
    private NumberPickerTextView explosionTwoLightDistance;


    public View getLayout() {
        return layoutEffects;
    }

    public void refreashLayout() {
        dotLightShinning.putValue(generator.getConfig().settings.dotLightShinning);
        dotLightDistance.putValue(generator.getConfig().settings.dotLightDistance);

        explosionOneLightDistance.putValue(generator.getConfig().settings.explosionOneLightDistance);
        explosionOneLightShinning.putValue(generator.getConfig().settings.explosionOneLightShinning);

        explosionTwoLightDistance.putValue(generator.getConfig().settings.explosionTwoLightDistance);
        explosionTwoLightShinning.putValue(generator.getConfig().settings.explosionTwoLightShinning);

    }

    public void initLayout(final GeneratorLayout generatorLayout) {
        this.generatorLayout = generatorLayout;
        this.generator = generatorLayout.generator;


        layoutEffects = LayoutInflater.from(generator.view.context).inflate(R.layout.layout_generator_effects, null);


        dotLightShinning = (NumberPickerTextView) layoutEffects.findViewById(R.id.et_dot_shinning);
        dotLightDistance = (NumberPickerTextView) layoutEffects.findViewById(R.id.et_dot_distance);

        explosionOneLightShinning = (NumberPickerTextView) layoutEffects.findViewById(R.id.et_explosion_one_shinning);
        explosionOneLightDistance = (NumberPickerTextView) layoutEffects.findViewById(R.id.et_explosion_one_distance);

        explosionTwoLightShinning = (NumberPickerTextView) layoutEffects.findViewById(R.id.et_explosion_two_shinning);
        explosionTwoLightDistance = (NumberPickerTextView) layoutEffects.findViewById(R.id.et_explosion_two_distance);


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

