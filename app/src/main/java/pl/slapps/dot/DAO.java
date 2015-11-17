package pl.slapps.dot;

import android.app.DownloadManager;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by piotr on 29.10.15.
 */
public class DAO {
    final static String TAG = DAO.class.getName();
    final static String url = "http://188.166.101.11:4321/v1/stage";



    public static void removeStage(Context context, Response.Listener listener, final String id) {
        RequestQueue queue = Volley.newRequestQueue(context);

        String request = url;
        if(id!=null)
            request= request+"?_id="+id;

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

        String request = url;
        if(id!=null)
            request= request+"?_id="+id;

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
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + "?find",
                listener, errorListener);
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

}
