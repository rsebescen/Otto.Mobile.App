package com.guvno.robotic;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.guvno.robotic.exceptions.BluetoothNotActivatedException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

public class BluetoothSettingsRepository {

    public BluetoothAdapter mBTAdapter;
    private static BluetoothSettingsRepository _instance;
    public static Set<BluetoothDevice> mPairedDevices;

    public BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    private final String TAG = MainActivity.class.getSimpleName();
    private ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    // #defines for identifying shared types between calling functions
    public final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    public final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    public final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    private BluetoothSettingsRepository() {

        mBTAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    public static BluetoothSettingsRepository getInstance() {
        if (_instance == null)
            _instance = new BluetoothSettingsRepository();

        return _instance;
    }


    public String[] listPairedDevices() throws BluetoothNotActivatedException {
        mPairedDevices = mBTAdapter.getBondedDevices();
        if(mBTAdapter.isEnabled()) {
            // put it's one to the adapter
            String[] devices = new String[mPairedDevices.size()];
            int i = 0;
            for (BluetoothDevice device : mPairedDevices) {
                devices[i] = device.getName() + "\n" + device.getAddress();
                i++;
            }
            return devices;
        }

        throw new BluetoothNotActivatedException();
    }

    public void connectTo(String address, String name, Handler handler) throws IOException {
        boolean fail = false;

        BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

        try {
            mBTSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            fail = true;
        }
        // Establish the Bluetooth socket connection.
        try {
            mBTSocket.connect();
        } catch (IOException e) {
            try {
                fail = true;
                mBTSocket.close();
                handler.obtainMessage(BluetoothSettingsRepository.CONNECTING_STATUS, -1, -1)
                        .sendToTarget();
            } catch (IOException e2) {
            }
        }
        if(fail) {
            throw new IOException("Socket creation failed");
        }
        else {
            mConnectedThread = new ConnectedThread(mBTSocket, handler);
            mConnectedThread.start();

            handler.obtainMessage(BluetoothSettingsRepository.CONNECTING_STATUS, 1, -1, name)
                    .sendToTarget();
        }
    }
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BTMODULEUUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection",e);
        }
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    public void send(String s) {
        try {
            mBTSocket.getOutputStream().write(s.getBytes());
            mBTSocket.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void send(Integer s) {
        try {
            mBTSocket.getOutputStream().write(s.byteValue());
            mBTSocket.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private final Handler mHandler;

        public ConnectedThread(BluetoothSocket socket, Handler handler) {
            mmSocket = socket;
            mHandler = handler;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.available();
                    if(bytes != 0) {
                        buffer = new byte[1024];
                        SystemClock.sleep(100); //pause and wait for rest of data. Adjust this depending on your sending speed.
                        bytes = mmInStream.available(); // how many bytes are ready to be read?
                        bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read
                        mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                                .sendToTarget(); // Send the obtained bytes to the UI activity
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
}
