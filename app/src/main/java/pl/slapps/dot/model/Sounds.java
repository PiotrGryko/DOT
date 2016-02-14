package pl.slapps.dot.model;

import org.json.JSONException;
import org.json.JSONObject;

import pl.slapps.dot.SoundsManager;

/**
 * Created by piotr on 14/02/16.
 */
public class Sounds {

    public String soundBackground="";
    public String soundPress= SoundsManager.DEFAULT_PRESS;
    public String soundCrash=SoundsManager.DEFAULT_CRASH;
    public String soundFinish=SoundsManager.DEFAULT_FINISH;


    public JSONObject toJson() {
        JSONObject sounds = new JSONObject();
        try {
            sounds.put("finish", soundFinish);
            sounds.put("press", soundPress);
            sounds.put("crash", soundCrash);
            sounds.put("background", soundBackground);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sounds;
    }

    public static Sounds valueOf(JSONObject object) {
        Sounds returnObject = new Sounds();



        try {


            JSONObject sounds = object.has("sounds") ? object.getJSONObject("sounds") : new JSONObject();
            String soundFinish = sounds.has("finish") ? sounds.getString("finish") : "";
            String soundPress = sounds.has("press") ? sounds.getString("press") : "";
            String soundCrash = sounds.has("crash") ? sounds.getString("crash") : "";
            String soundBackground = sounds.has("background") ? sounds.getString("background") : "";


            returnObject.soundBackground = soundBackground;
            returnObject.soundCrash = soundCrash;
            returnObject.soundFinish = soundFinish;
            returnObject.soundPress = soundPress;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnObject;
    }
}
