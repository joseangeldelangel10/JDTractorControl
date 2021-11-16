package com.example.jdtractorcontrol;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.jdtractorcontrol.fragments.ConnectionFragment;
import com.example.jdtractorcontrol.fragments.RemoteControlFragment;
import com.example.jdtractorcontrol.fragments.SelectDeviceFragment;
import com.example.jdtractorcontrol.fragments.TemperatureFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private final String TAG  = "MainActivity";
    public BottomNavigationView bottomMenu;
    public Fragment fragment;
    public FragmentManager fragmentManager;
    public ArrayList<BluetoothDevice> pairedBtDevices;
    private Context context;

    private UUID mDeviceUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //esp2 32 serial port service UUID
    private BluetoothAdapter bluetoothAdapter;
    private static final int BT_ENABLE_REQUEST = 10; // This is the code we use for BT Enable
    public BluetoothDevice btDevice;
    public BluetoothSocket btConnectionSocket;
    public OutputStream btOutputStream;
    public InputStream btInputStream;
    public boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pairedBtDevices = new ArrayList<>();
        context = this;
        fragmentManager = getSupportFragmentManager();

        bottomMenu = findViewById(R.id.BottomNavigationMenu);

        /* ==============================================================================
        *                       BT SET UP
        * ============================================================================== */
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();



        /* ==============================================================================
         *                       NAVIGATION BAR FUNCTIONALITY
         * ============================================================================== */

        bottomMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.connectionMenu:
                        Log.i(TAG, "connections menu selected");
                        fragment = new SelectDeviceFragment();
                        break;
                    case R.id.temperatureMenu:
                        Log.i(TAG, "temperature menu selected");
                        fragment = new TemperatureFragment();
                        break;
                    case R.id.remoteControlMenu:
                        Log.i(TAG, "rc menu selected");
                        fragment = new RemoteControlFragment();
                        break;
                    default:
                        fragment = new SelectDeviceFragment();
                }
                fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment).commit();
                return true;
            }
        });
        bottomMenu.setSelectedItemId(R.id.connectionMenu);
        /*-----------------------------------------------------------------------------------------*/


        //bottomMenu.setBackgroundColor(getResources().getColor(R.color.strong_green));
        //getWindow().setNavigationBarColor(getResources().getColor(R.color.strong_green));

    }




    public ArrayList<BluetoothDevice> getBtDevices(){
        if (bluetoothAdapter == null) {
            Toast.makeText(context, "Bluetooth not found", Toast.LENGTH_SHORT).show();
        } else if (!bluetoothAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, BT_ENABLE_REQUEST);
        } else {
            new getJDPairedDevices().execute();
        }
        return pairedBtDevices;
    }




    public boolean connectWith(BluetoothDevice device){
        if(device != null){
            Toast.makeText(context, "connecting with " + device.getName(), Toast.LENGTH_SHORT).show();
            try {
                btConnectionSocket = device.createInsecureRfcommSocketToServiceRecord(mDeviceUUID);
                // TODO: PASS CONECTION TO NON UI THREAD
                btConnectionSocket.connect();
                Toast.makeText(context, device.getName() + " connected!", Toast.LENGTH_SHORT).show();
                btOutputStream = btConnectionSocket.getOutputStream();
                btInputStream = btConnectionSocket.getInputStream();
                btDevice = device;
                connected = true;

                btOutputStream.write(0);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "failed to connect with " + device.getName(), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return false;
    }



    /**
     * Searches for paired devices. Doesn't do a scan! Only devices which are paired through Settings->Bluetooth
     * will show up with this.
     *
     */
    public class getJDPairedDevices extends AsyncTask<Void, Void, List<BluetoothDevice>> {

        @Override
        protected List<BluetoothDevice> doInBackground(Void... params) {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            List<BluetoothDevice> listDevices = new ArrayList<BluetoothDevice>();
            for (BluetoothDevice device : pairedDevices) {
                listDevices.add(device);
            }
            return listDevices; // notice this is a list of the same paired devices
        }

        @Override
        protected void onPostExecute(List<BluetoothDevice> listDevices) {
            super.onPostExecute(listDevices);
            pairedBtDevices.clear();
            if (listDevices.size() > 0) {
                for(BluetoothDevice device:listDevices){
                    //if (device.getName().contains("ESP32")){
                    if (true){
                        pairedBtDevices.add(device);
                    }
                }
            } else {
                Toast.makeText(context, "No paired devices found, please pair your serial BT device and try again", Toast.LENGTH_SHORT).show();
            }
        }

    }



}