package com.example.guoshijie.studyword.WordsDatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.guoshijie.studyword.getwordsFromMysql.getpartchapterFromMysql;

import org.litepal.LitePal;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class WordsDatabaseHelper extends SQLiteOpenHelper {
public static final String part_table="create table part_table ("
            +"id integer primary key autoincrement,"
            +"part varchar(50) )";

public static final String partchapter_table="create table partchapter_table ("
            +"id integer primary key autoincrement,"
            +"part varchar(50),"
            +"chapter varchar(50) )";

public static final String word_table="create table word_table ("
        +"id integer primary key autoincrement,"
        +"partchapter varchar(10),"
        +"word varchar(26),"
        +"translation varchar(50) )";

public static final String wordrecord_table="create table wordrecord_table ("
        +"id integer primary key autoincrement,"
        +"word varchar(26),"
        +"voiceEnText varchar(15),"
        +"voiceEnUrlText varchar(50),"
        +"voiceAmText varchar(15),"
        +"voiceAmUrlText varchar(50),"
        +"exampleText varchar(200) )";
public static final String wordstudy_table="create table wordstudy_table ("
        +"id integer primary key autoincrement,"
        +"word varchar(26) )";

private Context context;
    public WordsDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(word_table);
        sqLiteDatabase.execSQL(wordrecord_table);
        sqLiteDatabase.execSQL(partchapter_table);
        sqLiteDatabase.execSQL(part_table);
        sqLiteDatabase.execSQL(wordstudy_table);
        LitePal.getDatabase();

        ImportData(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    private void ImportData(SQLiteDatabase sqLiteDatabase) {
        try {
            InputStreamReader in = new InputStreamReader(context.getAssets().open("part5chapter1.csv"),Charset.forName("GBK"));
            BufferedReader br = new BufferedReader(in);
            br.readLine();
            String line = "";
            /**
             * 这里读取csv文件中的前10条数据
             * 如果要读取第10条到30条数据,只需定义i初始值为9,wile中i<10改为i>=9&&i<30即可,其他范围依次类推
             */
            ContentValues values=new ContentValues();
            ContentValues values1=new ContentValues();
            int i=0;
            while ((line = br.readLine()) != null ) {
                /**
                 *  csv格式每一列内容以逗号分隔,因此要取出想要的内容,以逗号为分割符分割字符串即可,
                 *  把分割结果存到到数组中,根据数组来取得相应值
                 */
                Log.d("buffguoshijie",line);
                String buffer[] = line.split(",");// 以逗号分隔
                values.put("partchapter",1+":"+1);
                values.put("word",buffer[2]);
                values.put("translation",buffer[3]);
                values1.put("word",buffer[2]);
                values1.put("voiceEnText","");
                values1.put("voiceEnUrlText","");
                values1.put("voiceAmText","");
                values1.put("voiceAmUrlText","");
                values1.put("exampleText",buffer[4]);
//                values1.put("exampletranslation",buffer[5]);
                sqLiteDatabase.insert("word_table",null,values);
                values.clear();
                sqLiteDatabase.insert("wordrecord_table",null,values1);
                values1.clear();
                if(i==0){
                    values.put("part",buffer[0]);
                    sqLiteDatabase.insert("part_table",null,values);
                    values.clear();
                    values.put("part","1");
                    values.put("chapter",buffer[1]);
                    sqLiteDatabase.insert("partchapter_table",null,values);
                    values.clear();
                }
                i++;
            }

            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void ImportpartchapterData(SQLiteDatabase sqLiteDatabase) {
        try {
            InputStreamReader in = new InputStreamReader(context.getAssets().open("partchapter.csv"),Charset.forName("GBK"));
            BufferedReader br = new BufferedReader(in);
            br.readLine();
            String line = "";
            /**
             * 这里读取csv文件中的前10条数据
             * 如果要读取第10条到30条数据,只需定义i初始值为9,wile中i<10改为i>=9&&i<30即可,其他范围依次类推
             */
            ContentValues values=new ContentValues();
            ContentValues values1=new ContentValues();
            while ((line = br.readLine()) != null ) {
                /**
                 *  csv格式每一列内容以逗号分隔,因此要取出想要的内容,以逗号为分割符分割字符串即可,
                 *  把分割结果存到到数组中,根据数组来取得相应值
                 */
                String buffer[] = line.split(",");// 以逗号分隔
//                        Toast.makeText((Activity) context,buffer[2],Toast.LENGTH_SHORT).show();
                values.put("partchapter",1+":"+1);
                values.put("word",buffer[2]);
                values.put("translation",buffer[3]);
                values1.put("word",buffer[2]);
                values1.put("exampleText",buffer[4]);
//                        values1.put("exampletranslation",buffer[5]);
                sqLiteDatabase.insert("word_table",null,values);
                values.clear();
                sqLiteDatabase.insert("wordrecord_table",null,values1);
                values1.clear();
                values.put("part",buffer[0]);
                sqLiteDatabase.insert("part_table",null,values);
                values.clear();
                values.put("part","1");
                values.put("chapter",buffer[1]);
                sqLiteDatabase.insert("partchapter_table",null,values);
                values.clear();
            }
            br.close();
            Toast.makeText(context,"单词导入成功",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
//                Toast.makeText((Activity) context,arrayList.get(position).getAbsolutePath(),Toast.LENGTH_SHORT).show();
    }

}
