package com.example.guoshijie.studyword.HttpHelper;

import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TranslateSentence {
    /**
     * 将字符串转成MD5值
     *
     * @param string
     * @return
     */
    private static String appid="20200428000432248";
    private static String tattedcode="1435660288";
    private static String keyt="lOS8KiBU6fAASG4rmMBQ";
    private static String url="";
    public static String translate(String string){
        String KEY=stringToMD5(appid+string+tattedcode+keyt);
        url="http://api.fanyi.baidu.com/api/trans/vip/translate?" +
                "q="+string +
                "&from=en&to=zh&appid="+appid+"&salt="+tattedcode+"&sign="+KEY;
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(url).build();
        Response response= null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //textView.setText(response.body().string());
        //Toast.makeText(MainActivity.this,"ss",LENGTH_SHORT).show();
        String data= null;
        try {
            data = response.body().string();
//            Log.d("translatedata",data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Pattern pattern = Pattern.compile("\\[\\{.*\\}\\]");
        Matcher matcher = pattern.matcher(data);
        String result="";
        while (matcher.find()) {
            result=matcher.group();
        }
        Pattern pattern2 = Pattern.compile("\"src.*\"");
        Matcher matcher2 = pattern2.matcher(result);
        result="";
        while (matcher2.find()) {
            result=matcher2.group();
        }
        String[] s=result.split(":");
        try {
            result=decode(s[1].substring(1,s[1].length()-7))+"\n"+decode(s[2].substring(1,s[2].length()-1));
        }catch (Exception E){
            return "";
        }
        return result;
    }
    public static String decode(String unicodeStr) {
        if (unicodeStr == null) {
            return null;
        }
        StringBuffer retBuf = new StringBuffer();
        int maxLoop = unicodeStr.length();
        for (int i = 0; i < maxLoop; i++) {
            if (unicodeStr.charAt(i) == '\\') {
                if ((i < maxLoop - 5) && ((unicodeStr.charAt(i + 1) == 'u') || (unicodeStr.charAt(i + 1) == 'U')))
                    try {
                        retBuf.append((char) Integer.parseInt(unicodeStr.substring(i + 2, i + 6), 16));
                        i += 5;
                    } catch (NumberFormatException localNumberFormatException) {
                        retBuf.append(unicodeStr.charAt(i));
                    }
                else
                    retBuf.append(unicodeStr.charAt(i));
            } else {
                retBuf.append(unicodeStr.charAt(i));
            }
        }
        return retBuf.toString().replace("\\\"","\"");
    }
    private static String stringToMD5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }
}
