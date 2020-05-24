package com.example.guoshijie.studyword.getwordsFromMysql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.guoshijie.studyword.WordsDatabaseHelper.WordsDatabaseHelper;

import java.util.ArrayList;

public class getpartchapterFromMysql {
    private WordsDatabaseHelper wordsDatabaseHelper;
    private SQLiteDatabase db;
    private Cursor cursor;
    private ArrayList<String> parts=new ArrayList<String>();
    private ArrayList<String> chapters=new ArrayList<String>();
    private ArrayList<String> getPartAndChapter=new ArrayList<String>();

    public getpartchapterFromMysql(Context context) {
        wordsDatabaseHelper = new WordsDatabaseHelper(context, "Words.db", null, 2);
        db = wordsDatabaseHelper.getWritableDatabase();
    }

    public  ArrayList<String> getParts() {
        parts.clear();
        cursor = db.query("part_table", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String part = cursor.getString(cursor.getColumnIndex("part"));
                parts.add(part);
            } while (cursor.moveToNext());
        }
        return parts;
    }
    public int addpart(String part){
        int result=0;
        ContentValues values=new ContentValues();
        values.put("part",part);
        cursor=db.query("part_table",null,"part=?",new String[]{part},null, null, null, null);
        if(cursor.getCount()==0){
            db.insert("part_table",null,values);
            cursor=db.query("part_table",null,"part=?",new String[]{part},null, null, null, null);
            if (cursor.moveToFirst()) {
                    result = cursor.getInt(cursor.getColumnIndex("id"));
                    }
            return result;
        }
        else {
            if (cursor.moveToFirst()) {
                result = cursor.getInt(cursor.getColumnIndex("id"));
            }
            return result;
        }

    }
    public void deletepart(int part,String partname){
        parts.clear();
        cursor = db.query("word_table", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String partchapter = cursor.getString(cursor.getColumnIndex("partchapter"));
                String word1=cursor.getString(cursor.getColumnIndex("word"));
                String[] s=partchapter.split(":");
                if(Integer.parseInt(s[0])>part){
                    ContentValues values=new ContentValues();
                    values.put("partchapter",(Integer.parseInt(s[0])-1)+":"+s[1]);
                    db.update("word_table",values,"word=?",new String[]{word1});
                }
            } while (cursor.moveToNext());
        }
        db.delete("partchapter_table","part=?",new String[]{(part+1)+""});
        cursor = db.query("partchapter_table", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String part1 = cursor.getString(cursor.getColumnIndex("part"));
                String chapter=cursor.getString(cursor.getColumnIndex("chapter"));

                if(Integer.parseInt(part1)>(part+1)){
                    ContentValues values=new ContentValues();
                    values.put("part",(Integer.parseInt(part1)-1));
                    db.update("partchapter_table",values,"part=?",new String[]{part1});
                }
            } while (cursor.moveToNext());
        }

        db.delete("part_table","part=?",new String[]{partname});

    }
    public int addchapter(int part, String chapter){
        int result=0;
        ContentValues values=new ContentValues();
        values.put("part",part+"");
        values.put("chapter",chapter);
        cursor=db.query("partchapter_table",null,"part=? and chapter=?",new String[]{part+"",chapter},null, null, null, null);
        if(cursor.getCount()==0){
            db.insert("partchapter_table",null,values);
            cursor=db.query("partchapter_table",null,"part=?",new String[]{part+""},null, null, null, null);
            result = cursor.getCount();
            return result;
        }
        else {
            if (cursor.moveToFirst()) {
                ArrayList<String> chapters=getChapters(part);
                for(int i=0;i<chapters.size();i++){
                    if(chapters.get(i).equals(chapter)){
                        result=i+1;
                    }
                }
            }
            return result;
        }
    }
    public void deletechapter(String part, String chapter){
        db.delete("partchapter_table","part=? and chapter=?",new String[]{part,chapter});
    }
    public  ArrayList<String> getChapters(int part) {
        chapters.clear();
        cursor = db.query("partchapter_table", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String thepart = cursor.getString(cursor.getColumnIndex("part"));
                String chapter = cursor.getString(cursor.getColumnIndex("chapter"));
                if(thepart.equals(part+"")){
                    chapters.add(chapter);
                }
            } while (cursor.moveToNext());
        }
        return chapters;
    }
    public ArrayList<String> getPartAndChapter(String PartAndChapter){
        getPartAndChapter.clear();
        if(PartAndChapter.compareTo("")==0)return getPartAndChapter;
        String[] s=PartAndChapter.split(":");
        ArrayList<String> part=getParts();
        getPartAndChapter.add(part.get(Integer.parseInt(s[0])-1));
        ArrayList<String> chapter=getChapters(Integer.parseInt(s[0]));
        getPartAndChapter.add(chapter.get(Integer.parseInt(s[1])-1));
        return getPartAndChapter;
    }

    public void updatachapter(String part, String chapter,String newchapter) {
        ContentValues values=new ContentValues();
        values.put("chapter",newchapter);
        db.update("partchapter_table",values,"part=? and chapter=?",new String[]{part,chapter});
    }

    public void updatapart(String part, String newpart) {
        ContentValues values=new ContentValues();
        values.put("part",newpart);
        db.update("part_table",values,"part=?",new String[]{part});
    }
}