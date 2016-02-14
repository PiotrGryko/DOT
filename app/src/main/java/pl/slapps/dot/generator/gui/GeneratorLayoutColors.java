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
    private View colorFence;

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
        colorBackground.setBackgroundColor(Color.parseColor(generator.getConfig().colors.colorBackground));
        colorRoute.setBackgroundColor(Color.parseColor(generator.getConfig().colors.colorRoute));
        colorDot.setBackgroundColor(Color.parseColor(generator.getConfig().colors.colorShip));
        colorExplosionStart.setBackgroundColor(Color.parseColor(generator.getConfig().colors.colorExplosionStart));
        colorExplosionEnd.setBackgroundColor(Color.parseColor(generator.getConfig().colors.colorExplosionEnd));
        colorDotLight.setBackgroundColor(Color.parseColor(generator.getConfig().colors.colorShipLight));
        colorExplosionLight.setBackgroundColor(Color.parseColor(generator.getConfig().colors.colorExplosionLight));

    }

    public void initLayout(final GeneratorLayout generatorLayout) {

        this.generatorLayout = generatorLayout;
        this.generator = generatorLayout.generator;
        layoutColors = LayoutInflater.from(generator.view.context).inflate(R.layout.layout_generator_colors,null);

        final LinearLayout tvDotLightColor = (LinearLayout) layoutColors.findViewById(R.id.tv_dot_light_color);
        final LinearLayout tvExplosionLightColor = (LinearLayout) layoutColors.findViewById(R.id.tv_explosion_ligh_color);
        final LinearLayout tvFenceColor = (LinearLayout) layoutColors.findViewById(R.id.tv_fence_color);
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
        colorFence = layoutColors.findViewById(R.id.color_fence);


        refreashLayout();

        tvDotLightColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AmbilWarnaDialog dialog = new AmbilWarnaDialog(generator.view.context, Color.parseColor(generator.getConfig().colors.colorShip), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        // color is the color selected by the user.

                        colorDotLight.setBackgroundColor(color);
                        generator.getConfig().colors.colorShip = "#" + Integer.toHexString(color);


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
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(generator.view.context, Color.parseColor(generator.getConfig().colors.colorExplosionLight), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        // color is the color selected by the user.

                        colorDotLight.setBackgroundColor(color);
                        generator.getConfig().colors.colorExplosionLight = "#" + Integer.toHexString(color);

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
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(generator.view.context, Color.parseColor(generator.getConfig().colors.colorBackground), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        // color is the color selected by the user.

                        colorBackground.setBackgroundColor(color);
                        generator.getConfig().colors.colorBackground = "#" + Integer.toHexString(color);
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
        tvRouteColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(generator.view.context, Color.parseColor(generator.getConfig().colors.colorRoute), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        // color is the color selected by the user.

                        colorRoute.setBackgroundColor(color);
                        generator.getConfig().colors.colorRoute = "#" + Integer.toHexString(color);

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
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(generator.view.context, Color.parseColor(generator.getConfig().colors.colorShip), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        // color is the color selected by the user.

                        colorDot.setBackgroundColor(color);
                        generator.getConfig().colors.colorShip = "#" + Integer.toHexString(color);


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
                        generator.getConfig().colors.colorExplosionStart = "#" + Integer.toHexString(color);


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

                AmbilWarnaDialog dialog = new AmbilWarnaDialog(generator.view.context, Color.parseColor(generator.getConfig().colors.colorExplosionEnd), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        // color is the color selected by the user.

                        colorExplosionEnd.setBackgroundColor(color);
                        generator.getConfig().colors.colorExplosionEnd = "#" + Integer.toHexString(color);


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

        tvFenceColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AmbilWarnaDialog dialog = new AmbilWarnaDialog(generator.view.context, Color.parseColor(generator.getConfig().colors.colorFence), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        // color is the color selected by the user.

                        colorFence.setBackgroundColor(color);
                        generator.getConfig().colors.colorFence = "#" + Integer.toHexString(color);
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
