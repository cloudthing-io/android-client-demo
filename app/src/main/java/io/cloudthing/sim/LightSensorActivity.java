package io.cloudthing.sim;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.MqttException;

import io.cloudthing.android_sdk.connectivity.mqtt.ClientWrapper;
import io.cloudthing.android_sdk.data.DataPayload;
import io.cloudthing.android_sdk.utils.CredentialCache;

public class LightSensorActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor lightSensor;

    private float currentLux = 0;

    private String tenant;
    private String deviceId;
    private String token;
    private Context ctx;
    private ClientWrapper client;
    private String topic = "v1/%s/data?ct=json";

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
        this.client = new ClientWrapper(this.tenant, this.deviceId, this.token, getApplicationContext());
        try {
            this.client.connect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
//        prepareRequestFactory();
    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        try {
            this.client.connect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        try {
            this.client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_LIGHT) {
            currentLux = event.values[0];

            TextView tenantView = (TextView) findViewById(R.id.luxValue);
            tenantView.setText(currentLux + " lux");
            DataPayload data = new DataPayload();
            data.putData("ambient", String.valueOf(currentLux));
            try {

                this.client.publish(String.format(topic, deviceId), data);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if(sensor.getType() == Sensor.TYPE_LIGHT) {

        }
    }
}
