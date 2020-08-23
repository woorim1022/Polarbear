package com.example.sjy.githubtest;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class GraphActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private View drawerView;

    private String uid;
    private String uname;

    ArrayList <String> date = new ArrayList();
    ArrayList <String> weight = new ArrayList();
    ArrayList <String> point = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        final LineChart graph = (LineChart) findViewById(R.id.graph);

        //로그인한 사용자 uid, uname 가져오기
        uid = PreferenceManager.getString(this, "userID");
        uname = PreferenceManager.getString(this, "userNAME");



        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://polarbear1022.dothome.co.kr/graph.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //결과 처리
                        String TAG_JSON = "graph";
                        String TAG_DATE = "date";
                        String TAG_WEIGHT = "weight";
                        String TAG_POINT = "point";
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                            for(int i = 0;i<jsonArray.length();i++) {
                                JSONObject object = jsonArray.getJSONObject(i);

                                date.add(object.getString(TAG_DATE));
                                weight.add(object.getString(TAG_WEIGHT));
                                point.add(object.getString(TAG_POINT));
                                Log.v("bbb", " date : " + date+ " weight : " + weight+ " point : " +point);
                            }



                            ArrayList<LineDataSet> lineDataSets = new ArrayList<>();

                            ArrayList<Entry> weights = new ArrayList<>();
                            ArrayList<Entry> points = new ArrayList<>();
                            ArrayList<String> labels = new ArrayList<>();

                            for(int i=0;i < date.size();i++) {
                                labels.add(date.get(i));

                                if(weight.get(i).equals("9999"))
                                    weights.add(new Entry(0, i));
                                else
                                    weights.add(new Entry(parseInt(weight.get(i)), i));
                                points.add(new Entry(parseInt(point.get(i)), i));
                            }

                            LineDataSet dataset1 = new LineDataSet(weights, "내가 버린 음식물 쓰레기 무게");
                            dataset1.setColor(Color.BLUE);

                            LineDataSet dataset2 = new LineDataSet(points, "획득 포인트");
                            dataset2.setColor(Color.RED);

                            lineDataSets.add(dataset1);
                            lineDataSets.add(dataset2);


                            LineData data = new LineData(labels, lineDataSets);
                            graph.setData(data);
                            graph.animateY(5000);




                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }){
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("uid", uid);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(GraphActivity.this);
        queue.add(stringRequest);


        drawerLayout = (DrawerLayout)findViewById(R.id.graph_layout);
        drawerView = (View)findViewById(R.id.drawerView);

        ImageView openDrawer = (ImageView)findViewById(R.id.menu_button);
        openDrawer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });


        String url = "http://polarbear1022.dothome.co.kr/graph.php";

        //메뉴 클릭
        // AsyncTask를 통해 HttpURLConnection 수행.
        GraphActivity.NetworkTask networkTask = new GraphActivity.NetworkTask(url, null);
        networkTask.execute();
    }



    public void menuOnClick(View v) {
        switch(v.getId()){
            case R.id.drawer_weight:
                Intent weight = new Intent(GraphActivity.this, WeightActivity.class);
                startActivity(weight);
                break;
            case R.id.drawer_graph:
                Intent graph = new Intent(GraphActivity.this, GraphActivity.class);
                startActivity(graph);
                break;
            case R.id.drawer_donate:
                Intent donate = new Intent(GraphActivity.this, DonateActivity.class);
                startActivity(donate);
                break;
            case R.id.drawer_ranking:
                Intent ranking = new Intent(GraphActivity.this, RankingActivity.class);
                startActivity(ranking);
                break;
            case R.id.drawer_shop:
                Intent shop = new Intent(GraphActivity.this, ShopActivity.class);
                startActivity(shop);
                break;
            case R.id.drawer_mypage:
                Intent mypage = new Intent(GraphActivity.this, MypageActivity.class);
                startActivity(mypage);
                break;
            case R.id.drawer_main:
                Intent main = new Intent(GraphActivity.this, MainActivity.class);
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
//            textView.setText(s);
        }
    }

}