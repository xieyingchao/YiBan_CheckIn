package com.xieyingchao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.ProxySelector;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HttpUtils {
    private CookieStore cookieStore;
    private CloseableHttpClient httpClient;

    HttpUtils() {
        cookieStore = new BasicCookieStore();
        httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault())).build();
        //.setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault()))
    }

    public CloseableHttpResponse get_response(String url, String params, List<BasicNameValuePair> headers, boolean redirectsEnabled) throws Exception {
        
        String realURL = url + "?" + params;
        //System.out.println(realURL);
        HttpGet httpGet = new HttpGet(realURL);

        for(BasicNameValuePair item : headers){
            if(item.getName().equals("Cookie")){
                String[] cookie = item.getValue().split("=");
                BasicClientCookie cookie1 = new BasicClientCookie(cookie[0], cookie[1]);
                cookie1.setDomain("uyiban.com");
                cookie1.setPath("/");
                cookie1.setAttribute(BasicClientCookie.DOMAIN_ATTR, "uyiban.com");
                cookie1.setAttribute(BasicClientCookie.PATH_ATTR, "/");
                cookieStore.addCookie(cookie1);
                continue;
            }
            httpGet.setHeader(item.getName(), item.getValue());
        }
        //System.out.println(Arrays.toString(httpGet.getAllHeaders()));
        //System.out.println(cookieStore.toString());
        //cookieStore.clear();

        //允许重定向吗？
        RequestConfig requestConfig = RequestConfig.custom().setRedirectsEnabled(redirectsEnabled)
                                                            .setConnectTimeout(5000)
                                                            .setSocketTimeout(5000)
                                                            .setConnectionRequestTimeout(5000).build();
        httpGet.setConfig(requestConfig);

        CloseableHttpResponse response = httpClient.execute(httpGet);
        //System.out.println(response.getStatusLine());
        return response;
    }

    public JSONObject get_JsonObject(String url, String params, List<BasicNameValuePair> headers, boolean redirectsEnabled) throws Exception {
        CloseableHttpResponse response = get_response(url, params, headers, redirectsEnabled);
        return getJsonObject(response);
    }

    public JSONObject post_JsonObject(String url, List<BasicNameValuePair> form, List<BasicNameValuePair> headers) throws Exception {

        HttpPost httpPost = new HttpPost(url);

        for(BasicNameValuePair item : headers) {
            httpPost.addHeader(item.getName(), item.getValue());
        }
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");

        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(form, StandardCharsets.UTF_8);
        httpPost.setEntity(entity);

        CloseableHttpResponse response = httpClient.execute(httpPost);
        return getJsonObject(response);
    }

    private JSONObject getJsonObject(CloseableHttpResponse response) throws IOException {
        HttpEntity httpEntity = response.getEntity();

        JSONObject jsonObject = null;

        if(null != httpEntity){
            String tmp = EntityUtils.toString(httpEntity);
            String resultString = new String(tmp.getBytes(StandardCharsets.UTF_8));
            jsonObject = JSON.parseObject(resultString);
        }
        response.close();
        return jsonObject;
    }

    public void close() throws IOException {
        httpClient.close();
    }
}