package pl.slapps.dot.generator.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import pl.slapps.dot.R;
import pl.slapps.dot.generator.Generator;
import pl.slapps.dot.generator.TileRoute;
import pl.slapps.dot.generator.gui.GeneratorLayout;
import pl.slapps.dot.generator.gui.GeneratorLayoutPath;
import pl.slapps.dot.model.Colors;

/**
 * Created by piotr on 17/02/16.
 */
public class PathPopup {


    private PopupWindow mPopup;
    private Generator generator;

    private GeneratorLayoutPath layoutPath;
    private float mCurrentX;
    private float mCurrentY;
    private View v;

    public void refreashLayout() {
        layoutPath.refreashLayout();
    }

    public void onDrawerSlide(float slide)
    {
        if(mPopup.isShowing())
        ViewCompat.setAlpha(v,1-slide);
    }

    public PathPopup(Generator generator) {
        this.generator = generator;
        layoutPath = new GeneratorLayoutPath();
        layoutPath.initLayout(generator.getLayout());
        layoutPath.refreashLayout();

        v = LayoutInflater.from(generator.view.context).inflate(R.layout.dialog_path, null);
        View drag = v.findViewById(R.id.base_drag);
        View close = v.findViewById(R.id.btn_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPopup.dismiss();
            }
        });
        LinearLayout pathBase = (LinearLayout) v.findViewById(R.id.base_path);

        pathBase.addView(layoutPath.getLayout());

        mPopup = new PopupWindow(v, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


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
                    mPopup.update((int) mX, (int) mY, -1, -1);
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

    public void dissmiss()
    {
        if(mPopup.isShowing())
        mPopup.dismiss();
    }

    public void show(final float mCurrentX, final float mCurrentY) {

        ViewCompat.setAlpha(v,1);

        if (!mPopup.isShowing()) {
            this.mCurrentX = mCurrentX;
            this.mCurrentY = mCurrentY;

            mPopup.showAtLocation(generator.view, Gravity.NO_GRAVITY, (int) mCurrentX, (int) mCurrentY);
        }
    }
}
