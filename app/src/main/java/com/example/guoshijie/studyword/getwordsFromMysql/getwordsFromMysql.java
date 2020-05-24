package com.example.guoshijie.studyword.getwordsFromMysql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.guoshijie.studyword.WordsDatabaseHelper.WordsDatabaseHelper;
import com.example.guoshijie.studyword.Word.word;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class getwordsFromMysql {
    private ArrayList<word> arrayList=new ArrayList();
    private ArrayList<word> appointedwords=new ArrayList();
    private WordsDatabaseHelper wordsDatabaseHelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private word word;
    public getwordsFromMysql(Context context){
        wordsDatabaseHelper=new WordsDatabaseHelper(context,"Words.db",null,2);
        db=wordsDatabaseHelper.getWritableDatabase();

    }
    public ArrayList<word> getwords(String string){
        arrayList.clear();
        if(string.isEmpty())
            return arrayList;
        cursor=db.query("word_table",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                String partchapter=cursor.getString(cursor.getColumnIndex("partchapter"));
                String word=cursor.getString(cursor.getColumnIndex("word"));
                String translation=cursor.getString(cursor.getColumnIndex("translation"));
                if(isChinese(string)){
                    if(translation.contains(string)){
                        arrayList.add(new word(partchapter,word,translation));
                    }
                }
                else{
                    if(word.startsWith(string)){
                        arrayList.add(new word(partchapter,word,translation));
                    }
                }

            }while (cursor.moveToNext());
        }
        return arrayList;
    }
    public ArrayList<word> getappointedwords(String PartChapter){
        cursor=db.query("word_table",null,null,null,null,null,null);
        appointedwords.clear();
        if(cursor.moveToFirst()){
            do{
                String partchapter=cursor.getString(cursor.getColumnIndex("partchapter"));
                String word=cursor.getString(cursor.getColumnIndex("word"));
                String translation=cursor.getString(cursor.getColumnIndex("translation"));
                if(partchapter.equals(""+PartChapter)){
                    appointedwords.add(new word(partchapter,word,translation));
                }
            }while (cursor.moveToNext());
        }
        return appointedwords;
    }
    public ArrayList<word> getpartwords(int part){
        cursor=db.query("word_table",null,null,null,null,null,null);
        appointedwords.clear();
        if(cursor.moveToFirst()){
            do{
                String partchapter=cursor.getString(cursor.getColumnIndex("partchapter"));
                String word=cursor.getString(cursor.getColumnIndex("word"));
                String translation=cursor.getString(cursor.getColumnIndex("translation"));
                String[] s=partchapter.split(":");
                if(s[0].equals(""+part)){
                    appointedwords.add(new word(partchapter,word,translation));
                }
            }while (cursor.moveToNext());
        }
        return appointedwords;
    }
    public ArrayList<word> getchapterwords(int part,int chapter){
        cursor=db.query("word_table",null,null,null,null,null,null);
        appointedwords.clear();
        if(cursor.moveToFirst()){
            do{
                String partchapter=cursor.getString(cursor.getColumnIndex("partchapter"));
                String word=cursor.getString(cursor.getColumnIndex("word"));
                String translation=cursor.getString(cursor.getColumnIndex("translation"));
                if(partchapter.equals(part+":"+chapter)){
                    appointedwords.add(new word(partchapter,word,translation));
                }
            }while (cursor.moveToNext());
        }
        return appointedwords;
    }
    public word getword(String string) {
        cursor = db.query("word_table", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String partchapter=cursor.getString(cursor.getColumnIndex("partchapter"));
                String word1=cursor.getString(cursor.getColumnIndex("word"));
                String translation=cursor.getString(cursor.getColumnIndex("translation"));
                if(word1.compareTo(string)==0){
                    word=new word(partchapter,word1,translation);
                    break;
                }
            } while (cursor.moveToNext());
        }
        return word;
    }
    public void changeword(String wordletter,word word) {
        ContentValues values=new ContentValues();
        values.put("partchapter",word.getPartchapter());
        values.put("word",word.getWord());
        values.put("translation",word.getTranslation());
        db.update("word_table",values,"word=?",new String[]{wordletter});
    }
    public void deleteword(word word){
        db.delete("word_table","word=?",new String[]{word.getWord()});
        db.delete("wordstudy_table","word=?",new String[]{word.getWord()});

    }
    public void deletepartword(int part){
        cursor = db.query("word_table", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String partchapter1=cursor.getString(cursor.getColumnIndex("partchapter"));
                String word1=cursor.getString(cursor.getColumnIndex("word"));
                String translation=cursor.getString(cursor.getColumnIndex("translation"));
                String[] S=partchapter1.split(":");
                if(S[0].compareTo(part+"")==0){
                    db.delete("word_table","word=?",new String[]{word1});
                    db.delete("wordrecord_table","word=?",new String[]{word1});
                    db.delete("wordstudy_table","word=?",new String[]{word1});
                }
            } while (cursor.moveToNext());
        }

    }
    public void deletechapterword(String partchapter){
        cursor = db.query("word_table", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String partchapter1=cursor.getString(cursor.getColumnIndex("partchapter"));
                String word1=cursor.getString(cursor.getColumnIndex("word"));
                String translation=cursor.getString(cursor.getColumnIndex("translation"));
                if(partchapter.compareTo(partchapter1)==0){
                    db.delete("word_table","word=?",new String[]{word1});
                    db.delete("wordrecord_table","word=?",new String[]{word1});
                    db.delete("wordstudy_table","word=?",new String[]{word1});
                }
            } while (cursor.moveToNext());
        }
    }
    public boolean saveword(word word){
        cursor=db.query("word_table",null,"word = ?", new String[]{word.getWord()},null,null,null);
        if(cursor.moveToFirst()){
            return false;
        }
        ContentValues values=new ContentValues();
        values.put("partchapter",word.getPartchapter());
        values.put("word",word.getWord());
        values.put("translation",word.getTranslation());
        db.insert("word_table",null,values);
        return true;
    }


    public static boolean isChinese(String str){

        String regEx = "[\\u4e00-\\u9fa5]+";

        Pattern p = Pattern.compile(regEx);

        Matcher m = p.matcher(str);

        if(m.find())

            return true;

        else

            return false;

    }
}
