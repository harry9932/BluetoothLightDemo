package com.telink.bluetooth.light.fragments;

import android.app.Activity;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;


import com.telink.bluetooth.TelinkLog;
import com.telink.bluetooth.event.DeviceEvent;
import com.telink.bluetooth.event.LeScanEvent;
import com.telink.bluetooth.light.DeviceInfo;
import com.telink.bluetooth.light.LeDeleteParameters;
import com.telink.bluetooth.light.LeScanParameters;
import com.telink.bluetooth.light.LightAdapter;
import com.telink.bluetooth.light.Parameters;
import com.telink.bluetooth.light.R;
import com.telink.bluetooth.light.TelinkLightApplication;
import com.telink.bluetooth.light.TelinkLightService;
import com.telink.bluetooth.light.activity.MainActivity;
import com.telink.bluetooth.light.activity.OtaActivity;
import com.telink.bluetooth.light.base.BaseRecyclerAdapter;
import com.telink.bluetooth.light.base.MyMenuItemDecoration;
import com.telink.bluetooth.light.base.RecyclerViewHolder;


import com.telink.bluetooth.light.model.Light;
import com.telink.bluetooth.light.model.Lights;
//import com.telink.bluetooth.light.widget.ColorPicker;
import com.telink.bluetooth.light.model.Mesh;
import com.telink.bluetooth.light.model.SettingAction;
import com.telink.util.Event;
import com.telink.util.EventListener;

import java.util.ArrayList;
import java.util.List;

public final class DeviceSettingFragment extends Fragment implements View.OnClickListener, EventListener<String> {

    public final static String TAG = DeviceSettingFragment.class.getSimpleName();

    public int meshAddress;
    public String macAddress;

    private byte modify = 0;

    private int adr;

    private SeekBar temperatureBar;
    //    private ColorPicker colorPicker;
//    private Button remove;
//    private View ota;
//    private View delete;
    private AlertDialog dialog;

    private Context mContext;

    private RecyclerView rvList;
    private RecyclerView rvList2;

    private ItemAdapter mItemAdapter;
    private ItemAdapter mItemAdapter2;

    private String[] idStrings = {"1号", "2号", "3号", "4号"};


    public TelinkLightApplication apparray = new TelinkLightApplication();


    private OnSeekBarChangeListener barChangeListener = new OnSeekBarChangeListener() {

        private long preTime;
        private int delayTime = 100;


        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            this.onValueChange(seekBar, seekBar.getProgress(), true);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            this.preTime = System.currentTimeMillis();
            this.onValueChange(seekBar, seekBar.getProgress(), true);
        }


        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {

           /* if (progress % 5 != 0)
                return;

            long currentTime = System.currentTimeMillis();

            if ((currentTime - this.preTime) >= this.delayTime) {
                this.preTime = currentTime;*/
            this.onValueChange(seekBar, progress, false);
            //}
        }

        private void onValueChange(View view, int progress, boolean immediate) {

//            int addr = meshAddress;
//            int addr = macAddress;
            byte opcode;
            byte[] params;

//            if (view == brightnessBar) {
//                opcode = (byte) 0xD2;
//                params = new byte[]{(byte) progress};
//
//                TelinkLightService.Instance().sendCommandNoResponse(opcode, addr, params, immediate);
//
//            } else if (view == temperatureBar) {
//
//                opcode = (byte) 0xE2;
//                params = new byte[]{0x05, (byte) progress};
//
//                TelinkLightService.Instance().sendCommandNoResponse(opcode, addr, params, immediate);
//            }
        }
    };

    //    private ColorPicker.OnColorChangeListener colorChangedListener = new ColorPicker.OnColorChangeListener() {
