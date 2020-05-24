package com.example.guoshijie.studyword.getwordsFromMysql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.guoshijie.studyword.Word.word;
import com.example.guoshijie.studyword.WordsDatabaseHelper.WordsDatabaseHelper;
import com.example.guoshijie.studyword.Word.wordrecord;
import java.util.ArrayList;

public class getwordrecordFromMysql {
    private ArrayList<wordrecord> arrayList = new ArrayList();
    private WordsDatabaseHelper wordsDatabaseHelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private wordrecord wordrecord;

    public getwordrecordFromMysql(Context context) {
        wordsDatabaseHelper = new WordsDatabaseHelper(context, "Words.db", null, 2);
        db = wordsDatabaseHelper.getWritableDatabase();
    }

    public wordrecord getwordrecord(String string) {
        cursor = db.query("wordrecord_table", null, "word = ?", new String[]{string}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String word = cursor.getString(cursor.getColumnIndex("word"));
                String voiceEnText= cursor.getString(cursor.getColumnIndex("voiceEnText"));
                String voiceEnUrlText= cursor.getString(cursor.getColumnIndex("voiceEnUrlText"));
                String voiceAmText= cursor.getString(cursor.getColumnIndex("voiceAmText"));
                String voiceAmUrlText= cursor.getString(cursor.getColumnIndex("voiceAmUrlText"));
                String exampleText= cursor.getString(cursor.getColumnIndex("exampleText"));
                if(word.compareTo(string)==0){
                    wordrecord=new wordrecord(word,voiceEnText,voiceEnUrlText,voiceAmText,voiceAmUrlText,exampleText);
                    break;
                }
            } while (cursor.moveToNext());
        }
        return wordrecord;
    }
    public void changewordrecord(String wordletter,wordrecord wordrecord) {
        ContentValues values=new ContentValues();
        values.put("word",wordrecord.getWord());
        values.put("exampleText",wordrecord.getExampleText());
//        values.put("exampletranslation",wordrecord.getExampletranslation());
        db.update("wordrecord_table",values,"word=?",new String[]{wordletter});
    }
    public void deleteword(word word){
        db.delete("wordrecord_table","word=?",new String[]{word.getWord()});

    }
    public void savewordrecord(wordrecord wordrecord){
        ContentValues values=new ContentValues();
        values.put("word",wordrecord.getWord());
        values.put("voiceEnText",wordrecord.getVoiceEnText());
        values.put("voiceEnUrlText",wordrecord.getVoiceEnUrlText());
        values.put("voiceAmText",wordrecord.getVoiceAmText());
        values.put("voiceAmUrlText",wordrecord.getVoiceAmUrlText());
        values.put("exampleText",wordrecord.getExampleText());
        db.insert("wordrecord_table",null,values);
    }
}
