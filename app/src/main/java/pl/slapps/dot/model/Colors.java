package pl.slapps.dot.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by piotr on 14/02/16.
 */
public class Colors {

    public String colorBackground="#FFFFFF";
    public String colorFence="#000000";
    public String colorShip = "#000000";
    public String colorRoute = "#8b2323";
    public String colorExplosionStart = "#000000";
    public String colorExplosionEnd = "#FFFFFF";
    public String colorExplosionLight = "#ff66ff";
    public String colorShipLight = "#FFFFFF";


    public JSONObject toJson() {
        JSONObject colors = new JSONObject();
        try {
            colors.put("explosion_start", colorExplosionStart);
            colors.put("explosion_end", colorExplosionEnd);
            colors.put("route", colorRoute);
            colors.put("ship", colorShip);
            colors.put("background", colorBackground);
            colors.put("fence", colorFence);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return colors;
    }

    public static Colors valueOf(JSONObject object) {
        Colors returnObject = new Colors();


        try {

            JSONObject colors = object.has("colors") ? object.getJSONObject("colors") : new JSONObject();
            Log.d("xxx","load colors "+colors.toString());

            String colorExplosionStart = colors.has("explosion_start") ? colors.getString("explosion_start") :returnObject.colorExplosionStart;
            String colorExplosionEnd = colors.has("explosion_end") ? colors.getString("explosion_end") : returnObject.colorExplosionEnd;
            String colorRoute = colors.has("route") ? colors.getString("route") :returnObject.colorRoute;
            String colorShip = colors.has("ship") ? colors.getString("ship") : returnObject.colorShip;
            String colorBackground = colors.has("background") ? colors.getString("background") : returnObject.colorBackground;
            String colorFence = colors.has("fence") ? colors.getString("fence") : returnObject.colorFence;


            returnObject.colorBackground = colorBackground;
            returnObject.colorExplosionEnd = colorExplosionEnd;
            returnObject.colorExplosionStart = colorExplosionStart;
            returnObject.colorRoute = colorRoute;
            returnObject.colorShip = colorShip;
            returnObject.colorFence = colorFence;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnObject;
    }
}
