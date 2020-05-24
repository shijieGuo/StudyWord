package com.example.guoshijie.studyword.ChangeWord;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.guoshijie.studyword.AppointedWords.AppointedWords;
import com.example.guoshijie.studyword.R;
import com.example.guoshijie.studyword.Word.word;
import com.example.guoshijie.studyword.Word.wordrecord;
import com.example.guoshijie.studyword.getwordsFromMysql.getpartchapterFromMysql;
import com.example.guoshijie.studyword.getwordsFromMysql.getwordrecordFromMysql;
import com.example.guoshijie.studyword.getwordsFromMysql.getwordsFromMysql;

import java.util.ArrayList;

public class ChangeWord extends AppCompatActivity {
    private TextView Part;
    private TextView Chapter;
    private EditText Word;
    private EditText Translation;
    private EditText Example;
    private String wordletter;
    private word word;
    private wordrecord wordrecord;
    private Button submit;
    private getwordsFromMysql getwordsFromMysql;
    private getwordrecordFromMysql getwordrecordFromMysql;
    private getpartchapterFromMysql getpartchapterFromMysql;
    private ArrayList<String> PartAndChapter=new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changeword);
        Intent intent=getIntent();
        wordletter=intent.getStringExtra("wordletter");
        finddata();
        findView();
        setview();
    }

    private void finddata() {
        word=new getwordsFromMysql(this).getword(wordletter);
        wordrecord=new getwordrecordFromMysql(this).getwordrecord(wordletter);
    }

    private void findView() {
        getpartchapterFromMysql=new getpartchapterFromMysql(this);
        PartAndChapter=getpartchapterFromMysql.getPartAndChapter(word.getPartchapter());
        getwordsFromMysql=new getwordsFromMysql(this);
        getwordrecordFromMysql=new getwordrecordFromMysql(this);
        submit=(Button)findViewById(R.id.submit);
        Part=(TextView)findViewById(R.id.Part);
        Chapter=(TextView)findViewById(R.id.Chapter) ;
        Word=(EditText)findViewById(R.id.editword);
        Translation=(EditText)findViewById(R.id.edittranslation);
        Example=(EditText)findViewById(R.id.editexample);
    }
    private void setview() {
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getwordsFromMysql.changeword(word.getWord(),new word(word.getPartchapter(),Word.getText().toString(),Translation.getText().toString()));
                getwordrecordFromMysql.changewordrecord(word.getWord(),new wordrecord(Word.getText().toString(),"","","","",Example.getText().toString()));
                refreshlist();
                finish();
            }
        });
        Part.setText(PartAndChapter.get(0));
        Chapter.setText(PartAndChapter.get(1));
        Word.setText(word.getWord());
        Translation.setText(word.getTranslation());
        Example.setText(wordrecord.getExampleText());
//        Exampletranslation.setText(wordrecord.getExampletranslation());
    }
    private void refreshlist(){
        Message message=new Message();
        message.what=0x111;
        AppointedWords.gethandler().sendMessage(message);
    }
}