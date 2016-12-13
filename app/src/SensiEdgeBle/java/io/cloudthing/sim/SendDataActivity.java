package io.cloudthing.sim;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import io.cloudthing.android_sdk.connectivity.http.DataRequestFactory;
import io.cloudthing.android_sdk.connectivity.http.HttpRequestQueue;
import io.cloudthing.android_sdk.utils.CredentialCache;

public class SendDataActivity extends AppCompatActivity {

    private String tenant;
    private String deviceId;
    private String token;

    private Context ctx;
    private DataRequestFactory dataRequestFactory;

    private boolean serviceBound = false;
    private CommandQueueService commandService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_data);

        this.tenant = CredentialCache.getInstance().getTenant();
        this.deviceId = CredentialCache.getInstance().getDeviceId();
        this.token = CredentialCache.getInstance().getToken();
        this.ctx = this.getApplicationContext();
        prepareRequestFactory();
        setTextViews();
        Intent intent = new Intent(this, CommandQueueService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void sendMessage(View view) {
        EditText dataIdText = (EditText) findViewById(R.id.data_id);
        EditText dataValueText = (EditText) findViewById(R.id.data_value);
        if (dataIdText.getText().length() == 0 || dataValueText.getText().length() == 0) {
            Toast.makeText(this.getApplicationContext(), "Fill the input!", Toast.LENGTH_SHORT).show();
            return;
        }
        sendData(dataIdText.getText().toString(), dataValueText.getText().toString());
    }

    public void openConnectSensiEdgeBleDeviceActivity(View view) {
        Intent intent = new Intent(ctx, ConnectSensibleActivity.class);
        startActivity(intent);
    }

    private void sendData(String dataId, String dataValue) {
        dataRequestFactory.clearData();
        dataRequestFactory.putData(dataId, dataValue);

        HttpRequestQueue.getInstance(ctx)
                .addToRequestQueue(dataRequestFactory.getRequest());
    }

    private void setTextViews() {
        TextView tenantView = (TextView) findViewById(R.id.tenant);
        tenantView.setText(tenant);
        TextView deviceIdView = (TextView) findViewById(R.id.deviceId);
        deviceIdView.setText(deviceId);
        TextView tokenView = (TextView) findViewById(R.id.token);
        tokenView.setText(token);
    }

    private void prepareRequestFactory() {
        dataRequestFactory = new DataRequestFactory(ctx, deviceId, token, tenant);
        dataRequestFactory.setErrorListener(new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ctx, "Error occurred during request!", Toast.LENGTH_SHORT).show();
            }
        });

        dataRequestFactory.setListener(new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                Toast.makeText(ctx, "Data has been sent!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (serviceBound) {
            unbindService(serviceConnection);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.i("bind", "service connected");
            CommandQueueService.LocalBinder binder = (CommandQueueService.LocalBinder) service;
            commandService = binder.getService();
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.i("bind", "service disconnected");
            serviceBound = false;
        }
    };
}
