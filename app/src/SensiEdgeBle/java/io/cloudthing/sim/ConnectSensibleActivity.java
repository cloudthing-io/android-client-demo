package io.cloudthing.sim;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.sensisdk.BleManager;
import com.sensisdk.DeviceManager;
import com.sensisdk.nodes.AbstractNode;

public class ConnectSensibleActivity extends AppCompatActivity implements AbsListView.OnItemClickListener {

    private BleManager mBleManager;
    private NodeArrayAdapter mAdapter;

    private final static int SCAN_TIME_MS = 10 * 1000; //10sec

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_sensiedge_ble_device);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AbsListView listView = (AbsListView) findViewById(R.id.nodeListView);
        //create the adapter and set it to the list view
        mAdapter = new NodeArrayAdapter(this);
        listView.setAdapter(mAdapter);

        mBleManager = DeviceManager.getBleManager();
        mBleManager.addListener(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        listView.setOnItemClickListener(this);

        //add the already discovered nodes
        mAdapter.addAll(mBleManager.getNodes());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scan, menu);

        boolean isScanning = (mBleManager != null) && mBleManager.isDiscovering();
        menu.findItem(R.id.menu_stop_scan).setVisible(isScanning);
        menu.findItem(R.id.menu_start_scan).setVisible(!isScanning);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_start_scan) {
            if (null != mBleManager)
                mBleManager.startDiscovery(SCAN_TIME_MS, this);
            return true;
        }
        if (id == R.id.menu_stop_scan) {
            if (null != mBleManager)
                mBleManager.stopNodeDiscovery();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        AbstractNode n = mAdapter.getItem(i);
        Intent intent = FeatureListActivity.getStartIntent(this, n);
        startActivity(intent);
    }
}
