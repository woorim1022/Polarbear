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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RankingActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private View drawerView;
    private Context mContext;
    private TextView whenDate;
    private TextView myrank;
    private TextView rank1, rank2, rank3, rank4, rank5, rank6, rank7, rank8, rank9, rank10;
    private TextView point1, point2, point3, point4, point5, point6, point7, point8, point9, point10;
    private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy/M/d");
    private String userId;
    private String success, rank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        drawerLayout = (DrawerLayout)findViewById(R.id.ranking_layout);
        drawerView = (View)findViewById(R.id.drawerView);
        mContext = this;
        whenDate = (TextView)findViewById(R.id.date);
        myrank = (TextView)findViewById(R.id.myrank);
        rank1 = (TextView)findViewById(R.id.rank1);
        rank2 = (TextView)findViewById(R.id.rank2);
        rank3 = (TextView)findViewById(R.id.rank3);
        rank4 = (TextView)findViewById(R.id.rank4);
        rank5 = (TextView)findViewById(R.id.rank5);
        rank6 = (TextView)findViewById(R.id.rank6);
        rank7 = (TextView)findViewById(R.id.rank7);
        rank8 = (TextView)findViewById(R.id.rank8);
        rank9 = (TextView)findViewById(R.id.rank9);
        rank10 = (TextView)findViewById(R.id.rank10);
        point1 = (TextView)findViewById(R.id.point1);
        point2 = (TextView)findViewById(R.id.point2);
        point3 = (TextView)findViewById(R.id.point3);
        point4 = (TextView)findViewById(R.id.point4);
        point5 = (TextView)findViewById(R.id.point5);
        point6 = (TextView)findViewById(R.id.point6);
        point7 = (TextView)findViewById(R.id.point7);
        point8 = (TextView)findViewById(R.id.point8);
        point9 = (TextView)findViewById(R.id.point9);
        point10 = (TextView)findViewById(R.id.point10);


        ImageView openDrawer = (ImageView)findViewById(R.id.menu_button);
        openDrawer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });

        String date = new SimpleDateFormat("yyyy년 MM월 W주차 랭킹").format(new Date());
        whenDate.setText(date);

        String url = "http://polarbear1022.dothome.co.kr/ranking.php";

        // AsyncTask를 통해 HttpURLConnection 수행.
        RankingActivity.NetworkTask networkTask = new RankingActivity.NetworkTask(url, null);
        networkTask.execute();

        getRank();
    }

    //유저 랭킹 정보 가져오기
    public void getRank() {
        userId = PreferenceManager.getString(mContext, "userID");

        Response.Listener<String> responseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                /**결과 처리**/
                String TAG_INFO = "ranking";
                String TAG_TF = "success";
                String TAG_RANK = "myrank";

                try {
                    JSONObject jsonResponse=new JSONObject(response);         //response : 서버로 부터 받은 결과 (getnum.php 의 $response 에 담겨있는 값)
                    JSONArray jsonArray = jsonResponse.getJSONArray(TAG_INFO);
                    JSONObject firstItem = jsonArray.getJSONObject(0);

                    success = firstItem.getString(TAG_TF);

                    //success 가 true 이면(순위 안에 있으면)
                    if(success.equals("true")){
                        rank = firstItem.getString(TAG_RANK);
                        myrank.setText(rank+ "위");
                    }
                    //success 가 false 이면(순위 밖에 있으면)
                    else{
                        myrank.setText("순위 밖");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        /**서버로 volley를 이용하여 요청을 함**/
        RankingRequest rankingRequest = new RankingRequest(userId, responseListener);     //Request 클래스를 이용하여 서버 요청 정보와 결과 처리 방법을 표현
        RequestQueue queue = Volley.newRequestQueue(RankingActivity.this);       //서버 요청자, 다른 request 클래스들의 정보대로 서버에 요청을 보내는 역할
        queue.add(rankingRequest);
    }

    //메뉴 클릭
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
            String TAG_JSON = "ranking";
            String TAG_NUM = "rank_num";
            String TAG_NAME = "rank_name";
            String TAG_ID = "rank_id";
            String TAG_POINT = "rank_point";

            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);


                JSONObject firstItem = jsonArray.getJSONObject(0);

                String firstName = firstItem.getString(TAG_NAME);
                String firstPoint = firstItem.getString(TAG_POINT);

                rank1.setText(firstName);
                point1.setText(firstPoint);

                JSONObject secondItem = jsonArray.getJSONObject(1);

                String secondName = secondItem.getString(TAG_NAME);
                String secondPoint = secondItem.getString(TAG_POINT);

                rank2.setText(secondName);
                point2.setText(secondPoint);

                JSONObject thirdItem = jsonArray.getJSONObject(2);

                String thirdName = thirdItem.getString(TAG_NAME);
                String thirdPoint = thirdItem.getString(TAG_POINT);

                rank3.setText(thirdName);
                point3.setText(thirdPoint);

                JSONObject fourthItem = jsonArray.getJSONObject(3);

                String fourthName = fourthItem.getString(TAG_NAME);
                String fourthPoint = fourthItem.getString(TAG_POINT);

                rank4.setText(fourthName);
                point4.setText(fourthPoint);

                JSONObject fifthItem = jsonArray.getJSONObject(4);

                String fifthName = fifthItem.getString(TAG_NAME);
                String fifthPoint = fifthItem.getString(TAG_POINT);

                rank5.setText(fifthName);
                point5.setText(fifthPoint);

                JSONObject sixthItem = jsonArray.getJSONObject(5);

                String sixthName = sixthItem.getString(TAG_NAME);
                String sixthPoint = sixthItem.getString(TAG_POINT);

                rank6.setText(sixthName);
                point6.setText(sixthPoint);

                JSONObject seventhItem = jsonArray.getJSONObject(6);

                String seventhName = seventhItem.getString(TAG_NAME);
                String seventhPoint = seventhItem.getString(TAG_POINT);

                rank7.setText(seventhName);
                point7.setText(seventhPoint);

                JSONObject eighthItem = jsonArray.getJSONObject(7);

                String eighthName = eighthItem.getString(TAG_NAME);
                String eighthPoint = eighthItem.getString(TAG_POINT);

                rank8.setText(eighthName);
                point8.setText(eighthPoint);

                JSONObject ninthItem = jsonArray.getJSONObject(8);

                String ninthName = ninthItem.getString(TAG_NAME);
                String ninthPoint = ninthItem.getString(TAG_POINT);

                rank9.setText(ninthName);
                point9.setText(ninthPoint);

                JSONObject tenthItem = jsonArray.getJSONObject(9);

                String tenthName = tenthItem.getString(TAG_NAME);
                String tenthPoint = tenthItem.getString(TAG_POINT);

                rank10.setText(tenthName);
                point10.setText(tenthPoint);


            } catch (JSONException e) {

                Log.d("rrr", "showResult : ", e);
            }
        }
    }

}