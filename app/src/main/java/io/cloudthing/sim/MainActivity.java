package io.cloudthing.sim;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Response;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.logging.Logger;

import io.cloudthing.sim.connectivity.http.HttpRequestQueue;
import io.cloudthing.sim.connectivity.http.ValidationRequestFactory;
import io.cloudthing.sim.utils.CredentialCache;

public class MainActivity extends AppCompatActivity {
    private Context ctx;

    private static MainActivity mainActivityRunningInstance;
    private EditText mDeviceIdText;
    private EditText mTokenText;
    private EditText mTenantText;

    private CredentialsReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ctx = this.getApplicationContext();
        mReceiver = new CredentialsReceiver(new Handler());
        registerReceiver(mReceiver, new IntentFilter("cloudthingio.intent.action.credentials"));
        mainActivityRunningInstance = this;

        mDeviceIdText = (EditText) findViewById(R.id.deviceId);
        mTokenText = (EditText) findViewById(R.id.token);
        mTenantText = (EditText) findViewById(R.id.tenant);
    }

    public void confirm(View view) {

        ValidationRequestFactory validationRequestFactory = new ValidationRequestFactory(
                ctx,
                mTenantText.getText().toString(),
                mDeviceIdText.getText().toString(),
                mTokenText.getText().toString());
        Log.d("appdbg", "short_name: " + mTenantText.getText().toString());
        Log.d("appdbg", "device_id: " + mDeviceIdText.getText().toString());
        Log.d("appdbg", "token: " + mTokenText.getText().toString());
        validationRequestFactory.setListener(new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                Intent intent = new Intent(ctx, SendDataActivity.class);
                CredentialCache.getInstance().setCredentials(
                        mTenantText.getText().toString(),
                        mDeviceIdText.getText().toString(),
                        mTokenText.getText().toString());
                startActivity(intent);
            }
        });

        HttpRequestQueue.getInstance(this.getApplicationContext())
                .addToRequestQueue(validationRequestFactory.getRequest());
    }

    public void scan(View view) {
        Intent intent = new Intent(ctx, SimpleScannerActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final EditText deviceIdText = (EditText) findViewById(R.id.deviceId);
        final EditText tokenText = (EditText) findViewById(R.id.token);
        final EditText tenantText = (EditText) findViewById(R.id.tenant);
        try {
            JSONObject json = new JSONObject(data.getStringExtra("EXTRA_CT_RESULT"));
            tenantText.setText(json.getString("tenant"));
            deviceIdText.setText(json.getString("deviceId"));
            tokenText.setText(json.getString("token"));
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static class CredentialsReceiver extends BroadcastReceiver {
        private final Handler handler;
        public CredentialsReceiver(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void onReceive(final Context context, final Intent intent) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    getInstance().updateUI( intent.getStringExtra("token"),
                                            intent.getStringExtra("tenant"),
                                            intent.getStringExtra("deviceId"));
                }
            });
        }
    }

    public void updateUI(final String strToken,
                         final String strTenant,
                         final String strDeviceId) {
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                mTenantText.setText(strTenant);
                mTokenText.setText(strToken);
                mDeviceIdText.setText(strDeviceId);
            }
        });
    }

    public static MainActivity getInstance() {
        return mainActivityRunningInstance;
    }
}
