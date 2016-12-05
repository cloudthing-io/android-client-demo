package io.cloudthing.sim;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.sensisdk.DeviceManager;
import com.sensisdk.nodes.AbstractNode;


public class NodeContainerFragment extends Fragment {
    /**
     * string used for store our data in the fragment args
     */
    final static String NODE_TAG = NodeContainerFragment.class.getCanonicalName() + ".NODE_TAG";

    /**
     * true if the user ask to skip the disconnect when the fragment is destroyed
     */
    boolean userAskToKeepConnection = false;
    /**
     * progress dialog to show when we wait that the node connection
     */
    private ProgressDialog mConnectionWait;
    /**
     * node handle by this class
     */
    private AbstractNode mNode = null;

    /**
     * node listener that will manage the dialog + pass the data to the user listener if it is set
     */
    private AbstractNode.NodeStateListener mNodeStateListener = new AbstractNode.NodeStateListener() {
        @Override
        public void onStateChange(final AbstractNode node, AbstractNode.State newState, AbstractNode.State prevState) {
            final Activity activity = NodeContainerFragment.this.getActivity();
            //we connect -> hide the dialog
            if ((newState == AbstractNode.State.Connected) && activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //close the progress dialog
                        mConnectionWait.dismiss();
                    }
                });
                //error state -> show a toast message and start a new connection
            } else if ((newState == AbstractNode.State.Unreachable ||
                    newState == AbstractNode.State.Dead ||
                    newState == AbstractNode.State.Lost) && activity != null) {
                final String msg;
                switch (newState) {
                    case Dead:
                        msg = String.format(getResources().getString(
                                R.string.progressDialogConnMsgDeadNodeError),node.getName());
                        break;
                    case Unreachable:
                        msg = String.format(getResources().getString(
                                R.string.progressDialogConnMsgUnreachableNodeError),node.getName());
                        break;
                    case Lost:
                    default:
                        msg = String.format(getResources().getString(
                                R.string.progressDialogConnMsgLostNodeError),node.getName());
                        break;
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!mConnectionWait.isShowing())
                            mConnectionWait.show();
                        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
                        mNode.connect(getActivity());
                    }
                });
            }
        }
    };

    /**
     * prepare the arguments to pass to this fragment
     *
     * @param n node that this fragment has to manage
     * @return bundle to pass as argument to a NodeContainerFragment
     */
    public static Bundle prepareArguments(AbstractNode n) {
        Bundle args = new Bundle();
        args.putString(NODE_TAG, n.getTag());
        return args;
    }

    /**
     * Prepare the progress dialog tho be shown setting the title and the message
     *
     * @param nodeName name of the node that we will use
     */
    private void setUpProgressDialog(String nodeName) {
        mConnectionWait = new ProgressDialog(getActivity(), ProgressDialog.STYLE_SPINNER);
        mConnectionWait.setTitle(R.string.progressDialogConnTitle);
        mConnectionWait.setMessage(String.format(getResources().getString(R.string
                        .progressDialogConnMsg),
                nodeName));
    }

    /**
     * return the node handle by this fragment
     *
     * @return return the node handle by this fragment
     */
    public AbstractNode getNode() {
        return mNode;
    }

    /**
     * set this fragment as retain state + recover the node from the manager
     *
     * @param savedInstanceState data stored from the previous instance (not used)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        String nodeTag = getArguments().getString(NODE_TAG);
        mNode = DeviceManager.getBleManager().getNodeWithTag(nodeTag);
        if (mNode != null)
            setUpProgressDialog(mNode.getName());
    }

    /**
     * if not already connected, show the dialog and stat the connection with the node
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mNode != null && !mNode.isConnected()) {
            mConnectionWait.show(); //show the dialog and set the listener for hide it
            mNode.addNodeStateListener(mNodeStateListener);
            mNode.connect(getActivity());
        }
    }

    /**
     * if we are still connection hide the progress dialog
     */
    @Override
    public void onPause() {
        //dismiss the dialog if we are showing it
        if (mConnectionWait.isShowing()) {
            mConnectionWait.dismiss();
        }
        super.onPause();
    }

    /**
     * if true avoid to disconnect the node when the fragment is destroyed
     * @param doIt true for skip the disconnect, false for disconnect, default = false
     */
    public void keepConnectionOpen(boolean doIt) {
        userAskToKeepConnection = doIt;
    }

    /**
     * if we are connected we disconnect the node
     */
    @Override
    public void onDestroy() {
        if (mNode != null && mNode.isConnected()) {
            if (!userAskToKeepConnection) {
                mNode.removeNodeStateListener(mNodeStateListener);
                mNode.disconnect();
            }
        }
        super.onDestroy();
    }

}

