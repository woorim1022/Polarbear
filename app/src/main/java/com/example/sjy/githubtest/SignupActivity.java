package com.example.sjy.githubtest;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //아이디 값 찾아주기
        et_id=findViewById(R.id.et_id);
        et_name=findViewById(R.id.et_name);
        btn_register=findViewById(R.id.btn_register);
        validateButton=findViewById(R.id.validateButton);


        validateButton.setOnClickListener(new View.OnClickListener() {//id중복체크
            @Override
            public void onClick(View view) {
                String uid=et_id.getText().toString();

                if(validate)
                {
                    return;
                }

                if(uid.equals("")){          //아이디를 입력하지 않았으면
                    AlertDialog.Builder builder=new AlertDialog.Builder( SignupActivity.this );
                    dialog=builder.setMessage("아이디는 빈 칸일 수 없습니다")
                            .setPositiveButton("확인",null)
                            .create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> responseListener=new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {    //서버로부터 수신된 문자열 데이터를 전달할 목적으로 세 번 째 매개변수로 지정된 콜백의 onResponse() 함수가 자동으로 호출되며, 이 함수의 매개변수로 서버 수신 문자열이 전달됨
                        //결과 처리
                        try {
                            JSONObject jsonResponse=new JSONObject(response);
                            boolean success=jsonResponse.getBoolean("success");
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
                            }
                            else{
                                AlertDialog.Builder builder=new AlertDialog.Builder( SignupActivity.this );
                                dialog=builder.setMessage("사용할 수 없는 아이디입니다.")
                                        .setNegativeButton("확인",null)
                                        .create();
                                dialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                ValidateRequest validateRequest=new ValidateRequest(uid, responseListener);
                RequestQueue queue= Volley.newRequestQueue(SignupActivity.this);    //서버 요청자, 다른 request 클래스들의 정보대로 서버에 요청을 보내는 역할
                queue.add(validateRequest);                                                    //requestqueue 객체의 add() 함수에 request 객체를 매개변수로 지정하여 호출하면 서버 연동 발생

            }
        });


        btn_register=findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //editText에 입력되어있는 값을 get(가져온다)해온다
                final String uid=et_id.getText().toString();
                final String uname=et_name.getText().toString();
                int ulevel=1;
                int uexp=0;

                Response.Listener<String> responseListener=new Response.Listener<String>() {//volley
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jasonObject=new JSONObject(response);//Register2 php에 response
                            boolean success=jasonObject.getBoolean("success");//Register2 php에 sucess

                            if (success) {//회원등록 성공한 경우
                                Toast.makeText(getApplicationContext(), "회원 등록 성공", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                intent.putExtra("userID", uid);
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
                //서버로 volley를 이용해서 요청을 함
                SignupRequest signupRequest =new SignupRequest(uid, uname, ulevel, uexp, responseListener);
                RequestQueue queue= Volley.newRequestQueue(SignupActivity.this);
                queue.add(signupRequest);
            }
        });
    }

}