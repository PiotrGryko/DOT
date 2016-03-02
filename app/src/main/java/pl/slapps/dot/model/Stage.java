package pl.slapps.dot.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by piotr on 08.12.15.
 */
public class Stage {


    private static final  String TAG = Stage.class.getName();

    public String id;
    public String name;
    public String description;
    public int yMax;
    public int xMax;
    public String worldId;

    public Config config;


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


            data.put("colors", config.colors.toJson());
            data.put("sounds", config.sounds.toJson());

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


        /*
        int maxLogSize = 1000;
        for(int i = 0; i <= object.toString().length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i+1) * maxLogSize;
            end = end > object.toString().length() ? object.toString().length() : end;
            Log.v(TAG, object.toString().substring(start, end));
        }
*/
        try {
            String id = object.has("_id") ? object.getString("_id") : "";

            String name = object.has("name") ? object.getString("name") : "";
            String description = object.has("description") ? object.getString("description") : "";
            int xMax = object.has("x_max") ? object.getInt("x_max") : 0;
            int yMax = object.has("y_max") ? object.getInt("y_max") : 0;
            String worldId = object.has("world_id") ? object.getString("world_id") : "";


            Config config = Config.valueOf(object);


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
            stage.config = config;


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return stage;
    }


}
