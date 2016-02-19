package pl.slapps.dot.generator.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;

import pl.slapps.dot.R;

/**
 * Created by piotr on 16/02/16.
 */
public class NumberPickerTextView extends RelativeLayout {

    private TextView tvLabel;
    private OnLabelValueChanged listener;
    private DecimalFormat precision = new DecimalFormat("0.0");
    private float mMinValue = 0;
    public interface OnLabelValueChanged {
        void onValueChanged(float value);
    }

    public void setListener(OnLabelValueChanged listener) {
        this.listener = listener;
    }

    public void putValue(float value)
    {
        tvLabel.setText(precision.format(value));

    }
    private void setValue(float value) {
        tvLabel.setText(precision.format(value));
        if(listener!=null)
            listener.onValueChanged(value);
    }

    public float getValue() {
        String value = tvLabel.getText().toString();
        return Float.parseFloat(value);
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

    public void setmMinValue(float value)
    {
        mMinValue=value;
    }

    private void setup(Context context) {
        View v = LayoutInflater.from(context).inflate(R.layout.number_picker, null);
        tvLabel = (TextView) v.findViewById(R.id.tv_count);

        v.findViewById(R.id.iv_left).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                float value = getValue();
                if (value > mMinValue)
                    setValue(value - 0.1f);
            }
        });


        v.findViewById(R.id.iv_right).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                float value = getValue();

                setValue(value + 0.1f);
            }
        });

        this.addView(v);
    }
}
