package pl.slapps.dot;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
    public final String WORLDS_FILE = "worlds.json";
    private LoadStages stageThread;
    private Handler handler;

    private final String STAGES_PREFIX = "tier_";
    private final String STAGES_SUFFIX = ".json";

    private final int TIER_SIZE = 10;
    private JSONArray jsonStages;
    private int CURRENT_TIER = -1;
    private boolean assets;


    public ActivityLoader(MainActivity context, Handler handler) {
        this.context = context;
        this.sounds = new ArrayList<>();
        this.jsonStages = new JSONArray();
        this.handler = handler;

    }


    public void onDestroy() {

        if (stageThread != null) {
            stageThread.cancel();
            stageThread = null;
        }

        Runtime.getRuntime().gc();
    }
    ////////////////////////


    /**
     * SOUNDS LOADING
     */
/*
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
*/

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public ArrayList<String> listSoundsFromAssets(String dir) {
        if (dir == null || dir.equals(""))
            dir = "sounds";
        else if (!dir.equals("sounds"))
            dir = "sounds" + dir;
        ArrayList<String> files = new ArrayList<>();
        String[] fields = new String[0];
        try {
            fields = context.getAssets().list(dir);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("yyy", e.toString());
        }
        for (int count = 0; count < fields.length; count++) {

            files.add(fields[count]);

        }
        for (int i = 0; i < sounds.size(); i++) {
            files.add(sounds.get(i));
        }

        Log.e("BBB", "listing from " + dir + " listed " + Arrays.toString(fields));
        return files;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public ArrayList<String> listBackgroundsFromAssets() {
        ArrayList<String> files = new ArrayList<>();
        String[] fields = new String[0];
        try {
            fields = context.getAssets().list("backgrounds");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("yyy", e.toString());
        }
        for (int count = 0; count < fields.length; count++) {

            files.add(fields[count]);

        }
        for (int i = 0; i < sounds.size(); i++) {
            files.add(sounds.get(i));
        }
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


    ////////////////////////

    /**
     * SAVING STAGES
     */


    private void saveTextFile(String text, String filename) {
        try {
            //File myFile = new File(context.getCacheDir(), filename);
            File myFile = new File(context.getObbDir(), filename);

            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter =
                    new OutputStreamWriter(fOut);
            myOutWriter.append(text);
            myOutWriter.close();
            fOut.close();
            //  Toast.makeText(context,
            //          "Done writing SD 'mysdfile.txt'",
            //         Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Stages file saved " + filename);
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.toString());

        }
    }


    ////////////////////////

    /**
     * LOADING STAGES
     */


    public interface OnStagesLoadingListener {
        public void onLoaded();

        public void onFailed();

        public void onProgress(float progress);
    }

    public interface OnStageLoadingListener {
        public void onLoaded(Stage stage);

        public void onFailed();
    }


    public void loadStagesFile(final OnStagesLoadingListener listener, final boolean assets) {
        this.assets = assets;
        Log.d(TAG, "loding stages assets=" + assets);
        if (assets) {
            listener.onLoaded();
            //loadStages(true, listener);
            //  if (listener != null)
            //      listener.onLoaded();
        } else {
            Log.d(TAG, "making http request...");
            DAO.getWorlds(new Response.Listener() {
                @Override
                public void onResponse(Object response) {

                    Log.d(TAG, "response " + response.toString());
                    try {
                        int counter = 0;
                        int currentTier = 0;

                        JSONObject jsonData = new JSONObject(response.toString());
                        JSONObject api = jsonData.has("api") ? jsonData.getJSONObject("api") : new JSONObject();
                        jsonStages = new JSONArray();
                        JSONArray jsonArray = api.has("results") ? api.getJSONArray("results") : new JSONArray();
                        boolean saved = false;
                        ArrayList<JSONObject> tmp = new ArrayList<JSONObject>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject world = jsonArray.getJSONObject(i);
                            tmp.add(world);
                        }
                        Collections.sort(tmp, new Comparator<JSONObject>() {
                            @Override
                            public int compare(JSONObject lhs, JSONObject rhs) {
                                try {
                                    String left = lhs.has("name") ? lhs.getString("name") : "";
                                    String right = rhs.has("name") ? rhs.getString("name") : "";


                                    int l = Integer.parseInt(left);
                                    int r = Integer.parseInt(right);
                                    return l - r;
                                } catch (Throwable t) {

                                }

                                return 0;
                            }
                        });


                        for (int i = 0; i < tmp.size(); i++) {
                            JSONObject world = tmp.get(i);

                            try {
                            } catch (Throwable t) {
                            }

                            JSONArray stages = world.has("stages") ? world.getJSONArray("stages") : new JSONArray();
                            for (int j = 0; j < stages.length(); j++) {
                                jsonStages.put(stages.getJSONObject(j));

                                Log.d(TAG, "iterating .. " + j);

                                if (counter > 0 && counter % 10 == 0) {
                                    int tier = counter / 10;
                                    if (currentTier != tier) {
                                        String fileName = STAGES_PREFIX + currentTier + STAGES_SUFFIX;
                                        Log.d(TAG, "saving....");
                                        saveTextFile(jsonStages.toString(), fileName);
                                        currentTier = tier;

                                        jsonStages = new JSONArray();
                                        saved = true;
                                    }
                                }

                                counter++;


                            }
                        }
                        if (!saved) {

                            String fileName = STAGES_PREFIX + currentTier + STAGES_SUFFIX;
                            Log.d(TAG, "saving....");
                            saveTextFile(jsonStages.toString(), fileName);
                            jsonStages = new JSONArray();
                        }

                        Log.d(TAG, "STAGES STAVED!");

                        //    result = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG, e.toString());
                        ///   result = false;
                    }


                    // saveTextFile(response.toString());
                    //  loadStages(false, listener);


                    listener.onLoaded();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "on http error " + error);
                    Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                    // loadStages(false, listener);
                    // listener.onFailed();
                    listener.onLoaded();
                }
            }, true);
        }
    }

    public void getStageAtIndex(int index, OnStageLoadingListener listener) {

        Log.d(TAG, "Loading stage raw index=" + index);

        int tier = 0;
        if (index > 0)
            tier = index / 10;


        if (tier == CURRENT_TIER && listener != null) {
            if (index > 0)
                index = index % TIER_SIZE;
            try {
                Log.d(TAG, "Loading stage from current tier " + CURRENT_TIER + " stage=" + index);
                listener.onLoaded(Stage.valueOf(jsonStages.getJSONObject(index)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;

        }

        String filename = STAGES_PREFIX + tier + STAGES_SUFFIX;

        if (stageThread != null) {
            stageThread.cancel();

        }
        stageThread = new LoadStages();
        Log.d(TAG, "loading stage nr=" + index + "tier=" + tier + " from file " + filename);
        stageThread.load(listener, filename, tier, index);


    }


    private InputStream getInputStream(String filename) {
        InputStream stream = null;
        try {
            if (assets)
                stream = context.getAssets().open("stages/" + filename);
            else {
                File myFile = new File(context.getCacheDir(), filename);
                stream = new FileInputStream(myFile);
            }

        } catch (Throwable t) {
            t.printStackTrace();

        }
        return stream;

    }

    class LoadStages extends Thread {
        StringBuilder returnString = null;
        InputStream fIn = null;
        String filename;
        OnStageLoadingListener listener;
        boolean running;
        private int tier;
        private int stageNr;

        public void load(OnStageLoadingListener listener, String filename, int tier, int stageNr) {
            this.listener = listener;
            this.filename = filename;
            this.tier = tier;
            this.stageNr = stageNr;
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
            stageThread = null;

            Log.d("EEE", "Cancel internal");


        }


        public void run() {

            running = true;

            try {

                fIn = getInputStream(filename);
                returnString = new StringBuilder();
                byte[] bytes = new byte[1000];
                int numRead = 0;
                while ((numRead = fIn.read(bytes)) >= 0) {
                    returnString.append(new String(bytes, 0, numRead));
                }

            } catch (Exception e) {
                e.getMessage();
                if (listener != null)
                    listener.onFailed();
                return;
            } finally {
                try {

                    if (fIn != null)
                        fIn.close();



                } catch (Exception e2) {
                    e2.getMessage();
                }
            }


            try {

                jsonStages = new JSONArray(returnString.toString());
                final Stage stage = Stage.valueOf(jsonStages.getJSONObject(stageNr % TIER_SIZE));


                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null)
                            listener.onLoaded(stage);
                        Log.d(TAG, "stage loaded tier=" + tier + " stage nr " + stageNr + " stage in tier nr= " + stageNr % TIER_SIZE);
                        CURRENT_TIER = tier;

                        onDestroy();

                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(TAG, e.toString());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //  if(listener!=null)
                        //   listener.onFailed();
                        onDestroy();
                    }
                });
            }


            running = false;
            //clear();

        }

    }


}
