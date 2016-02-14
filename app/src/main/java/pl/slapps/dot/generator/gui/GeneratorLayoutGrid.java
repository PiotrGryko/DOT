package pl.slapps.dot.generator.gui;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import pl.slapps.dot.R;
import pl.slapps.dot.generator.Generator;

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
    private EditText etWidth;
    private EditText etHeight;


    public View getLayout()
    {
        return layoutGrid;
    }


    public void refreashLayout() {
        etWidth.setText(Integer.toString(generator.gridX));
        etHeight.setText(Integer.toString(generator.gridY));
    }

    public void initLayout(final GeneratorLayout generatorLayout) {
        this.generatorLayout=generatorLayout;
        this.generator=generatorLayout.generator;
        layoutGrid = LayoutInflater.from(generator.view.context).inflate(R.layout.layout_generator_grid,null);
        Button btnSave = (Button) layoutGrid.findViewById(R.id.btn_save_grid);

        etWidth = (EditText) layoutGrid.findViewById(R.id.et_width);
        etHeight = (EditText) layoutGrid.findViewById(R.id.et_height);

        refreashLayout();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textWidht = etWidth.getText().toString();
                String textHeight = etHeight.getText().toString();

                if (textWidht.trim().equals("")) {
                    Toast.makeText(generator.view.context, "gridX is required", Toast.LENGTH_LONG).show();
                    return;
                }
                if (textHeight.trim().equals("")) {
                    Toast.makeText(generator.view.context, "gridY is required", Toast.LENGTH_LONG).show();
                    return;
                }

                generator.initGrid(Integer.parseInt(textWidht), Integer.parseInt(textHeight));
                refreashLayout();
            }
        });


    }


}
