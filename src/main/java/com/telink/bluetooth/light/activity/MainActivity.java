package com.telink.bluetooth.light.activity;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;


import com.blankj.utilcode.util.LogUtils;
import com.telink.bluetooth.LeBluetooth;
import com.telink.bluetooth.TelinkLog;
import com.telink.bluetooth.event.DeviceEvent;
import com.telink.bluetooth.event.MeshEvent;
import com.telink.bluetooth.event.NotificationEvent;
import com.telink.bluetooth.event.ServiceEvent;
import com.telink.bluetooth.light.ConnectionStatus;
import com.telink.bluetooth.light.DeviceInfo;
import com.telink.bluetooth.light.LeAutoConnectParameters;
import com.telink.bluetooth.light.LeRefreshNotifyParameters;
import com.telink.bluetooth.light.LightAdapter;
import com.telink.bluetooth.light.OnlineStatusNotificationParser;
import com.telink.bluetooth.light.Parameters;
import com.telink.bluetooth.light.R;
import com.telink.bluetooth.light.TelinkFragmentActivity;
import com.telink.bluetooth.light.TelinkLightApplication;
import com.telink.bluetooth.light.TelinkLightService;
import com.telink.bluetooth.light.fragments.DeviceListFragment;
import com.telink.bluetooth.light.fragments.GroupListFragment;
import com.telink.bluetooth.light.model.Light;
import com.telink.bluetooth.light.model.Lights;
import com.telink.bluetooth.light.model.Mesh;
import com.telink.bluetooth.light.util.FragmentFactory;
import com.telink.util.BuildUtils;
import com.telink.util.Event;
import com.telink.util.EventListener;

import com.telink.bluetooth.light.NotificationInfo;

import java.util.List;



public final class MainActivity extends TelinkFragmentActivity implements EventListener<String> {

    private final static String TAG = MainActivity.class.getSimpleName();

    private static final int UPDATE_LIST = 0;
    private FragmentManager fragmentManager;
    private DeviceListFragment deviceFragment;
//    private GroupListFragment groupFragment;
    //private Button btn_up;
    private Fragment mContent;

    private RadioGroup tabs;

    private TelinkLightApplication mApplication;

    private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            if (checkedId == R.id.tab_devices) {
                switchContent(mContent, deviceFragment);
            }
//            else if (checkedId == R.id.tab_groups) {
//                switchContent(mContent, groupFragment);
//            }
        }
    };

    private int connectMeshAddress;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_LIST:
                    NotificationInfo info = (NotificationInfo) msg.obj;
//                    // RecvText.setText(RecvText.getText(), TextView.BufferType.EDITABLE);
//                    //RecvText.setText(Arrays.bytesToHexString(info.params, ", "));
//                    RecvText.append(Arrays.bytesToHexString(info.params, ", ")+"\n");
//                    // RecvText.setText(Arrays.bytesToHexString(info.params),"," );
//                    if(RecvText.getLineCount() >15)
//                    {
//                        RecvText.setText("");
//                    }
                    deviceFragment.notifyDataSetChanged();
                    break;
            }
        }
    };

    private Handler mDelayHandler = new Handler();
    private Runnable mDelayTask = new SendCommandTask();
    private int delay = 200;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);

                switch (state) {
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "蓝牙开启");
                        TelinkLightService.Instance().idleMode(true);
                        autoConnect();
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "蓝牙关闭");
                        break;
                }
            }
        }
    };

    private void userAllNotify(NotificationEvent event) {
        Message message;

        message = mHandler.obtainMessage();//性能优化后
        message.what=UPDATE_LIST;
        message.obj = event.getArgs();
        mHandler.sendMessage(message); //发送消息
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);



//        btn_up=(Button)findViewById(R.id.)

        Log.d(TAG, "onCreate");
        //TelinkLog.ENABLE = false;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.activity_main);

        this.mApplication = (TelinkLightApplication) this.getApplication();

        this.fragmentManager = this.getFragmentManager();

        this.deviceFragment = (DeviceListFragment) FragmentFactory
                .createFragment(R.id.tab_devices);
