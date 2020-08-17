package com.example.sjy.githubtest;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

/****/
import com.example.sjy.githubtest.R;
/****/
public class StepcountService extends Service implements SensorEventListener {

    IBinder mBinder = new MyBinder();
    public static Intent serviceIntent = null;
    private StepCallback callback;

    /****/
    private static final int MILLISINFUTURE = 1000*1000;
    private static final int COUNT_DOWN_INTERVAL = 1000;
    /****/


    public void setCallback(StepCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.v("service" , "onSensorChanged" );
        if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            Log.v("service", "service에서 스텝" + sensorEvent.values[0]);
            if (callback != null)
                callback.onStepCallback((int)sensorEvent.values[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    class MyBinder extends Binder {
        StepcountService getService() { // 서비스 객체를 리턴
            return StepcountService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v("service" , "onBind" );
        // 액티비티에서 bindService() 를 실행하면 호출됨
        // 리턴한 IBinder 객체는 서비스와 클라이언트 사이의 인터페이스 정의한다
        return mBinder; // 서비스 객체를 리턴
    }

    private SensorManager sensorManager;
    private Sensor stepCountSensor;

    @Override
    public void onCreate() {
        Log.v("service" , "onCreate" );

        /****/
        unregisterRestartAlarm();
        /****/


        super.onCreate();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        //측정 시작
        sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_NORMAL);



        startForegroundService();

    }

    void setCurrentStep(int currentstep){
//        //알림창에 현재 걸음 수
//        Log.v("service", "현재 걸음 수 : " + currentstep);

    }



    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)     /**에러나면 현재버전으로 바꿔보기**/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        //측정 시작
        sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_NORMAL);


        serviceIntent = intent;

        startForegroundService();

        Log.v("service", "onStartCommand" );
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v("service", "onUnbind" );
        unRegistManager();
        if (callback != null)
            callback.onUnbindService();
        return super.onUnbind(intent);
    }


    public void unRegistManager() { //혹시 모를 에러상황에 트라이 캐치
        try {
            sensorManager.unregisterListener(this);
        } catch (Exception e) {
            Log.v("service", "Exception" );
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.v("service" , "onDestroy" );

        /**
         * 서비스 종료 시 알람 등록을 통해 서비스 재 실행
         */
        registerRestartAlarm();

    }



    void startForegroundService() {
        Log.v("service", "startForegroundService" );
        Intent notificationIntent = new Intent(this, WeightActivity.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, notificationIntent, 0);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_service);


        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= 26) {

            String CHANNEL_ID = "stepcount_service_channel";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "StepCount Service Channel", NotificationManager.IMPORTANCE_DEFAULT);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);

        } else {

            builder = new NotificationCompat.Builder(this);

        }

        builder.setSmallIcon(R.mipmap.ic_launcher).setContent(remoteViews).setContentIntent(pendingIntent);

        startForeground(1, builder.build());
    }




    /**
     * 알람 매니져에 서비스 등록
     */
    private void registerRestartAlarm(){

        Log.i("service" , "registerRestartAlarm" );
        Intent intent = new Intent(StepcountService.this,RestartService.class);
        intent.setAction("ACTION.RESTART.registerRestartAlarm");
        PendingIntent sender = PendingIntent.getBroadcast(StepcountService.this,0,intent,0);


    }

    /**
     * 알람 매니져에 서비스 해제
     */
    private void unregisterRestartAlarm(){

        Log.i("service" , "unregisterRestartAlarm" );

        Intent intent = new Intent(StepcountService.this,RestartService.class);
        intent.setAction("ACTION.RESTART.registerRestartAlarm");
        PendingIntent sender = PendingIntent.getBroadcast(StepcountService.this,0,intent,0);



    }





}
