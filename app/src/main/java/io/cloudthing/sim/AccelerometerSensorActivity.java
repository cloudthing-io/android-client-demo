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
import io.cloudthing.sim.connectivity.http.ManyValuesDataRequestFactory;
import io.cloudthing.sim.utils.CredentialCache;

public class AccelerometerSensorActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;

    private double accX = 0;
    private double accY = 0;
    private double accZ = 0;

    private String tenant;
    private String deviceId;
    private String token;
    private Context ctx;
    private ManyValuesDataRequestFactory manyValuesDataRequestFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer_sensor);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        this.tenant = CredentialCache.getInstance().getTenant();
        this.deviceId = CredentialCache.getInstance().getDeviceId();
        this.token = CredentialCache.getInstance().getToken();
        this.ctx = this.getApplicationContext();
        prepareRequestFactory();
    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometerSensor, 1500000);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accX = event.values[0];
            accY = event.values[1];
            accZ = event.values[2];

            setTextValues();

            manyValuesDataRequestFactory.clearData();
            manyValuesDataRequestFactory.putData("accX", String.valueOf(accX));
            manyValuesDataRequestFactory.putData("accY", String.valueOf(accY));
            manyValuesDataRequestFactory.putData("accZ", String.valueOf(accZ));

            HttpRequestQueue.getInstance(ctx)
                    .addToRequestQueue(manyValuesDataRequestFactory.getRequest());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if(sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

        }
    }

    private void setTextValues() {
        TextView accXView = (TextView) findViewById(R.id.accXView);
        accXView.setText(accX + " m/s^2");
        TextView accYView = (TextView) findViewById(R.id.accYView);
        accYView.setText(accY + " m/s^2");
        TextView accZView = (TextView) findViewById(R.id.accZView);
        accZView.setText(accZ + " m/s^2");
    }

    private void prepareRequestFactory() {
        manyValuesDataRequestFactory = new ManyValuesDataRequestFactory(ctx, deviceId, token, tenant);
        manyValuesDataRequestFactory.setErrorListener(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ctx, "Error occurred during request!", Toast.LENGTH_SHORT).show();
            }
        });

        manyValuesDataRequestFactory.setListener(new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                Toast.makeText(ctx, "Data has been sent!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
