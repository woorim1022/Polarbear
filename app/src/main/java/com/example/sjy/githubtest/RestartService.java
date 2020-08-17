package com.example.sjy.githubtest;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;



public class RestartService extends BroadcastReceiver {
    private StepcountService scService;
    boolean isService = false;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.v("restartservice" , "RestartService called : " + intent.getAction());

        /**
         * 서비스 죽일때 알람으로 다시 서비스 등록
         */
        if(intent.getAction().equals("ACTION.RESTART.StepcountService")){

            Log.i("restartservice" ,"ACTION.RESTART.StepcountService " );

            Intent i = new Intent(context,StepcountService.class);
            context.startService(i);
            context.bindService(i, conn, Context.BIND_AUTO_CREATE);
        }

        /**
         * 폰 재시작 할때 서비스 등록
         */
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){

            Log.i("RestartService" , "ACTION_BOOT_COMPLETED" );
            Intent i = new Intent(context,StepcountService.class);
            context.startService(i);
            context.bindService(i, conn, Context.BIND_AUTO_CREATE);

        }

    }

    private ServiceConnection conn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 서비스와 연결되었을 때 호출되는 메서드
            // 서비스 객체를 전역변수로 저장
            Log.v("service", "예스바인딩");
            StepcountService.MyBinder mBinder = (StepcountService.MyBinder) service;
            scService = mBinder.getService(); // 서비스가 제공하는 메소드 호출하여
            scService.setCallback(stepCallback);
            // 서비스쪽 객체를 전달받을수 있슴
            isService = true;
        }
        public void onServiceDisconnected(ComponentName name) {
            // 서비스와 연결이 끊겼을 때 호출되는 메서드
            isService = false;
            Log.v("service", "디스바인딩");
        }
    };

    private StepCallback stepCallback = new StepCallback() { //서비스 내부로 Set되어 스텝카운트의 변화와 Unbind의 결과를 전달하는 콜백 객체의 구현체
        @Override
        public void onStepCallback(int step) {
            }

        @Override
        public void onUnbindService() {
            isService = false;
        }
    };

}
