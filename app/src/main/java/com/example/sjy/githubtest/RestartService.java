package com.example.sjy.githubtest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;



public class RestartService extends Service {
    private StepcountService stepService;

//    private StepCallback stepCallback = new StepCallback() { //서비스 내부로 Set되어 스텝카운트의 변화와 Unbind의 결과를 전달하는 콜백 객체의 구현체
//        @Override
//        public void onStepCallback(int step) {
//            Log.v("@@@", "onStepCallback(restartservice)");
//        }
//        @Override
//        public void onUnbindService() {
//        }
//    };

    public RestartService() {
    }


    @Override
    public void onCreate() {
        Log.v("@@@", "onCreate(restartservice)");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.v("@@@", "onDestroy(restartservice)");
        super.onDestroy();
        stopForeground(true);
        Log.v("@@@", "stopForeground");
        stopSelf();
    }

//    private ServiceConnection serviceConnection = new ServiceConnection() { //서비스 바인드를 담당하는 객체의 구현체
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            Log.v("@@@", "onServiceConnected(restartservice)");
//            StepcountService.MyBinder mb = (StepcountService.MyBinder) service;
//            stepService = mb.getService(); //
////            stepService.setCallback(stepCallback);
//        }
//        @Override
//        public void onServiceDisconnected(ComponentName name) { //요거는 사실상 서비스가 킬되거나 아예 죽임 당했을 때만 호출된다고 보시면 됨
//// stopService 또는 unBindService때 호출되지 않음.
//        }
//    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("@@@", "onStartCommand(restartservice)");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(null);
        builder.setContentText(null);
        Intent notificationIntent = new Intent(this, WeightActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        builder.setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_NONE));
        }

        Notification notification = builder.build();
        startForeground(9, notification);
        Log.v("@@@", "startForeground");
        /////////////////////////////////////////////////////////////////////
        Intent in = new Intent(this, StepcountService.class);
        startService(in);
//        bindService(in, serviceConnection, Context.BIND_AUTO_CREATE);

//        stopForeground(true);
//        Log.v("@@@", "stopForeground");
//        stopSelf();

        return START_NOT_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

}
