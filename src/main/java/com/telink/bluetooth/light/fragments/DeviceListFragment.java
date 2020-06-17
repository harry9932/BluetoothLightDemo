package com.telink.bluetooth.light.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.telink.bluetooth.light.ConnectionStatus;
import com.telink.bluetooth.light.OnlineStatusNotificationParser;
import com.telink.bluetooth.light.R;
import com.telink.bluetooth.light.TelinkLightService;
import com.telink.bluetooth.light.activity.AddMeshActivity;
import com.telink.bluetooth.light.activity.DeviceScanningActivity;
import com.telink.bluetooth.light.activity.DeviceSettingActivity;
import com.telink.bluetooth.light.activity.OtaDeviceListActivity;
import com.telink.bluetooth.light.model.Light;
import com.telink.bluetooth.light.model.Lights;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;


public final class DeviceListFragment extends Fragment {

    private static final String TAG = DeviceListFragment.class.getSimpleName();
    private static final int UPDATE = 1;
    private LayoutInflater inflater;
    private DeviceListAdapter adapter;

    public String macAddress;

    public final static class meshtomac{
//        public static int addrmesh[];
//        public static String  addrmac[];
        public static List<Integer> addrmeshList=new ArrayList<Integer>();
        public static List<String> addrmacList=new ArrayList<String>();
    }
    private Button backView;
    private ImageView editView;
    private Button btnAllOn;
//    private Button btnAllOff;
//    private Button btnOta;

    private Activity mContext;

//    private EditText txtSendInterval;
//    private EditText txtSendNumbers;
//    private TextView txtNotifyCount;
//    private TextView btnStartOrStop;

    private int sendTotal;
    private int sendInterval;
    private long notifyCount;
    private long notifyItemCount;

    private int currentSendCount;
    private boolean isStarted;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE:
//                    txtNotifyCount.setText(notifyCount + ":" + notifyItemCount);
                    break;
            }
        }
    };
    private Runnable mTask = new Runnable() {
        @Override
        public void run() {
            currentSendCount++;
            if (currentSendCount > sendTotal) {
                stopTest();
            } else {
                if (currentSendCount % 2 == 0) {
                    byte opcode = (byte) 0xD0;
                    int address = 0xFFFF;
                    byte[] params = new byte[]{0x01, 0x00, 0x00};
                    TelinkLightService.Instance().sendCommandNoResponse(opcode, address,
                            params);
                } else {
                    byte opcode = (byte) 0xD0;
                    int address = 0xFFFF;
                    byte[] params = new byte[]{0x00, 0x00, 0x00};
                    TelinkLightService.Instance().sendCommandNoResponse(opcode, address,
                            params);
                }
                mHandler.postDelayed(mTask, sendInterval);
            }
        }
    };
    private OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            if (v == btnAllOn) {



                    Intent intent = new Intent(getActivity(),
                            DeviceSettingActivity.class);
                    intent.putExtra("meshAddress", 0xFF);
                    startActivity(intent);


            }
//            else if (v == btnAllOff) {
//                byte opcode = (byte) 0xD0;
//                int address = 0xFFFF;
//                byte[] params = new byte[]{0x00, 0x00, 0x00};
//                TelinkLightService.Instance().sendCommandNoResponse(opcode, address,
//                        params);
//            }
            else if (v == backView) {
                Intent intent = new Intent(mContext, AddMeshActivity.class);
                startActivity(intent);

            }
            else if (v == editView) {
                Intent intent = new Intent(mContext,
                        DeviceScanningActivity.class);
                startActivity(intent);
            }
//            else if (v == btnOta) {
//                Intent intent = new Intent(mContext, OtaDeviceListActivity.class);
//                startActivity(intent);
//            } else if (v == btnStartOrStop) {
//                if (!isStarted) {
//                    startTest();
//                } else {
//                    stopTest();
//                }
//            }
        }
    };
    private OnItemClickListener itemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

