package com.example.sjy.githubtest;

import android.content.ContentValues;
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

    private TextView textView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //메뉴 버튼 클릭
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerView = (View)findViewById(R.id.drawerView);

        ImageView openDrawer = (ImageView)findViewById(R.id.menu_button);

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


        try {
            Intent getintent = getIntent();
            String userID = getintent.getStringExtra("userID");
            Log.v("aaa", userID);
        }catch (NullPointerException e) {
            Intent signup = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(signup);
        }




        // URL 설정.
        String url = "http://polarbear1022.dothome.co.kr/try.php";

    //메뉴 클릭
        // AsyncTask를 통해 HttpURLConnection 수행.
        NetworkTask networkTask = new NetworkTask(url, null);
        networkTask.execute();
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
            //textView.setText(s);
        }
    }



}



