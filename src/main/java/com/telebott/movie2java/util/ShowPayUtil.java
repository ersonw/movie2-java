package com.telebott.movie2java.util;

import com.alibaba.fastjson.JSONObject;
import com.telebott.movie2java.data.ToPayNotify;
import com.telebott.movie2java.entity.CashInConfig;
import com.telebott.movie2java.entity.CashInOrder;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.DigestUtils;

import java.util.*;

public class ShowPayUtil {
    public static String toPay(CashInOrder showPayOrders, CashInConfig showPay) {
        try {
            //支付post下单地址
            String url = showPay.getDomain();
            String mchid = showPay.getMchId();//商户号
            String secretKey = showPay.getSecretKey();//商户秘钥
//            String money = String.valueOf(showPayOrders.getAmount());
//            //金额换算成分单位
//            BigDecimal rate = new BigDecimal(100);
//            Integer total_fee = new BigDecimal(money).multiply(rate).intValue();
//            System.out.println(total_fee);
//            String out_trade_no = UUID.randomUUID().toString().replace("-", "");

            Map<String, String> data = new HashMap<>();
            data.put("mchid", mchid);//商户号
//            data.put("total_fee", String.valueOf(total_fee));//金额单位分
//            data.put("total_fee", String.valueOf(showPayOrders.getAmount()));//金额单位分
            data.put("out_trade_no", showPayOrders.getOrderNo());//用户自定义订单号
            data.put("callback_url", showPay.getCallbackUrl());//支付成功同步跳转地址
            data.put("notify_url", showPay.getNotifyUrl());//支付成功异步通知地址
            data.put("error_url", showPay.getErrorUrl());//支付失败或者取消同步跳转地址

            String _sign = getWxSign(data, secretKey);
            data.put("sign", _sign);//签名

            String result = doPost(url, data);
            if (org.springframework.util.StringUtils.isEmpty(result) || result.indexOf("code") < 0) {
                System.out.println("下单请求错误");
            } else {
                JSONObject jsonObject = JSONObject.parseObject(result);
                if (jsonObject.getInteger("code") == 0) {
                    JSONObject data2 = jsonObject.getJSONObject("data");

                    //支付地址拼接
                    String payUrl = data2.getString("payUrl");
                    showPayOrders.setOrderId(data2.getString("orderId"));
//                    System.out.println(payUrl);
                    //使用302跳转到支付地址
                    return payUrl;
                } else {
                    System.out.println(result);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static boolean toPayNotify(ToPayNotify toPayNotify, CashInConfig showPay) {

        String secretKey = showPay.getSecretKey();//商户秘钥

        Map<String, String> map = new HashMap<>();
        map.put("out_trade_no", toPayNotify.getOut_trade_no());
        map.put("trade_no", toPayNotify.getTrade_no());
        map.put("total_fee", toPayNotify.getTotal_fee());
        map.put("mchid", toPayNotify.getMchid());

        String str_sign = getWxSign(map, secretKey);
        String sign = toPayNotify.getSign();
        if (sign.equals(str_sign)) {
            return true;
        }
        System.out.println("签名错误");
        return false;
    }

    public static String getWxSign(Map<String, String> map, String key) {

        String result = "";
        try {
            List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(map.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {

                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                    return (o1.getKey()).toString().compareTo(o2.getKey());
                }
            });

            // 构造签名键值对的格式
            List<String> values = new ArrayList<String>();
            for (Map.Entry<String, String> item : infoIds) {
                if (item.getKey() != null || item.getKey() != "") {
                    String k = item.getKey();
                    String v = item.getValue();
                    if (!(v == "" || v == null)) {
                        values.add(k + "=" + v);
                    }
                }
            }

            String sign = StringUtils.join(values, "&") + key;
            //System.out.println(sign);

            //进行MD5加密
            result = DigestUtils.md5DigestAsHex(sign.getBytes()).toUpperCase();
        } catch (Exception e) {
            return null;
        }
        return result;
    }

    public static String doPost(String url, Map<String, String> map) throws Exception {
        HttpClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;
        try {
            httpClient = new DefaultHttpClient();
            httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=" + "UTF-8");
            RequestConfig build = RequestConfig.custom().setConnectTimeout(10000).build();
            httpPost.setConfig(build);
            //设置参数
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            Iterator iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> elem = (Map.Entry<String, String>) iterator.next();
                list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
            }
            if (list.size() > 0) {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
                httpPost.setEntity(entity);
            }
            HttpResponse response = httpClient.execute(httpPost);
            if (response != null) {
                org.apache.http.HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, "UTF-8");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            //throw new Exception();
        }
        return result;
    }
//    public static void getOrder(String httpUrl, HashMap<String,Object> map) {
//        RestTemplate restTemplate = new RestTemplate();
//        JSONObject jsonObject = new JSONObject();
//        LinkedHashMap body = getMapValueForLinkedHashMap(map);
////        String body = getParams(map);
////        System.out.println(JSONObject.toJSONString(body));
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.TEXT_PLAIN);
//        HttpEntity httpEntity = new HttpEntity(body,headers);
//        try {
//            //使用 exchange 发送请求，以String的类型接收返回的数据
//            //ps，我请求的数据，其返回是一个json
//            ResponseEntity<String> strbody = restTemplate.exchange(httpUrl, HttpMethod.POST,httpEntity,String.class);
//            //解析返回的数据
//            JSONObject jsTemp = JSONObject.parseObject(strbody.getBody());
//            System.out.println(jsonObject.toJSONString());
////            return jsTemp;
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//    public static LinkedHashMap getMapValueForLinkedHashMap(Map dataMap) {
//        LinkedHashMap returnMap = new LinkedHashMap();
//        if (MapUtil.isEmpty(dataMap)) {
//            return returnMap;
//        }
//        Iterator iterator = dataMap.keySet().iterator();
//        while (iterator.hasNext()) {
//            Object objKey = iterator.next();
//            Object objValue = dataMap.get(objKey);
//            if (objValue instanceof Map) {
//                returnMap.put(objKey, getMapValueForLinkedHashMap((Map) objValue));
//            } else {
//                returnMap.put(objKey, objValue);
//            }
//        }
//        return returnMap;
//    }
//    public static LinkedHashMap getMapValueForLinkedHashMap(Map dataMap, String keyName) {
//        LinkedHashMap returnMap = new LinkedHashMap();
//        if (MapUtil.isEmpty(dataMap)) {
//            return returnMap;
//        }
//        Object valueObj = dataMap.get(keyName);
//        if (valueObj instanceof Map) {
//            Map objMap = (Map) valueObj;
//            Iterator iterator = objMap.keySet().iterator();
//            while (iterator.hasNext()) {
//                Object objKey = iterator.next();
//                Object objValue = objMap.get(objKey);
//                if (objValue instanceof Map) {
//                    returnMap.put(objKey, getMapValueForLinkedHashMap((Map)objValue));
//                } else {
//                    returnMap.put(objKey, objValue);
//                }
//            }
//        }
//        return returnMap;
//    }
//    public static String request(String httpUrl, String body) {
//        BufferedReader reader = null;
//        String result = null;
//        StringBuilder sbf = new StringBuilder();
//        System.out.println(body.getBytes().length);
//        byte[] data = body.getBytes(StandardCharsets.UTF_8);
//        try {
//            URL url = new URL(httpUrl);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("POST");
//            connection.setDoOutput(true);
//            connection.setInstanceFollowRedirects(false);
//            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
////            connection.setRequestProperty("Content-Type", "text/plain");
//            connection.setRequestProperty("charset", "utf-8");
//            connection.setRequestProperty( "Content-Length", Integer.toString( data.length ));
//            connection.setUseCaches( false );
//            connection.connect();
//            try( DataOutputStream wr = new DataOutputStream( connection.getOutputStream())) {
//                wr.write( data );
//            }
//            InputStream is = connection.getInputStream();
//            reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
//            String strRead = reader.readLine();
//            if (strRead != null) {
//                sbf.append(strRead);
//                while ((strRead = reader.readLine()) != null) {
//                    sbf.append("\n");
//                    sbf.append(strRead);
//                }
//            }
//            reader.close();
//            result = sbf.toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println(result);
//        return result;
//    }
//    public static String getSign(String params, String key){
//        String sign = params + key;
//        return DigestUtils.md5DigestAsHex(sign.getBytes()).toUpperCase();
//    }
//    public static boolean verifySign(ToPayNotify toPayNotify){
//        return false;
//    }
//    public static String getParams(HashMap<String,Object> map){
//        String[] sortKeys = map.keySet().toArray(new String[]{});
//        Arrays.sort(sortKeys);
//        StringBuilder builder = new StringBuilder();
//        for (String key : sortKeys) {
//            builder.append(key).append("=").append(map.get(key)).append("&");
//        }
//        builder.deleteCharAt(builder.length()-1);
//        return builder.toString();
//    }

}
