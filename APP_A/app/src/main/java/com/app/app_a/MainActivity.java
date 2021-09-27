package com.app.app_a;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.app.login.ILoginInterface;

public class MainActivity extends AppCompatActivity {

    private boolean isStartRemote;
    private ILoginInterface iLogin; // AIDL定义接口

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_login_third_part).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(iLogin != null){
                    try {
                        // 调用server提供的方法
                        iLogin.login();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MainActivity.this,"请先安装第三方登录应用",Toast.LENGTH_SHORT).show();
                }
            }
        });

        initBindService();
    }

    private void initBindService() {
        Intent intent = new Intent();
        // 服务唯一标识
        intent.setAction("app_b_login_service");
        // 设置service应用包名
        intent.setPackage("com.app.app_b");
        // 绑定远程服务
        bindService(intent,conn,BIND_AUTO_CREATE);

        isStartRemote = true;
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("app_a","onServiceConnected " + name +"::"+service);
            // 使用服务端  AIDL
            iLogin = ILoginInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("app_a","onServiceDisconnected " + name);
            initBindService();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isStartRemote){
            unbindService(conn);
        }
    }
}