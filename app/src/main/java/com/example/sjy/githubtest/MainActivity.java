package com.example.sjy.githubtest;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private View drawerView;
    private static String IP_ADDRESS = "IP주소";
    private static String TAG = "phptest";
    private String userId;
    private String used_point;
    private String donate_count;
    private String userLevel, userExp;
    private String numApple, numFish, numMeat, numIce;
    private int iApple, iFish, iMeat, iIce, iLevel, iExp, maxExp, nextExp;
    private Context mContext;
    private TextView username, level, appleCount, fishCount, meatCount, iceCount;
    private ImageView badge1, badge2, badge3;
    private ImageView btnApple, btnFish, btnMeat, btnIce;
    private ImageView polarbear;
    private ProgressBar expbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        long startTime = System.currentTimeMillis();

        //메뉴 버튼 클릭
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerView = (View)findViewById(R.id.drawerView);

        ImageView openDrawer = (ImageView)findViewById(R.id.menu_button);

        mContext = this;

        username = (TextView)findViewById(R.id.username);
        level = (TextView)findViewById(R.id.level);
        appleCount = (TextView)findViewById(R.id.num_apple);
        fishCount = (TextView)findViewById(R.id.num_fish);
        meatCount = (TextView)findViewById(R.id.num_meat);
        iceCount = (TextView)findViewById(R.id.num_ice);
        btnApple = (ImageView)findViewById(R.id.btn_apple);
        btnFish = (ImageView)findViewById(R.id.btn_fish);
        btnMeat = (ImageView)findViewById(R.id.btn_meat);
        btnIce = (ImageView)findViewById(R.id.btn_ice);
        polarbear = (ImageView)findViewById(R.id.polarbear);
        expbar = (ProgressBar)findViewById(R.id.exp_bar);

        openDrawer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });

        /**Sharedpreference 클래스 이용해서 내부 저장소에 저장되어있는 userid, username 값 가져오기**/
        String uid = PreferenceManager.getString(mContext, "userID");
        String uname = PreferenceManager.getString(mContext, "userNAME");
        Log.v("Sharedpreferences", "userID : " + uid);
        Log.v("Sharedpreferences", "userNAME : " + uname);
        /**만약 uid 값이 없으면, 회원가입을 위해 SignActivity.class 로 이동**/
        if(uid == ""){
            Intent signup = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(signup);
        }
        else{
            username.setVisibility(View.VISIBLE);
            username.setText(uname + "님, 환영합니다");
        }

        //userID로 디비에 검색해서 포인트, 경험치 정보 가져오기
        //경험치 값 만큼 레벨 환산, 경험치 바 채우기
        
        //조건에 맞는 배지만 노출되도록 설정 (VISIBLE)
        badge1 = (ImageView)findViewById(R.id.badge01);
        badge2 = (ImageView)findViewById(R.id.badge02);
        badge3 = (ImageView)findViewById(R.id.badge03);
        showBadge();

        checkUserInfo();
        checkIteminfo();
        //실행 시간 측정
        long endTime = System.currentTimeMillis();
        Log.d("timecheck", "onCreate:" + (endTime - startTime));
    }

    //뱃지 노출 설정
    public void showBadge(){
        userId = PreferenceManager.getString(mContext, "userID");

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                /**결과 처리**/
                String TAG_INFO = "badgeinfo";
                String TAG_COUNT = "donatecount";
                String TAG_USED = "usedpoint";
                try {
                    JSONObject jsonResponse = new JSONObject(response);//response : 서버로 부터 받은 결과 (badge.php 의 $response 에 담겨있는 값)
                    JSONArray jsonArray = jsonResponse.getJSONArray(TAG_INFO);

                    JSONObject firstItem = jsonArray.getJSONObject(0);

                    donate_count = firstItem.getString(TAG_COUNT);
                    used_point = firstItem.getString(TAG_USED);

                    int usedpoint = Integer.parseInt(used_point);
                    int donatecount = Integer.parseInt(donate_count);

                    Log.v("aaa", "기부에 사용한 포인트 : " + usedpoint);
                    Log.v("aaa", "기부 횟수 : " + donatecount);

                    if(donatecount >= 1) {
                        badge1.setVisibility(View.VISIBLE);
                    }
                    if(donatecount >= 10){
                        badge2.setVisibility(View.VISIBLE);
                    }
                    if(usedpoint >= 5000){
                        badge3.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        /**서버로 volley를 이용하여 요청을 함**/
        BadgeRequest badgeRequest = new BadgeRequest(userId, responseListener);     //Request 클래스를 이용하여 서버 요청 정보와 결과 처리 방법을 표현
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);       //서버 요청자, 다른 request 클래스들의 정보대로 서버에 요청을 보내는 역할
        queue.add(badgeRequest);
    }

    //메뉴 클릭
    public void menuOnClick(View v) {
        switch(v.getId()){
            case R.id.drawer_weight:
                Intent weight = new Intent(MainActivity.this, WeightActivity.class);
                startActivity(weight);
                break;
            case R.id.drawer_graph:
                Intent graph = new Intent(MainActivity.this, GraphActivity.class);
                startActivity(graph);
                break;
            case R.id.drawer_donate:
                Intent donate = new Intent(MainActivity.this, DonateActivity.class);
                startActivity(donate);
                break;
            case R.id.drawer_ranking:
                Intent ranking = new Intent(MainActivity.this, RankingActivity.class);
                startActivity(ranking);
                break;
            case R.id.drawer_shop:
                Intent shop = new Intent(MainActivity.this, ShopActivity.class);
                startActivity(shop);
                break;
            case R.id.drawer_mypage:
                Intent mypage = new Intent(MainActivity.this, MypageActivity.class);
                startActivity(mypage);
                break;
            case R.id.drawer_main:
                Intent main = new Intent(MainActivity.this, MainActivity.class);
                startActivity(main);
                break;

        }
    }

    //유저 정보 갱신
    public void checkUserInfo() {
        userId = PreferenceManager.getString(mContext, "userID");

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                /**결과 처리**/
                String TAG_INFO = "userinfo";
                String TAG_LEVEL = "ulevel";
                String TAG_EXP = "uexp";
                try {
                    JSONObject jsonResponse = new JSONObject(response);//response : 서버로 부터 받은 결과 (userinfo.php 의 $response 에 담겨있는 값)
                    JSONArray jsonArray = jsonResponse.getJSONArray(TAG_INFO);

                    JSONObject firstItem = jsonArray.getJSONObject(0);

                    //userLevel = firstItem.getString(TAG_LEVEL);
                    userExp = firstItem.getString(TAG_EXP);
                    returnLevel();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        /**서버로 volley를 이용하여 요청을 함**/
        UserRequest userRequest = new UserRequest(userId, responseListener);     //Request 클래스를 이용하여 서버 요청 정보와 결과 처리 방법을 표현
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);  //서버 요청자, 다른 request 클래스들의 정보대로 서버에 요청을 보내는 역할
        queue.add(userRequest);

    }

    //경험치를 레벨로 표시
    public void returnLevel() {
        iExp = Integer.parseInt(userExp);

        if(iExp < 100) {iLevel = 1; nextExp = iExp; maxExp = 100;}
        else if(iExp >= 100 && iExp < 200) {iLevel = 2; nextExp = iExp - 100; maxExp = 100;}
        else if(iExp >= 200 && iExp < 300) {iLevel = 3; nextExp = iExp - 200; maxExp = 100;}
        else if(iExp >= 300 && iExp < 400) {iLevel = 4; nextExp = iExp - 300; maxExp = 100;}
        else if(iExp >= 400 && iExp < 500) {iLevel = 5; nextExp = iExp - 400; maxExp = 100;}
        else if(iExp >= 500 && iExp < 600) {iLevel = 6; nextExp = iExp - 500; maxExp = 100;}
        else if(iExp >= 600 && iExp < 700) {iLevel = 7; nextExp = iExp - 600; maxExp = 100;}
        else if(iExp >= 700 && iExp < 800) {iLevel = 8; nextExp = iExp - 700; maxExp = 100;}
        else if(iExp >= 800 && iExp < 900) {iLevel = 9; nextExp = iExp - 800; maxExp = 100;}
        else if(iExp >= 900 && iExp < 1100) {iLevel = 10; nextExp = iExp - 900; maxExp = 200;}
        else if(iExp >= 1100 && iExp < 1300) {iLevel = 11; nextExp = iExp - 1100; maxExp = 200;}
        else if(iExp >= 1300 && iExp < 1500) {iLevel = 12; nextExp = iExp - 1300; maxExp = 200;}
        else if(iExp >= 1500 && iExp < 1700) {iLevel = 13; nextExp = iExp - 1500; maxExp = 200;}
        else if(iExp >= 1700 && iExp < 1900) {iLevel = 14; nextExp = iExp - 1700; maxExp = 200;}
        else if(iExp >= 1900 && iExp < 2100) {iLevel = 15; nextExp = iExp - 1900; maxExp = 200;}
        else if(iExp >= 2100 && iExp < 2300) {iLevel = 16; nextExp = iExp - 2100; maxExp = 200;}
        else if(iExp >= 2300 && iExp < 2500) {iLevel = 17; nextExp = iExp - 2300; maxExp = 200;}
        else if(iExp >= 2500 && iExp < 2700) {iLevel = 18; nextExp = iExp - 2500; maxExp = 200;}
        else if(iExp >= 2700 && iExp < 2900) {iLevel = 19; nextExp = iExp - 2700; maxExp = 200;}
        else if(iExp >= 2900 && iExp < 3200) {iLevel = 20; nextExp = iExp - 2900; maxExp = 300;}
        else if(iExp >= 3200 && iExp < 3500) {iLevel = 21; nextExp = iExp - 3200; maxExp = 300;}
        else if(iExp >= 3500 && iExp < 3800) {iLevel = 22; nextExp = iExp - 3500; maxExp = 300;}
        else if(iExp >= 3800 && iExp < 4100) {iLevel = 23; nextExp = iExp - 3800; maxExp = 300;}
        else if(iExp >= 4100 && iExp < 4400) {iLevel = 24; nextExp = iExp - 4100; maxExp = 300;}
        else if(iExp >= 4400 && iExp < 4700) {iLevel = 25; nextExp = iExp - 4400; maxExp = 300;}
        else if(iExp >= 4700 && iExp < 5000) {iLevel = 26; nextExp = iExp - 4700; maxExp = 300;}
        else if(iExp >= 5000 && iExp < 5300) {iLevel = 27; nextExp = iExp - 5000; maxExp = 300;}
        else if(iExp >= 5300 && iExp < 5600) {iLevel = 28; nextExp = iExp - 5300; maxExp = 300;}
        else if(iExp >= 5600 && iExp < 5900) {iLevel = 29; nextExp = iExp - 5600; maxExp = 300;}
        else if(iExp >= 5900) {iLevel = 30; nextExp = iExp; maxExp = iExp;}

        showPolarbear();
        expbar.setMax(maxExp);
        expbar.setProgress(nextExp);

        userLevel = Integer.toString(iLevel);
        sendLevel();

        level.setText("레벨 " + userLevel);
    }

    //레벨에 따라 북극곰 이미지 표시
    public void showPolarbear() {
        if(iLevel < 10) polarbear.setImageResource(R.drawable.baby_polarbear);
        else if(iLevel >= 10 && iLevel < 20) polarbear.setImageResource(R.drawable.middle_polarbear);
        else if(iLevel >= 20) polarbear.setImageResource(R.drawable.adult_polarbear);
    }

   //아이템 개수 불러오기
    public void checkIteminfo() {

        Response.Listener<String> itemresponseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                /**결과 처리**/
                String TAG_INFO = "iteminfo";
                String TAG_APPLE = "apple";
                String TAG_FISH = "fish";
                String TAG_MEAT = "meat";
                String TAG_ICE = "ice";

                try {
                    JSONObject jsonResponse = new JSONObject(response);//response : 서버로 부터 받은 결과 (iteminfo.php 의 $response 에 담겨있는 값)
                    JSONArray jsonArray = jsonResponse.getJSONArray(TAG_INFO);

                    JSONObject firstItem = jsonArray.getJSONObject(0);

                    numApple = firstItem.getString(TAG_APPLE);
                    Log.d("count", "사과 :" + numApple);
                    numFish = firstItem.getString(TAG_FISH);
                    Log.d("count", "물고기 :" + numFish);
                    numMeat = firstItem.getString(TAG_MEAT);
                    Log.d("count", "고기 :" + numMeat);
                    numIce = firstItem.getString(TAG_ICE);
                    Log.d("count", "얼음 :" + numIce);

                    iApple = Integer.parseInt(numApple);
                    if(iApple < 1)
                        btnApple.setEnabled(false);
                    iFish = Integer.parseInt(numFish);
                    if(iFish < 1)
                        btnFish.setEnabled(false);
                    iMeat = Integer.parseInt(numMeat);
                    if(iMeat < 1)
                        btnMeat.setEnabled(false);
                    iIce = Integer.parseInt(numIce);
                    if(iIce < 1)
                        btnIce.setEnabled(false);
                    appleCount.setText("x "+numApple);
                    fishCount.setText("x "+numFish);
                    meatCount.setText("x "+numMeat);
                    iceCount.setText("x "+numIce);



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        /**서버로 volley를 이용하여 요청을 함**/
        ItemRequest itemRequest = new ItemRequest(userId, itemresponseListener);     //Request 클래스를 이용하여 서버 요청 정보와 결과 처리 방법을 표현
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(itemRequest);
    }

    //아이템 버튼 클릭(드래그앤드롭으로 수정해서 구현해야 함)
    public void itemOnClick(View v) {
        switch (v.getId()) {
            case R.id.btn_apple:
                modifyApple();
                Toast.makeText(this.getApplicationContext(),"50 경험치 획득.", Toast.LENGTH_SHORT).show();
                checkUserInfo();
                checkIteminfo();
                break;
            case R.id.btn_fish:
                modifyFish();
                Toast.makeText(this.getApplicationContext(),"100 경험치 획득.", Toast.LENGTH_SHORT).show();
                checkUserInfo();
                checkIteminfo();
                break;
            case R.id.btn_meat:
                modifyMeat();
                Toast.makeText(this.getApplicationContext(),"150 경험치 획득.", Toast.LENGTH_SHORT).show();
                checkUserInfo();
                checkIteminfo();
                break;
            case R.id.btn_ice:
                modifyIce();
                Toast.makeText(this.getApplicationContext(), "200 경험치 획득.", Toast.LENGTH_SHORT).show();
                checkUserInfo();
                checkIteminfo();
                break;
        }
        checkUserInfo();
        checkIteminfo();
    }

    public void modifyApple(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://polarbear1022.dothome.co.kr/useapple.php",
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

                params.put("uid", userId);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(stringRequest);

    }

    public void modifyFish(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://polarbear1022.dothome.co.kr/usefish.php",
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

                params.put("uid", userId);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(stringRequest);

    }

    public void modifyMeat(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://polarbear1022.dothome.co.kr/usemeat.php",
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

                params.put("uid", userId);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(stringRequest);

    }

    public void modifyIce(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://polarbear1022.dothome.co.kr/useice.php",
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

                params.put("uid", userId);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(stringRequest);

    }


    public void sendLevel(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://polarbear1022.dothome.co.kr/sendlevel.php",
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

                params.put("uid", userId);
                params.put("ulevel", userLevel);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(stringRequest);

    }
}





