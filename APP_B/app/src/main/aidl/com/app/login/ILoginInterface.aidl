// ILoginInterface.aidl
package com.app.login;

// Declare any non-default types here with import statements

interface ILoginInterface {
    void login();
    void loginCallback(boolean loginStatus,String loginUser);
}