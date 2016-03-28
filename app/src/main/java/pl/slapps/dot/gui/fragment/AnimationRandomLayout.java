package pl.slapps.dot.gui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import pl.slapps.dot.DAO;
import pl.slapps.dot.MainActivity;
import pl.slapps.dot.R;
import pl.slapps.dot.SurfaceRenderer;
import pl.slapps.dot.drawing.Util;
import pl.slapps.dot.gui.AnimationEntrance;
import pl.slapps.dot.gui.AnimationHide;
import pl.slapps.dot.gui.AnimationShow;
import pl.slapps.dot.model.Config;
import pl.slapps.dot.model.Stage;

/**
 * Created by piotr on 16/03/16.
 */
public class AnimationRandomLayout {

    private final static String TAG = AnimationRandomLayout.class.getName();

    private AnimationShow showAnimation;
    private AnimationHide hideAnimation;
    private ImageButton btnShuffle;
    private TextView tvName;
    private TextView tvDesc;

    private Animation colorAnimation;
    private View layout;
    private MainActivity context;
    private SurfaceRenderer renderer;

    public AnimationRandomLayout(SurfaceRenderer renderer)
    {
        this.renderer=renderer;
    }
    public void config(Stage stage) {

        Config config = stage.config;
        String textColor = config.colors.colorShip;
        tvName.setText(stage.name);
        tvDesc.setText(stage.description);

        tvName.setTextColor(Color.parseColor(textColor));
        tvDesc.setTextColor(Color.parseColor(textColor));



    }

    public View getLayout()
    {
        return layout;
    }
    public void hide()
    {
        hideAnimation.startAnimation(500);
    }
    public void initLayout(final MainActivity context) {

        this.context=context;
        layout = LayoutInflater.from(context).inflate(R.layout.layout_random,null);

        tvName = (TextView) layout.findViewById(R.id.tv_lvl);
        tvDesc = (TextView) layout.findViewById(R.id.tv_desc);
        showAnimation = new AnimationShow(layout);

        hideAnimation = new AnimationHide(layout, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                context.gameHolder.removeView(layout);
                renderer.setRunnig(true);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        colorAnimation = new Animation() {
            public void applyTransformation(float progress, Transformation f) {

            //    Log.d("zzz", "estimate colors " + colorStart + "  " + colorEnd);
             //   scoreLayout.setBackgroundColor(Color.parseColor(Util.calculateColorsSwitch(colorStart, colorEnd, progress)));
              //  ViewCompat.setAlpha(tvScore, 1 - progress);
            }
        };

        colorAnimation.setDuration(1000);

        colorAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

/*

*/

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        btnShuffle = (ImageButton) layout.findViewById(R.id.btn_shuffle);


        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                DAO.getRandomStage(new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {

                        Log.d(TAG, response.toString());
                        JSONObject object = null;
                        try {
                            object = new JSONObject(response.toString());
                            object = object.has("api") ? object.getJSONObject("api") : object;
                            object = object.has("doc") ? object.getJSONObject("doc") : object;

                            context.loadStage(Stage.valueOf(object), true);

                            context.mAdView.setVisibility(View.GONE);
                            hideAnimation.startAnimation(300);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());

                    }
                }, context.android_id);

            }
        });


    }


    public void showLayout(final AnimationShow.OnAnimationListener listener) {


        if(layout.getParent()==null) {
            context.gameHolder.addView(layout);
            Log.d("aaaa","score layout showed");
        }


        //scoreLayout.setVisibility(View.VISIBLE);

        Log.d("zzz", "start animation");

        showAnimation.startAnimation(new AnimationShow.OnAnimationListener() {
            @Override
            public void onAnimationEnd() {
                //entranceAnimation.startAnimation(listener);
            }

            @Override
            public void onAnimationStart() {
                //entranceAnimation.clearAnimation();

                //scoreLayout.clearAnimation();
                //scoreLayout.setVisibility(View.VISIBLE);
            }
        }, 500);

    }

}
