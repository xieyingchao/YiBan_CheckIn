package com.xieyingchao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class App 
{
    static HttpUtils httpUtils;
    private static PropertiesUtils properties;
    private static String access_token = "";
    private static String shareLink = "";
    private static String WFId = "";
    private static String CSRF = "d5102915bd51c3653cc87967be65ce2c";
    private static List<BasicNameValuePair> headers = new ArrayList<BasicNameValuePair>(){
        private static final long serialVersionUID = 1L;
        {
            String USERAGENT = "Mozilla/5.0 (Linux; Android 7.1.1; OPPO R9s Build/NMF26F; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/81.0.4044.117 Mobile Safari/537.36 yiban_android";
            add(new BasicNameValuePair("User-Agent", USERAGENT));
            String ORIGIN = "https://c.uyiban.com";
            add(new BasicNameValuePair("Origin", ORIGIN));
        }
    };

    public static void main( String[] args ) throws Exception {
        httpUtils = new HttpUtils();

        String path = "PersonInfo.properties";
        //System.out.println(path);
        if (args.length != 0) path = args[0];
        try {
            properties = new PropertiesUtils(path);
        }catch (FileNotFoundException e){
            System.out.println("==================== 找不到配置文件 ====================");
            System.out.println("===请将配置文件命名为PersonInfo.properties，并放在jar包同目录下，或指定配置文件路径===");
            return;
        }

        WFId = properties.getValue("WFId");

        Date today = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("================= "+ simpleDateFormat.format(today) +" ================");

        String result = autoLogin();
        System.out.println("======================= result ======================");
        System.out.println("                    " + result);
        push2WeChat(result, !shareLink.equals(""));

        httpUtils.close();
    }

    /**
     * 使用记录自动登陆
     * @return 登陆结果
     * @throws Exception
     */
    public static String autoLogin() throws Exception {
        access_token = properties.getValue("access_token");

        String url = "https://mobile.yiban.cn/api/v3/passport/autologin";
        String params = "access_token=" + access_token;

        JSONObject jsonObject = httpUtils.get_JsonObject(url, params, headers, false);
        int statusCode = jsonObject.getInteger("response");
        if(statusCode != 100){
            System.out.println("==================== 自动登陆失败 ===================");
            System.out.println("================== 尝试账号密码登陆 =================");
            return login();
        }else{
            System.out.println("==================== 自动登陆成功 ===================");
            return getVerifyRequest(access_token);
        }
    }

    /**
     * 使用账号密码登陆，获取access_token通行证
     * @return 登陆结果
     * @throws Exception
     */
    public static String login() throws Exception {
        String account = properties.getValue("account");
        String password = properties.getValue("password");
        String version = properties.getValue("appVersion");

        String encrypString = RSAEncrypt.passwdEncrypt(password);

        String url = "https://mobile.yiban.cn/api/v2/passport/login";

        String params = "account=" + account +
                        "&passwd=" + URLEncoder.encode(encrypString, "utf-8") +
                        "&ct=2&app=1&v=" + version +
                        "&apn=wifi&identify=123456789&sig=0b3c4822b21b2948&token=&device=OPPO%3AOPPO+R9s&sversion=25";
        JSONObject jsonObject = httpUtils.get_JsonObject(url, params, headers, false);
        String statu = jsonObject.getString("response");
        if(!statu.equals("100")){
            System.out.println("====================== 登陆失败 =====================");
            return "登录失败";
        }
        access_token = jsonObject.getJSONObject("data").getString("access_token");
        properties.updateValue("access_token", access_token);  //将access_token写回到配置文件用于自动登陆

        System.out.println("====================== 登陆成功 =====================");
        System.out.println("  access_token：   " + access_token);
        System.out.println("=====================================================");

        return getVerifyRequest(access_token);
    }

    /**
     * 前往首页
     * @throws Exception
     */
    public static void goIndex() throws Exception {

        String url = "https://mobile.yiban.cn/api/v3/home";

        JSONObject jsonObject = httpUtils.get_JsonObject(url, "access_token=" + access_token, headers, false);
        String statu = jsonObject.getString("response");
        if(!statu.equals("100")){
            System.out.println("=====易班校本化Url获取失败====");
            return;
        }
        JSONObject app = jsonObject.getJSONObject("data").getJSONArray("hotApps").getJSONObject(5);
        //System.out.println(app.toJSONString());
        String localSchoolUrl = app.getString("url");
        System.out.println("=====易班校本化Url获取成功====");
        System.out.println("   url：" + localSchoolUrl + "\n==============================");
    }

    /**
     * 获取VerifyRequest，用于二次认证
     * @param access_token 通行证
     * @return 获取结果
     * @throws Exception
     */
    public static String getVerifyRequest(String access_token) throws Exception {
        String url = "http://f.yiban.cn/iapp/index";

        String params = "act=iapp7463&v=" + access_token;

        CloseableHttpResponse response = httpUtils.get_response(url, params, headers, false);

        Header location = response.getFirstHeader("Location");
        if(location == null){
            System.out.println("============== verify_request 获取失败 ==============");
            return "verify_request获取失败";
        }
        String verify_request = location.getValue().split("=")[1].split("&")[0];
        System.out.println("============== verify_request 获取成功 ==============");
        System.out.println("   verify_request：" + verify_request);
        System.out.println("=====================================================");

        response.close();
        return secondAuth(verify_request);
    }

    /**
     * 二次认证，获取下发的cookie
     * @param verify_request 之前获取的认证码
     * @return 认证结果
     * @throws Exception
     */
    public static String secondAuth(String verify_request) throws Exception{
        String url = "https://api.uyiban.com/base/c/auth/yiban";

        headers.add(new BasicNameValuePair("Cookie", "csrf_token=" + CSRF));

        String params = "verifyRequest=" + verify_request +
                        "&CSRF=" + CSRF;
        JSONObject jsonObject = httpUtils.get_JsonObject(url, params, headers, false);
        int statusCode = jsonObject.getInteger("code");

        headers.remove(2);  //之前传的cookie已经保存了，删除掉header中重复的
        if(statusCode != 0){
            System.out.println("===================== 二次认证失败 ===================");
            return "二次认证失败";
        }
        System.out.println("============== 认证成功，cookies已更新 ==============");

        return getTaskId();
    }

    /**
     * 获取健康打卡的任务id
     * @return 获取结果
     * @throws Exception
     */
    public static String getTaskId() throws Exception {
        String url = "https://api.uyiban.com/officeTask/client/index/uncompletedList";

        String params = "CSRF=" + CSRF;

        JSONObject jsonObject = httpUtils.get_JsonObject(url, params, headers, false);
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        if(jsonArray== null || jsonArray.size() == 0){
            System.out.println("=================== 所有任务已完成 ==================");
            return "所有任务已完成";
        }
        String taskId = jsonArray.getJSONObject(0).getString("TaskId");
        System.out.println("=================== 获取TaskId成功 ===================");
        System.out.println("   TaskId: " + taskId);
        System.out.println("=====================================================");

        return getWFId(taskId);
    }

    /**
     * 获取表单id，表单id能判断表单内容是否变化，获取表单发布机构
     * @param TaskId 之前的任务id
     * @return 返回获取的结果
     * @throws Exception
     */
    public static String getWFId(String TaskId) throws Exception {
        String url = "https://api.uyiban.com/officeTask/client/index/detail";

        String params = "TaskId=" + TaskId + "&CSRF=" + CSRF;

        JSONObject jsonObject = httpUtils.get_JsonObject(url, params, headers, false);
        JSONObject dataJson = jsonObject.getJSONObject("data");
        String tmpId = dataJson.getString("WFId");
        if(!WFId.equals(tmpId)){
            System.out.println("=============== WFId已更新，表单已更改 ===============");
            WFId = tmpId;
            properties.updateValue("WFId", WFId);
            System.out.println("============ 已为你更改WFId，请更改提交内容 ============");
            return "WFId已更新，表单已更改";
        }
        System.out.println("==================== WFId尚未更改 ====================");

        String title = dataJson.getString("Title");
        String PubOrgName = dataJson.getString("PubOrgName");
        String PubPersonName = dataJson.getString("PubPersonName");
        String extend = patchExtend(TaskId, title, PubOrgName, PubPersonName);

        return submitForm(extend);
    }

    /**
     * 提交表单，获取结果的initiateId
     * @param extend 获取到的表单发布机构
     * @return 返回获取到的结果
     * @throws Exception
     */
    public static String submitForm(String extend) throws Exception{
        String url = "https://api.uyiban.com/workFlow/c/my/apply/" + WFId + "?CSRF=" + CSRF;

        String data = properties.getValue("data");
        List<BasicNameValuePair> form = new ArrayList<>();
        form.add(new BasicNameValuePair("data",data));
        form.add(new BasicNameValuePair("extend",extend));

        JSONObject jsonObject = httpUtils.post_JsonObject(url, form, headers);
        int statusCode = jsonObject.getInteger("code");
        if(statusCode != 0){
            System.out.println("==================== 表单提交失败 ====================");
            return "表单提交失败";
        }else{
            System.out.println("==================== 表单提交成功 ====================");
            String initiateId = jsonObject.getString("data");
            System.out.println("   InitiateId: " + initiateId);
            System.out.println("=====================================================");
            return getShareLink(initiateId);
        }
    }

    /**
     * 根据initiateId，获取表单的分享链接
     * @param initiateId 表单结果
     * @return 返回获取到的联机
     * @throws Exception
     */
    public static String getShareLink(String initiateId) throws Exception {
        String url = "https://api.uyiban.com/workFlow/c/work/share";

        String params = "InitiateId=" + initiateId + "&CSRF=" + CSRF;

        JSONObject jsonObject = httpUtils.get_JsonObject(url, params, headers, false);
        int statusCode = jsonObject.getInteger("code");
        shareLink = jsonObject.getJSONObject("data").getString("uri");
        if(statusCode != 0){
            System.out.println("================== shareLink获取失败 =================");
            return "shareLink获取失败";
        }else{
            System.out.println("================== shareLink获取成功 =================\n" + "  shareLinke: " + shareLink);
            System.out.println("=====================================================");
            return shareLink;
        }
    }

    /**
     * 利用server酱将结果发送到微信
     * @param result 要发送的内容
     * @param isLink 是否为链接
     * @throws Exception
     */
    public static void push2WeChat(String result, Boolean isLink) throws Exception {
        String SCKEY = properties.getValue("SCKEY");

        String url = "https://sc.ftqq.com/" + SCKEY + ".send";
        String params = "text=易班健康打卡结果&desp=";

        if (isLink){
            String linkMD = "[打卡成功分享链接](" + result + ")";
            params += linkMD;
        }else{
            params += result;
        }
        JSONObject jsonObject = httpUtils.get_JsonObject(url, params, headers, false);
        int statusCode = jsonObject.getInteger("errno");
        String errmsg = jsonObject.getString("errmsg");
        if(statusCode != 0){
            System.out.println("================== 微信消息推送失败 =================");
            System.out.println("================= " + errmsg + " ================");
        }
        else System.out.println("================= 微信消息推送成功 =================");
    }

    public static String patchExtend(String TaskId, String title, String PubOrgName, String PubPersonName){
        JSONObject json1 = new JSONObject();
        json1.put("label", "任务名称");
        json1.put("value", title);

        JSONObject json2 = new JSONObject();
        json2.put("label", "发布机构");
        json2.put("value", PubOrgName);

        JSONObject json3 = new JSONObject();
        json3.put("label", "发布人");
        json3.put("value", PubPersonName);

        JSONArray content = new JSONArray();
        content.add(json1);
        content.add(json2);
        content.add(json3);

        JSONObject extend = new JSONObject();
        extend.put("content",content);
        extend.put("title", "任务信息");
        extend.put("TaskId", TaskId);
        return extend.toString();
    }

}
