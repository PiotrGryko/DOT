package pl.slapps.dot.generator.gui;

import android.app.Dialog;
import android.graphics.Color;
import android.text.Layout;
import android.util.Log;
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

import com.android.volley.Response;

import pl.slapps.dot.DAO;
import pl.slapps.dot.MainActivity;
import pl.slapps.dot.R;
import pl.slapps.dot.generator.Generator;
import pl.slapps.dot.model.World;
import yuku.ambilwarna.AmbilWarnaDialog;

/**
 * Created by piotr on 14/02/16.
 */
public class GeneratorLayoutWorlds {

    private String TAG = GeneratorLayoutWorlds.class.getName();
    private View layoutWorld;
    private GeneratorLayout generatorLayout;
    private Generator generator;

    public View getLayout()
    {
        return layoutWorld;
    }

    public void initLayout(final GeneratorLayout generatorLayout) {

        this.generatorLayout=generatorLayout;
        this.generator=generatorLayout.generator;
        layoutWorld = LayoutInflater.from(generator.view.context).inflate(R.layout.layout_generator_worlds,null);

        final World tmpWorld = new World();

        final ImageView btnSaveWorld = (ImageView) layoutWorld.findViewById(R.id.btn_save_world);
        final EditText etWorldName = (EditText) layoutWorld.findViewById(R.id.et_world_name);
        final LinearLayout tvBackgroundColor = (LinearLayout) layoutWorld.findViewById(R.id.tv_world_background_color);
        final LinearLayout tvRouteColor = (LinearLayout) layoutWorld.findViewById(R.id.tv_world_route_color);
        final LinearLayout tvDotColor = (LinearLayout) layoutWorld.findViewById(R.id.tv_dot_world_color);
        final LinearLayout tvExplosionStartColor = (LinearLayout) layoutWorld.findViewById(R.id.tv_world_explosion_start_color);
        final LinearLayout tvExplosionEndColor = (LinearLayout) layoutWorld.findViewById(R.id.tv_world_explosion_end_color);


        final View colorBackground = layoutWorld.findViewById(R.id.color_world_background);
        final View colorRoute = layoutWorld.findViewById(R.id.color_world_route);
        final View colorDot = layoutWorld.findViewById(R.id.color_world_dot);
        final View colorExplosionStart = layoutWorld.findViewById(R.id.color_world_explosion_start);
        final View colorExplosionEnd = layoutWorld.findViewById(R.id.color_world_explosion_end);


        colorBackground.setBackgroundColor(Color.parseColor(generator.backgroundColor));
        colorRoute.setBackgroundColor(Color.parseColor(generator.routeColor));
        colorDot.setBackgroundColor(Color.parseColor(generator.dotColor));
        colorExplosionStart.setBackgroundColor(Color.parseColor(generator.explosionStartColor));
        colorExplosionEnd.setBackgroundColor(Color.parseColor(generator.explosionEndColor));



        tvBackgroundColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(generator.view.context, Color.parseColor(generator.backgroundColor), new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        // color is the color selected by the user.

                        colorBackground.setBackgroundColor(color);
                        tmpWorld.colorBackground = "#" + Integer.toHexString(color);


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
                        tmpWorld.colorRoute = "#" + Integer.toHexString(color);

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
                        tmpWorld.colorShip = "#" + Integer.toHexString(color);

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
                        tmpWorld.colorExplosionStart = "#" + Integer.toHexString(color);

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
                        tmpWorld.colorExplosionEnd = "#" + Integer.toHexString(color);

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




        final TextView btnSoundBackground = (TextView) layoutWorld.findViewById(R.id.btn_choose_world_background);
        final TextView btnSoundPress = (TextView) layoutWorld.findViewById(R.id.btn_choose_world_press);
        final TextView btnSoundCrash = (TextView) layoutWorld.findViewById(R.id.btn_choose_world_crash);
        final TextView btnSoundFinish = (TextView) layoutWorld.findViewById(R.id.btn_choose_world_finish);

        ImageView btnPlayBackground = (ImageView) layoutWorld.findViewById(R.id.btn_play_world_background);
        ImageView btnPlayPress = (ImageView) layoutWorld.findViewById(R.id.btn_play_world_press);
        ImageView btnPlayCrash = (ImageView) layoutWorld.findViewById(R.id.btn_play_world_crash);
        ImageView btnPlayFinish = (ImageView) layoutWorld.findViewById(R.id.btn_play_world_finish);

        if (!tmpWorld.soundBackground.equals(""))
            btnSoundBackground.setText(tmpWorld.soundBackground);
        if (!tmpWorld.soundPress.equals(""))
            btnSoundPress.setText(tmpWorld.soundPress);
        if (!tmpWorld.soundCrash.equals(""))
            btnSoundBackground.setText(tmpWorld.soundCrash);
        if (!tmpWorld.soundFinish.equals(""))
            btnSoundFinish.setText(tmpWorld.soundFinish);


        btnPlayBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vie) {
                if (tmpWorld.soundBackground.trim().equals("")) {
                    Toast.makeText(generator.view.context, "Select file first", Toast.LENGTH_LONG).show();
                    return;
                }

                generator.view.context.getSoundsManager().playRawFile(tmpWorld.soundBackground);
            }
        });
        btnPlayPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vie) {
                if (tmpWorld.soundPress.trim().equals("")) {
                    Toast.makeText(generator.view.context, "Select file first", Toast.LENGTH_LONG).show();
                    return;
                }
                generator.view.context.getSoundsManager().playRawFile(tmpWorld.soundPress);

            }
        });
        btnPlayCrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vie) {
                if (tmpWorld.soundCrash.trim().equals("")) {
                    Toast.makeText(generator.view.context, "Select file first", Toast.LENGTH_LONG).show();
                    return;
                }
                generator.view.context.getSoundsManager().playRawFile(tmpWorld.soundCrash);

            }
        });
        btnPlayFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vie) {
                if (tmpWorld.soundFinish.trim().equals("")) {
                    Toast.makeText(generator.view.context, "Select file first", Toast.LENGTH_LONG).show();
                    return;
                }
                generator.view.context.getSoundsManager().playRawFile(tmpWorld.soundFinish);

            }
        });


        btnSoundBackground.setOnClickListener(new View.OnClickListener() {
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
                        tmpWorld.soundBackground = adapterView.getItemAtPosition(i).toString();
                        btnSoundBackground.setText(tmpWorld.soundBackground);
                        dialogChooseSound.dismiss();
                    }
                });
                dialogChooseSound.setContentView(chooseView);
                dialogChooseSound.show();

            }
        });

        btnSoundPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                final Dialog dialogChooseSound = new Dialog(generator.view.context);
                dialogChooseSound.requestWindowFeature(Window.FEATURE_NO_TITLE);
                View chooseView = LayoutInflater.from(generator.view.context).inflate(R.layout.dialog_stages, null);
                ListView lv = (ListView) chooseView.findViewById(R.id.lv);
                lv.setAdapter(new ArrayAdapter<String>(generator.view.context, android.R.layout.simple_list_item_1, MainActivity.listRaw()));
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        tmpWorld.soundPress = adapterView.getItemAtPosition(i).toString();
                        btnSoundPress.setText(tmpWorld.soundPress);

                        dialogChooseSound.dismiss();
                    }
                });
                dialogChooseSound.setContentView(chooseView);
                dialogChooseSound.show();

            }
        });

        btnSoundCrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                final Dialog dialogChooseSound = new Dialog(generator.view.context);
                dialogChooseSound.requestWindowFeature(Window.FEATURE_NO_TITLE);
                View chooseView = LayoutInflater.from(generator.view.context).inflate(R.layout.dialog_stages, null);
                ListView lv = (ListView) chooseView.findViewById(R.id.lv);
                lv.setAdapter(new ArrayAdapter<String>(generator.view.context, android.R.layout.simple_list_item_1, MainActivity.listRaw()));
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        tmpWorld.soundCrash = adapterView.getItemAtPosition(i).toString();
                        btnSoundCrash.setText(tmpWorld.soundCrash);

                        dialogChooseSound.dismiss();
                    }
                });
                dialogChooseSound.setContentView(chooseView);
                dialogChooseSound.show();

            }
        });

        btnSoundFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {
                final Dialog dialogChooseSound = new Dialog(generator.view.context);
                dialogChooseSound.requestWindowFeature(Window.FEATURE_NO_TITLE);
                View chooseView = LayoutInflater.from(generator.view.context).inflate(R.layout.dialog_stages, null);
                ListView lv = (ListView) chooseView.findViewById(R.id.lv);
                lv.setAdapter(new ArrayAdapter<String>(generator.view.context, android.R.layout.simple_list_item_1, MainActivity.listRaw()));
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        tmpWorld.soundFinish = adapterView.getItemAtPosition(i).toString();
                        btnSoundFinish.setText(tmpWorld.soundFinish);

                        dialogChooseSound.dismiss();
                    }
                });
                dialogChooseSound.setContentView(chooseView);
                dialogChooseSound.show();

            }
        });


        btnSaveWorld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etWorldName.getText().toString().trim().equals("")) {
                    Toast.makeText(generator.view.context, "set world name", Toast.LENGTH_LONG).show();
                    return;
                }
                if (tmpWorld.soundBackground.equals("")) {
                    Toast.makeText(generator.view.context, "choose background sound", Toast.LENGTH_LONG).show();
                    return;
                }
                if (tmpWorld.soundPress.equals("")) {
                    Toast.makeText(generator.view.context, "choose press sound", Toast.LENGTH_LONG).show();
                    return;
                }
                if (tmpWorld.soundCrash.equals("")) {
                    Toast.makeText(generator.view.context, "choose crash sound", Toast.LENGTH_LONG).show();
                    return;
                }
                if (tmpWorld.soundFinish.equals("")) {
                    Toast.makeText(generator.view.context, "choose finish sound", Toast.LENGTH_LONG).show();
                    return;
                }
                tmpWorld.name = etWorldName.getText().toString();
                //tmpWorld.id=Long.toString(System.currentTimeMillis());
                //addWorld(tmpWorld);

                DAO.addWorld(generator.view.context, tmpWorld.toJson(), new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        Log.d(TAG, response.toString());
                        Toast.makeText(generator.view.context, "World created", Toast.LENGTH_LONG).show();
                    }
                }, null);

            }
        });


    }

}
