package com.example.guoshijie.studyword.example;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.guoshijie.studyword.Dialog.WordDialog;
import com.example.guoshijie.studyword.R;
import com.example.guoshijie.studyword.HttpHelper.TranslateSentence;
import com.example.guoshijie.studyword.Word.word;
import com.example.guoshijie.studyword.Word.wordrecord;
import com.example.guoshijie.studyword.getwordsFromMysql.getwordrecordFromMysql;
import com.example.guoshijie.studyword.getwordsFromMysql.getwordsFromMysql;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class example extends AppCompatActivity {
    private String result;
    private TextView title;
    private TextView middletext;
    private TextView examplesentence;
    private Socket socket = null;
    private String Code_Adress = "193.112.144.243";
    private String wordletter;
    private com.example.guoshijie.studyword.getwordsFromMysql.getwordrecordFromMysql getwordrecordFromMysql;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.example);
        title=(TextView) findViewById(R.id.title);
        middletext=(TextView) findViewById(R.id.middletext);
        examplesentence=(TextView) findViewById(R.id.examplesentence);
        getwordrecordFromMysql=new getwordrecordFromMysql(this);
        Intent intent=getIntent();
        result=intent.getStringExtra("dataSend");
        wordletter=intent.getStringExtra("word");
//        Log.d("translatedata",result);
        final String[] results=result.split("::::::",3);
//        Log.d("translatedata",results[0]);
//        Log.d("translatedata",results[1]);
//        Log.d("translatedata",results[2]);
        if(results.length<3){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    title.setText("");
                }
            });

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    middletext.setText("");
                }
            });

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    examplesentence.setText("");
                }
            });
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String s=TranslateSentence.translate(results[0]);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        title.setText(s);
                    }
                });

                final String s1=TranslateSentence.translate(results[1]);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        middletext.setText(s1);
                    }
                });

                final String s2=TranslateSentence.translate(results[2]);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        examplesentence.setText(s2);
                    }
                });
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh,menu);
        menu.getItem(0).setTitle(wordletter);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.refresh:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final byte[] bytes = new byte[10000];
                            socket = new Socket(Code_Adress,8888);
                            OutputStream outputStream = socket.getOutputStream();
                            InputStream inputStream=socket.getInputStream();
                            outputStream.write(wordletter.getBytes());
                            inputStream.read(bytes);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                    Intent intent =new  Intent(example.this,example.class);
                                    intent.putExtra("dataSend",new String (bytes).trim());
                                    intent.putExtra("word",wordletter);
                                    startActivity(intent);
//                                            Toast.makeText(getContext(),"收到服务器返回："+new String (bytes).trim(),Toast.LENGTH_SHORT).show();
                                }
                            });
                            Log.d("receve", new String (bytes).trim());
                            outputStream.close();
                            inputStream.close();
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
//                Toast.makeText(getContext(),"DownLoadWord",Toast.LENGTH_SHORT).show();
                break;
            case R.id.w:
                wordrecord wordrecord;
                wordrecord=getwordrecordFromMysql.getwordrecord(wordletter);
                word word=new getwordsFromMysql(this).getword(wordletter);
                WordDialog wordDialog=new WordDialog(this,word,wordrecord,0.6,0.75,1);
                wordDialog.setCancelable(true);
                wordDialog.show();

        }
        return true;
    }
}

