package com.guvno.robotic;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.guvno.robotic.exceptions.BluetoothNotActivatedException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


public class BluetoothSettings extends Fragment {

    private TextView mBluetoothStatus;
    private TextView mReadBuffer;
    private Switch mBluetoothToggle;
    private Button mListPairedDevicesBtn;
    private Button mDiscoverBtn;
    private ArrayAdapter<String> mBTArrayAdapter;
    private Spinner mDevicesListView;

    private static Handler mHandler; // Our main handler that will receive callback fragment_notifications


    public BluetoothSettings() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_bluetooth_settings, container, false);

        mBluetoothStatus = (TextView) view.findViewById(R.id.bluetoothStatus);
        mReadBuffer = (TextView) view.findViewById(R.id.readBuffer);
        mBluetoothToggle = view.findViewById(R.id.bluetoothToggle);
        mBluetoothToggle.setChecked(BluetoothSettingsRepository.getInstance().mBTAdapter.isEnabled());
        mDiscoverBtn = (Button) view.findViewById(R.id.discover);
        mListPairedDevicesBtn = (Button) view.findViewById(R.id.PairedBtn);

        mBTArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);

        mDevicesListView = (Spinner) view.findViewById(R.id.devicesListView);
        mDevicesListView.setAdapter(mBTArrayAdapter); // assign model to view
        mDevicesListView.setOnItemSelectedListener(mDeviceClickListener);

        // Ask for location permission if not already allowed
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);


        mHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == BluetoothSettingsRepository.MESSAGE_READ) {
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    mReadBuffer.setText(readMessage);
                }

                if (msg.what == BluetoothSettingsRepository.CONNECTING_STATUS) {
                    if (msg.arg1 == 1)
                        mBluetoothStatus.setText("Connected to Device: " + (String) (msg.obj));
                    else
                        mBluetoothStatus.setText("Connection Failed");
                }
            }
        };

        if (mBTArrayAdapter == null) {
            // Device does not support Bluetooth
            mBluetoothStatus.setText("Status: Bluetooth not found");
            Toast.makeText(getActivity().getApplicationContext(), "Bluetooth device not found!", Toast.LENGTH_SHORT).show();
        } else {
            mBluetoothToggle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleBluetooth(v);
                }
            });

            mListPairedDevicesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listPairedDevices(v);
                }
            });

            mDiscoverBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    discover(v);
                }
            });
        }

        return view;
    }

    private void bluetoothOn(View view) {
        if (!BluetoothSettingsRepository.getInstance().mBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BluetoothSettingsRepository.REQUEST_ENABLE_BT);
            mBluetoothStatus.setText("Bluetooth enabled");
            Toast.makeText(getActivity().getApplicationContext(), "Bluetooth turned on", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Bluetooth is already on", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleBluetooth(View view) {

        Boolean newState = BluetoothSettingsRepository.getInstance().toggleBluetooth();
        mBluetoothStatus.setText(newState ? "Bluetooth enabled" : "Bluetooth disabled");
        Toast.makeText(getActivity().getApplicationContext(), newState ? "Bluetooth turned On" : "Bluetooth turned Off", Toast.LENGTH_SHORT).show();
    }

    private void discover(View view) {
        // Check if the device is already discovering
        if (BluetoothSettingsRepository.getInstance().mBTAdapter.isDiscovering()) {
            BluetoothSettingsRepository.getInstance().mBTAdapter.cancelDiscovery();
            Toast.makeText(getActivity().getApplicationContext(), "Discovery stopped", Toast.LENGTH_SHORT).show();
        } else {
            if (BluetoothSettingsRepository.getInstance().mBTAdapter.isEnabled()) {
                mBTArrayAdapter.clear(); // clear items
                BluetoothSettingsRepository.getInstance().mBTAdapter.startDiscovery();
                Toast.makeText(getActivity().getApplicationContext(), "Discovery started", Toast.LENGTH_SHORT).show();
                getActivity().registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            } else {
                toast("Bluetooth not on");
            }
        }
    }

    private void toast(String message) {
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name to the list
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                mBTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    private void listPairedDevices(View view) {
        mBTArrayAdapter.clear();
        try {
            for (String device : BluetoothSettingsRepository.getInstance().listPairedDevices())
                mBTArrayAdapter.add(device);
            Toast.makeText(getActivity().getApplicationContext(), "Show Paired Devices", Toast.LENGTH_SHORT).show();
        } catch (BluetoothNotActivatedException e) {
            Toast.makeText(getActivity().getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
        }
    }

    private AdapterView.OnItemSelectedListener mDeviceClickListener = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> av, View v, int arg2, long arg3) {

            if (!BluetoothSettingsRepository.getInstance().mBTAdapter.isEnabled()) {
                Toast.makeText(getActivity().getBaseContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
                return;
            }

            mBluetoothStatus.setText("Connecting...");
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0, info.length() - 17);

            // Spawn a new thread to avoid blocking the GUI one

            new Thread() {
                public void run() {
                    try {
                        synchronized (this) {
                            wait(500);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        mDevicesListView.setEnabled(false);
                                        BluetoothSettingsRepository.getInstance().connectTo(address, name, mHandler);
                                    } catch (IOException e) {
                                        Toast.makeText(getActivity().getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                    mDevicesListView.setEnabled(true);
                                }
                            });

                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

}
