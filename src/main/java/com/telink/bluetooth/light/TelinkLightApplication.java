package com.telink.bluetooth.light;

import com.telink.TelinkApplication;
import com.telink.bluetooth.TelinkLog;
import com.telink.bluetooth.light.model.Mesh;
import com.telink.bluetooth.light.util.FileSystem;

public final class TelinkLightApplication extends TelinkApplication {

    private Mesh mesh;

    @Override
    public void onCreate() {
        super.onCreate();
        //this.doInit();
        AdvanceStrategy.setDefault(new MySampleAdvanceStrategy());
    }

    @Override
    public void doInit() {

        String fileName = "telink-";
        fileName += System.currentTimeMillis();
        fileName += ".log";
        TelinkLog.LOG2FILE_ENABLE = false;
        TelinkLog.onCreate(fileName);
        super.doInit();
        //AES.Security = true;
        if (FileSystem.exists("telink.meshs")) {
            this.mesh = (Mesh) FileSystem.readAsObject("telink.meshs");
        }
        //启动LightService
        this.startLightService(TelinkLightService.class);
    }

    @Override
    public void doDestroy() {
        TelinkLog.onDestroy();
        super.doDestroy();

    }

    public Mesh getMesh() {
        return this.mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    public boolean isEmptyMesh() {
        return this.mesh == null;
    }
}
