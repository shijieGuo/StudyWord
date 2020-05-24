package com.example.guoshijie.studyword.Fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guoshijie.studyword.AppointedWords.AppointedWords;
import com.example.guoshijie.studyword.Dialog.PdfUtils;
import com.example.guoshijie.studyword.R;
import com.example.guoshijie.studyword.Word.word;
import com.example.guoshijie.studyword.Word.wordrecord;
import com.example.guoshijie.studyword.getwordsFromMysql.getpartchapterFromMysql;
import com.example.guoshijie.studyword.getwordsFromMysql.getwordrecordFromMysql;
import com.example.guoshijie.studyword.getwordsFromMysql.getwordsFromMysql;
import com.example.guoshijie.studyword.myAdapter.wordAdapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class All_wordsFragment extends android.support.v4.app.Fragment{
    private View view=null;
    private ListView partlist;
    private ListView chapterlist;
    private getpartchapterFromMysql getpartchapterFromMysql;
    private ArrayList<String> parts=new ArrayList<String>();
    private ArrayList<String> parts1=new ArrayList<String>();
    private ArrayList<String> chapters=new ArrayList<String>();
    private ArrayList<String> chapters1=new ArrayList<String>();
    private ArrayAdapter<String> adapter1;
    private ArrayAdapter<String> adapter2;
    private String part;
    private LinearLayout allwordsview;
    private LinearLayout findwordview;
    private EditText editText;
    private Button cancle;
    private ListView listView;
    private wordAdapter adapter;
    private ArrayList<word> words=new ArrayList();
    private ArrayList<word> Words=new ArrayList();
    private TextView showpart;
    private getwordsFromMysql getwordsFromMysql;
    private ImageButton addpart;
    private ImageButton addchapter;
    private int currentpart=1;
    private ProgressDialog myDialog; // 保存进度框
    private static final int PDF_SAVE_START = 1;// 保存PDF文件的开始意图
    private static final int PDF_SAVE_RESULT = 2;// 保存PDF文件的结果开始意图
    private static final int PDF_SAVE_ERROR = 3;// 保存PDF文件出错
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(view==null) {
            view = inflater.inflate(R.layout.fragment_allwords, container, false);
            findView();
            setView();
            renewpartlist();
            renewchapterlist(currentpart);
        }
        return view;
    }
    private Handler handlerpdf = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case PDF_SAVE_START:
                    if (!myDialog.isShowing())
                        myDialog.show();
                    break;

                case PDF_SAVE_RESULT:
                    if (myDialog.isShowing())
                        myDialog.dismiss();
                    Toast.makeText(getContext(), "保存成功", Toast.LENGTH_SHORT)
                            .show();
                    break;
                case PDF_SAVE_ERROR:
                    if (myDialog.isShowing())
                        myDialog.dismiss();
                    Toast.makeText(getContext(),"无内容",Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });
    private void findView() {
        getpartchapterFromMysql=new getpartchapterFromMysql(getContext());
//        chapters=getpartchapterFromMysql.getChapters(1);
        partlist=view.findViewById(R.id.partlist);
        chapterlist=view.findViewById(R.id.chapterlist);
        findwordview=view.findViewById(R.id.findwordview);
        allwordsview=view.findViewById(R.id.allwordsview);
        showpart=view.findViewById(R.id.showpart);
        addpart=view.findViewById(R.id.addpart);
        addchapter=view.findViewById(R.id.addchapter);
    }

    private void setView() {
        initProgress();
        addpart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText inputServer = new EditText(getContext());
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("输入词典名").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.setPositiveButton("添加", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String text = inputServer.getText().toString();
                        int result=getpartchapterFromMysql.addpart(text);
                        if(result<=parts.size()) Toast.makeText(getContext(),"已存在",Toast.LENGTH_SHORT).show();
//                        getpartchapterFromMysql.addchapter(currentpart,"1");
                        Message message=new Message();
                        message.what=0X111;
                        message.arg1=1;
                        message.arg2=-1;
                        handler.sendMessage(message);
                    }
                    });
                builder.show();


            }
        });
        addchapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText inputServer = new EditText(getContext());
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("输入chapter名").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.setPositiveButton("添加", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String text = inputServer.getText().toString();
                        int result=getpartchapterFromMysql.addchapter(currentpart,text);
                        if(result<=chapters.size()) Toast.makeText(getContext(),"已存在",Toast.LENGTH_SHORT).show();
                        Message message=new Message();
                        message.what=0X111;
                        message.arg1=-1;
                        message.arg2=currentpart;
                        handler.sendMessage(message);
                    }
                });
                builder.show();
            }
        });
        getwordsFromMysql=new getwordsFromMysql(getContext());
        editText=view.findViewById(R.id.edittext);
        TextWatcher watch=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                allwordsview.setVisibility(View.INVISIBLE);
                findwordview.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Message message=new Message();
                message.what=0x111;
                handler1.sendMessage(message);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editText.getText().toString().isEmpty()){
                    allwordsview.setVisibility(View.VISIBLE);
                    findwordview.setVisibility(View.INVISIBLE);
                }

            }
        };
        editText.addTextChangedListener(watch);
        cancle=view.findViewById(R.id.cancle);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
            }
        });
        listView=view.findViewById(R.id.list_view);
        adapter=new wordAdapter(getActivity(),R.layout.worditem,words,1);
        listView.setAdapter(adapter);
        adapter1=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,parts);
        partlist.setAdapter(adapter1);
        adapter2=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,chapters);
        chapterlist.setAdapter(adapter2);
        partlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showpart.setText(parts.get(i));
                currentpart=i+1;
                Message message=new Message();
                message.what=0X111;
                message.arg1=-1;
                message.arg2=i+1;
                handler.sendMessage(message);
