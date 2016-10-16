package com.example.utils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class HttpRequestUtil implements Runnable {
    //get the json data from remote server
    public JSONObject data;

    private String requestType;
    private String requestUrl;
    private List<NameValuePair> postParams;
    private HttpRequestCallBackUtil httpRequestCallBackUtil;
    //10 seconds for over time of request
    private static final int CONNECTION_TIMEOUT = 100000;
    private static final String Log_Tag = "HttpRequestUtil";

    //constructor for get the request-type, url and parameters of the request
    public HttpRequestUtil(String requestType, String requestUrl, List<NameValuePair> postParams, HttpRequestCallBackUtil httpRequestCallBackUtil){
        this.requestType     = requestType;
        this.requestUrl      = requestUrl;
        this.postParams      = postParams;
        this.httpRequestCallBackUtil = httpRequestCallBackUtil;
    }

    //transform the json-data which get from the remote server
    private void requestData(HttpResponse httpResponse, int messageCode){
        String requestResult = null;
        try {
            requestResult = EntityUtils.toString(httpResponse.getEntity());
            try {
                JSONObject jsonData = new JSONObject(requestResult);
                data = jsonData;
                httpRequestCallBackUtil.getData(data, messageCode);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        HttpResponse httpResponse = null;

        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, CONNECTION_TIMEOUT);

        //get-type
        if (requestType == "GET") {
            HttpGet httpGet = new HttpGet(requestUrl);
            try {
                httpResponse = new DefaultHttpClient().execute(httpGet);
                requestData(httpResponse, httpResponse.getStatusLine().getStatusCode());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //post-type
        else if (requestType == "POST") {
            HttpPost httpPost = new HttpPost(requestUrl);
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(postParams, HTTP.UTF_8));
                try {
                    httpResponse = new DefaultHttpClient().execute(httpPost);
                    requestData(httpResponse, httpResponse.getStatusLine().getStatusCode());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}
