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


    public String colorSwitchBackgroundStart = "#ff66ff";
    public String colorSwitchBackgroundEnd = "#8b2323";


    public String colorSwitchRouteStart = "#ff66ff";
    public String colorSwitchRouteEnd = "#8b2323";



    public String colorSwitchDotStart = "#FFFFFF";
    public String colorSwitchDotEnd = "#FFFFFF";



    public JSONObject toJson() {
        JSONObject colors = new JSONObject();
        try {
            colors.put("explosion_start", colorExplosionStart);
            colors.put("explosion_end", colorExplosionEnd);
            colors.put("route", colorRoute);
            colors.put("ship", colorShip);
            colors.put("background", colorBackground);
            colors.put("fence", colorFence);

            colors.put("switch_background_start", colorSwitchBackgroundStart);
            colors.put("switch_background_end", colorSwitchBackgroundEnd);

            colors.put("switch_route_start", colorSwitchRouteStart);
            colors.put("switch_route_end", colorSwitchRouteEnd);

            colors.put("switch_dot_start", colorSwitchDotStart);
            colors.put("switch_dot_end", colorSwitchDotEnd);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return colors;
    }

    public static Colors valueOf(JSONObject object) {
        Colors returnObject = new Colors();


        try {

            JSONObject colors = object.has("colors") ? object.getJSONObject("colors") : new JSONObject();

            String colorExplosionStart = colors.has("explosion_start") ? colors.getString("explosion_start") :returnObject.colorExplosionStart;
            String colorExplosionEnd = colors.has("explosion_end") ? colors.getString("explosion_end") : returnObject.colorExplosionEnd;
            String colorRoute = colors.has("route") ? colors.getString("route") :returnObject.colorRoute;
            String colorShip = colors.has("ship") ? colors.getString("ship") : returnObject.colorShip;
            String colorBackground = colors.has("background") ? colors.getString("background") : returnObject.colorBackground;
            String colorFence = colors.has("fence") ? colors.getString("fence") : returnObject.colorFence;

            String switchBackgroundStart = colors.has("switch_background_start") ? colors.getString("switch_background_start") : returnObject.colorSwitchBackgroundStart;
            String switchBackgroundEnd = colors.has("switch_background_end") ? colors.getString("switch_background_end") : returnObject.colorSwitchBackgroundEnd;

            String switchRouteStart = colors.has("switch_route_start") ? colors.getString("switch_route_start") : returnObject.colorSwitchRouteStart;
            String switchRouteEnd = colors.has("switch_route_end") ? colors.getString("switch_route_end") : returnObject.colorSwitchRouteEnd;

            String switchDotStart = colors.has("switch_dot_start") ? colors.getString("switch_dot_start") : returnObject.colorSwitchDotStart;
            String switchDotEnd = colors.has("switch_dot_end") ? colors.getString("switch_dot_end") : returnObject.colorSwitchDotEnd;




            returnObject.colorBackground = colorBackground;
            returnObject.colorExplosionEnd = colorExplosionEnd;
            returnObject.colorExplosionStart = colorExplosionStart;
            returnObject.colorRoute = colorRoute;
            returnObject.colorShip = colorShip;
            returnObject.colorFence = colorFence;

            returnObject.colorSwitchBackgroundStart=switchBackgroundStart;
            returnObject.colorSwitchBackgroundEnd=switchBackgroundEnd;

            returnObject.colorSwitchRouteStart=switchRouteStart;
            returnObject.colorSwitchRouteEnd=switchRouteEnd;

            returnObject.colorSwitchDotStart=switchDotStart;
            returnObject.colorSwitchDotEnd=switchDotEnd;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnObject;
    }
}
