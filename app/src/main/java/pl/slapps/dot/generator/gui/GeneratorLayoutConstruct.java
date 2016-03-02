package pl.slapps.dot.generator.gui;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import pl.slapps.dot.MainActivity;
import pl.slapps.dot.R;
import pl.slapps.dot.generator.Generator;
import pl.slapps.dot.generator.TileRoute;
import pl.slapps.dot.generator.widget.NumberPickerTextView;
import pl.slapps.dot.model.Route;

/**
 * Created by piotr on 14/02/16.
 */
public class GeneratorLayoutConstruct {

    private String TAG = GeneratorLayout.class.getName();
    private View layoutConstruct;
    private Generator generator;
    private GeneratorLayout generatorLayout;

    private LinearLayout layoutProgress;

    public View getLayout() {
        return layoutConstruct;
    }

    public void initLayout(GeneratorLayout generatorLayout) {
        this.generatorLayout = generatorLayout;
        this.generator = generatorLayout.generator;


        layoutConstruct = LayoutInflater.from(generator.view.context).inflate(R.layout.layout_generator_construct, null);
        layoutProgress = (LinearLayout) layoutConstruct.findViewById(R.id.layout_progress_base);
    }

    public void refreashLayout(ArrayList<TileRoute> currentRoutes) {




        layoutProgress.removeAllViews();
        for (int i = 0; i < currentRoutes.size(); i++) {
            layoutProgress.addView(getProgressRow(currentRoutes.get(i), i));
        }


    }


    private View getProgressRow(final TileRoute route, int index) {
        View row = LayoutInflater.from(generator.view.context).inflate(R.layout.row_path_element, null);
        ImageView imgRoute = (ImageView) row.findViewById(R.id.img_route);
        ImageView imgTrash = (ImageView) row.findViewById(R.id.img_trash);
        TextView tvCount = (TextView) row.findViewById(R.id.tv_count);
        TextView tvMove = (TextView) row.findViewById(R.id.tv_movement);
        NumberPickerTextView etSpeedRatio = (NumberPickerTextView) row.findViewById(R.id.et_speed_ratio);
        final LinearLayout layoutBase = (LinearLayout) row.findViewById(R.id.layout_base);
        final LinearLayout layoutDetails = (LinearLayout) row.findViewById(R.id.layout_details);


        final TextView btnSound = (TextView) row.findViewById(R.id.btn_choose_sound);
        ImageView btnPlay = (ImageView) row.findViewById(R.id.btn_play_sound);


        if (route.sound != null && !route.sound.equals(""))
            btnSound.setText(route.sound);


        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vie) {
                if (route.sound.trim().equals("")) {
                    Toast.makeText(generator.view.context, "Select file first", Toast.LENGTH_LONG).show();
                    return;
                }

                generator.view.context.getSoundsManager().playRawFile(route.sound);
            }
        });


        btnSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                final Dialog dialogChooseSound = new Dialog(generator.view.context);
                dialogChooseSound.requestWindowFeature(Window.FEATURE_NO_TITLE);
                final View chooseView = LayoutInflater.from(generator.view.context).inflate(R.layout.dialog_stages, null);
                ListView lv = (ListView) chooseView.findViewById(R.id.lv);
                lv.setAdapter(new ArrayAdapter<String>(generator.view.context, android.R.layout.simple_list_item_1, MainActivity.listRaw()));

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        route.sound = adapterView.getItemAtPosition(i).toString();
                        btnSound.setText(route.sound);
                        dialogChooseSound.dismiss();
                    }
                });
                dialogChooseSound.setContentView(chooseView);
                dialogChooseSound.show();

            }
        });


        layoutBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutDetails.getVisibility() == View.GONE) {
                    layoutBase.setSelected(true);
                    layoutDetails.setVisibility(View.VISIBLE);

                    generatorLayout.setCurrentTile(route);
                } else {
                    layoutBase.setSelected(false);

                    layoutDetails.setVisibility(View.GONE);
                }
            }
        });

        TextView tvType = (TextView) row.findViewById(R.id.tv_type);
        TextView tvX = (TextView) row.findViewById(R.id.tv_x);
        TextView tvY = (TextView) row.findViewById(R.id.tv_y);
        etSpeedRatio.setmMinValue(0.1f);
        etSpeedRatio.putValue((float)route.speedRatio);
        etSpeedRatio.setListener(new NumberPickerTextView.OnLabelValueChanged() {
            @Override
            public void onValueChanged(float value) {
                route.speedRatio = value;
                if(generator.view.getGame().getPreview())
                    generator.view.getGame().configRoute(route);

            }
        });


        tvType.setText(route.type.name());
        tvX.setText(Integer.toString(route.horizontalPos));
        tvY.setText(Integer.toString(route.verticalPos));
        tvCount.setText("#" + index);
        imgRoute.setImageDrawable(getRouteDrawable(route));
        tvMove.setText(route.getDirection().name());
        imgTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generator.view.context.getSoundsManager().playRawFile("construct");

                TileRoute r = new TileRoute(generator.view.screenWidth, generator.view.screenHeight, generator.gridX, generator.gridY, route.horizontalPos, route.verticalPos, Route.Direction.TOP.name(), Route.Direction.RIGHT.name(), Route.Type.TILE,generator);
                generator.tiles.remove(route);
                generator.tiles.add(r);
                ArrayList<TileRoute> currentRoutes = generator.getPath();
                refreashLayout(currentRoutes);

            }
        });

        return row;
    }

    private Drawable getRouteDrawable(TileRoute route) {
        Drawable d = null;

        switch (route.getDirection()) {
            case LEFTBOTTOM:
            case BOTTOMLEFT:
                d = generator.view.context.getResources().getDrawable(R.drawable.arrow_bottom_left);
                break;
            case LEFTTOP:
            case TOPLEFT:
                d = generator.view.context.getResources().getDrawable(R.drawable.arrow_top_left);
                break;
            case LEFTRIGHT:
            case RIGHTLEFT:
                d = generator.view.context.getResources().getDrawable(R.drawable.arrow_horizontal);
                break;
            case RIGHTBOTTOM:
            case BOTTOMRIGHT:
                d = generator.view.context.getResources().getDrawable(R.drawable.arrow_bottom_right);
                break;
            case RIGHTTOP:
            case TOPRIGHT:
                d = generator.view.context.getResources().getDrawable(R.drawable.arrow_top_right);
                break;
            case TOPBOTTOM:
            case BOTTOMTOP:
                d = generator.view.context.getResources().getDrawable(R.drawable.arrow_vertical);
                break;


        }
        return d;
    }



}
