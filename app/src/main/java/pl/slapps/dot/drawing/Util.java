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
            //Log.d(TAG, "color setted " + color);
            returnValue = new float[]{r,g,b,a};
        } catch (Throwable t) {
            Log.d(TAG, "background color  null " + color);
        }
        return returnValue;
    }


    public static String calculateColorsSwitch(String colorStart, String colorEnd, float progress) {
        int startColor = Color.parseColor(colorStart);
        int endColor = Color.parseColor(colorEnd);

        if (progress < 0)
            progress = 0;
        if (progress > 1)
            progress = 1;

        int startRed;
        int endRed;

        //if (Color.red(startColor) > Color.red(endColor)) {
            startRed = Color.red(startColor);
            endRed = Color.red(endColor);
        //} else {
        //    startRed = Color.red(endColor);
        //    endRed = Color.red(startColor);
        //}

        int startGreen;
        int endGreen;

        //if (Color.green(startColor) > Color.green(endColor)) {
            startGreen = Color.green(startColor);
            endGreen = Color.green(endColor);
        //} else {
        //    startGreen = Color.green(endColor);
        //    endGreen = Color.green(startColor);
        //}

        int startBlue;
        int endBlue;

        //if (Color.blue(startColor) > Color.blue(endColor)) {
            startBlue = Color.blue(startColor);
            endBlue = Color.blue(endColor);
        //} else {
        //    startBlue = Color.blue(endColor);
        //    endBlue = Color.blue(startColor);
        //}


        float redDiff = endRed - startRed;
        float greenDiff = endGreen - startGreen;
        float blueDiff = endBlue - startBlue;

        int r = (int) (progress * redDiff + startRed);
        int g = (int) (progress * greenDiff + startGreen);
        int b = (int) (progress * blueDiff + startBlue);

        int returnColor = Color.rgb(r, g, b);
        return "#" + Integer.toHexString(returnColor);
    }

}
