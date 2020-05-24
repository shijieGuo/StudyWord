package com.example.guoshijie.studyword.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guoshijie.studyword.HttpHelper.OkHttpHelper;
import com.example.guoshijie.studyword.R;
import com.example.guoshijie.studyword.Word.word;
import com.example.guoshijie.studyword.Word.wordrecord;
import com.example.guoshijie.studyword.getwordsFromMysql.getpartchapterFromMysql;
import com.example.guoshijie.studyword.getwordsFromMysql.getwordrecordFromMysql;
import com.example.guoshijie.studyword.getwordsFromMysql.getwordsFromMysql;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class WordDialog extends AlertDialog {
    private TextView voiceEnText;
    private TextView voiceAmText;
    private LinearLayout downloadview;
    private LinearLayout localview;
    private double dialog_width;
    private double dialog_height;
    private TextView Part;
    private TextView Chapter;
    private TextView Word;
    private TextView Translation;
    private TextView Example;
    private ImageButton PlayvoiceEn;
    private ImageButton PlayvoiceAm;
    private ImageButton downloadwordtolocal;
    private word word;
    private wordrecord wordrecord;
    private getpartchapterFromMysql getpartchapterFromMysql;
    private getwordsFromMysql getwordsFromMysql;
    private getwordrecordFromMysql getwordrecordFromMysql;
    private ArrayList<String> PartAndChapter=new ArrayList<String>();
    private int iflocal;
    private ArrayList<String> parts=new ArrayList<String>();
    private ArrayList<String> chapters=new ArrayList<String>();
    private Spinner partsSpinner;
    private Spinner chaptersSpinner;
    private LinearLayout choosepartandchapter;
    private ScrollView showword;
    private Button saveword;
    private int choosepart=1;
    private int choosechapter=1;
    public WordDialog(@NonNull Context context, word word,wordrecord wordrecord, double dialog_height, double dialog_width,int iflocal) {
        super(context);
        //  setContentView(R.layout.custom_dialog);
        this.dialog_height = dialog_height;
        this.dialog_width = dialog_width;
        this.word=word;
        this.wordrecord=wordrecord;
        this.iflocal=iflocal;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //为了锁定app界面的东西是来自哪个xml文件
        setContentView(R.layout.worddialog);



        //设置弹窗的宽度
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        Point size = new Point();
        d.getSize(size);
        p.width = (int) (size.x * dialog_width);//size.x*0.8是dialog的宽度为app界面的80%
        p.height = (int) (size.y * dialog_height);
        getWindow().setAttributes(p);
        findview();
        setview();
    }

    private void setview() {
        downloadwordtolocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showword.setVisibility(View.GONE);
                choosepartandchapter.setVisibility(View.VISIBLE);
                getwordsFromMysql=new getwordsFromMysql(getContext());
                getwordrecordFromMysql=new getwordrecordFromMysql(getContext());
                getpartchapterFromMysql=new getpartchapterFromMysql(getContext());
                parts=getpartchapterFromMysql.getParts();
                chapters=getpartchapterFromMysql.getChapters(1);
                ArrayAdapter<String> partsadapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_item, parts);
                partsadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                partsSpinner.setAdapter(partsadapter);
                final ArrayAdapter<String> chaptersadapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_item, chapters);
                chaptersadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                chaptersSpinner.setAdapter(chaptersadapter);
                partsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        choosepart=i+1;
                        chapters=getpartchapterFromMysql.getChapters(i+1);
                        chaptersadapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                chaptersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        choosechapter=i+1;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
        });
        saveword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getContext(),choosepart+""+choosechapter+"",Toast.LENGTH_SHORT).show();
                word.setPartchapter(choosepart+":"+choosechapter+"");
                if(getwordsFromMysql.saveword(word)){
                    getwordrecordFromMysql.savewordrecord(wordrecord);
                    Toast.makeText(getContext(),"保存成功",Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(getContext(),"本地已存在此单词",Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        if(iflocal==1){
            localview.setVisibility(View.VISIBLE);
            downloadview.setVisibility(View.INVISIBLE);
            Part.setText(PartAndChapter.get(0));
            Chapter.setText(PartAndChapter.get(1));
        }
        else {
            localview.setVisibility(View.INVISIBLE);
            downloadview.setVisibility(View.VISIBLE);
        }
        Word.setText(word.getWord());
        if(!wordrecord.getVoiceEnUrlText().isEmpty()){
            voiceEnText.setText(wordrecord.getVoiceEnText()+" ");
        }
        if(!wordrecord.getVoiceAmUrlText().isEmpty()){
            voiceAmText.setText(wordrecord.getVoiceAmText()+" ");
        }
        Translation.setText(word.getTranslation());
        Example.setText(wordrecord.getExampleText());
        PlayvoiceEn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(wordrecord.getVoiceEnUrlText().isEmpty())
                    return;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        File file=new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"" +
                                "/StudyWord/WordRecordData/"+"voiceEn"+word.getWord()+".mp3");
                        if(!file.exists()){
                            OkHttpHelper.downloadFile(wordrecord.getVoiceEnUrlText(),"voiceEn"+wordrecord.getWord());
                        }
                        else {
                            MediaPlayer mediaPlayer=new MediaPlayer();
                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    mediaPlayer.release();
                                }
                            });
