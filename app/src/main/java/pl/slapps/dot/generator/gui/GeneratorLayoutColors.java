package pl.slapps.dot.generator.gui;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import pl.slapps.dot.R;
import pl.slapps.dot.generator.Generator;
import pl.slapps.dot.gui.AmbilWarnaView;

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
    private View colorFilter;

    private View colorCurrent;
    private TextView currentColorLabel;

    private LinearLayout currentColor;
    private LinearLayout colorusList;
    private AmbilWarnaView ambilWarnaView;


    private enum COLOR_TYPE{
        LIGHT_DOT,DOT,ROUTE,BACKGROUND,FENCE,EXPLOSION_START,EXPLOSION_END,COLOR_FILTER;
    }

    public View getLayout()
    {
        return layoutColors;
    }

    private void refreashCurrentColor(int color)
    {
        colorCurrent.setBackgroundColor(color);
    }
    private void setCurrentCurrentLabel(COLOR_TYPE type)
    {
        switch (type)
        {
            case LIGHT_DOT:
                currentColorLabel.setText("Dot light");
                colorCurrent.setBackgroundColor(Color.parseColor(generator.getConfig().colors.colorShipLight));
                ambilWarnaView.setColor(Color.parseColor(generator.getConfig().colors.colorShipLight));
                ambilWarnaView.setListener(new AmbilWarnaView.OnAmbilWarnaListener() {
                    @Override
                    public void onTouch(int color) {
                        colorDotLight.setBackgroundColor(color);
                        generator.getConfig().colors.colorShip = "#" + Integer.toHexString(color);


                        generator.refreashMaze();
                        refreashCurrentColor(color);
                    }
                });
                break;
            case BACKGROUND:
                currentColorLabel.setText("Background color");
                colorCurrent.setBackgroundColor(Color.parseColor(generator.getConfig().colors.colorBackground));
                ambilWarnaView.setColor(Color.parseColor(generator.getConfig().colors.colorBackground));
                ambilWarnaView.setListener(new AmbilWarnaView.OnAmbilWarnaListener() {
                    @Override
                    public void onTouch(int color) {
                        colorBackground.setBackgroundColor(color);
                        generator.getConfig().colors.colorBackground = "#" + Integer.toHexString(color);
                        generator.refreashMaze();
                        refreashCurrentColor(color);
                    }
                });
                break;
            case ROUTE:
                currentColorLabel.setText("Route color");
                colorCurrent.setBackgroundColor(Color.parseColor(generator.getConfig().colors.colorRoute));
                ambilWarnaView.setColor(Color.parseColor(generator.getConfig().colors.colorRoute));
                ambilWarnaView.setListener(new AmbilWarnaView.OnAmbilWarnaListener() {
                    @Override
                    public void onTouch(int color) {
                        colorRoute.setBackgroundColor(color);
                        generator.getConfig().colors.colorRoute = "#" + Integer.toHexString(color);

                        generator.refreashMaze();
                        refreashCurrentColor(color);
                    }
                });
                break;
            case DOT:
                currentColorLabel.setText("Dot color");
                colorCurrent.setBackgroundColor(Color.parseColor(generator.getConfig().colors.colorShip));
                ambilWarnaView.setColor(Color.parseColor(generator.getConfig().colors.colorShip));
                ambilWarnaView.setListener(new AmbilWarnaView.OnAmbilWarnaListener() {
                    @Override
                    public void onTouch(int color) {
                        colorDot.setBackgroundColor(color);
                        generator.getConfig().colors.colorShip = "#" + Integer.toHexString(color);


                        generator.refreashMaze();
                        refreashCurrentColor(color);
                    }
                });
                break;
            case FENCE:
                currentColorLabel.setText("Fence color");
                colorCurrent.setBackgroundColor(Color.parseColor(generator.getConfig().colors.colorFence));
                ambilWarnaView.setColor(Color.parseColor(generator.getConfig().colors.colorFence));
                ambilWarnaView.setListener(new AmbilWarnaView.OnAmbilWarnaListener() {
                    @Override
                    public void onTouch(int color) {
                        colorFence.setBackgroundColor(color);
                        generator.getConfig().colors.colorFence = "#" + Integer.toHexString(color);
                        generator.refreashMaze();
                        refreashCurrentColor(color);
                    }
                });
                break;
            case EXPLOSION_START:
                currentColorLabel.setText("Explosion start color");
                colorCurrent.setBackgroundColor(Color.parseColor(generator.getConfig().colors.colorExplosionStart));
                ambilWarnaView.setColor(Color.parseColor(generator.getConfig().colors.colorExplosionStart));
                ambilWarnaView.setListener(new AmbilWarnaView.OnAmbilWarnaListener() {
                    @Override
                    public void onTouch(int color) {
                        colorExplosionStart.setBackgroundColor(color);
                        generator.getConfig().colors.colorExplosionStart = "#" + Integer.toHexString(color);


                        generator.refreashMaze();
                        refreashCurrentColor(color);
                    }
                });
                break;
            case EXPLOSION_END:
                currentColorLabel.setText("Explosion end color");
                colorCurrent.setBackgroundColor(Color.parseColor(generator.getConfig().colors.colorExplosionEnd));
                ambilWarnaView.setColor(Color.parseColor(generator.getConfig().colors.colorExplosionEnd));
                ambilWarnaView.setListener(new AmbilWarnaView.OnAmbilWarnaListener() {
                    @Override
                    public void onTouch(int color) {
                        colorExplosionEnd.setBackgroundColor(color);
                        generator.getConfig().colors.colorExplosionEnd = "#" + Integer.toHexString(color);


                        generator.refreashMaze();
                        refreashCurrentColor(color);
                    }
                });
                break;
            case COLOR_FILTER:
                currentColorLabel.setText("color filter");
                colorCurrent.setBackgroundColor(Color.parseColor(generator.getConfig().colors.colorFilter));
                ambilWarnaView.setColor(Color.parseColor(generator.getConfig().colors.colorFilter));
                ambilWarnaView.setListener(new AmbilWarnaView.OnAmbilWarnaListener() {
                    @Override
                    public void onTouch(int color) {
                        colorFilter.setBackgroundColor(color);
                        generator.getConfig().colors.colorFilter = "#" + Integer.toHexString(color);


                        generator.refreashMaze();
                        refreashCurrentColor(color);
                    }
                });
                break;


        }

    }



    public void refreashLayout() {
        Log.e("EEE","background color check "+generator.getConfig().colors.colorBackground);
        colorBackground.setBackgroundColor(Color.parseColor(generator.getConfig().colors.colorBackground));
        colorRoute.setBackgroundColor(Color.parseColor(generator.getConfig().colors.colorRoute));
        colorDot.setBackgroundColor(Color.parseColor(generator.getConfig().colors.colorShip));
        colorExplosionStart.setBackgroundColor(Color.parseColor(generator.getConfig().colors.colorExplosionStart));
        colorExplosionEnd.setBackgroundColor(Color.parseColor(generator.getConfig().colors.colorExplosionEnd));
        colorDotLight.setBackgroundColor(Color.parseColor(generator.getConfig().colors.colorShipLight));
        colorExplosionLight.setBackgroundColor(Color.parseColor(generator.getConfig().colors.colorExplosionLight));
        colorFence.setBackgroundColor(Color.parseColor(generator.getConfig().colors.colorFence));
        colorFilter.setBackgroundColor(Color.parseColor(generator.getConfig().colors.colorFilter));
    }

    public void initLayout(final GeneratorLayout generatorLayout) {

        this.generatorLayout = generatorLayout;
        this.generator = generatorLayout.generator;
        layoutColors = LayoutInflater.from(generator.view.context).inflate(R.layout.layout_generator_colors,null);
        ambilWarnaView = (AmbilWarnaView)layoutColors.findViewById(R.id.ambilwarna);
        currentColor = (LinearLayout)layoutColors.findViewById(R.id.tv_current_color);
        colorusList =(LinearLayout)layoutColors.findViewById(R.id.layout_colors);
        currentColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(colorusList.getVisibility()==View.GONE)
                    colorusList.setVisibility(View.VISIBLE);
                else
                    colorusList.setVisibility(View.GONE);
            }
        });

        final LinearLayout tvDotLightColor = (LinearLayout) layoutColors.findViewById(R.id.tv_dot_light_color);
        final LinearLayout tvExplosionLightColor = (LinearLayout) layoutColors.findViewById(R.id.tv_explosion_ligh_color);
        final LinearLayout tvFenceColor = (LinearLayout) layoutColors.findViewById(R.id.tv_fence_color);
        final LinearLayout tvBackgroundColor = (LinearLayout) layoutColors.findViewById(R.id.tv_background_color);
        final LinearLayout tvRouteColor = (LinearLayout) layoutColors.findViewById(R.id.tv_route_color);
        final LinearLayout tvDotColor = (LinearLayout) layoutColors.findViewById(R.id.tv_dot_color);
        final LinearLayout tvExplosionStartColor = (LinearLayout) layoutColors.findViewById(R.id.tv_explosion_start_color);
        final LinearLayout tvExplosionEndColor = (LinearLayout) layoutColors.findViewById(R.id.tv_explosion_end_color);
        final LinearLayout tvFilterColor = (LinearLayout) layoutColors.findViewById(R.id.tv_filter_color);

        colorDotLight = layoutColors.findViewById(R.id.color_dot_light);
        colorExplosionLight = layoutColors.findViewById(R.id.color_explosion_light);
        colorBackground = layoutColors.findViewById(R.id.color_background);
        colorRoute = layoutColors.findViewById(R.id.color_route);
        colorDot = layoutColors.findViewById(R.id.color_dot);
        colorExplosionStart = layoutColors.findViewById(R.id.color_explosion_start);
        colorExplosionEnd = layoutColors.findViewById(R.id.color_explosion_end);
        colorFence = layoutColors.findViewById(R.id.color_fence);
        colorCurrent = layoutColors.findViewById(R.id.color_current);
        colorFilter = layoutColors.findViewById(R.id.color_filter);

        currentColorLabel = (TextView)layoutColors.findViewById(R.id.current_color_label);

        refreashLayout();

        setCurrentCurrentLabel(COLOR_TYPE.BACKGROUND);

        tvDotLightColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                setCurrentCurrentLabel(COLOR_TYPE.LIGHT_DOT);
                colorusList.setVisibility(View.GONE);

            }
        });
        tvExplosionLightColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ambilWarnaView.setListener(new AmbilWarnaView.OnAmbilWarnaListener() {
                    @Override
                    public void onTouch(int color) {
                        colorDotLight.setBackgroundColor(color);
                        generator.getConfig().colors.colorExplosionLight = "#" + Integer.toHexString(color);


                    }
                });
                setCurrentCurrentLabel(COLOR_TYPE.LIGHT_DOT);
                colorusList.setVisibility(View.GONE);
            }

        });

        tvBackgroundColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                colorusList.setVisibility(View.GONE);
                setCurrentCurrentLabel(COLOR_TYPE.BACKGROUND);
            }
        });
        tvRouteColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                setCurrentCurrentLabel(COLOR_TYPE.ROUTE);
                colorusList.setVisibility(View.GONE);

            }
        });
        tvDotColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                setCurrentCurrentLabel(COLOR_TYPE.DOT);

                colorusList.setVisibility(View.GONE);

            }
        });
        tvExplosionStartColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                setCurrentCurrentLabel(COLOR_TYPE.EXPLOSION_START);

                colorusList.setVisibility(View.GONE);

            }
        });
        tvExplosionEndColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                setCurrentCurrentLabel(COLOR_TYPE.EXPLOSION_END);

                colorusList.setVisibility(View.GONE);

            }
        });

        tvFenceColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                setCurrentCurrentLabel(COLOR_TYPE.FENCE);

                colorusList.setVisibility(View.GONE);

            }
        });

        tvFilterColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                setCurrentCurrentLabel(COLOR_TYPE.COLOR_FILTER);

                colorusList.setVisibility(View.GONE);

            }
        });


    }

}
