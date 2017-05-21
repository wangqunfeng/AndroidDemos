// IMyAidlServiceInterface.aidl
package com.frank.servapp_0508;

// Declare any non-default types here with import statements
import com.frank.servapp_0508.IMyAidlClientCallback;

interface IMyAidlServiceInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
     int registerCallback(IMyAidlClientCallback callback);
     int unRegisterCallback(IMyAidlClientCallback callback);
     int setValue(int val1, int val2);

}
