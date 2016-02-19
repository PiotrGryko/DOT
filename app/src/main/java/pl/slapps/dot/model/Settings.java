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

    public boolean toggleColors;


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
            Log.d("xxx", "load colors " + settings.toString());

            double dotLightShinning = settings.has("dot_light_shinning") ? settings.getDouble("dot_light_shinning") :returnObject.dotLightShinning;
            double dotLightDistance = settings.has("dot_light_distance") ? settings.getDouble("dot_light_distance") : returnObject.dotLightDistance;

            double explosionOneLightShinning = settings.has("explosion_one_light_shinning") ? settings.getDouble("explosion_one_light_shinning") :returnObject.explosionOneLightShinning;
            double explosionOneLightDistance = settings.has("explosion_one_light_distance") ? settings.getDouble("explosion_one_light_distance") :returnObject.explosionOneLightDistance;

            double explosionTwoLightShinning = settings.has("explosion_two_light_shinning") ? settings.getDouble("explosion_two_light_shinning") :returnObject.explosionTwoLightShinning;
            double explosionTwoLightDistance = settings.has("explosion_two_light_distance") ? settings.getDouble("explosion_two_light_distance") :returnObject.explosionTwoLightDistance;

            int explosionParticlesCount = settings.has("explosion_particles_count") ? settings.getInt("explosion_particles_count"):returnObject.explosionParticlesCount;


            returnObject.dotLightDistance=(float)dotLightDistance;
            returnObject.dotLightShinning=(float)dotLightShinning;
            returnObject.explosionOneLightDistance=(float)explosionOneLightDistance;
            returnObject.explosionOneLightShinning=(float)explosionOneLightShinning;
            returnObject.explosionTwoLightDistance=(float)explosionTwoLightDistance;
            returnObject.explosionTwoLightShinning=(float)explosionTwoLightShinning;
            returnObject.explosionParticlesCount=explosionParticlesCount;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnObject;
    }

}
