package io.cloudthing.sim.actions;

import android.content.Context;
import android.os.Vibrator;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import io.cloudthing.android_sdk.connectivity.mqtt.ICommandAction;

/**
 * Created by kleptoman on 14.09.16.
 */
public class VibrateAction implements ICommandAction {

    private final Context context;
    private long milis = 500;

    public VibrateAction(Context context) {
        this.context = context;
    }

    @Override
    public void execute(MqttMessage message) throws Exception {
        ((Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(milis);
    }

    public long getMilis() {
        return milis;
    }

    public void setMilis(long milis) {
        this.milis = milis;
    }
}
