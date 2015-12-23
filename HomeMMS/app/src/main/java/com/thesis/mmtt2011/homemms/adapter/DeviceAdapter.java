package com.thesis.mmtt2011.homemms.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thesis.mmtt2011.homemms.R;
import com.thesis.mmtt2011.homemms.SSH.UtilsSSH;
import com.thesis.mmtt2011.homemms.UtilsMain;
import com.thesis.mmtt2011.homemms.implement.InstallRaspAsyncTask;
import com.thesis.mmtt2011.homemms.model.Device;
import com.thesis.mmtt2011.homemms.model.RaspberryPi;

import java.util.List;

/**
 * Created by Xpie on 10/22/2015.
 */
public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private List<Device> devices;
    public static Activity activity;
    public static UtilsMain utilsMain;

    public DeviceAdapter(List<Device> _devices, Activity activity) {
        devices = _devices;
        this.activity = activity;
        utilsMain = new UtilsMain(activity);
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
        protected ImageView imvDevice;
        private  Device mDevice;

        public DeviceViewHolder(View deviceView) {
            super(deviceView);
            tvIPAddress = (TextView)deviceView.findViewById(R.id.ipAddress);
            tvMACAddress = (TextView)deviceView.findViewById(R.id.macAddress);
            imvDevice = (ImageView)deviceView.findViewById(R.id.iconDevice);
            deviceView.setOnClickListener(this);
        }

        //bind device properties to interface
        public void bindDevice(Device device) {
            mDevice = device;
            tvIPAddress.setText(mDevice.getIPAddress());
//            tvMACAddress.setText(mDevice.getMacAddress());
            tvMACAddress.setText(mDevice.getDeviceName());
            switch (device.getDeviceType()){
                case 0:
                    imvDevice.setImageResource(R.drawable.ic_devices_white_48dp);
                    break;
                case 1:
                    imvDevice.setImageResource(R.drawable.ic_developer_board_white_48dp);
                    break;
                case 2:
                    imvDevice.setImageResource(R.drawable.ic_computer_white_48dp);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onClick(View v) {
            if (mDevice != null) {
                //check and create Pi client if it was Pi.
                if (UtilsSSH.CheckIsRaspPiDefault(mDevice)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle(R.string.install_config);
                    builder.setMessage(R.string.install_alert_message)
                            .setPositiveButton(R.string.install, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // Create the AlertDialog object and return it
                                    RaspberryPi rasp = new RaspberryPi(mDevice.getDeviceName(), mDevice.getIPAddress(), mDevice.getMacAddress());
                                    //Install config to Pi with Info of Access Point.
                                    new InstallRaspAsyncTask(activity, rasp).execute();
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else {
                    utilsMain.ShowToast("It's not Rasp Pi Default");
                }
            }
        }
    }
}
