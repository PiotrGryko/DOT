package pl.slapps.dot.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by piotr on 08.12.15.
 */
public class World {


    public String id;
    public String name;
    public String colorExplosionStart = "#ffffff";
    public String colorExplosionEnd = "#000000";
    public String colorRoute ="#3c3c3c";
    public String colorBackground = "#b3b3b3";
    public String colorShip ="#ffffff";

    public String soundFinish = "";
    public String soundCrash = "";
    public String soundPress = "";
    public String soundBackground = "";

    public ArrayList<Stage> stages = new ArrayList<>();

    public World() {
    }

    public World(ArrayList<Stage> stages) {
        this.stages = stages;
    }


    public JSONObject toJson() {
        JSONObject data = new JSONObject();
        try {
            data.put("name", name);
            data.put("_id", id);

            JSONObject colors = new JSONObject();
            colors.put("explosion_start", colorExplosionStart);
            colors.put("explosion_end", colorExplosionEnd);
            colors.put("route", colorRoute);
            colors.put("ship", colorShip);
            colors.put("background", colorBackground);
            data.put("colors", colors);

            JSONObject sounds = new JSONObject();
            sounds.put("finish", soundFinish);
            sounds.put("press", soundPress);
            sounds.put("crash", soundCrash);
            sounds.put("background", soundBackground);
            data.put("sounds", sounds);

            JSONArray stagesJson = new JSONArray();

            for (int i = 0; i < stages.size(); i++) {
                stagesJson.put(stages.get(i).toJson());
            }
            data.put("stages",stagesJson);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return data;
    }

    public static World valueOf(JSONObject o) {
        World w = new World();


        try {
            String id = o.has("_id") ? o.getString("_id") : "";

            String name = o.has("name") ? o.getString("name") : "";

            JSONObject colors = o.has("colors") ? o.getJSONObject("colors") : new JSONObject();
            String colorExplosionStart = colors.has("explosion_start") ? colors.getString("explosion_start") :w.colorExplosionStart;
            String colorExplosionEnd = colors.has("explosion_end") ? colors.getString("explosion_end") :w.colorExplosionEnd;
            String colorRoute = colors.has("route") ? colors.getString("route") :w.colorRoute;
            String colorShip = colors.has("ship") ? colors.getString("ship") : w.colorShip;
            String colorBackground = colors.has("background") ? colors.getString("background") : w.colorBackground;

            JSONObject sounds = o.has("sounds") ? o.getJSONObject("sounds") : new JSONObject();
            String soundFinish = sounds.has("finish") ? sounds.getString("finish") : "";
            String soundPress = sounds.has("press") ? sounds.getString("press") : "";
            String soundCrash = sounds.has("crash") ? sounds.getString("crash") : "";
            String soundBackground = sounds.has("background") ? sounds.getString("background") : "";

            JSONArray jsonStages = o.has("stages") ? o.getJSONArray("stages") : new JSONArray();
            ArrayList<Stage> stages = new ArrayList<>();
            for(int i=0;i<jsonStages.length();i++)
            {
                stages.add(Stage.valueOf(jsonStages.getJSONObject(i)));
            }
            w.id = id;
            w.name = name;
            w.colorBackground = !colorBackground.equals("")?colorBackground : w.colorBackground;
            w.colorExplosionEnd = !colorExplosionEnd.equals("")?colorExplosionEnd:w.colorExplosionEnd;
            w.colorExplosionStart = !colorExplosionStart.equals("")?colorExplosionStart:w.colorExplosionStart;
            w.colorRoute = !colorRoute.equals("")?colorRoute:w.colorRoute;
            w.colorShip = !colorShip.equals("")?colorShip:w.colorShip;

            w.soundFinish = soundFinish;
            w.soundPress = soundPress;
            w.soundCrash = soundCrash;
            w.soundBackground = soundBackground;
            w.stages=stages;

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return w;
    }

}
