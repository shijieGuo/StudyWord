package com.example.guoshijie.studyword;

import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.guoshijie.studyword.Dialog.ImportWordsDialog;
import com.example.guoshijie.studyword.Dialog.PdfCenterDialog;
import com.example.guoshijie.studyword.DisplayUtils.DisplayUtils;
import com.example.guoshijie.studyword.Fragment.All_wordsFragment;
import com.example.guoshijie.studyword.Fragment.DownLoad_wordsFragment;
import com.example.guoshijie.studyword.Fragment.Manage_wordsFragment;
import com.example.guoshijie.studyword.WordsDatabaseHelper.WordsDatabaseHelper;
import com.example.guoshijie.studyword.checkpermission.CheckPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.guoshijie.studyword.StudyWordApplication.StudyWordApplication.getContext;

public class MainActivity extends AppCompatActivity {
    private WordsDatabaseHelper wordsDatabaseHelper;
    private ImportWordsDialog importWordsDialog;
    private ViewPager viewPager;
    public ArrayList<Fragment> mFragmentList = new ArrayList <Fragment>();
    private FragmentPagerAdapter fragmentPagerAdapter;
    private File sdcardfile = null;
    private File datafile=null;

    private Button[] Buttons=new Button[3];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_main);
        setpermission();
        initFragment();
        setView();
        creatdatabase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.DownLoadWord:
                PdfCenterDialog dialog = new PdfCenterDialog();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                dialog.show(ft, "");
//                Toast.makeText(getContext(),"DownLoadWord",Toast.LENGTH_SHORT).show();
                break;
            case R.id.ImportWords:
//                Toast.makeText(getContext(),"ImportWords",Toast.LENGTH_SHORT).show();
                importWordsDialog =new ImportWordsDialog(MainActivity.this,0.3,1);
                importWordsDialog.setCancelable(true);
                importWordsDialog.show();
                break;

        }
        return true;
    }

    private void setpermission()
    {
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
//            }
//        }
        if (new CheckPermissions(this,this).checklacks()){
            return;
        }

    }
    private void creatdatabase() {
        wordsDatabaseHelper=new WordsDatabaseHelper(this,"words.db",null,1);
        initFile();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        DisplayUtils.hideInputWhenTouchOtherView(this, ev,null);
        return super.dispatchTouchEvent(ev);
    }

    private void initFragment()
    {

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        mFragmentList.add(new DownLoad_wordsFragment());
        mFragmentList.add(new All_wordsFragment());
        mFragmentList.add(new Manage_wordsFragment());

        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return mFragmentList != null ? mFragmentList.get(i) : null;
            }

            @Override
            public int getCount() {
                return mFragmentList != null ? mFragmentList.size() : 0;
            }
        };
        viewPager.setAdapter(fragmentPagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                switch (i){
                    case 0:
                        Buttons[0].setTextColor(Color.BLACK);
                        Buttons[1].setTextColor(Color.WHITE);
                        Buttons[2].setTextColor(Color.WHITE);
                        break;
                    case 1:
                        Buttons[1].setTextColor(Color.BLACK);
                        Buttons[0].setTextColor(Color.WHITE);
                        Buttons[2].setTextColor(Color.WHITE);
                        break;
                    case 2:
                        Buttons[2].setTextColor(Color.BLACK);
                        Buttons[1].setTextColor(Color.WHITE);
                        Buttons[0].setTextColor(Color.WHITE);
                        ((Manage_wordsFragment) mFragmentList.get(2)).refresh();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private void setView()
    {
        Buttons[0]=findViewById(R.id.seek_imagebutton);
        Buttons[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(0,true);
            }
        });
        Buttons[1]=findViewById(R.id.allwords_imagebutton);
        Buttons[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1,true);
            }
        });
        Buttons[2]=findViewById(R.id.manage_imagebutton);
        Buttons[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(2,true);
                ((Manage_wordsFragment) mFragmentList.get(2)).refresh();
            }
        });
        Buttons[0].setTextColor(Color.BLACK);
        Buttons[1].setTextColor(Color.WHITE);
        Buttons[2].setTextColor(Color.WHITE);
    }
    private void initFile()
    {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) //内存卡存在
        {
//            sdcardfile = Environment.getExternalStorageDirectory();//获取目录文件
            File datafile=new File(Environment.getExternalStorageDirectory().getAbsoluteFile()+"/StudyWord/WordRecordData");
            createDir(datafile.getAbsolutePath());
        }
    }
    private static boolean createDir(String destDirName) {
        File dir = new File(destDirName);
        if (dir.exists()) {
            return false;
        }
        if (!destDirName.endsWith(File.separator)) {//如果没有/ 加上/
            destDirName = destDirName + File.separator;
        }
        //创建目录
        if (dir.mkdirs()) { return true;  }
        else { return false; }
    }
}