//            Light light = adapter.getItem(position);
//
//            if (light.status == ConnectionStatus.OFFLINE)
//                return;
//
//            int dstAddr = light.meshAddress;
//
//            Log.d(TAG, " on off " + dstAddr);
//
//            byte opcode = (byte) 0xD0;
//
//            if (light.status == ConnectionStatus.OFF) {
//
//                if (TelinkLightService.Instance().sendCommandNoResponse(opcode, dstAddr,
//                        new byte[]{0x01, 0x00, 0x00})) {
//                    light.status = ConnectionStatus.ON;
//                    light.icon = R.drawable.icon_light_on;
//                    notifyDataSetChanged();
//                }
//            } else if (light.status == ConnectionStatus.ON) {
//
//                if (TelinkLightService.Instance().sendCommandNoResponse(opcode, dstAddr,
//                        new byte[]{0x00, 0x00, 0x00})) {
//                    light.status = ConnectionStatus.OFF;
//                    light.icon = R.drawable.icon_light_off;
//                    notifyDataSetChanged();
//                }
////                TelinkLightService.Instance().sendCommandNoResponse(opcode, dstAddr,
////                        new byte[]{0x04, -1, -1,-1});
//            }
        }
    };

    private OnItemLongClickListener itemLongClickListener = new OnItemLongClickListener() {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view,
                                       int position, long id) {

//            Intent intent = new Intent(getActivity(),
//                    DeviceSettingActivity.class);
//            Light light = adapter.getItem(position);
//            for(int i=0;i<meshtomac.addrmeshList.size();i++) {
//                if (light.meshAddress == meshtomac.addrmeshList.get(i))
//                {
//                    macAddress = meshtomac.addrmacList.get(i);
//                }
//                else;
//            }
//            intent.putExtra("meshAddress", light.meshAddress);
//            //intent.putExtra("macAddress", macAddress);
//            startActivity(intent);
//            return true;
            Intent intent = new Intent(getActivity(),
                    DeviceSettingActivity.class);
            Light light = adapter.getItem(position);
            intent.putExtra("meshAddress", light.meshAddress);
            startActivity(intent);
            return true;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this.getActivity();
        this.adapter = new DeviceListAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.inflater = inflater;

        View view = inflater.inflate(R.layout.fragment_device_list, null);

        GridView listView = (GridView) view.findViewById(R.id.list_devices);

        listView.setOnItemClickListener(this.itemClickListener);
        listView.setOnItemLongClickListener(this.itemLongClickListener);
        listView.setAdapter(this.adapter);

        this.backView = (Button) view.findViewById(R.id.img_header_menu_left);
        this.backView.setOnClickListener(this.clickListener);

        this.editView = (ImageView) view
                .findViewById(R.id.img_header_menu_right);
        this.editView.setOnClickListener(this.clickListener);

        this.btnAllOn = (Button) view.findViewById(R.id.btn_on);
        this.btnAllOn.setOnClickListener(this.clickListener);

//        this.btnAllOff = (Button) view.findViewById(R.id.btn_off);
//        this.btnAllOff.setOnClickListener(this.clickListener);

//        this.btnOta = (Button) view.findViewById(R.id.btn_scene);
//        this.btnOta.setOnClickListener(this.clickListener);

//        this.txtSendInterval = (EditText) view.findViewById(R.id.sendInterval);
//        this.txtSendNumbers = (EditText) view.findViewById(R.id.sendNumbers);
//        this.txtNotifyCount = (TextView) view.findViewById(R.id.notifyCount);
//        this.btnStartOrStop = (TextView) view.findViewById(R.id.startOrStop);
//        this.btnStartOrStop.setOnClickListener(this.clickListener);
        return view;
    }

    public void addDevice(Light light) {
        this.adapter.add(light);
    }

    public Light getDevice(int meshAddress) {
        return this.adapter.get(meshAddress);
    }

    public void notifyDataSetChanged() {
        if (this.adapter != null)
            this.adapter.notifyDataSetChanged();
    }

    public void onNotify(List<OnlineStatusNotificationParser.DeviceNotificationInfo> notificationInfos) {
        if (isStarted) {
            notifyCount++;
            notifyItemCount += notificationInfos.size();
            mHandler.obtainMessage(UPDATE).sendToTarget();
        }
    }

    private void startTest() {
//        hidSoftInput(mContext, txtSendInterval.getWindowToken());
//        hidSoftInput(mContext, txtSendNumbers.getWindowToken());
        isStarted = true;
        notifyCount = 0;
        notifyItemCount = 0;
        currentSendCount = 0;

//        sendInterval = Integer.valueOf(txtSendInterval.getText().toString());
//        sendTotal = Integer.valueOf(txtSendNumbers.getText().toString());
//        txtNotifyCount.setText("");
//        btnStartOrStop.setText("stop");
        mHandler.postDelayed(mTask, sendInterval);
    }

    private void stopTest() {
        isStarted = false;
        mHandler.removeCallbacks(mTask);
//        btnStartOrStop.setText("start");
    }

    private static void hidSoftInput(Context context, IBinder token) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(token, 0);
        } catch (Exception e) {
        }
    }

    private static class DeviceItemHolder {
        public ImageView statusIcon;
        public TextView txtName;
    }

    final class DeviceListAdapter extends BaseAdapter {

        public DeviceListAdapter() {

        }

        @Override
        public int getCount() {
            return Lights.getInstance().size();
        }

        @Override
        public Light getItem(int position) {
            return Lights.getInstance().get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            DeviceItemHolder holder;

            if (convertView == null) {

                convertView = inflater.inflate(R.layout.device_item, null);

                ImageView statusIcon = (ImageView) convertView
                        .findViewById(R.id.img_icon);
                TextView txtName = (TextView) convertView
                        .findViewById(R.id.txt_name);

                holder = new DeviceItemHolder();

                holder.statusIcon = statusIcon;
                holder.txtName = txtName;

                convertView.setTag(holder);
            } else {
                holder = (DeviceItemHolder) convertView.getTag();
            }

            Light light = this.getItem(position);

            holder.txtName.setText(light.getLabel());
            holder.txtName.setTextColor(light.textColor);
            holder.statusIcon.setImageResource(light.icon);

            return convertView;
        }

        public void add(Light light) {
            Lights.getInstance().add(light);
        }

        public Light get(int meshAddress) {
            return Lights.getInstance().getByMeshAddress(meshAddress);
        }
    }
}
