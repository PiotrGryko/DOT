package pl.slapps.dot.generator.gui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import pl.slapps.dot.R;
import pl.slapps.dot.generator.Generator;
import pl.slapps.dot.generator.widget.NumberPickerTextView;
import yuku.ambilwarna.AmbilWarnaDialog;

/**
 * Created by piotr on 21/02/16.
 */
public class GeneratorLayoutEffects {

    private View layoutEffects;
    private GeneratorLayout generatorLayout;
    private Generator generator;

    /*
 colors layout elements
  */
    private View colorBackgroundStart;
    private View colorBackgroundEnd;
    private CheckBox cbSwitchBackgroundColors;


    private View colorRouteStart;
    private View colorRouteEnd;
    private CheckBox cbSwitchRouteColors;


    private View colorDotStart;
    private View colorDotEnd;
    private CheckBox cbSwitchDotColors;

    private NumberPickerTextView dotLightStart;
    private NumberPickerTextView dotLightEnd;
    private CheckBox cbSwitchDotLightsDistance;


    public View getLayout() {
        return layoutEffects;
    }

    public void refreashLayout() {
        colorBackgroundStart.setBackgroundColor(Color.parseColor(generator.getConfig().colors.colorSwitchBackgroundStart));
        colorBackgroundEnd.setBackgroundColor(Color.parseColor(generator.getConfig().colors.colorSwitchBackgroundEnd));

        colorRouteStart.setBackgroundColor(Color.parseColor(generator.getConfig().colors.colorSwitchRouteStart));
        colorRouteEnd.setBackgroundColor(Color.parseColor(generator.getConfig().colors.colorSwitchRouteEnd));

        colorDotStart.setBackgroundColor(Color.parseColor(generator.getConfig().colors.colorSwitchDotStart));
        colorDotEnd.setBackgroundColor(Color.parseColor(generator.getConfig().colors.colorSwitchDotEnd));


        dotLightStart.putValue(generator.getConfig().settings.dotLightDistanceStart);
        dotLightEnd.putValue(generator.getConfig().settings.dotLightDistanceEnd);



        cbSwitchRouteColors.setChecked(generator.getConfig().settings.switchRouteColors);
        cbSwitchBackgroundColors.setChecked(generator.getConfig().settings.switchBackgroundColors);
        cbSwitchDotColors.setChecked(generator.getConfig().settings.switchDotColor);

    }

    public void initLayout(final GeneratorLayout generatorLayout) {

        this.generatorLayout = generatorLayout;
        this.generator = generatorLayout.generator;
        layoutEffects = LayoutInflater.from(generator.view.context).inflate(R.layout.layout_generator_effects, null);

        cbSwitchDotColors = (CheckBox) layoutEffects.findViewById(R.id.cb_switch_dot_color);

        cbSwitchBackgroundColors = (CheckBox) layoutEffects.findViewById(R.id.cb_switch_background_color);
        cbSwitchRouteColors = (CheckBox) layoutEffects.findViewById(R.id.cb_switch_route_color);
        cbSwitchDotLightsDistance = (CheckBox) layoutEffects.findViewById(R.id.cb_switch_dot_light);


        cbSwitchBackgroundColors.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                generator.getConfig().settings.switchBackgroundColors = b;
                generator.refreashMaze();

            }
        });

        cbSwitchRouteColors.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                generator.getConfig().settings.switchRouteColors = b;
                generator.refreashMaze();

            }
        });

        cbSwitchDotLightsDistance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                generator.getConfig().settings.switchDotLightDistance = b;
                generator.refreashMaze();

            }
        });

        cbSwitchDotColors.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                generator.getConfig().settings.switchDotColor = b;
                generator.refreashMaze();

            }
        });


        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        colorBackgroundStart = layoutEffects.findViewById(R.id.color_background_start);
        colorBackgroundEnd = layoutEffects.findViewById(R.id.color_background_end);

        final LinearLayout tvSwitchBackgroundStart = (LinearLayout) layoutEffects.findViewById(R.id.tv_background_color_start);
        final LinearLayout tvSwitchBackgroundEnd = (LinearLayout) layoutEffects.findViewById(R.id.tv_background_color_end);


        tvSwitchBackgroundStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AmbilWarnaDialog dialog = new AmbilWarnaDialog(generator.view.context, Color.parseColor(generator.getConfig().colors.colorSwitchBackgroundStart), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        // color is the color selected by the user.


                        colorBackgroundStart.setBackgroundColor(color);
                        generator.getConfig().colors.colorSwitchBackgroundStart = "#" + Integer.toHexString(color);


                        if (generator.getConfig().settings.switchBackgroundColors)
                            generator.refreashMaze();
                        dialog.getDialog().dismiss();
                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // cancel was selected by the user
                    }
                });


                dialog.show();
            }
        });
        tvSwitchBackgroundEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(generator.view.context, Color.parseColor(generator.getConfig().colors.colorSwitchBackgroundEnd), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        // color is the color selected by the user.

                        colorBackgroundEnd.setBackgroundColor(color);
                        generator.getConfig().colors.colorSwitchBackgroundEnd = "#" + Integer.toHexString(color);

                        if (generator.getConfig().settings.switchBackgroundColors)
                            generator.refreashMaze();

                        dialog.getDialog().dismiss();
                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // cancel was selected by the user
                    }
                });

                dialog.show();
            }
        });


