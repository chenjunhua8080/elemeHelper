package com.elemeHelper.thread;

import com.elemeHelper.http.HttpUtil2;
import com.elemeHelper.result.Result;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
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
            Long currentAt = Long.valueOf(jsonObject.get("current_at").toString())*1000;
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
        Calendar cal = null;
        Long targetTime = null;
        for (int i = 0; i < times.length; i++) {
            cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, times[i]);
            cal.set(Calendar.MINUTE, minute);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            targetTime = cal.getTimeInMillis();
            if (targetTime-System.currentTimeMillis()==delay) {
                return true;
            }
        }
        return false;
    }

    public static Result getLimitHongbao(String ck) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("cookie", ck);
        headerMap.put("User-agent",
            "Mozilla/5.0 (Linux; Android 5.0; SM-N9100 Build/LRX21V) > AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 > Chrome/37.0.0.0 Mobile Safari/537.36 V1_AND_SQ_5.3.1_196_YYB_D > QQ/5.3.1.2335 NetType/WIFI");
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("channel", "app");
        paramMap.put("userId", "145998491");
        String resp = HttpUtil2.postRequest(url_limit_receive.replace("USERID", "4765838938"), headerMap, paramMap);
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

    public static boolean isCheck = true;
    public static boolean isBegin = false;
    public static int sleep = 1000;

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (isCheck) {
                isBegin = checkTime(System.currentTimeMillis());
                System.out.println(this.getName() + isBegin + ":" + System.currentTimeMillis());
            }
            if (isBegin) {
                isCheck = false;
                sleep = 600;
                Result limitHongbao = getLimitHongbao(
                    "SID=iIhNhN0A3Rh1qbtT6jHXtHCF7B2IQBIbH7Hw;USERID=4765838938;track_id=1543295210|e8a5f3b5ca53306fad9740177b3d99497afdc1ee3b746159c4|72f72fe14b683a44e3ef4d81776240b0;");
                if (limitHongbao.getCode() != -1) {
                    this.interrupt();
                    break;
                }
            }
        }
    }

    public static void main(String[] args) {
        long sum = 0L;
        for (int i = 0; i < 10; i++) {
            sum += Math.abs(checkLimitHongbao());
        }
        //10次请求，计算延时平均值
        long delay = sum / 10;
        System.out.println("==========平均延迟："+delay+"============");

        while (!checkTime(delay)){

        }
        System.out.println("开始。。。");
        checkLimitHongbao();

//        for (int i = 0; i < 1; i++) {
//            new LimitHongbaoThread("T" + i + " : ").start();
//        }
    }

}
