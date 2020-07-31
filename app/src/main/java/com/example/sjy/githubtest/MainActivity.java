package com.example.sjy.githubtest;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private View drawerView;
    private static String IP_ADDRESS = "IP주소";
    private static String TAG = "phptest";
    private Context mContext;
    private TextView textView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //메뉴 버튼 클릭
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerView = (View)findViewById(R.id.drawerView);

        ImageView openDrawer = (ImageView)findViewById(R.id.menu_button);

        mContext = this;

        openDrawer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });

        //조건에 맞는 배지만 노출되도록 설정 (VISIBLE)
        ImageView badge1 = (ImageView)findViewById(R.id.badge01);
        ImageView badge2 = (ImageView)findViewById(R.id.badge02);
        ImageView badge3 = (ImageView)findViewById(R.id.badge03);


        badge1.setVisibility(View.VISIBLE);
        badge2.setVisibility(View.VISIBLE);
        badge3.setVisibility(View.VISIBLE);


        /**Sharedpreference 클래스 이용해서 내부 저장소에 저장되어있는 userid, username 값 가져오기**/
        String uid = PreferenceManager.getString(mContext, "userID");
        String uname = PreferenceManager.getString(mContext, "userNAME");
        Log.v("aaa", uid);
        Log.v("aaa", uname);
        /**만약 uid 값이 없으면, 회원가입을 위해 SignActivity.class 로 이동**/
        if(uid == ""){
            Intent signup = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(signup);
        }

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



