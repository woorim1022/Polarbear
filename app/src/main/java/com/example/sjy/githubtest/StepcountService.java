package com.example.sjy.githubtest;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;

import static java.lang.Integer.parseInt;

public class StepcountService extends Service implements SensorEventListener {

    private Thread mainThread;
    public static Intent serviceIntent = null;

    private int mStepDetector;
    private StepCallback callback;

    private Receiver br;

    private String stepPref;

    private int count = 0;

    private String serviceCount = "false";

    public void setCallback(StepCallback callback) {
        Log.v("@@@", "setCallback");
        this.callback = callback;
    }

    private MyBinder mMyBinder = new MyBinder();

    class MyBinder extends Binder { //바인드 클래스를 생성
        StepcountService getService() { // 서비스 객체를 리턴
            return StepcountService.this;
        }
    }

    public StepcountService() {  //생성자
    }

    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;
    private Sensor stepCountSensor;

    @Override
    public void onCreate() {
        Log.v("@@@", "onCreate");
        super.onCreate();
        stepPref = PreferenceManager.getString(StepcountService.this, "STEPCOUNT");
        if(!stepPref.equals(""))
            mStepDetector += parseInt(stepPref);
        else
            mStepDetector = 0;

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (stepDetectorSensor == null) {
        } else {

            sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_NORMAL);

        }
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepCountSensor == null) {
        } else {

            sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);

        }


        serviceCount = PreferenceManager.getString(StepcountService.this, "serviceCount");
        if(serviceCount.equals(""))
            serviceCount = "false";
        Calendar cal = Calendar.getInstance();
        int nWeek = cal.get(Calendar.DAY_OF_WEEK);
        /**
         * 월요일이고 오늘 처음 StepcountService 에 진입한 경우
         * **/
        if(nWeek == 2 && serviceCount.equals("false")) {
            PreferenceManager.setString(StepcountService.this, "serviceCount", "true");
            mStepDetector = 0;  //누적 걸음 수 초기화
        }

        if(nWeek == 1)
            PreferenceManager.setString(StepcountService.this, "serviceCount", "false");
}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("@@@", "onStartCommand");
        serviceIntent = intent;

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if (stepDetectorSensor == null) {
        } else {

            sensorManager.registerListener(StepcountService.this, stepCountSensor, SensorManager.SENSOR_DELAY_NORMAL);

        }
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepCountSensor == null) {
        } else {

            sensorManager.registerListener(StepcountService.this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);

        }
//
//        mainThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Log.v("@@@", "mainThread");
//                boolean run = true;
//                while (run) {
//                        Log.v("@@@", "");
                        sendNotification("현재 걸음 수 : " + mStepDetector);
//                }
//            }
//        });
//        mainThread.start();

        return START_NOT_STICKY;   //START_STICKY, START_REDELIVER_INTENT
    }

    @Override
    public void onDestroy() {
        Log.v("@@@", "onDestroy");
        super.onDestroy();

        serviceIntent = null;
        sendBroadcast();
//        Thread.currentThread().interrupt();
//
//        if (mainThread != null) {
//            mainThread.interrupt();
//            mainThread = null;
//        }
    }

    /******************************************/
    protected void sendBroadcast() {
        Log.v("@@@", "broadcastreceiver");
        Intent intent = new Intent(this, Receiver.class);
        this.sendBroadcast(intent);
    }
    /******************************************/

    private void sendNotification(String messageBody) {
        Log.v("@@@", "sendNotification");
        Intent intent = new Intent(this, WeightActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = "fcm_default_channel";//getString(R.string.default_notification_channel_id);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)//drawable.splash)
                        .setContentTitle("걸음 수 측정 중")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,"Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v("@@@", "onBind");
        return mMyBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
//        Log.v("@@@", "onUnbind");
//        unRegistManager();
//        if (callback != null)
//            callback.onUnbindService();
        return super.onUnbind(intent);
    }

//    public void unRegistManager() { //혹시 모를 에러상황에 트라이 캐치
//        try {
//            Log.v("@@@", "unRegistManager");
//            sensorManager.unregisterListener(this);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            if (sensorEvent.values[0] == 1.0f) {
                mStepDetector += sensorEvent.values[0];
                if (callback != null)
                    callback.onStepCallback(mStepDetector);
                PreferenceManager.setString(StepcountService.this, "STEPCOUNT", "" + mStepDetector);  //sharedpreferences
                Log.v("@@@", "걸음 수 : " + mStepDetector);
            }
        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}
