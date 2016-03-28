package pl.slapps.dot.generator.gui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;

import pl.slapps.dot.DAO;
import pl.slapps.dot.R;
import pl.slapps.dot.generator.Generator;
import pl.slapps.dot.generator.auto.AutoGenerator;

/**
 * Created by piotr on 14/02/16.
 */
public class GeneratorLayoutConfig {

    private String TAG = GeneratorLayout.class.getName();
    private View layoutConfig;
    private Generator generator;
    private GeneratorLayout generatorLayout;
    private AutoGenerator autoGenerator;

    public View getLayout()
    {
        return layoutConfig;
    }

    public void initLayout(GeneratorLayout generatorLayout) {
        this.generatorLayout=generatorLayout;
        this.generator=generatorLayout.generator;
        autoGenerator = new AutoGenerator(generator);


        layoutConfig = LayoutInflater.from(generator.view.context).inflate(R.layout.layout_generator_config,null);

        TextView tvLoad = (TextView) layoutConfig.findViewById(R.id.tv_load);
        TextView tvLoadOnline = (TextView) layoutConfig.findViewById(R.id.tv_load_online);
        TextView tvDeleteOnline = (TextView) layoutConfig.findViewById(R.id.tv_delete_online);
        TextView tvGenerateRandom= (TextView) layoutConfig.findViewById(R.id.tv_generate_random);
        TextView tvGenerateWorldStages= (TextView) layoutConfig.findViewById(R.id.tv_generate_world_stages);


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int id = v.getId();

                switch (id) {


                    case R.id.tv_load: {
                        GeneratorLayoutConfig.this.generatorLayout.loadMaze();


                        break;
                    }

                    case R.id.tv_load_online: {
                        GeneratorLayoutConfig.this.generatorLayout.loadOnlineMaze();
                        break;
                    }



                    case R.id.tv_delete_online: {


                        if (generator._id == null) {
                            Toast.makeText(generator.view.context, "First you have to load online stage", Toast.LENGTH_LONG).show();
                        } else {

                            AlertDialog dialog = new AlertDialog.Builder(generator.view.context).setMessage("Remove stage?").setNegativeButton("no", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialogInterface, int i) {
                                    DAO.removeStage(new Response.Listener() {
                                        @Override
                                        public void onResponse(Object response) {

                                            Log.d(TAG, "Stage removed ");
                                            Toast.makeText(generator.view.context, "Stage removed", Toast.LENGTH_LONG).show();
                                            generator._id = null;
                                            dialogInterface.dismiss();
                                        }
                                    }, generator._id);
                                }
                            }).show();


                        }
                        //deleteOnlineMaze();
                        break;
                    }

                    case R.id.tv_generate_random:
                    {

                        autoGenerator.generateMaze(12,12,null);
                        break;
                    }

                    case R.id.tv_generate_world_stages:
                    {

                        autoGenerator.generateStagesSet(500);
                        break;
                    }


                }


            }
        };


        tvLoad.setOnClickListener(listener);
        tvLoadOnline.setOnClickListener(listener);
        tvDeleteOnline.setOnClickListener(listener);
        tvGenerateRandom.setOnClickListener(listener);
        tvGenerateWorldStages.setOnClickListener(listener);


    }

}
