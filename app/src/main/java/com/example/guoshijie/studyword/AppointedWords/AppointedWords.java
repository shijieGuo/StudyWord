package com.example.guoshijie.studyword.AppointedWords;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.example.guoshijie.studyword.R;
import com.example.guoshijie.studyword.Word.word;
import com.example.guoshijie.studyword.getwordsFromMysql.getwordsFromMysql;
import com.example.guoshijie.studyword.myAdapter.wordAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AppointedWords extends AppCompatActivity {
    private ListView wordlist;
    private String partchapter;
    private getwordsFromMysql getwordsFromMysql;
    private wordAdapter adapter;

    private static Handler handler;
    private ArrayList<word> words=new ArrayList();
    private ArrayList<word> Words=new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointedwords);
        Intent intent=getIntent();
        partchapter=intent.getStringExtra("partchapter");
//        Toast.makeText(AppointedWords.this,partchapter,Toast.LENGTH_SHORT).show();
        findView();
        setView();
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==0X111){
                    new Thread(){
                        public void run()
                        {
                            words.clear();
                            Words.clear();
                            getwordsFromMysql=new getwordsFromMysql(AppointedWords.this);
                            Words=getwordsFromMysql.getappointedwords(partchapter);
                            SortWords();//对检索结果进行排序
                            for(int i=0;i<Words.size();i++){
                                words.add(Words.get(i));}
                            Message message=new Message();
                            message.what=0x110;
                            try { Thread.sleep(500);  } catch (InterruptedException e) {   e.printStackTrace();  }
                            handler.sendMessage(message);

                        }
                    }.start();
                }
                else if(msg.what==0x110)
                {
                    adapter.notifyDataSetChanged();
                }
                super.handleMessage(msg);
            }
        };
    }

    private void findView() {
        getwordsFromMysql=new getwordsFromMysql(AppointedWords.this);
        wordlist=(ListView)findViewById(R.id.wordlist);
    }


    private void setView() {
        Words=getwordsFromMysql.getappointedwords(partchapter);
        SortWords();//对检索结果进行排序
        for(int i=0;i<Words.size();i++){
            words.add(Words.get(i));}
        if(words.isEmpty())
            Toast.makeText(AppointedWords.this,"暂无单词",Toast.LENGTH_SHORT).show();
//        Toast.makeText(getActivity(),words.get(0),Toast.LENGTH_SHORT).show();
// adapter.notifyDataSetChanged();
        adapter=new wordAdapter(AppointedWords.this,R.layout.worditem,words,1);
        wordlist.setAdapter(adapter);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

    }
    private void SortWords() {
        Collections.sort(Words, new Comparator<word>(){
            public int compare(word p1, word p2) {
                if(p1.getWord().compareTo(p2.getWord())>0){
                    return 1;
                }
                if(p1.getWord().compareTo(p2.getWord())==0){
                    return 0;
                }
                return -1;
            }
        });
    }
    public static Handler gethandler(){
        return handler;
    }
}


