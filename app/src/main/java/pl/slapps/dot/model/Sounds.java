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
    public String soundCrashTwo=SoundsManager.DEFAULT_CRASH;

    public String soundFinish=SoundsManager.DEFAULT_FINISH;

    public boolean overlap;

    public JSONObject toJson() {
        JSONObject sounds = new JSONObject();
        try {
            sounds.put("finish", soundFinish);
            sounds.put("press", soundPress);
            sounds.put("crash", soundCrash);
            sounds.put("crash_two", soundCrashTwo);

            sounds.put("background", soundBackground);
            sounds.put("overlap", overlap);

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
            String soundCrashTwo = sounds.has("crash_two") ? sounds.getString("crash_two") : "";

            String soundBackground = sounds.has("background") ? sounds.getString("background") : "";
            boolean overlap = sounds.has("overlap") ? sounds.getBoolean("overlap") : false;


            returnObject.soundBackground = soundBackground;
            returnObject.soundCrash = soundCrash;
            returnObject.soundCrashTwo=soundCrashTwo;
            returnObject.soundFinish = soundFinish;
            returnObject.soundPress = soundPress;
            returnObject.overlap=overlap;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnObject;
    }
}