//
//        private long preTime;
//        private int delayTime = 100;
//
//        @Override
//        public void onStartTrackingTouch(ColorPicker view) {
//            this.preTime = System.currentTimeMillis();
//            this.changeColor(view.getColor());
//        }
//
//        @Override
//        public void onStopTrackingTouch(ColorPicker view) {
//            this.changeColor(view.getColor());
//        }
//
//        @Override
//        public void onColorChanged(ColorPicker view, int color) {
//
//            long currentTime = System.currentTimeMillis();
//
//            if ((currentTime - this.preTime) >= this.delayTime) {
//                this.preTime = currentTime;
//                this.changeColor(color);
//            }
//        }
//
//        private void changeColor(int color) {
//
//            byte red = (byte) (color >> 16 & 0xFF);
//            byte green = (byte) (color >> 8 & 0xFF);
//            byte blue = (byte) (color & 0xFF);
//
//            int addr = meshAddress;
//            byte opcode = (byte) 0xE2;
//            byte[] params = new byte[]{0x04, red, green, blue};
//
//            TelinkLightService.Instance().sendCommandNoResponse(opcode, addr, params);
//        }
//    };
    private TelinkLightApplication mApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this.getActivity();
        this.mApp = (TelinkLightApplication) this.getActivity().getApplication();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.mApp.addEventListener(LeScanEvent.LE_SCAN, this);
        this.mApp.addEventListener(DeviceEvent.STATUS_CHANGED, this);
    }

    @Override
    public void onPause() {
        super.onPause();
        this.mApp.removeEventListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvList = (RecyclerView) view.findViewById(R.id.rv_list);
        rvList2 = (RecyclerView) view.findViewById(R.id.rv_list_2);
        List<SettingAction> actionList = new ArrayList<>();
        actionList.add(new SettingAction(getString(R.string.action_up), R.drawable.ic_action_up));
        actionList.add(new SettingAction(getString(R.string.action_stop), R.drawable.ic_action_stop));
        actionList.add(new SettingAction(getString(R.string.action_down), R.drawable.ic_action_down));
        actionList.add(new SettingAction(getString(R.string.action_light_open), R.drawable.ic_light_open));
        actionList.add(new SettingAction(getString(R.string.action_fan_open), R.drawable.ic_fan_open));
        actionList.add(new SettingAction(getString(R.string.action_socket_open), R.drawable.ic_socket_open));
        actionList.add(new SettingAction(getString(R.string.action_light_close), R.drawable.ic_light_close));
        actionList.add(new SettingAction(getString(R.string.action_fan_close), R.drawable.ic_fan_close));
        actionList.add(new SettingAction(getString(R.string.action_socket_close), R.drawable.ic_socket_close));

        List<SettingAction> actionList2 = new ArrayList<>();
        for (String id : idStrings) {
            actionList2.add(new SettingAction(id, R.drawable.ic_light_close));
        }

        mItemAdapter = new ItemAdapter(getActivity(), actionList);
        mItemAdapter2 = new ItemAdapter(getActivity(), actionList2);

        int spanCount = 3;
        int spanCount2 = 2;

        rvList.setLayoutManager(new GridLayoutManager(getActivity(), spanCount));
        rvList.addItemDecoration(new MyMenuItemDecoration(getActivity(), 2, getResources().getColor(R.color.golden_yuji)));

        rvList2.setLayoutManager(new GridLayoutManager(getActivity(), spanCount2));

        rvList.setAdapter(mItemAdapter);
        rvList2.setAdapter(mItemAdapter2);
        mItemAdapter2.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int pos) {
//                mItemAdapter2.getItem(pos).getName()
            }
        });

        mItemAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final View itemView, int pos) {
                itemView.setBackground(getActivity().getResources().getDrawable(R.color.transparent_black));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        itemView.setBackground(getActivity().getResources().getDrawable(R.drawable.menu_foreground_gray_transparent));
                    }
                },100);
                String name = mItemAdapter.getItem(pos).getName();
                if (name.equals((getString(R.string.action_up)))) {
                    btnUpOnclick();
                } else if (name.equals((getString(R.string.action_stop)))) {
                    btnStopOnclick();
                } else if (name.equals((getString(R.string.action_down)))) {
                    btnDownOnclick();
                } else if (name.equals((getString(R.string.action_light_open)))) {
                    btnLightOpenOnclick();
                } else if (name.equals((getString(R.string.action_fan_open)))) {
                    btnFanOpenOnclick();
                } else if (name.equals((getString(R.string.action_socket_open)))) {
                    btnSocketOpenOnclick();
                } else if (name.equals((getString(R.string.action_light_close)))) {
                    btnLightCloseOnclick();
                } else if (name.equals((getString(R.string.action_fan_close)))) {
                    btnFanCloseOnclick();
                } else if (name.equals((getString(R.string.action_socket_close)))) {
                    btnSocketCloseOnclick();
                }


            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


