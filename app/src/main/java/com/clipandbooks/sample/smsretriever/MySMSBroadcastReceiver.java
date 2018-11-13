package com.clipandbooks.sample.smsretriever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

public class MySMSBroadcastReceiver extends BroadcastReceiver {

    private OnAuthNumberReceivedListener listener;

    public static final String SMSRetrievedAction = "com.google.android.gms.auth.api.phone.SMS_RETRIEVED";

    public MySMSBroadcastReceiver(OnAuthNumberReceivedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);


            Log.d("TAG", "MySMSBroadcastReceiver : onReceiver");

            switch(status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    // Get SMS message contents
                    String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                    // Extract one-time code from the message and complete verification
                    // by sending the code back to your server.
                    listener.onAuthNumberReceived(message);
                    Log.d("TAG", "MySMSBroadcastReceiver : onReceiver(CommonStatusCodes.SUCCESS)");
                    break;
                case CommonStatusCodes.TIMEOUT:
                    // Waiting for SMS timed out (5 minutes)
                    // Handle the error ...
                    Log.d("TAG", "MySMSBroadcastReceiver : onReceiver(CommonStatusCodes.TIMEOUT)");
                    break;
            }
        }
    }
}
