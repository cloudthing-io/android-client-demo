package io.cloudthing.sim;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensisdk.DeviceManagerListener;
import com.sensisdk.nodes.AbstractNode;

/**
 * Created by yuri on 06/11/16.
 */
public class NodeArrayAdapter extends ArrayAdapter<AbstractNode> implements DeviceManagerListener {

    /**
     * activity where this adapter is attached
     */
    private Activity mActivity;
    /**
     * image to show for the different boards
     */
    private Drawable mSensibleImage;

    /**
     * build the adapter
     *
     * @param context context where the adapter will be used
     */
    public NodeArrayAdapter(Activity context) {
        super(context, R.layout.node_view_item);
        mActivity = context;
        Resources res = mActivity.getResources();
        // TODO resolve getDrawable
//        mSensiBleImage = res.getDrawable(R.drawable.sensible);
    }

    /**
     * disconnect al connected node manage by this adapter
     */
    void disconnectAllNodes() {
        for (int i = 0; i < getCount(); i++) {
//            AbstractNode n = getItem(i);
//            if (n.isConnected())
//                n.disconnect();
        }
    }

    @Override
    public void onDiscoveryFinish() {
        Log.d("appdbg", "DeviceManagerListener-onDiscoveryFinish()");
        mActivity.invalidateOptionsMenu();
    }

    @Override
    public void onDiscoveryStart() {
        Log.d("appdbg", "DeviceManagerListener-onDiscoveryStart()");
        mActivity.invalidateOptionsMenu();
    }

    @Override
    public void onNodeDiscovery(final AbstractNode sensiNode) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                add(sensiNode);
            }
        });
    }

    /**
     * create a view that describe a particular node
     *
     * @param position position that have to be build
     * @param v        where store the information
     * @param parent   group where the view will be stored
     * @return a view that contains the information about the node in position \code{position}
     */
    @Override
    public View getView(int position, View v, ViewGroup parent) {

        ViewHolderItem viewHolder;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.node_view_item, parent, false);
            viewHolder = new ViewHolderItem();
            viewHolder.sensorName = (TextView) v.findViewById(R.id.nodeName);
            viewHolder.sensorTag = (TextView) v.findViewById(R.id.nodeTag);
            viewHolder.boardType = (ImageView) v.findViewById(R.id.nodeBoard);
            v.setTag(viewHolder);
        } else {
            //else -> is a recycled view -> we have only to update the values
            viewHolder = (ViewHolderItem) v.getTag();
        }

        // TODO get the corresponding sensor
        AbstractNode sensor = getItem(position);

        viewHolder.sensorName.setText(sensor.getName());
        viewHolder.sensorTag.setText(sensor.getTag());

        return v;
    }

    /**
     * class that contains view that we have to change between different items
     */
    private static class ViewHolderItem {
        TextView sensorName;
        TextView sensorTag;
        ImageView boardType;
    }

}