//                Uri uri=FileProvider.getUriForFile(getContext(),"com.example.guoshijie.studyword.fileprovider",
//                        new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/StudyWord/WordRecordData/"+word.getWord()+".mp3"));
                            try {
                                mediaPlayer.setDataSource(file.getAbsolutePath());
                                mediaPlayer.prepare();
                                mediaPlayer.start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        });
        PlayvoiceAm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(wordrecord.getVoiceAmUrlText().isEmpty())
                    return;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            File file=new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"" +
                                    "/StudyWord/WordRecordData/"+"voiceAm"+word.getWord()+".mp3");
                            if(!file.exists()){
                                OkHttpHelper.downloadFile(wordrecord.getVoiceAmUrlText(),"voiceAm"+wordrecord.getWord());
                            }
                            else {
                                MediaPlayer mediaPlayer=new MediaPlayer();
                                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        mediaPlayer.release();
                                    }
                                });
//                Uri uri=FileProvider.getUriForFile(getContext(),"com.example.guoshijie.studyword.fileprovider",
//                        new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/StudyWord/WordRecordData/"+word.getWord()+".mp3"));
                                try {
                                    mediaPlayer.setDataSource(file.getAbsolutePath());
                                    mediaPlayer.prepare();
                                    mediaPlayer.start();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
            }
        });
    }

    @Override
    public void show() {
        super.show();
        getWindow().setWindowAnimations(R.style.dialogWindowAnim1);
        // 设置显示位置为右部
        getWindow().setGravity(Gravity.RIGHT);
    }
    public void findview() {
        getpartchapterFromMysql=new getpartchapterFromMysql(getContext());
        PartAndChapter=getpartchapterFromMysql.getPartAndChapter(word.getPartchapter());
        Part=(TextView)findViewById(R.id.part);
        Chapter=(TextView)findViewById(R.id.chapter);
        Word=(TextView)findViewById(R.id.word);
        Translation=(TextView)findViewById(R.id.translation);
        Example=(TextView)findViewById(R.id.example);
        localview=(LinearLayout)findViewById(R.id.localview);
        downloadview=(LinearLayout)findViewById(R.id.downloadview);
        voiceEnText=(TextView)findViewById(R.id.voiceEnText);
        voiceAmText=(TextView)findViewById(R.id.voiceAmText);
        PlayvoiceEn=(ImageButton)findViewById(R.id.playvoiceEn);
        PlayvoiceAm=(ImageButton)findViewById(R.id.playvoiceAm);
        downloadwordtolocal=(ImageButton)findViewById(R.id.downloadwordtolocal);
        choosepartandchapter=(LinearLayout)findViewById(R.id.choosepartandchapter);
        showword=(ScrollView)findViewById(R.id.showword);
        partsSpinner=(Spinner) findViewById(R.id.downloadtopart);
        chaptersSpinner=(Spinner)findViewById(R.id.downloadtochapter);
        saveword=(Button)findViewById(R.id.saveword);
    }
}