//                chapters.clear();
//                chapters1=getpartchapterFromMysql.getParts();
//                for(int j=0;j<chapters1.size();j++){
//                    chapters.add(chapters1.get(j));}
//                Toast.makeText(getActivity(),"fff",Toast.LENGTH_SHORT).show();
//                adapter2.notifyDataSetChanged();
            }
        });
        partlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
                builder.setPositiveButton("生成pdf", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        turnToPdf(i+1);
                    }
                });
                builder.setNegativeButton("重命名", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface,  int which) {
                        final EditText inputServer = new EditText(getContext());
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("请输入新命名").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.setPositiveButton("完成", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String text = inputServer.getText().toString();
                                getpartchapterFromMysql.updatapart(getpartchapterFromMysql.getParts().get(i),text);
                                renewpartlist();
                                renewchapterlist(currentpart);
                            }
                        });
                        builder.show();
                    }
                });

                builder.setNeutralButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getwordsFromMysql.deletepartword(i+1);
                        getpartchapterFromMysql.deletepart(i,getpartchapterFromMysql.getParts().get(i));
                        currentpart=1;
                        renewpartlist();
                        renewchapterlist(currentpart);
                    }
                });
                builder.show();
                return true;
            }
        });
        chapterlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(getActivity(),AppointedWords.class);
                String partchapter=currentpart+":"+(i+1)+"";
                intent.putExtra("partchapter",partchapter);
                startActivity(intent);
            }
        });
        chapterlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
                builder.setPositiveButton("生成pdf", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        turnToPdf(currentpart,i+1);
                    }
                });
                builder.setNegativeButton("重命名", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface,  int which) {
                        final EditText inputServer = new EditText(getContext());
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("请输入新命名").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.setPositiveButton("完成", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String text = inputServer.getText().toString();
                                getpartchapterFromMysql.updatachapter(currentpart+"",getpartchapterFromMysql.getChapters(currentpart).get(i),text);
                                renewpartlist();
                                renewchapterlist(currentpart);
                            }
                        });
                        builder.show();
                    }
                });
                builder.setNeutralButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getwordsFromMysql.deletechapterword(currentpart+":"+(i+1));
                        getpartchapterFromMysql.deletechapter(currentpart+"",getpartchapterFromMysql.getChapters(currentpart).get(i));
                        renewpartlist();
                        renewchapterlist(currentpart);
                    }
                });
                builder.show();
                return true;
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



    }





    public Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0X111){
                if(msg.arg1==-1){
                    renewchapterlist(msg.arg2);
                }
                if(msg.arg2==-1){
                    renewpartlist();
                }
            }
            else refresh();

            super.handleMessage(msg);
        }
    };
    private void renewpartlist(){

        parts.clear();
        parts1.clear();
        parts1=getpartchapterFromMysql.getParts();
        for(int i=0;i<parts1.size();i++){
            parts.add(parts1.get(i));}
//                Toast.makeText(getActivity(),"fff",Toast.LENGTH_SHORT).show();
        adapter1.notifyDataSetChanged();
        if(parts.size()>0){
            showpart.setText(parts.get(0));
        }
        else showpart.setText("请编辑单词库");
    }
    private void renewchapterlist(int part){
                chapters.clear();
                chapters1.clear();
                chapters1=getpartchapterFromMysql.getChapters(part);
                for(int i=0;i<chapters1.size();i++){
                    chapters.add(chapters1.get(i));}
//                Toast.makeText(getActivity(),"fff",Toast.LENGTH_SHORT).show();
                adapter2.notifyDataSetChanged();
//        Toast.makeText(getActivity(),"fff",Toast.LENGTH_SHORT).show();


    }

    public void refresh(){
        renewpartlist();
        renewchapterlist(currentpart);
    }
    private Handler handler1=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==0X111){
                renewlist();
            }
            super.handleMessage(msg);
        }
    };

    private void renewlist() {
        words.clear();
        Words=getwordsFromMysql.getwords(editText.getText().toString());
        SortWords();//对检索结果进行排序
        for(int i=0;i<Words.size();i++){
            words.add(Words.get(i));}
//        Toast.makeText(getActivity(),words.get(0),Toast.LENGTH_SHORT).show();
        adapter.notifyDataSetChanged();
    }

    private void SortWords() {
        Collections.sort(Words, new Comparator<word>(){
            public int compare(word p1, word p2) {
                if(p1.getWord().compareTo(p2.getWord())>0){
                    return 1;
                }
                if(p1.getWord().compareTo(p2.getWord())==0){
                    return 0;
                }
                return -1;
            }
        });
    }

