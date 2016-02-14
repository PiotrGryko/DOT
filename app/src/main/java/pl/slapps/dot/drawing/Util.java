package pl.slapps.dot.drawing;

import android.graphics.Color;
import android.util.Log;

/**
 * Created by piotr on 14/02/16.
 */
public class Util {

    private static String TAG = Util.class.getName();
    public static float[] parseColor(String color) {

        float[] returnValue = new float[]{0,0,0,0};
        try {
            int intColor = Color.parseColor(color);

            float a = (float) Color.alpha(intColor) / 255;
            float r = (float) Color.red(intColor) / 255;
            float g = (float) Color.green(intColor) / 255;
            float b = (float) Color.blue(intColor) / 255;
            Log.d("XXX", "color setted " + color);
            returnValue = new float[]{r,g,b,a};
        } catch (Throwable t) {
            Log.d(TAG, "background color  null " + color);
        }
        return returnValue;
    }
}
