package io.cloudthing.sim.connectivity.http;

import android.content.Context;
import android.util.Base64;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kleptoman on 02.09.16.
 */
public abstract class DeviceRequestFactory {


    protected final String tenant;
    protected final String deviceId;
    protected final String token;
    protected final Context ctx;
    protected Response.ErrorListener errorListener;
    protected Response.Listener listener;

    public DeviceRequestFactory(Context ctx, String deviceId, String token, String tenant) {
        this.ctx = ctx;
        this.deviceId = deviceId;
        this.token = token;
        this.tenant = tenant;
        initListeners();
    }

    public abstract Request getRequest();

    protected void initListeners() {
        listener = new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                VolleyLog.d("Everything is fine!");
            }
        };

        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ctx, "Your device credentials are not correct!", Toast.LENGTH_SHORT).show();
            }
        };
    }

    protected Map<String, String> generateHeaders() {
        Map<String, String> result = new HashMap<>();
        result.put("Content-Type", "application/json");
        result.put("Authorization", getAuthorization());
        return result;
    }

    private String getAuthorization() {
        String auth = deviceId + ":" + token;
        byte[] data = new byte[0];
        try {
            data = auth.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String base64 = Base64.encodeToString(data, Base64.DEFAULT);
        return "Basic " + base64;
    }

    protected String getUrl() {
        String urlTemplate = "https://%s.cloudthing.io:444/v1/%s/data";
        return String.format(urlTemplate, tenant, deviceId);
    }

    public void setListener(Response.Listener listener) {
        this.listener = listener;
    }

    public void setErrorListener(Response.ErrorListener errorListener) {
        this.errorListener = errorListener;
    }
}
