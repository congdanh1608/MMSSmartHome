package com.thesis.mmtt2011.homemms.Network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import com.thesis.mmtt2011.homemms.R;
import com.thesis.mmtt2011.homemms.activity.ScanDevicesActivity;
import com.thesis.mmtt2011.homemms.adapter.DeviceAdapter;
import com.thesis.mmtt2011.homemms.model.Device;
import com.thesis.mmtt2011.homemms.model.RaspberryPiClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by CongDanh on 18/11/2015.
 */
public class ScanDevicesAsyncTask extends AsyncTask<Void, Integer, Void> {
    private Activity activity;
    ProgressDialog pd;
    private ArrayList<IpAddressWithSubnet> listOfIpAddressWithSubnet;
    private ArrayList<RaspberryPiClient> listRaspberryPiClient;
    private String startIP = null, endIP = null;
    private String nmbLookupLocation;
    private Utils utilsNetwork;

    public ScanDevicesAsyncTask(Activity activity) {
        this.activity = activity;
        pd = new ProgressDialog(activity);

        utilsNetwork = new Utils(activity);
        nmbLookupLocation = utilsNetwork.getnmbLookupLocation();
        listOfIpAddressWithSubnet = Interface.GetInterfaceAddresses();
        listRaspberryPiClient = new ArrayList<RaspberryPiClient>();
    }

    public void FindRangeOfIPSubner() {
        for (IpAddressWithSubnet ipAddressWithSubnet : listOfIpAddressWithSubnet) {
            String IPAddress = ipAddressWithSubnet.getIpAddress();
            int SubnetMask = ipAddressWithSubnet.getSubnet();
            long longIp = Utils.ipToLong(IPAddress);
            String binaryLongIp = Long.toBinaryString(longIp);
            if (binaryLongIp.length() < 32) {
                int add = 32 - binaryLongIp.length();
                for (int i = 0; i < add; i++)
                    binaryLongIp = "0" + binaryLongIp;
            }
            String startAddress = "";
            String endAddress = "";
            for (int i = 0; i < SubnetMask; i++) {
                startAddress += binaryLongIp.charAt(i);
                endAddress += binaryLongIp.charAt(i);
            }
            for (int i = 0; i < 32 - SubnetMask; i++) {
                startAddress += "0";
                endAddress += "1";
            }

            startIP = Utils.fromBinaryToIp(startAddress);
            endIP = Utils.fromBinaryToIp(endAddress);
        }
    }

    private ArrayList<String> slipIP(String hexStartAddress, String hexEndAddress) {
        int total = 0, spart = 0;
        ArrayList<String> ipSlip = new ArrayList<String>();
        total = Utils.countIP(hexStartAddress, hexEndAddress);
        //chia 2
        spart = (int) total / 4;
        // 0_1--2_3--4_5--6_7
        //0 - 1
        ipSlip.add(0, hexStartAddress);
        for (int i = 0; i < spart; i++) {
            hexStartAddress = Utils.addTwoHexToString(hexStartAddress, "01");
        }
        ipSlip.add(1, hexStartAddress);
        //2 - 3
        hexStartAddress = Utils.addTwoHexToString(hexStartAddress, "01");
        ipSlip.add(2, hexStartAddress);
        for (int i = 0; i < spart; i++) {
            hexStartAddress = Utils.addTwoHexToString(hexStartAddress, "01");
        }
        ipSlip.add(3, hexStartAddress);
        //4 - 5
        hexStartAddress = Utils.addTwoHexToString(hexStartAddress, "01");
        ipSlip.add(4, hexStartAddress);
        for (int i = 0; i < spart; i++) {
            hexStartAddress = Utils.addTwoHexToString(hexStartAddress, "01");
        }
        ipSlip.add(5, hexStartAddress);
        //6 - 7
        ipSlip.add(6, Utils.addTwoHexToString(hexStartAddress, "01"));
        ipSlip.add(7, hexEndAddress);
        return ipSlip;
    }

    private Thread ScanThread(final String start, final String end) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                String start1 = start;
                while (!start1.equals(end)) {
                    final String currentIpAddress = Utils.fromHexToIp(start1);
                    InetAddress inetAddress = null;
                    try {
                        inetAddress = InetAddress.getByName(currentIpAddress);
                        if (inetAddress.isReachable(500)) {
                            String HostName = Discover.getHostNameNmblookup(currentIpAddress, nmbLookupLocation);
                            String MacAddress = Discover.getMacFromArpCache(currentIpAddress);
                            if (HostName.contains("Unknown") && !MacAddress.contains("Unknown") && Utils.CheckMacPi(MacAddress)) {
                                HostName = "Raspberry Pi";
                                listRaspberryPiClient.add(new RaspberryPiClient(HostName, currentIpAddress, MacAddress));
                            }
                            Device device;
                            if (HostName.contains("Unknown")) {
                                device = new Device(HostName, currentIpAddress, MacAddress);
                            } else if (HostName.contains("Raspberry Pi")) {
                                device = new Device(HostName, currentIpAddress, MacAddress, 1);
                            } else {
                                device = new Device(HostName, currentIpAddress, MacAddress, 2);
                            }
                            ScanDevicesActivity.devices.add(device);
                        }
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    start1 = Utils.addTwoHexToString(start1, "01");
                }
            }
        });
        return thread;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Clear List devices
        ScanDevicesActivity.devices.clear();
        //Show ProgressDialog
        pd.setTitle("Scan device");
        pd.setMessage("Scanning. Please Wait...");
        pd.setProgressStyle(pd.STYLE_SPINNER);
//        pd.setProgress(0);
        pd.show();
        pd.setCancelable(false);
    }

    @Override
    protected Void doInBackground(Void... params) {
//        publishProgress(1, 10);
        if (listOfIpAddressWithSubnet != null) {
            FindRangeOfIPSubner();
        }

        String HexStartAddress = Utils.fromIpToHex(startIP);
        final String HexEndAddress = Utils.fromIpToHex(endIP);

        //Mutil-Thread. 4 Thread
        final ArrayList<String> ipSlip = slipIP(HexStartAddress, HexEndAddress);
        ArrayList<Thread> threads = new ArrayList<Thread>();
        int d = 0;
        for (int i = 0; i < 4; i++) {
            Thread thread;
            thread = ScanThread(ipSlip.get(d), ipSlip.get(d + 1));
            threads.add(thread);
            thread.start();
            d += 2;
//            publishProgress(1 + i, 10);
        }
        try {
            System.out.println("Waiting for threads to finish.");
            for (int i = 0; i < 4; i++) {
                threads.get(i).join();
//                publishProgress(5 + i, 10);
            }
        } catch (InterruptedException e) {
            System.out.println("Main thread Interrupted");
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
//        pd.setMax(values[1]);
//        pd.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        UpdateAdapterDevices();
        pd.dismiss();
    }

    private void UpdateAdapterDevices() {
        RecyclerView mRecyclerView = (RecyclerView) activity.findViewById(R.id.device_recycler_view);
        RecyclerView.Adapter mAdapter = new DeviceAdapter(ScanDevicesActivity.devices, activity);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
}
