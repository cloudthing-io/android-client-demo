package io.cloudthing.sim.connectivity.mqtt;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Created by kleptoman on 14.09.16.
 */
public class ClientFactory {

    private static final String BROKER_TEMPLATE = "tcp://%s.cloudthing.io:1883";
    private static final String TOPIC = "tcp://%s.cloudthing.io:1883";

    private static ClientFactory instance;

    public static ClientFactory getInstance() {
        if (instance == null) {
            instance = new ClientFactory();
        }
        return instance;
    }

    private ClientFactory() {

    }

    public MqttClient getClient(Context context, String tenant) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        MemoryPersistence persistence = new MemoryPersistence();
        MqttClient client = null;
        try {
            client = new MqttClient(String.format(BROKER_TEMPLATE, tenant), tm.getDeviceId(), persistence);
        } catch (MqttException e) {
            Log.e("MQTT-FACTORY", "Error when creating MqttClient: " + e.getMessage(), e);
        }
        return client;
    }
}
