package pl.slapps.dot.generator.gui;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import pl.slapps.dot.R;
import pl.slapps.dot.generator.Generator;
import pl.slapps.dot.generator.widget.NumberPickerTextView;

/**
 * Created by piotr on 14/02/16.
 */
public class GeneratorLayoutGrid {

    private String TAG = GeneratorLayoutWorlds.class.getName();
    private Generator generator;
    private GeneratorLayout generatorLayout;

    private View layoutGrid;

    /*
   gridsize layout elements
    */
    private NumberPickerTextView etWidth;
    private NumberPickerTextView etHeight;


    public View getLayout() {
        return layoutGrid;
    }


    public void refreashLayout() {
        etWidth.putValue(generator.gridX);
        etHeight.putValue(generator.gridY);
    }

    public void initLayout(final GeneratorLayout generatorLayout) {
        this.generatorLayout = generatorLayout;
        this.generator = generatorLayout.generator;
        layoutGrid = LayoutInflater.from(generator.view.context).inflate(R.layout.layout_generator_grid, null);

        etWidth = (NumberPickerTextView) layoutGrid.findViewById(R.id.et_width);
        etHeight = (NumberPickerTextView) layoutGrid.findViewById(R.id.et_height);

        etHeight.setInteger(true);
        etWidth.setInteger(true);


        etWidth.setListener(new NumberPickerTextView.OnLabelValueChanged() {
            @Override
            public void onValueChanged(float value) {
                generator.initGrid((int) value, (int) etHeight.getValue());
                refreashLayout();
            }
        });

        etHeight.setListener(new NumberPickerTextView.OnLabelValueChanged() {
            @Override
            public void onValueChanged(float value) {
                generator.initGrid((int) etWidth.getValue(), (int) value);
                refreashLayout();
            }
        });


    }


}
