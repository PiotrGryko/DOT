package pl.slapps.dot;

import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by piotr on 05/03/16.
 */
public class ActivityControls {

    private MainActivity context;



    //////////tmp
    private TextView tvMax;
    //private TextView tvMin;
    //private TextView tvCur;


    private LinearLayout layoutButtons;

    private Handler handler;


    public ActivityControls(MainActivity context, Handler handler) {
        this.context = context;
        this.handler=handler;
    }



    public LinearLayout getLayoutButtons() {
        return layoutButtons;
    }


    public void initLayout() {


        layoutButtons = (LinearLayout) context.findViewById(R.id.layout_buttons);

        tvMax = (TextView) context.findViewById(R.id.tv_max_time);
//        tvMin = (TextView) context.findViewById(R.id.tv_min_time);
//        tvCur = (TextView) context.findViewById(R.id.tv_current_time);

    }

/*
    public void setCurrent(final float value) {
        handler.post(new Runnable() {
            @Override
            public void run() {


                tvCur.setText(Integer.toString((int) value));


            }
        });
    }
*/

    public void resetLogs() {
        tvMax.setText(Integer.toString(0));
  //      tvCur.setText(Integer.toString(0));
    //    tvMin.setText(Integer.toString(0));
    }

    public void setMax(final long value) {
        handler.post(new Runnable() {
            @Override
            public void run() {


                String current = tvMax.getText().toString();

                try {
                    float v = Long.parseLong(current);
                    if (value > v)
                        tvMax.setText(Long.toString(value));
                } catch (Throwable t) {
                    tvMax.setText(Long.toString(value));

                }

            }
        });
    }
/*
    public void setMin(final long value) {
        handler.post(new Runnable() {
            @Override
            public void run() {


                String current = tvMin.getText().toString();

                try {
                    float v = Long.parseLong(current);
                    if (value < v)
                        tvMin.setText(Long.toString(value));
                } catch (Throwable t) {
                    tvMin.setText(Long.toString(value));

                }

            }
        });
    }
    */
}
