package pl.slapps.dot.layout;

import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import pl.slapps.dot.drawing.Util;

/**
 * Created by piotr on 23/02/16.
 */
public class AnimationMainMenu {

    private MainMenu menu;
    public AnimationHide menuHideAnimation;
    public AnimationShow menuShowAnimation;
    public AnimationHide headerHideAnimation;
    public AnimationHide btnsHideAnimation;
    public AnimationEntrance entranceAnimation;

    public AnimationMainMenu(MainMenu menu)
    {
        this.menu=menu;

        init();
    }

    public void showMenu()
    {
        if(menu.getLayout().getParent()==null)
            menu.context.gameHolder.addView(menu.getLayout());

        menu.getGame().setRunnig(false);
        menu.enableButtons();


        menuShowAnimation.startAnimation(null, 500);
    }
    public void hideMenu()
    {
        menu.getGame().setDrawing(true);
        menu.disableButtons();
        headerHideAnimation.startAnimation(500);
        btnsHideAnimation.startAnimation(500);
       // menuHideAnimation.startAnimation(500);
    }

    public void setColor(final String colorStart, final String colorEnd)
    {
        Animation colorAnimation = new Animation() {
            public void applyTransformation(float progress, Transformation f) {

                Log.d("zzz", "estimate colors " + colorStart + "  " + colorEnd);

                final int color = Color.parseColor(Util.calculateColorsSwitch(colorStart, colorEnd, progress));
                int c = Color.argb(100, Color.red(color), Color.green(color), Color.blue(color));
                menu.getBackground().setBackgroundColor(c);
            }
        };
        colorAnimation.setDuration(500);
        menu.getBackground().startAnimation(colorAnimation);
    }

    public void init()
    {



        menuHideAnimation = new AnimationHide(menu.getLayoutMenu(), new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                menu.getLayoutMenu().setVisibility(View.GONE);
                menuHideAnimation.clearAnimation();
                entranceAnimation.clearAnimation();
                btnsHideAnimation.clearAnimation();
                headerHideAnimation.clearAnimation();

                menu.context.gameHolder.removeView(menu.getLayout());
                menu.getGame().setRunnig(true);



            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        menuShowAnimation = new AnimationShow(menu.getLayoutMenu());
        headerHideAnimation = new AnimationHide(menu.getTvHeader(), new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        entranceAnimation = new AnimationEntrance(menu.getLayoutHeader());

        btnsHideAnimation = new AnimationHide(menu.getLayoutBtns(), new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                entranceAnimation.startAnimation(new AnimationShow.OnAnimationListener() {
                    @Override
                    public void onAnimationEnd() {
                        menuHideAnimation.startAnimation(500);

                    }

                    @Override
                    public void onAnimationStart() {

                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
