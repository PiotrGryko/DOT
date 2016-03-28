package pl.slapps.dot.generator.gui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pl.slapps.dot.DAO;
import pl.slapps.dot.R;
import pl.slapps.dot.generator.Generator;
import pl.slapps.dot.model.World;

/**
 * Created by piotr on 14/02/16.
 */
public class GeneratorLayoutWorldsList {

    private String TAG = GeneratorLayoutWorldsList.class.getName();
    private LinearLayout layoutWorldsList;
    private LinearLayout woldsBase;
    private ProgressBar progressBar;
    private Generator generator;
    private GeneratorLayout generatorLayout;

    private boolean isLoading;

    public LinearLayout getLayout()
    {
        return layoutWorldsList;
    }

    public void initLayout(GeneratorLayout generatorLayout) {
        this.generatorLayout = generatorLayout;
        this.generator = generatorLayout.generator;


        layoutWorldsList = (LinearLayout)LayoutInflater.from(generator.view.context).inflate(R.layout.layout_generator_worlds_list, null);
        woldsBase = (LinearLayout)layoutWorldsList.findViewById(R.id.base);
        progressBar = (ProgressBar)layoutWorldsList.findViewById(R.id.bar);
        progressBar.setVisibility(View.GONE);
    }

    public interface OnListBuildedListener
    {
        public void onListBuilded(boolean flag);
    }
    private void isLoading(boolean flag)
    {
        this.isLoading = flag;
        if(isLoading)
            progressBar.setVisibility(View.VISIBLE);
        else
        {
            progressBar.setVisibility(View.GONE);
            //AnimationHide ah = new AnimationHide(progressBar,null);
            //ah.startAnimation(300);
        }

    }

    public void buildWorldsList(final OnListBuildedListener listener)
    {
        if(isLoading)
            return;
        isLoading(true);
        woldsBase.removeAllViews();
        DAO.getWorlds( new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                Log.d(TAG, "worlds response");
                Log.d(TAG, response.toString());

                JSONObject res = null;
                try {
                    res = new JSONObject(response.toString());

                    JSONObject api = res.has("api") ? res.getJSONObject("api") : new JSONObject();
                    JSONArray jsonArray = api.has("results") ? api.getJSONArray("results") : new JSONArray();


                    //layoutWorldsList.removeAllViews();


                    for (int i = 0; i < jsonArray.length(); i++) {
                        final World w = World.valueOf(jsonArray.getJSONObject(i));
                        View v = getWorldRow(w, i);

                        woldsBase.addView(v);
                        v.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                generatorLayout.setCurrentWorld(w);
                                //generatorLayout.currentWorld = w;
                                //generatorLayout.refreshControlls();

                            }
                        });

                    }


                    if(listener!=null)
                        listener.onListBuilded(true);




                } catch (JSONException e) {
                    e.printStackTrace();
                }

                isLoading(false);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "get worlds error ");
                Log.d(TAG, error.toString());
                isLoading(false);
                if(listener!=null)
                    listener.onListBuilded(false);

            }
        }, false);
    }

    private View getWorldRow(final World world, int index) {
        final View row = LayoutInflater.from(generator.view.context).inflate(R.layout.row_world, null);

        TextView tvWorldCount = (TextView) row.findViewById(R.id.tv_world_number);
        TextView tvWorldName = (TextView) row.findViewById(R.id.tv_world_name);
        TextView tvWorldStages = (TextView) row.findViewById(R.id.tv_stages_count);
        ImageView btnTrash = (ImageView) row.findViewById(R.id.btn_trash);


        tvWorldCount.setText("#" + index);
        tvWorldName.setTextColor(Color.parseColor(world.colorBackground));
        tvWorldName.setText(world.name);
        tvWorldStages.setText("Stages count: " + world.stages.size());
        btnTrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog dialog = new AlertDialog.Builder(generator.view.context).setMessage("Remove world?").setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        DAO.removeWorld( new Response.Listener() {
                            @Override
                            public void onResponse(Object response) {
                                layoutWorldsList.removeView(row);
                                dialogInterface.dismiss();

                            }
                        }, world.id);
                    }
                }).create();

                dialog.show();
            }
        });


        return row;
    }

}
