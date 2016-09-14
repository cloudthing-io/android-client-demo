package io.cloudthing.sim;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Response;


import org.json.JSONException;
import org.json.JSONObject;

import io.cloudthing.sim.connectivity.http.HttpRequestQueue;
import io.cloudthing.sim.connectivity.http.ValidationRequestFactory;
import io.cloudthing.sim.utils.CredentialCache;

public class MainActivity extends AppCompatActivity {
    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ctx = this.getApplicationContext();
    }

    public void confirm(View view) {
        final EditText deviceIdText = (EditText) findViewById(R.id.deviceId);
        final EditText tokenText = (EditText) findViewById(R.id.token);
        final EditText tenantText = (EditText) findViewById(R.id.tenant);

        ValidationRequestFactory validationRequestFactory = new ValidationRequestFactory(
                ctx,
                tenantText.getText().toString(),
                deviceIdText.getText().toString(),
                tokenText.getText().toString());

        validationRequestFactory.setListener(new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                Intent intent = new Intent(ctx, SendDataActivity.class);
                CredentialCache.getInstance().setCredentials(
                        tenantText.getText().toString(),
                        deviceIdText.getText().toString(),
                        tokenText.getText().toString());
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
}
