package com.example.guoshijie.studyword.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;

import com.example.guoshijie.studyword.FindFiles.FindFiles;
import com.example.guoshijie.studyword.MainActivity;
import com.example.guoshijie.studyword.R;
import com.example.guoshijie.studyword.StudyWordApplication.StudyWordApplication;
import com.example.guoshijie.studyword.myAdapter.fileAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImportWordsDialog extends AlertDialog {

    private List<String> lstFile = new ArrayList<String>();
    private File sdcardfile = null;
    private File datafile=null;
    private ArrayList<File> files=new ArrayList<File>();
    private fileAdapter adapter;
    private FindFiles Findfiles;
    private ListView filelist;
    private double dialog_width;
    private double dialog_height;
    private Activity activity;
    private View view;
    private View roadingview;
    private loading loading;
//    private wordrecord wordrecord;

    public ImportWordsDialog(Activity activity,double dialog_height, double dialog_width) {
        super(activity);
        this.dialog_height = dialog_height;
        this.dialog_width = dialog_width;
        this.activity=activity;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //为了锁定app界面的东西是来自哪个xml文件
        setContentView(R.layout.improt_words_dialog);


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
        setviews();
    }
    @Override
    public void show() {
        super.show();
        getWindow().setWindowAnimations(R.style.dialogWindowAnim1);
        // 设置显示位置为右部
        getWindow().setGravity(Gravity.CENTER);
    }
    public void findview() {
        loading =(loading)findViewById(R.id.roading);
        Findfiles=new FindFiles();
        filelist=(ListView) findViewById(R.id.filelist);

    }
    public void setviews(){
        adapter=new fileAdapter(this.activity,R.layout.fileitem,files);
        filelist.setAdapter(adapter);
        new Thread(new Runnable() {
            @Override
            public void run() {
                sdcardfile = Environment.getExternalStorageDirectory();//获取目录文件
                Findfiles.clearListFile();
                Findfiles.GetFiles(sdcardfile.getAbsolutePath(),"csv",true);
                lstFile=Findfiles.getLstFile();
                files.clear();
                if(lstFile.size()!=0) {
                    for (int i = 0; i < lstFile.size(); i++) {
                        datafile = new File(lstFile.get(i));
                        files.add(datafile);
                    }

                }
                dismissRoadingDialog();

            }
        }).start();
    }
    private void dismissRoadingDialog(){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loading.setVisibility(View.GONE);
                filelist.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            }
        });
    }
}