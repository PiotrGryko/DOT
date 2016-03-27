package pl.slapps.dot.gui;

import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.TextView;

/**
 * Created by piotr on 23/02/16.
 */
public class AnimationEntrance {
    private View view;
    private ScaleAnimation animation;
    private AnimationShow.OnAnimationListener listener;


    public void setListener(AnimationShow.OnAnimationListener listener)
    {
        this.listener=listener;
    }
    public AnimationEntrance(final View view) {
        this.view = view;

        animation = new ScaleAnimation(
                1f, 2f, // Start and end values for the X axis scaling
                1f, 2f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling

        animation.setFillAfter(true);
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
