package com.telink.bluetooth.light.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.telink.bluetooth.light.model.Mesh;

import com.telink.bluetooth.light.model.Light;
import com.telink.bluetooth.light.model.Lights;
//import com.telink.bluetooth.light.widget.ColorPicker;
import com.telink.util.Event;
import com.telink.util.EventListener;

public final class DeviceSettingFragment extends Fragment implements View.OnClickListener, EventListener<String> {

    public final static String TAG = DeviceSettingFragment.class.getSimpleName();

    public int meshAddress;
    public String macAddress;

    private byte modify=0;

    private int adr;

    private SeekBar temperatureBar;
//    private ColorPicker colorPicker;
//    private Button remove;
//    private View ota;
//    private View delete;
    private AlertDialog dialog;

    private Context mContext;

    private Button btn_up;
    private Button btn_stop;
    private Button btn_down;

    private Button btn_lighting_open;
    private Button btn_lighting_close;
    private Button btn_fan_open;
    private Button btn_fan_close;
    private Button btn_socket_open;
    private Button btn_socket_close;

    public TelinkLightApplication apparray=new TelinkLightApplication();



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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_device_setting, null);





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

        this.btn_up=(Button) view.findViewById(R.id.button_up);//绑定ID
        btn_up.setOnClickListener(new View.OnClickListener() {


                private long preTime;
                private int delayTime = 100;
                @Override
                public void onClick(View v) {
                    //点击监听

                    //int addr = meshAddress;
                    if(meshAddress != 0xff)
                    {

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
                            if (addr[13] >= '0' && addr[13] <= '9') {
                                adr = addr[13] - '0';
                                adr = adr << 12;
                            } else if (addr[13] >= 'A' && addr[13] <= 'F') {
                                adr = addr[13] - 'A' + 0x0a;
                                adr = adr << 12;
                            }
                            if (addr[14] >= '0' && addr[14] <= '9') {
                                adr = adr | ((addr[14] - '0') << 8);
                            } else if (addr[14] >= 'A' && addr[14] <= 'F') {
                                adr = adr | ((addr[14] - 'A' + 0x0a) << 8);
                            }
                            if (addr[15] >= '0' && addr[15] <= '9') {
                                adr = adr | ((addr[15] - '0') << 4);
                            } else if (addr[15] >= 'A' && addr[15] <= 'F') {
                                adr = adr | ((addr[15] - 'A' + 0x0a) << 4);
                            }
                            if (addr[16] >= '0' && addr[16] <= '9') {
                                adr = adr | (addr[16] - '0');
                            } else if (addr[16] >= 'A' && addr[16] <= 'F') {
                                adr = adr | (addr[16] - 'A' + 0x0a);
                            }
                        }
                    }
                    else
                    {
                        adr = 0xff;
                    }






                    modify = (byte)((modify | 0x02) & 0xFE);

                    byte[] params = new byte[]{(byte)adr,0,modify,0,0,0,0,0,0,0};
//                    byte[] params = new byte[]{(byte)adr,0,modify,0,0,0,0,0,0,0};

                    byte opcode = (byte) 0xD0;
                    int address = 0xFFFF;

                    TelinkLightService.Instance().sendCommand(opcode, address, params);


                    //TelinkLightService.Instance().sendCommandNoResponse(opcode, addr, params);

                }



        });

        this.btn_stop=(Button) view.findViewById(R.id.button_stop);
        btn_stop.setOnClickListener(new View.OnClickListener() {
            private long preTime;
            private int delayTime = 100;
            @Override
            public void onClick(View v) {
                //点击监听

                if(meshAddress != 0xff)
                {

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
                }
                else
                {
                    adr = 0xff;
                }
//                int addr = meshAddress;
                modify = (byte)((modify & 0xFD) & 0xFE);

                byte[] params = new byte[]{(byte)adr,0,modify,0,0,0,0,0,0,0};


                byte opcode = (byte) 0xD0;
                int address = 0xFFFF;

                TelinkLightService.Instance().sendCommand(opcode, address, params);

            }



        });

        this.btn_down=(Button) view.findViewById(R.id.button_down);

        btn_down.setOnClickListener(new View.OnClickListener() {
            private long preTime;
            private int delayTime = 100;
            @Override
            public void onClick(View v) {
                //点击监听

//                int addr = meshAddress;
                if(meshAddress != 0xff)
                {

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
                }
                else
                {
                    adr = 0xff;
                }
                modify = (byte)((modify & 0xFD) | 0x01);

                byte[] params = new byte[]{(byte)adr,0,modify,0,0,0,0,0,0,0};


                byte opcode = (byte) 0xD0;
                int address = 0xFFFF;

                TelinkLightService.Instance().sendCommand(opcode, address, params);

            }



        });

        this.btn_fan_open=(Button) view.findViewById(R.id.button_fan_open);//绑定ID
        btn_fan_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击监听

