package com.example.guoshijie.studyword.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;

import com.example.guoshijie.studyword.R;

public class RoadingDialog extends AlertDialog {

    private double dialog_width;
    private double dialog_height;
    private Activity activity;
    private Context context;

    public RoadingDialog(Context context, double dialog_height, double dialog_width) {
        super(context);
        //  setContentView(R.layout.custom_dialog);
        this.activity = (Activity) context;
        this.dialog_height = dialog_height;
        this.dialog_width = dialog_width;
        this.context=context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //为了锁定app界面的东西是来自哪个xml文件
        setContentView(R.layout.roadingdialog);


        //设置弹窗的宽度
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        Point size = new Point();
        d.getSize(size);
        p.width = (int) (size.x * dialog_width);
        p.height = (int) (size.y * dialog_height);
        getWindow().setAttributes(p);
    }
    @Override
    public void show() {
        super.show();
        getWindow().setWindowAnimations(R.style.dialogWindowAnim1);
        // 设置显示位置为中间
        getWindow().setGravity(Gravity.CENTER);
    }
}