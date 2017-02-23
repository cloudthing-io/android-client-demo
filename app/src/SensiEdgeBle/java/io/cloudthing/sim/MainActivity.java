package io.cloudthing.sim;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import io.cloudthing.android_sdk.connectivity.http.HttpRequestQueue;
import io.cloudthing.android_sdk.connectivity.http.ValidationRequestFactory;
import io.cloudthing.android_sdk.utils.CredentialCache;

public class MainActivity extends AppCompatActivity {
    private Context ctx;

    private EditText mDeviceIdText;
    private EditText mTokenText;
    private EditText mTenantText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ctx = this.getApplicationContext();
        Intent intent = getIntent();
        String strToken = intent.getStringExtra("token");
        String strDeviceId = intent.getStringExtra("deviceId");
        String strTenant = intent.getStringExtra("tenant");
        Log.d("appdbg", "Activity intent: " + intent);

        mDeviceIdText = (EditText) findViewById(R.id.deviceId);
        mTokenText = (EditText) findViewById(R.id.token);
        mTenantText = (EditText) findViewById(R.id.tenant);
        if (null != strDeviceId)
            mDeviceIdText.setText(strDeviceId);
        if (null != strToken)
            mTokenText.setText(strToken);
        if (null != strTenant)
            mTenantText.setText(strTenant);
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
}
