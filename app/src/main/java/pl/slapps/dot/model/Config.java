package pl.slapps.dot.model;

import org.json.JSONObject;

/**
 * Created by piotr on 14/02/16.
 */
public class Config {

    public Colors colors;
    public Sounds sounds;
    public  Settings settings;

    public Config() {
        colors = new Colors();
        sounds = new Sounds();
        settings = new Settings();
    }


    public static Config valueOf(JSONObject object) {
        Config config = new Config();

        config.colors = Colors.valueOf(object);
        config.sounds = Sounds.valueOf(object);
        config.settings=Settings.valueOf(object);

        return config;
    }
}
