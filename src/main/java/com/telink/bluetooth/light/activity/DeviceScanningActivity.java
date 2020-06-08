package com.telink.bluetooth.light.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.telink.bluetooth.event.DeviceEvent;
import com.telink.bluetooth.event.LeScanEvent;
import com.telink.bluetooth.event.MeshEvent;
import com.telink.bluetooth.light.DeviceInfo;
import com.telink.bluetooth.light.LeScanParameters;
import com.telink.bluetooth.light.LeUpdateParameters;
import com.telink.bluetooth.light.LightAdapter;
import com.telink.bluetooth.light.Parameters;
import com.telink.bluetooth.light.R;
import com.telink.bluetooth.light.TelinkActivity;
import com.telink.bluetooth.light.TelinkLightApplication;
import com.telink.bluetooth.light.TelinkLightService;
import com.telink.bluetooth.light.fragments.DeviceListFragment;
import com.telink.bluetooth.light.model.Light;
import com.telink.bluetooth.light.model.Mesh;
import com.telink.util.Event;
import com.telink.util.EventListener;

import java.util.ArrayList;
import java.util.List;

public final class DeviceScanningActivity extends TelinkActivity implements AdapterView.OnItemClickListener, EventListener<String> {

    private final static String TAG = DeviceScanningActivity.class
            .getSimpleName();

    private ImageView backView;
    private Button btnScan;

    private LayoutInflater inflater;
    private DeviceListAdapter adapter;

    private TelinkLightApplication mApplication;
    private List<DeviceInfo> updateList;

    public String macadr;
//    private static String hexString="0123456789ABCDEF";
//    private int adr;

