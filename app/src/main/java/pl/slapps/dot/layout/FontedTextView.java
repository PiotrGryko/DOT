package pl.slapps.dot.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import pl.slapps.dot.R;

/**
 * Created by piotr on 28/02/16.
 */
public class FontedTextView extends TextView {

    public FontedTextView(Context context) {
        super(context);
        setFont(false);
    }

    public FontedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FontedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.FontedTextView,
                0, 0);

        try {
            boolean bold = a.getBoolean(R.styleable.FontedTextView_bold, false);
            setFont(bold);
        } finally {
            a.recycle();
        }
    }

    public void setFont(boolean bold) {
        if (bold)
            this.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "bold.otf"));

        else
            this.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "medium.otf"));
    }
}
