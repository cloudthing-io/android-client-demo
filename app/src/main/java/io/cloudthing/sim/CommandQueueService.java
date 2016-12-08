package io.cloudthing.sim;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import io.cloudthing.android_sdk.connectivity.mqtt.ClientWrapper;
import io.cloudthing.android_sdk.connectivity.mqtt.ComplexCallback;
import io.cloudthing.sim.actions.VibrateAction;
import io.cloudthing.android_sdk.utils.CredentialCache;

public class CommandQueueService extends Service {

    private ClientWrapper client;
    private String topic = "v1/%s/commands/+";

    private final IBinder binder = new LocalBinder();


    public void connectToCloudThing() throws Exception {
        CredentialCache credentials = CredentialCache.getInstance();
        this.client = new ClientWrapper(credentials.getTenant(), credentials.getDeviceId(), credentials.getToken(), getApplicationContext());
        this.client.setCallback(createCallback());
        this.client.connect();
        this.client.subscribe(String.format(this.topic, credentials.getDeviceId()));

        if(!this.client.isConnected()){
            System.out.println("Not Connected");
            return;
        }
        System.out.println("Connected");
    }

    public void disconnectFromCloudThing() throws Exception {
        if (!this.client.isConnected()) {
            System.out.println("Already not connected");
            return;
        }
        CredentialCache credentials = CredentialCache.getInstance();
        this.client.unsubscribe(String.format(this.topic, credentials.getDeviceId()));
        this.client.disconnect();
    }

    private ComplexCallback createCallback() {
        ComplexCallback callback = new ComplexCallback();
        VibrateAction vibrateAction = new VibrateAction(this);
        callback.addAction("blink", vibrateAction);
        return callback;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        CommandQueueService getService() {
            return CommandQueueService.this;
        }
    }
}
