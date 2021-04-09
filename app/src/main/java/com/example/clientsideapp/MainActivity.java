package com.example.clientsideapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Messenger randomNoRequestMessenger, randomNoReceiveMessenger;


    Intent serviceIntent;
    Button bind_btn, unbind_btn, get_random_no_btn;
    TextView textView;

    int randomNumberValue;
    boolean mIsBound;
    public static final int GET_RANDOM_NUMBER_FLAG = 0;


    class RecieveRandomNumberHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            randomNumberValue = 0;
            switch (msg.what) {

                case GET_RANDOM_NUMBER_FLAG:
                    randomNumberValue = msg.arg1;
                    textView.setText("Random Number:" + randomNumberValue);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }


    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            randomNoRequestMessenger = new Messenger(iBinder);
            randomNoReceiveMessenger = new Messenger(new RecieveRandomNumberHandler());
            mIsBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            randomNoReceiveMessenger = null;
            randomNoRequestMessenger = null;
            mIsBound = false;

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context mContext = getApplicationContext();

        textView = findViewById(R.id.randomtext);

        bind_btn = findViewById(R.id.Bind);
        unbind_btn = findViewById(R.id.unbind);
        get_random_no_btn = findViewById(R.id.getrandom);

        bind_btn.setOnClickListener(this);
        unbind_btn.setOnClickListener(this);
        get_random_no_btn.setOnClickListener(this);

        serviceIntent = new Intent();
        serviceIntent.setComponent(new ComponentName("com.example.servicesideapp", "com.example.servicesideapp.MyService"));
        serviceIntent.setPackage(getPackageName());
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.Bind:
                bindToService();
                break;
            case R.id.unbind:
                unBindFromRemoteService();
                break;
            case R.id.getrandom:
                fetchRandomNumber();
                break;

            default:
                break;
        }

    }

    private void fetchRandomNumber() {

        if (mIsBound == true) {
            Message requestMessage = Message.obtain(null, GET_RANDOM_NUMBER_FLAG);
            requestMessage.replyTo = randomNoReceiveMessenger;
            try {
                randomNoRequestMessenger.send(requestMessage);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Service is not Bound ,Can't get Random Number", Toast.LENGTH_SHORT).show();
        }
    }

    private void unBindFromRemoteService() {

        unbindService(serviceConnection);
        mIsBound = false;
        Toast.makeText(getApplicationContext(), "Service UnBound", Toast.LENGTH_SHORT).show();

    }

    private void bindToService() {

        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
        Toast.makeText(getApplicationContext(), "Service Bound", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        serviceConnection = null;

    }
}