package com.elemeHelper.http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpUtil2 {

    public static String getRequest(String urlStr, String charset) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestMethod("GET");
//			connection.setRequestProperty("Accept", "text/plain;charset=utf-8");
//			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
//			connection.setRequestProperty("user-agent", "Mozilla/5.0 (Linux; Android 5.1; OPPO R9tm Build/LMY47I; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/53.0.2785.49 Mobile MQQBrowser/6.2 TBS/043128 Safari/537.36 V1_AND_SQ_7.0.0_676_YYB_D PA QQ/7.0.0.3135 NetType/4G WebP/0.3.0 Pixel/1080");

            BufferedReader reader = null;
            StringBuffer resultBuffer = new StringBuffer();
            String tempLine = null;

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), charset));
            } else {
                System.err.println("请求发送错误：" + responseCode);
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), charset));
            }

            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
            }
            reader.close();
            reader = null;

            String result = resultBuffer.toString();
            System.err.println("resp >>> " + result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getRequest(String urlStr, Map<String, String> headerMap) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestMethod("GET");

            if (headerMap != null) {
                for (String key : headerMap.keySet()) {
                    connection.setRequestProperty(key, headerMap.get(key));
                }
            }

            BufferedReader reader = null;
            StringBuffer resultBuffer = new StringBuffer();
            String tempLine = null;

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            } else {
                System.err.println("请求发送错误：" + responseCode);
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"));
            }

            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
            }
            reader.close();
            reader = null;

            String result = resultBuffer.toString();
            System.err.println("resp >>> " + result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String postRequest(String urlStr, Map<String, String> paramMap) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            if (paramMap != null && paramMap.size() > 0) {
                String paramStr = "";
                BufferedWriter writer = null;
                for (String key : paramMap.keySet()) {
                    paramStr += key + "=" + paramMap.get(key) + "&";
                }
                paramStr = paramStr.substring(0, paramStr.length() - 1);
                writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                writer.write(paramStr);
                writer.flush();
                writer.close();
                writer = null;
            }

            BufferedReader reader = null;
            StringBuffer resultBuffer = new StringBuffer();
            String tempLine = null;

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            } else {
                System.err.println("请求发送错误：" + responseCode);
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"));
            }

            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
            }
            reader.close();
            reader = null;

            String result = resultBuffer.toString();
            System.err.println("resp >>> " + result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String postRequest(String urlStr, Map<String, String> headerMap, Map<String, String> paramMap) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            if (headerMap != null) {
                for (String key : headerMap.keySet()) {
                    System.out.println(key + "----" + headerMap.get(key));
                    connection.setRequestProperty(key, headerMap.get(key));
                }
            }
            if (paramMap != null && paramMap.size() > 0) {
                String paramStr = "";
                BufferedWriter writer = null;
                for (String key : paramMap.keySet()) {
                    paramStr += key + "=" + paramMap.get(key) + "&";
                }
                paramStr = paramStr.substring(0, paramStr.length() - 1);
                writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                writer.write(paramStr);
                writer.flush();
                writer.close();
                writer = null;
            }

            BufferedReader reader = null;
            StringBuffer resultBuffer = new StringBuffer();
            String tempLine = null;

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            } else {
                System.err.println("请求发送错误：" + responseCode);
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"));
            }

            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
            }
            reader.close();
            reader = null;

            String result = resultBuffer.toString();
            System.err.println("resp >>> " + result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, String> getCookieByPostRequest(String urlStr, Map<String, String> headerMap,
        Map<String, String> paramMap) {
        Map<String, String> result = new HashMap<>();
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            if (headerMap != null) {
                for (String key : headerMap.keySet()) {
                    connection.setRequestProperty(key, headerMap.get(key));
                }
            }
            if (paramMap != null && paramMap.size() > 0) {
                String paramStr = "";
                BufferedWriter writer = null;
                for (String key : paramMap.keySet()) {
                    paramStr += key + "=" + paramMap.get(key) + "&";
                }
                paramStr = paramStr.substring(0, paramStr.length() - 1);
                writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                writer.write(paramStr);
                writer.flush();
                writer.close();
                writer = null;
            }

            BufferedReader reader = null;
            StringBuffer resultBuffer = new StringBuffer();
            String tempLine = null;

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            } else {
                System.err.println("请求发送错误：" + responseCode);
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"));
            }

            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
            }
            reader.close();
            reader = null;

            String resp = resultBuffer.toString();
            System.err.println("resp >>> " + resp);
            result.put("body", resp);

            Map<String, List<String>> headerFields = connection.getHeaderFields();
            for (String key : headerFields.keySet()) {
                if (key != null && "Set-Cookie".equals(key)) {
                    String cookieStr = "";
                    for (String item : headerFields.get(key)) {
                        System.err.println(key + " >>> " + item);
                        item = item.replaceAll(" ", "");
                        String[] split = item.split(";");
                        String cookieKey = "";
                        for (int i = 0; i < split.length - 1; i++) {
                            String[] split2 = split[i].split("=");
                            cookieKey = split2[0];
                            if ("Max-Age".equals(cookieKey) || "Path".equals(cookieKey) || "body"
                                .equals(cookieKey) || "Domain".equals(cookieKey)) {
                                continue;
                            }
                            if ("USERID".equals(cookieKey)) {
                                result.put("USERID", split2[1]);
                            }
                            cookieStr += split[i] + ";";
                        }
                    }
                    result.put("cookie", cookieStr);
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String postRequestByJson(String urlStr, Map<String, String> paramMap) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            if (paramMap != null && paramMap.size() > 0) {
                String paramStr = "{";
                BufferedWriter writer = null;
                for (String key : paramMap.keySet()) {
                    paramStr += "\"" + key + "\"" + ":" + "\"" + paramMap.get(key) + "\"" + ",";
                }
                paramStr = paramStr.substring(0, paramStr.length() - 1);
                paramStr += "}";
                writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                writer.write(paramStr);
                writer.flush();
                writer.close();
                writer = null;
            }

            BufferedReader reader = null;
            StringBuffer resultBuffer = new StringBuffer();
            String tempLine = null;

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            } else {
                System.err.println("请求发送错误：" + responseCode);
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"));
            }

            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
            }
            reader.close();
            reader = null;

            String result = resultBuffer.toString();
            System.err.println("resp >>> " + result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("source", "index_nav");
        paramMap.put("form_email", "1341357259");
        paramMap.put("form_password", "dbcjh2018");
        getCookieByPostRequest("https://www.douban.com/accounts/login", null, paramMap);
    }

}
