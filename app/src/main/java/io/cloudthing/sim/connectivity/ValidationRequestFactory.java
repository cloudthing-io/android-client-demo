package io.cloudthing.sim.connectivity;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

/**
 * Created by kleptoman on 02.09.16.
 */
public class ValidationRequestFactory extends DeviceRequestFactory {

    public ValidationRequestFactory(Context ctx, String tenant, String deviceId, String token) {
        super(ctx, deviceId, token, tenant);
    }

    @Override
    public Request getRequest() {
        if (listener == null) {
            setListener(new Response.Listener() {
                @Override
                public void onResponse(Object response) {
                    VolleyLog.d("Everything is fine!");
                }
            });
        }
        return new ValidationRequest(Request.Method.POST, getUrl(), listener, errorListener);
    }

    private class ValidationRequest extends StringRequest {

        public ValidationRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(method, url, listener, errorListener);
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            return generateHeaders();
        }
    }

}
