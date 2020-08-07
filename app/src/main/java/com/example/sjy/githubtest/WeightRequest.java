package com.example.sjy.githubtest;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**서버에 전송할 데이터는 StringRequest를 상속받은 클래스를 정의하고,
 이 클래스에서 getParams( ) 함수를 재정의하며 getParams ( ) 함수에서 서버에 전송할 데이터를 Map 객체에 담아 반환하면 됩니다.
 이렇게 하면 getParams( ) 함수에서 반환한 Map 객체의 데이터를 웹의 질의 문자열 형식으로 만들어 RequestQueue에서 서버 요청 시 서버에 전송합니다.
 **/
public class WeightRequest extends StringRequest {   //stringrequest : 문자열을 결과로 받는 요청 정보, 서버로부터 문자열 데이터를 얻을 목적으로 사용하는 클래스

    //서버 url 설정(php파일 연동)
    final static  private String URL="http://polarbear1022.dothome.co.kr/weight.php";
    private Map<String,String> map;

    public WeightRequest(String uid, int weight, int point, Response.Listener<String> listener){
        super(Method.POST, URL, listener,null);//위 url에 post방식으로 값을 전송

        map=new HashMap<>();
        map.put("uid",uid);
        map.put("weight",weight+"");
        map.put("point",point+"");
    }       //생성자

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {             //서버에 전송할 데이터는 stringrequest를 상속받은 클래스를 정의하고, 이 클래스에서 getParams() 함수를 재정의하며 getParams() 함수에서 서버에 전송할 데이터를 Map 객체에 담아 반환
        return map;
    }
}