package com.telink.bluetooth.light.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.telink.bluetooth.light.R;
import com.telink.bluetooth.light.TelinkActivity;
import com.telink.bluetooth.light.TelinkLightApplication;
import com.telink.bluetooth.light.TelinkLightService;
import com.telink.bluetooth.light.model.Mesh;

import java.io.File;

public final class AddMeshActivity extends TelinkActivity {

    private ImageView backView;
    private Button btnSave;
    private ImageView imgDel;

    private TelinkLightApplication mApplication;
    private OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v == backView) {
                finish();
            } else if (v == btnSave) {
                saveMesh();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.activity_add_mesh);

        this.mApplication = (TelinkLightApplication) this.getApplication();

        this.backView = (ImageView) this
                .findViewById(R.id.img_header_menu_left);
        this.backView.setOnClickListener(this.clickListener);

        this.btnSave = (Button) this.findViewById(R.id.btn_save);
        this.btnSave.setOnClickListener(this.clickListener);
        imgDel = (ImageView) findViewById(R.id.img_del);
        imgDel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(AddMeshActivity.this).setTitle("是否删除旧数据")
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                File dir = Environment.getExternalStorageDirectory();
                                File file = new File(dir, "telink.meshs");
                                boolean result = FileUtils.deleteFile(file);
                                if (result){
                                    ToastUtils.showShort("删除成功");
                                    AppUtils.relaunchApp();
                                }else {
                                    ToastUtils.showShort("删除失败");
                                }
                            }
                        }).create().show();
            }
        });
        this.updateGUI();

        TelinkLightService.Instance().idleMode(false);
    }

    private void updateGUI() {

        if (this.mApplication.isEmptyMesh())
            return;

        EditText txtMeshName = (EditText) this.findViewById(R.id.txt_mesh_name);
        EditText txtPassword = (EditText) this
                .findViewById(R.id.txt_mesh_password);

        EditText txtFactoryMeshName = (EditText) this
                .findViewById(R.id.txt_factory_name);
        EditText txtFactoryPassword = (EditText) this
                .findViewById(R.id.txt_factory_password);

        Mesh mesh = this.mApplication.getMesh();

        txtMeshName.setText(mesh.name);
        txtPassword.setText(mesh.password);
        txtFactoryMeshName.setText(mesh.factoryName);
        txtFactoryPassword.setText(mesh.factoryPassword);
    }

    @SuppressLint("ShowToast")
    private void saveMesh() {

        EditText txtMeshName = (EditText) this.findViewById(R.id.txt_mesh_name);
        EditText txtPassword = (EditText) this
                .findViewById(R.id.txt_mesh_password);

        EditText txtFactoryMeshName = (EditText) this
                .findViewById(R.id.txt_factory_name);
        EditText txtFactoryPassword = (EditText) this
                .findViewById(R.id.txt_factory_password);
        //EditText otaText = (EditText) this.findViewById(R.id.ota_device);
        Mesh mesh = this.mApplication.getMesh();

        String newfactoryName = txtMeshName.getText().toString().trim();
        String newfactoryPwd = txtPassword.getText().toString().trim();

        String factoryName = txtFactoryMeshName.getText().toString().trim();
        String factoryPwd = txtFactoryPassword.getText().toString().trim();

        if (mesh == null)
            mesh = new Mesh();

        if (!factoryName.equals(mesh.factoryName) || !factoryPwd.equals(mesh.factoryPassword)) {
            mesh.allocDeviceAddress = null;
            mesh.devices.clear();
        }

        mesh.name = newfactoryName;
        mesh.password = newfactoryPwd;
        mesh.factoryName = factoryName;
        mesh.factoryPassword = factoryPwd;
        //mesh.otaDevice = otaText.getText().toString().trim();

        if (mesh.saveOrUpdate()) {
            this.mApplication.setMesh(mesh);
            this.show("Save Mesh Success");
        }
    }
}
