package com.example.sjy.githubtest;

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
import android.widget.TextView;

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
    private Context mContext;
    private TextView username;
    private ImageView badge1, badge2, badge3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //메뉴 버튼 클릭
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerView = (View)findViewById(R.id.drawerView);

        ImageView openDrawer = (ImageView)findViewById(R.id.menu_button);

        mContext = this;

        username = (TextView)findViewById(R.id.username);


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

}



