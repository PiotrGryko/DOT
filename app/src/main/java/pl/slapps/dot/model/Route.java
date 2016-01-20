package pl.slapps.dot.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by piotr on 09.12.15.
 */
public class Route {

    public enum Movement {
        BOTTOMRIGHT, BOTTOMLEFT, RIGHTTOP, RIGHTBOTTOM, LEFTBOTTOM, LEFTTOP, TOPLEFT, TOPRIGHT, LEFTRIGHT, RIGHTLEFT, TOPBOTTOM, BOTTOMTOP;
    }

    public enum Direction {
        LEFT, TOP, RIGHT, BOTTOM;
    }

    public enum Type {
        FINISH, START, ROUTE, TILE, BLOCK, FILL;
    }


    public String backgroundColor;
    public String sound;
    public Type type;
    public Movement next;
    public double speedRatio=1;

    public int x;
    public int y;
    public Direction from;
    public Direction to;


    public JSONObject toJson() {
        JSONObject data = new JSONObject();
        try {

            data.put("background_color", backgroundColor);
            data.put("sound",sound);
            data.put("type", type.name());
            data.put("next", next.name());
            data.put("from", from.name());
            data.put("to", to.name());
            data.put("x", x);
            data.put("y", y);
            data.put("ratio", speedRatio);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return data;
    }

    public static Route valueOf(JSONObject o) {
        Route r = new Route();


        try {
            String backgroundColor = o.has("background_color") ? o.getString("background_color") : "#3b3b3b3b";
            String sound = o.has("sound") ? o.getString("sound") : "";
            String type = o.has("type") ? o.getString("type") : "ROUTE";
            String next = o.has("next") ? o.getString("next") : "LEFTBOTTOM";

            String from = o.has("from") ? o.getString("from") : "BOTTOM";
            String to = o.has("to") ? o.getString("to") : "TOP";
            int x = o.has("x") ? o.getInt("x") : 0;
            int y = o.has("y") ? o.getInt("y") : 0;
            double ratio = o.has("ratio") ? o.getDouble("ratio") : 1;


            r.next = Movement.valueOf(next);
            r.backgroundColor = backgroundColor;
            r.sound = sound;
            r.type = Type.valueOf(type);
            r.from = Direction.valueOf(from);
            r.to = Direction.valueOf(to);
            r.x = x;
            r.y = y;
            r.speedRatio=ratio;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return r;

    }


}
