package io.cloudthing.sim.connectivity.http;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kleptoman on 02.09.16.
 */
public class ManyValuesDataRequestFactory extends DeviceRequestFactory {

    private static final String BODY_TEMPLATE = "{\"r\":[%s]}";
    private static final String DATA_OBJ_TEMPLATE = "{'k':'%s','v':%s}";

    private Map<String, String> data = new HashMap<>();


    public ManyValuesDataRequestFactory(Context ctx, String deviceId, String token, String tenant) {
        super(ctx, deviceId, token, tenant);
    }

    @Override
    public Request getRequest() {
        try {
            return new SimpleDataRequest(getUrl(), getRequestBody(), listener, errorListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject getRequestBody() throws JSONException {
        StringBuilder sBuilder = new StringBuilder();
        int iter = 0;
        for (Map.Entry<String, String> dataEntry: data.entrySet()) {
            if (iter != 0) {
                sBuilder.append(',');
            }
            sBuilder.append(String.format(DATA_OBJ_TEMPLATE, dataEntry.getKey(), dataEntry.getValue()));
            iter++;
        }

        return new JSONObject(String.format(BODY_TEMPLATE, sBuilder.toString()));
    }

    public void clearData() {
        data.clear();
    }

    public void putData(String dataId, String dataValue) {
        data.put(dataId, dataValue);
    }

    private class SimpleDataRequest extends JsonObjectRequest {

        public SimpleDataRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
            super(Method.POST, url, jsonRequest, listener, errorListener);
            Log.d("appdbg","Request: " + jsonRequest.toString());
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            return generateHeaders();
        }

        @Override
        protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
            if (response.statusCode == 200 || response.statusCode == 202) {
                return Response.success(new JSONObject(), HttpHeaderParser.parseCacheHeaders(response));
            } else {
                return super.parseNetworkResponse(response);
            }
        }
    }
}
