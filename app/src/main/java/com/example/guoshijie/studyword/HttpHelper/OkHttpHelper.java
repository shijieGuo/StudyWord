package com.example.guoshijie.studyword.HttpHelper;


import android.os.Environment;
import android.util.Log;

import com.example.guoshijie.studyword.Json.wordjson;
import com.example.guoshijie.studyword.Word.wordInCloud;
import com.google.gson.Gson;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;

import static android.content.ContentValues.TAG;

public class OkHttpHelper {
    private static String[] wordmessage=new String[7];
    private static wordInCloud word;
    private static ArrayList<String> arrayList;
    public OkHttpHelper(){

    }

//英文查找中文
public  static wordInCloud findchIncloud(final String string){

                OkHttpClient client=new OkHttpClient();
                Request request=new Request.Builder().url("https://dict-co.iciba.com/api/dictionary.php?w="+
                        string+"&key=486316F23DB09D4D536C71EC695111BE").build();
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
        Log.d("data",data);
    } catch (IOException e) {
        e.printStackTrace();
    }
    //data为xml文件内容
    //解析并返回word对象
                word =new wordInCloud(parseJinshanEnglishToChineseXMLWithPull(data));
//                Log.d("datadata",word11.getQueryText());
        return word;
}

     //使用pull方式解析金山词霸返回的XML数据。
public static String[] parseJinshanEnglishToChineseXMLWithPull(String result) {

        try {

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(result));
            int eventType = xmlPullParser.getEventType();

            String queryText = "";      //查询文本
            String voiceText = "";      //发音信息
            String voiceUrlText = "";   //发音地址信息
            String meanText = "";       //基本释义信息
            String exampleText = "";    //例句信息

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = xmlPullParser.getName();
                switch (eventType) {
                    //开始解析
                    case XmlPullParser.START_TAG: {
                        switch (nodeName) {
                            case "key":
                                queryText += xmlPullParser.nextText();
                                break;
                            case "ps":
                                voiceText += xmlPullParser.nextText() + "|";
                                break;
                            case "pron":
                                voiceUrlText += xmlPullParser.nextText() + "|";
                                break;
                            case "pos":
                                meanText += xmlPullParser.nextText() + "  ";
                                break;
                            case "acceptation":
                                meanText += xmlPullParser.nextText();
                                break;
                            case "orig":
                                exampleText += xmlPullParser.nextText();
                                exampleText = exampleText.substring(0,exampleText.length()-1);
                                break;
                            case "trans":
                                exampleText += xmlPullParser.nextText();
                                break;
                            default:
                                break;
                        }
                    }
                    default:
                        break;
                }
                eventType = xmlPullParser.next();
            }
            if(voiceText.isEmpty()||voiceUrlText.isEmpty()){
                wordmessage[1]="";
                wordmessage[2]="";
                wordmessage[3]="";
                wordmessage[4]="";
            }
            else {
                    String[] voiceArray = voiceText.split("\\|");
                    String[] voiceUrlArray = voiceUrlText.split("\\|");
                    wordmessage[1]="英 ["+voiceArray[0]+"]";
                    wordmessage[2]=voiceUrlArray[0];
                    if(voiceArray.length>1||voiceUrlArray.length>2){
                        wordmessage[3]="美 ["+voiceArray[1]+"]";
                        wordmessage[4]=voiceUrlArray[1];
                    }
                    else {
                        wordmessage[3]="";
                        wordmessage[4]="";
                    }

            }
            if(meanText.isEmpty()){
                wordmessage[5]="";
            }
            else {
                meanText = meanText.substring(0,meanText.length()-1);
                wordmessage[5]=meanText;

            }
            if(exampleText.isEmpty()){
                wordmessage[6]="";
            }
            else {
                exampleText = exampleText.substring(1,exampleText.length());
                wordmessage[6]=exampleText;
            }
//            Log.d("queryText",queryText);
//            Log.d("voiceEnText","["+voiceArray[0]+"]");
//            Log.d("voiceEnUrlText",voiceUrlArray[0]);
//            Log.d("voiceAmText","["+voiceArray[1]+"]");
//            Log.d("voiceAmUrlText",voiceUrlArray[1]);
//            Log.d("meanText",meanText);
//            Log.d("exampleText",exampleText);
            wordmessage[0]=queryText;

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "解析过程中出错！！！");
        }
//            Log.d("queryText",wordmessage[0]);
//            Log.d("voiceEnText",wordmessage[1]);
//            Log.d("voiceEnUrlText",wordmessage[2]);
//            Log.d("voiceAmText",wordmessage[3]);
//            Log.d("voiceAmUrlText",wordmessage[4]);
//            Log.d("meanText",wordmessage[5]);
//            Log.d("exampleText",wordmessage[6]);
        return wordmessage;
    }

//中文查找英文
    //返回的是json格式，通过解析得到相关的单词
public  static ArrayList<String> findenIncloud(final String string){

    OkHttpClient client=new OkHttpClient();
    Request request=new Request.Builder().url("https://dict-co.iciba.com/api/dictionary.php?w="+
            string+"&type=json&key=486316F23DB09D4D536C71EC695111BE").build();
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
    } catch (IOException e) {
        e.printStackTrace();
    }
    //data为json文件内容
    Gson gson = new Gson();
    wordjson wordjson = gson.fromJson(data,wordjson.class);
    try {
        arrayList=wordjson.getword_mean();
    }catch (Exception e){
        Log.d("Json","无此单词");
        arrayList=new ArrayList<String>();
    }

    for(int i=0;i<arrayList.size();i++){
        Log.d("wordjson",arrayList.get(i));
    }
    return arrayList;
}

public static void downloadFile(final String string, final String wordname){
        //下载路径，如果路径无效了，可换成你的下载路径
        final String url = string;
        final long startTime = System.currentTimeMillis();
        Log.i("DOWNLOAD","startTime="+startTime);

        Request request = new Request.Builder().url(url).build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                e.printStackTrace();
                Log.i("DOWNLOAD","download failed");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Sink sink = null;
                BufferedSink bufferedSink = null;
                try {
//                    String mSDCardPath= Environment.getExternalStorageDirectory().getAbsolutePath();
                    File dest = new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/StudyWord/WordRecordData/"+wordname+".mp3");
                    sink = Okio.sink(dest);
                    bufferedSink = Okio.buffer(sink);
                    bufferedSink.writeAll(response.body().source());

                    bufferedSink.close();
                    Log.i("DOWNLOAD","download success");
                    Log.i("DOWNLOAD","totalTime="+ (System.currentTimeMillis() - startTime));
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("DOWNLOAD","download failed");
                } finally {
                    if(bufferedSink != null){
                        bufferedSink.close();
                    }

                }
            }
        });
    }

}
