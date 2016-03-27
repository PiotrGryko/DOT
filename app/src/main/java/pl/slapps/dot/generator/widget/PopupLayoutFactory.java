package pl.slapps.dot.generator.widget;

import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import pl.slapps.dot.MainActivity;
import pl.slapps.dot.R;
import pl.slapps.dot.generator.Generator;
import pl.slapps.dot.generator.gui.GeneratorLayoutColors;
import pl.slapps.dot.generator.gui.GeneratorLayoutControls;
import pl.slapps.dot.generator.gui.GeneratorLayoutGrid;
import pl.slapps.dot.generator.gui.GeneratorLayoutLights;
import pl.slapps.dot.generator.gui.GeneratorLayoutPath;
import pl.slapps.dot.generator.gui.GeneratorLayoutSounds;

/**
 * Created by piotr on 17/02/16.
 */
public class PopupLayoutFactory {


    class PopupLayout {
        PopupWindow popup;
        private View layout;
        private View popupView;
        private float mCurrentX;
        private float mCurrentY;
        private View drag;
        private boolean reopen;

        public float getmCurrentX() {
            return mCurrentX;
        }

        public float getmCurrentY() {
            return mCurrentY;
        }

        public View getDrag() {
            return drag;
        }


        public PopupLayout(View layout, boolean closable, String title, final View.OnClickListener listener) {
            this.layout = layout;

            final View v = LayoutInflater.from(generator.view.context).inflate(R.layout.dialog_popup, null);

            drag = v.findViewById(R.id.base_drag);
            View close = v.findViewById(R.id.btn_close);
            TextView tvTitle = (TextView) v.findViewById(R.id.tv_drag);
            tvTitle.setText(title);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popup.dismiss();
                    if (listener != null)
                        listener.onClick(view);

                }
            });

            if (!closable) {
                tvTitle.setVisibility(View.GONE);
                close.setVisibility(View.GONE);
            }

            LinearLayout pathBase = (LinearLayout) v.findViewById(R.id.base_path);

            pathBase.addView(layout);


            popup = new PopupWindow(v, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


            View.OnTouchListener otl = new View.OnTouchListener() {
                private float mDx;
                private float mDy;

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if (generator.view.context.drawer.isDrawerOpen(generator.view.context.drawerContent))
                        return true;

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


        public void animateToTop(float progress) {


            float initX = mCurrentX + ((MainActivity.screenWidth - mCurrentX) * (progress));
            float initY = mCurrentY * (1 - progress);


            popup.update((int) initX, (int) initY, -1, -1);


        }

        public void dismissAndReopen() {
            if (popup.isShowing()) {
                reopen = true;
                popup.dismiss();
            }
        }

        public void dissmiss() {
            if (popup.isShowing())
                popup.dismiss();
        }

        public boolean isShowing() {
            if (!popup.isShowing() && reopen) {
                reopen();
                return true;
            }
            return popup.isShowing();
        }

        public void reopen() {


            if (!popup.isShowing() && reopen) {

                show(mCurrentX, mCurrentY);
            }

        }


        public void show(final float mCurrentX, final float mCurrentY) {

            //layoutPath.refreashLayout();

            //ViewCompat.setAlpha(v,1);


            if (!popup.isShowing()) {
                reopen = false;
                this.mCurrentX = mCurrentX;
                this.mCurrentY = mCurrentY;

                popup.showAtLocation(generator.view.context.gameHolder, Gravity.NO_GRAVITY, (int) mCurrentX, (int) mCurrentY);
            }
            //layoutPath.estimateAllDirections();

        }

    }

    private PopupLayout popupPath;
    private PopupLayout popupColors;
    private PopupLayout popupLights;
    private PopupLayout popupSounds;
    private PopupLayout popupGrid;
    private PopupLayout popupControls;


    private Generator generator;

    private GeneratorLayoutPath layoutPath;
    private GeneratorLayoutColors layoutColors;
    private GeneratorLayoutLights layoutLights;
    private GeneratorLayoutSounds layoutSounds;
    private GeneratorLayoutGrid layoutGrid;
    private GeneratorLayoutControls layoutControls;

    //private View v;

    public void refreashLayout() {
        layoutPath.refreashLayout();
        layoutColors.refreashLayout();
        layoutLights.refreashLayout();
        layoutSounds.refreashLayout();
        layoutGrid.refreashLayout();
        layoutControls.refreshLayout();

    }

    public GeneratorLayoutControls getLayoutControls() {
        return layoutControls;
    }


    public PopupLayoutFactory(final Generator generator) {
        this.generator = generator;
        layoutPath = new GeneratorLayoutPath();
        layoutPath.initLayout(generator.getLayout());
        layoutPath.refreashLayout();

        popupPath = new PopupLayout(layoutPath.getLayout(), true, "Path", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generator.getLayout().tile.setCurrentTile(false);
            }
        });


        layoutColors = new GeneratorLayoutColors();
        layoutColors.initLayout(generator.getLayout());
        layoutColors.refreashLayout();

        popupColors = new PopupLayout(layoutColors.getLayout(), true, "Colours", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutControls.getButtonColours().setSelected(false);
            }
        });

        /// initView(path, layoutPath.getLayout(), popupPath);
        layoutLights = new GeneratorLayoutLights();
        layoutLights.initLayout(generator.getLayout());
        layoutLights.refreashLayout();


        popupLights = new PopupLayout(layoutLights.getLayout(), true, "Lights", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutControls.getButtonLights().setSelected(false);
            }
        });


        layoutSounds = new GeneratorLayoutSounds();
        layoutSounds.initLayout(generator.getLayout());
        layoutSounds.refreashLayout();


        popupSounds = new PopupLayout(layoutSounds.getLayout(), true, "Sounds",  new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutControls.getButtonSounds().setSelected(false);
            }
        });

        layoutGrid = new GeneratorLayoutGrid();
        layoutGrid.initLayout(generator.getLayout());
        layoutGrid.refreashLayout();


        popupGrid = new PopupLayout(layoutGrid.getLayout(), true, "Grid",  new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutControls.getButtonGrid().setSelected(false);
            }
        });


        layoutControls = new GeneratorLayoutControls();
        layoutControls.initLayout(generator.getLayout());
        //layoutControls.refreashLayout();

        popupControls = new PopupLayout(layoutControls.getLayout(), false, "", null);
        popupControls.getDrag().setBackgroundDrawable(null);

        generator.view.context.drawer.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                //  pathPopup.onDrawerSlide(slideOffset);
                popupControls.animateToTop(slideOffset);

                alphaAnimatePopups(slideOffset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                //  pathPopup.dissmissPath();
                hideOrShowPopoups();
                layoutControls.getButtonSettings().setSelected(true);

            }

            @Override
            public void onDrawerClosed(View drawerView) {

                hideOrShowPopoups();
                layoutControls.getButtonSettings().setSelected(false);

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

    }

    public void alphaAnimatePopups(float slideOffset) {
        onSlide(slideOffset, popupPath);
        onSlide(slideOffset, popupColors);
        onSlide(slideOffset, popupLights);
        onSlide(slideOffset, popupGrid);
        onSlide(slideOffset, popupSounds);

    }

    public void hideOrShowPopoups() {
        setDrawerState(popupPath);
        setDrawerState(popupColors);
        setDrawerState(popupLights);
        setDrawerState(popupGrid);
        setDrawerState(popupSounds);

    }

    private void onSlide(float slide, PopupLayout window) {
        if (window.isShowing())
            ViewCompat.setAlpha(window.getPopup().getContentView(), 1 - slide);
    }

    private void setDrawerState(PopupLayout window) {
        if (generator.view.context.drawer.isDrawerOpen(generator.view.context.drawerContent)) {
            ViewCompat.setAlpha(window.getPopup().getContentView(), 0);
            //window.getPopup().getContentView().setEnabled(false);
            window.dismissAndReopen();

        } else {
            ViewCompat.setAlpha(window.getPopup().getContentView(), 1);
            window.getPopup().getContentView().setEnabled(true);
        }
    }


    public void dissmissPath() {
        popupPath.dissmiss();
    }


    public void showPath(final float mCurrentX, final float mCurrentY) {

        layoutPath.refreashLayout();

        popupPath.show(mCurrentX, mCurrentY);
        layoutPath.estimateAllDirections();

        setDrawerState(popupPath);

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

    public void dissmissGrod() {

        popupGrid.dissmiss();
    }

    public void showGrid() {

        layoutGrid.refreashLayout();

        popupGrid.show(200, 200);

    }


    public void dissmissSounds() {

        popupSounds.dissmiss();
    }

    public void showSounds() {

        layoutSounds.refreashLayout();

        popupSounds.show(200, 200);

    }


    public void dissmissControls() {

        popupControls.dissmiss();
    }

    public void showControls() {

        //layoutControls.refreashLayout();

        popupControls.show(MainActivity.screenWidth, 0);

    }
}
