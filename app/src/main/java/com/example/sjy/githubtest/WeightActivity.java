package com.example.sjy.githubtest;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

public class WeightActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private View drawerView;
    private TextView weight;
    private Button button;
    private TextView measure_day;
    private TextView step_goal;
    private TextView step_current;

    private int count = 6;
    private CountDownTimer countDownTimer;

    AlertDialog.Builder builder, builder2;
    AlertDialog alertDialog;


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


        weight = (TextView)findViewById(R.id.weight);
        button = (Button) findViewById(R.id.button);
        measure_day = (TextView)findViewById(R.id.measure_day);
        step_goal = (TextView)findViewById(R.id.step_goal);
        step_current = (TextView)findViewById(R.id.step_current);

        Intent intent = getIntent();  //메인 화면에서 넘어온 intent 받음
        String datafrommain = intent.getStringExtra("메인 액티비티에서 넘길 정보"); //pustExtra로 지정했던 데이터의 키값을 지정하면 해당하는 데이터 값이 나오게 됨


        if(true) {   //포인트가 20이하면
            builder = new AlertDialog.Builder(this);

            builder.setTitle("이번주에는 아쉽게도 포인트를 많이 획득하지 못하셨군요ㅠㅠ").setMessage("이번주에는 승용차를 이용하는 대신 짧은 거리를 걸어보는 것이 어떨까요? 승용차를 일주일에 하루만 덜 타면 연간 445kg의 이산화탄소를 줄일 수 있다고 해요! (5000걸음을 걸으시면 추가 포인트가 지급됩니다.)");

            builder.setPositiveButton("걸음 수 측정하기", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int id)
                {
                    Toast.makeText(getApplicationContext(), "걸음 수 측정 시작", Toast.LENGTH_SHORT).show();

                    step_goal.setVisibility(View.VISIBLE);
                    step_current.setVisibility(View.VISIBLE);
                }
            });

            builder.setNegativeButton("다음에", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int id)
                {
                    Toast.makeText(getApplicationContext(), "걸음 수 측정 취소", Toast.LENGTH_SHORT).show();
                }
            });
            alertDialog = builder.create();
            alertDialog.show();

            String url = "http://polarbear1022.dothome.co.kr/weight.php";

            //메뉴 클릭
            // AsyncTask를 통해 HttpURLConnection 수행.
            WeightActivity.NetworkTask networkTask = new WeightActivity.NetworkTask(url, null);
            networkTask.execute();

        }

        View.OnClickListener weightlistener = new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                builder2 = new AlertDialog.Builder(WeightActivity.this);
                builder2.setTitle("5초 후에 음식물 쓰레기 봉투를 올려주세요");
                builder2.setNegativeButton("취소하기", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        //무게재기 취소
                        Toast.makeText(getApplicationContext(), "무게재기 취소", Toast.LENGTH_SHORT).show();
                    }
                });
                alertDialog = builder2.create();
                alertDialog.show();
                countDownTimer = new CountDownTimer(5*1000, 1000) {
                    @Override
                    public void onTick(long l) {
                        count --;
                    }
                    @Override
                    public void onFinish() {
                        alertDialog.dismiss();
                    }
                };
                countDownTimer.start();
                //센서에서 무게 가져오기
                //서버에 저장
                //화면에 무게, 날짜 출력
                StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://polarbear1022.dothome.co.kr/weighttest.php",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //결과 처리
                                try {
                                    Log.v("rrr", response);
                                    JSONObject jsonResponse = new JSONObject(response);
                                    Integer weightvalue = jsonResponse.getInt("weightvalue");
                                    Log.v("rrr", response+"");
                                    weight.setText("무게 : " + weightvalue + "g");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                                public void onErrorResponse(VolleyError error) {
                            }
                });
                RequestQueue queue = Volley.newRequestQueue(WeightActivity.this);
                queue.add(stringRequest);
            }
        };
        button.setOnClickListener(weightlistener);



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

    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values) {

            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            RequestHttpConnection requestHttpConnection = new RequestHttpConnection();
            result = requestHttpConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
            weight.setText(s + "kg");
        }
    }



}