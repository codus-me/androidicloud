package me.codus.androidicloud;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by codus on 2017/9/7.
 */

public class AndroidiCloudSession {

    public static class Response {
        InputStream inputStream;
        public Response(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        public JSONObject json() throws IOException, JSONException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            inputStream.close();
            String response = sb.toString();
            JSONObject resp = new JSONObject(response);
            return resp;
        }
    }

    public Map<String, String> headers = new HashMap<>();

    public Response post(String urlString, Map<String,String> getParams, String payload) throws IOException {
        return request(urlString, getParams, payload, 20*1000, "POST");
    }

    public Response get(String urlString, Map<String,String> getParams) throws IOException {
        return request(urlString, getParams, null, 20*1000, "GET");
    }

    public Response request(final String urlString, final Map<String,String> getParams, final String payload, final int connnectionTimeout, final String method) throws IOException {
        String paramsUrl = "";
        boolean isFirstParam = true;
        for (Map.Entry<String, String> param: getParams.entrySet()) {
            paramsUrl += String.format("%s%s=%s",isFirstParam?"?":"&", param.getKey(), URLEncoder.encode(param.getValue(), "UTF-8"));
            isFirstParam = false;
        }

        URL url = new URL(urlString+paramsUrl);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        for (Map.Entry<String,String> header :headers.entrySet()) {
            httpURLConnection.setRequestProperty(header.getKey(), header.getValue());
        }
        httpURLConnection.setConnectTimeout(connnectionTimeout);
        httpURLConnection.setRequestMethod( method );
        if(method.equals("POST")&&payload!=null) {
            byte[] postData = payload.getBytes("UTF-8");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.getOutputStream().write(postData);
        }
        httpURLConnection.connect();

        InputStream inputStream = httpURLConnection.getInputStream();

        httpURLConnection.disconnect();

        return new Response(inputStream);
    }
}
