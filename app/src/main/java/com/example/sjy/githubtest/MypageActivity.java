package com.example.sjy.githubtest;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;


public class MypageActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private View drawerView;
    private EditText edt_nickname;
    private Button bt_modify;
    private Context mContext;
    private String userId, newName;
    private String numApple, numFish, numMeat, numIce;
    private TextView tApple, tFish, tMeat, tIce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        drawerLayout = (DrawerLayout)findViewById(R.id.mypage_layout);
        drawerView = (View)findViewById(R.id.drawerView);
        edt_nickname = (EditText)findViewById(R.id.et_nickname);
        bt_modify = (Button)findViewById(R.id.bt_modify);
        tApple = (TextView)findViewById(R.id.applenum);
        tFish = (TextView)findViewById(R.id.fishnum);
        tMeat = (TextView)findViewById(R.id.meatnum);
        tIce = (TextView)findViewById(R.id.icenum);
        mContext = this;

        ImageView openDrawer = (ImageView)findViewById(R.id.menu_button);

        openDrawer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });

        String url = "http://polarbear1022.dothome.co.kr/mypage.php";

        //마이페이지 닉네임 설정
        userId = PreferenceManager.getString(mContext, "userID");
        String name = PreferenceManager.getString(mContext,"userNAME");
        edt_nickname.setText(name);
        getItemlist();

        bt_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newName = edt_nickname.getText().toString();
                Log.v("aaa", "수정한 이름 : " +newName);
                modifyName();
                PreferenceManager.setString(mContext, "userID", userId);
                PreferenceManager.setString(mContext, "userNAME", newName);
                Log.v("aaa", "아이디 : " +userId);
            }
        });
    }

    //메뉴 클릭
    public void menuOnClick(View v) {
        switch(v.getId()){
            case R.id.drawer_weight:
                Intent weight = new Intent(MypageActivity.this, WeightActivity.class);
                startActivity(weight);
                break;
            case R.id.drawer_graph:
                Intent graph = new Intent(MypageActivity.this, GraphActivity.class);
                startActivity(graph);
                break;
            case R.id.drawer_donate:
                Intent donate = new Intent(MypageActivity.this, DonateActivity.class);
                startActivity(donate);
                break;
            case R.id.drawer_ranking:
                Intent ranking = new Intent(MypageActivity.this, RankingActivity.class);
                startActivity(ranking);
                break;
            case R.id.drawer_shop:
                Intent shop = new Intent(MypageActivity.this, ShopActivity.class);
                startActivity(shop);
                break;
            case R.id.drawer_mypage:
                Intent mypage = new Intent(MypageActivity.this, MypageActivity.class);
                startActivity(mypage);
                break;
            case R.id.drawer_main:
                Intent main = new Intent(MypageActivity.this, MainActivity.class);
                startActivity(main);
                break;

        }
    }

    public void modifyName(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://polarbear1022.dothome.co.kr/modifyname.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(mContext,"닉네임 변경 완료", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("uid", userId);
                params.put("uname", newName);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(MypageActivity.this);
        queue.add(stringRequest);

    }

    public void getItemlist(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://polarbear1022.dothome.co.kr/mypage.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String TAG_JSON = "buyinginfo";
                        String TAG_APPLE = "apple";
                        String TAG_FISH = "fish";
                        String TAG_MEAT = "meat";
                        String TAG_ICE = "ice";

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);


                            JSONObject firstItem = jsonArray.getJSONObject(0);

                            numApple = firstItem.getString(TAG_APPLE);
                            Log.d("count", "사과 :" + numApple);
                            numFish = firstItem.getString(TAG_FISH);
                            Log.d("count", "물고기 :" + numFish);
                            numMeat = firstItem.getString(TAG_MEAT);
                            Log.d("count", "고기 :" + numMeat);
                            numIce = firstItem.getString(TAG_ICE);
                            Log.d("count", "얼음 :" + numIce);

                            tApple.setText(numApple + " 개");
                            tFish.setText(numFish + " 개");
                            tMeat.setText(numMeat + " 개");
                            tIce.setText(numIce + " 개");

                        } catch (JSONException e) {

                            Log.d("rrr", "showResult : ", e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("uid", userId);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(MypageActivity.this);
        queue.add(stringRequest);

    }
}