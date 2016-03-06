package pl.slapps.dot.gui;

import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by piotr on 23/02/16.
 */
public class AnimationEntrance {
    private View view;
    private Animation animation;
    private AnimationShow.OnAnimationListener listener;

    public void setListener(AnimationShow.OnAnimationListener listener)
    {
        this.listener=listener;
    }
    public AnimationEntrance(final View view) {
        this.view = view;

        animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                super.applyTransformation(interpolatedTime, t);
                ViewCompat.setScaleX(view, 1 + interpolatedTime / 3);
                ViewCompat.setScaleY(view, 1 + interpolatedTime / 3);


            }
        };

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                if (listener != null)
                    listener.onAnimationStart();


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

        animation.setDuration(3000);


    }

    public void startAnimation(AnimationShow.OnAnimationListener listener) {
        if (listener != null)
            this.listener = listener;
        view.startAnimation(animation);
    }

    public void clearAnimation() {
        ViewCompat.setScaleX(view, 1);
        ViewCompat.setScaleY(view, 1);
    }
}
