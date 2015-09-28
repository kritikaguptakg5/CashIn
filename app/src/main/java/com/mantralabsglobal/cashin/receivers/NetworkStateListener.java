
package com.mantralabsglobal.cashin.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.mantralabsglobal.cashin.event.InternetChangeListenerEvent;

import de.greenrobot.event.EventBus;

public class NetworkStateListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
            EventBus.getDefault().post(new InternetChangeListenerEvent());
    }
}
