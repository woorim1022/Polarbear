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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.PowerManager;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;


public class WeightActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private View drawerView;
    private TextView weight;
    private Button button;
    private TextView textView4;
    private TextView measure_day;
    private TextView step_goal;
    private TextView step_current;
    private TextView last_point;

    private int count = 6;
    private CountDownTimer countDownTimer;

    AlertDialog.Builder builder, builder2, builder3;
    AlertDialog alertDialog, alertDialog2;

    private int weightValue;
    private String prevDateStr;
    private String recentDateStr = "";
    private String lastpoint;
    private String weeklyPoint;

    private int weightPerDay;
    private int point;

    private String uid;
    private String uname;

    private Intent serviceIntent;

    private StepcountService stepService; // 서비스 클래스 객체를 선언
    boolean isService = false; // 서비스 중인 확인용

    private static int currentstep;

    private String dialogCount = "false";

    private StepCallback stepCallback = new StepCallback() { //서비스 내부로 Set되어 스텝카운트의 변화와 Unbind의 결과를 전달하는 콜백 객체의 구현체
        @Override
        public void onStepCallback(int step) {
            currentstep = step;
            step_current.setText("현재 걸음 수 : " + currentstep + " 보");
            if(step >= 20000)   //20000
                stopCount(currentstep);
        }

        @Override
        public void onUnbindService() {
            isService = false;
            Toast.makeText(WeightActivity.this, "디스바인딩", Toast.LENGTH_SHORT).show();
        }
    };

    private ServiceConnection serviceConnection = new ServiceConnection() { //서비스 바인드를 담당하는 객체의 구현체
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Toast.makeText( WeightActivity.this, "예스바인딩", Toast.LENGTH_SHORT).show();
            StepcountService.MyBinder mb = (StepcountService.MyBinder) service;
            stepService = mb.getService(); //
            stepService.setCallback(stepCallback);
            isService = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) { //요거는 사실상 서비스가 킬되거나 아예 죽임 당했을 때만 호출된다고 보시면 됨
// stopService 또는 unBindService때 호출되지 않음.
            isService = false;
            Toast.makeText(WeightActivity.this, "디스바인딩", Toast.LENGTH_SHORT).show();
        }
    };

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
        textView4 = (TextView) findViewById(R.id.textView4);
        last_point = (TextView) findViewById(R.id.last_point);


        uid = PreferenceManager.getString(this, "userID");
        uname = PreferenceManager.getString(this, "userNAME");


        final Intent intent = getIntent();  //메인 화면에서 넘어온 intent 받음
        String datafrommain = intent.getStringExtra("메인 액티비티에서 넘길 정보"); //pustExtra로 지정했던 데이터의 키값을 지정하면 해당하는 데이터 값이 나오게 됨


        //절전 모드를 사용하지 않는 예외 앱으로 처리
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(POWER_SERVICE);
        boolean isWhiteListing = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            isWhiteListing = pm.isIgnoringBatteryOptimizations(getApplicationContext().getPackageName());
        }
        if (!isWhiteListing) {
            Intent intent2 = new Intent();
            intent2.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent2.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
            startActivity(intent2);
        }


        weeklyPoint = PreferenceManager.getString(WeightActivity.this, "weeklyPoint");
        dialogCount = PreferenceManager.getString(WeightActivity.this, "dialogCount");
        if(dialogCount.equals(""))
            dialogCount = "false";
        Log.v("aaa", "dialogCount : " + dialogCount);
        Log.v("aaa", "일주일 누적 포인트 " + weeklyPoint);
        final String stepPref = PreferenceManager.getString(WeightActivity.this, "STEPCOUNT");


        Calendar cal = Calendar.getInstance();
        int nWeek = cal.get(Calendar.DAY_OF_WEEK);


        /**걸음수 측정**/
        if (nWeek == 2) {   //월요일이면,
            if(dialogCount.equals("false")) {  //오늘 아직 다이얼로그를 띄우지 않았으면
                PreferenceManager.setString(WeightActivity.this, "dialogCount", "true");
                if (!weeklyPoint.equals("")) {
                    if (parseInt(weeklyPoint) < 200) {  //전주에 획득한 포인트의 총 합이 200pt 미만이면,
                        builder = new AlertDialog.Builder(this);

                        builder.setTitle("저번주에는 아쉽게도 포인트를 많이 획득하지 못하셨군요ㅠㅠ").setMessage("이번주에는 승용차를 이용하는 대신 짧은 거리는 걸어보는 것이 어떨까요? 승용차를 일주일에 하루 덜 타면 연간 445kg의 이산화탄소를 줄일 수 있다고 해요! (일요일까지 2만 걸음을 걸으시면 추가 포인트가 지급됩니다.)");

                        /**걸음수 측정 yes**/
                        builder.setPositiveButton("걸음 수 측정하기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(getApplicationContext(), "걸음 수 측정 시작", Toast.LENGTH_SHORT).show();
                                step_goal.setVisibility(View.VISIBLE);
                                step_current.setVisibility(View.VISIBLE);

                                textView4.setText("걸음수를 측정 중입니다.");
                                /**
                                 *  현재 걸음 수 가져와서 화면에 표시
                                 */
                                Log.v("aaa", "현재 걸음 수" + stepPref);
                                if (!stepPref.equals("")) {
                                    step_current.setText("현재 걸음 수(pref) : " + parseInt(stepPref) + " 보");
                                } else {
                                    step_current.setText("현재 걸음 수(pref) : " + 0 + " 보");
                                }
                                //서비스 시작하기
                                if (StepcountService.serviceIntent == null) {
                                    serviceIntent = new Intent(WeightActivity.this, StepcountService.class);
                                    startService(serviceIntent);
                                    bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
                                } else {
                                    serviceIntent = StepcountService.serviceIntent;//getInstance().getApplication();
                                    bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
                                    Toast.makeText(getApplicationContext(), "already", Toast.LENGTH_LONG).show();
                                }

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
                PreferenceManager.setString(WeightActivity.this, "weeklyPoint", "" + 0);
            }

        }
        if(nWeek == 1) {//일요일이면
            stopCount(parseInt(stepPref));
            PreferenceManager.setString(WeightActivity.this, "dialogCount", "false");
        }





        //내부저장소에 저장된 날짜 삭제
//        PreferenceManager.removeKey(WeightActivity.this, "prevDateStr");
//        PreferenceManager.removeKey(WeightActivity.this, "recentDateStr");
//        PreferenceManager.removeKey(WeightActivity.this, "lastPoint");

        //마지막 측정 날짜 출력
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();

        String todayWeight = PreferenceManager.getString(WeightActivity.this, "todayWeight");
        String lastmeasuredate = PreferenceManager.getString(WeightActivity.this, "recentDateStr");
        if(!lastmeasuredate.equals("")) {
            String[] splitdate = lastmeasuredate.split("-");
            String year = splitdate[0];
            String month = splitdate[1];
            String day = splitdate[2];
            measure_day.setText("마지막 측정 날짜 : " + year + "년 " + month + "월 " + day + "일 ");
            if(lastmeasuredate.equals(format1.format(today))){ //마지막 측정 날짜가 오늘이면
                weight.setText("" + todayWeight + " g");
            }
        }

        String lastpoint = PreferenceManager.getString(WeightActivity.this, "lastPoint");
        if(!lastpoint.equals("")) {
            last_point.setText("마지막 획득 포인트 : " + lastpoint);
        }


        /**무게재기**/
        View.OnClickListener weightlistener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                prevDateStr = PreferenceManager.getString(WeightActivity.this, "prevDateStr");
                recentDateStr = PreferenceManager.getString(WeightActivity.this, "recentDateStr");
                Log.v("aaa", "prevdatestr(내부저장소) : " + prevDateStr);
                Log.v("aaa", "recentdatestr(내부저장소) : " + recentDateStr);

                //무게를 처음 측정하는 경우
                if (recentDateStr.equals("")) {

                    Log.v("aaa", "무게첫측정");

                    //5초 대기 다이얼로그창
//                    builder2 = new AlertDialog.Builder(WeightActivity.this);
//                    builder2.setTitle("5초 후에 음식물 쓰레기 봉투를 올려주세요");
//                    builder2.setNegativeButton("취소하기", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int id) {
//                            /**
//                             * 무게재기 취소
//                             * **/
//                            Toast.makeText(getApplicationContext(), "무게측정 취소", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                    alertDialog2 = builder2.create();
//                    alertDialog2.show();
//                    countDownTimer = new CountDownTimer(5 * 1000, 1000) {
//                        @Override
//                        public void onTick(long l) {
//                            count--;
//                        }
//
//                        @Override
//                        public void onFinish() {
//                            alertDialog2.dismiss();
//                        }
//                    };
//                    countDownTimer.start();

                    //무게 측정
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
                                        weight.setText("" + weightValue + " g");
                                        measure_day.setText("마지막 측정 날짜 : " + year + "년 " + month + "월 " + day + "일 ");


                                        PreferenceManager.setString(WeightActivity.this, "prevDateStr", prevDateStr); //prevDateStr
                                        PreferenceManager.setString(WeightActivity.this, "recentDateStr", recentDateStr); //recentDateStr
                                        PreferenceManager.setString(WeightActivity.this, "todayWeight", ""+weightValue);

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
                                        PreferenceManager.setString(WeightActivity.this, "lastPoint", "" + 100);
                                        PreferenceManager.setString(WeightActivity.this, "weeklyPoint", "" + 100);
                                        last_point.setText("마지막 획득 포인트 : 100");

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
                                                params.put("uname", uname);
                                                params.put("weight", ""+weightValue);
                                                params.put("point", ""+point);
                                                return params;
                                            }
                                        };
                                        RequestQueue queue = Volley.newRequestQueue(WeightActivity.this);
                                        queue.add(stringRequest);



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


                }
                //무게를 이전에 측정한 적이 있는 경우
                else {
                        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
                        Date today = new Date();
                        /**마지막 측정 날짜가 오늘이면**/
                        if (recentDateStr.equals(format1.format(today))) {
                            Log.v("aaa", "prevdatestr(마지막 측정 날짜가 오늘) : " + prevDateStr);
                            Log.v("aaa", "recentdatestr(마지막 측정 날짜가 오늘) : " + recentDateStr);
                            //무게 측정 안함
                            Toast.makeText(getApplicationContext(), "무게는 하루에 한 번만 측정하실 수 있습니다.", Toast.LENGTH_SHORT).show();
                        }
                        /**마지막 측정 날짜가 오늘이 아니면**/
                        else {
                            //무게재기
                            //5초 대기 다이얼로그창
//                            builder2 = new AlertDialog.Builder(WeightActivity.this);
//                            builder2.setTitle("5초 후에 음식물 쓰레기 봉투를 올려주세요");
//                            builder2.setNegativeButton("취소하기", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int id) {
//                                    /**
//                                     * 무게재기 취소
//                                     * **/
//                                    Toast.makeText(getApplicationContext(), "무게측정 취소", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                            alertDialog2 = builder2.create();
//                            alertDialog2.show();
//                            countDownTimer = new CountDownTimer(5 * 1000, 1000) {
//                                @Override
//                                public void onTick(long l) {
//                                    count--;
//                                }
//
//                                @Override
//                                public void onFinish() {
//                                    alertDialog2.dismiss();
//                                }
//                            };
//                            countDownTimer.start();

                            //무게 측정
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
                                                weight.setText("" + weightValue + " g");
                                                measure_day.setText("마지막 측정 날짜 : " + year + "년 " + month + "월 " + day + "일 ");

                                                PreferenceManager.setString(WeightActivity.this, "prevDateStr", prevDateStr);
                                                PreferenceManager.setString(WeightActivity.this, "recentDateStr", recentDateStr);
                                                PreferenceManager.setString(WeightActivity.this, "todayWeight", ""+weightValue);

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
                                                    PreferenceManager.setString(WeightActivity.this, "lastPoint", "" + point);
                                                    PreferenceManager.setString(WeightActivity.this, "weeklyPoint", "" + point);
                                                    last_point.setText("마지막 획득 포인트 : " + point);

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
                                                            params.put("uname", uname);
                                                            params.put("weight", ""+weightValue);
                                                            params.put("point", ""+point);
                                                            return params;
                                                        }
                                                    };
                                                    RequestQueue queue = Volley.newRequestQueue(WeightActivity.this);
                                                    queue.add(stringRequest);

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


                        }
                }
            }
        };
        button.setOnClickListener(weightlistener);

        Intent resultintent = new Intent();
        resultintent.putExtra("결과", "무게값, 포인트값 등");
        setResult(0, resultintent); //자신을 실행한 액티비티에게 돌려줄 결과

    }

    protected void stopCount(int currentstep) {
        Log.v("@@@", "stopCount");
         /**일요일이거나 2만보 채우면**/
             if (serviceIntent != null) {
                 stopService(serviceIntent);
                 serviceIntent = null;
                 if(currentstep >= 20000) {    //20000
                     Log.v("@@@", "추가 포인트 지급");
                     /**
                      * 추가 포인트 지급
                      */
                     StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://polarbear1022.dothome.co.kr/steppoint.php",
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
                             params.put("uid", uid);
                             params.put("uname", uname);
                             params.put("point", ""+30);
                             return params;
                         }
                     };
                     RequestQueue queue = Volley.newRequestQueue(WeightActivity.this);
                     queue.add(stringRequest);

                     /**
                      * 2만 보 채운 축하메시지 띄우기
                      * 토스트로 포인트 지급 알리기
                      * **/
                 }
         }
    }

    @Override
    protected void onDestroy() {
        Log.v("@@@", "걸음 onDestroy(weightactivity)");
        stopService(serviceIntent);
        super.onDestroy();

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


}
