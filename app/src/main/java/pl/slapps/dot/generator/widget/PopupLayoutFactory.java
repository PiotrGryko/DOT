package pl.slapps.dot.generator.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import pl.slapps.dot.R;
import pl.slapps.dot.generator.Generator;
import pl.slapps.dot.generator.TileRoute;
import pl.slapps.dot.generator.gui.GeneratorLayout;
import pl.slapps.dot.generator.gui.GeneratorLayoutColors;
import pl.slapps.dot.generator.gui.GeneratorLayoutLights;
import pl.slapps.dot.generator.gui.GeneratorLayoutPath;
import pl.slapps.dot.generator.gui.GeneratorLayoutSounds;
import pl.slapps.dot.model.Colors;

/**
 * Created by piotr on 17/02/16.
 */
public class PopupLayoutFactory {


    class PopupLayout {
        PopupWindow popup;
        private View layout;
        private float mCurrentX;
        private float mCurrentY;


        public PopupLayout(View layout) {
            this.layout = layout;

            View v = LayoutInflater.from(generator.view.context).inflate(R.layout.dialog_path, null);

            View drag = v.findViewById(R.id.base_drag);
            View close = v.findViewById(R.id.btn_close);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popup.dismiss();
                }
            });
            LinearLayout pathBase = (LinearLayout) v.findViewById(R.id.base_path);

            pathBase.addView(layout);


            popup = new PopupWindow(v, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


            View.OnTouchListener otl = new View.OnTouchListener() {
                private float mDx;
                private float mDy;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    if (action == MotionEvent.ACTION_DOWN) {
                        mDx = mCurrentX - event.getRawX();
                        mDy = mCurrentY - event.getRawY();
                    } else if (action == MotionEvent.ACTION_MOVE) {
                        float mX = (event.getRawX() + mDx);
                        float mY = (event.getRawY() + mDy);
                        popup.update((int) mX, (int) mY, -1, -1);
                    } else if (action == MotionEvent.ACTION_UP) {
                        mCurrentX = (event.getRawX() + mDx);
                        mCurrentY = (event.getRawY() + mDy);
                        //mPopup.update((int) mX, (int) mY, -1, -1);
                    }

                    return true;
                }
            };
            drag.setOnTouchListener(otl);


        }

        public PopupWindow getPopup() {
            return popup;
        }


        public void dissmiss() {
            if (popup.isShowing())
                popup.dismiss();
        }

        public void show(final float mCurrentX, final float mCurrentY) {

            //layoutPath.refreashLayout();

            //ViewCompat.setAlpha(v,1);

            if (!popup.isShowing()) {
                this.mCurrentX = mCurrentX;
                this.mCurrentY = mCurrentY;

                popup.showAtLocation(generator.view, Gravity.NO_GRAVITY, (int) mCurrentX, (int) mCurrentY);
            }
            //layoutPath.estimateAllDirections();

        }

    }

    private PopupLayout popupPath;
    private PopupLayout popupColors;
    private PopupLayout popupLights;
    private PopupLayout popupSounds;

    private Generator generator;

    private GeneratorLayoutPath layoutPath;
    private GeneratorLayoutColors layoutColors;
    private GeneratorLayoutLights layoutLights;
    private GeneratorLayoutSounds layoutSounds;


    //private View v;

    public void refreashLayout() {
        layoutPath.refreashLayout();
        layoutColors.refreashLayout();
        layoutLights.refreashLayout();
        layoutSounds.refreashLayout();

    }

    public void onDrawerSlide(float slide) {
        //if (popupPath.getPopup().isShowing())
        //    ViewCompat.setAlpha(v, 1 - slide);
    }


    public PopupLayoutFactory(Generator generator) {
        this.generator = generator;
        layoutPath = new GeneratorLayoutPath();
        layoutPath.initLayout(generator.getLayout());
        layoutPath.refreashLayout();

        popupPath = new PopupLayout(layoutPath.getLayout());


        layoutColors = new GeneratorLayoutColors();
        layoutColors.initLayout(generator.getLayout());
        layoutColors.refreashLayout();

        popupColors = new PopupLayout(layoutColors.getLayout());

        /// initView(path, layoutPath.getLayout(), popupPath);
        layoutLights = new GeneratorLayoutLights();
        layoutLights.initLayout(generator.getLayout());
        layoutLights.refreashLayout();


        popupLights = new PopupLayout(layoutLights.getLayout());


        layoutSounds = new GeneratorLayoutSounds();
        layoutSounds.initLayout(generator.getLayout());
        layoutSounds.refreashLayout();


        popupSounds = new PopupLayout(layoutSounds.getLayout());

    }


    public void dissmissPath() {
        popupPath.dissmiss();
    }

    public void showPath(final float mCurrentX, final float mCurrentY) {

        layoutPath.refreashLayout();

        popupPath.show(mCurrentX, mCurrentY);
        layoutPath.estimateAllDirections();

    }

    public void dissmissColours() {

            popupColors.dissmiss();
    }

    public void showColours() {

        layoutColors.refreashLayout();

        popupColors.show(200, 200);

    }



    public void dissmissLights() {

        popupLights.dissmiss();
    }

    public void showLights() {

        layoutLights.refreashLayout();

        popupLights.show(200, 200);

    }



    public void dissmissSounds() {

        popupSounds.dissmiss();
    }

    public void showSounds() {

        layoutSounds.refreashLayout();

        popupSounds.show(200, 200);

    }
}
