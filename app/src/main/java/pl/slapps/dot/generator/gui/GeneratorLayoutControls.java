package pl.slapps.dot.generator.gui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

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
    private ImageView btnBackground;
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

    public ImageView getButtonBackground() {
        return btnBackground;
    }

    public View getLayout() {
        return layoutControls;
    }

    public void refreshLayout() {
        getButtonPlay().setVisibility(View.VISIBLE);

        /*

        if(generator.getStartRoute()==null)
        {
            getButtonPlay().setVisibility(View.GONE);

        }
        else {
            getButtonPlay().setVisibility(View.VISIBLE);
            getButtonPlay().setImageDrawable(generator.view.context.getResources().getDrawable(R.drawable.play));

        }
*/
    }

    public void initLayout(final GeneratorLayout generatorLayout) {
        this.generatorLayout = generatorLayout;
        this.generator = generatorLayout.generator;


        layoutControls = LayoutInflater.from(generator.view.context).inflate(R.layout.layout_generator_controls, null);

        btnSettings = (ImageView) layoutControls.findViewById(R.id.btn_settings);

        btnPlay = (ImageView) layoutControls.findViewById(R.id.btn_play);

        btnPalette = (ImageView) layoutControls.findViewById(R.id.btn_colours);

        btnLights = (ImageView) layoutControls.findViewById(R.id.btn_lights);

        btnSounds = (ImageView) layoutControls.findViewById(R.id.btn_sounds);

        btnGrid = (ImageView) layoutControls.findViewById(R.id.btn_grid);

        btnBackground = (ImageView) layoutControls.findViewById(R.id.btn_background);


        getButtonSettings().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GeneratorDialog.showGeneratorMenuDialog(Generator.this);

                generator.showMenu();


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
        getButtonPlay().setVisibility(View.GONE);


        getButtonColours().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!getButtonColours().isSelected()) {
                    getButtonColours().setSelected(true);
                    generator.getPathPopup().showColours();

                } else {
                    getButtonColours().setSelected(false);
                    generator.getPathPopup().dissmissColours();
                }


            }
        });

        getButtonLights().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!getButtonLights().isSelected()) {
                    getButtonLights().setSelected(true);
                    generator.getPathPopup().showLights();
                } else {
                    getButtonLights().setSelected(false);
                    generator.getPathPopup().dissmissLights();
                }

            }
        });


        getButtonGrid().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!getButtonGrid().isSelected()) {
                    getButtonGrid().setSelected(true);
                    generator.getPathPopup().showGrid();
                } else {
                    getButtonGrid().setSelected(false);
                    generator.getPathPopup().dissmissGrod();
                }

            }
        });

        getButtonSounds().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!getButtonSounds().isSelected()) {
                    getButtonSounds().setSelected(true);
                    generator.getPathPopup().showSounds();
                } else {
                    getButtonSounds().setSelected(false);
                    generator.getPathPopup().dissmissSounds();
                }
            }
        });

        final Dialog dialogChooseSound = new Dialog(generator.view.context);
        dialogChooseSound.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                getButtonBackground().setSelected(false);
            }
        });


        dialogChooseSound.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View chooseView = LayoutInflater.from(generator.view.context).inflate(R.layout.dialog_stages, null);
        ListView lv = (ListView) chooseView.findViewById(R.id.lv);
        final ArrayList<String> files = generator.view.context.getActivityLoader().listBackgroundsFromAssets();
        lv.setAdapter(new ArrayAdapter<String>(generator.view.context, android.R.layout.simple_expandable_list_item_1, files));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                generator.getConfig().settings.backgroundFile = files.get(i);
                generator.refreashMaze();
                dialogChooseSound.dismiss();
            }
        });
        dialogChooseSound.setContentView(chooseView);

        getButtonBackground().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!getButtonBackground().isSelected()) {
                    getButtonBackground().setSelected(true);

                    dialogChooseSound.show();
                } else {
                    getButtonBackground().setSelected(false);
                    if (dialogChooseSound.isShowing())
                        dialogChooseSound.dismiss();
                }


            }
        });

        refreshLayout();

    }

}
