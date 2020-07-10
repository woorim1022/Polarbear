package com.example.sjy.githubtest;

        import android.content.ContentValues;
        import android.util.Log;

        import java.io.BufferedReader;
        import java.io.ByteArrayOutputStream;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.io.OutputStream;
        import java.net.HttpURLConnection;
        import java.net.MalformedURLException;
        import java.net.ProtocolException;
        import java.net.URL;
        import java.util.Map;

public class RequestHttpConnection {

    public String request(String _url, ContentValues _params){
        HttpURLConnection urlConn = null;
        StringBuffer sbParams = new StringBuffer();


        if(_params == null) {
            sbParams.append("null");
            Log.v("aaa", _url);
        }
        else{
            boolean isAnd = false;
            String key;
            String value;

            for(Map.Entry<String, Object> parameter : _params.valueSet()){
                key = parameter.getKey();
                value = parameter.getValue().toString();

                if(isAnd)
                    sbParams.append("&");

                sbParams.append(key).append("=").append(value);

                if(!isAnd)
                    if(_params.size() >= 2)
                        isAnd = true;
            }
        }
        try{
            URL url = new URL(_url);
            urlConn = (HttpURLConnection) url.openConnection();

            if(urlConn != null)
                Log.v("aaa",urlConn.toString());

            urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
            urlConn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;charset=UTF-8");

            Log.v("aaa","222");
            String strParams = sbParams.toString();
            Log.v("aaa","33");

            OutputStream os = urlConn.getOutputStream();
            os.write(strParams.getBytes("UTF-8"));
            Log.v("aaa","55");
            os.flush();
            Log.v("aaa","66");
            os.close();
            Log.v("aaa","77");

            if(urlConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.v("aaa","실패");
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));



            String line;
            String page = "";

            while((line = reader.readLine()) != null){
                page += line;
            }

            return page;
        } catch (MalformedURLException e) { // for URL.
            Log.v("aaa","88");
            e.printStackTrace();

        } catch (IOException e) { // for openConnection().
            Log.v("aaa","99");
            e.printStackTrace();
        } finally {
            if (urlConn != null)
                urlConn.disconnect();
        }

        return null;
    }
}