//        this.brightnessBar = (SeekBar) view.findViewById(R.id.sb_brightness);
//        this.temperatureBar = (SeekBar) view.findViewById(R.id.sb_temperature);
//
//        this.brightnessBar.setOnSeekBarChangeListener(this.barChangeListener);
//        this.temperatureBar.setOnSeekBarChangeListener(this.barChangeListener);

//        this.colorPicker = (ColorPicker) view.findViewById(R.id.color_picker);
//        this.colorPicker.setOnColorChangeListener(this.colorChangedListener);

//        this.remove = (Button) view.findViewById(R.id.btn_remove);
//        this.remove.setOnClickListener(this);

//        this.ota = view.findViewById(R.id.ota);
//        this.ota.setOnClickListener(this);

//        this.delete = view.findViewById(R.id.btn_delete);
//        this.delete.setOnClickListener(this);


        return inflater.inflate(R.layout.fragment_device_setting, null);
    }

    @Override
    public void onClick(View v) {
//        if (v == this.ota) {
//            Intent intent = new Intent(mContext, OtaActivity.class);
//            intent.putExtra("meshAddress", meshAddress);
//            startActivity(intent);
//        } else if (v == this.remove) {
//            byte opcode = (byte) 0xE3;
//            TelinkLightService.Instance().sendCommand(opcode, meshAddress, null);
//        } else if (v == this.delete) {
//            this.dialog = new AlertDialog.Builder(this.mContext).setMessage("start scan").show();
//            this.startScan();
//        }
    }

    private void startScan() {
        TelinkLightApplication mApp = (TelinkLightApplication) this.getActivity().getApplication();
        LeScanParameters params = Parameters.createScanParameters();
        com.telink.bluetooth.light.model.DeviceInfo selectedDevice = mApp.getMesh().getDevice(this.meshAddress);
        params.setMeshName(selectedDevice.meshName);
        params.setTimeoutSeconds(10);
        TelinkLightService.Instance().startScan(params);
    }

    private void delete() {
        TelinkLightApplication mApp = (TelinkLightApplication) this.getActivity().getApplication();
        Mesh currentMesh = mApp.getMesh();
        LeDeleteParameters params = Parameters.createDeleteParameters();
        params.setMeshName(currentMesh.name);
        params.setPassword(currentMesh.password);
        params.setTimeoutSeconds(10);
        com.telink.bluetooth.light.model.DeviceInfo selectedDevice = mApp.getMesh().getDevice(this.meshAddress);
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.macAddress = selectedDevice.macAddress;
        params.setDeviceInfo(deviceInfo);
        TelinkLightService.Instance().delete(params);
    }

    @Override
    public void performed(Event<String> event) {
        if (event instanceof LeScanEvent) {
            this.onLeScanEvent((LeScanEvent) event);
        } else if (event instanceof DeviceEvent) {
            this.onDeviceEvent((DeviceEvent) event);
        }
    }

    private void onLeScanEvent(LeScanEvent event) {
        String type = event.getType();

        switch (type) {
            case LeScanEvent.LE_SCAN:
                DeviceInfo deviceInfo = event.getArgs();
                com.telink.bluetooth.light.model.DeviceInfo selected = this.mApp.getMesh().getDevice(this.meshAddress);
                TelinkLog.d("LeScan : " + deviceInfo.macAddress + ":" + selected.macAddress);
                if (deviceInfo.macAddress.equals(selected.macAddress)) {
                    this.dialog.setMessage("start delete");
                    this.delete();
                }
                break;
            case LeScanEvent.LE_SCAN_TIMEOUT:
                break;
        }
    }

    private void onDeviceEvent(DeviceEvent event) {
        String type = event.getType();
        switch (type) {
            case DeviceEvent.STATUS_CHANGED:
                int status = event.getArgs().status;
                if (status == LightAdapter.STATUS_CONNECTED) {
                    this.dialog.setMessage("connected");
                } else if (status == LightAdapter.STATUS_DELETE_COMPLETED) {
                    this.dialog.setMessage("delete success");
                    this.dialog.dismiss();
                } else if (status == LightAdapter.STATUS_DELETE_FAILURE) {
                    this.dialog.setMessage("delete fail");
                }
                break;
        }
    }

    private void btnUpOnclick() {
        //点击监听

        //int addr = meshAddress;
        if (meshAddress != 0xff) {

            if (macAddress == null) {
                apparray = (TelinkLightApplication) getActivity().getApplication();
                apparray.doInit();

                Mesh mesharray = apparray.getMesh();

                if (mesharray != null && mesharray.devices != null && mesharray.devices.size() != 0) {
                    for (int i = 0; i < mesharray.devices.size(); i++) {

                        if (meshAddress == mesharray.devices.get(i).meshAddress) {
                            macAddress = mesharray.devices.get(i).macAddress;
                        }
                    }
                }

                char[] addr = macAddress.toCharArray();
                if (addr[15] >= '0' && addr[15] <= '9') {
                    adr = addr[15] - '0';
                    adr = adr << 4;
                } else if (addr[15] >= 'A' && addr[15] <= 'F') {
                    adr = addr[15] - 'A' + 0x0a;
                    adr = adr << 4;
                }
                if (addr[16] >= '0' && addr[16] <= '9') {
                    adr = adr | (addr[16] - '0');
                } else if (addr[16] >= 'A' && addr[16] <= 'F') {
                    adr = adr | (addr[16] - 'A' + 0x0a);
                }
            }
        } else {
            adr = 0xff;
        }


        modify = (byte) ((modify | 0x02) & 0xFE);

        byte[] params = new byte[]{(byte) adr, 0, modify, 0, 0, 0, 0, 0, 0, 0};
//                    byte[] params = new byte[]{(byte)adr,0,modify,0,0,0,0,0,0,0};

        byte opcode = (byte) 0xD0;
        int address = 0xFFFF;

        TelinkLightService.Instance().sendCommand(opcode, address, params);


        //TelinkLightService.Instance().sendCommandNoResponse(opcode, addr, params);

    }

    private void btnStopOnclick() {
        //点击监听

        if (meshAddress != 0xff) {

            if (macAddress == null) {
                apparray = (TelinkLightApplication) getActivity().getApplication();
                apparray.doInit();

                Mesh mesharray = apparray.getMesh();

                if (mesharray != null && mesharray.devices != null && mesharray.devices.size() != 0) {
                    for (int i = 0; i < mesharray.devices.size(); i++) {

                        if (meshAddress == mesharray.devices.get(i).meshAddress) {
                            macAddress = mesharray.devices.get(i).macAddress;
                        }
                    }
                }
                char[] addr = macAddress.toCharArray();
                if (addr[15] >= '0' && addr[15] <= '9') {
                    adr = addr[15] - '0';
                    adr = adr << 4;
                } else if (addr[15] >= 'A' && addr[15] <= 'F') {
                    adr = addr[15] - 'A' + 0x0a;
                    adr = adr << 4;
                }
                if (addr[16] >= '0' && addr[16] <= '9') {
                    adr = adr | (addr[16] - '0');
                } else if (addr[16] >= 'A' && addr[16] <= 'F') {
                    adr = adr | (addr[16] - 'A' + 0x0a);
                }
            }
        } else {
            adr = 0xff;
        }
//                int addr = meshAddress;
        modify = (byte) ((modify & 0xFD) & 0xFE);

        byte[] params = new byte[]{(byte) adr, 0, modify, 0, 0, 0, 0, 0, 0, 0};


        byte opcode = (byte) 0xD0;
        int address = 0xFFFF;

        TelinkLightService.Instance().sendCommand(opcode, address, params);
    }


    private void btnDownOnclick() {
        //点击监听

//                int addr = meshAddress;
        if (meshAddress != 0xff) {

            if (macAddress == null) {
                apparray = (TelinkLightApplication) getActivity().getApplication();
                apparray.doInit();

                Mesh mesharray = apparray.getMesh();

                if (mesharray != null && mesharray.devices != null && mesharray.devices.size() != 0) {
                    for (int i = 0; i < mesharray.devices.size(); i++) {

                        if (meshAddress == mesharray.devices.get(i).meshAddress) {
                            macAddress = mesharray.devices.get(i).macAddress;
                        }
                    }
                }
                char[] addr = macAddress.toCharArray();
                if (addr[15] >= '0' && addr[15] <= '9') {
                    adr = addr[15] - '0';
                    adr = adr << 4;
                } else if (addr[15] >= 'A' && addr[15] <= 'F') {
                    adr = addr[15] - 'A' + 0x0a;
                    adr = adr << 4;
                }
                if (addr[16] >= '0' && addr[16] <= '9') {
                    adr = adr | (addr[16] - '0');
                } else if (addr[16] >= 'A' && addr[16] <= 'F') {
                    adr = adr | (addr[16] - 'A' + 0x0a);
                }
            }
        } else {
            adr = 0xff;
        }
        modify = (byte) ((modify & 0xFD) | 0x01);

        byte[] params = new byte[]{(byte) adr, 0, modify, 0, 0, 0, 0, 0, 0, 0};


        byte opcode = (byte) 0xD0;
        int address = 0xFFFF;

        TelinkLightService.Instance().sendCommand(opcode, address, params);
    }


    private void btnFanOpenOnclick() {
        //点击监听

//                int addr = meshAddress;
        if (meshAddress != 0xff) {

            if (macAddress == null) {
                apparray = (TelinkLightApplication) getActivity().getApplication();
                apparray.doInit();

                Mesh mesharray = apparray.getMesh();

                if (mesharray != null && mesharray.devices != null && mesharray.devices.size() != 0) {
                    for (int i = 0; i < mesharray.devices.size(); i++) {

                        if (meshAddress == mesharray.devices.get(i).meshAddress) {
                            macAddress = mesharray.devices.get(i).macAddress;
                        }
                    }
                }
                char[] addr = macAddress.toCharArray();
                if (addr[15] >= '0' && addr[15] <= '9') {
                    adr = addr[15] - '0';
                    adr = adr << 4;
                } else if (addr[15] >= 'A' && addr[15] <= 'F') {
                    adr = addr[15] - 'A' + 0x0a;
                    adr = adr << 4;
                }
                if (addr[16] >= '0' && addr[16] <= '9') {
                    adr = adr | (addr[16] - '0');
                } else if (addr[16] >= 'A' && addr[16] <= 'F') {
                    adr = adr | (addr[16] - 'A' + 0x0a);
                }
            }
        } else {
            adr = 0xff;
        }

        modify = (byte) (modify | 0x08);
        byte[] params = new byte[]{(byte) adr, 0, modify, 0, 0, 0, 0, 0, 0, 0};


        byte opcode = (byte) 0xD0;

        int address = 0xFFFF;

        TelinkLightService.Instance().sendCommand(opcode, address, params);
    }


    private void btnFanCloseOnclick() {
        //点击监听

//                int addr = meshAddress;
        if (meshAddress != 0xff) {

            if (macAddress == null) {
                apparray = (TelinkLightApplication) getActivity().getApplication();
                apparray.doInit();

                Mesh mesharray = apparray.getMesh();

                if (mesharray != null && mesharray.devices != null && mesharray.devices.size() != 0) {
                    for (int i = 0; i < mesharray.devices.size(); i++) {

                        if (meshAddress == mesharray.devices.get(i).meshAddress) {
                            macAddress = mesharray.devices.get(i).macAddress;
                        }
                    }
                }
                char[] addr = macAddress.toCharArray();
                if (addr[15] >= '0' && addr[15] <= '9') {
                    adr = addr[15] - '0';
                    adr = adr << 4;
                } else if (addr[15] >= 'A' && addr[15] <= 'F') {
                    adr = addr[15] - 'A' + 0x0a;
                    adr = adr << 4;
                }
                if (addr[16] >= '0' && addr[16] <= '9') {
                    adr = adr | (addr[16] - '0');
                } else if (addr[16] >= 'A' && addr[16] <= 'F') {
                    adr = adr | (addr[16] - 'A' + 0x0a);
                }
            }
        } else {
            adr = 0xff;
        }

        modify = (byte) (modify & 0xF7);
        byte[] params = new byte[]{(byte) adr, 0, modify, 0, 0, 0, 0, 0, 0, 0};


        byte opcode = (byte) 0xD0;
        int address = 0xFFFF;

        TelinkLightService.Instance().sendCommand(opcode, address, params);
    }

    private void btnSocketOpenOnclick() {
        //点击监听

//                int addr = meshAddress;
        if (meshAddress != 0xff) {

            if (macAddress == null) {
                apparray = (TelinkLightApplication) getActivity().getApplication();
                apparray.doInit();

                Mesh mesharray = apparray.getMesh();

                if (mesharray != null && mesharray.devices != null && mesharray.devices.size() != 0) {
                    for (int i = 0; i < mesharray.devices.size(); i++) {

                        if (meshAddress == mesharray.devices.get(i).meshAddress) {
                            macAddress = mesharray.devices.get(i).macAddress;
                        }
                    }
                }
                char[] addr = macAddress.toCharArray();
                if (addr[15] >= '0' && addr[15] <= '9') {
                    adr = addr[15] - '0';
                    adr = adr << 4;
                } else if (addr[15] >= 'A' && addr[15] <= 'F') {
                    adr = addr[15] - 'A' + 0x0a;
                    adr = adr << 4;
                }
                if (addr[16] >= '0' && addr[16] <= '9') {
                    adr = adr | (addr[16] - '0');
                } else if (addr[16] >= 'A' && addr[16] <= 'F') {
                    adr = adr | (addr[16] - 'A' + 0x0a);
                }
            }
        } else {
            adr = 0xff;
        }

        modify = (byte) (modify | 0x10);
        byte[] params = new byte[]{(byte) adr, 0, modify, 0, 0, 0, 0, 0, 0, 0};


        byte opcode = (byte) 0xD0;
        int address = 0xFFFF;

        TelinkLightService.Instance().sendCommand(opcode, address, params);
    }

    private void btnSocketCloseOnclick() {
        //点击监听

//                int addr = meshAddress;
        if (meshAddress != 0xff) {

            if (macAddress == null) {
                apparray = (TelinkLightApplication) getActivity().getApplication();
                apparray.doInit();

                Mesh mesharray = apparray.getMesh();

                if (mesharray != null && mesharray.devices != null && mesharray.devices.size() != 0) {
                    for (int i = 0; i < mesharray.devices.size(); i++) {

                        if (meshAddress == mesharray.devices.get(i).meshAddress) {
                            macAddress = mesharray.devices.get(i).macAddress;
                        }
                    }
                }
                char[] addr = macAddress.toCharArray();
                if (addr[15] >= '0' && addr[15] <= '9') {
                    adr = addr[15] - '0';
                    adr = adr << 4;
                } else if (addr[15] >= 'A' && addr[15] <= 'F') {
                    adr = addr[15] - 'A' + 0x0a;
                    adr = adr << 4;
                }
                if (addr[16] >= '0' && addr[16] <= '9') {
                    adr = adr | (addr[16] - '0');
                } else if (addr[16] >= 'A' && addr[16] <= 'F') {
                    adr = adr | (addr[16] - 'A' + 0x0a);
                }
            }
        } else {
            adr = 0xff;
        }

        modify = (byte) (modify & 0xEF);
        byte[] params = new byte[]{(byte) adr, 0, modify, 0, 0, 0, 0, 0, 0, 0};


        byte opcode = (byte) 0xD0;
        int address = 0xFFFF;

        TelinkLightService.Instance().sendCommand(opcode, address, params);
    }


    private void btnLightOpenOnclick() {
        //点击监听

//                int addr = meshAddress;
        if (meshAddress != 0xff) {

            if (macAddress == null) {
                apparray = (TelinkLightApplication) getActivity().getApplication();
                apparray.doInit();

                Mesh mesharray = apparray.getMesh();

                if (mesharray != null && mesharray.devices != null && mesharray.devices.size() != 0) {
                    for (int i = 0; i < mesharray.devices.size(); i++) {

                        if (meshAddress == mesharray.devices.get(i).meshAddress) {
                            macAddress = mesharray.devices.get(i).macAddress;
                        }
                    }
                }
                char[] addr = macAddress.toCharArray();
                if (addr[15] >= '0' && addr[15] <= '9') {
                    adr = addr[15] - '0';
                    adr = adr << 4;
                } else if (addr[15] >= 'A' && addr[15] <= 'F') {
                    adr = addr[15] - 'A' + 0x0a;
                    adr = adr << 4;
                }
                if (addr[16] >= '0' && addr[16] <= '9') {
                    adr = adr | (addr[16] - '0');
                } else if (addr[16] >= 'A' && addr[16] <= 'F') {
                    adr = adr | (addr[16] - 'A' + 0x0a);
                }
            }
        } else {
            adr = 0xff;
        }

        modify = (byte) (modify | 0x04);
        byte[] params = new byte[]{(byte) adr, 0, modify, 0, 0, 0, 0, 0, 0, 0};


        byte opcode = (byte) 0xD0;
        int address = 0xFFFF;

        TelinkLightService.Instance().sendCommand(opcode, address, params);
    }


    private void btnLightCloseOnclick() {
        //点击监听

//                int addr = meshAddress;
        if (meshAddress != 0xff) {

            if (macAddress == null) {
                apparray = (TelinkLightApplication) getActivity().getApplication();
                apparray.doInit();

                Mesh mesharray = apparray.getMesh();

                if (mesharray != null && mesharray.devices != null && mesharray.devices.size() != 0) {
                    for (int i = 0; i < mesharray.devices.size(); i++) {

                        if (meshAddress == mesharray.devices.get(i).meshAddress) {
                            macAddress = mesharray.devices.get(i).macAddress;
                        }
                    }
                }
                char[] addr = macAddress.toCharArray();
                if (addr[15] >= '0' && addr[15] <= '9') {
                    adr = addr[15] - '0';
                    adr = adr << 4;
                } else if (addr[15] >= 'A' && addr[15] <= 'F') {
                    adr = addr[15] - 'A' + 0x0a;
                    adr = adr << 4;
                }
                if (addr[16] >= '0' && addr[16] <= '9') {
                    adr = adr | (addr[16] - '0');
                } else if (addr[16] >= 'A' && addr[16] <= 'F') {
                    adr = adr | (addr[16] - 'A' + 0x0a);
                }
            }
        } else {
            adr = 0xff;
        }

        modify = (byte) (modify & 0xFB);
        byte[] params = new byte[]{(byte) adr, 0, modify, 0, 0, 0, 0, 0, 0, 0};


        byte opcode = (byte) 0xD0;
        int address = 0xFFFF;

        TelinkLightService.Instance().sendCommand(opcode, address, params);
    }


    static class ItemAdapter extends BaseRecyclerAdapter<SettingAction> {

        public ItemAdapter(Context ctx, List<SettingAction> data) {
            super(ctx, data);
        }

        @Override
        public int getItemLayoutId(int viewType) {
            return R.layout.item_menu_detail;
        }

        @Override
        public void bindData(RecyclerViewHolder holder, int position, SettingAction item) {
            holder.getTextView(R.id.text).setText(item.getName());
            if (item.getIconRes() != 0) {
                holder.getImageView(R.id.img).setImageResource(item.getIconRes());
            }
        }
    }
}
