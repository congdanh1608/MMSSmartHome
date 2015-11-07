package com.thesis.mmtt2011.homemms.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thesis.mmtt2011.homemms.R;
import com.thesis.mmtt2011.homemms.model.Device;

import java.util.List;

/**
 * Created by Xpie on 10/22/2015.
 */
public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private List<Device> devices;

    public DeviceAdapter(List<Device> _devices) {
        devices = _devices;
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_card_view, parent, false);
        DeviceViewHolder deviceViewHolder = new DeviceViewHolder(v);
        return deviceViewHolder;
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder holder, int position) {
        Device device = devices.get(position);
        holder.bindDevice(device);
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView tvIPAddress;
        protected TextView tvMACAddress;
        private  Device mDevice;

        public DeviceViewHolder(View deviceView) {
            super(deviceView);
            tvIPAddress = (TextView)deviceView.findViewById(R.id.ipAddress);
            tvMACAddress = (TextView)deviceView.findViewById(R.id.macAddress);

            deviceView.setOnClickListener(this);
        }

        //bind device properties to interface
        public void bindDevice(Device device) {
            mDevice = device;
            tvIPAddress.setText(mDevice.getIPAddress());
            tvMACAddress.setText(mDevice.getMacAddress());
        }

        @Override
        public void onClick(View v) {
            if (mDevice != null) {
                //do something with device
            }
        }
    }
}
