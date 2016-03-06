package pl.slapps.dot.generator.gui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import pl.slapps.dot.DAO;
import pl.slapps.dot.R;
import pl.slapps.dot.generator.Generator;

/**
 * Created by piotr on 06/03/16.
 */
public class GeneratorLayoutControls {

    private String TAG = GeneratorLayout.class.getName();
    private View layoutControls;
    private Generator generator;
    private GeneratorLayout generatorLayout;


    private ImageView btnSettings;
    private ImageView btnPlay;
    private ImageView btnPalette;
    private ImageView btnLights;
    private ImageView btnGrid;

    private ImageView btnSounds;

    public View getButtonSettings() {
        return btnSettings;
    }


    public ImageView getButtonPlay() {
        return btnPlay;
    }

    public ImageView getButtonColours() {
        return btnPalette;
    }

    public ImageView getButtonSounds() {
        return btnSounds;
    }

    public ImageView getButtonLights() {
        return btnLights;
    }

    public ImageView getButtonGrid() {
        return btnGrid;
    }


    public View getLayout()
    {
        return layoutControls;
    }

    public void initLayout(GeneratorLayout generatorLayout) {
        this.generatorLayout=generatorLayout;
        this.generator=generatorLayout.generator;


        layoutControls = LayoutInflater.from(generator.view.context).inflate(R.layout.layout_generator_controls,null);

        btnSettings = (ImageView) layoutControls.findViewById(R.id.btn_settings);

        btnPlay = (ImageView) layoutControls.findViewById(R.id.btn_play);

        btnPalette = (ImageView) layoutControls.findViewById(R.id.btn_colours);

        btnLights = (ImageView) layoutControls.findViewById(R.id.btn_lights);

        btnSounds = (ImageView) layoutControls.findViewById(R.id.btn_sounds);

        btnGrid = (ImageView) layoutControls.findViewById(R.id.btn_grid);


        getButtonSettings().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GeneratorDialog.showGeneratorMenuDialog(Generator.this);
                generator.view.context.toggleMenu();
            }
        });



        getButtonPlay().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (generator.getPreview()) {
                    generator.stopPreview();
                    getButtonPlay().setImageDrawable(generator.view.context.getResources().getDrawable(R.drawable.play));
                } else {
                    generator.startPreview();
                    getButtonPlay().setImageDrawable(generator.view.context.getResources().getDrawable(R.drawable.stop));
                }
            }
        });


        getButtonColours().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                generator.getPathPopup().showColours();


            }
        });

        getButtonLights().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                generator.getPathPopup().showLights();


            }
        });


        getButtonGrid().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                generator.getPathPopup().showGrid();


            }
        });

        getButtonSounds().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                generator.getPathPopup().showSounds();


            }
        });



    }

}
