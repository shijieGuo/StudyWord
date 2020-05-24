package com.example.guoshijie.studyword.getwordsFromMysql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.guoshijie.studyword.Word.word;
import com.example.guoshijie.studyword.WordsDatabaseHelper.WordsDatabaseHelper;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class getnotewordsFromMysql {
    private WordsDatabaseHelper wordsDatabaseHelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    public getnotewordsFromMysql(Context context){
        wordsDatabaseHelper=new WordsDatabaseHelper(context,"Words.db",null,2);
        db=wordsDatabaseHelper.getWritableDatabase();

    }

    public ArrayList<String> getnotewords(){
        ArrayList<String> arrayList=new ArrayList();
        cursor=db.query("wordstudy_table",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                String word=cursor.getString(cursor.getColumnIndex("word"));
                arrayList.add(word);
            }while (cursor.moveToNext());
        }
        return arrayList;
    }
    public void deleteword(String string){
        db.delete("wordstudy_table","word=?",new String[]{string});
    }

    public void cleanwords() {
        db.delete("wordstudy_table",null,null);
    }
}