//        this.groupFragment = (GroupListFragment) FragmentFactory
//                .createFragment(R.id.tab_groups);

        this.tabs = (RadioGroup) this.findViewById(R.id.tabs);
        this.tabs.setOnCheckedChangeListener(this.checkedChangeListener);

        if (savedInstanceState == null) {

            FragmentTransaction transaction = this.fragmentManager
                    .beginTransaction();
            transaction.add(R.id.content, this.deviceFragment).commit();

            this.mContent = this.deviceFragment;
        }

        this.mApplication.doInit();

        TelinkLog.d("-------------------------------------------");
        TelinkLog.d(Build.MANUFACTURER);
        TelinkLog.d(Build.TYPE);
        TelinkLog.d(Build.BOOTLOADER);
        TelinkLog.d(Build.DEVICE);
        TelinkLog.d(Build.HARDWARE);
        TelinkLog.d(Build.SERIAL);
        TelinkLog.d(Build.BRAND);
        TelinkLog.d(Build.DISPLAY);
        TelinkLog.d(Build.FINGERPRINT);

        TelinkLog.d(Build.PRODUCT + ":" + Build.VERSION.SDK_INT + ":" + Build.VERSION.RELEASE + ":" + Build.VERSION.CODENAME + ":" + Build.ID);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY - 1);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onStart() {

        super.onStart();

        Log.d(TAG, "onStart");

        int result = BuildUtils.assetSdkVersion("4.4");
        Log.d(TAG, " Version : " + result);

        // 监听各种事件
        this.mApplication.addEventListener(DeviceEvent.STATUS_CHANGED, this);
        this.mApplication.addEventListener(NotificationEvent.ONLINE_STATUS, this);
        this.mApplication.addEventListener(ServiceEvent.SERVICE_CONNECTED, this);
        this.mApplication.addEventListener(MeshEvent.OFFLINE, this);
        this.mApplication.addEventListener(MeshEvent.ERROR, this);
        this.mApplication.addEventListener(NotificationEvent.GET_USER_ALL_DATA, this);

        this.autoConnect();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //检查是否支持蓝牙设备
        if (!LeBluetooth.getInstance().isSupport(getApplicationContext())) {
            Toast.makeText(this, "蓝牙不支持", Toast.LENGTH_SHORT).show();
            this.finish();
            return;
        }

        if (!LeBluetooth.getInstance().isEnabled()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("开启蓝牙，体验智能灯!");
            builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton("开启", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    LeBluetooth.getInstance().enable(getApplicationContext());
                }
            });
            builder.show();
        }

        DeviceInfo deviceInfo = this.mApplication.getConnectDevice();

        if (deviceInfo != null) {
            this.connectMeshAddress = this.mApplication.getConnectDevice().meshAddress & 0xFF;
        }

        Log.d(TAG, "onResume");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        //移除事件
        this.mApplication.removeEventListener(this);
        TelinkLightService.Instance().disableAutoRefreshNotify();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        unregisterReceiver(mReceiver);
        this.mApplication.doDestroy();
        this.mDelayHandler.removeCallbacksAndMessages(null);
        Lights.getInstance().clear();
    }

    /**
     * 自动重连
     */
    private void autoConnect() {

        if (TelinkLightService.Instance() != null) {

            if (TelinkLightService.Instance().getMode() != LightAdapter.MODE_AUTO_CONNECT_MESH) {

                Lights.getInstance().clear();
                this.deviceFragment.notifyDataSetChanged();

                if (this.mApplication.isEmptyMesh())
                    return;

                Mesh mesh = this.mApplication.getMesh();

                //自动重连参数
                LeAutoConnectParameters connectParams = Parameters.createAutoConnectParameters();
                connectParams.setMeshName(mesh.name);
                connectParams.setPassword(mesh.password);
                connectParams.autoEnableNotification(true);
                //自动重连
                TelinkLightService.Instance().autoConnect(connectParams);
            }

            //刷新Notify参数
            LeRefreshNotifyParameters refreshNotifyParams = Parameters.createRefreshNotifyParameters();
            refreshNotifyParams.setRefreshRepeatCount(2);
            refreshNotifyParams.setRefreshInterval(2000);
            //开启自动刷新Notify
            TelinkLightService.Instance().autoRefreshNotify(refreshNotifyParams);
        }
    }

    private void switchContent(Fragment from, Fragment to) {

        if (this.mContent != to) {
            this.mContent = to;

            FragmentTransaction transaction = this.fragmentManager
                    .beginTransaction();

            if (!to.isAdded()) {
                transaction.hide(from).add(R.id.content, to);
            } else {
                transaction.hide(from).show(to);
            }

            transaction.commit();
        }
    }

    private Handler mHanlder = new Handler();

    private void onDeviceStatusChanged(DeviceEvent event) {

        DeviceInfo deviceInfo = event.getArgs();

        switch (deviceInfo.status) {
            case LightAdapter.STATUS_LOGIN:
                this.connectMeshAddress = this.mApplication.getConnectDevice().meshAddress;
                this.show("登录成功");
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        TelinkLightService.Instance().sendCommandNoResponse((byte) 0xE4, 0xFFFF, new byte[]{});
                    }
                }, 3 * 1000);
                break;
            case LightAdapter.STATUS_CONNECTING:
                this.show("登录");
                break;
            case LightAdapter.STATUS_LOGOUT:
                this.show("设备掉线");
                break;
            default:
                break;
        }
    }

    /**
     * 处理{@link NotificationEvent#ONLINE_STATUS}事件
     *
     * @param event
     */
    private void onOnlineStatusNotify(NotificationEvent event) {

        TelinkLog.d("Thread ID : " + Thread.currentThread().getId());
        List<OnlineStatusNotificationParser.DeviceNotificationInfo> notificationInfoList;
        //noinspection unchecked
        notificationInfoList = (List<OnlineStatusNotificationParser.DeviceNotificationInfo>) event.parse();

        if (notificationInfoList == null || notificationInfoList.size() <= 0)
            return;

        if (this.deviceFragment != null) {
            this.deviceFragment.onNotify(notificationInfoList);
        }

        for (OnlineStatusNotificationParser.DeviceNotificationInfo notificationInfo : notificationInfoList) {

            int meshAddress = notificationInfo.meshAddress;
            String macAddress = notificationInfo.macAddress;
            int brightness = notificationInfo.brightness;

            Light light = this.deviceFragment.getDevice(meshAddress);

            if (light == null) {
                light = new Light();

            }
            light.macAddress = macAddress;
            light.meshAddress = meshAddress;
            light.brightness = brightness;



            light.status = notificationInfo.connectStatus;

            if (light.meshAddress == this.connectMeshAddress) {
                light.textColor = this.getResources().getColorStateList(
                        R.color.theme_positive_color);
            } else {
                light.textColor = this.getResources().getColorStateList(
                        R.color.black);
            }

            light.updateIcon();

            try{
                if (light.status!= ConnectionStatus.OFFLINE){
                    if (deviceFragment!=null){
                        this.deviceFragment.addDevice(light);
                    }

                }
            }catch (Exception e){
                LogUtils.e(e);
            }
        }

        mHandler.obtainMessage(UPDATE_LIST).sendToTarget();
    }

    private void onServiceConnected(ServiceEvent event) {
        this.autoConnect();
    }

    private void onServiceDisconnected(ServiceEvent event) {

    }

    private void onMeshOffline(MeshEvent event) {

        List<Light> lights = Lights.getInstance().get();
        for (Light light : lights) {
            light.status = ConnectionStatus.OFFLINE;
            light.updateIcon();
        }
        this.deviceFragment.notifyDataSetChanged();
    }

    private void onMeshError(MeshEvent event) {
        new AlertDialog.Builder(this).setMessage("蓝牙出问题了，重启蓝牙试试!!").show();
    }

    /**
     * 事件处理方法
     *
     * @param event
     */
    @Override
    public void performed(Event<String> event) {
        switch (event.getType()) {
            case NotificationEvent.ONLINE_STATUS:
                this.onOnlineStatusNotify((NotificationEvent) event);
                break;
            case DeviceEvent.STATUS_CHANGED:
                this.onDeviceStatusChanged((DeviceEvent) event);
                break;
            case MeshEvent.OFFLINE:
                this.onMeshOffline((MeshEvent) event);
                break;
            case MeshEvent.ERROR:
                this.onMeshError((MeshEvent) event);
                break;
            case ServiceEvent.SERVICE_CONNECTED:
                this.onServiceConnected((ServiceEvent) event);
                break;
            case ServiceEvent.SERVICE_DISCONNECTED:
                this.onServiceDisconnected((ServiceEvent) event);
                break;
            case NotificationEvent.GET_USER_ALL_DATA:
                this.userAllNotify((NotificationEvent) event);
                break;
        }
    }

    private class SendCommandTask implements Runnable {
        @Override
        public void run() {
            TelinkLightService.Instance().sendCommandNoResponse((byte) 0xE2, 0xFFFF, new byte[]{0x04, 0x00, 0x00, 0x00});
            mDelayHandler.postDelayed(this, delay);
        }
    }
}
