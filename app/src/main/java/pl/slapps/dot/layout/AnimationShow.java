package pl.slapps.dot.layout;

import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by piotr on 23/02/16.
 */
public class AnimationShow {

    public interface OnAnimationListener {
        public void onAnimationEnd();

        public void onAnimationStart();
    }


    private View view;
    private Animation animation;
    private OnAnimationListener listener;


    public AnimationShow(final View view) {
        this.view = view;
        animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                super.applyTransformation(interpolatedTime, t);
                ViewCompat.setAlpha(view, interpolatedTime);
            }
        };
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {



                if (listener != null)
                    listener.onAnimationStart();

                view.setVisibility(View.VISIBLE);
                Log.d("zzz", "show layout");

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                if (listener != null)
                    listener.onAnimationEnd();


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void startAnimation(OnAnimationListener listener, int duration) {
        this.listener = listener;
        animation.setDuration(duration);
        view.clearAnimation();
        view.startAnimation(animation);
        //view.setVisibility(View.VISIBLE);
        Log.d("zzz","show animation started");
        //generator.setVisibility(View.VISIBLE);
    }

}