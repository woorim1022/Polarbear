package com.example.sjy.githubtest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class WeightActivity extends AppCompatActivity implements SensorEventListener {

    private DrawerLayout drawerLayout;
    private View drawerView;
    private TextView weight;
    private Button button;
    private TextView measure_day;
    private TextView step_goal;
    private TextView step_current;

    private int count = 6;
    private CountDownTimer countDownTimer;

    AlertDialog.Builder builder, builder2, builder3;
    AlertDialog alertDialog, alertDialog2;

    private SensorManager sensorManager;
    private Sensor stepCountSensor;

    private int weightValue;
    private String prevDateStr;
    private String recentDateStr = "";

    private int weightPerDay;
    private int point;

    private String uid;

    private StepcountService scService;
    boolean isService = false; // 서비스 중인 확인용
    boolean mIsBound;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);

        drawerLayout = (DrawerLayout) findViewById(R.id.weight_layout);
        drawerView = (View) findViewById(R.id.drawerView);

        ImageView openDrawer = (ImageView) findViewById(R.id.menu_button);
        openDrawer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });


        weight = (TextView) findViewById(R.id.weight);
        button = (Button) findViewById(R.id.button);
        measure_day = (TextView) findViewById(R.id.measure_day);
        step_goal = (TextView) findViewById(R.id.step_goal);
        step_current = (TextView) findViewById(R.id.step_current);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCountSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepCountSensor == null)
            Log.v("aaa", "No step Detect Sensor");


        uid = PreferenceManager.getString(this, "userID");


        final Intent intent = getIntent();  //메인 화면에서 넘어온 intent 받음
        String datafrommain = intent.getStringExtra("메인 액티비티에서 넘길 정보"); //pustExtra로 지정했던 데이터의 키값을 지정하면 해당하는 데이터 값이 나오게 됨


        /**걸음수 측정**/
        //월요일이면,
            //전주에 획득한 포인트의 총 합이 200pt 미만이면,
                //걸음수 측정?
                    //yes
                        //측정시작
                        //앱 종료해도 측정
                        //20000보 채우면,
                            //포인트 획득, 측정 종료
                        //일요일이면,
                            //측정 종료
                    //no
                        //아무일 x
        if (true) {   //월요일이면,
            if(true) {  //전주에 획득한 포인트의 총 합이 200pt 미만이면,
                builder = new AlertDialog.Builder(this);

                builder.setTitle("저번주에는 아쉽게도 포인트를 많이 획득하지 못하셨군요ㅠㅠ").setMessage("이번주에는 승용차를 이용하는 대신 짧은 거리는 걸어보는 것이 어떨까요? 승용차를 일주일에 하루 덜 타면 연간 445kg의 이산화탄소를 줄일 수 있다고 해요! (일요일까지 2만 걸음을 걸으시면 추가 포인트가 지급됩니다.)");

                /**걸음수 측정 yes**/
                builder.setPositiveButton("걸음 수 측정하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplicationContext(), "걸음 수 측정 시작", Toast.LENGTH_SHORT).show();
                        step_goal.setVisibility(View.VISIBLE);
                        step_current.setVisibility(View.VISIBLE);

                        //측정시작
                        sensorManager.registerListener(WeightActivity.this, stepCountSensor, SensorManager.SENSOR_DELAY_NORMAL);

                        Log.v("service", "측정시작");
                        setStartService();




                    }
                });

                /**걸음수 측정 no**/
                builder.setNegativeButton("다음에", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplicationContext(), "걸음 수 측정 취소", Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialog = builder.create();
                alertDialog.show();
            }
        }








        //내부저장소에 저장된 날짜 삭제
        PreferenceManager.removeKey(WeightActivity.this, "prevDateStr");
        PreferenceManager.removeKey(WeightActivity.this, "recentDateStr");

        //마지막 측정 날짜 출력
        String lastmeasuredate = PreferenceManager.getString(WeightActivity.this, "recentDateStr");
        if(!lastmeasuredate.equals("")) {
            String[] splitdate = lastmeasuredate.split("-");
            String year = splitdate[0];
            String month = splitdate[1];
            String day = splitdate[2];
            measure_day.setText("마지막 측정 날짜 : " + year + "년 " + month + "월 " + day + "일 ");
        }

