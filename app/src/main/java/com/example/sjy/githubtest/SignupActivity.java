package com.example.sjy.githubtest;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {
    private EditText et_id, et_name;
    private Button btn_register, validateButton;
    private AlertDialog dialog;
    private boolean validate=false;
    private Context mContext;

    /** userid 와 username 을 입력받아 중복체크하고 서버에 전송, 디비에 저장하는 페이지 **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //아이디 값 찾아주기
        et_id=findViewById(R.id.et_id);
        et_name=findViewById(R.id.et_name);
        btn_register=findViewById(R.id.btn_register);
        validateButton=findViewById(R.id.validateButton);
        mContext = this;

        //아이디 중복체크 버튼
        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid=et_id.getText().toString();


                if(validate)
                {
                    return;
                }

                //아이디를 입력하지 않고 중복체크 버튼을 누른 경우
                if(uid.equals("")){
                    AlertDialog.Builder builder=new AlertDialog.Builder( SignupActivity.this );
                    dialog=builder.setMessage("아이디는 빈 칸일 수 없습니다")
                            .setPositiveButton("확인",null)
                            .create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> responseListener=new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        /**결과 처리**/
                        try {
                            JSONObject jsonResponse=new JSONObject(response);         //response : 서버로 부터 받은 결과 (uservalidate.php 의 $response 에 담겨있는 값)
                            boolean success=jsonResponse.getBoolean("success");//uservalidate.php의 success

                            //success 가 true 이면(사용 가능한 아이디이면)
                            if(success){
                                AlertDialog.Builder builder=new AlertDialog.Builder( SignupActivity.this );
                                dialog=builder.setMessage("사용할 수 있는 아이디입니다.")
                                        .setPositiveButton("확인",null)
                                        .create();
                                dialog.show();
                                et_id.setEnabled(false);
                                validate=true;
                                validateButton.setText("확인");
                                btn_register.setEnabled(true);
                                btn_register.setBackgroundColor(Color.parseColor("#AEDDEF"));
                                btn_register.setTextColor(Color.parseColor("#000000"));
                            }
                            //success 가 false 이면(이미 존재하는 아이디이면)
                            else{
                                AlertDialog.Builder builder=new AlertDialog.Builder( SignupActivity.this );
                                dialog=builder.setMessage("이미 존재하는 아이디 입니다.")
                                        .setNegativeButton("확인",null)
                                        .create();
                                dialog.show();
                            }
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                };

                /**서버로 volley를 이용하여 요청을 함**/
                ValidateRequest validateRequest=new ValidateRequest(uid, responseListener);     //Request 클래스를 이용하여 서버 요청 정보와 결과 처리 방법을 표현
                RequestQueue queue= Volley.newRequestQueue(SignupActivity.this);       //서버 요청자, 다른 request 클래스들의 정보대로 서버에 요청을 보내는 역할
                queue.add(validateRequest);                                                     //requestqueue 객체의 add() 함수에 request 객체를 매개변수로 지정하여 호출하면 서버 연동 발생

            }
        });

        //회원가입 버튼(디비에 저장하는 버튼)
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ed_id, et_name 에 입력되어있는 값을 get(가져온다)해온다
                final String uid=et_id.getText().toString();
                final String uname=et_name.getText().toString();
                int ulevel=1;
                int uexp=0;

                Response.Listener<String> responseListener=new Response.Listener<String>() {//volley
                    @Override
                    public void onResponse(String response) {
                        /**결과 처리**/

                        try {
                            JSONObject jasonObject=new JSONObject(response);         //response : 서버로 부터 받은 결과 (signup.php 의 $response 에 담겨있는 값)
                            boolean success=jasonObject.getBoolean("success");//signup.php의 success

                            if (success) {//회원등록 성공한 경우

                                /**Sharedpeference 클래스를 이용해서 userid, username 저장, 각각 userID, userNAME 키에 저장**/
                                PreferenceManager.setString(mContext, "userID", uid);  //sharedpreferences
                                PreferenceManager.setString(mContext, "userNAME", uname);

                                Toast.makeText(getApplicationContext(), "회원 등록 성공", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                startActivity(intent);
                             }

                            else{//회원등록 실패한 경우
                                Toast.makeText(getApplicationContext(),"회원 등록 실패",Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                /**서버로 volley를 이용하여 요청을 함**/
                SignupRequest signupRequest =new SignupRequest(uid, uname, ulevel, uexp, responseListener);
                RequestQueue queue= Volley.newRequestQueue(SignupActivity.this);
                queue.add(signupRequest);
            }
        });
    }

}