//                int addr = meshAddress;
                if(meshAddress != 0xff)
                {

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
                }
                else
                {
                    adr = 0xff;
                }

                modify = (byte)(modify | 0x08);
                byte[] params = new byte[]{(byte)adr,0,modify,0,0,0,0,0,0,0};


                byte opcode = (byte) 0xD0;

                int address = 0xFFFF;

                TelinkLightService.Instance().sendCommand(opcode, address, params);
            }
        });

        this.btn_fan_close=(Button) view.findViewById(R.id.button_fan_close);//绑定ID
        btn_fan_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击监听

//                int addr = meshAddress;
                if(meshAddress != 0xff)
                {

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
                }
                else
                {
                    adr = 0xff;
                }

                modify = (byte)(modify & 0xF7);
                byte[] params = new byte[]{(byte)adr,0,modify,0,0,0,0,0,0,0};


                byte opcode = (byte) 0xD0;
                int address = 0xFFFF;

                TelinkLightService.Instance().sendCommand(opcode, address, params);
            }
        });

        this.btn_socket_open=(Button) view.findViewById(R.id.button_socket_open);//绑定ID
        btn_socket_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击监听

//                int addr = meshAddress;
                if(meshAddress != 0xff)
                {

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
                }
                else
                {
                    adr = 0xff;
                }

                modify = (byte)(modify | 0x10);
                byte[] params = new byte[]{(byte)adr,0,modify,0,0,0,0,0,0,0};


                byte opcode = (byte) 0xD0;
                int address = 0xFFFF;

                TelinkLightService.Instance().sendCommand(opcode, address, params);
            }
        });

        this.btn_socket_close=(Button) view.findViewById(R.id.button_socket_close);//绑定ID
        btn_socket_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击监听

//                int addr = meshAddress;
                if(meshAddress != 0xff)
                {

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
                }
                else
                {
                    adr = 0xff;
                }

                modify = (byte)(modify & 0xEF);
                byte[] params = new byte[]{(byte)adr,0,modify,0,0,0,0,0,0,0};


                byte opcode = (byte) 0xD0;
                int address = 0xFFFF;

                TelinkLightService.Instance().sendCommand(opcode, address, params);
            }
        });

        this.btn_lighting_open=(Button) view.findViewById(R.id.button_lighting_open);//绑定ID
        btn_lighting_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击监听

//                int addr = meshAddress;
                if(meshAddress != 0xff)
                {

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
                }
                else
                {
                    adr = 0xff;
                }

                modify = (byte)(modify | 0x04);
                byte[] params = new byte[]{(byte)adr,0,modify,0,0,0,0,0,0,0};


                byte opcode = (byte) 0xD0;
                int address = 0xFFFF;

                TelinkLightService.Instance().sendCommand(opcode, address, params);
            }
        });

        this.btn_lighting_close=(Button) view.findViewById(R.id.button_lighting_close);//绑定ID
        btn_lighting_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击监听

//                int addr = meshAddress;
                if(meshAddress != 0xff)
                {

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
                }
                else
                {
                    adr = 0xff;
                }

                modify = (byte)(modify & 0xFB);
                byte[] params = new byte[]{(byte)adr,0,modify,0,0,0,0,0,0,0};


                byte opcode = (byte) 0xD0;
                int address = 0xFFFF;

                TelinkLightService.Instance().sendCommand(opcode, address, params);
            }
        });

        return view;
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
}
