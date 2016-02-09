package pl.slapps.dot;

import android.app.DownloadManager;
import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by piotr on 29.10.15.
 */
public class DAO {
    final static String TAG = DAO.class.getName();
    final static String url = "http://188.166.101.11:4321/v1/";


    public static void removeStage(Context context, Response.Listener listener, final String id) {
        RequestQueue queue = Volley.newRequestQueue(context);

        String request = url + "stage";
        if (id != null)
            request = request + "?_id=" + id;

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, request,
                listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Log.d(TAG, error.toString());
            }
        }) {


            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public static void addStage(Context context, final JSONObject stage, Response.Listener listener, final String id) {
        RequestQueue queue = Volley.newRequestQueue(context);

        String request = url + "stage";
        if (id != null)
            request = request + "?_id=" + id;

        Log.d(TAG, request);
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, request,
                listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Log.d(TAG, error.toString());
            }
        }) {

            @Override
            public byte[] getBody() {
                return stage.toString().getBytes();
            }


            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public static void getStages(Context context, Response.Listener listener, Response.ErrorListener errorListener) {
        RequestQueue queue = Volley.newRequestQueue(context);

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + "stage?find",
                listener, errorListener);
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    public static void getWorlds(Context context, Response.Listener listener, Response.ErrorListener errorListener, boolean fetchStages) {
        RequestQueue queue = Volley.newRequestQueue(context);

// Request a string response from the provided URL.
        String request = url + "world?find";
        if (fetchStages)
            request = request + "&fetchStages";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, request,
                listener, errorListener);
// Add the request to the RequestQueue.

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10*1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    public static void addWorld(Context context, final JSONObject stage, Response.Listener listener, final String id) {
        RequestQueue queue = Volley.newRequestQueue(context);

        String request = url + "world";
        if (id != null)
            request = request + "?_id=" + id;

        Log.d(TAG, request);
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, request,
                listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Log.d(TAG, error.toString());
            }
        }) {

            @Override
            public byte[] getBody() {
                return stage.toString().getBytes();
            }


            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    public static void addStageToWorld(Context context, Response.Listener listener, final String id, final String worldId) {
        RequestQueue queue = Volley.newRequestQueue(context);

        String request = url + "stage?_id=" + id;

        final JSONObject data = new JSONObject();
        try {
            data.put("world_id", worldId);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Log.d(TAG, request);
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, request,
                listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Log.d(TAG, error.toString());
            }
        }) {

            @Override
            public byte[] getBody() {
                return data.toString().getBytes();
            }


            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    public static void removeWorld(Context context, Response.Listener listener, final String id) {
        RequestQueue queue = Volley.newRequestQueue(context);

        String request = url + "world";
        if (id != null)
            request = request + "?_id=" + id;

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, request,
                listener, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Log.d(TAG, error.toString());
            }
        }) {


            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}
