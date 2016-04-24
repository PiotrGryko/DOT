package pl.slapps.dot.gui;

import android.graphics.Color;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import pl.slapps.dot.MainActivity;
import pl.slapps.dot.R;
import pl.slapps.dot.SurfaceRenderer;
import pl.slapps.dot.drawing.Util;
import pl.slapps.dot.model.Config;
import pl.slapps.dot.model.Stage;

/**
 * Created by piotr on 22/02/16.
 */
public class AnimationScoreLayout {

    private RelativeLayout scoreLayout;
   // private TextView tvScore;

    private TextView tvName;
    private TextView tvDesc;

    private LinearLayout layoutHeader;

    private AnimationEntrance headerEntranceAnimation;
    private AnimationShow headerShowAnimation;

   // private AnimationEntrance entranceAnimation;
    private AnimationShow showAnimation;
    private AnimationHide hideAnimation;

    private String colorStart = "#ffffff";
    private String colorEnd;
    private String textColor;

    private Animation colorAnimation;

    private SurfaceRenderer renderer;
    private MainActivity context;

    private View layout;

    public AnimationScoreLayout(SurfaceRenderer renderer) {
        this.renderer = renderer;
    }


    public void config(Stage stage, int currentStage) {

        Config config = stage.config;
        textColor = config.colors.colorShip;
        tvName.setText("#"+currentStage);
        //tvName.setText(stage.name);
        //tvDesc.setText(stage.description);
        tvDesc.setVisibility(View.GONE);
        tvName.setTextColor(Color.parseColor(textColor));
        //tvDesc.setTextColor(Color.parseColor(textColor));




        if (scoreLayout.getVisibility() == View.GONE) {
            colorStart = config.colors.colorRoute;

            int c = Color.parseColor(config.colors.colorRoute);
            scoreLayout.setBackgroundColor(c);

          //  c = Color.parseColor(config.colors.colorRoute);
          //  tvScore.setTextColor(c);


        } else {
            colorEnd = config.colors.colorRoute;

            scoreLayout.startAnimation(colorAnimation);

            //hideLayout();

        }

    }

    public void initLayout(final MainActivity context) {

        this.context=context;
        layout = LayoutInflater.from(context).inflate(R.layout.layout_score,null);

        scoreLayout = (RelativeLayout) layout.findViewById(R.id.layout_score);
       // tvScore = (TextView) layout.findViewById(R.id.tv_score);

        tvName = (TextView) layout.findViewById(R.id.tv_score_lvl);
        tvDesc = (TextView) layout.findViewById(R.id.tv_score_desc);
        layoutHeader = (LinearLayout) layout.findViewById(R.id.layout_score_header);

        layoutHeader.setVisibility(View.GONE);

        showAnimation = new AnimationShow(scoreLayout);
       // entranceAnimation = new AnimationEntrance(tvScore);

        headerShowAnimation = new AnimationShow(layoutHeader);
        headerEntranceAnimation = new AnimationEntrance(layoutHeader);

        hideAnimation = new AnimationHide(scoreLayout, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                new Handler().post(new Runnable() {
                    public void run() {
                        context.gameHolder.removeView(layout);
                    }
                });
                renderer.setRunnig(true);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        colorAnimation = new Animation() {
            public void applyTransformation(float progress, Transformation f) {

                scoreLayout.setBackgroundColor(Color.parseColor(Util.calculateColorsSwitch(colorStart, colorEnd, progress)));
              //  ViewCompat.setAlpha(tvScore, 1 - progress);
            }
        };

        colorAnimation.setDuration(1000);

        colorAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

             //   tvScore.setTextColor(Color.parseColor(textColor));
                showHeaders();

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                colorStart = colorEnd;
                //    hideLayout();
               // tvScore.setVisibility(View.GONE);
/*

*/

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    public void zoomHeaders() {
        headerEntranceAnimation.startAnimation(new AnimationShow.OnAnimationListener() {
            @Override
            public void onAnimationEnd() {
                hideLayout();
            }

            @Override
            public void onAnimationStart() {

            }
        });
    }

    public void showHeaders() {
        headerShowAnimation.startAnimation(new AnimationShow.OnAnimationListener() {
            @Override
            public void onAnimationEnd() {
                zoomHeaders();
            }

            @Override
            public void onAnimationStart() {

                headerEntranceAnimation.clearAnimation();

            }
        }, 500);
    }

    public void hideLayout() {

        hideAnimation.startAnimation(1000);
    }

    public void showScore(final AnimationShow.OnAnimationListener listener) {


        if(layout.getParent()==null) {
            context.gameHolder.addView(layout);
        }
        //tvScore.setText(score);
        //ViewCompat.setAlpha(tvScore, 1);



      //  entranceAnimation.clearAnimation();

        hideAnimation.clearAnimation();

        layoutHeader.setVisibility(View.GONE);
       // tvScore.setVisibility(View.VISIBLE);


        showAnimation.startAnimation(new AnimationShow.OnAnimationListener() {
            @Override
            public void onAnimationEnd() {
                //entranceAnimation.startAnimation(listener);
                listener.onAnimationEnd();

            }

            @Override
            public void onAnimationStart() {

            //    entranceAnimation.clearAnimation();

                //scoreLayout.clearAnimation();
                //scoreLayout.setVisibility(View.VISIBLE);
            }
        }, 500);

    }


}
