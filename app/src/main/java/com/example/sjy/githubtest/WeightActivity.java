package com.example.sjy.githubtest;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.TestLooperManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
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
    private TextView weightView;
    private Button button;
    private TextView dateView;
    private TextView goalwalk;
    private TextView currentwalk;

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


        weightView = (TextView)findViewById(R.id.weightView);
        button = (Button) findViewById(R.id.button);
        dateView = (TextView)findViewById(R.id.dateView);
        goalwalk = (TextView)findViewById(R.id.goalwalk);
        currentwalk = (TextView)findViewById(R.id.currentwalk);

        Intent intent = getIntent();  //메인 화면에서 넘어온 intent 받음
        String datafrommain = intent.getStringExtra("메인 액티비티에서 넘길 정보"); //pustExtra로 지정했던 데이터의 키값을 지정하면 해당하는 데이터 값이 나오게 됨

        Toast.makeText(this, datafrommain, Toast.LENGTH_SHORT).show();

        if(true) {   //포인트가 20이하면
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("이번주에는 아쉽게도 포인트를 많이 획득하지 못하셨군요!ㅜㅜ").setMessage("환경을 위해 이번주에는 자동차 대신 짧은 거리를 걸어보는 것이 어떨까요? 2000걸음을 걸으시면 추가 포인트가 지급됩니다.");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int id)
                {
                    Toast.makeText(getApplicationContext(), "OK Click", Toast.LENGTH_SHORT).show();

                    goalwalk.setVisibility(View.VISIBLE);
                    currentwalk.setVisibility(View.VISIBLE);
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int id)
                {
                    Toast.makeText(getApplicationContext(), "Cancel Click", Toast.LENGTH_SHORT).show();
                }
            });



            AlertDialog alertDialog = builder.create();

            alertDialog.show();

        }

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