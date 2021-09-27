package com.app.app_b;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.app.login.ILoginInterface;

// Server
public class MainActivity extends AppCompatActivity {
    private ILoginInterface iLogin;
    EditText etName;
    private boolean isStartClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = (EditText) findViewById(R.id.etName);
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startLogin();
            }
        });

        initBindService();
    }

    private void initBindService() {
        Intent intent = new Intent();
        intent.setAction("app_a_client_action");
        intent.setPackage("com.app.app_a");
        bindService(intent,conn,BIND_AUTO_CREATE);

        isStartClient = true;
    }

    private ServiceConnection conn = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("app_b","onServiceConnected " + name +"::"+service);
            iLogin = ILoginInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("app_b","onServiceDisconnected " + name);
            initBindService();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isStartClient){
            unbindService(conn);
        }
    }

    private void startLogin() {
        if(TextUtils.isEmpty(etName.getText().toString())){
            Toast.makeText(this,"用户名为空",Toast.LENGTH_SHORT).show();
            return;
        }

        Context context;
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("登录");
        dialog.setMessage("登录中.....");
        dialog.show();

        new Thread(new Runnable() {

            @Override
            public void run() {
                SystemClock.sleep(2000);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            boolean loginStatus = false;
                            if("zhan".equalsIgnoreCase(etName.getText().toString())){
                                Toast.makeText(MainActivity.this,"第三方登录成功",Toast.LENGTH_SHORT).show();
                                loginStatus = true;
                            } else {
                                Toast.makeText(MainActivity.this,"第三方登录失败",Toast.LENGTH_SHORT).show();
                                loginStatus = false;
                            }
                            dialog.dismiss();
                            // 通知client登录成功
                            iLogin.loginCallback(loginStatus,etName.getText().toString());
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }
}