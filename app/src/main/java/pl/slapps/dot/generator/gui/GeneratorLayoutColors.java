package pl.slapps.dot.generator.gui;

import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import pl.slapps.dot.R;
import pl.slapps.dot.generator.Generator;
import yuku.ambilwarna.AmbilWarnaDialog;

/**
 * Created by piotr on 14/02/16.
 */
public class GeneratorLayoutColors {

    private View layoutColors;
    private GeneratorLayout generatorLayout;
    private Generator generator;

    /*
 colors layout elements
  */
    private View colorBackground;
    private View colorRoute;
    private View colorDot;
    private View colorExplosionStart;
    private View colorExplosionEnd;
    private View colorDotLight;
    private View colorExplosionLight;


    public View getLayout()
    {
        return layoutColors;
    }

    public void refreashLayout() {
        colorBackground.setBackgroundColor(Color.parseColor(generator.backgroundColor));
        colorRoute.setBackgroundColor(Color.parseColor(generator.routeColor));
        colorDot.setBackgroundColor(Color.parseColor(generator.dotColor));
        colorExplosionStart.setBackgroundColor(Color.parseColor(generator.explosionStartColor));
        colorExplosionEnd.setBackgroundColor(Color.parseColor(generator.explosionEndColor));
        colorDotLight.setBackgroundColor(Color.parseColor(generator.dotLightColor));
        colorExplosionLight.setBackgroundColor(Color.parseColor(generator.explosionLightColor));

    }

    public void initLayout(final GeneratorLayout generatorLayout) {

        this.generatorLayout = generatorLayout;
        this.generator = generatorLayout.generator;
        layoutColors = LayoutInflater.from(generator.view.context).inflate(R.layout.layout_generator_colors,null);

        final LinearLayout tvDotLightColor = (LinearLayout) layoutColors.findViewById(R.id.tv_dot_light_color);
        final LinearLayout tvExplosionLightColor = (LinearLayout) layoutColors.findViewById(R.id.tv_explosion_ligh_color);

        final LinearLayout tvBackgroundColor = (LinearLayout) layoutColors.findViewById(R.id.tv_background_color);
        final LinearLayout tvRouteColor = (LinearLayout) layoutColors.findViewById(R.id.tv_route_color);
        final LinearLayout tvDotColor = (LinearLayout) layoutColors.findViewById(R.id.tv_dot_color);
        final LinearLayout tvExplosionStartColor = (LinearLayout) layoutColors.findViewById(R.id.tv_explosion_start_color);
        final LinearLayout tvExplosionEndColor = (LinearLayout) layoutColors.findViewById(R.id.tv_explosion_end_color);

        colorDotLight = layoutColors.findViewById(R.id.color_dot_light);
        colorExplosionLight = layoutColors.findViewById(R.id.color_explosion_light);
        colorBackground = layoutColors.findViewById(R.id.color_background);
        colorRoute = layoutColors.findViewById(R.id.color_route);
        colorDot = layoutColors.findViewById(R.id.color_dot);
        colorExplosionStart = layoutColors.findViewById(R.id.color_explosion_start);
        colorExplosionEnd = layoutColors.findViewById(R.id.color_explosion_end);


        refreashLayout();

        tvDotLightColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(generator.view.context, Color.parseColor(generator.dotLightColor), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        // color is the color selected by the user.

                        colorDotLight.setBackgroundColor(color);
                        generator.dotLightColor = "#" + Integer.toHexString(color);


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
        tvExplosionLightColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(generator.view.context, Color.parseColor(generator.explosionLightColor), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        // color is the color selected by the user.

                        colorDotLight.setBackgroundColor(color);
                        generator.explosionLightColor = "#" + Integer.toHexString(color);

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

        tvBackgroundColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(generator.view.context, Color.parseColor(generator.backgroundColor), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        // color is the color selected by the user.

                        colorBackground.setBackgroundColor(color);
                        generator.backgroundColor = "#" + Integer.toHexString(color);


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
        tvRouteColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(generator.view.context, Color.parseColor(generator.routeColor), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        // color is the color selected by the user.

                        colorRoute.setBackgroundColor(color);
                        generator.routeColor = "#" + Integer.toHexString(color);

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
        tvDotColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(generator.view.context, Color.parseColor(generator.dotColor), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        // color is the color selected by the user.

                        colorDot.setBackgroundColor(color);
                        generator.dotColor = "#" + Integer.toHexString(color);


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
        tvExplosionStartColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(generator.view.context, Color.BLUE, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        // color is the color selected by the user.

                        colorExplosionStart.setBackgroundColor(color);
                        generator.explosionStartColor = "#" + Integer.toHexString(color);


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
        tvExplosionEndColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AmbilWarnaDialog dialog = new AmbilWarnaDialog(generator.view.context, Color.parseColor(generator.explosionEndColor), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        // color is the color selected by the user.

                        colorExplosionEnd.setBackgroundColor(color);
                        generator.explosionEndColor = "#" + Integer.toHexString(color);


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


    }

}
