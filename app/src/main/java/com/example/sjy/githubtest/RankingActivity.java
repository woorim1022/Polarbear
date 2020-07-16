package com.example.sjy.githubtest;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RankingActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private View drawerView;
    private TextView whenDate;
    private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy/M/d");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        drawerLayout = (DrawerLayout)findViewById(R.id.ranking_layout);
        drawerView = (View)findViewById(R.id.drawerView);
        whenDate = (TextView)findViewById(R.id.date);

        ImageView openDrawer = (ImageView)findViewById(R.id.menu_button);
        openDrawer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });

        String date = new SimpleDateFormat("yyyy년 MM월 W주차 랭킹").format(new Date());
        whenDate.setText(date);

    }

    public void menuOnClick(View v) {
        switch(v.getId()){
            case R.id.drawer_weight:
                Intent weight = new Intent(RankingActivity.this, WeightActivity.class);
                startActivity(weight);
                break;
            case R.id.drawer_graph:
                Intent graph = new Intent(RankingActivity.this, GraphActivity.class);
                startActivity(graph);
                break;
            case R.id.drawer_donate:
                Intent donate = new Intent(RankingActivity.this, DonateActivity.class);
                startActivity(donate);
                break;
            case R.id.drawer_ranking:
                Intent ranking = new Intent(RankingActivity.this, RankingActivity.class);
                startActivity(ranking);
                break;
            case R.id.drawer_shop:
                Intent shop = new Intent(RankingActivity.this, ShopActivity.class);
                startActivity(shop);
                break;
            case R.id.drawer_mypage:
                Intent mypage = new Intent(RankingActivity.this, MypageActivity.class);
                startActivity(mypage);
                break;
            case R.id.drawer_main:
                Intent main = new Intent(RankingActivity.this, MainActivity.class);
                startActivity(main);
                break;

        }
    }
}