////////////////////////////////////////////////////////////////////////////////////////////////////////


        colorRouteStart = layoutEffects.findViewById(R.id.color_route_start);
        colorRouteEnd = layoutEffects.findViewById(R.id.color_route_end);

        final LinearLayout tvSwitchRoutedStart = (LinearLayout) layoutEffects.findViewById(R.id.tv_route_color_start);
        final LinearLayout tvSwitchRouteEnd = (LinearLayout) layoutEffects.findViewById(R.id.tv_route_color_end);



        tvSwitchRoutedStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AmbilWarnaDialog dialog = new AmbilWarnaDialog(generator.view.context, Color.parseColor(generator.getConfig().colors.colorSwitchRouteStart), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        // color is the color selected by the user.


                        colorRouteStart.setBackgroundColor(color);
                        generator.getConfig().colors.colorSwitchRouteStart = "#" + Integer.toHexString(color);


                        if (generator.getConfig().settings.switchRouteColors)
                            generator.refreashMaze();
                        dialog.getDialog().dismiss();
                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // cancel was selected by the user
                    }
                });


                dialog.show();
            }
        });
        tvSwitchRouteEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(generator.view.context, Color.parseColor(generator.getConfig().colors.colorSwitchRouteEnd), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        // color is the color selected by the user.

                        colorRouteEnd.setBackgroundColor(color);
                        generator.getConfig().colors.colorSwitchRouteEnd = "#" + Integer.toHexString(color);

                        if (generator.getConfig().settings.switchRouteColors)
                            generator.refreashMaze();

                        dialog.getDialog().dismiss();
                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // cancel was selected by the user
                    }
                });

                dialog.show();
            }
        });

        ///////////////////////////////////////////////////////////////////


        colorDotStart = layoutEffects.findViewById(R.id.color_dot_start);
        colorDotEnd = layoutEffects.findViewById(R.id.color_dot_end);

        final LinearLayout tvSwitchDotStart = (LinearLayout) layoutEffects.findViewById(R.id.tv_dot_color_start);
        final LinearLayout tvSwitchDotEnd = (LinearLayout) layoutEffects.findViewById(R.id.tv_dot_color_end);



        tvSwitchDotStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AmbilWarnaDialog dialog = new AmbilWarnaDialog(generator.view.context, Color.parseColor(generator.getConfig().colors.colorSwitchDotStart), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        // color is the color selected by the user.


                        colorDotStart.setBackgroundColor(color);
                        generator.getConfig().colors.colorSwitchDotStart = "#" + Integer.toHexString(color);


                        if (generator.getConfig().settings.switchDotColor)
                            generator.refreashMaze();
                        dialog.getDialog().dismiss();
                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // cancel was selected by the user
                    }
                });


                dialog.show();
            }
        });
        tvSwitchDotEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(generator.view.context, Color.parseColor(generator.getConfig().colors.colorSwitchDotEnd), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        // color is the color selected by the user.

                        colorDotEnd.setBackgroundColor(color);
                        generator.getConfig().colors.colorSwitchDotEnd = "#" + Integer.toHexString(color);

                        if (generator.getConfig().settings.switchDotColor)
                            generator.refreashMaze();

                        dialog.getDialog().dismiss();
                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // cancel was selected by the user
                    }
                });

                dialog.show();
            }
        });

        ///////////////////////////////////////////////////////////////////


        dotLightStart = (NumberPickerTextView)layoutEffects.findViewById(R.id.dot_light_distance_start);
        dotLightEnd = (NumberPickerTextView)layoutEffects.findViewById(R.id.dot_light_distance_end);

        dotLightStart.setListener(new NumberPickerTextView.OnLabelValueChanged() {
            @Override
            public void onValueChanged(float value) {
                generator.getConfig().settings.dotLightDistanceStart=value;
                if(generator.getConfig().settings.switchDotLightDistance)
                    generator.refreashMaze();
            }
        });
        dotLightEnd.setListener(new NumberPickerTextView.OnLabelValueChanged() {
            @Override
            public void onValueChanged(float value) {
                generator.getConfig().settings.dotLightDistanceEnd = value;
                if (generator.getConfig().settings.switchDotLightDistance)
                    generator.refreashMaze();
            }
        });



        refreashLayout();
    }


}
