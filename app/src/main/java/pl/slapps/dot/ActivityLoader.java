package pl.slapps.dot;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Objects;

import pl.slapps.dot.model.Stage;
import pl.slapps.dot.model.World;

/**
 * Created by piotr on 05/03/16.
 */
public class ActivityLoader {


    private String TAG = ActivityLoader.class.getName();

    private MainActivity context;
    public ArrayList<String> sounds;
    //public ArrayList<Stage> stages;
    public JSONArray jsonStages;
    public final String WORLDS_FILE = "worlds.json";
    private LoadStages stagesThread;
    private Handler handler;


    public ActivityLoader(MainActivity context, Handler handler) {
        this.context = context;
        this.sounds = new ArrayList<>();
        this.jsonStages = new JSONArray();
        this.handler = handler;

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
                    return null;
                }

                File f = new File(context.getCacheDir() + "/" + name);

                if (f.exists()) {
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
                    //publishProgress((int) (total * 100 / lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
                listCatche();
            } catch (Exception e) {
                Log.d("aaa", e.toString());
            }
            return null;
        }

    }


    public void loadSounds() {


        DAO.getSounds(new Response.Listener() {
            @Override
            public void onResponse(Object response) {


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
    public ArrayList<String> listRaw() {
        ArrayList<String> files = new ArrayList<>();
        String[] fields = new String[0];
        try {
            fields = context.getAssets().list("sounds");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("yyy",e.toString());
        }
        for (int count = 0; count < fields.length; count++) {

            files.add(fields[count]);

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

    private void saveTextFile(String text) {
        try {
            File myFile = new File(context.getCacheDir(), WORLDS_FILE);
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter =
                    new OutputStreamWriter(fOut);
            myOutWriter.append(text);
            myOutWriter.close();
            fOut.close();
            Toast.makeText(context,
                    "Done writing SD 'mysdfile.txt'",
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public interface OnStagesLoadingListener {
        public void onLoaded();

        public void onFailed();

        public void onProgress(float progress);
    }

    public void loadStagesFile(final OnStagesLoadingListener listener, final boolean assets) {
        if (assets) {
            loadStages(true, listener);
            //  if (listener != null)
            //      listener.onLoaded();
        } else {
            DAO.getWorlds(new Response.Listener() {
                @Override
                public void onResponse(Object response) {

                    saveTextFile(response.toString());
                    loadStages(false, listener);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                    loadStages(false, listener);

                }
            }, true);
        }
    }

    private InputStream getInputStream(boolean assets) {
        InputStream stream = null;
        try {
            if (assets)
                stream = context.getAssets().open("worlds.json");
            else {
                File myFile = new File(context.getCacheDir(), WORLDS_FILE);
                stream = new FileInputStream(myFile);
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }
        return stream;

    }

    public Stage getStageAtIndex(int index) {
        Stage stage = null;

        try {
            stage = Stage.valueOf(jsonStages.getJSONObject(index));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return stage;
    }

    public void onDestroy() {
        if (stagesThread != null) {
            stagesThread.cancel();
            stagesThread = null;
        }
        Runtime.getRuntime().gc();
    }

    class LoadStages extends Thread {
        StringBuilder returnString = null;
        InputStream fIn = null;

        boolean assets;
        OnStagesLoadingListener listener;
        boolean running;

        public void load(boolean assets, OnStagesLoadingListener listener) {
            this.assets = assets;
            this.listener = listener;

            start();
        }

        public void cancel() {

            interrupt();


            returnString = null;
            listener = null;
            if (fIn != null) {
                try {
                    fIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fIn = null;

            }
            stagesThread = null;

            Log.d("EEE", "Cancel internal");


        }


        public void run() {

            boolean result = false;
            running = true;

            try {
                fIn = getInputStream(assets);
                returnString = new StringBuilder();
                byte[] bytes = new byte[1000];
                int numRead = 0;
                while ((numRead = fIn.read(bytes)) >= 0) {
                    returnString.append(new String(bytes, 0, numRead));
                }

            } catch (Exception e) {
                e.getMessage();
            } finally {
                try {

                    if (fIn != null)
                        fIn.close();

                } catch (Exception e2) {
                    e2.getMessage();
                }
            }

            try {

                JSONObject jsonData = new JSONObject(returnString.toString());
                JSONObject api = jsonData.has("api") ? jsonData.getJSONObject("api") : new JSONObject();
                jsonStages = new JSONArray();
                JSONArray jsonArray = api.has("results") ? api.getJSONArray("results") : new JSONArray();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject world = jsonArray.getJSONObject(i);
                    JSONArray stages = world.has("stages") ? world.getJSONArray("stages") : new JSONArray();
                    for (int j = 0; j < stages.length(); j++) {
                        jsonStages.put(stages.getJSONObject(j));

                    }
                }

                result = true;
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
                result = false;
            }

            if (result)
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(listener!=null)
                        listener.onLoaded();
                        onDestroy();

                    }
                });
            else
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(listener!=null)
                        listener.onFailed();
                        onDestroy();
                    }
                });
            running = false;
            //clear();

        }

    }

    private void loadStages(final boolean assets, final OnStagesLoadingListener listener) {

        if (stagesThread != null) {
            stagesThread.cancel();

        }
        stagesThread = new LoadStages();
        stagesThread.load(assets, listener);


    }

}
