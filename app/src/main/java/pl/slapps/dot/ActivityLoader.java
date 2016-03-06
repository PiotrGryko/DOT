package pl.slapps.dot;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import pl.slapps.dot.model.Stage;
import pl.slapps.dot.model.World;

/**
 * Created by piotr on 05/03/16.
 */
public class ActivityLoader {


    private String TAG = ActivityLoader.class.getName();

    private MainActivity context;
    public ArrayList<String> sounds;
    public ArrayList<Stage> stages;

    public final String WORLDS_FILE = "worlds.json";

    public ActivityLoader(MainActivity context)
    {
        this.context=context;
        this.sounds = new ArrayList<>();
        this.stages = new ArrayList<>();
    }



    private class DownloadFile extends AsyncTask<String, Integer, String> {

        private JSONObject o;

        public DownloadFile(JSONObject object) {
            this.o = object;
        }

        @Override
        protected String doInBackground(String... arg) {
            int count;
            try {

                String path = o.has("path") ? o.getString("path") : null;
                String name = o.has("originalname") ? o.getString("originalname") : null;

                if (path == null) {
                    Log.d("aaa", "path null");
                    return null;
                }

                File f = new File(context.getCacheDir() + "/" + name);

                if (f.exists()) {
                    Log.d("aaa", "file exist");
                    return null;
                }

                URL url = new URL(DAO.url_files + path);
                URLConnection conexion = url.openConnection();
                conexion.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conexion.getContentLength();

                // downlod the file
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(f.getPath());

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    Log.d("aaa", "progress " + (total * 100 / lenghtOfFile));
                    //publishProgress((int) (total * 100 / lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
                Log.d("aaa", "file saved");
                listCatche();
            } catch (Exception e) {
                Log.d("aaa", e.toString());
            }
            return null;
        }

    }

    public void loadSounds() {


        DAO.getSounds(context, new Response.Listener() {
            @Override
            public void onResponse(Object response) {


                Log.d(TAG, response.toString());
                JSONObject object = null;
                try {
                    object = new JSONObject(response.toString());

                    object = object.has("api") ? object.getJSONObject("api") : object;

                    JSONArray array = object.has("results") ? object.getJSONArray("results") : new JSONArray();

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject o = array.getJSONObject(i);

                        new DownloadFile(o).execute();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d(TAG, error.toString());
            }
        });


    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public  ArrayList<String> listRaw() {
        ArrayList<String> files = new ArrayList<>();
        Field[] fields = R.raw.class.getFields();
        for (int count = 0; count < fields.length; count++) {

            files.add(fields[count].getName());

        }
        for (int i = 0; i < sounds.size(); i++) {
            files.add(sounds.get(i));
        }
        files.add("");
        return files;
    }

    public void listCatche() {
        sounds = new ArrayList<>();

        File f = new File(context.getCacheDir().getPath());
        File[] files1 = f.listFiles();

        for (int i = 0; i < files1.length; i++) {
            if (files1[i].getAbsolutePath().endsWith("mp3"))
                sounds.add(files1[i].getAbsolutePath());
        }


    }


    public void loadStagesFile() {
        StringBuilder returnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = context.getResources().getAssets()
                    .open(WORLDS_FILE, Context.MODE_WORLD_READABLE);
            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line = "";
            while ((line = input.readLine()) != null) {
                returnString.append(line);
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            try {
                if (isr != null)
                    isr.close();
                if (fIn != null)
                    fIn.close();
                if (input != null)
                    input.close();
            } catch (Exception e2) {
                e2.getMessage();
            }
        }

        try {
            ArrayList<World> worlds = new ArrayList<>();
            JSONObject jsonData = new JSONObject(returnString.toString());
            JSONObject api = jsonData.has("api") ? jsonData.getJSONObject("api") : new JSONObject();

            JSONArray jsonArray = api.has("results") ? api.getJSONArray("results") : new JSONArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                worlds.add(World.valueOf(jsonArray.getJSONObject(i)));
            }


            stages = new ArrayList<>();
            for (World w : worlds) {
                stages.addAll(w.stages);
            }

            Log.d(TAG, "stages loaded " + stages.size());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }

    }

}
