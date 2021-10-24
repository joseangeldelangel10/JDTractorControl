package com.example.jdtractorcontrol.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jdtractorcontrol.MainActivity;
import com.example.jdtractorcontrol.R;
import com.example.jdtractorcontrol.fragments.ConnectionFragment;

import java.util.ArrayList;

public class PairedDevicesAdapter extends RecyclerView.Adapter<PairedDevicesAdapter.PairedDevicesHolder> {
    ArrayList<BluetoothDevice> devices;
    Context context;

    public PairedDevicesAdapter(ArrayList<BluetoothDevice> devices, Context context){
        this.devices = devices;
        this.context = context;
    }

    @NonNull
    @Override
    public PairedDevicesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PairedDevicesHolder(LayoutInflater.from(context).inflate(R.layout.paired_device_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PairedDevicesHolder holder, int position) {
        BluetoothDevice device = devices.get(position);
        holder.bind(device);
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    class PairedDevicesHolder extends RecyclerView.ViewHolder{
        View mainView;
        TextView deviceName;
        TextView deviceAddress;


        public PairedDevicesHolder(@NonNull View itemView) {
            super(itemView);
            mainView = itemView;
            deviceName = mainView.findViewById(R.id.PDItemName);
            deviceAddress = mainView.findViewById(R.id.PDItemAdress);
        }

        public void bind(BluetoothDevice device){
            deviceName.setText(device.getName());
            deviceAddress.setText(device.getAddress());
            mainView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity mainActivity = ((MainActivity) context);
                    FragmentManager fragmentManager = mainActivity.fragmentManager;

                    if( mainActivity.connectWith(device) == true ){
                        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, new ConnectionFragment()).commit();
                    }
                }
            });
        }
    }
}
