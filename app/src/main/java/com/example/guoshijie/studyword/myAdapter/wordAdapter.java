package com.example.guoshijie.studyword.myAdapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.guoshijie.studyword.Dialog.ChooseDialog;
import com.example.guoshijie.studyword.Dialog.WordDialog;
import com.example.guoshijie.studyword.R;
import com.example.guoshijie.studyword.Word.wordInCloud;
import com.example.guoshijie.studyword.Word.wordrecord;
import com.example.guoshijie.studyword.getwordsFromMysql.getwordrecordFromMysql;
import com.example.guoshijie.studyword.Word.word;

import org.litepal.LitePal;

import java.util.ArrayList;

public class wordAdapter extends ArrayAdapter {
    private ArrayList<word> arrayList;
    private getwordrecordFromMysql getwordrecordFromMysql;
    private Context context;
    private int iflocal;
    private int resourceID;
    private ListView.LayoutParams params;
    public wordAdapter(Context context, int textViewResourceId, ArrayList<word> objects,int iflocal) {
        super(context, textViewResourceId, objects);
        this.context=context;
        this.resourceID=textViewResourceId;
        this.arrayList = objects;
        this.iflocal=iflocal;
        this.getwordrecordFromMysql=new getwordrecordFromMysql(context);
        params = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT,120);//设置宽度和高度
    }
    @Override
    public int getCount() {
        return super.getCount();
    }
    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {

//        Log.d("position",position+"");
        View v = convertView;
        if(convertView==null){
            v=LayoutInflater.from(context).inflate(resourceID,parent,false);
//            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            v = inflater.inflate(R.layout.worditem, null);
        }
        else {
            v=convertView;
//            Log.d("position",position+"");
        }
//        ListView.LayoutParams params = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT,120);//设置宽度和高度
        v.setLayoutParams(params);
        v.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
//                Toast.makeText((Activity) context,longclick+"",Toast.LENGTH_SHORT).show();
                if(iflocal==1){
                    wordrecord wordrecord;
                    wordrecord=getwordrecordFromMysql.getwordrecord(arrayList.get(position).getWord());
                    ChooseDialog chooseDialog=new ChooseDialog(getContext(),arrayList.get(position),0.6,0.3);
                    chooseDialog.setCancelable(true);
                    chooseDialog.show();
                }
                return true;
            }
        });
        v.setClickable(true);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(iflocal==1){
                    wordrecord wordrecord;
                    wordrecord=getwordrecordFromMysql.getwordrecord(arrayList.get(position).getWord());
                    WordDialog wordDialog=new WordDialog(getContext(),arrayList.get(position),wordrecord,0.6,0.75,iflocal);
                    wordDialog.setCancelable(true);
                    wordDialog.show();
                }
                else {
                    wordrecord wordrecord;
                    wordInCloud wordInCloud =LitePal.limit(1).where("queryText = ?",arrayList.get(position).getWord()).find(wordInCloud.class).get(0);
                    wordrecord=new wordrecord(wordInCloud.getQueryText(), wordInCloud.getVoiceEnText(),
                            wordInCloud.getVoiceEnUrlText(), wordInCloud.getVoiceAmText(), wordInCloud.getVoiceAmUrlText(), wordInCloud.getExampleText());
                    WordDialog wordDialog=new WordDialog(getContext(),arrayList.get(position),wordrecord,0.6,0.75,iflocal);
                    wordDialog.setCancelable(true);
                    wordDialog.show();
                }


            }
        });
        TextView textView=(TextView) v.findViewById(R.id.textview);
        textView.setText(arrayList.get(position).getWord()+" "+arrayList.get(position).getTranslation());

        return v;
    }

    public ArrayList getArraryList()
    {
        return arrayList;
    }
}

