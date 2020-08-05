package com.example.sjy.githubtest;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.sjy.githubtest.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.sjy.githubtest.R.id.drawerView;

public class DonateActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private View drawerView;
    private String mJsonString;
    private String userName;
    private String userLevel;
    private String userExp;
    private String userPoint;
    private String userTotal;
    private String userId;
    private TextView donate_name1;
    private TextView donate_name2;
    private TextView donate_name3;
    private TextView donate_price1;
    private TextView donate_price2;
    private TextView donate_price3;
    private TextView currentpoint;
    private Button donate_button1;
    private Button donate_button2;
    private Button donate_button3;
    private Context mContext;

    //임시
    private AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        drawerLayout = (DrawerLayout) findViewById(R.id.donate_layout);
        drawerView = (View) findViewById(R.id.drawerView);
        mContext = this;

        ImageView openDrawer = (ImageView) findViewById(R.id.menu_button);
        openDrawer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });

        donate_name1 = (TextView) findViewById(R.id.donate_name1);
        donate_name2 = (TextView) findViewById(R.id.donate_name2);
        donate_name3 = (TextView) findViewById(R.id.donate_name3);
        donate_price1 = (TextView) findViewById(R.id.donate_price1);
        donate_price2 = (TextView) findViewById(R.id.donate_price2);
        donate_price3 = (TextView) findViewById(R.id.donate_price3);
        currentpoint = (TextView) findViewById(R.id.currentpoint);
        donate_button1 = (Button) findViewById(R.id.donate_button1);
        donate_button2 = (Button) findViewById(R.id.donate_button2);
        donate_button3 = (Button) findViewById(R.id.donate_button3);

        String url = "http://polarbear1022.dothome.co.kr/donate.php";

        // AsyncTask를 통해 HttpURLConnection 수행.
        DonateActivity.NetworkTask networkTask = new DonateActivity.NetworkTask(url, null);
        networkTask.execute();

        //userid에 따라 userinfo 받아옴
        String userId = PreferenceManager.getString(mContext,"userID");

        Response.Listener<String> responseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                /**결과 처리**/
                String TAG_INFO = "userinfo";
                String TAG_NAME = "uname";
                String TAG_LEVEL = "ulevel";
                String TAG_EXP = "uexp";
                String TAG_POINT = "user_point";
                String TAG_TOTAL = "user_total";
                try {
                    JSONObject jsonResponse=new JSONObject(response);//response : 서버로 부터 받은 결과 (uservalidate.php 의 $response 에 담겨있는 값)
                    JSONArray jsonArray = jsonResponse.getJSONArray(TAG_INFO);

                    JSONObject firstItem = jsonArray.getJSONObject(0);

                    userName = firstItem.getString(TAG_NAME);
                    userLevel = firstItem.getString(TAG_LEVEL);
                    userExp = firstItem.getString(TAG_EXP);
                    userPoint = firstItem.getString(TAG_POINT);
                    userTotal = firstItem.getString(TAG_TOTAL);

                    currentpoint.setText(userPoint);

                    int point = Integer.parseInt(userPoint);
                    if(point < 300) {
                        donate_button1.setEnabled(false);
                        donate_button1.setBackgroundColor(Color.parseColor("#4DAEDDEF"));
                        donate_button1.setTextColor(Color.parseColor("#4D000000"));
                        donate_button2.setEnabled(false);
                        donate_button2.setBackgroundColor(Color.parseColor("#4DAEDDEF"));
                        donate_button2.setTextColor(Color.parseColor("#4D000000"));
                        donate_button3.setEnabled(false);
                        donate_button3.setBackgroundColor(Color.parseColor("#4DAEDDEF"));
                        donate_button3.setTextColor(Color.parseColor("#4D000000"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        /**서버로 volley를 이용하여 요청을 함**/
        UserRequest userRequest=new UserRequest(userId, responseListener);     //Request 클래스를 이용하여 서버 요청 정보와 결과 처리 방법을 표현
        RequestQueue queue= Volley.newRequestQueue(DonateActivity.this);       //서버 요청자, 다른 request 클래스들의 정보대로 서버에 요청을 보내는 역할
        queue.add(userRequest);
    }

    //기부하기 버튼 클릭
    public void DonateClick(View v) {
        switch(v.getId()){
            case R.id.donate_button1:
                AlertDialog.Builder builder1=new AlertDialog.Builder( DonateActivity.this );
                dialog=builder1.setTitle("THANK YOU!")
                        .setMessage("세계 자연 기금에 기부하셨습니다.")
                        .setPositiveButton("확인",null)
                        .create();
                dialog.show();


                break;
            case R.id.donate_button2:
                AlertDialog.Builder builder2=new AlertDialog.Builder( DonateActivity.this );
                dialog=builder2.setTitle("THANK YOU!")
                        .setMessage("그린피스에 기부하셨습니다.")
                        .setPositiveButton("확인",null)
                        .create();
                dialog.show();
                break;
            case R.id.donate_button3:
                AlertDialog.Builder builder3=new AlertDialog.Builder( DonateActivity.this );
                dialog=builder3.setTitle("THANK YOU!")
                        .setMessage("지구의 벗에 기부하셨습니다.")
                        .setPositiveButton("확인",null)
                        .create();
                dialog.show();
                break;
        }
    }
    //메뉴 클릭
    public void menuOnClick(View v) {
        switch(v.getId()){
            case R.id.drawer_weight:
                Intent weight = new Intent(DonateActivity.this, WeightActivity.class);
                startActivity(weight);
                break;
            case R.id.drawer_graph:
                Intent graph = new Intent(DonateActivity.this, GraphActivity.class);
                startActivity(graph);
                break;
            case R.id.drawer_donate:
                Intent donate = new Intent(DonateActivity.this, DonateActivity.class);
                startActivity(donate);
                break;
            case R.id.drawer_ranking:
                Intent ranking = new Intent(DonateActivity.this, RankingActivity.class);
                startActivity(ranking);
                break;
            case R.id.drawer_shop:
                Intent shop = new Intent(DonateActivity.this, ShopActivity.class);
                startActivity(shop);
                break;
            case R.id.drawer_mypage:
                Intent mypage = new Intent(DonateActivity.this, MypageActivity.class);
                startActivity(mypage);
                break;
            case R.id.drawer_main:
                Intent main = new Intent(DonateActivity.this, MainActivity.class);
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
            mJsonString = result;
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
           showResult();
        }

        private void showResult(){

            String TAG_JSON = "donate list";
            String TAG_NAME = "donate_name";
            String TAG_ID = "d_id";
            String TAG_PRICE = "donate_price";

            try {
                JSONObject jsonObject = new JSONObject(mJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);


                JSONObject firstItem = jsonArray.getJSONObject(0);

                String firstName = firstItem.getString(TAG_NAME);
                String firstId = firstItem.getString(TAG_ID);
                String firstPrice = firstItem.getString(TAG_PRICE);

                donate_name1.setText(firstName);
                donate_price1.setText(firstPrice);

                JSONObject secondItem = jsonArray.getJSONObject(1);

                String secondName = secondItem.getString(TAG_NAME);
                String secondId = secondItem.getString(TAG_ID);
                String secondPrice = secondItem.getString(TAG_PRICE);

                donate_name2.setText(secondName);
                donate_price2.setText(secondPrice);

                JSONObject thirdItem = jsonArray.getJSONObject(2);

                String thirdName = thirdItem.getString(TAG_NAME);
                String thirdId = thirdItem.getString(TAG_ID);
                String thirdPrice = thirdItem.getString(TAG_PRICE);

                donate_name3.setText(thirdName);
                donate_price3.setText(thirdPrice);


            } catch (JSONException e) {

                Log.d("rrr", "showResult : ", e);
            }
        }
    }

}