//    private void startTimer()
//    {
//        timerTask=new TimerTask() {
//            @Override
//            public void run() {
//                if(!editText.getText().toString().equals(input)){
//                    Message message=new Message();
//                    message.what=0x111;
//                    handler1.sendMessage(message);
//                }
//                input=editText.getText().toString();
//            }
//        };
//        timer=new Timer();
//        timer.schedule(timerTask,0,100);
//    }
private void initProgress() {
    myDialog = new ProgressDialog(getContext(), ProgressDialog.THEME_HOLO_LIGHT);
    myDialog.setIndeterminateDrawable(getResources().getDrawable(
            R.drawable.progress_ocr));
    myDialog.setMessage("正在保存PDF文件...");
    myDialog.setCanceledOnTouchOutside(false);
    myDialog.setCancelable(false);
}
    private void turnToPdf(final int part) {
        File file = new File(PdfUtils.ADDRESS);
        if (!file.exists())
            file.mkdirs();
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        final String pdf_address = PdfUtils.ADDRESS + File.separator + getpartchapterFromMysql.getParts().get(part-1)
                 + ".pdf";
        handlerpdf.sendEmptyMessage(PDF_SAVE_START);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<word> allwords=getwordsFromMysql.getpartwords(part);
                if(allwords.size()==0){
                    handlerpdf.sendEmptyMessage(PDF_SAVE_ERROR);
                    return;
                }
                Document doc = new Document();// 创建一个document对象
                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(pdf_address); // pdf_address为Pdf文件保存到sd卡的路径
                    PdfWriter.getInstance(doc, fos);
                    doc.open();
                    doc.setPageCount(1);
                    //
                    for(int i=0;i<allwords.size();i++){
                        wordrecord wordrecord=new getwordrecordFromMysql(getContext()).getwordrecord(allwords.get(i).getWord());
                        word word=new getwordsFromMysql(getContext()).getword(allwords.get(i).getWord());
                        String PDF="单词："+allwords.get(i).getWord()+"\n"+
                                "词义："+word.getTranslation()+"\n"+
                                "例句："+wordrecord.getExampleText()+"\n"+
                                "\n\n";

                        doc.add(new Paragraph(PDF,
                                setChineseFont())); // result为保存的字符串
                    }
                    //
                    // ,setChineseFont()为pdf字体
                    // 一定要记得关闭document对象
                    doc.close();
                    fos.flush();
                    fos.close();
                    handlerpdf.sendEmptyMessage(PDF_SAVE_RESULT);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
    private void turnToPdf(final int part, final int chapter) {
        File file = new File(PdfUtils.ADDRESS);
        if (!file.exists())
            file.mkdirs();
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        final String pdf_address = PdfUtils.ADDRESS + File.separator + getpartchapterFromMysql.getParts().get(part-1)+"-"+
                getpartchapterFromMysql.getChapters(part).get(chapter-1)
                +  ".pdf";
        handlerpdf.sendEmptyMessage(PDF_SAVE_START);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<word> allwords=getwordsFromMysql.getchapterwords(part,chapter);
                if(allwords.size()==0){
                    handlerpdf.sendEmptyMessage(PDF_SAVE_ERROR);
                    return;
                }
                Document doc = new Document();// 创建一个document对象
                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(pdf_address); // pdf_address为Pdf文件保存到sd卡的路径
                    PdfWriter.getInstance(doc, fos);
                    doc.open();
                    doc.setPageCount(1);
                    //
                    for(int i=0;i<allwords.size();i++){
                        wordrecord wordrecord=new getwordrecordFromMysql(getContext()).getwordrecord(allwords.get(i).getWord());
                        word word=new getwordsFromMysql(getContext()).getword(allwords.get(i).getWord());
                        String PDF="单词："+allwords.get(i).getWord()+"\n"+
                                "词义："+word.getTranslation()+"\n"+
                                "例句："+wordrecord.getExampleText()+"\n"+
                                "\n\n";

                        doc.add(new Paragraph(PDF,
                                setChineseFont())); // result为保存的字符串
                    }
                    //
                    // ,setChineseFont()为pdf字体
                    // 一定要记得关闭document对象
                    doc.close();
                    fos.flush();
                    fos.close();
                    handlerpdf.sendEmptyMessage(PDF_SAVE_RESULT);
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
    /**
     * 设置PDF字体(较为耗时)
     */
    public Font setChineseFont() {
        BaseFont bf = null;
        Font fontChinese = null;
        try {
            // STSong-Light : Adobe的字体
            // UniGB-UCS2-H : pdf 字体
            bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",
                    BaseFont.NOT_EMBEDDED);
            fontChinese = new Font(bf, 12, Font.NORMAL);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fontChinese;
    }
}
