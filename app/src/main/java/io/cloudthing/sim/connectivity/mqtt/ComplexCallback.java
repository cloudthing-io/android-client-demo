package io.cloudthing.sim.connectivity.mqtt;

import android.app.Application;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kleptoman on 14.09.16.
 */
public class ComplexCallback implements MqttCallback {

    private final Map<String, ICommandAction> actions = new HashMap<>();

    @Override
    public void connectionLost(Throwable cause) {
        Log.w("MQTT-CLIENT", "Connection lost!", cause);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.v("MQTT-CLIENT", "Message: " + message.toString() + ", arrived on topic: " + topic);
        if (actions.containsKey(getCommand(topic))) {
            actions.get(getCommand(topic)).execute(message);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.v("MQTT-CLIENT", "Delivered: " + token.toString());
    }

    private String getCommand(String topic) {
        String[] splitted = topic.split("/");
        Log.i("ACTIONS", splitted[splitted.length - 1]);
        return splitted[splitted.length - 1];
    }

    public void addAction(String commandName, ICommandAction action) {
        this.actions.put(commandName, action);
    }

    public void removeAction(String commandName) {
        this.actions.remove(commandName);
    }

    public ICommandAction getAction(String commandName) {
        return this.actions.get(commandName);
    }

    public Map<String, ICommandAction> getActions() {
        return actions;
    }
}
