package com.example.guoshijie.studyword.myAdapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guoshijie.studyword.Fragment.All_wordsFragment;
import com.example.guoshijie.studyword.Fragment.Manage_wordsFragment;
import com.example.guoshijie.studyword.MainActivity;
import com.example.guoshijie.studyword.R;
import com.example.guoshijie.studyword.WordsDatabaseHelper.WordsDatabaseHelper;
import com.example.guoshijie.studyword.getwordsFromMysql.getpartchapterFromMysql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class fileAdapter extends ArrayAdapter {
    private ArrayList<File> arrayList;
    private ImageButton importcsv;
    private Activity activity;
    private WordsDatabaseHelper wordsDatabaseHelper;
    private SQLiteDatabase sqLiteDatabase;
    private int resourceID;
    private ListView.LayoutParams params;
    private getpartchapterFromMysql getpartchapterFromMysql;
    public fileAdapter(Activity activity, int textViewResourceId, ArrayList<File> objects) {
        super(activity, textViewResourceId, objects);
        getpartchapterFromMysql=new getpartchapterFromMysql(getContext());
        this.activity=activity;
        this.arrayList = objects;
        this.resourceID=textViewResourceId;
        params = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT,120);//设置宽度和高度
    }
    @Override
    public int getCount() {
        return super.getCount();
    }
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {

        View v = convertView;
        if(convertView==null){
            v=LayoutInflater.from(getContext()).inflate(resourceID,parent,false);
//            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            v = inflater.inflate(R.layout.worditem, null);
        }
        else {
            v=convertView;
//            Log.d("position",position+"");
        }
//        ListView.LayoutParams params = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT,120);//设置宽度和高度
        v.setLayoutParams(params);
        importcsv=v.findViewById(R.id.importcsv);
        v.setLongClickable(true);
        v.setClickable(true);
        importcsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    wordsDatabaseHelper=new WordsDatabaseHelper(getContext(),"Words.db",null,2);
                    sqLiteDatabase=wordsDatabaseHelper.getWritableDatabase();
//                    Toast.makeText((Activity) context,arrayList.get(position).getAbsolutePath(),Toast.LENGTH_SHORT).show();
                    InputStreamReader in = new InputStreamReader(new FileInputStream(new File(arrayList.get(position).getAbsolutePath())),Charset.forName("GBK"));
//                    Log.d("guoshijielujin",arrayList.get(position).getAbsolutePath());
                    BufferedReader br = new BufferedReader(in);
//                    Toast.makeText(getContext(),br.readLine(),Toast.LENGTH_SHORT).show();
                    String head=br.readLine();
                    if(head==null||head.isEmpty()){
                        Toast.makeText(getContext(),"文件格式错误",Toast.LENGTH_SHORT).show();
                        br.close();
                        return;
                    }
                    if(!head.contains(",")){
                        Toast.makeText(getContext(),"文件格式错误",Toast.LENGTH_SHORT).show();
                        br.close();
                        return;
                    }
                    String headbuffer[] = head.split(",");// 以逗号分隔
                    if((headbuffer.length!=5)&&!(headbuffer[0].equals("Part")&&headbuffer[1].equals("Chapter")&&headbuffer[2].equals("Word")&&headbuffer[3].equals("WordTranslation")&&headbuffer[4].equals("ExampleAndTranslation"))){
                        Toast.makeText(getContext(),"文件格式错误",Toast.LENGTH_SHORT).show();
                        br.close();
                        return;
                    }
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
                        if(buffer.length<5)break;
                        int part=getpartchapterFromMysql.addpart(buffer[0]);
                        int chapter=getpartchapterFromMysql.addchapter(part,buffer[1]);
                        values.put("partchapter",part+":"+chapter);
                        values.put("word",buffer[2]);
                        values.put("translation",buffer[3]);
                        values1.put("word",buffer[2]);
                        values1.put("voiceEnText","");
                        values1.put("voiceEnUrlText","");
                        values1.put("voiceAmText","");
                        values1.put("voiceAmUrlText","");
                        values1.put("exampleText",buffer[4]);
//                        values1.put("exampletranslation",buffer[5]);
                        sqLiteDatabase.insert("word_table",null,values);
                        values.clear();
                        sqLiteDatabase.insert("wordrecord_table",null,values1);
                        values1.clear();
                    }
                    br.close();
                    Toast.makeText(getContext(),"单词导入成功",Toast.LENGTH_SHORT).show();
                    MainActivity mainActivity=(MainActivity)activity;
                    ((All_wordsFragment) mainActivity.mFragmentList.get(1)).refresh();
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                Toast.makeText((Activity) context,arrayList.get(position).getAbsolutePath(),Toast.LENGTH_SHORT).show();
            }
        });
        TextView textView=(TextView) v.findViewById(R.id.filename);
        textView.setText(arrayList.get(position).getName());

        return v;
    }

    public ArrayList getArraryList()
    {
        return arrayList;
    }

}
