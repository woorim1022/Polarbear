package com.example.sjy.githubtest;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class SignupRequest extends StringRequest {   //stringrequest : 문자열을 결과로 받는 요청 정보, 서버로부터 문자열 데이터를 얻을 목적으로 사용하는 클래스

    //서버 url 설정(php파일 연동)
    final static  private String URL="http://polarbear1022.dothome.co.kr/signup.php";
    private Map<String,String> map;

    public SignupRequest(String uid, String uname, int ulevel, int uexp, Response.Listener<String> listener){
        super(Method.POST, URL, listener,null);//위 url에 post방식으로 값을 전송

        map=new HashMap<>();
        map.put("uid",uid);
        map.put("uname",uname);
        map.put("ulevel",ulevel+"");
        map.put("uexp",uexp+"");
    }       //생성자

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {             //서버에 전송할 데이터는 stringrequest를 상속받은 클래스를 정의하고, 이 클래스에서 getParams() 함수를 재정의하며 getParams() 함수에서 서버에 전송할 데이터를 Map 객체에 담아 반환
        return map;
    }
}