//        StringRequest Requesttoloadcell = new StringRequest(Request.Method.POST, "http://polarbear1022.dothome.co.kr/loadcelltest.php",
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//            }
//        }){
//            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//
//                Log.v("bbb", "uid(내부저장소) : " + uid);
//                params.put("uid", uid);
//                return params;
//            }
//        };
//        RequestQueue queuetoloadcell = Volley.newRequestQueue(WeightActivity.this);
//        queuetoloadcell.add(Requesttoloadcell);

        /**무게재기**/
        View.OnClickListener weightlistener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                prevDateStr = PreferenceManager.getString(WeightActivity.this, "prevDateStr");
                recentDateStr = PreferenceManager.getString(WeightActivity.this, "recentDateStr");
                Log.v("aaa", "prevdatestr(내부저장소) : " + prevDateStr);
                Log.v("aaa", "recentdatestr(내부저장소) : " + recentDateStr);

                //무게를 처음 측정하는 경우
                /************************************************************************************************/
                /************************************************************************************************/
                if (recentDateStr.equals("")) {

                    Log.v("aaa", "무게첫측정");

                    //5초 대기 다이얼로그창
                    builder2 = new AlertDialog.Builder(WeightActivity.this);
                    builder2.setTitle("5초 후에 음식물 쓰레기 봉투를 올려주세요");
                    builder2.setNegativeButton("취소하기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            /**
                             * 무게재기 취소
                             * **/
                            Toast.makeText(getApplicationContext(), "무게측정 취소", Toast.LENGTH_SHORT).show();
                        }
                    });
                    alertDialog2 = builder2.create();
                    alertDialog2.show();
                    countDownTimer = new CountDownTimer(5 * 1000, 1000) {
                        @Override
                        public void onTick(long l) {
                            count--;
                        }

                        @Override
                        public void onFinish() {
                            alertDialog2.dismiss();
                        }
                    };
                    countDownTimer.start();

                    //무게 측정
                    /************************************************************************************************/
                    /************************************************************************************************/
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://polarbear1022.dothome.co.kr/weight.php",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    //결과 처리
                                    try {
                                        JSONObject jsonResponse = new JSONObject(response);
                                        weightValue = jsonResponse.getInt("weight");

                                        recentDateStr = jsonResponse.getString("date");
                                        String[] splitdate = recentDateStr.split("-");
                                        String year = splitdate[0];
                                        String month = splitdate[1];
                                        String day = splitdate[2];
                                        weight.setText("무게 : " + weightValue + "g");
                                        measure_day.setText("마지막 측정 날짜 : " + year + "년 " + month + "월 " + day + "일 ");

                                        PreferenceManager.setString(WeightActivity.this, "prevDateStr", prevDateStr); //prevDateStr
                                        PreferenceManager.setString(WeightActivity.this, "recentDateStr", "2020-08-10"); //recentDateStr
                                        builder3 = new AlertDialog.Builder(WeightActivity.this);
                                        builder3.setTitle("첫 무게 측정을 축하드립니다!").setMessage("첫 무게 측정 기념으로 100포인트를 지급합니다.");
                                        builder3.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int id) {
                                                Toast.makeText(getApplicationContext(), "100포인트 획득", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        alertDialog = builder3.create();
                                        alertDialog.show();

                                        Log.v("aaa", "weightvalue : " + weightValue);

                                        point = 100;

                                        /**
                                         * 무게값, 포인트값 디비에 저장
                                         * **/
                                        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://polarbear1022.dothome.co.kr/weightresult.php",
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                    }
                                                }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                            }
                                        }){
                                            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                                                Map<String, String> params = new HashMap<String, String>();

                                                Log.v("aaa", "weight(내부저장소) : " + weightValue);
                                                Log.v("aaa", "point(내부저장소) : " + point);

                                                params.put("uid", uid);
                                                params.put("weight", ""+weightValue);
                                                params.put("point", ""+point);
                                                return params;
                                            }
                                        };
                                        RequestQueue queue = Volley.newRequestQueue(WeightActivity.this);
                                        queue.add(stringRequest);


                                        /**
                                         * 현재 보유 포인트에 더하기
                                         * **/

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            }){
                        protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();

                            params.put("uid", uid);
                            return params;
                        }
                    };
                    RequestQueue queue = Volley.newRequestQueue(WeightActivity.this);
                    queue.add(stringRequest);
                    /************************************************************************************************/
                    /************************************************************************************************/

                }
                /************************************************************************************************/
                /************************************************************************************************/
                //무게를 이전에 측정한 적이 있는 경우
                else {
                        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                        Date today = new Date();
                        /**마지막 측정 날짜가 오늘이면**/
                        /************************************************************************************************/
                        /************************************************************************************************/
                        if (recentDateStr.equals(format1.format(today))) {
                            Log.v("aaa", "prevdatestr(마지막 측정 날짜가 오늘) : " + prevDateStr);
                            Log.v("aaa", "recentdatestr(마지막 측정 날짜가 오늘) : " + recentDateStr);
                            //무게 측정 안함
                            Toast.makeText(getApplicationContext(), "무게는 하루에 한 번만 측정하실 수 있습니다.", Toast.LENGTH_SHORT).show();
                        }
                        /************************************************************************************************/
                        /************************************************************************************************/
                        /**마지막 측정 날짜가 오늘이 아니면**/
                        else {
                            //무게재기
                            //5초 대기 다이얼로그창
                            builder2 = new AlertDialog.Builder(WeightActivity.this);
                            builder2.setTitle("5초 후에 음식물 쓰레기 봉투를 올려주세요");
                            builder2.setNegativeButton("취소하기", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    /**
                                     * 무게재기 취소
                                     * **/
                                    Toast.makeText(getApplicationContext(), "무게측정 취소", Toast.LENGTH_SHORT).show();
                                }
                            });
                            alertDialog2 = builder2.create();
                            alertDialog2.show();
                            countDownTimer = new CountDownTimer(5 * 1000, 1000) {
                                @Override
                                public void onTick(long l) {
                                    count--;
                                }

                                @Override
                                public void onFinish() {
                                    alertDialog2.dismiss();
                                }
                            };
                            countDownTimer.start();

                            //무게 측정
                            /************************************************************************************************/
                            /************************************************************************************************/
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://polarbear1022.dothome.co.kr/weight.php",
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            //결과 처리
                                            try {
                                                JSONObject jsonResponse = new JSONObject(response);
                                                weightValue = jsonResponse.getInt("weight");
                                                prevDateStr = recentDateStr;
                                                recentDateStr = jsonResponse.getString("date");
                                                String[] splitdate = recentDateStr.split("-");
                                                String year = splitdate[0];
                                                String month = splitdate[1];
                                                String day = splitdate[2];
                                                weight.setText("무게 : " + weightValue + "g");
                                                measure_day.setText("마지막 측정 날짜 : " + year + "년 " + month + "월 " + day + "일 ");

                                                PreferenceManager.setString(WeightActivity.this, "prevDateStr", prevDateStr);
                                                PreferenceManager.setString(WeightActivity.this, "recentDateStr", recentDateStr);

                                                Log.v("aaa", "prevdatestr(달라야함) : " + prevDateStr);
                                                Log.v("aaa", "recentdatestr(달라야함) : " + recentDateStr);
                                                Log.v("aaa", "weightvalue : " + weightValue);


                                                try {
                                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                                    Date prevdate = format.parse(prevDateStr);
                                                    Date presentdate = format.parse(recentDateStr);

                                                    long calDate = presentdate.getTime() - prevdate.getTime();
                                                    long calDateDays = calDate / (24 * 60 * 60 * 1000);

                                                    calDateDays = Math.abs(calDateDays);

                                                    weightPerDay = weightValue / (int) calDateDays;

                                                    Log.v("aaa", "weightvalue : " + weightValue + "  weightPerDay : " + weightPerDay + "  calDateDays : " + calDateDays);
                                                    if (weightPerDay > 0 && weightPerDay < 100) point = 100;
                                                    else if (weightPerDay >= 100 && weightPerDay < 200) point = 90;
                                                    else if (weightPerDay >= 200 && weightPerDay < 300) point = 80;
                                                    else if (weightPerDay >= 300 && weightPerDay < 400) point = 70;
                                                    else if (weightPerDay >= 400 && weightPerDay < 500) point = 60;
                                                    else if (weightPerDay >= 500 && weightPerDay < 600) point = 50;
                                                    else if (weightPerDay >= 600 && weightPerDay < 700) point = 40;
                                                    else if (weightPerDay >= 700 && weightPerDay < 800) point = 30;
                                                    else if (weightPerDay >= 800 && weightPerDay < 900) point = 20;
                                                    else if (weightPerDay >= 900 && weightPerDay < 1000) point = 10;
                                                    else if (weightPerDay >= 1000) point = 5;
                                                    //현재 보유 포인트에 point 더하기
                                                    Log.v("aaa", "point : " + point);
                                                    Toast.makeText(getApplicationContext(),point +"포인트 획득", Toast.LENGTH_SHORT).show();

                                                    /**
                                                     * 무게값, 포인트값 디비에 저장
                                                     * **/
                                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://polarbear1022.dothome.co.kr/weightresult.php",
                                                            new Response.Listener<String>() {
                                                                @Override
                                                                public void onResponse(String response) {
                                                                }
                                                            }, new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                        }
                                                    }){
                                                        protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                                                            Map<String, String> params = new HashMap<String, String>();

                                                            Log.v("aaa", "weight(내부저장소) : " + weightValue);
                                                            Log.v("aaa", "point(내부저장소) : " + point);

                                                            params.put("uid", uid);
                                                            params.put("weight", ""+weightValue);
                                                            params.put("point", ""+point);
                                                            return params;
                                                        }
                                                    };
                                                    RequestQueue queue = Volley.newRequestQueue(WeightActivity.this);
                                                    queue.add(stringRequest);

                                                    /**
                                                     * 현재 보유 포인트에 더하기
                                                     * **/

                                                }catch (ParseException e){}
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                        }
                                    }){
                                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                                    Map<String, String> params = new HashMap<String, String>();

                                    params.put("uid", uid);
                                    return params;
                                }
                            };
                            RequestQueue queue = Volley.newRequestQueue(WeightActivity.this);
                            queue.add(stringRequest);
                            /************************************************************************************************/
                            /************************************************************************************************/


                        }
                        /************************************************************************************************/
                        /************************************************************************************************/
                }
            }
        };
        button.setOnClickListener(weightlistener);



        Intent resultintent = new Intent();
        resultintent.putExtra("결과", "무게값, 포인트값 등");

        setResult(0, resultintent); //자신을 실행한 액티비티에게 돌려줄 결과


    }

    public void menuOnClick(View v) {
        switch (v.getId()) {
            case R.id.drawer_weight:
                Intent weight = new Intent(WeightActivity.this, WeightActivity.class);
                startActivity(weight);
                break;
            case R.id.drawer_graph:
                Intent graph = new Intent(WeightActivity.this, GraphActivity.class);
                startActivity(graph);
                break;
            case R.id.drawer_donate:
                Intent donate = new Intent(WeightActivity.this, DonateActivity.class);
                startActivity(donate);
                break;
            case R.id.drawer_ranking:
                Intent ranking = new Intent(WeightActivity.this, RankingActivity.class);
                startActivity(ranking);
                break;
            case R.id.drawer_shop:
                Intent shop = new Intent(WeightActivity.this, ShopActivity.class);
                startActivity(shop);
                break;
            case R.id.drawer_mypage:
                Intent mypage = new Intent(WeightActivity.this, MypageActivity.class);
                startActivity(mypage);
                break;
            case R.id.drawer_main:
                Intent main = new Intent(WeightActivity.this, MainActivity.class);
                startActivity(main);
                break;

        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            step_current.setText("현재 걸음걸이 수 : " + (int) event.values[0]);

            if(!isService){
                return;
            }
            scService.setCurrentStep((int) event.values[0]);

            if((int) event.values[0] >= 1000) {/**2만보 채웠거나 일요일이면**/
                //20000보 채우면,
                //포인트 획득, 측정 종료
                //일요일이면,
                //측정 종료
                unbindService(conn);
                isService = false;
                Log.v("service", "서비스종료");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private ServiceConnection conn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 서비스와 연결되었을 때 호출되는 메서드
            // 서비스 객체를 전역변수로 저장
            StepcountService.MyBinder mBinder = (StepcountService.MyBinder) service;
            scService = mBinder.getService(); // 서비스가 제공하는 메소드 호출하여
            // 서비스쪽 객체를 전달받을수 있슴
            isService = true;
        }
        public void onServiceDisconnected(ComponentName name) {
            // 서비스와 연결이 끊겼을 때 호출되는 메서드
            isService = false;
        }
    };

    private void setStartService() {
        Log.v("service", "setstratservice()");
        Intent intent = new Intent(WeightActivity.this, StepcountService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        startService(intent);
        Log.v("service", "bindService");
        mIsBound = true;
    }

}
