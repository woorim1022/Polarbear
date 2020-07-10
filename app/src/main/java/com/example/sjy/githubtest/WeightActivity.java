package com.example.sjy.githubtest;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WeightActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private View drawerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight);


        drawerLayout = (DrawerLayout)findViewById(R.id.weight_layout);
        drawerView = (View)findViewById(R.id.drawerView);

        ImageView openDrawer = (ImageView)findViewById(R.id.menu_button);
        openDrawer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                 drawerLayout.openDrawer(drawerView);
            }
        });


        TextView weightView = (TextView)findViewById(R.id.weightView);
        Button button = (Button) findViewById(R.id.button);
        TextView dateView = (TextView)findViewById(R.id.dateView);

        Intent intent = getIntent();  //메인 화면에서 넘어온 intent 받음
        String datafrommain = intent.getStringExtra("메인 액티비티에서 넘길 정보"); //pustExtra로 지정했던 데이터의 키값을 지정하면 해당하는 데이터 값이 나오게 됨

        Toast.makeText(this, datafrommain, Toast.LENGTH_SHORT).show();

        View.OnClickListener listener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                //센서에서 무게 가져오기
                //서버에 저장
                //화면에 무게, 날짜 출력
            }
        };
        button.setOnClickListener(listener);

        Intent resultintent = new Intent();
        resultintent.putExtra("결과", "무게값, 포인트값 등");

        setResult(0, resultintent); //자신을 실행한 액티비티에게 돌려줄 결과

    }

    public void menuOnClick(View v) {
        switch(v.getId()){
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