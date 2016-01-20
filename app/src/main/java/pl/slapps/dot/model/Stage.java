package pl.slapps.dot.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by piotr on 08.12.15.
 */
public class Stage {


    public String id;
    public String name;
    public String description;
    public int yMax;
    public int xMax;
    public String sounBackground;
    public String soundPress;
    public String soundCrash;
    public String soundFinish;

    public String worldId;
    public String colorExplosionStart;
    public String colorExplosionEnd;
    public String colorShip;
    public String colorRoute;
    public String colorBackground;
    public JSONObject sounds;

    public ArrayList<Route> routes;


    public JSONObject toJson() {
        JSONObject data = new JSONObject();
        try {
            data.put("_id", id);

            data.put("name", name);
            data.put("description", description);
            data.put("x_max", xMax);
            data.put("y_max", yMax);
            data.put("world_id", worldId);

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
            sounds.put("background", sounBackground);
            data.put("sounds", sounds);

            JSONArray route = new JSONArray();

            for (int i = 0; i < routes.size(); i++) {
                route.put(routes.get(i).toJson());
            }
            data.put("route", route);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return data;

    }

    public static Stage valueOf(JSONObject object) {

        Stage stage = new Stage();

        try {
            String id = object.has("_id") ? object.getString("_id") : "";

            String name = object.has("name") ? object.getString("name") : "";
            String description = object.has("description") ? object.getString("description") : "";
            int xMax = object.has("x_max") ? object.getInt("x_max") : 0;
            int yMax = object.has("y_max") ? object.getInt("y_max") : 0;
            String worldId = object.has("world_id") ? object.getString("world_id") : "";

            JSONObject colors = object.has("colors") ? object.getJSONObject("colors") : new JSONObject();
            String colorExplosionStart = colors.has("explosion_start") ? colors.getString("explosion_start") : "";
            String colorExplosionEnd = colors.has("explosion_end") ? colors.getString("explosion_end") : "";
            String colorRoute = colors.has("route") ? colors.getString("route") : "";
            String colorShip = colors.has("ship") ? colors.getString("ship") : "";
            String colorBackground = colors.has("background") ? colors.getString("background") : "";

            JSONObject sounds = object.has("sounds") ? object.getJSONObject("sounds") : new JSONObject();
            String soundFinish = sounds.has("finish") ? sounds.getString("finish") : "";
            String soundPress = sounds.has("press") ? sounds.getString("press") : "";
            String soundCrash = sounds.has("crash") ? sounds.getString("crash") : "";
            String soundBackground = sounds.has("background") ? sounds.getString("background") : "";


            ArrayList<Route> routes = new ArrayList<>();
            JSONArray jsonRoutes = object.has("route") ? object.getJSONArray("route") : new JSONArray();

            for (int i = 0; i < jsonRoutes.length(); i++) {
                routes.add(Route.valueOf(jsonRoutes.getJSONObject(i)));
            }

            stage.routes = routes;
            stage.id = id;
            stage.name = name;
            stage.description = description;
            stage.xMax = xMax;
            stage.yMax = yMax;
            stage.worldId = worldId;
            stage.colorBackground = colorBackground;
            stage.colorExplosionEnd = colorExplosionEnd;
            stage.colorExplosionStart = colorExplosionStart;
            stage.colorRoute = colorRoute;
            stage.colorShip = colorShip;
            stage.sounBackground = soundBackground;
            stage.soundCrash = soundCrash;
            stage.soundFinish = soundFinish;
            stage.soundPress = soundPress;
            stage.sounds = sounds;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return stage;
    }


}
