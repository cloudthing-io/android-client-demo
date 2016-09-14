package io.cloudthing.sim;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import io.cloudthing.sim.connectivity.http.HttpRequestQueue;
import io.cloudthing.sim.connectivity.http.SimpleDataRequestFactory;
import io.cloudthing.sim.utils.CredentialCache;

public class LightSensorActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor lightSensor;

    private float currentLux = 0;

    private String tenant;
    private String deviceId;
    private String token;
    private Context ctx;
    private SimpleDataRequestFactory simpleDataRequestFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_sensor);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        this.tenant = CredentialCache.getInstance().getTenant();
        this.deviceId = CredentialCache.getInstance().getDeviceId();
        this.token = CredentialCache.getInstance().getToken();
        this.ctx = this.getApplicationContext();
        prepareRequestFactory();
    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_LIGHT) {
            currentLux = event.values[0];

            TextView tenantView = (TextView) findViewById(R.id.luxValue);
            tenantView.setText(currentLux + " lux");

            simpleDataRequestFactory.setDataValue(String.valueOf(currentLux));

            HttpRequestQueue.getInstance(ctx)
                    .addToRequestQueue(simpleDataRequestFactory.getRequest());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if(sensor.getType() == Sensor.TYPE_LIGHT) {

        }
    }

    private void prepareRequestFactory() {
        simpleDataRequestFactory = new SimpleDataRequestFactory(ctx, deviceId, token, tenant);
        simpleDataRequestFactory.setErrorListener(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ctx, "Error occurred during request!", Toast.LENGTH_SHORT).show();
            }
        });

        simpleDataRequestFactory.setListener(new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                Toast.makeText(ctx, "Data has been sent!", Toast.LENGTH_SHORT).show();
            }
        });
        simpleDataRequestFactory.setDataId("ambient");
    }
}
