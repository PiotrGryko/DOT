package pl.slapps.dot.generator.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;

import pl.slapps.dot.R;

/**
 * Created by piotr on 16/02/16.
 */
public class NumberPickerTextView extends RelativeLayout {

    private TextView tvLabel;
    private OnLabelValueChanged listener;
    private float mMinValue = 0;
    private float currentValue = 0;
    private boolean isInteger = false;

    public interface OnLabelValueChanged {
        void onValueChanged(float value);
    }

    public void setListener(OnLabelValueChanged listener) {
        this.listener = listener;
    }

    public void setInteger(boolean flag) {
        this.isInteger = flag;
    }

    public void putValue(float value) {
        currentValue = value;
        if (!isInteger)
            tvLabel.setText(String.format(Locale.ENGLISH, "%.1f", value));
        else
            tvLabel.setText(String.format(Locale.ENGLISH, "%d", (int) value));


    }

    private void setValue(float value) {
        currentValue = value;
        if (!isInteger)
            tvLabel.setText(String.format(Locale.ENGLISH, "%.1f", value));
        else
            tvLabel.setText(String.format(Locale.ENGLISH, "%d", (int) value));
        if (listener != null)
            listener.onValueChanged(value);
    }

    public float getValue() {
        String value = tvLabel.getText().toString();
        try {
            float returnValue = Float.parseFloat(value);
            return returnValue;
        } catch (Throwable e) {
            e.printStackTrace();
            return currentValue;


        }

    }


    public NumberPickerTextView(Context context) {
        super(context);
        setup(context);
    }


    public NumberPickerTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context);

    }

    public NumberPickerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context);

    }

    public void setmMinValue(float value) {
        mMinValue = value;

    }

    private void setup(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.number_picker, null);
        tvLabel = (TextView) v.findViewById(R.id.tv_count);

        v.findViewById(R.id.iv_left).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                float value = getValue();
                if (value > mMinValue) {
                    if (!isInteger)
                        setValue(value - 0.1f);
                    else
                        setValue(value - 1);


                }
            }
        });


        v.findViewById(R.id.iv_right).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                float value = getValue();
                if (!isInteger)
                    setValue(value + 0.1f);
                else
                    setValue(value + 1);
            }
        });

        this.addView(v);
    }
}
