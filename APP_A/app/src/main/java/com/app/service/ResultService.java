package com.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import com.app.login.ILoginInterface;

/**
 * @author feifei.zhan
 */
public class ResultService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ILoginInterface.Stub() {
            @Override
            public void login() throws RemoteException {

            }

            @Override
            public void loginCallback(boolean loginStatus, String loginUser) throws RemoteException {
                // server的登录回调  通知客户端
                Log.e("app_a","APP_A LoginResult >>> " + loginStatus +"::"+loginUser);
            }
        };
    }
}
