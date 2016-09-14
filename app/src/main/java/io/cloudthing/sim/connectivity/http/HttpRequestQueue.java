package io.cloudthing.sim.connectivity.http;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by kleptoman on 02.09.16.
 */
public class HttpRequestQueue {

    private static HttpRequestQueue instance;
    private static Context ctx;

    private RequestQueue requestQueue;

    public static HttpRequestQueue getInstance(Context context) {
        if (instance == null) {
            instance = new HttpRequestQueue(context);
        }
        return instance;
    }

    private HttpRequestQueue(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
