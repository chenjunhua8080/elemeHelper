package com.elemeHelper.thread;

import com.elemeHelper.http.HttpUtil2;
import com.elemeHelper.result.Result;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class LimitHongbaoThread extends Thread {

    public LimitHongbaoThread() {
        // TODO Auto-generated constructor stub
    }

    public LimitHongbaoThread(String name) {
        this.setName(name);
    }

    //限量
    public static final String url_limit_check = "https://h5.ele.me/restapi/member/v1/sign_in/limit/hongbao/info?channel=app";
    public static final String url_limit_receive = "https://h5.ele.me/restapi/member/v1/users/USERID/sign_in/limit/hongbao";

    //苏宁校时
    public static final String url_sn_check = "http://api.m.taobao.com/rest/api3.do?api=mtop.common.getTimestamp";


    public static Long checkLimitHongbao() {
        long startTime = System.currentTimeMillis();
        System.err.println("开始时间：" + startTime);
        String resp = HttpUtil2.getRequest(url_limit_check, "utf-8");
        long endTime = System.currentTimeMillis();
        System.err.println("结束时间：" + endTime);
        long usrTime = endTime - startTime;
        System.err.println("耗时：" + usrTime);
        String body = resp;
        if (body.contains("current_at")) {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = null;
            try {
                jsonObject = (JSONObject) parser.parse(body);
//                jsonObject = (JSONObject) jsonObject.get("data");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Long currentAt = Long.valueOf(jsonObject.get("current_at").toString()) * 1000;
            System.err.println("对方时间：" + currentAt);
            long delay = currentAt - startTime;
            System.err.println("延迟：" + delay);
            return delay;
        }
        return (long) 0;
    }

    /**
     * 本地整点时间比对他服务器的时间，在10s内，返回true
     */
    public static boolean checkTime(Long delay) {
        int[] times = {10, 14, 18, 20};
        int minute = 0;
        Calendar cal;
        long targetTime;
        long cha;
        for (int i = 0; i < times.length; i++) {
            cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, times[i]);
            cal.set(Calendar.MINUTE, minute);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            targetTime = cal.getTimeInMillis();
            cha = targetTime - System.currentTimeMillis();
            if (cha - delay < 300 && cha - delay > 0) {
                return true;
            }
        }
        return false;
    }

    public static Result getLimitHongbao(String ck, String userId) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("cookie", ck);
        headerMap.put("User-agent",
            "Mozilla/5.0 (Linux; Android 5.0; SM-N9100 Build/LRX21V) > AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 > Chrome/37.0.0.0 Mobile Safari/537.36 V1_AND_SQ_5.3.1_196_YYB_D > QQ/5.3.1.2335 NetType/WIFI");
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("channel", "app");
        paramMap.put("userId", userId);
        String resp = HttpUtil2.postRequest(url_limit_receive.replace("USERID", userId), headerMap, paramMap);
        String body = resp;
        if (body.contains("message")) {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = null;
            try {
                jsonObject = (JSONObject) parser.parse(body);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return new Result(-1, resp);
        }
        System.out.println("ok");
        return new Result(0, resp);
    }

    @Override
    public void run() {
        getLimitHongbao(
            "ubt_ssid=2dc9h9cjnkcngj1aaekzeeol7pp9maau_2019-06-19; _utrace=e1f0c1c8a3571c3541890f9a6cd4f4c5_2019-06-19; perf_ssid=ue5yytkrqvena9mrto4rc5o2bnri4qk2_2019-06-19; cna=IP16EmZ0YmECAbcYjWohDOzd; _bl_uid=nbjtIx5m3245q3xRR2Ivz8v4RtmI; track_id=1560944326|5c9d17cde1f2ba8330b4f5436d4f3869ea2f5c6f9f8283db79|01566040367790e407a6f6920f53096e; USERID=145998491; UTUSER=145998491; SID=qFEQaJXHHAhtGg8zvdcnjS4zVscKuZgdBUYw; ZDS=1.0|1560944326|7rMwz66LJ4QpK77emRZHTLgX1Eofw0WK1ixcAGUiQfMmoO0cdXgyNmQ3gG0ZNMR+; tzyy=96f572671da1057585fe6e89c9e785d7; ut_ubt_ssid=pf0ppvdjsib3qf31b06lhq7r7y7r91h6_2019-06-19; isg=BPT0I614FS0VwoFhIBHTmz87xbLK3RmmFPLco45VJn8C-ZVDtt0dRnKre3GEGlAP",
            "145998491");
    }

    public static void main(String[] args) {
        long sum = 0L;
        for (int i = 0; i < 10; i++) {
            sum += checkLimitHongbao();
        }
        //10次请求，计算延时平均值
        long delay = sum / 10;
        System.out.println("==========平均延迟：" + delay + "============");

        while (!checkTime(delay)) {

        }
        System.out.println("==================开始======================");
        ExecutorService pool = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 5; i++) {
            pool.execute(() -> {
                getLimitHongbao(
                    "ubt_ssid=ed2toeyjnjwxz5s910zlpzs0ntzmpj7x_2019-06-20; perf_ssid=7m138ej9lbywz9c0rv7gp3o9awspqov4_2019-06-20; _bl_uid=1tjhbxg849n926jUbbgwrkR1qOze; cna=IPsMFRApvWoCAbcGOse9vc/i; _utrace=3cac16c330b3c0786b03fdf9f7199005_2019-06-20; track_id=1561010947|d6cfd1b7b6d3e66b457464942408a77a963d2150a14c732d54|6315b9a5c220b8aff54bb08813901361; USERID=5893951186; UTUSER=5893951186; SID=lSJf68EBhbVSIBS9M2KqRuazQ7qNJx3mQDIg; ZDS=1.0|1561010947|G6R+HOofw5vL3lBhDjl5K9RYCEQF2Z9q/II6Se6NmY/U7Eidl44tatK8W2hKDKzu; isg=BAkJYYugsLP0-ExHOazssEZQGDWj_vzm0LUXFKt-gfAv8i0E-6bHWb5jMBBhqpXA",
                    "5893951186");
                checkLimitHongbao();
            });
        }
        System.out.println("==================结束======================");
    }

}
