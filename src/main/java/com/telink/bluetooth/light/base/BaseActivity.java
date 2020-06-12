//package com.telink.bluetooth.light.base;
//
//import android.annotation.SuppressLint;
//import android.os.Bundle;
//import android.widget.Toast;
//
//import androidx.fragment.app.FragmentActivity;
//
//import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
//
//public class BaseActivity extends FragmentActivity {
//
//    protected Toast toast;
//
//    @Override
//    @SuppressLint("ShowToast")
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        QMUIStatusBarHelper.translucent(this);
//        this.toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        this.toast.cancel();
//        this.toast = null;
//    }
//
//    public void show(CharSequence s) {
//
//        if (this.toast != null) {
//            this.toast.setView(this.toast.getView());
//            this.toast.setDuration(Toast.LENGTH_SHORT);
//            this.toast.setText(s);
//            this.toast.show();
//        }
//    }
//}
