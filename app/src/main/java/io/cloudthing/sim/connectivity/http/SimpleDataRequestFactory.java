package io.cloudthing.sim.connectivity.http;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by kleptoman on 02.09.16.
 */
public class SimpleDataRequestFactory extends DeviceRequestFactory {

    private static final String BODY_TEMPLATE = "{\"r\":[{'k':'%s','v':%s}]}";

    private String dataId;
    private String dataValue;

    public SimpleDataRequestFactory(Context ctx, String deviceId, String token, String tenant) {
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
        return new JSONObject(String.format(BODY_TEMPLATE, dataId, dataValue));
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getDataValue() {
        return dataValue;
    }

    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }

    private class SimpleDataRequest extends JsonObjectRequest {

        public SimpleDataRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
            super(Method.POST, url, jsonRequest, listener, errorListener);
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