    private OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == backView) {
//                TelinkLightService.Instance().idleMode();
                finish();
            } else if (v == btnScan) {
                finish();
                //stopScanAndUpdateMesh();
            }
        }
    };
    private Handler mHandler = new Handler();

    @Override
    public void onBackPressed() {
//        TelinkLightService.Instance().idleMode();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.activity_device_scanning);

        //监听事件
        this.mApplication = (TelinkLightApplication) this.getApplication();
        this.mApplication.addEventListener(LeScanEvent.LE_SCAN, this);
        this.mApplication.addEventListener(LeScanEvent.LE_SCAN_TIMEOUT, this);
        this.mApplication.addEventListener(DeviceEvent.STATUS_CHANGED, this);
        this.mApplication.addEventListener(MeshEvent.UPDATE_COMPLETED, this);
        this.mApplication.addEventListener(MeshEvent.ERROR, this);

        this.inflater = this.getLayoutInflater();
        this.adapter = new DeviceListAdapter();

        this.backView = (ImageView) this
                .findViewById(R.id.img_header_menu_left);
        this.backView.setOnClickListener(this.clickListener);

        this.btnScan = (Button) this.findViewById(R.id.btn_scan);
        this.btnScan.setOnClickListener(this.clickListener);
        this.btnScan.setEnabled(false);
        this.btnScan.setBackgroundResource(R.color.gray);

        GridView deviceListView = (GridView) this
                .findViewById(R.id.list_devices);
        deviceListView.setAdapter(this.adapter);
        deviceListView.setOnItemClickListener(this);

        this.updateList = new ArrayList<>();

        this.startScan(100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.updateList = null;
        this.mApplication.removeEventListener(this);
    }

    /**
     * 开始扫描
     *
     * @param delay
     */
    private void startScan(int delay) {

        this.mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mApplication.isEmptyMesh())
                    return;

                Mesh mesh = mApplication.getMesh();

                //扫描参数
                LeScanParameters params = LeScanParameters.create();
                params.setMeshName(mesh.factoryName);
                params.setOutOfMeshName("kick");
                params.setTimeoutSeconds(10);
                params.setScanMode(true);
                TelinkLightService.Instance().startScan(params);
            }
        }, delay);

    }

    /**
     * 处理扫描事件
     *
     * @param event
     */
    private void onLeScan(LeScanEvent event) {

        Mesh mesh = this.mApplication.getMesh();
        int meshAddress = mesh.getDeviceAddress();
        //int dst;

        if (meshAddress == -1) {
            this.show("哎呦，网络里的灯泡太多了！目前可以有256灯");
            this.finish();
            return;
        }

        //更新参数
        LeUpdateParameters params = Parameters.createUpdateParameters();
        params.setOldMeshName(mesh.factoryName);
        params.setOldPassword(mesh.factoryPassword);
        params.setNewMeshName(mesh.name);
        params.setNewPassword(mesh.password);

        DeviceInfo deviceInfo = event.getArgs();
        deviceInfo.meshAddress = meshAddress;
        this.macadr = deviceInfo.macAddress;

        DeviceListFragment.meshtomac.addrmeshList.add(meshAddress);
        DeviceListFragment.meshtomac.addrmacList.add(macadr);




//        char[] addr = macadr.toCharArray();

////根据默认编码获取字节数组
//            byte[] bytes=macadr.getBytes();
//            StringBuilder sb=new StringBuilder(bytes.length*2);
////将字节数组中每个字节拆解成2位16进制整数
//            for(int i=0;i<bytes.length;i++)
//            {
//                sb.append(hexString.charAt((bytes[i]&0xf0)>>4));
//                sb.append(hexString.charAt((bytes[i]&0x0f)>>0));
//            }
////            String addr =  sb.toString();
//            char[] addr = sb.toString().toCharArray();
//            if(addr[32]>= '0' && addr[32] <= '9') {
//                adr = addr[32] - '0';
//                adr = adr << 4;
//            }
//            else if(addr[32]>= 'A' && addr[32]<= 'F')
//            {
//                adr = addr[32] - 'A' + 0x0a;
//                adr = adr << 4;
//            }
//        if(addr[33]>= '0' && addr[33] <= '9') {
//            adr = adr |(addr[33] - '0');
//        }
//        else if(addr[33]>= 'A' && addr[33]<= 'F')
//        {
//            adr = adr |(addr[33] - 'A' + 0x0a);
//        }
//        deviceInfo.meshAddress  = adr;
//            else if(addr[32]>=)


        //dst = (mac_id[0] | (((mac_id[1]^mac_id[2])&0x7f)<< 8))

        params.setUpdateDeviceList(deviceInfo);
        //params.set(Parameters.PARAM_DEVICE_LIST, deviceInfo);
        TelinkLightService.Instance().idleMode(true);
        //加灯
        TelinkLightService.Instance().updateMesh(params);
    }

    /**
     * 扫描不到任何设备了
     *
     * @param event
     */
    private void onLeScanTimeout(LeScanEvent event) {
        this.btnScan.setEnabled(true);
        this.btnScan.setBackgroundResource(R.color.theme_positive_color);
    }

    private void onDeviceStatusChanged(DeviceEvent event) {

        DeviceInfo deviceInfo = event.getArgs();

        switch (deviceInfo.status) {
            case LightAdapter.STATUS_UPDATE_MESH_COMPLETED:
                //加灯完成继续扫描,直到扫不到设备
                com.telink.bluetooth.light.model.DeviceInfo deviceInfo1 = new com.telink.bluetooth.light.model.DeviceInfo();
                deviceInfo1.deviceName = deviceInfo.deviceName;
                deviceInfo1.firmwareRevision = deviceInfo.firmwareRevision;
                deviceInfo1.longTermKey = deviceInfo.longTermKey;
                deviceInfo1.macAddress = deviceInfo.macAddress;
                deviceInfo1.meshAddress = deviceInfo.meshAddress;
                deviceInfo1.meshUUID = deviceInfo.meshUUID;
                deviceInfo1.productUUID = deviceInfo.productUUID;
                deviceInfo1.status = deviceInfo.status;
                deviceInfo1.meshName = deviceInfo.meshName;
                this.mApplication.getMesh().devices.add(deviceInfo1);
                this.mApplication.getMesh().saveOrUpdate();
                int meshAddress = deviceInfo.meshAddress & 0xFF;
                Light light = this.adapter.get(meshAddress);

                if (light == null) {
                    light = new Light();
                    light.name = deviceInfo.meshName;
                    light.meshAddress = meshAddress;
                    light.textColor = this.getResources().getColorStateList(
                            R.color.black);
                    light.selected = false;
                    light.raw = deviceInfo;
                    this.adapter.add(light);
                    this.adapter.notifyDataSetChanged();
                }

                this.startScan(1000);
                break;
            case LightAdapter.STATUS_UPDATE_MESH_FAILURE:
                //加灯失败继续扫描
                this.startScan(1000);
                break;
        }
    }

    private void onMeshEvent(MeshEvent event) {
        new AlertDialog.Builder(this).setMessage("重启蓝牙,更好地体验智能灯!").show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Light light = this.adapter.getItem(position);
        light.selected = !light.selected;
        DeviceItemHolder holder = (DeviceItemHolder) view.getTag();
        holder.selected.setChecked(light.selected);

        if (light.selected) {
            this.updateList.add(light.raw);
        } else {
            this.updateList.remove(light.raw);
        }
    }

    /**
     * 事件处理方法
     *
     * @param event
     */
    @Override
    public void performed(Event<String> event) {

        switch (event.getType()) {
            case LeScanEvent.LE_SCAN:
                this.onLeScan((LeScanEvent) event);
                break;
            case LeScanEvent.LE_SCAN_TIMEOUT:
                this.onLeScanTimeout((LeScanEvent) event);
                break;
            case DeviceEvent.STATUS_CHANGED:
                this.onDeviceStatusChanged((DeviceEvent) event);
                break;
            case MeshEvent.ERROR:
                this.onMeshEvent((MeshEvent) event);
                break;
        }
    }

    private static class DeviceItemHolder {
        public ImageView icon;
        public TextView txtName;
        public CheckBox selected;
    }

    final class DeviceListAdapter extends BaseAdapter {

        private List<Light> lights;

        public DeviceListAdapter() {

        }

        @Override
        public int getCount() {
            return this.lights == null ? 0 : this.lights.size();
        }

        @Override
        public Light getItem(int position) {
            return this.lights.get(position);
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
                ImageView icon = (ImageView) convertView
                        .findViewById(R.id.img_icon);
                TextView txtName = (TextView) convertView
                        .findViewById(R.id.txt_name);
                CheckBox selected = (CheckBox) convertView.findViewById(R.id.selected);

                holder = new DeviceItemHolder();

                holder.icon = icon;
                holder.txtName = txtName;
                holder.selected = selected;
                holder.selected.setVisibility(View.GONE);

                convertView.setTag(holder);
            } else {
                holder = (DeviceItemHolder) convertView.getTag();
            }

            Light light = this.getItem(position);

            holder.txtName.setText(light.name);
            holder.icon.setImageResource(R.drawable.icon_light_on);
            holder.selected.setChecked(light.selected);

            return convertView;
        }

        public void add(Light light) {

            if (this.lights == null)
                this.lights = new ArrayList<>();

            this.lights.add(light);
        }

        public Light get(int meshAddress) {

            if (this.lights == null)
                return null;

            for (Light light : this.lights) {
                if (light.meshAddress == meshAddress) {
                    return light;
                }
            }

            return null;
        }
    }
}
