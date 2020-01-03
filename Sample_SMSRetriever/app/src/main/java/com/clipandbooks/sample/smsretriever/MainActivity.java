package com.clipandbooks.sample.smsretriever;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends Activity implements View.OnClickListener {

    private MySMSBroadcastReceiver mySMSBroadcastReceiver;

    Button start;
    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start = (Button)findViewById(R.id.start_receiver);
        result = (TextView)findViewById(R.id.result);
        start.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.start_receiver :
                result.setText("");
                registerSmsReceiver();
                smsRetrieverCall();
                break;
        }
    }

    private void smsRetrieverCall() {
        // Get an instance of SmsRetrieverClient, used to start listening for a matching
        // SMS message.
        SmsRetrieverClient client = SmsRetriever.getClient(this /* context */);

        // Starts SmsRetriever, which waits for ONE matching SMS message until timeout
        // (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
        // action SmsRetriever#SMS_RETRIEVED_ACTION.
        Task<Void> task = client.startSmsRetriever();

        // Listen for success/failure of the start Task. If in a background thread, this
        // can be made blocking using Tasks.await(task, [timeout]);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Successfully started retriever, expect broadcast intent
                Log.d("TAG", "smsRetrieverCall SUCCESS");
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failed to start retriever, inspect Exception for more details
                // ...
                Log.d("TAG", "smsRetrieverCall FAIL");
                result.setText("Retriever start Fail");
            }
        });
    }

    private void registerSmsReceiver() {
        if (mySMSBroadcastReceiver == null) {
            mySMSBroadcastReceiver = new MySMSBroadcastReceiver(new OnAuthNumberReceivedListener() {
                @Override
                public void onAuthNumberReceived(String authNumber) {
                    Log.d("TAG", "************************");
                    Log.d("TAG", "RECEIVED String : "+authNumber);
                    Log.d("TAG", "************************");
                    result.setText(authNumber);
                }
            });
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(MySMSBroadcastReceiver.SMSRetrievedAction);

        registerReceiver(mySMSBroadcastReceiver, filter);
    }
}
