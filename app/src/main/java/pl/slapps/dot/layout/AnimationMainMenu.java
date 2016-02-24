package pl.slapps.dot.layout;

import android.view.View;
import android.view.animation.Animation;

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
        menu.getGame().setRunnig(false);
        menu.enableButtons();
        menuShowAnimation.startAnimation(null,500);
    }
    public void hideMenu()
    {
        menu.getGame().setDrawing(true);
        menu.disableButtons();
        headerHideAnimation.startAnimation(500);
        btnsHideAnimation.startAnimation(500);
       // menuHideAnimation.startAnimation(500);
    }

    public void init()
    {
        menuHideAnimation = new AnimationHide(menu.layoutMenu, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                menu.getGame().setRunnig(true);
                menu.layoutMenu.setVisibility(View.GONE);
                menuHideAnimation.clearAnimation();
                entranceAnimation.clearAnimation();
                btnsHideAnimation.clearAnimation();
                headerHideAnimation.clearAnimation();


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        menuShowAnimation = new AnimationShow(menu.layoutMenu);
        headerHideAnimation = new AnimationHide(menu.tvHeader, new Animation.AnimationListener() {
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
        entranceAnimation = new AnimationEntrance(menu.layoutHeader);

        btnsHideAnimation = new AnimationHide(menu.layoutBtns, new Animation.AnimationListener() {
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
