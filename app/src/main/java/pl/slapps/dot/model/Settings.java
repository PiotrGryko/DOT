package pl.slapps.dot.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by piotr on 15/02/16.
 */
public class Settings {

    public float dotLightShinning=1f;
    public float dotLightDistance=0.3f;

    public float explosionOneLightShinning=1.0f;
    public float explosionOneLightDistance=0.7f;

    public float explosionTwoLightShinning=0.7f;
    public float explosionTwoLightDistance=1f;

    public int explosionParticlesCount=40;

    public boolean switchBackgroundColors=false;
    public boolean switchRouteColors=false;
    public boolean switchDotLightDistance=false;


    public float dotLightDistanceStart=0.3f;
    public float dotLightDistanceEnd=0.8f;




    public JSONObject toJson() {
        JSONObject settings = new JSONObject();
        try {
            settings.put("dot_light_shinning", dotLightShinning);
            settings.put("dot_light_distance", dotLightDistance);
            settings.put("explosion_one_light_shinning", explosionTwoLightDistance);
            settings.put("explosion_one_light_distance", explosionOneLightDistance);
            settings.put("explosion_two_light_shinning", explosionTwoLightShinning);
            settings.put("explosion_two_light_distance", explosionTwoLightDistance);
            settings.put("explosionParticlesCount", explosionParticlesCount);



            settings.put("switch_background_colors", switchBackgroundColors);
            settings.put("switch_route_colors", switchRouteColors);
            settings.put("switch_lights_distance", switchDotLightDistance);


            settings.put("dot_light_distance_start", dotLightDistanceStart);
            settings.put("dot_light_distance_end", dotLightDistanceEnd);



        } catch (JSONException e) {
            e.printStackTrace();
        }
        return settings;
    }

    public static Settings valueOf(JSONObject object)
    {
        Settings returnObject = new Settings();


        try {

            JSONObject settings = object.has("settings") ? object.getJSONObject("settings") : new JSONObject();

            double dotLightShinning = settings.has("dot_light_shinning") ? settings.getDouble("dot_light_shinning") :returnObject.dotLightShinning;
            double dotLightDistance = settings.has("dot_light_distance") ? settings.getDouble("dot_light_distance") : returnObject.dotLightDistance;

            double explosionOneLightShinning = settings.has("explosion_one_light_shinning") ? settings.getDouble("explosion_one_light_shinning") :returnObject.explosionOneLightShinning;
            double explosionOneLightDistance = settings.has("explosion_one_light_distance") ? settings.getDouble("explosion_one_light_distance") :returnObject.explosionOneLightDistance;

            double explosionTwoLightShinning = settings.has("explosion_two_light_shinning") ? settings.getDouble("explosion_two_light_shinning") :returnObject.explosionTwoLightShinning;
            double explosionTwoLightDistance = settings.has("explosion_two_light_distance") ? settings.getDouble("explosion_two_light_distance") :returnObject.explosionTwoLightDistance;

            int explosionParticlesCount = settings.has("explosion_particles_count") ? settings.getInt("explosion_particles_count"):returnObject.explosionParticlesCount;

            double dotLightDistanceStart = settings.has("dot_light_distance_start") ? settings.getDouble("dot_light_distance_start") : returnObject.dotLightDistanceStart;
            double dotLightDistanceEnd = settings.has("dot_light_distance_end") ? settings.getDouble("dot_light_distance_end") : returnObject.dotLightDistanceEnd;


            boolean switchBackgroundColors = settings.has("switch_background_colors") ? settings.getBoolean("switch_background_colors"):returnObject.switchBackgroundColors;
            boolean switchRouteColors = settings.has("switch_route_colors") ? settings.getBoolean("switch_route_colors"):returnObject.switchRouteColors;
            boolean switchLightsDistance = settings.has("switch_lights_distance") ? settings.getBoolean("switch_lights_distance"):returnObject.switchRouteColors;



            returnObject.dotLightDistance=(float)dotLightDistance;
            returnObject.dotLightShinning=(float)dotLightShinning;
            returnObject.explosionOneLightDistance=(float)explosionOneLightDistance;
            returnObject.explosionOneLightShinning=(float)explosionOneLightShinning;
            returnObject.explosionTwoLightDistance=(float)explosionTwoLightDistance;
            returnObject.explosionTwoLightShinning=(float)explosionTwoLightShinning;
            returnObject.explosionParticlesCount=explosionParticlesCount;

            returnObject.dotLightDistanceStart=(float)dotLightDistanceStart;
            returnObject.dotLightDistanceEnd=(float)dotLightDistanceEnd;


            returnObject.switchBackgroundColors=switchBackgroundColors;
            returnObject.switchRouteColors=switchRouteColors;
            returnObject.switchDotLightDistance=switchLightsDistance;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnObject;
    }

}
