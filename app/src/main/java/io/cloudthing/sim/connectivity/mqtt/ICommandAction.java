package io.cloudthing.sim.connectivity.mqtt;

import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by kleptoman on 14.09.16.
 */
public interface ICommandAction {

    void execute(MqttMessage message) throws Exception;
}
