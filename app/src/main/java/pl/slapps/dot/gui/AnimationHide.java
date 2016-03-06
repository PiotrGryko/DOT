package pl.slapps.dot.gui;

import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by piotr on 23/02/16.
 */
public  class AnimationHide {
    private View view;
    private Animation animation;

    public AnimationHide(final View view, Animation.AnimationListener listener) {
        this.view = view;
        animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                super.applyTransformation(interpolatedTime, t);
                ViewCompat.setAlpha(view, 1 - interpolatedTime);
            }
        };

        if (listener != null)
            animation.setAnimationListener(listener);
        else {
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

    public void startAnimation(int duration) {
        animation.setDuration(duration);
        view.startAnimation(animation);
    }

    public void clearAnimation() {
        ViewCompat.setAlpha(view, 1);
    }
}